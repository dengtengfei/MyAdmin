package com.dtf.modules.mnt.websocket;

import lombok.Data;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/7/14 22:37
 */
@Data
public class SocketMsg {
    private String msg;
    private MsgType msgType;

    public SocketMsg(String msg, MsgType msgType) {
        this.msg = msg;
        this.msgType = msgType;
    }
}
