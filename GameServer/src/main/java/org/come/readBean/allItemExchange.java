package org.come.readBean;

import java.util.concurrent.ConcurrentHashMap;

import org.come.model.ItemExchange;

public class allItemExchange {
    private ConcurrentHashMap<Integer, ItemExchange> allItemExchange;

    public ConcurrentHashMap<Integer, ItemExchange> getAllItemExchange() {
        return this.allItemExchange;
    }

    public void setAllItemExchange(ConcurrentHashMap<Integer, ItemExchange> allItemExchange) {
        this.allItemExchange = allItemExchange;
    }


}
