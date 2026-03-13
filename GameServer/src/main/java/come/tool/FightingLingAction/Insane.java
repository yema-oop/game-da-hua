package come.tool.FightingLingAction;

import java.util.ArrayList;
import java.util.List;

import come.tool.FightingData.*;

public class Insane implements LingAction {

	@Override
	public void lingAction(ManData manData, List<ManData> help,
						   FightingSkill skill, Battlefield battlefield) {
		// TODO Auto-generated method stub
		List<ManData> nomydata = new ArrayList<>();
		List<ManData> Tempydata = new ArrayList<>();
		int m=0;
		if (battlefield.fightingdata.size()<=1)return;

		m=manData.getCamp()==1?2:1;
		if (m==0)return;
		for (int p = battlefield.fightingdata.size() - 1; p >=0; p--) {

			if (battlefield.fightingdata.get(p).getCamp() ==m) {
                 if (nomydata.size()<=2){
					 if (battlefield.fightingdata.get(p).getType()==0)
						 nomydata.add(battlefield.fightingdata.get(p));

				 }

			}
		}
		if (nomydata.size()<=1)
			return;
		else if (nomydata.get(0).getTPZS()){return;}else if (nomydata.get(1).getTPZS()){return;}
		FightingEvents fightingEvents = new FightingEvents();
		List<FightingState> Accepterlist = new ArrayList<>();
		FightingState org = new FightingState();
		org.setCamp(manData.getCamp());
		org.setMan(manData.getMan());
		org.setStartState("法术攻击");
		//  Accepterlist.add(org);
		ChangeFighting changeFighting = new ChangeFighting();
		FightingState state = new FightingState();
		for (int i = help.size() - 1; i >= 0; i--) {
			FightingState org1 = new FightingState();
			org1.setCamp(help.get(i).getCamp());
			org1.setMan(help.get(i).getMan());
			org1.setStartState("法术攻击");
			Accepterlist.add(org1);
		}

		for (int i=0;i<=nomydata.size()-2;i++){
           List<FightingSkill> temp;
			List<FightingSkill> temp1;
			ManData data=nomydata.get(i);
			ManData data1=nomydata.get(i+1);
			data.setTPZS(true);
			data1.setTPZS(true);
            temp=data.getSkills();
            data.setSkills(data1.getSkills());
            data1.setSkills(temp);
			Tempydata.add(data);
			Tempydata.add(data1);
            Battlefield.Delejiaohuan(data,data1);
			//Battlefield.jiohuan[]
           /* FightingState org2=new FightingState();
            org2.setCamp(data.getCamp());
            org2.setMan(data.getMan());
            org2.setSkillskin(skill.getSkillid()+"");
            org2.setStartState("天蓬转世");
            changeFighting.setChangesum(2);
            manData.ChangeData(changeFighting, state);*/
			data.addAddState("精神错乱",0,0,2);
			data1.addAddState("精神错乱",0,0,2);
			FightingEvents zl=new FightingEvents();
			FightingState accepter=new FightingState();
			accepter.setCamp(data.getCamp());
			accepter.setMan(data.getMan());
			//  List<FightingState> Accepterlist=new ArrayList<>();
			accepter.setEndState_1("精神错乱");
			//   accepter.setHp_Change(-1);
			changeFighting.setChangesum(2);
			accepter.setSkillskin("3026");
			accepter.setStartState("代价");
			Accepterlist.add(accepter);
			fightingEvents.setAccepterlist(Accepterlist);
			// NewEvents.add(zl);


			FightingPackage.ChangeProcess(changeFighting, null, data, accepter, skill.getSkilltype(), Accepterlist, battlefield);

	}fightingEvents.setAccepterlist(Accepterlist);
		battlefield.NewEvents.add(fightingEvents);
}}