package org.come.action.vip;

import io.netty.channel.ChannelHandlerContext;
import org.come.action.IAction;
import org.come.entity.PayvipBean;
import org.come.handler.SendMessage;
import org.come.protocol.Agreement;
import org.come.server.GameServer;
import org.come.until.GsonUtil;

import java.util.List;

/** 获取vip兑换 */
public class GetGiftGradePackAction implements IAction {

	@Override
	public void action(ChannelHandlerContext ctx, String message) {
		// 查询最新的vip商城列表
		List<PayvipBean> payVipList = GameServer.getPayvipList();
		payVipList.get(0).setGetnumber(0);
		String mes = GsonUtil.getGsonUtil().getgson().toJson(payVipList);
		// 将信息返回给前端
		String sendmes = Agreement.getAgreement().GetgiftgradepackAgreement(mes);
		SendMessage.sendMessageToSlef(ctx, sendmes);
	}
}
