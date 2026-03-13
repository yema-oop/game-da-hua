// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   StallBuy.java

package come.tool.Stall;

import java.math.BigDecimal;

public class StallSell
{

	private int id;
	private BigDecimal roleid;
	private int type;
	private BigDecimal buyid;
	private int sum;

	public StallSell()
	{
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public BigDecimal getRoleid()
	{
		return roleid;
	}

	public void setRoleid(BigDecimal roleid)
	{
		this.roleid = roleid;
	}

	public int getType()
	{
		return type;
	}

	public void setType(int type)
	{
		this.type = type;
	}

	public BigDecimal getBuyid()
	{
		return buyid;
	}

	public void setBuyid(BigDecimal buyid)
	{
		this.buyid = buyid;
	}

	public int getSum()
	{
		return sum;
	}

	public void setSum(int sum)
	{
		this.sum = sum;
	}
}
