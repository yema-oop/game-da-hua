package come.tool.Stall;

import io.netty.channel.ChannelHandlerContext;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.come.action.monitor.MonitorUtil;
import org.come.bean.LoginResult;
import org.come.entity.Goodstable;
import org.come.entity.Record;
import org.come.entity.RoleSummoning;
import org.come.handler.SendMessage;
import org.come.protocol.Agreement;
import org.come.server.GameServer;
import org.come.tool.EquipTool;
import org.come.until.AllServiceUtil;
import org.come.until.GsonUtil;

import come.tool.Mixdeal.AnalysisString;
import org.come.until.StringUtil;

/**
 * 摆摊工具
 *
 * @author Administrator
 */
public class StallPool {
    //税
//	private static long TAX;
    private static double TaxXs = 0.05;
    //	private static Object object=new Object();
    //1购买处理
    //试营业
    public static int PREPARE = 0;
    //营业
    public static int OFF = 1;
    //托管
    public static int MANAGE = 2;
    //关门
    public static int NO = 3;
    private static StallPool pool;

    public static StallPool getPool() {
        if (pool == null) {
            pool = new StallPool();
        }
        return pool;
    }

    public StallPool() {
        // TODO Auto-generated constructor stub
        allStall = new HashMap<>();
        allStallBean = new HashMap<>();
        mapStallBean = new HashMap<>();

    }

    //自增长的摊位的id
    private int increasesum = 100;
    //记录所有摊位的详细信息
    private Map<Integer, Stall> allStall;
    //记录所有的摊位
    private Map<Integer, StallBean> allStallBean;
    //根据地图id记录
    private Map<Integer, List<StallBean>> mapStallBean;

    //根据地图id获取
    public synchronized List<StallBean> getmap(int mapid) {
        List<StallBean> beans = mapStallBean.get(mapid);
        if (beans == null) {
            beans = new ArrayList<>();
            mapStallBean.put(mapid, beans);
        }
        return beans;
    }

    //添加一个摊位
    public StallBean addStall(Stall stall, ChannelHandlerContext ctx) {
        boolean is = false;
        stall.setId(getIncreasesum());
        allStall.put(stall.getId(), stall);
        StallBean bean = new StallBean(stall);
        allStallBean.put(bean.getId(), bean);
        getmap(bean.getMapid()).add(bean);
        //把资产隔离
        Commodity[] goodstables = stall.getGoodstables();
        Commodity[] pets = stall.getPets();
        for (int i = 0; i < goodstables.length; i++) {
            Commodity commodity = goodstables[i];
            if (commodity != null && commodity.getShougou() == 0) {
                Goodstable good = commodity.getGood();
                int usetime = good.getUsetime();
                if (usetime <= 0) {
                    is = true;
                    goodstables[i] = null;
                    continue;
                }
                good = AllServiceUtil.getGoodsTableService().getGoodsByRgID(good.getRgid());
                if (good == null || good.getRole_id().compareTo(stall.getRoleid()) != 0) {
                    is = true;
                    goodstables[i] = null;
                    continue;
                }
                if (AnalysisString.jiaoyi(good.getQuality())) {
                    is = true;
                    goodstables[i] = null;
                    StringBuffer buffer = new StringBuffer();
                    buffer.append(stall.getRoleid());
                    buffer.append("欲摆摊禁交易物品:");
                    buffer.append(good.getRgid());
                    buffer.append(":");
                    buffer.append(good.getGoodsname());
                    AllServiceUtil.getRecordService().insert(new Record(5, buffer.toString()));
                    continue;
                }
                good = good.clone();
                commodity.setGood(good);
                if (EquipTool.canSuper(good.getType())) {//叠加
                    // 判断该角色是否拥有这件物品
                    Goodstable goodstable = good.clone();
                    goodstable.setRole_id(stall.getRoleid());
                    if (usetime > goodstable.getUsetime()) {
                        usetime = goodstable.getUsetime();
                    }
                    goodstable.setUsetime(goodstable.getUsetime() - usetime);
                    AllServiceUtil.getGoodsTableService().updateGoodRedis(goodstable);
                    good.setUsetime(usetime);
                    good.setRole_id(new BigDecimal(-stall.getRoleid().longValue()));
                    AllServiceUtil.getGoodsTableService().insertGoods(good);
                } else {//不可叠加
                    AllServiceUtil.getGoodsTableService().updateGoodsIndex(good, new BigDecimal(-stall.getRoleid().longValue()), null, null);
                }
            }
        }
        for (int i = 0; i < pets.length; i++) {
            Commodity commodity = pets[i];
            if (commodity != null) {
                RoleSummoning pet = commodity.getPet();
                pet = AllServiceUtil.getRoleSummoningService().selectRoleSummoningsByRgID(pet.getSid());
                if (pet == null || pet.getRoleid().compareTo(stall.getRoleid()) != 0) {
                    is = true;
                    pets[i] = null;
                    continue;
                }
                commodity.setPet(pet);
                AllServiceUtil.getRoleSummoningService().updateRoleSummoningIndex(pet, new BigDecimal(-stall.getRoleid().longValue()));
            }
        }
        SendMessage.sendMessageToMapRoles(new Long(bean.getMapid()), Agreement.getAgreement().stallstateAgreement(GsonUtil.getGsonUtil().getgson().toJson(bean)));
        if (is) {
            SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().PromptAgreement("有部分物品属于违规已被取消上架"));
        }
        return bean;
    }

    public synchronized int getIncreasesum() {
        increasesum++;
        if (increasesum > 999999) increasesum = 100;
        return increasesum;
    }

    //修改摊位状态
    public boolean updateState(BigDecimal booth_id, int state, BigDecimal roleid) {
        if (booth_id == null) {
            return false;
        }
        int id = booth_id.intValue();
        Stall stall = allStall.get(id);
        StallBean bean = allStallBean.get(id);
        if (bean == null || stall == null || stall.getRoleid().compareTo(roleid) != 0) {
            return false;
        }
        stall.setState(state);
        bean.setState(state);
        if (state == NO) {
            allStall.remove(id);
            List<StallBean> list = getmap(stall.getMapid());
            list.remove(allStallBean.remove(id));
        }
        SendMessage.sendMessageToMapRoles(new Long(bean.getMapid()), Agreement.getAgreement().stallstateAgreement(GsonUtil.getGsonUtil().getgson().toJson(bean)));
        return true;
    }

    //购买摊位的物品和宝宝
    public synchronized void BuyStall(LoginResult loginResult, StallBuy buy) {
        Stall stall = allStall.get(buy.getId());
        if (stall == null) {
            SendMessage.sendMessageByRoleName(loginResult.getRolename(), Agreement.getAgreement().PromptAgreement("该摊位已经不存在了"));
            return;
        }
        switch (buy.getType()) {
            case 0:
                //购买物品
                BuyGood(loginResult, buy, stall);
                break;
            case 1:
                //购买召唤兽
                BuyPet(loginResult, buy, stall);
                break;
            case 2:
                //购买灵宝
                BuyLing(loginResult, buy, stall);
                break;
        }
    }

    //摊位卖出物品
    public synchronized void sellStall(LoginResult sellResult, StallSell stallSell) {
        Stall stall = allStall.get(stallSell.getId());
        if (stall == null) {
            SendMessage.sendMessageByRoleName(sellResult.getRolename(), Agreement.getAgreement().PromptAgreement("该摊位已经不存在了"));
            return;
        }
        Commodity commodity = stall.getGood(stallSell.getBuyid());
        if (commodity == null || commodity.getGood().getUsetime() < stallSell.getSum()) {
            SendMessage.sendMessageByRoleName(sellResult.getRolename(), Agreement.getAgreement().PromptAgreement("商家收购不了这么多"));
            SendMessage.sendMessageByRoleName(sellResult.getRolename(), Agreement.getAgreement().stallAgreement(GsonUtil.getGsonUtil().getgson().toJson(stall)));
            return;
        } else if (stallSell.getSum() <= 0) {
            SendMessage.sendMessageByRoleName(sellResult.getRolename(), Agreement.getAgreement().PromptAgreement("出售数量不能为0"));
            return;
        }
        List<Goodstable> sellGood = AllServiceUtil.getGoodsTableService().selectGoodsByRoleIDAndGoodsID(sellResult.getRole_id(), commodity.getGood().getGoodsid());
        if (sellGood == null || sellGood.isEmpty() || sellGood.get(0).getUsetime() < stallSell.getSum()) {
            SendMessage.sendMessageByRoleName(sellResult.getRolename(), Agreement.getAgreement().PromptAgreement("你没有这么多物品"));
            return;
        }
        LoginResult loginResult = null;
        ChannelHandlerContext ctx = GameServer.getRoleNameMap().get(stall.getRole());
        if (ctx != null) {
            loginResult = GameServer.getAllLoginRole().get(ctx);
            if (loginResult == null) {
                SendMessage.sendMessageByRoleName(sellResult.getRolename(), Agreement.getAgreement().PromptAgreement("收货方不在线"));
                return;
            }
        }
        Goodstable goodstable = commodity.getGood();
        AssetUpdate assetUpdate = new AssetUpdate();
        Integer dt = goodstable.getUsetime();
        if (EquipTool.canSuper(goodstable.getType())) {
            List<Goodstable> ltrGood = AllServiceUtil.getGoodsTableService().selectGoodsByRoleIDAndGoodsID(loginResult.getRole_id(), commodity.getGood().getGoodsid());
            if (ltrGood != null && ltrGood.size() != 0) {
                int uses = ltrGood.get(0).getUsetime() + stallSell.getSum();
                ltrGood.get(0).setUsetime(uses);
                // 修改数据库
                AllServiceUtil.getGoodsTableService().updateGoodRedis(ltrGood.get(0));
                assetUpdate.setGood(ltrGood.get(0));
                AllServiceUtil.getGoodsrecordService().insert(goodstable, stall.getRoleid(), stallSell.getSum(), -3);
                goodstable.setUsetime(stallSell.getSum() + ltrGood.get(0).getUsetime());
            } else {
                goodstable.setUsetime(stallSell.getSum());
                goodstable.setRole_id(stall.getRoleid());
                // 插入数据库
                AllServiceUtil.getGoodsTableService().insertGoods(goodstable);
                Goodstable gd = new Goodstable();
                gd.setRgid(goodstable.getRgid());
                gd.setGoodsid(goodstable.getGoodsid());
                gd.setRole_id(goodstable.getRole_id());
                gd.setUsetime(stallSell.getSum());
                gd.setValue(goodstable.getValue());
                gd.setType(goodstable.getType());
                gd.setQuality(goodstable.getQuality());
                gd.setStatus(goodstable.getStatus());
                gd.setGoodsname(goodstable.getGoodsname());
                gd.setSkin(goodstable.getSkin());
                assetUpdate.setGood(gd);
                AllServiceUtil.getGoodsrecordService().insert(goodstable, stall.getRoleid(), stallSell.getSum(), -3);
            }
        } else {
            goodstable.setUsetime(1);
            AllServiceUtil.getGoodsTableService().insertGoods(goodstable);
            assetUpdate.setGood(goodstable);
            AllServiceUtil.getGoodsrecordService().insert(goodstable, null, stallSell.getSum(), -3);
        }

        assetUpdate.setType(AssetUpdate.STALLSellGET);
        assetUpdate.updata("G" + commodity.getGood().getRgid() + "=" + stallSell.getSum());
        assetUpdate.setMsg(commodity.getGood().getGoodsname());
        long price = stallSell.getSum() * commodity.getMoney();
        assetUpdate.updata("D=" + (-price));
        //long s=(long) (price*TaxXs);
        if (loginResult != null) {
            if (price > loginResult.getGold().longValue()) {
                SendMessage.sendMessageByRoleName(sellResult.getRolename(), Agreement.getAgreement().PromptAgreement("收货方金钱不足"));
                return;
            }
            loginResult.setGold(new BigDecimal(loginResult.getGold().longValue() - price));
            goodstable.setUsetime(dt - stallSell.getSum());
            SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().assetAgreement(GsonUtil.getGsonUtil().getgson().toJson(assetUpdate)));
            SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().stallAgreement(GsonUtil.getGsonUtil().getgson().toJson(stall)));
        } else {
            SendMessage.sendMessageByRoleName(sellResult.getRolename(), Agreement.getAgreement().PromptAgreement("收货方不在线"));
            return;
        }
        // 交易记录
//        StringBuffer msg = new StringBuffer();
//        msg.append("#Y店主以").append(StringUtil.toMoneyString(BigDecimal.valueOf(price))).append("#Y的价格收购了玩家").append("#R").append(sellResult.getRolename());
//        msg.append("#Y的").append("#R").append(sellSum).append("#Y个").append("#G").append(good.getGoodsname());
//        stall.addCollectMsg(msg.toString());


        assetUpdate = new AssetUpdate();
        //Integer aa = stall.getGood(commodity.getGood().getRgid()).getGood().getUsetime();
        //commodity.getGood().setUsetime(stall.getGood(commodity.getGood().getRgid()).getGood().getUsetime()- stallSell.getSum());
        sellResult.setGold(new BigDecimal(sellResult.getGold().longValue() + price));
        assetUpdate.setData(null);
        assetUpdate.updata("D=" + (price));
        assetUpdate.setType(AssetUpdate.STALLSell);
        assetUpdate.setMsg(commodity.getGood().getGoodsname());
        List<Goodstable> ltrGood = AllServiceUtil.getGoodsTableService().selectGoodsByRoleIDAndGoodsID(sellResult.getRole_id(), commodity.getGood().getGoodsid());
        // 假设玩家ID是一个 BigDecimal
        String playerId = sellResult.getRole_id().toString();
        String maskedPlayerId;
        if (playerId.length() > 4) {
            maskedPlayerId = "****" + playerId.substring(4);
        } else {
            maskedPlayerId = "****";
        }
        StringBuffer msg = new StringBuffer();
        msg.append("#Y玩家").append("#R").append(maskedPlayerId)
        .append("#Y以").append("#G").append(StringUtil.toMoneyString(BigDecimal.valueOf(price)))
        .append("#Y的价格出售了").append(stallSell.getSum()).append("#Y个收购物品").append("#G").append(commodity.getGood().getGoodsname());
        stall.addSellMsg(msg.toString());


        Goodstable sg = ltrGood.get(0);
        //Integer xx = sg.getUsetime()-stallSell.getSum();
        assetUpdate.updata("G" + sg.getRgid() + "=" + (sg.getUsetime() - stallSell.getSum()));
        //Integer a = sg.getUsetime();
        sg.setUsetime(sg.getUsetime() - stallSell.getSum());
        AllServiceUtil.getGoodsTableService().updateGoodRedis(sg);
        SendMessage.sendMessageByRoleName(sellResult.getRolename(), Agreement.getAgreement().assetAgreement(GsonUtil.getGsonUtil().getgson().toJson(assetUpdate)));
        SendMessage.sendMessageByRoleName(sellResult.getRolename(), Agreement.getAgreement().stallAgreement(GsonUtil.getGsonUtil().getgson().toJson(stall)));
    }

    /**
     * 购买物品
     */
    public void BuyGood(LoginResult buyRole, StallBuy buy, Stall stall) {
        Commodity commodity = stall.getGood(buy.getBuyid());
        if (commodity == null || commodity.getGood().getUsetime() < buy.getSum()) {
            SendMessage.sendMessageByRoleName(buyRole.getRolename(), Agreement.getAgreement().PromptAgreement("下手慢了或者数量不足"));
            SendMessage.sendMessageByRoleName(buyRole.getRolename(), Agreement.getAgreement().stallAgreement(GsonUtil.getGsonUtil().getgson().toJson(stall)));
            return;
        } else if (buy.getSum() <= 0) {
            SendMessage.sendMessageByRoleName(buyRole.getRolename(), Agreement.getAgreement().PromptAgreement("购买数量不能为0"));
            return;
        }
        Goodstable goodstable = commodity.getGood();
        if (goodstable.getRole_id().longValue() > 0) {
            SendMessage.sendMessageByRoleName(buyRole.getRolename(), Agreement.getAgreement().PromptAgreement("摆摊物品异常"));
            return;
        }
        long price = buy.getSum() * commodity.getMoney();
        if (price > buyRole.getGold().longValue()) {
            SendMessage.sendMessageByRoleName(buyRole.getRolename(), Agreement.getAgreement().PromptAgreement("金钱不足"));
            return;
        }
        //扣钱
        buyRole.setGold(new BigDecimal(buyRole.getGold().longValue() - price));
        //判断是添加还是修改
        boolean add = true;
        //判断是否叠加  叠加返回ture
        boolean fold = EquipTool.canSuper(goodstable.getType());
        goodstable.setUsetime(goodstable.getUsetime() - buy.getSum());
        if (goodstable.getUsetime() <= 0 || !fold) {
            add = false;
            stall.Buy(commodity);
        }
        //摆摊记录
        AllServiceUtil.getGoodsrecordService().insert(goodstable, buyRole.getRole_id(), buy.getSum(), 1);
        //复制一个属性
        Goodstable good = goodstable.clone();
        good.setUsetime(buy.getSum());
        //给购买者添加物品
        if (fold) {//叠加
            // 判断该角色是否拥有这件物品
            List<Goodstable> sameGoodstable = AllServiceUtil.getGoodsTableService().selectGoodsByRoleIDAndGoodsID(buyRole.getRole_id(), good.getGoodsid());
            if (sameGoodstable.size() != 0) {
                // 修改使用次数
                sameGoodstable.get(0).setUsetime(sameGoodstable.get(0).getUsetime() + good.getUsetime());
                // 修改数据库
                AllServiceUtil.getGoodsTableService().updateGoodRedis(goodstable);
                AllServiceUtil.getGoodsTableService().updateGoodRedis(sameGoodstable.get(0));
                good = sameGoodstable.get(0);
            } else {
                // 插入数据库
                if (add) {
                    AllServiceUtil.getGoodsTableService().updateGoodRedis(goodstable);
                    good.setRole_id(buyRole.getRole_id());
                    AllServiceUtil.getGoodsTableService().insertGoods(good);
                } else {
                    AllServiceUtil.getGoodsTableService().updateGoodsIndex(good, buyRole.getRole_id(), null, null);
                }
            }
        } else {//不可叠加
            AllServiceUtil.getGoodsTableService().updateGoodsIndex(good, buyRole.getRole_id(), null, null);
        }
        // 交易记录
//        StringBuffer msg = new StringBuffer();
//        msg.append("#Y玩家").append("#R").append(buyRole.getRolename()).append("#Y花费").append(StringUtil.toMoneyString(BigDecimal.valueOf(price)));
//        msg.append("#Y购买了").append("#R").append(buy.getSum()).append("#Y个").append("#G").append(good.getGoodsname());
//        stall.addSellMsg(msg.toString());
        String playerId = buyRole.getRole_id().toString();
        String maskedPlayerId;
        if (playerId.length() > 4) {
            maskedPlayerId = "****" + playerId.substring(playerId.length() - 4);
        } else {
            maskedPlayerId = "****";
        }

        StringBuffer msg = new StringBuffer();
        msg.append("#Y玩家").append("#R").append(maskedPlayerId)
        .append("#Y花费").append("#G").append(StringUtil.toMoneyString(BigDecimal.valueOf(price)))
        .append("#Y购买了").append("#R").append(buy.getSum()).append("#Y个")
        .append("#G").append(good.getGoodsname());
        stall.addSellMsg(msg.toString());
        //扣税
        long s = (long) (price * TaxXs);
        MonitorUtil.getStallM().add(s);
        MonitorUtil.getMoney().useD(s);
        //交易处理
        AssetUpdate assetUpdate = new AssetUpdate();
        assetUpdate.setType(AssetUpdate.STALLGET);
        assetUpdate.setMsg(buy.getSum() + "个" + good.getGoodsname());
        assetUpdate.updata("D=" + (price - s));
        LoginResult loginResult = null;
        ChannelHandlerContext ctx = GameServer.getRoleNameMap().get(stall.getRole());
        if (ctx != null) {
            loginResult = GameServer.getAllLoginRole().get(ctx);
        }
        if (loginResult != null) {
            loginResult.setGold(new BigDecimal(loginResult.getGold().longValue() + price - s));
            SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().assetAgreement(GsonUtil.getGsonUtil().getgson().toJson(assetUpdate)));
            SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().stallAgreement(GsonUtil.getGsonUtil().getgson().toJson(stall)));
        } else {
            BigDecimal gold = AllServiceUtil.getRoleTableService().selectMoneyRoleID(stall.getRoleid());
            if(gold == null)
                gold = BigDecimal.ZERO;
            gold = gold.add(new BigDecimal(price - s));
            if (gold.compareTo(new BigDecimal("999999999999")) > 0) {
                gold = new BigDecimal("999999999999");
            }
            AllServiceUtil.getRoleTableService().updateMoneyRoleID(stall.getRoleid(), gold);
        }
        if (MonitorUtil.isUpMoney(3, price)) {
            StringBuffer buffer = new StringBuffer();
            buffer.append("摆摊大宗金额流动:");
            buffer.append(stall.getRoleid());
            buffer.append("卖给");
            buffer.append(buyRole.getRole_id());
            buffer.append(buy.getSum() + "个" + good.getGoodsname());
            buffer.append("物品id:" + good.getRgid());
            buffer.append("金额");
            buffer.append(price);
            AllServiceUtil.getRecordService().insert(new Record(3, buffer.toString()));
        }
        assetUpdate.setData(null);
        assetUpdate.updata("D=" + (-price));
        assetUpdate.setGood(good);
        assetUpdate.setType(AssetUpdate.STALLBUY);
        SendMessage.sendMessageByRoleName(buyRole.getRolename(), Agreement.getAgreement().assetAgreement(GsonUtil.getGsonUtil().getgson().toJson(assetUpdate)));
        SendMessage.sendMessageByRoleName(buyRole.getRolename(), Agreement.getAgreement().stallAgreement(GsonUtil.getGsonUtil().getgson().toJson(stall)));
    }

    /**
     * 购买宠物
     */
    public void BuyPet(LoginResult buyRole, StallBuy buy, Stall stall) {
        Commodity commodity = stall.getPet(buy.getBuyid());
        if (commodity == null) {
            SendMessage.sendMessageByRoleName(buyRole.getRolename(), Agreement.getAgreement().PromptAgreement("下手慢了"));
            SendMessage.sendMessageByRoleName(buyRole.getRolename(), Agreement.getAgreement().stallAgreement(GsonUtil.getGsonUtil().getgson().toJson(stall)));
            return;
        }
        RoleSummoning pet = commodity.getPet();
        long price = commodity.getMoney();
        if (price > buyRole.getGold().longValue()) {
            SendMessage.sendMessageByRoleName(buyRole.getRolename(), Agreement.getAgreement().PromptAgreement("金钱不足"));
            return;
        }
        //扣税
        long s = (long) (price * TaxXs);
        MonitorUtil.getStallM().add(s);
        MonitorUtil.getMoney().useD(s);
        //扣钱
        buyRole.setGold(new BigDecimal(buyRole.getGold().longValue() - price));
        //交易处理
        AssetUpdate assetUpdate = new AssetUpdate();
        assetUpdate.setType(AssetUpdate.STALLGET);
        assetUpdate.setMsg(buy.getSum() + "个" + pet.getSummoningname());
        assetUpdate.updata("D=" + (price - s));
        stall.Buy(commodity);
        LoginResult loginResult = null;
        ChannelHandlerContext ctx = GameServer.getRoleNameMap().get(stall.getRole());
        if (ctx != null) {
            loginResult = GameServer.getAllLoginRole().get(ctx);
        }
        if (loginResult != null) {
            loginResult.setGold(new BigDecimal(loginResult.getGold().longValue() + price - s));
            SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().assetAgreement(GsonUtil.getGsonUtil().getgson().toJson(assetUpdate)));
            SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().stallAgreement(GsonUtil.getGsonUtil().getgson().toJson(stall)));
        } else {
            BigDecimal gold = AllServiceUtil.getRoleTableService().selectMoneyRoleID(stall.getRoleid());
            gold = gold.add(new BigDecimal(price - s));
            if (gold.compareTo(new BigDecimal("999999999999")) > 0) {
                gold = new BigDecimal("999999999999");
            }
            AllServiceUtil.getRoleTableService().updateMoneyRoleID(stall.getRoleid(), gold);
        }
        //清空亲密
        pet.setFriendliness(0L);
        //修改数据库
        AllServiceUtil.getRoleSummoningService().updateRoleSummoningIndex(pet, buyRole.getRole_id());
        assetUpdate.setPet(pet);
        assetUpdate.setType(AssetUpdate.STALLBUY);
        //转移召唤兽装备
        List<BigDecimal> goods = pet.getGoods();
        if (goods != null) {
            for (BigDecimal v : goods) {
                Goodstable good = AllServiceUtil.getGoodsTableService().getGoodsByRgID(v);
                if (good != null) {
                    AllServiceUtil.getGoodsTableService().updateGoodsIndex(good, buyRole.getRole_id(), null, null);
                    assetUpdate.setGood(good);
                } else {
                    System.out.println("不见的id:" + v);
                }
            }
        }
        // 交易记录
        StringBuffer msg = new StringBuffer();
        msg.append("#Y玩家").append("#R").append(buyRole.getRolename()).append("#Y花费").append(StringUtil.toMoneyString(BigDecimal.valueOf(price)));
        msg.append("#Y购买了").append("#G").append(pet.getSummoningname());
        stall.addSellMsg(msg.toString());
        if (MonitorUtil.isUpMoney(3, price)) {
            StringBuffer buffer = new StringBuffer();
            buffer.append("摆摊大宗金额流动:");
            buffer.append(stall.getRoleid());
            buffer.append("卖给");
            buffer.append(buyRole.getRole_id());
            buffer.append("宠物id:" + pet.getSid());
            buffer.append("金额");
            buffer.append(price);
            AllServiceUtil.getRecordService().insert(new Record(3, buffer.toString()));
        }
        assetUpdate.setData(null);
        assetUpdate.updata("D=" + (-price));
        SendMessage.sendMessageByRoleName(buyRole.getRolename(), Agreement.getAgreement().assetAgreement(GsonUtil.getGsonUtil().getgson().toJson(assetUpdate)));
        SendMessage.sendMessageByRoleName(buyRole.getRolename(), Agreement.getAgreement().stallAgreement(GsonUtil.getGsonUtil().getgson().toJson(stall)));
    }

    /**
     * 购买灵宝
     */
    public void BuyLing(LoginResult buyRole, StallBuy buy, Stall stall) {

    }

    //收摊处理
    public void RetreatStall(int id) {
        Stall stall = allStall.remove(id);
        if (stall == null) {
            return;
        }
        stall.setState(NO);
        List<StallBean> list = getmap(stall.getMapid());
        StallBean bean = allStallBean.remove(id);
        bean.setState(NO);
        list.remove(bean);
        SendMessage.sendMessageToMapRoles(new Long(stall.getMapid()), Agreement.getAgreement().stallstateAgreement(GsonUtil.getGsonUtil().getgson().toJson(bean)));
        AssetUpdate assetUpdate = new AssetUpdate();
        assetUpdate.setType(AssetUpdate.STALLRET);
        Commodity[] goodstables = stall.getGoodstables();
        Commodity[] pets = stall.getPets();
        for (int i = 0; i < goodstables.length; i++) {
            Commodity commodity = goodstables[i];
            if (commodity != null && commodity.getShougou() == 0) {
                Goodstable good = commodity.getGood();
                if (EquipTool.canSuper(good.getType())) {// 叠加
                    // 判断该角色是否拥有这件物品
                    List<Goodstable> sameGoodstable = AllServiceUtil.getGoodsTableService().selectGoodsByRoleIDAndGoodsID(stall.getRoleid(), good.getGoodsid());
                    if (sameGoodstable.size() != 0) {
                        // 修改使用次数
                        sameGoodstable.get(0).setUsetime(sameGoodstable.get(0).getUsetime() + good.getUsetime());
                        // 修改数据库
                        AllServiceUtil.getGoodsTableService().updateGoodRedis(sameGoodstable.get(0));
                        AllServiceUtil.getGoodsTableService().deleteGoodsByRgid(good.getRgid());
                        good = sameGoodstable.get(0);
                    } else {// 插入数据库
                        AllServiceUtil.getGoodsTableService().updateGoodsIndex(good, stall.getRoleid(), null, null);
                    }
                } else {// 不可叠加
                    AllServiceUtil.getGoodsTableService().updateGoodsIndex(good, stall.getRoleid(), null, null);
                }
                assetUpdate.setGood(good);
            }
        }
        for (int i = 0; i < pets.length; i++) {
            Commodity commodity = pets[i];
            if (commodity != null) {
                RoleSummoning pet = commodity.getPet();
                AllServiceUtil.getRoleSummoningService().updateRoleSummoningIndex(pet, stall.getRoleid());
                assetUpdate.setPet(pet);
            }
        }
        SendMessage.sendMessageByRoleName(stall.getRole(), Agreement.getAgreement().assetAgreement(GsonUtil.getGsonUtil().getgson().toJson(assetUpdate)));
    }

    public Map<Integer, Stall> getAllStall() {
        return allStall;
    }

    public void setAllStall(Map<Integer, Stall> allStall) {
        this.allStall = allStall;
    }

    /// 关闭服务器处理
    public void guanbi() {
        for (Stall stall : allStall.values()) {
            AssetUpdate assetUpdate = new AssetUpdate();
            assetUpdate.setType(AssetUpdate.STALLRET);
            Commodity[] goodstables = stall.getGoodstables();
            Commodity[] pets = stall.getPets();
            for (int i = 0; i < goodstables.length; i++) {
                Commodity commodity = goodstables[i];
                if (commodity != null) {
                    Goodstable good = commodity.getGood();
                    if (EquipTool.canSuper(good.getType())) {// 叠加
                        // 判断该角色是否拥有这件物品
                        List<Goodstable> sameGoodstable = AllServiceUtil.getGoodsTableService().selectGoodsByRoleIDAndGoodsID(stall.getRoleid(), good.getGoodsid());
                        if (sameGoodstable.size() != 0) {
                            // 修改使用次数
                            sameGoodstable.get(0).setUsetime(sameGoodstable.get(0).getUsetime() + good.getUsetime());
                            // 修改数据库
                            AllServiceUtil.getGoodsTableService().updateGoodRedis(sameGoodstable.get(0));
                            AllServiceUtil.getGoodsTableService().deleteGoodsByRgid(good.getRgid());
                            good = sameGoodstable.get(0);
                        } else {// 插入数据库
                            AllServiceUtil.getGoodsTableService().updateGoodsIndex(good, stall.getRoleid(), null, null);
                        }
                    } else {// 不可叠加
                        AllServiceUtil.getGoodsTableService().updateGoodsIndex(good, stall.getRoleid(), null, null);
                    }
                    assetUpdate.setGood(good);
                }
            }
            for (int i = 0; i < pets.length; i++) {
                Commodity commodity = pets[i];
                if (commodity != null) {
                    RoleSummoning pet = commodity.getPet();
                    AllServiceUtil.getRoleSummoningService().updateRoleSummoningIndex(pet, stall.getRoleid());
                    assetUpdate.setPet(pet);
                }
            }
        }
    }
}
