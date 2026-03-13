package come.tool.FightingDataAction;

import come.tool.FightingData.Battlefield;
import come.tool.FightingData.FightingEvents;
import come.tool.FightingData.ManData;
import come.tool.FightingData.PK_MixDeal;
import io.netty.channel.ChannelHandlerContext;
import org.come.action.sys.ChangeRoleAction;
import org.come.server.GameServer;

public class Escape implements DataAction{

	@Override
	public void analysisAction(ManData manData, FightingEvents fightingEvents,
							   String type,Battlefield battlefield) {
		// TODO Auto-generated method stub
		int gl=80;
		//创建逃跑动作
//		 10 1阵营不能逃跑 2阵营逃跑概率调低
//		 15 16无法逃跑
		if (manData.getType()==2) {
			gl=101;
		}else if (battlefield.BattleType==10) {
			if (manData.getCamp()==1) {
				gl=-1;
			}else {
				gl=50;
			}
		}else if (battlefield.BattleType==15||battlefield.BattleType==16||battlefield.BattleType==101) {
			gl=-1;
		}
		if (type.equals("认输")) {
			gl = 1000;
		}
		if (Battlefield.random.nextInt(90) < gl) {
			fightingEvents.getOriginator().setStartState("逃跑成功");
			fightingEvents.getOriginator().setSkillsy("逃跑成功");
			if (type.equals("认输"))
				fightingEvents.getOriginator().setText("#G溜了溜了#35");
			if (manData.getType()==0) {
				for (int i = 0; i < 4; i++) {
					int rolepath=battlefield.Datapath(manData.getCamp(), manData.getMan()+i*5);
					if (rolepath!=-1)
					{
						battlefield.fightingdata.get(rolepath).setStates(2);
						battlefield.fightingdata.get(rolepath).getAddStates().clear();
					}
				}
			}else {
				manData.setStates(2);
			}
			ChannelHandlerContext ctx = GameServer.getRoleNameMap().get(manData.getManname());
			if (manData.getType() == 0 && ctx != null && ChangeRoleAction.reverseMap.get(ctx) != null) {
				battlefield.setEscape(true);
			}
		}else {
			fightingEvents.getOriginator().setStartState("逃跑失败");
			fightingEvents.getOriginator().setSkillsy("逃跑失败");
		}
		battlefield.NewEvents.add(fightingEvents);
	}



}
