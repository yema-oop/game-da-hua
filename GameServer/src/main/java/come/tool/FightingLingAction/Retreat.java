package come.tool.FightingLingAction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import come.tool.FightingData.*;

public class Retreat implements LingAction{

	@Override
	public void lingAction(ManData manData, List<ManData> help,
			FightingSkill skill, Battlefield battlefield) {

		// TODO Auto-generated method stub
		List<ManData> nomydata = new ArrayList<>();
		int m=0;
		ChangeFighting changeFighting = new ChangeFighting();
		FightingState accepter=new FightingState();
		if (battlefield.fightingdata.size()<=1)return;
		if(skill.getSkillid()==3029){
			m=manData.getCamp()==1?2:1;
			if (m==0)return;
			for (int p = battlefield.fightingdata.size() - 1; p >=0; p--) {
				if (battlefield.fightingdata.get(p).getCamp() ==m) {
                      if (nomydata.size()<=1){if (battlefield.fightingdata.get(p).getType()==0)
						  nomydata.add(battlefield.fightingdata.get(p));}
				}if (nomydata.size()==0)continue;
				changeFighting.setChangehp((nomydata.get(0).getHp().negate()).divide(BigDecimal.valueOf(2)));
				accepter.setSkillskin("3029");
			}
		}else if (skill.getSkillid()==3030){
           Random tem=new Random();
           int o=tem.nextInt(100);
           int l=1;
           if (o<30)l=2;
			m=manData.getCamp()==1?2:1;
			for (int p = battlefield.fightingdata.size() - 1; p >=0; p--) {
				if (battlefield.fightingdata.get(p).getCamp() ==m) {
					if (nomydata.size()<=l){if (battlefield.fightingdata.get(p).getType()==1)
						nomydata.add(battlefield.fightingdata.get(p));}
				}if (nomydata.size()==0)continue;
				changeFighting.setChangehp((nomydata.get(0).getHp().negate()).divide(BigDecimal.valueOf(2)));
				accepter.setSkillskin("3030");
		}}else if (skill.getSkillid()==3031){
			m=manData.getCamp()==1?1:2;
			for (int p = battlefield.fightingdata.size() - 1; p >=0; p--) {

				if (battlefield.fightingdata.get(p).getCamp() ==m) {
					if (battlefield.fightingdata.get(p).getSp().longValue()>manData.getSp().longValue()&&nomydata.size()<=3)
					if (battlefield.fightingdata.get(p).getType()==0)
						nomydata.add(battlefield.fightingdata.get(p));
				}if (nomydata.size()==0)continue;
				changeFighting.setChangehp((nomydata.get(0).getHp().negate()).divide(BigDecimal.valueOf(3)));
				accepter.setSkillskin("3031");
		}}
		if (nomydata.size()==0)return;
		FightingEvents fightingEvents = new FightingEvents();
		List<FightingState> Accepterlist = new ArrayList<>();
		FightingState org = new FightingState();
		org.setCamp(manData.getCamp());
		org.setMan(manData.getMan());
		org.setStartState("法术攻击");
		org.setText("看我的厉害#0");
		Accepterlist.add(org);
		FightingState state = new FightingState();
		for (int i = help.size() - 1; i >= 0; i--) {
			FightingState org1 = new FightingState();
			org1.setCamp(help.get(i).getCamp());
			org1.setMan(help.get(i).getMan());
			org1.setStartState("法术攻击");
			Accepterlist.add(org1);
		}
		for (int i=0;i<=nomydata.size()-1;i++){
			ManData data=nomydata.get(i);
			if(data.getQKZT())return;
			data.addAddState("乾坤破阵",0,0,2);
            data.setbianshen(true);
			accepter.setCamp(data.getCamp());
			accepter.setMan(data.getMan());
			accepter.setEndState_1("乾坤破阵");
			changeFighting.setChangesum(2);
			accepter.setStartState("代价");
			Accepterlist.add(accepter);
			fightingEvents.setAccepterlist(Accepterlist);
			FightingPackage.ChangeProcess(changeFighting,null , data, accepter, skill.getSkilltype(), Accepterlist, battlefield);
		}
		fightingEvents.setAccepterlist(Accepterlist);
		battlefield.NewEvents.add(fightingEvents);
	}

}
