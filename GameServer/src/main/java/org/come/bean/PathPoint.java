package org.come.bean;

import java.math.BigDecimal;

public class PathPoint {
  
	private int x;
//	private int y;
	private BigDecimal y;
	public PathPoint() {
		// TODO Auto-generated constructor stub
	}
	public PathPoint(int x, int y) {
		super();
		this.x = x;
		this.y = BigDecimal.valueOf(y);
	}

	public PathPoint(int x, BigDecimal y) {
		super();
		this.x = x;
		this.y = y;
	}
	public void add(int v){
		x+=v;
		y = y.add(BigDecimal.valueOf(v));
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		if(y== null) return 0;
		return y.intValue();
	}
	public BigDecimal getBY() {
		return y;
	}
	public void setY(int y) {
		this.y = BigDecimal.valueOf(y);
	}
}
