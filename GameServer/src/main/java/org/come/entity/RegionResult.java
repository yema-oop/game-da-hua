package org.come.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 三端
 * 
 * @author zz
 * @time 2019年7月17日14:16:18
 * 
 */
public class RegionResult {

	List<GetServers> Servers=new ArrayList<>();//远程服务器获取区服信息

	// 地域名称
	private String RE_NAME;
	// 区域ID
	private BigDecimal RA_ID;
	// 区域名称
	private String RA_NAME;
	private String ip;
	private int port;
	private String dowport;

	private String OT_BELONG;

	// 大区火热度
	private int regionHeat = 2;
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getDowport() {
		return dowport;
	}

	public void setDowport(String dowport) {
		this.dowport = dowport;
	}

	public RegionResult() {
		// TODO Auto-generated constructor stub
	}

	//
	public List<GetServers> getServerinfo() {
		return Servers;
	}
	public String getRE_NAME() {
		return RE_NAME;
	}

	public void setRE_NAME(String rE_NAME) {
		RE_NAME = rE_NAME;
	}

	public BigDecimal getRA_ID() {
		return RA_ID;
	}

	public void setRA_ID(BigDecimal rA_ID) {
		RA_ID = rA_ID;
	}

	public String getRA_NAME() {
		return RA_NAME;
	}

	public void setRA_NAME(String rA_NAME) {
		RA_NAME = rA_NAME;
	}

	public int getRegionHeat() {
		return regionHeat;
	}

	public void setRegionHeat(int regionHeat) {
		this.regionHeat = regionHeat;
	}

	public String getOT_BELONG() {
		return OT_BELONG;
	}

	public void setOT_BELONG(String OT_BELONG) {
		this.OT_BELONG = OT_BELONG;
	}

	public List<GetServers> getServers() {
		return Servers;
	}

	public void setServers(List<GetServers> servers) {
		Servers = servers;
	}
}
