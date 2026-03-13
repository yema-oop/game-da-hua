package org.come.model;

import org.come.entity.Goodstable;

import java.math.BigDecimal;

public class Itemrecycling {

    private int eId;// 兑换id
    private String type;// 分类
    private String consume;// 消耗
    private BigDecimal goods;// 装备id
    private String goodsname;// 召唤兽名称
    private String skin;// 皮肤
    private Long quality;
    private BigDecimal rgid;
    private Integer status;





    private String value;

    private String instruction;

    @Override
    public String toString() {
        return "itemrecycling{" +
                "eId=" + this.eId +
                ", type='" + this.type + '\'' +
                ", consume='" + this.consume + '\'' +
                ", goods=" + this.goods +
                ", goodsname='" + this.goodsname + '\'' +
                ", skin='" + this.skin + '\'' +
                ", quality=" + this.quality +
                ", rgid=" + this.rgid +
                ", status=" + this.status +
                ", value='" + this.value + '\'' +
                ", instruction='" + this.instruction + '\'' +
                '}';
    }

    public void initPet(Goodstable goods){
        this.goodsname=goods.getGoodsname();
        this.skin=goods.getSkin();
        this.quality=goods.getQuality();
        this.value=goods.getValue();
        this.instruction=goods.getInstruction();
        this.rgid=goods.getRgid();
        this.status=goods.getStatus();


    }
    public int geteId() {
        return this.eId;
    }

    public void seteId(int eId) {
        this.eId = eId;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getConsume() {
        return this.consume;
    }

    public void setConsume(String consume) {
        this.consume = consume;
    }

    public BigDecimal getGoods() {
        return this.goods;
    }

    public void setGoods(BigDecimal goods) {
        this.goods = goods;
    }

    public String getGoodsname() {
        return this.goodsname;
    }

    public void setGoodsname(String goodsname) {
        this.goodsname = goodsname;
    }

    public String getSkin() {
        return this.skin;
    }

    public void setSkin(String skin) {
        this.skin = skin;
    }

    public Long getQuality() {
        return this.quality;
    }

    public void setQuality(Long quality) {
        this.quality = quality;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getInstruction() {
        return this.instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }


}
