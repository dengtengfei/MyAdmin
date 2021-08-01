package com.dtf.modules.mnt.websocket;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/7/14 22:22
 */
@ServerEndpoint("/webSocket/{sid}")
@Component
@Slf4j
public class WebSocketServer {
    private static final CopyOnWriteArraySet<WebSocketServer> webSocketSet = new CopyOnWriteArraySet<>();
    private Session session;
    private String sid = "";

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("sid") String sid) {
        this.session = session;
        //如果存在就先删除一个，防止重复推送消息
        for (WebSocketServer webSocket : webSocketSet) {
            if (webSocket.sid.equals(sid)) {
                webSocketSet.remove(webSocket);
            }
        }
        webSocketSet.add(this);
        this.sid = sid;
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        webSocketSet.remove(this);
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("收到来" + sid + "的信息:" + message);
        //群发消息
        for (WebSocketServer item : webSocketSet) {
            try {
                item.sendMessage(message);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误");
        error.printStackTrace();
    }

    public static void sendInfo(SocketMsg socketMsg, @PathParam("sid") String sid) throws IOException {
        String message = JSONObject.toJSONString(socketMsg);
        log.info("push msg to " + sid + ", content: " + message);
        for (WebSocketServer item : webSocketSet) {
            try {
                if (sid == null) {
                    item.sendMessage(message);
                } else if (sid.equals(item.sid)) {
                    item.sendMessage(message);
                }
            } catch (IOException ignored) {
            }
        }
    }

    private void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WebSocketServer that = (WebSocketServer) o;
        return Objects.equals(session, that.session) &&
                Objects.equals(sid, that.sid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(session, sid);
    }
}
