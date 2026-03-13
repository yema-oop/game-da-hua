package org.come.action.fight;


import come.tool.FightingData.ManData;
import io.netty.channel.ChannelHandlerContext;

import org.come.action.IAction;
import org.come.bean.LoginResult;
import org.come.entity.RoleSummoning;
import org.come.server.GameServer;

import come.tool.Battle.BattleData;
import come.tool.Battle.BattleThreadPool;
import org.come.until.AllServiceUtil;

import java.math.BigDecimal;

/**
 * 战斗结束,
 * @author 叶豪芳
 * @date 2017年11月24日 下午11:31:12
 * 
 */ 
public class FightingendAction implements IAction {
	static int size=0;
	@Override
	public void action(ChannelHandlerContext ctx, String message) {
		LoginResult roleinfo=GameServer.getAllLoginRole().get(ctx);	
		if (roleinfo==null) {return;}
		BattleData battleData=BattleThreadPool.BattleDatas.get(roleinfo.getFighting());
		if (battleData!=null){
			battleData.getParticipantlist().remove(roleinfo.getRolename());
			BattleThreadPool.sendBattleState(0,roleinfo.getRolename());
			for (int i = battleData.getBattlefield().fightingdata.size()-1; i >=0; i--) {
				//逃跑单独计算 Hp mp
				ManData data=battleData.getBattlefield().fightingdata.get(i);
				if(data.getManname().equals(roleinfo.getRolename()) && data.getStates() == 2){
					if (data.getHp().longValue()<=0) {
						roleinfo.setHp((data.getHp_z().multiply(BigDecimal.valueOf(0.25))));
						roleinfo.setMp((data.getMp_z().multiply(BigDecimal.valueOf(0.25))));
					}else {
						roleinfo.setHp(data.getHp().longValue()>0?data.getHp():BigDecimal.ONE);
						roleinfo.setMp(data.getMp());
					}

					if (battleData.getBattlefield().isFightType()) {
						RoleSummoning pet=data.getPet(true, true,true,true);
						if (pet!=null) {
							AllServiceUtil.getRoleSummoningService().updatePetRedis(pet);
						}
					}
					if(roleinfo.getRoleData().getLimit("VIP")!=null){
						RoleSummoning pet=data.vipUpdatePet();
						if (pet!=null) {
							AllServiceUtil.getRoleSummoningService().updatePetRedis(pet);
						}
					}
					break;

				}


			}

		}
		roleinfo.setFighting(0);
		
		// 修复：战斗结束后清理 Tab 切换映射，防止状态残留
		// 当一键多开角色在战斗中 Tab 切换后，currentLoginMap 可能残留错误映射
		ChannelHandlerContext reverseCtx = org.come.action.sys.ChangeRoleAction.reverseMap.get(ctx);
		if (reverseCtx != null && reverseCtx != ctx) {
			// 清除此角色的 Tab 切换映射
			org.come.action.sys.ChangeRoleAction.currentLoginMap.remove(ctx);
			org.come.action.sys.ChangeRoleAction.currentLoginMap.remove(reverseCtx);
			org.come.action.sys.ChangeRoleAction.reverseMap.remove(ctx);
		}
		
		// 修复：确保战斗结束后角色数据正确保存，防止下线时报错导致封号
		try {
			// 强制同步 HP/MP 到数据库
			org.come.until.AllServiceUtil.getRoleTableService().updateRoleWhenExit(roleinfo);
		} catch (Exception e) {
			// 记录错误但不影响正常流程
			org.come.tool.WriteOut.addtxt("战斗结束保存 HP/MP 报错:" + e.getMessage(), 9999);
		}
	}
}
