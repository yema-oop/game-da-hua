package org.come.readBean;

import java.util.List;

import org.come.model.ActiveAward;
import org.come.model.ActiveBase;

public class AllActive {

	private ActiveAward[] vipAwards;//VIP奖励
	private ActiveAward[] awards;//活跃奖励
	private ActiveBase[] bases;//获取活跃选项
	
	public AllActive(List<ActiveAward> vipAwardList,List<ActiveAward> awardList,List<ActiveBase> baseList) {
		super();
		vipAwards=new ActiveAward[vipAwardList.size()];
		for (int i = 0; i < vipAwards.length; i++) {vipAwards[i]=vipAwardList.get(i);}

		awards=new ActiveAward[awardList.size()];
		for (int i = 0; i < awards.length; i++) {awards[i]=awardList.get(i);}
		
		bases=new ActiveBase[baseList.size()];
		for (int i = 0; i < bases.length; i++) {bases[i]=baseList.get(i);}
	}
	public AllActive(List<ActiveBase> baseList) {
		bases=new ActiveBase[baseList.size()];
		for (int i = 0; i < bases.length; i++) {bases[i]=baseList.get(i);}
	}

	public ActiveAward[] getVipAwards() {
		return vipAwards;
	}

	public void setVipAwards(ActiveAward[] vipAwards) {
		this.vipAwards = vipAwards;
	}

	public ActiveAward[] getAwards() {
		return awards;
	}
	public void setAwards(ActiveAward[] awards) {
		this.awards = awards;
	}
	public ActiveBase[] getBases() {
		return bases;
	}
	public void setBases(ActiveBase[] bases) {
		this.bases = bases;
	}
	
}
