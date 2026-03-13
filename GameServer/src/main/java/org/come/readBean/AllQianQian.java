package org.come.readBean;

import java.math.BigDecimal;
import java.util.Map;

import org.come.model.QianQian;
import org.come.model.Talent;

public class AllQianQian
{
    // 所有npc的信息
    private Map<BigDecimal, QianQian> qianQianConcurrentHashMap;

    public Map<BigDecimal, QianQian> getAllTalent() {
        return qianQianConcurrentHashMap;
    }

    public void setAllTalent(Map<BigDecimal, QianQian> allTalent) {
        this.qianQianConcurrentHashMap = allTalent;
    }
}
