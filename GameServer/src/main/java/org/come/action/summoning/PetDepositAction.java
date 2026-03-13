package org.come.action.summoning;

import come.tool.Role.Hang;
import come.tool.Role.RoleData;
import come.tool.Role.RolePool;
import come.tool.Stall.AssetUpdate;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.lang3.StringUtils;
import org.come.action.IAction;
import org.come.bean.LoginResult;
import org.come.entity.RoleSummoning;
import org.come.handler.SendMessage;
import org.come.protocol.Agreement;
import org.come.protocol.ParamTool;
import org.come.server.GameServer;
import org.come.until.AllServiceUtil;
import org.come.until.GsonUtil;

import java.math.BigDecimal;

/**
 * 宠物放生,客户端发来该宠物的标识，删除该宠物
 *
 * @author 叶豪芳
 * @date 2018年1月4日 上午10:42:21
 */
public class PetDepositAction implements IAction {

    @Override
    public void action(ChannelHandlerContext ctx, String message) {

        LoginResult roleInfo = GameServer.getAllLoginRole().get(ctx);
        if (roleInfo.getFighting() != 0) {
            SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().PromptAgreement("你还在战斗中"));
            return;
        }

        if (StringUtils.isBlank(message))
            return;

        if (message.startsWith("deposit")) {
            String[] split = message.split("=");
            RoleSummoning exRoleSummoning = AllServiceUtil.getRoleSummoningService().selectRoleSummoningsByRgID(new BigDecimal(split[1]));
            if (exRoleSummoning == null) return;
            if (roleInfo.getSummoning_id() != null && roleInfo.getSummoning_id().compareTo(exRoleSummoning.getSid()) == 0) {
                SendMessage.sendMessageToSlef(ctx,Agreement.getAgreement().PromptAgreement(("这只召唤兽已在参战中！无法寄存！")));
                return;
            }
            //判断当前召唤兽是否是自己的
            if (exRoleSummoning.getRoleid().compareTo(roleInfo.getRole_id()) != 0) {
                ParamTool.ACTION_MAP.get("accountstop").action(ctx, roleInfo.getUserName());
                ctx.close();
                return;
            }
            exRoleSummoning.setDeposit(1);
            AllServiceUtil.getRoleSummoningService().updateRoleSummoning(exRoleSummoning);
            AssetUpdate assetUpdate = new AssetUpdate();
            assetUpdate.setType(99);
            assetUpdate.updata("#DelSid="+exRoleSummoning.getSid());
            assetUpdate.setMsg("寄存成功！");
            SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().assetAgreement(GsonUtil.getGsonUtil().getgson().toJson(assetUpdate)));
        }
        else if (message.startsWith("depositPetList")) {
//            LoginResult roleInfo = GameServer.getAllLoginRole().get(ctx);

//            List<RoleSummoning> roleSummonings = AllServiceUtil.getRoleSummoningService().selectRoleSummoningsByRoleIDAndDeposit(roleInfo.getRole_id());
//            SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().assetAgreement(GsonUtil.getGsonUtil().getgson().toJson(assetUpdate)));
        }
        else if(message.startsWith("retrieve")){
            String[] split = message.split("=");
            RoleSummoning exRoleSummoning = AllServiceUtil.getRoleSummoningService().selectRoleSummoningsByRgID(new BigDecimal(split[1]));
            if (exRoleSummoning == null) return;
            //判断当前召唤兽是否是自己的
            if (exRoleSummoning.getRoleid().compareTo(roleInfo.getRole_id()) != 0) {
                ParamTool.ACTION_MAP.get("accountstop").action(ctx, roleInfo.getUserName());
                ctx.close();
                return;
            }
            if(exRoleSummoning.getDeposit() == 0 || exRoleSummoning.getDeposit() == null){
                SendMessage.sendMessageToSlef(ctx,Agreement.getAgreement().PromptAgreement(("这只召唤兽已取回！")));
                return;
            }

            RoleData data= RolePool.getRoleData(roleInfo.getRole_id());

//            roleInfo.getShowRoleSummoningList().add(exRoleSummoning);
            data.getPets().add(new Hang(exRoleSummoning.getSid()));

            exRoleSummoning.setDeposit(0);
            AllServiceUtil.getRoleSummoningService().updateRoleSummoning(exRoleSummoning);
            AssetUpdate assetUpdate = new AssetUpdate();
            assetUpdate.setType(99);
            assetUpdate.updata("#retrieve="+exRoleSummoning.getSid());
            assetUpdate.setMsg("取回成功！");
            SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().assetAgreement(GsonUtil.getGsonUtil().getgson().toJson(assetUpdate)));
        }
    }

}
