package come.tool.FightingLingAction;

import come.tool.FightingData.*;

import java.util.ArrayList;
import java.util.List;

public class MaBuHuanKou implements LingAction {
    public void lingAction(ManData manData, List<ManData> help,
                           FightingSkill skill, Battlefield battlefield) {
        // TODO Auto-generated method stub
        List<ManData> nomydata=new ArrayList<>();
        int sum=0;
        int mubiao=0;
        switch (help.size()){
            case 3:
                sum=3;
                mubiao=3;break;
            case 4:
                mubiao=5;
                sum=5;break;
            case 5:
                sum=5;
                mubiao=7;
        }
        for (int p=battlefield.fightingdata.size()-1;p>=0;p--){
            if (battlefield.fightingdata.get(p).getCamp()==0){


                nomydata.add(battlefield.fightingdata.get(p));
            }


        }FightingEvents fightingEvents=new FightingEvents();
        List<FightingState> Accepterlist=new ArrayList<>();
        FightingState org=new FightingState();
        org.setCamp(manData.getCamp());
        org.setMan(manData.getMan());
        org.setStartState("法术攻击");
        //  Accepterlist.add(org);
        ChangeFighting changeFighting=new ChangeFighting();
        FightingState state=new FightingState();
        for (int i = help.size()-1; i >=0; i--) {
            FightingState org1=new FightingState();
            org1.setCamp(help.get(i).getCamp());
            org1.setMan(help.get(i).getMan());
            org1.setStartState("法术攻击");
            Accepterlist.add(org1);
        }
        for (int i=0;i<=nomydata.size()-1;i++){

            ManData data=nomydata.get(i);
           /* FightingState org2=new FightingState();
            org2.setCamp(data.getCamp());
            org2.setMan(data.getMan());
            org2.setSkillskin(skill.getSkillid()+"");
            org2.setStartState("天蓬转世");
            changeFighting.setChangesum(2);
            manData.ChangeData(changeFighting, state);*/
            data.addAddState("禁言",0,0,2);
            FightingEvents zl=new FightingEvents();
            FightingState accepter=new FightingState();
            accepter.setCamp(data.getCamp());
            accepter.setMan(data.getMan());
            //  List<FightingState> Accepterlist=new ArrayList<>();
            accepter.setEndState_1("禁言");
            //   accepter.setHp_Change(-1);
            changeFighting.setChangesum(5);
            accepter.setSkillskin("3025");
            accepter.setStartState("代价");
            Accepterlist.add(accepter);
            fightingEvents.setAccepterlist(Accepterlist);
            // NewEvents.add(zl);


            FightingPackage.ChangeProcess(changeFighting, null, data, accepter, skill.getSkilltype(), Accepterlist, battlefield);
        }

        fightingEvents.setAccepterlist(Accepterlist);
        battlefield.NewEvents.add(fightingEvents);
    }
}