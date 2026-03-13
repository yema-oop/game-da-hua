

package org.come.bean;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AutoSwitchBean {
    private Integer cuurentE;
    private Map<Integer, List<BigDecimal>> allEquip = new HashMap<>();
    private Map<String, String> allEquipName = new HashMap<>();
    private Map<Integer, String> tzName = new HashMap<>();
    private Integer cuurentS;
    private Map<Integer, List<Integer>> allSX = new HashMap<>();

    public AutoSwitchBean() {
    }

    public Integer getCuurentE() {
        return this.cuurentE;
    }

    public void setCuurentE(Integer cuurentE) {
        this.cuurentE = cuurentE;
    }

    public Map<Integer, List<BigDecimal>> getAllEquip() {
        return this.allEquip;
    }

    public void setAllEquip(Map<Integer, List<BigDecimal>> allEquip) {
        this.allEquip = allEquip;
    }

    public Integer getCuurentS() {
        return this.cuurentS;
    }

    public void setCuurentS(Integer cuurentS) {
        this.cuurentS = cuurentS;
    }

    public Map<Integer, List<Integer>> getAllSX() {
        return this.allSX;
    }

    public void setAllSX(Map<Integer, List<Integer>> allSX) {
        this.allSX = allSX;
    }

    public Map<Integer, String> getTzName() {
        return this.tzName;
    }

    public void setTzName(Map<Integer, String> tzName) {
        this.tzName = tzName;
    }

    public Map<String, String> getAllEquipName() {
        return this.allEquipName;
    }

    public void setAllEquipName(Map<String, String> allEquipName) {
        this.allEquipName = allEquipName;
    }

    public String getEquipName(String name) {
        String s1 = (String)this.allEquipName.get(name);
        return s1 == null ? name : s1;
    }
}
