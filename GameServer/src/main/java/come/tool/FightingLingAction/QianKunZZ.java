package come.tool.FightingLingAction;

import java.util.ArrayList;
import java.util.List;

import come.tool.FightingData.Battlefield;
import come.tool.FightingData.ChangeFighting;
import come.tool.FightingData.FightingEvents;
import come.tool.FightingData.FightingPackage;
import come.tool.FightingData.FightingSkill;
import come.tool.FightingData.FightingState;
import come.tool.FightingData.ManData;

public class QianKunZZ implements LingAction {

    @Override
    public void lingAction(ManData manData, List<ManData> help,
                           FightingSkill skill, Battlefield battlefield) {
        // TODO Auto-generated method stub
        List<ManData> nomydata = new ArrayList<>();
        int m=0;

        if (battlefield.fightingdata.size()<=1)return;
        if(manData.xzstate("冷却")!=null)return;
        m=manData.getCamp()==1?1:2;
        if (m==0)return;
        for (int p = battlefield.fightingdata.size() - 1; p >=0; p--) {

            if (battlefield.fightingdata.get(p).getCamp() ==m) {

                    if (battlefield.fightingdata.get(p).getType()!=3)
                        nomydata.add(battlefield.fightingdata.get(p));
            }

        }
        FightingEvents fightingEvents = new FightingEvents();
        List<FightingState> Accepterlist = new ArrayList<>();
        FightingState org = new FightingState();
        org.setCamp(manData.getCamp());
        org.setMan(manData.getMan());
        org.setStartState("法术攻击");
        org.setText("看我的乾坤遮天#0");
        manData.addAddState("冷却",3028,0,5);
        Accepterlist.add(org);
        ChangeFighting changeFighting = new ChangeFighting();
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
            FightingState accepter=new FightingState();
            accepter.setCamp(data.getCamp());
            accepter.setMan(data.getMan());
            accepter.setEndState_1("乾坤遮天");
            data.addAddState("乾坤遮天",0,0,3);
            data.setbianshen(true);
            accepter.setSkillskin("乾坤遮天");
            accepter.setStartState("代价");
            Accepterlist.add(accepter);
            fightingEvents.setAccepterlist(Accepterlist);
            FightingPackage.ChangeProcess(changeFighting,null , data, accepter, skill.getSkilltype(), Accepterlist, battlefield);







        }




        fightingEvents.setAccepterlist(Accepterlist);
        battlefield.NewEvents.add(fightingEvents);

    }




}
