package org.come.action.sale;

import come.tool.Stall.AssetUpdate;
import come.tool.newGang.GangUtil;
import io.netty.channel.ChannelHandlerContext;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import org.come.action.IAction;
import org.come.action.monitor.MonitorUtil;
import org.come.bean.*;
import org.come.entity.*;
import org.come.entity.SalegoodsExample.Criteria;
import org.come.handler.SendMessage;
import org.come.protocol.Agreement;
import org.come.server.GameServer;
import org.come.serviceImpl.DianKaServiceImpl;
import org.come.until.AllServiceUtil;
import org.come.until.GsonUtil;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
/**
 * 指定购买查询
 * @author Administrator
 *
 */
public class DianKaSale implements IAction {

    @Override
    public void action(ChannelHandlerContext ctx, String message) {

        LoginResult roleInfo = GameServer.getAllLoginRole().get(ctx);
        String dianka_type = message.substring(message.length() - 3);
        String jsontype =  message.substring(0, message.length() - 3);
        if(dianka_type.equals("sel")) {
            DianKaEntity dianKaEntity = GsonUtil.getGsonUtil().getgson().fromJson(jsontype, DianKaEntity.class);
            long total_price = dianKaEntity.getDianKaNum() * dianKaEntity.getMoney();
            long committ = Long.valueOf((long) Math.ceil(total_price * 0.05));
            dianKaEntity.setCommitt(committ);
            //long all_fee = total_price + committ;
            if (dianKaEntity.getDianKaNum() > roleInfo.getCodecard().longValue()) {
                SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().PromptAgreement("你没有这么多的仙玉"));
            } else if (committ > roleInfo.getGold().longValue()) {
                SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().PromptAgreement("你没有足够的押金"));
            } else if (DianKaServiceImpl.getDianKas().size() > 200) {
                SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().PromptAgreement("上架的点卡已超出限制"));
            }else {
                AllServiceUtil.getDianKaService().insertDianKaRec(dianKaEntity);

                DianKaServiceImpl.add(dianKaEntity);
                AssetUpdate asset = new AssetUpdate(AssetUpdate.USEGOOD);
                asset.setMsg("售卖成功");

                asset.updata("X=" + (-dianKaEntity.getDianKaNum()));
                roleInfo.setCodecard(roleInfo.getCodecard().subtract(new BigDecimal(dianKaEntity.getDianKaNum())));

                asset.updata("D=" + (-committ));
                roleInfo.setGold(roleInfo.getGold().subtract(new BigDecimal(committ)));
                SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().assetAgreement(GsonUtil.getGsonUtil().getgson().toJson(asset)));

                NChatBean bean=new NChatBean();
                bean.setId(9);
                String roId = "***"+roleInfo.getRole_id().toString().substring(roleInfo.getRole_id().toString().length()-2);
                bean.setMessage("#R"+roId+"#W已经以#R"+total_price+"#W的价格寄售了#R"+dianKaEntity.getDianKaNum()+"点#W仙玉。");
                String msgb = Agreement.getAgreement().chatAgreement(GsonUtil.getGsonUtil().getgson().toJson(bean));
                SendMessage.sendMessageToAllRoles(msgb);

                NChatBean bean1=new NChatBean();
                bean1.setId(10);
//                String roId = "***"+roleInfo.getRole_id().toString().substring(roleInfo.getRole_id().toString().length()-2);
                bean1.setMessage("#R"+roId+"#W已经以#R"+total_price+"#W的价格寄售了#R"+dianKaEntity.getDianKaNum()+"点#W仙玉。");
                String msgb1 = Agreement.getAgreement().chatAgreement(GsonUtil.getGsonUtil().getgson().toJson(bean1));
                SendMessage.sendMessageToAllRoles(msgb1);
//            //玩家在线
//            if( GameServer.getRoleNameMap().get(roleName) != null ){
//                SendMessage.sendMessageToSlef(GameServer.getRoleNameMap().get(roleName), Agreement.getAgreement().assetAgreement(GsonUtil.getGsonUtil().getgson().toJson(assetUpdate)));
//            }
                //AllServiceUtil.getRoleTableService().updateMoneyRoleID(stall.getRoleid(),gold);
                DianKaListBean dianKaListbean = new DianKaListBean();
                List<DianKaEntity> diankaList = AllServiceUtil.getDianKaService().selectAllDian();
                dianKaListbean.setDianKa(diankaList);
                DianKaServiceImpl.setDian(diankaList);
                String msg = Agreement.getAgreement().DianKaListAgreement(GsonUtil.getGsonUtil().getgson().toJson(dianKaListbean));
                SendMessage.sendMessageToSlef(ctx, msg);
            }
        }else{
            DianKaEntity dianKaEntity = GsonUtil.getGsonUtil().getgson().fromJson(jsontype, DianKaEntity.class);
            boolean find = false;
            for(int i= 0 ; i < DianKaServiceImpl.getDianKas().size(); i++){
                if(Objects.equals(DianKaServiceImpl.getDianKas().get(i).getRoleid(), roleInfo.getRole_id())
                && dianKaEntity.getOutTime() == DianKaServiceImpl.getDianKas().get(i).getOutTime()){

                    AllServiceUtil.getDianKaService().deleteDianKa(roleInfo.getRole_id().longValue(), dianKaEntity.getOutTime());

                    AssetUpdate asset = new AssetUpdate(AssetUpdate.USEGOOD);
                    asset.setMsg("下架成功");

                    asset.updata("X=" + (DianKaServiceImpl.getDianKas().get(i).getDianKaNum()));
                    roleInfo.setCodecard(roleInfo.getCodecard().add(new BigDecimal(DianKaServiceImpl.getDianKas().get(i).getDianKaNum())));

                    asset.updata("D=" + (DianKaServiceImpl.getDianKas().get(i).getCommitt()));
                    roleInfo.setGold(roleInfo.getGold().add(new BigDecimal(DianKaServiceImpl.getDianKas().get(i).getCommitt())));

                    DianKaServiceImpl.remove(i);
                    SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().assetAgreement(GsonUtil.getGsonUtil().getgson().toJson(asset)));
                    find = true;
                    break;
                }
            }

            if(!find){
                SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().PromptAgreement("未找到需要下架的商品"));
            }
            DianKaListBean dianKaListbean = new DianKaListBean();
            List<DianKaEntity> diankaList = AllServiceUtil.getDianKaService().selectAllDian();
            dianKaListbean.setDianKa(diankaList);
            DianKaServiceImpl.setDian(diankaList);
            String msg = Agreement.getAgreement().DianKaListAgreement(GsonUtil.getGsonUtil().getgson().toJson(dianKaListbean));
            SendMessage.sendMessageToSlef(ctx, msg);

        }
    }

}
