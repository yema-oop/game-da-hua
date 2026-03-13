package org.come.action.summoning;

import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.lang3.StringUtils;
import org.come.action.IAction;
import org.come.bean.LoginResult;
import org.come.entity.Record;
import org.come.entity.RoleSummoning;
import org.come.handler.SendMessage;
import org.come.protocol.Agreement;
import org.come.protocol.AgreementUtil;
import org.come.protocol.ParamTool;
import org.come.server.GameServer;
import org.come.until.AllServiceUtil;
import org.come.until.GsonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 修改宠物信息
 * @author 叶豪芳
 * @date 2017年12月23日 下午8:03:21
 *
 */
public class PetChangeAction implements IAction{
	private static List<String> lykstr = new ArrayList<String>() {
		{
			add("抗遗忘");
			add("抗雷");
			add("抗风");
			add("抗水");
			add("抗火");
			add("抗封印");
			add("抗昏睡");
			add("抗遗忘");
			add("物理吸收");
			add("抗中毒");
			add("抗鬼火");
			add("抗混乱");
		}
	};
	@Override
	public void action(ChannelHandlerContext ctx, String message) {
		// 获得宠物信息
		RoleSummoning roleSummoning = GsonUtil.getGsonUtil().getgson().fromJson(message, RoleSummoning.class);
		LoginResult loginResult = GameServer.getAllLoginRole().get(ctx);
		List<RoleSummoning> roleSummonings=null;
		if(roleSummoning.isShow()){
			//宠物跟随
//			roleSummonings = AllServiceUtil.getRoleSummoningService().selectRoleSummoningsByRoleID(loginResult.getRole_id());
//			if (roleSummonings.stream().filter(e->e.isShow()).count()>2){
//				SendMessage.sendMessageByRoleName(loginResult.getRolename(),Agreement.getAgreement().PromptAgreement("最多跟随3只"));
//				return;
//			}
		}
		//验证lyk
		if (StringUtils.isNotBlank(roleSummoning.getLyk())) {
//			String[] split = roleSummoning.getLyk().split("\\|");
//			for (String item : split) {
//				String[] split1 = item.split("=");
//				if (!lykstr.contains(split1[0]) || Float.parseFloat(split1[1]) >= 250) {
//					roleSummoning.setLyk("");
//					SendMessage.sendMessageByRoleName(loginResult.getRolename(), Agreement.getAgreement().PromptAgreement("修改宝宝什么的最讨厌了#116，你想秒怪你说啊，你不说我怎么知道你想秒怪#91"));
//					SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().inlineLoginAgreement());
//					ParamTool.ACTION_MAP.get(AgreementUtil.accountstop).action(GameServer.getRoleNameMap().get(loginResult.getRolename()), loginResult.getUserName());
//					AllServiceUtil.getRecordService().insert(new Record(5, "封号:" + loginResult.getUserName() + "_理由:" + "修改宠物抗性" + roleSummoning.getLyk()));//添加记录
//					return;   //炼妖石封号
//				}
//
//			}
		}
		try {
			RoleSummoning roleSummoning1 = AllServiceUtil.getRoleSummoningService().selectRoleSummoningsByRgID(roleSummoning.getSid());
			if (StringUtils.isNotBlank(roleSummoning1.getXl())) {
				roleSummoning.setXl(roleSummoning1.getXl());
			}
		} catch (Exception e) {

		}
		// 修改宠物信息
		AllServiceUtil.getRoleSummoningService().updateRoleSummoning(roleSummoning);

//		roleSummonings = AllServiceUtil.getRoleSummoningService().selectRoleSummoningsByRoleID(loginResult.getRole_id());
//		loginResult.setShowRoleSummoningList(roleSummonings);
		//通知地图
//		SendMessage.sendMessageToMapRoles(loginResult.getMapid(), Agreement.getAgreement().upRoleShowAgreement(GsonUtil.getGsonUtil().getgson().toJson(loginResult.getRoleShow())));

	}
}
