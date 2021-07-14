package com.dtf.modules.mnt.utils;

import cn.hutool.core.io.IoUtil;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Vector;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/7/14 19:05
 */
@Slf4j
public class ExecuteShellUtil {
    private Vector<String> stdout;
    Session session;

    public ExecuteShellUtil(final String ipAddress, final String username, final String password, int port) {
        try {
            JSch jSch = new JSch();
            session = jSch.getSession(username, ipAddress, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect(3000);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public int execute(final String command) {
        ChannelShell channelShell = null;
        PrintWriter printWriter = null;
        BufferedReader input = null;
        stdout = new Vector<>();
        try {
            channelShell = (ChannelShell) session.openChannel("shell");
            channelShell.connect();
            input = new BufferedReader(new InputStreamReader(channelShell.getInputStream()));
            printWriter = new PrintWriter(channelShell.getOutputStream());
            printWriter.println(command);
            printWriter.println("exit");
            printWriter.flush();
            log.info("The remote command is: ");
            String line;
            while ((line = input.readLine()) != null) {
                stdout.add(line);
                System.out.println(line);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return -1;
        } finally {
            IoUtil.close(printWriter);
            IoUtil.close(input);
            if (channelShell != null) {
                channelShell.disconnect();
            }
        }
        return 0;
    }

    public void close() {
        if (session != null) {
            session.disconnect();
        }
    }
}
