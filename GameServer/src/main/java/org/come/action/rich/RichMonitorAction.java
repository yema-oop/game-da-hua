package org.come.action.rich;

import come.tool.Stall.Commodity;
import come.tool.Stall.Stall;
import come.tool.Stall.StallPool;
import io.netty.channel.ChannelHandlerContext;

import org.come.action.IAction;
import org.come.bean.LoginResult;
import org.come.entity.Goodstable;
import org.come.entity.Lingbao;
import org.come.entity.RoleSummoning;
import org.come.handler.SendMessage;
import org.come.protocol.Agreement;
import org.come.server.GameServer;
import org.come.until.AllServiceUtil;
import org.come.until.GsonUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class RichMonitorAction implements IAction {

	@Override
	public void action(ChannelHandlerContext ctx, String message) {
		// TODO Auto-generated method stub
		InputBean InputBean=GsonUtil.getGsonUtil().getgson().fromJson(message, InputBean.class);
		if (InputBean.getType()==2) {
			Goodstable goodstable = AllServiceUtil.getGoodsTableService().getGoodsByRgID(InputBean.getId());
			if (goodstable!=null) {
				// 单独返回的消息
				String messages = Agreement.getAgreement().richMAgreement(InputBean.getType(),GsonUtil.getGsonUtil().getgson().toJson(goodstable));
				SendMessage.sendMessageToSlef(ctx,messages);
			}
		}else if (InputBean.getType()==3) {
			RoleSummoning pet=AllServiceUtil.getRoleSummoningService().selectRoleSummoningsByRgID(InputBean.getId());
			List<Goodstable> goodstables = new ArrayList<>();

			if (pet.getStye()!=null&&pet.getStye().length()>1) {
				String[] v=pet.getStye().split("\\|");
				for (int i = 1; i < v.length; i++) {
					String[] vs=v[i].split("-");
					if (vs.length>=2) {
						Goodstable goodstable = AllServiceUtil.getGoodsTableService().getGoodsByRgID(new BigDecimal(vs[1]));
						goodstables.add(goodstable);
					}
				}
			}
			pet.setGoodstables(goodstables);

			if (pet!=null) {
				// 单独返回的消息
				String messages = Agreement.getAgreement().richMAgreement(InputBean.getType(),GsonUtil.getGsonUtil().getgson().toJson(pet));
				SendMessage.sendMessageToSlef(ctx,messages);
			}
		}else if (InputBean.getType()==4) {
			Lingbao lingbao=AllServiceUtil.getLingbaoService().selectLingbaoByID(InputBean.getId());
			if (lingbao!=null) {
				// 单独返回的消息
				String messages = Agreement.getAgreement().richMAgreement(InputBean.getType(),GsonUtil.getGsonUtil().getgson().toJson(lingbao));
				SendMessage.sendMessageToSlef(ctx,messages);
			}
		}else if(InputBean.getType()==5){
			Stall stall= StallPool.getPool().getAllStall().get(InputBean.getId().intValue());
			if(stall==null){
				SendMessage.sendMessageToSlef(ctx,Agreement.getAgreement().PromptAgreement("摊位不存在"));
			}
			LoginResult loginResult = GameServer.getAllLoginRole().get(ctx);
			if(loginResult.getRole_id().compareTo(stall.getRoleid())==0){
				SendMessage.sendMessageToSlef(ctx,Agreement.getAgreement().PromptAgreement("自卖自销吗#35,这在大唐是不允许的#35"));
				return;
			}
			Stall dt = new Stall();
			dt.setId(stall.getId());
			dt.setMapid(stall.getMapid());
			dt.setState(stall.getState());
			dt.setRole(stall.getRole());
			dt.setStall(stall.getStall());
			dt.setRoleid(stall.getRoleid());
			dt.setPets(stall.getPets());
			dt.setSpid(stall.getSpid());
			dt.setX(stall.getX());
			dt.setY(stall.getY());
			dt.setSellMsgs(stall.getSellMsgs());
			dt.setCollectMsg(stall.getCollectMsg());
			Commodity[] goodstables = stall.getGoodstables();
			Commodity[] dtg = new Commodity[24];
			for (int i = 0; i <goodstables.length ; i++) {
				if (goodstables[i]!=null&&goodstables[i].getShougou()==0) {
					dtg[i]=goodstables[i];
				}
			}
			dt.setPets(stall.getPets());
			if (stall!=null) {
				dt.setGoodstables(dtg);
				SendMessage.sendMessageToSlef(ctx,Agreement.getAgreement().stallAgreement(GsonUtil.getGsonUtil().getgson().toJson(dt)));
			}
		}
	}

}
