package come.tool.FightingData;

import java.math.BigDecimal;

/**原始的战斗数据*/
public class Original {

	private BigDecimal hp_z;
	private double hpxs;
	private BigDecimal mp_z;
	private double mpxs;
	private String model;
	
	public Original(BigDecimal hp_z, BigDecimal mp_z, String model) {
		super();
		this.hp_z = hp_z;
		this.mp_z = mp_z;
		this.model = model;
		this.hpxs=1D;
		this.mpxs=1D;
	}
	public double upXS(int type,double xs){
		if (type==0) {return hpxs+=xs;}
		else if (type==1) {return mpxs+=xs;}
		return 1D;
	}
	public BigDecimal getHp_z() {
		return hp_z;
	}
	public void setHp_z(BigDecimal hp_z) {
		this.hp_z = hp_z;
	}
	public BigDecimal getMp_z() {
		return mp_z;
	}
	public void setMp_z(BigDecimal mp_z) {
		this.mp_z = mp_z;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	
	
}
