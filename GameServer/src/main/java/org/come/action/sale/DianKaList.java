package org.come.action.sale;

import come.tool.Stall.AssetUpdate;
import come.tool.newGang.GangUtil;
import io.netty.channel.ChannelHandlerContext;
import org.come.action.IAction;
import org.come.bean.DianKaListBean;
import org.come.bean.GangListBean;
import org.come.bean.LoginResult;
import org.come.entity.DianKaEntity;
import org.come.handler.SendMessage;
import org.come.protocol.Agreement;
import org.come.server.GameServer;
import org.come.service.DianKaService;
import org.come.serviceImpl.DianKaServiceImpl;
import org.come.until.AllServiceUtil;
import org.come.until.GsonUtil;

import java.math.BigDecimal;
import java.util.List;

/**
 * 指定购买查询
 * @author Administrator
 *
 */
public class DianKaList implements IAction {

    @Override
    public void action(ChannelHandlerContext ctx, String message) {

        DianKaListBean dianKaListbean = new DianKaListBean();
        // 放进bean中返回客户端
        if(DianKaServiceImpl.isIsSearch()){
            List<DianKaEntity> diankaList = DianKaServiceImpl.getDianKas();
            dianKaListbean.setDianKa(diankaList);
            String msg = Agreement.getAgreement().DianKaListAgreement(GsonUtil.getGsonUtil().getgson().toJson(dianKaListbean));
            SendMessage.sendMessageToSlef(ctx, msg);
        }else {
            List<DianKaEntity> diankaList = AllServiceUtil.getDianKaService().selectAllDian();
            dianKaListbean.setDianKa(diankaList);
            DianKaServiceImpl.setDian(diankaList);
            String msg = Agreement.getAgreement().DianKaListAgreement(GsonUtil.getGsonUtil().getgson().toJson(dianKaListbean));
            SendMessage.sendMessageToSlef(ctx, msg);
        }
    }

}
