package org.come.action.sale;

import come.tool.Stall.AssetUpdate;
import io.netty.channel.ChannelHandlerContext;
import org.come.action.IAction;
import org.come.bean.DianKaListBean;
import org.come.bean.LoginResult;
import org.come.bean.NChatBean;
import org.come.entity.DianKaEntity;
import org.come.handler.SendMessage;
import org.come.protocol.Agreement;
import org.come.server.GameServer;
import org.come.serviceImpl.DianKaServiceImpl;
import org.come.until.AllServiceUtil;
import org.come.until.GsonUtil;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * 指定购买查询
 * @author Administrator
 *
 */
public class DianKaBuy implements IAction {

    @Override
    public void action(ChannelHandlerContext ctx, String message) {

        LoginResult roleInfo = GameServer.getAllLoginRole().get(ctx);
        DianKaEntity dianKaEntity = GsonUtil.getGsonUtil().getgson().fromJson(message, DianKaEntity.class);
        boolean find = false;
        long otherRoleid = dianKaEntity.getRoleid().longValue();
        long total_price = dianKaEntity.getDianKaNum() * dianKaEntity.getMoney();
        if (total_price > roleInfo.getGold().longValue()) {
            SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().PromptAgreement("你没有这么多的大话币"));
        }else{
            for(int i= 0 ; i < DianKaServiceImpl.getDianKas().size(); i++){
                if(DianKaServiceImpl.getDianKas().get(i).getRoleid().longValue() == otherRoleid
                        && dianKaEntity.getOutTime() == DianKaServiceImpl.getDianKas().get(i).getOutTime()){

                    // 对自己
                    AssetUpdate asset = new AssetUpdate(AssetUpdate.USEGOOD);
                    asset.setMsg("购买成功");
                    asset.updata("X=" + (dianKaEntity.getDianKaNum()));
                    roleInfo.setCodecard(roleInfo.getCodecard().add(new BigDecimal(dianKaEntity.getDianKaNum())));
                    asset.updata("D=" + (-total_price));
                    roleInfo.setGold(roleInfo.getGold().subtract(new BigDecimal(total_price)));
                    SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().assetAgreement(GsonUtil.getGsonUtil().getgson().toJson(asset)));
                    // 对别人
                    //玩家在线
                    // LoginResult otherRoleInfo = GameServer.getAllLoginRole().get(GameServer.getRoleNameMap().get(dianKaEntity.getName()));
                    LoginResult otherRoleInfo = AllServiceUtil.getRoleTableService().selectRoleID(dianKaEntity.getRoleid());
                    if( GameServer.getRoleNameMap().get(dianKaEntity.getName()) != null ){
                        AssetUpdate otherAsset = new AssetUpdate(AssetUpdate.USEGOOD);
                        otherAsset.setMsg("#Y你寄售的#R"+dianKaEntity.getDianKaNum()+"#Y点卡已经被其他玩家购买,你获得了#R"+total_price+"#Y两银子");

                        BigDecimal otherGold = otherRoleInfo.getGold();
                        BigDecimal otherNowGold = otherGold.add(new BigDecimal(total_price));
                        otherRoleInfo.setGold(otherNowGold);
                        otherAsset.updata("D=" + (total_price));
                        //otherRoleInfo.setGold(roleInfo.getGold().add(new BigDecimal(total_price)));
                        SendMessage.sendMessageToSlef(GameServer.getRoleNameMap().get(dianKaEntity.getName()),
                                Agreement.getAgreement().assetAgreement(GsonUtil.getGsonUtil().getgson().toJson(otherAsset)));

                        NChatBean bean=new NChatBean();
                        bean.setId(9);
                        String roId = "***"+roleInfo.getRole_id().toString().substring(roleInfo.getRole_id().toString().length()-2);
                        String otId = "***"+otherRoleInfo.getRole_id().toString().substring(otherRoleInfo.getRole_id().toString().length()-2);
                        bean.setMessage("#Y"+roId+"#W成功购得#Y"+otId+"#W出售的#Y"+dianKaEntity.getDianKaNum()+"点#W仙玉，售价#Y"+total_price+"两银子。");
                        String msg = Agreement.getAgreement().chatAgreement(GsonUtil.getGsonUtil().getgson().toJson(bean));
                        SendMessage.sendMessageToAllRoles(msg);
                    }else{
                        //直接修改数据库
                        AllServiceUtil.getRoleTableService().updateAddMoneyRoleID(dianKaEntity.getRoleid(), new BigDecimal(total_price));

                        NChatBean bean=new NChatBean();
                        bean.setId(9);
                        String roId = "***"+roleInfo.getRole_id().toString().substring(roleInfo.getRole_id().toString().length()-2);
                        String otId = "***"+otherRoleInfo.getRole_id().toString().substring(otherRoleInfo.getRole_id().toString().length()-2);
                        bean.setMessage("#Y"+roId+"#W成功购得#Y"+otId+"#W出售的#Y"+dianKaEntity.getDianKaNum()+"点#W仙玉，售价#Y"+total_price+"两银子。");
                        String msg = Agreement.getAgreement().chatAgreement(GsonUtil.getGsonUtil().getgson().toJson(bean));
                        SendMessage.sendMessageToAllRoles(msg);
                    }


                    if(dianKaEntity.getDianKaNum() >= DianKaServiceImpl.getDianKas().get(i).getDianKaNum()){
                        AllServiceUtil.getDianKaService().deleteDianKa(dianKaEntity.getRoleid().longValue(), dianKaEntity.getOutTime());
                        DianKaServiceImpl.remove(i);
                    }else{
                        Integer leftDianShu = DianKaServiceImpl.getDianKas().get(i).getDianKaNum() - dianKaEntity.getDianKaNum();
                        long leftCommitte = DianKaServiceImpl.getDianKas().get(i).getCommitt() - (long)Math.ceil(total_price * 0.05);
                        AllServiceUtil.getDianKaService().updateDianNum(dianKaEntity.getRoleid().longValue(), dianKaEntity.getOutTime(),
                                leftDianShu, leftCommitte);

                        DianKaServiceImpl.getDianKas().get(i).setDianKaNum(leftDianShu);
                        DianKaServiceImpl.getDianKas().get(i).setCommitt(leftCommitte);
                    }


                    find = true;

                    break;
                }
            }

            if(!find){
                SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().PromptAgreement("未找到需要购买的商品"));
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
