package org.come.entity;


import java.math.BigDecimal;

public class Fly implements Cloneable {
    private int flyId;
    private int stairs;
    private String name;
    private String skin;
    private String skill;
    private String text;
    private Double flySpeed;
    private Integer flyLevel;
    private Integer currFlyLevel;
    private Integer flyType;
    private Integer ldz;
    private Integer currLdz;
    private Integer consumeFuel;
    private int exp;
    private int fuel;
    private String skillTJ;
    private BigDecimal fid;

    private BigDecimal roleid;

    public BigDecimal getFid() {
        return this.fid;
    }

    public void setFid(BigDecimal fid) {
        this.fid = fid;
    }


    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSkin() {
        return this.skin;
    }

    public void setSkin(String skin) {
        this.skin = skin;
    }

    public int getStairs() {
        return this.stairs;
    }

    public void setStairs(int stairs) {
        this.stairs = stairs;
    }

    public int getExp() {
        return this.exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int getFuel() {
        return this.fuel;
    }

    public void setFuel(int fuel) {
        this.fuel = fuel;
    }

    public String getSkill() {
        return this.skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public String getSkillTJ() {
        return this.skillTJ;
    }

    public void setSkillTJ(String skillTJ) {
        this.skillTJ = skillTJ;
    }

    public static int getFlyExp(int lvl) {
        return lvl * 100;
    }

    public static int getFlyTotalExp(int lvl) {
        return (lvl + 1) * lvl * 50;
    }

    public static int getFlyLvl(int value) {
        int lvl = 0;

        do {
            ++lvl;
            value -= getFlyExp(lvl);
        } while(value >= 0);

        return lvl;
    }

    public Integer getFlyId() {
        return flyId;
    }

    public void setFlyId(int flyId) {
        this.flyId = flyId;
    }

    public BigDecimal getRoleid() {
        return roleid;
    }

    public void setRoleid(BigDecimal roleid) {
        this.roleid = roleid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Double getFlySpeed() {
        return flySpeed;
    }

    public void setFlySpeed(Double flySpeed) {
        this.flySpeed = flySpeed;
    }

    public Integer getFlyLevel() {
        return flyLevel;
    }

    public void setFlyLevel(Integer flyLevel) {
        this.flyLevel = flyLevel;
    }

    public Integer getConsumeFuel() {
        return consumeFuel;
    }

    public void setConsumeFuel(Integer consumeFuel) {
        this.consumeFuel = consumeFuel;
    }

    public Integer getCurrFlyLevel() {
        return currFlyLevel;
    }

    public void setCurrFlyLevel(Integer currFlyLevel) {
        this.currFlyLevel = currFlyLevel;
    }

    public Integer getLdz() {
        return ldz;
    }

    public void setLdz(Integer ldz) {
        this.ldz = ldz;
    }

    public Integer getCurrLdz() {
        return currLdz;
    }

    public void setCurrLdz(Integer currLdz) {
        this.currLdz = currLdz;
    }

    public Integer getFlyType() {
        return flyType;
    }

    public void setFlyType(Integer flyType) {
        this.flyType = flyType;
    }

}
