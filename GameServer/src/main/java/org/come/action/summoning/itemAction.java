package org.come.action.summoning;

import come.tool.Good.DropUtil;
import come.tool.Stall.AssetUpdate;
import io.netty.channel.ChannelHandlerContext;
import org.come.action.IAction;
import org.come.action.monitor.MonitorUtil;
import org.come.bean.LoginResult;
import org.come.bean.NChatBean;
import org.come.bean.SummonPetBean;
import org.come.bean.itemBean;
import org.come.entity.Goodstable;
import org.come.entity.RoleSummoning;
import org.come.handler.SendMessage;
import org.come.model.PetExchange;
import org.come.model.ItemExchange;
import org.come.protocol.Agreement;
import org.come.redis.RedisCacheUtil;
import org.come.server.GameServer;
import org.come.tool.WriteOut;
import org.come.until.AllServiceUtil;
import org.come.until.GsonUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * 合成神兽、解封召唤兽，客户端发来消耗物品和召唤的神兽，返回所有召唤兽信息
 * 金柳露
 *
 * @author 叶豪芳
 * @date 2018年1月4日 上午11:34:59
 */
public class itemAction implements IAction {
    public static Random random = new Random();
    public static String[] kxs = {"抗混乱=30", "抗封印=30", "抗昏睡=30", "抗中毒=30", "物理吸收=30", "抗风=30", "抗火=30", "抗水=30", "抗雷=30", "抗鬼火=30", "抗遗忘=30"};

    @Override
    public void action(ChannelHandlerContext ctx, String message) {
        System.out.println("ctx是" + ctx);
        System.out.println("message是" + message);

        LoginResult loginResult = GameServer.getAllLoginRole().get(ctx);
        System.out.println("roleid是" + loginResult.getRole_id());
        if (loginResult == null) {
            return;
        }
        // 获取发来的信息
        itemBean itemBean = GsonUtil.getGsonUtil().getgson().fromJson(message, itemBean.class);
        if (itemBean.getOpertype() == 0) {//插入数据库
            System.out.println("要插入数据库");
        }
        if (itemBean.getOpertype() == 100) {//宝宝兑换
            System.out.println("装备兑换接收信息了");
            exchange(itemBean, ctx);

        }
    }

    /**
     * 宝宝兑换
     */
    public void exchange(itemBean itemBean, ChannelHandlerContext ctx) {
        System.out.println("进入宝宝兑换了");
        System.out.println("itembean是" + itemBean.toString());
        System.out.println("ctx是" + ctx.toString());

        LoginResult loginResult = GameServer.getAllLoginRole().get(ctx);
        System.out.println("宝宝兑换的loginresult是" + loginResult.toString());
        if (loginResult == null) {
            return;
        }
        System.out.println("要兑换的装备id是" + itemBean.getzhuangbeiid().intValue());
        ItemExchange exchange = GameServer.getItemExchange(itemBean.getzhuangbeiid().intValue());
        System.out.println("exchange是" + exchange.toString());
        Goodstable item = exchange != null ? GameServer.getgoods(exchange.getGoods()) : null;

//		exchange.initPet(item);
        System.out.println("item" + item.toString());
        item.setRgid(RedisCacheUtil.getGoods_pk());
        if (item.getUsetime() == null) {
            item.setUsetime(1);
        }
        if (item.getStatus() == null) {
            item.setStatus(0);
        }

//item.setStatus(0);
        if (item == null) {
            SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().PromptAgreement("错误兑换公式"));
            System.out.println("错误兑换公式");

            return;
        } else
            System.out.println("装备不为空");

        //验证消耗
        List<Goodstable> list = new ArrayList<>();
        long money = 0;
        String goodName = null;
        String[] v = exchange.getConsume().split("\\|");
        System.out.println("v是" + v.toString());


        for (int i = 0; i < v.length; i++) {
            if (v[i].startsWith("D")) {
                money = Long.parseLong(v[i].substring(2));
                System.out.println("money是" + money);
                System.out.println("玩家的钱是" + loginResult.getGold());
                if (loginResult.getGold().longValue() < money) {
                    SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().PromptAgreement("金钱不够" + money));
                    return;
                }
            } else if (v[i].startsWith("G")) {
                String[] vs = v[i].split("=");
                BigDecimal goodid = new BigDecimal(vs[1]);
                int sum = Integer.parseInt(vs[2]);
                Goodstable goodstable = GameServer.getAllGoodsMap().get(goodid);
                System.out.println("goodsable是" + goodstable.toString());
                if (goodstable == null) {
                    SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().PromptAgreement("错误兑换公式"));
                    System.out.println("错误兑换二");
                    return;
                }
                List<Goodstable> goods1 = AllServiceUtil.getGoodsTableService().getGoodsByRoleID(loginResult.getRole_id());
                List<Goodstable> goods = goods1.stream().filter(item1 -> item1.getGoodsid().compareTo(goodid) == 0).collect(Collectors.toList());
                System.out.println("goods是----" + goods.toString());
                int SYsum = sum;
                for (int k = 0; k < goods.size(); k++) {
                    Goodstable good = goods.get(k);
                    if (goodName == null) {
                        goodName = good.getGoodsname();
                    }
                    if (good.getUsetime() >= SYsum) {
                        good.setUsetime(good.getUsetime() - SYsum);
                        SYsum = 0;
                        list.add(good);
                        break;
                    }
                    SYsum -= good.getUsetime();
                    good.setUsetime(0);
                    list.add(good);
                }
                if (SYsum > 0) {
                    SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().PromptAgreement("不够" + sum + "个" + goodstable.getGoodsname()));
                    return;
                }
            }
        }
        AssetUpdate assetUpdate = new AssetUpdate();
        assetUpdate.setType(AssetUpdate.USEGOOD);
        if (money != 0) {
            loginResult.setGold(loginResult.getGold().add(new BigDecimal(-money)));
            MonitorUtil.getMoney().useD(money);
        }
        assetUpdate.updata("D=-" + money);
        if (list.size() != 0) {
            for (int i = 0; i < list.size(); i++) {
                Goodstable good = list.get(i);
                AllServiceUtil.getGoodsTableService().updateGoodRedis(good);
                assetUpdate.updata("G" + good.getRgid() + "=" + good.getUsetime());
            }
        }
        item.setRole_id(loginResult.getRole_id());
        //星卡走兑换
        if (item.getType() == 520) {
            assetUpdate.setGood(item);
            item.setRole_id(loginResult.getRole_id());
            AllServiceUtil.getRoleSummoningService().insertitem(item);
        } else {
            String message = item.getGoodsid() + "," + 1 + "," + item.getGoodsname();
            adGoods(message, loginResult);
        }

//		assetUpdate.setGood(item);
        assetUpdate.setMsg("成功兑换#R" + item.getGoodsname()+("#Y消耗了#R#G" + money)+"#Y银两");
//        assetUpdate.setMsg("消耗了#R" + money);
        SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().assetAgreement(GsonUtil.getGsonUtil().getgson().toJson(assetUpdate)));
//       if (itemBean.getzhuangbeiid()!=1226)
        if (!item.getGoodsname().equals("超级藏宝图")){
            StringBuffer buffer = new StringBuffer();
            buffer.append("#Y天空一声巨响,#G");
            buffer.append(loginResult.getRolename());
            buffer.append("#Y终于集齐材料#G");
            buffer.append("#Y,获得一件#G");
            buffer.append(item.getGoodsname());
            buffer.append("#Y激动的赶紧将此宝贝收于麾下！#89");
            NChatBean bean = new NChatBean();
            bean.setId(10);
            bean.setMessage(buffer.toString());
            String msg = Agreement.getAgreement().chatAgreement(GsonUtil.getGsonUtil().getgson().toJson(bean));
            SendMessage.sendMessageToAllRoles(msg);
        }

    }


    private void adGoods(String message, LoginResult loginResult) {
        String[] sm = message.split(",");
        String mes = sm[0] + "|" + sm[1];
        String msg = ":25|" + mes + "|#AAFFFO";
        DropUtil.isDH(msg, loginResult);
    }


}
