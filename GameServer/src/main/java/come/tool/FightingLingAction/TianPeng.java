package come.tool.FightingLingAction;

import come.tool.FightingData.*;

import java.util.ArrayList;
import java.util.List;

public class TianPeng implements LingAction {
    public void lingAction(ManData manData, List<ManData> help,
                           FightingSkill skill, Battlefield battlefield) {
        // TODO Auto-generated method stub
        List<ManData> nomydata=new ArrayList<>();

        int mubiao=10;
      switch (help.size()){
            case 2:
            mubiao=3;
            break;
            case 3:
                mubiao=5;
            break;
            case 4:
                mubiao=7;
        }
        int m=0;
        m=manData.getCamp()==1?2:1;
        if (m==0)return;
        if(manData.xzstate("冷却")!=null)return;
        if(battlefield.fightingdata.size()<mubiao)mubiao=battlefield.fightingdata.size();
        for (int p=mubiao-1;p>=0;p--){//PK要改下这个
            if (battlefield.fightingdata.get(p).getCamp()==m&&battlefield.fightingdata.get(p).getType()!=3){
                nomydata.add(battlefield.fightingdata.get(p));
            }
        }
        List<FightingState> Accepterlist=new ArrayList<>();
        FightingState org=new FightingState();
        org.setCamp(manData.getCamp());
        org.setMan(manData.getMan());
        org.setStartState("法术攻击");
        org.setText("看我的天蓬转世#0");
        manData.addAddState("冷却",3027,0,5);
      Accepterlist.add(org);
        ChangeFighting changeFighting=new ChangeFighting();
        FightingEvents fightingEvents=new FightingEvents();
        for (int i = help.size()-1; i >=0; i--) {
            FightingState org1=new FightingState();
            org1.setCamp(help.get(i).getCamp());
            org1.setMan(help.get(i).getMan());
            org1.setStartState("法术攻击");
            Accepterlist.add(org1);
        }
        for (int i=0;i<=nomydata.size()-1;i++){
            ManData data=nomydata.get(i);
            FightingState accepter=new FightingState();
            accepter.setCamp(data.getCamp());
            accepter.setMan(data.getMan());
            data.setbianshen(true);
            accepter.setEndState_1("天蓬转世");
            data.addAddState("天蓬转世",0,0,2);
            accepter.setSkillskin("3027");
            Accepterlist.add(accepter);
            Ql ql=new Ql();
            ql.addK_BHSY(-2);
            ql.addK_FHSLG(-5);
            data.setQuality(ql);
            FightingPackage.ChangeProcess(changeFighting, null, data, accepter, skill.getSkilltype(), Accepterlist, battlefield);
        }
        fightingEvents.setAccepterlist(Accepterlist);
       battlefield.NewEvents.add(fightingEvents);
    }
}