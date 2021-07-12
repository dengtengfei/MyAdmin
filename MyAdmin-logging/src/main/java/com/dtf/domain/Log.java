package com.dtf.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/7/12 22:08
 */
@Entity
@Getter
@Setter
@Table(name = "sys_log")
@NoArgsConstructor
public class Log {
    @Id
    @Column(name = "log_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "ID", hidden = true)
    private Long id;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "方法")
    private String method;

    @ApiModelProperty(value = "参数")
    private String params;

    @ApiModelProperty(value = "日志类型")
    private String logType;

    @ApiModelProperty(value = "请求IP")
    private String requestIp;

    @ApiModelProperty(value = "地址")
    private String address;

    @ApiModelProperty(value = "浏览器")
    private String browser;

    @ApiModelProperty(value = "请求耗时")
    private Long time;

    @ApiModelProperty(value = "异常详情")
    private byte[] exceptionDetail;

    @CreationTimestamp
    @ApiModelProperty(value = "创建时间")
    private Timestamp createTime;

    public Log(String logType, Long time) {
        this.logType = logType;
        this.time = time;
    }
}
