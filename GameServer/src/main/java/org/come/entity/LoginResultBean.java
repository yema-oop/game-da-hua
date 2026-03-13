package org.come.entity;

import java.util.List;

public class LoginResultBean {
    private String username;
    private String userPwd;

    private String flag;

    private List<RegionResult> resultList;

    private String tuijianma;
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserPwd() {
        return userPwd;
    }

    public void setUserPwd(String userPwd) {
        this.userPwd = userPwd;
    }

    public List<RegionResult> getResultList() {
        return resultList;
    }

    public void setResultList(List<RegionResult> resultList) {
        this.resultList = resultList;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getTuijianma() {
        return tuijianma;
    }

    public void setTuijianma(String tuijianma) {
        this.tuijianma = tuijianma;
    }
}
