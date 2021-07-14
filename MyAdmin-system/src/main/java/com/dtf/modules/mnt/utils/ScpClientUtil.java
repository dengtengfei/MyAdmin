package com.dtf.modules.mnt.utils;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import com.dtf.utils.StringUtils;
import com.google.common.collect.Maps;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/7/14 23:02
 */
public class ScpClientUtil {
    static private Map<String, ScpClientUtil> instance = Maps.newHashMap();
    private final String ip, username, password;
    private final int port;

    static synchronized public ScpClientUtil getInstance(String ip, int port, String username, String password) {
        if (instance.get(ip) == null) {
            instance.put(ip, new ScpClientUtil(ip, port, username, password));
        }
        return instance.get(ip);
    }

    public ScpClientUtil(String ip, int port, String username, String password) {
        this.ip = ip;
        this.username = username;
        this.password = password;
        this.port = port;
    }

    public void putFile(String localFile, String remoteTargetDirectory) {
        putFile(localFile, null, remoteTargetDirectory);
    }

    public void putFile(String localFile, String remoteFileName, String remoteTargetDirectory) {
        putFile(localFile, remoteFileName, remoteTargetDirectory, null);
    }

    public void putFile(String localFile, String remoteFileName, String remoteTargetDirectory, String mode) {
        Connection connection = new Connection(ip, port);
        try {
            connection.connect();
            boolean isAuthenticated = connection.authenticateWithPassword(username, password);
            if (!isAuthenticated) {
                System.out.println("authentication failed");
            }
            SCPClient scpClient = new SCPClient(connection);
            if (StringUtils.isBlank(mode)) {
                mode = "0600";
            }
            if (StringUtils.isBlank(remoteFileName)) {
                scpClient.put(localFile, remoteTargetDirectory);
            } else {
                scpClient.put(localFile, remoteFileName, remoteTargetDirectory, mode);
            }
        } catch (IOException e) {
            Logger.getLogger(ScpClientUtil.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            connection.close();
        }
    }
}
