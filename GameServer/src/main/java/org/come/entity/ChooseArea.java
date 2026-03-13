package org.come.entity;

/**
 * 三端
 * @author zz
 * @time 2019年7月17日11:01:10
 * 
 */
public class ChooseArea {

	// 用户ID
	private String uid;
	// 区域ID
	private String qid;

	private String username;
	private String password;

	private int flag;

	private String tuijianma;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	private int type;

	public ChooseArea() {
		// TODO Auto-generated constructor stub
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getQid() {
		return qid;
	}

	public void setQid(String qid) {
		this.qid = qid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public String getTuijianma() {
		return tuijianma;
	}

	public void setTuijianma(String tuijianma) {
		this.tuijianma = tuijianma;
	}
}
