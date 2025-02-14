package com.dtf.modules.mnt.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.dtf.exception.BadRequestException;
import com.dtf.modules.mnt.domain.App;
import com.dtf.modules.mnt.domain.Deploy;
import com.dtf.modules.mnt.domain.DeployHistory;
import com.dtf.modules.mnt.domain.ServerDeploy;
import com.dtf.modules.mnt.repository.DeployRepository;
import com.dtf.modules.mnt.service.DeployHistoryService;
import com.dtf.modules.mnt.service.DeployService;
import com.dtf.modules.mnt.service.ServerDeployService;
import com.dtf.modules.mnt.service.dto.AppDto;
import com.dtf.modules.mnt.service.dto.DeployDto;
import com.dtf.modules.mnt.service.dto.DeployQueryCriteria;
import com.dtf.modules.mnt.service.dto.ServerDeployDto;
import com.dtf.modules.mnt.service.mapstruct.DeployMapper;
import com.dtf.modules.mnt.utils.ExecuteShellUtil;
import com.dtf.modules.mnt.utils.ScpClientUtil;
import com.dtf.modules.mnt.websocket.MsgType;
import com.dtf.modules.mnt.websocket.SocketMsg;
import com.dtf.modules.mnt.websocket.WebSocketServer;
import com.dtf.utils.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/7/14 20:53
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeployServiceImpl implements DeployService {
    private final DeployRepository deployRepository;
    private final DeployMapper deployMapper;
    private final ServerDeployService serverDeployService;
    private final DeployHistoryService deployHistoryService;

    private final String FILE_SEPARATOR = "/";
    private final int loopCount = 30;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(Deploy deploy) {
        deployRepository.save(deploy);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Set<Long> ids) {
        deployRepository.deleteAllByIdIn(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Deploy deploy) {
        Deploy oldDeploy = deployRepository.findById(deploy.getId()).orElseGet(Deploy::new);
        ValidationUtil.isNull(oldDeploy.getId(), "Deploy", "id", deploy.getId());
        oldDeploy.copy(deploy);
        deployRepository.save(oldDeploy);
    }

    @Override
    public Object queryAll(DeployQueryCriteria criteria, Pageable pageable) {
        Page<Deploy> page = deployRepository.findAll(((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder)), pageable);
        return PageUtil.toPage(page.map(deployMapper::toDto));
    }

    @Override
    public List<DeployDto> queryAll(DeployQueryCriteria criteria) {
        return deployMapper.toDto(deployRepository.findAll(((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder))));
    }

    @Override
    public DeployDto findById(Long id) {
        Deploy deploy = deployRepository.findById(id).orElseGet(Deploy::new);
        ValidationUtil.isNull(deploy.getId(), "Deploy", "id", id);
        return deployMapper.toDto(deploy);
    }

    @Override
    public void download(List<DeployDto> deployDtoList, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (DeployDto deployDto : deployDtoList) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("应用名称", deployDto.getApp().getName());
            map.put("服务器", deployDto.getServers());
            map.put("部署日期", deployDto.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public void deploy(String fileSavePath, Long id) {
        deployApp(fileSavePath, id);
    }

    @Override
    public String startServer(Deploy deploy) {
        Set<ServerDeploy> deploySet = deploy.getDeploys();
        App app = deploy.getApp();
        for (ServerDeploy serverDeploy : deploySet) {
            StringBuilder stringBuilder = new StringBuilder();
            ExecuteShellUtil executeShellUtil = getExecuteShellUtil(serverDeploy.getIp());
            // to avoid start repeated
            stopApp(app.getPort(), executeShellUtil);
            stringBuilder.append("server: ").append(serverDeploy.getName()).append("<br>app: ").append(app.getName());
            sendMsg("send start command", MsgType.INFO);
            executeShellUtil.execute(app.getStartScript());
            sleep(3);
            sendMsg("app starting, please wait or view result later.", MsgType.INFO);
            int i = 0;
            boolean result = false;
            while (i++ < loopCount) {
                result = checkIsRunningStatus(app.getPort(), executeShellUtil);
                if (result) {
                    break;
                }
                sleep(6);
            }
            sendResultMsg(result, stringBuilder);
            log.info(stringBuilder.toString());
            executeShellUtil.close();
        }
        return "exec complete.";
    }

    @Override
    public String stopServer(Deploy deploy) {
        Set<ServerDeploy> deploySet = deploy.getDeploys();
        App app = deploy.getApp();
        for (ServerDeploy serverDeploy : deploySet) {
            StringBuilder stringBuilder = new StringBuilder();
            ExecuteShellUtil executeShellUtil = getExecuteShellUtil(serverDeploy.getIp());
            stringBuilder.append("server: ").append(serverDeploy.getName()).append("<br>app: ").append(app.getName());
            sendMsg("send stop command", MsgType.INFO);
            stopApp(app.getPort(), executeShellUtil);
            sleep(1);
            boolean result = checkIsRunningStatus(app.getPort(), executeShellUtil);
            if (result) {
                stringBuilder.append("<br>stop failed.");
                sendMsg(stringBuilder.toString(), MsgType.ERROR);
            } else {
                stringBuilder.append("<br>stop success.");
                sendMsg(stringBuilder.toString(), MsgType.INFO);
            }
            log.info(stringBuilder.toString());
            executeShellUtil.close();
        }
        return "exec complete.";
    }

    @Override
    public String serverReduction(DeployHistory deployHistory) {
        Long deployId = deployHistory.getDeployId();
        Deploy deploy = deployRepository.findById(deployId).orElseGet(Deploy::new);
        ValidationUtil.isNull(deploy.getId(), "Deploy", "id", deployId);
        String deployDate = DateUtil.format(deployHistory.getDeployDate(), DatePattern.PURE_DATETIME_PATTERN);
        App app = deploy.getApp();
        if (app == null) {
            sendMsg("app is not existed: " + deployHistory.getAppName(), MsgType.ERROR);
            throw new BadRequestException("app is not existed: " + deployHistory.getAppName());
        }
        String backupPath = app.getBackupPath() + FILE_SEPARATOR;
        backupPath += deployHistory.getAppName() + FILE_SEPARATOR + deployDate;
        String deployPath = app.getDeployPath();
        String ip = deployHistory.getIp();
        ExecuteShellUtil executeShellUtil = getExecuteShellUtil(ip);
        String msg = String.format("login server: %s", ip);
        log.info(msg);
        sendMsg(msg, MsgType.INFO);
        sendMsg("stop previous app", MsgType.INFO);

        stopApp(app.getPort(), executeShellUtil);
        sendMsg("delete app", MsgType.INFO);
        executeShellUtil.execute("rm -rf " + deployPath + FILE_SEPARATOR + deployHistory.getAppName());

        sendMsg("backup app", MsgType.INFO);
        executeShellUtil.execute("cp -r " + backupPath + "/. " + deployPath);

        sendMsg("start app", MsgType.INFO);
        executeShellUtil.execute(app.getStartScript());
        sendMsg("app starting, please wait or view result later.", MsgType.INFO);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("server: ").append(ip).append("<br>app: ").append(deployHistory.getAppName());
        sendResultMsg(checkStarted(app.getPort(), executeShellUtil), stringBuilder);
        executeShellUtil.close();
        return "";
    }

    @Override
    public String serverStatus(Deploy deploy) {
        Set<ServerDeploy> deploySet = deploy.getDeploys();
        App app = deploy.getApp();
        for (ServerDeploy serverDeploy : deploySet) {
            StringBuilder stringBuilder = new StringBuilder();
            ExecuteShellUtil executeShellUtil = getExecuteShellUtil(serverDeploy.getIp());
            stringBuilder.append("server: ").append(serverDeploy.getName()).append("<br>app: ").append(app.getName());
            boolean result = checkIsRunningStatus(app.getPort(), executeShellUtil);
            if (result) {
                stringBuilder.append("<br>app is in running.");
                sendMsg(stringBuilder.toString(), MsgType.INFO);
            } else {
                stringBuilder.append("<br>app is not in running.");
                sendMsg(stringBuilder.toString(), MsgType.ERROR);
            }
            log.info(stringBuilder.toString());
            executeShellUtil.close();
        }
        return "exec complete.";
    }

    private void deployApp(String fileSavePath, Long id) {
        //TODO judge the port available ?
        DeployDto deployDto = findById(id);
        if (deployDto == null) {
            sendMsg("部署信息不存在", MsgType.ERROR);
            throw new BadRequestException("部署信息不存在");
        }
        AppDto appDto = deployDto.getApp();
        if (appDto == null) {
            sendMsg("包对应应用信息不存在", MsgType.ERROR);
            throw new BadRequestException("包对应应用信息不存在");
        }

        StringBuilder stringBuilder = new StringBuilder();
        String msg;
        Set<ServerDeployDto> deployDtoSet = deployDto.getDeploys();
        for (ServerDeployDto serverDeployDto : deployDtoSet) {
            String ip = serverDeployDto.getIp();
            ExecuteShellUtil executeShellUtil = getExecuteShellUtil(ip);

            boolean flag = checkFile(executeShellUtil, appDto);
            executeShellUtil.execute("mkdir -p " + appDto.getUploadPath());
            executeShellUtil.execute("mkdir -p " + appDto.getBackupPath());
            executeShellUtil.execute("mkdir -p " + appDto.getDeployPath());

            msg = String.format("login server: %s", ip);
            ScpClientUtil scpClientUtil = getScpClientUtil(ip);
            log.info(msg);
            sendMsg(msg, MsgType.INFO);
            scpClientUtil.putFile(fileSavePath, appDto.getUploadPath());
            if (flag) {
                sendMsg("stop last deploy", MsgType.INFO);
                stopApp(appDto.getPort(), executeShellUtil);
                sendMsg("back before app", MsgType.INFO);
                backupApp(executeShellUtil, ip, appDto.getDeployPath() + FILE_SEPARATOR, appDto.getName(), appDto.getBackupPath() + FILE_SEPARATOR, id);
            }
            sendMsg("deploy app", MsgType.INFO);

            executeShellUtil.execute(appDto.getDeployScript());
            sleep(3);
            sendMsg("app deploying: please wait or view result later.", MsgType.INFO);
            boolean result = checkStarted(appDto.getPort(), executeShellUtil);
            stringBuilder.append("server: ").append(serverDeployDto.getName()).append("<br>app: ").append(appDto.getName());
            sendResultMsg(result, stringBuilder);
            executeShellUtil.close();
        }
    }

    private void stopApp(int port, ExecuteShellUtil executeShellUtil) {
        executeShellUtil.execute(String.format("lsof -i :%d|grep -v \"PID\"|awk '{print \"kill -9\",$2}'|sh", port));
    }

    private void backupApp(ExecuteShellUtil executeShellUtil, String ip, String fileSavePath, String appName, String backupPath, Long id) {
        String deployDate = DateUtil.format(new Date(), DatePattern.PURE_DATETIME_PATTERN);
        StringBuilder stringBuilder = new StringBuilder();
        backupPath += appName + FILE_SEPARATOR + deployDate + "\n";
        stringBuilder.append("mkdir -p ").append(backupPath);
        stringBuilder.append("mv -f ").append(fileSavePath);
        stringBuilder.append(appName).append(" ").append(backupPath);
        log.info("备份应用脚本" + stringBuilder);
        executeShellUtil.execute(stringBuilder.toString());
        DeployHistory deployHistory = new DeployHistory();
        deployHistory.setAppName(appName);
        deployHistory.setDeployUser(SecurityUtils.getCurrentUsername());
        deployHistory.setIp(ip);
        deployHistory.setDeployId(id);
        deployHistoryService.create(deployHistory);
    }

    private boolean checkStarted(int port, ExecuteShellUtil executeShellUtil) {
        int i = 0;
        boolean result = false;
        while (i++ < loopCount) {
            result = checkIsRunningStatus(port, executeShellUtil);
            if (result) {
                break;
            }
            sleep(6);
        }
        return result;
    }

    private boolean checkIsRunningStatus(int port, ExecuteShellUtil executeShellUtil) {
        String result = executeShellUtil.executeForResult(String.format("fuser -n tcp %d", port));
        return result.indexOf("/tcp:") > 0;
    }

    private void sendMsg(String msg, MsgType msgType) {
        try {
            WebSocketServer.sendInfo(new SocketMsg(msg, msgType), "deploy");
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void sendResultMsg(boolean result, StringBuilder stringBuilder) {
        if (result) {
            stringBuilder.append("<br>start success!");
            sendMsg(stringBuilder.toString(), MsgType.INFO);
        } else {
            stringBuilder.append("<br>start failed!");
            sendMsg(stringBuilder.toString(), MsgType.ERROR);
        }
    }

    private ExecuteShellUtil getExecuteShellUtil(String ip) {
        ServerDeployDto serverDeployDto = serverDeployService.findByIp(ip);
        if (serverDeployDto == null) {
            sendMsg("IP对应服务器信息不存在, " + ip, MsgType.ERROR);
            throw new BadRequestException("IP对应服务器信息不存在, " + ip);
        }
        return new ExecuteShellUtil(ip, serverDeployDto.getAccount(), serverDeployDto.getPassword(), serverDeployDto.getPort());
    }

    private ScpClientUtil getScpClientUtil(String ip) {
        ServerDeployDto serverDeployDto = serverDeployService.findByIp(ip);
        if (serverDeployDto == null) {
            sendMsg("IP对应服务器信息不存在, " + ip, MsgType.ERROR);
            throw new BadRequestException("IP对应服务器信息不存在, " + ip);
        }
        return ScpClientUtil.getInstance(ip, serverDeployDto.getPort(), serverDeployDto.getAccount(), serverDeployDto.getPassword());
    }

    private boolean checkFile(ExecuteShellUtil executeShellUtil, AppDto appDto) {
        String result = executeShellUtil.executeForResult("find " + appDto.getDeployPath() + " -name " + appDto.getName());
        return result.indexOf(appDto.getName()) > 0;
    }

    private void sleep(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }
}
