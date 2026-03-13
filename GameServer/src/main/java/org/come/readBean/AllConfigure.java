package org.come.readBean;

import java.math.BigDecimal;
import java.util.Map;

import org.come.model.Configure;

public class AllConfigure
{
    // 所有的信息
    private Map<BigDecimal, Configure> configureConcurrentHashMap;

    public Map<BigDecimal, Configure> getAllConfigure() {
        return configureConcurrentHashMap;
    }

    public void setAllConfigure(Map<BigDecimal, Configure> configureConcurrentHashMap) {
        this.configureConcurrentHashMap = configureConcurrentHashMap;
    }
}
