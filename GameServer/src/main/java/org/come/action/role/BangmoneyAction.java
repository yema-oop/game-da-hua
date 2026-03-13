package org.come.action.role;

import come.tool.Stall.AssetUpdate;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.lang3.StringUtils;
import org.come.action.IAction;
import org.come.bean.LoginResult;
import org.come.entity.Goodstable;
import org.come.entity.Record;
import org.come.handler.SendMessage;
import org.come.protocol.Agreement;
import org.come.server.GameServer;
import org.come.until.AllServiceUtil;
import org.come.until.GsonUtil;

import java.math.BigDecimal;
import java.util.List;

/**
 * 角色移动
 *
 * @author 叶豪芳
 * @date : 2017年11月30日 下午4:01:59
 */
public class BangmoneyAction implements IAction {

    @Override
    public void action(ChannelHandlerContext ctx, String message) {
        // 获得人物信息
        LoginResult roleInfo = GameServer.getAllLoginRole().get(ctx);
        if (StringUtils.isBlank(message))
            return;
        BigDecimal bg = new BigDecimal(message);
        if (roleInfo.getContribution().compareTo(bg) < 0) {
            SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().PromptAgreement("你没有那么多帮贡"));
            return;
        }
        if (roleInfo.getGold().compareTo(new BigDecimal(500000)) < 0) {
            SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().PromptAgreement("你没有那么多银两"));
            return;
        }
        if (!bg.equals(BigDecimal.valueOf(1000)) && !bg.equals(BigDecimal.valueOf(10000))) {
            SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().PromptAgreement("只能兑换1000或10000帮贡"));
            return;
        }
        roleInfo.setContribution(roleInfo.getContribution().subtract(bg));
        roleInfo.setGold(roleInfo.getGold().subtract(new BigDecimal(500000)));
        BigDecimal goodsId;

        if (bg.compareTo(BigDecimal.valueOf(10000)) == 0) {
            goodsId = BigDecimal.valueOf(81281);
        } else if (bg.compareTo(BigDecimal.valueOf(1000)) == 0) {
            goodsId = BigDecimal.valueOf(81261);
        } else {
            SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().PromptAgreement("只能兑换10000和1000帮贡#35"));
            return;
        }
        Goodstable goodstable = GameServer.getGood(goodsId);
        StringBuffer buffer = new StringBuffer();
        AssetUpdate assetUpdate = new AssetUpdate();
        assetUpdate.setType(AssetUpdate.USEGOOD);
        int mun = 1;

        goodstable.setRole_id(roleInfo.getRole_id());
        List<Goodstable> sameGoodstable = AllServiceUtil.getGoodsTableService().selectGoodsByRoleIDAndGoodsID(roleInfo.getRole_id(), goodstable.getGoodsid());
        if (sameGoodstable.size() != 0) {
            // 修改使用次数
            int uses = sameGoodstable.get(0).getUsetime() + mun;
            sameGoodstable.get(0).setUsetime(uses);
            // 修改数据库
            AllServiceUtil.getGoodsTableService().updateGoodRedis(sameGoodstable.get(0));
            assetUpdate.setGood(sameGoodstable.get(0));
        } else {
            goodstable.setUsetime(mun);
            // 插入数据库
            AllServiceUtil.getGoodsTableService().insertGoods(goodstable);
            assetUpdate.setGood(goodstable);
        }
        buffer.append("你获得了").append(mun).append("个");
        buffer.append(goodstable.getGoodsname());
        assetUpdate.setMsg(buffer.toString());
        SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().assetAgreement(GsonUtil.getGsonUtil().getgson().toJson(assetUpdate)));
    }
}
