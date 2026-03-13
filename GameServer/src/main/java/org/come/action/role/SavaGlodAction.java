package org.come.action.role;

import come.tool.Stall.AssetUpdate;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.lang3.StringUtils;
import org.come.action.IAction;
import org.come.bean.LoginResult;
import org.come.entity.Record;
import org.come.handler.SendMessage;
import org.come.protocol.Agreement;
import org.come.server.GameServer;
import org.come.until.AllServiceUtil;
import org.come.until.GsonUtil;

import java.math.BigDecimal;

/**
 * 角色移动
 *
 * @author 叶豪芳
 * @date : 2017年11月30日 下午4:01:59
 */
public class SavaGlodAction implements IAction {

    @Override
    public void action(ChannelHandlerContext ctx, String message) {
        // 获得人物信息
        LoginResult roleInfo = GameServer.getAllLoginRole().get(ctx);

        if (StringUtils.isBlank(message))
            return;
        String[] split = message.split("\\|");
        BigDecimal bigDecimal = new BigDecimal(split[1]);


        //存款
        if (split[0].equals("1")) {
            BigDecimal gold = roleInfo.getGold();
            if (gold.compareTo(bigDecimal) == -1) {
                SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().PromptAgreement("你身上没有那么多银两"));
                return;
            }
            roleInfo.setDeposit(roleInfo.getDeposit().add(bigDecimal));
            roleInfo.setGold(roleInfo.getGold().subtract(bigDecimal));


            AssetUpdate assetUpdate = new AssetUpdate();
            assetUpdate.updata("D=-" + bigDecimal);
            assetUpdate.updata("SAVEGOLD=" + roleInfo.getDeposit());
            assetUpdate.setMsg("你向钱庄存入了" + roleInfo.getDeposit().toString() + "两银子");
            assetUpdate.setType(AssetUpdate.USEGOOD);
            SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().assetAgreement(GsonUtil.getGsonUtil().getgson().toJson(assetUpdate)));
            AllServiceUtil.getRecordService().insert(new Record(5, "角色Id" + roleInfo.getRole_id() + ":存款" + bigDecimal.longValue() + ",剩余" + roleInfo.getGold()));
        }
        //取款
        else if (split[0].equals("2")) {
            BigDecimal gold = roleInfo.getDeposit();
            if (gold.compareTo(bigDecimal) == -1) {
                SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().PromptAgreement("你在钱庄里的存的银两不够"));
                return;
            }
            roleInfo.setDeposit(roleInfo.getDeposit().subtract(bigDecimal));
            roleInfo.setGold(roleInfo.getGold().add(bigDecimal));

            AssetUpdate assetUpdate = new AssetUpdate();
            assetUpdate.updata("D=" + bigDecimal.toString());
            assetUpdate.updata("SAVEGOLD=" + roleInfo.getDeposit().toString());
            assetUpdate.setMsg("你从钱庄取出了" + bigDecimal.toString() + "两银子");
            assetUpdate.setType(AssetUpdate.USEGOOD);
            SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().assetAgreement(GsonUtil.getGsonUtil().getgson().toJson(assetUpdate)));
            AllServiceUtil.getRecordService().insert(new Record(5, "角色Id" + roleInfo.getRole_id() + ":取款" + bigDecimal.longValue() + ",剩余存款" + roleInfo.getDeposit()));
        }
    }

}
