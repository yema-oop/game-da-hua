package org.come.action.wechat;

import come.tool.Stall.AssetUpdate;
import io.netty.channel.ChannelHandlerContext;
import org.come.action.IAction;
import org.come.bean.LoginResult;
import org.come.bean.Role_bean;
import org.come.handler.SendMessage;
import org.come.protocol.Agreement;
import org.come.server.GameServer;
import org.come.until.AllServiceUtil;
import org.come.until.GsonUtil;

import java.math.BigDecimal;

/**
 * 千里眼功能
 * @author Administrator
 *
 */
public class SearcahChatRoleMapAction implements IAction {

	@Override
	public void action(ChannelHandlerContext ctx, String message) {
		//千里眼功能
		// 获取角色ID
		if(message == null) return;
		BigDecimal roleID = new BigDecimal(message);
		// 查找角色信息

		LoginResult roleInfo = AllServiceUtil.getRoleTableService().selectRoleID(roleID);
		if(roleInfo==null || GameServer.getRoleNameMap().get(roleInfo.getRolename()) == null ){
			AssetUpdate assetUpdate=new AssetUpdate(AssetUpdate.USEGOOD);
			assetUpdate.setMsg("此人不在线");
			SendMessage.sendMessageToSlef(ctx,Agreement.getAgreement().assetAgreement(GsonUtil.getGsonUtil().getgson().toJson(assetUpdate)));
			return;
		}
		roleInfo = GameServer.getAllLoginRole().get(GameServer.getRoleNameMap().get(roleInfo.getRolename()));
		if( roleInfo!= null ){
			Role_bean bean = new Role_bean();
			// 封装bean返回客户端
			bean.setGangname(roleInfo.getGangname());
			bean.setGrade(roleInfo.getGrade());
			bean.setRace_name(roleInfo.getRace_name());
			bean.setRole_id(roleInfo.getRole_id());
			bean.setRolename(roleInfo.getRolename());
			bean.setTitle(roleInfo.getTitle());
			bean.setMapId(roleInfo.getMapid());
			bean.setX(roleInfo.getX());
			bean.setY(roleInfo.getY());
			bean.setMapName(GameServer.getMapName(roleInfo.getMapid()+""));
			SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().searcahChatRoleMapAgreement(GsonUtil.getGsonUtil().getgson().toJson(bean)));
		}
	}

}
