package come.tool.FightingDataAction;

import come.tool.FightingData.*;
import org.springframework.beans.factory.support.ManagedList;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class WanZhongFengQing implements DataAction {
	@Override
	public void analysisAction(ManData myData, FightingEvents fightingEvents, String type, Battlefield battlefield) {
		//int nocamp = MixDeal.getcamp(type, myData.getCamp(), battlefield.nomy(myData.getCamp()));
       //这里输出下
		FightingSkill skill = myData.getSkillType(TypeUtil.BB_WZFQ);

		int size = (int) skill.getSkillhurt();
		long sum=skill.getSkillsum()+(Battlefield.random.nextInt(100)<skill.getSkillgain()?1:0);

		List<ManData> nomyDatas=MixDeal.getdaji(sum, myData.getCamp(), fightingEvents, battlefield);
		if (nomyDatas.size()==0){;return;}

		//int  gan=fightingEvents.getAccepterlist().get(0).getMan();
		List<ManData> datas = MixDeal.get(false, myData, 0, 1, 0, 0, 0, 0, 10, -1, battlefield, 0);

		int gan =Battlefield.random.nextInt(datas.size());

		List<ManData> gans= new ManagedList<>();

		if(datas.size()>0) {
			MixDeal.move(myData.getCamp(), myData.getMan(),"瞬移",battlefield.nomy(1)+"|"+gan+"|3",battlefield);
			FightingEvents gjEvents = new FightingEvents();
			List<FightingState> zls = new ArrayList<>();
			FightingState gjz = new FightingState();
			gjz.setCamp(myData.getCamp());
			gjz.setMan(myData.getMan());
			gjz.setEndState(Battlefield.random.nextInt(8)+"");///ddd
			gjz.setStartState("普通攻击");
			gjz.setText("#G看我的剑荡八荒，#2");
			zls.add(gjz);
			gjEvents.setAccepterlist(zls);
			battlefield.NewEvents.add(gjEvents);

			for (int i = 0; i < datas.size(); i++) {
				if(datas.get(i).getMan()==gan){gans.add(datas.get(i));};
				switch (gan)
				{
					case 0:
						if (datas.get(i).getMan()==1||datas.get(i).getMan()==5&&datas.get(i).getStates()==0) gans.add(datas.get(i));
						break;
					case 1:
						if (datas.get(i).getMan()==0||datas.get(i).getMan()==2||datas.get(i).getMan()==6&&datas.get(i).getStates()==0) gans.add(datas.get(i));
						break;
					case 2:
						if (datas.get(i).getMan()==1||datas.get(i).getMan()==3||datas.get(i).getMan()==7&&datas.get(i).getStates()==0) gans.add(datas.get(i));
						break;
					case 3:
						if (datas.get(i).getMan()==2||datas.get(i).getMan()==4||datas.get(i).getMan()==8&&datas.get(i).getStates()==0) gans.add(datas.get(i));
						break;
					case 4:
						if (datas.get(i).getMan()==3||datas.get(i).getMan()==9&&datas.get(i).getStates()==0) gans.add(datas.get(i));
						break;
					case 5:
						if (datas.get(i).getMan()==0||datas.get(i).getMan()==6&&datas.get(i).getStates()==0) gans.add(datas.get(i));
						break;
					case 6:
						if (datas.get(i).getMan()==1||datas.get(i).getMan()==5||datas.get(i).getMan()==7&&datas.get(i).getStates()==0) gans.add(datas.get(i));
						break;
					case 7:
						if (datas.get(i).getMan()==2||datas.get(i).getMan()==6||datas.get(i).getMan()==8&&datas.get(i).getStates()==0) gans.add(datas.get(i));
						break;
					case 8:
						if (datas.get(i).getMan()==3||datas.get(i).getMan()==7||datas.get(i).getMan()==9&&datas.get(i).getStates()==0) gans.add(datas.get(i));
						break;
					case 9:
						if (datas.get(i).getMan()==4||datas.get(i).getMan()==8&&datas.get(i).getStates()==0) gans.add(datas.get(i));
						break;
				}
			}


			FightingEvents gjEvents2 = new FightingEvents();
			List<FightingState> zls2 = new ArrayList<>();

			for(int i=0;i<gans.size();i++)
			{
				FightingState gjz2 = new FightingState();
				gjz2.setCamp(gans.get(i).getCamp());
				gjz2.setMan(gans.get(i).getMan());
				//gjz.setEndState(Battlefield.random.nextInt(8)+"");
				gjz.setEndState(Battlefield.random.nextInt(8)+"");
				gjz2.setSkillskin("1255");
				zls2.add(gjz2);
				extracted(myData, battlefield, zls2, gans.get(i), 1);
			}
			//MixDeal.move(myData.getCamp(), myData.getMan(),"瞬移",myData.getCamp()+"|"+myData.getMan(),battlefield);
			gjEvents2.setAccepterlist(zls2);
			battlefield.NewEvents.add(gjEvents2);

		}
    }

	private void extracted(ManData myData, Battlefield battlefield, List<FightingState> zls,
			               ManData data, double apPercentage) {
		FightingState ace1 = new FightingState();
		ChangeFighting acec1 = new ChangeFighting();//根据伤害等，修改属性
		//Hurt(long ap,int g,ManData mydaData,ManData nomaData,String type,FightingState ace,List<FightingState> zls, Battlefield battlefield,double zm,double kb){
		BigDecimal ap = PhyAttack.Hurt(myData.getAp(), 1, myData, data, TypeUtil.PTGJ, ace1, zls,battlefield,0,0);
		acec1.setChangehp((ap.negate().multiply(BigDecimal.valueOf(apPercentage))));
		acec1.setChangevlaue(20);
		acec1.setChangesum(1);//持续回合
		FightingPackage.ChangeProcess(acec1, null, data, ace1, TypeUtil.PTGJ, zls, battlefield);//执行属性改变
		//ace1.setSkillskin(TypeUtil.BB_TQLH);
	}

}
