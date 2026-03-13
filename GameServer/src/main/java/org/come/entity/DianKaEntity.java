package org.come.entity;

import java.math.BigDecimal;

public class DianKaEntity {
    private BigDecimal roleid;

    // 点卡售卖时间
    private long outTime;
    // 点卡售卖点数
    private Integer dianKaNum;

    // 点卡售卖价格
    private long money;
    private long committ;

    private String name;

    public DianKaEntity() {
    }

    public BigDecimal getRoleid() {
        return roleid;
    }

    public void setRoleid(BigDecimal roleid) {
        this.roleid = roleid;
    }

    public long getOutTime() {
        return outTime;
    }

    public void setOutTime(long outTime) {
        this.outTime = outTime;
    }

    public Integer getDianKaNum() {
        return dianKaNum;
    }

    public void setDianKaNum(Integer dianKaNum) {
        this.dianKaNum = dianKaNum;
    }

    public long getMoney() {
        return money;
    }

    public void setMoney(long money) {
        this.money = money;
    }

    public long getCommitt() {
        return committ;
    }

    public void setCommitt(long committ) {
        this.committ = committ;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
