package org.come.entity;

import java.math.BigDecimal;

public class FlySkill {
    private BigDecimal skillid;
    private String skillname;
    private BigDecimal mid;
    public BigDecimal getSkillid() {
        return skillid;
    }
    public void setSkillid(BigDecimal skillid) {
        this.skillid = skillid;
    }
    public String getSkillname() {
        return skillname;
    }
    public void setSkillname(String skillname) {
        this.skillname = skillname;
    }
    public BigDecimal getMid() {
        return mid;
    }
    public void setMid(BigDecimal mid) {
        this.mid = mid;
    }
}
