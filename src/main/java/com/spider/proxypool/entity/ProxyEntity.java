package com.spider.proxypool.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by 13 on 2017/10/11.
 */
public class ProxyEntity implements Serializable {
    private String ip;//ip
    private int port;//端口
    private String location;//代理位置
    private String agentType;//代理类型
    private Date lastValidateTime;//验证时间
    private boolean usable;//是否可用

    public ProxyEntity() {
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAgentType() {
        return agentType;
    }

    public void setAgentType(String agentType) {
        this.agentType = agentType;
    }

    public Date getLastValidateTime() {
        return lastValidateTime;
    }

    public void setLastValidateTime(Date lastValidateTime) {
        this.lastValidateTime = lastValidateTime;
    }

    public boolean isUsable() {
        return usable;
    }

    public void setUsable(boolean usable) {
        this.usable = usable;
    }


    @Override
    public String toString() {
        return "Proxy{" + "ip='" + ip + '\'' + ", port=" + port + ", location='" + location + '\'' +
                ", agentType='" + agentType + '\'' + ", lastValidateTime='" + lastValidateTime + '\'' + '}';
    }
}
