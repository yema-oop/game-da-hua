package org.come.model;

public class AutoActiveBase {

    private int id;

    private int type;        //分类
    private String aName;//活动名称
    private int tasksetId;
    private int goodNum;        //推荐次数
    private int maxNum;
    private String guide;//引导位置   地图id-x-y-npcId
    private String text;//介绍

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getaName() {
        return this.aName;
    }

    public void setaName(String aName) {
        this.aName = aName;
    }

    public String getGuide() {
        return this.guide;
    }

    public void setGuide(String guide) {
        this.guide = guide;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getTasksetId() {
        return tasksetId;
    }

    public void setTasksetId(int tasksetId) {
        this.tasksetId = tasksetId;
    }

    public int getGoodNum() {
        return goodNum;
    }

    public void setGoodNum(int goodNum) {
        this.goodNum = goodNum;
    }

    public int getMaxNum() {
        return maxNum;
    }

    public void setMaxNum(int maxNum) {
        this.maxNum = maxNum;
    }
}
