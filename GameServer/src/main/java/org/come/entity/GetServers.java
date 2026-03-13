package org.come.entity;

import java.math.BigDecimal;

public class GetServers {
    private String Name;
    private String ip;
    private int port;
    private int Dowport;

    private String status;

    private BigDecimal raId;


    //生成成员获取方法
    public String getName() {
        return Name;
    }
    public String getIp() {
        return ip;
    }
    public int getPort() {
        return port;
    }
    public int getDowport() {
        return Dowport;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setDowport(int dowport) {
        Dowport = dowport;
    }

    public BigDecimal getRaId() {
        return raId;
    }

    public void setRaId(BigDecimal raId) {
        this.raId = raId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
