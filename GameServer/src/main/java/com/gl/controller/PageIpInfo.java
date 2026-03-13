package com.gl.controller;

import org.come.entity.RoleTable;

import java.util.List;

public class PageIpInfo {

    private List<UserIp> list;

    private int pageNum;
    private long total;
    private int pages;
    public long getTotal() {
        return this.total;
    }
    public void setTotal(long total) {
        this.total = total;
    }
    public List<UserIp> getList() {
        return this.list;
    }
    public void setList(List<UserIp> list) {
        this.list = list;
    }
    public int getPageNum() {
        return this.pageNum;
    }
    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }
    public int getPages() {
        return this.pages;
    }
    public void setPages(int pages) {
        this.pages = pages;
    }

}
