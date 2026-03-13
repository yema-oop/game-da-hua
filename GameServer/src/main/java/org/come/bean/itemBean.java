package org.come.bean;

import java.math.BigDecimal;

/**
 * 神兽召唤
 * @author Administrator
 *
 */
public class itemBean {

    //进行的操作类型
    private int opertype;//0:获得野生召唤兽 1:金柳露
    //新赠的召唤兽id或者修改
    private BigDecimal zhuangbeiid;
    private BigDecimal goodid;//消耗的物品id
    public int getOpertype() {
        return this.opertype;
    }
    public void setOpertype(int opertype) {
        this.opertype = opertype;
    }
    public BigDecimal getzhuangbeiid() {
        return this.zhuangbeiid;
    }
    public void setzhuangbeiid(BigDecimal zhuangbeiid) {
        this.zhuangbeiid = zhuangbeiid;
    }
    public BigDecimal getGoodid() {
        return this.goodid;
    }
    public void setGoodid(BigDecimal goodid) {
        this.goodid = goodid;
    }


}
