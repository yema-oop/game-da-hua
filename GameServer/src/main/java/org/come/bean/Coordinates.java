package org.come.bean;

public class Coordinates {

	private int mapID;
	private int x;
	private long y;
	
	public Coordinates(int mapID, int x, long y) {
		super();
		this.mapID = mapID;
		this.x = x;
		this.y = y;
	}
	public int getMapID() {
		return mapID;
	}
	public void setMapID(int mapID) {
		this.mapID = mapID;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public long getY() {
		return y;
	}
	public void setY(long y) {
		this.y = y;
	}
	
}
