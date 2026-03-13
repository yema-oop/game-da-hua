package org.come.model;

import come.tool.Calculation.BaseQl;

import java.math.BigDecimal;

/**
 * 称谓
 * @author Administrator
 *
 */
public class Title {
	// id
	private Integer id;
	// 称谓名称
	private String titlename;
	// 条件
	private String exist;
    //描述
	private String text;
	//属性
	private String value;
	//特效皮肤
	private String skin;
	//时间限制
	private int limitTime;
	private String  color;
	private transient BaseQl[] qls;
	
	public BaseQl[] getQls() {
		if (qls==null) {
			if (value!=null&&!value.equals("")) {
				String[] vs=value.split("\\|");
				qls=new BaseQl[vs.length];
				for (int i = 0; i < vs.length; i++) {
					String[] vss=vs[i].split("=");
					qls[i] = new BaseQl(vss[0], BigDecimal.valueOf(Double.parseDouble(vss[1])));
				}
			}
		}
		return qls;
	}
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitlename() {
		return titlename;
	}

	public void setTitlename(String titlename) {
		this.titlename = titlename;
	}

	public String getExist() {
		return exist;
	}

	public void setExist(String exist) {
		this.exist = exist;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getSkin() {
		return skin;
	}

	public void setSkin(String skin) {
		this.skin = skin;
	}


	public int getLimitTime() {
		return this.limitTime;
	}

	public void setLimitTime(int limitTime) {
		this.limitTime = limitTime;
	}


	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}
}
