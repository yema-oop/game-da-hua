package org.come.readBean;

import org.come.model.Itemrecycling;

import java.util.concurrent.ConcurrentHashMap;

public class allItemRecycling {
    private ConcurrentHashMap<Integer, Itemrecycling> allItemRecycling;

    public ConcurrentHashMap<Integer, Itemrecycling> getAllItemExchange() {
        return this.allItemRecycling;
    }

    public void setAllItemExchange(ConcurrentHashMap<Integer, Itemrecycling> allItemRecycling) {
        this.allItemRecycling = allItemRecycling;
    }


}
