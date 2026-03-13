package org.come.action.fight;

import come.tool.FightingData.FightingState;
import io.netty.channel.ChannelHandlerContext;

import org.come.action.IAction;
import org.come.handler.SendMessage;
import org.come.protocol.Agreement;
import org.come.until.GsonUtil;

import come.tool.Battle.BattleData;
import come.tool.Battle.BattleThreadPool;
import come.tool.FightingData.FightingEvents;

/**
 * 战斗回合转发
 *
 * @author 叶豪芳
 * @date 2018年1月6日 上午10:04:57
 */
public class FightRoundAction implements IAction {

	@Override
	public void action(ChannelHandlerContext ctx, String message) {


		// 接受客户端发来的消息
		FightingEvents fightingEvents = GsonUtil.getGsonUtil().getgson().fromJson(message, FightingEvents.class);
		FightingState originator = fightingEvents.getOriginator();
		if (originator != null) {
			BattleData battleData = BattleThreadPool.BattleDatas.get(fightingEvents.getFightingsum());
			if (battleData == null) {
				return;
			}
			// 如果不是指挥状态，添加策略到战斗数据中
			battleData.addPolicy(fightingEvents);
			// 格式化非指挥状态下的消息
			String fig10 = message.replace("\"Originator\":", ",")
					.replace("\"Accepterlist\":", "")
					.replaceAll("\\{", "")
					.replaceAll("\\}", "")
					.replaceAll("\"", "")
					.replace(":", "");
			String msg = Agreement.FightingBattleReportDealAgreement(GsonUtil.getGsonUtil().getgson().toJson(fig10));
			// 向所有参与者发送消息
			for (String participant : battleData.getParticipantlist()) {
				SendMessage.sendMessageByRoleName(participant, msg);
			}
		}
	}
}
