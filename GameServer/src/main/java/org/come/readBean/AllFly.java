package org.come.readBean;

import org.come.entity.Fly;

import java.util.concurrent.ConcurrentHashMap;

public class AllFly {
	private ConcurrentHashMap<Integer, Fly> allFly;

	public ConcurrentHashMap<Integer, Fly> getAllFly() {
		return allFly;
	}

	public void setAllFly(ConcurrentHashMap<Integer, Fly> allFly) {
		this.allFly = allFly;
	}
}
