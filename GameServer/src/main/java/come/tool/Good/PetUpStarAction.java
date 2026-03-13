package come.tool.Good;

import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;

import org.come.action.IAction;
import org.come.action.monitor.MonitorUtil;
import org.come.action.suit.SuitMixdeal;
import org.come.action.summoning.SummonPetAction;
import org.come.bean.LoginResult;
import org.come.bean.XXGDBean;
import org.come.entity.Goodstable;
import org.come.entity.RoleSummoning;
import org.come.handler.SendMessage;
import org.come.model.Configure;
import org.come.protocol.Agreement;
import org.come.server.GameServer;
import org.come.tool.WriteOut;
import org.come.until.AllServiceUtil;
import org.come.until.GsonUtil;

import come.tool.Role.RoleData;
import come.tool.Role.RolePool;
import come.tool.Stall.AssetUpdate;
import io.netty.channel.ChannelHandlerContext;

import static come.tool.Good.UsePetAction.mathDouble;

/**
 * 召唤兽升星
 * @author mist
 *
 */
public class PetUpStarAction implements IAction {

    @Override
    public void action(ChannelHandlerContext ctx, String message) {
        LoginResult login = GameServer.getAllLoginRole().get(ctx);
        String[] vs = message.split("=");
        RoleSummoning pet = AllServiceUtil.getRoleSummoningService().selectRoleSummoningsByRgID(new BigDecimal(vs[1]));
        System.err.println(pet.getSummoningid());
        if(message.startsWith("SX")) {//召唤兽升星
            if (login.getGold().compareTo(new BigDecimal(5000000)) < 0) {
                SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().PromptAgreement("金币不足!!!"));
                return;
            }
            Goodstable good = AllServiceUtil.getGoodsTableService().getGoodsByRgID(new BigDecimal(vs[2]));
            SummoningAscending(pet, good, ctx, login);
        }else if(message.startsWith("FJ")) {//召唤兽升星
            // 删除召唤兽
            AllServiceUtil.getRoleSummoningService().deleteRoleSummoningBySid(new BigDecimal(vs[1]));
            int num = 0;
            if(pet.getFragment()>0) {
                num = 30+pet.getFragment();
            }else {
                num = 30;
            }
            transmitFragment(ctx, login, pet, num);

        }
    }

    /**
     * 召唤兽升星
     * @param pet
     * @param good
     * @param ctx
     * @param login
     */
    public static void SummoningAscending(RoleSummoning pet, Goodstable good, ChannelHandlerContext ctx, LoginResult login) {
        ConcurrentHashMap<Integer, Configure> s = GameServer.getAllConfigure();
        Configure configure = s.get(1);
        Configure configure1 = s.get(3);

        AssetUpdate assetUpdate = new AssetUpdate();
        assetUpdate.setType(AssetUpdate.USEGOOD);
        int lvl = 0;
        String lvlj = "";
        if(pet.getFragment() == 0) {

            useGood(good, 30);
            pet.setFragment(30);
            lvl = 30;
            lvlj = "一";
        }else if(pet.getFragment() == 30) {
            useGood(good, 60);
            pet.setFragment(90);
            lvl = 60;
            lvlj = "二";
        }else if(pet.getFragment() == 90) {
            useGood(good, 90);
            pet.setFragment(180);
            lvl = 90;
            lvlj = "三";
        }else if(pet.getFragment() == 180) {
            useGood(good, 120);
            pet.setFragment(300);
            lvl = 120;
            lvlj = "四";
        }else if(pet.getFragment() == 300) {
            useGood(good, 150);
            pet.setFragment(450);
            lvl = 150;
            lvlj = "五";
        }else if(pet.getFragment() == 450) {
            useGood(good, 180);
            pet.setFragment(630);
            lvl = 180;
            lvlj = "六";

        }else if(pet.getFragment() == 630) {

            useGood(good, 210);
            pet.setFragment(840);
            lvl = 210;
            lvlj = "七";

        }else if(pet.getFragment() == 840) {
            useGood(good, 240);
            pet.setFragment(1110);
            lvl = 240;
            lvlj = "八";

        }else if(pet.getFragment() == 1110) {
            useGood(good, 270);
            pet.setFragment(1380);
            lvl = 270;
            lvlj = "九";

        }else if(pet.getFragment() == 1380) {
            useGood(good, 300);
            pet.setFragment(1710);
            lvl = 300;  //升星+成长多少
            lvlj = "十";

        }else if(pet.getFragment() == 1710) {
            assetUpdate.setMsg("#R您的召唤兽【#G"+pet.getSummoningname()+"#R】已达到最高星级！");
            SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().assetAgreement(GsonUtil.getGsonUtil().getgson().toJson(assetUpdate)));
            return;
        }
        System.err.println(lvl);
        configure = s.get(3);
        Double d = (lvl*100.0); //升星+成长多少
        BigDecimal grow = mathDouble(Double.parseDouble(pet.getGrowlevel()),d );
        assetUpdate.updata("G" + good.getRgid() + "=" + good.getUsetime());
//        pet.setHp(Integer.parseInt((pet.getHp() + Math.round((pet.getHp()*d)))+""));
//        pet.setMp(Integer.parseInt((pet.getMp() + Math.round((pet.getMp()*d)))+""));
//        pet.setAp(Integer.parseInt((pet.getAp() + Math.round((pet.getAp()*d)))+""));
//        pet.setSp(Integer.parseInt((pet.getSp() + Math.round((pet.getSp()*d)))+""));

        pet.setHp(pet.getHp().subtract(BigDecimal.valueOf(pet.getSI2("hp"))));
        pet.setMp(pet.getMp().subtract(BigDecimal.valueOf(pet.getSI2("mp"))));
        pet.setAp(pet.getAp().subtract(BigDecimal.valueOf(pet.getSI2("ap"))));
        pet.setSp(pet.getSp().subtract(BigDecimal.valueOf(pet.getSI2("sp"))));
        pet.setGrowlevel(grow+"");

        pet.setBasishp(BigDecimal.valueOf(0));
        pet.setBasismp(BigDecimal.valueOf(0));
        AllServiceUtil.getRoleSummoningService().updatePetRedis(pet);
        assetUpdate.setPet(pet);
        assetUpdate.setMsg("#R恭喜您：您的召唤兽#G【" + pet.getSummoningname() + "】#Y升星成功，当前星级#G"+lvlj+"星#R!!!");
        AllServiceUtil.getRoleSummoningService().updatePetRedis(pet);
        assetUpdate.setPet(pet);

        assetUpdate.updata("D=-5000000");
        login.setGold(login.getGold().subtract(new BigDecimal(5000000)));
        MonitorUtil.getMoney().useD(5000000L);
        assetUpdate.setMsg("#R恭喜您，升星成功！#G花费5000000两银子！");
        SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().assetAgreement(GsonUtil.getGsonUtil().getgson().toJson(assetUpdate)));
    }

    public static void useGood(Goodstable good,int sum){
        good.goodxh(sum);//添加记录
        AllServiceUtil.getGoodsrecordService().insert(good, null, 1, 9);
        AllServiceUtil.getGoodsTableService().updateGoodRedis(good);
    }


    /**发送获得的碎片*/
    public static String transmitFragment(ChannelHandlerContext ctx,LoginResult loginResult,RoleSummoning pet,int sum) {
        String petid = pet.getSummoningid();
        String goods = 888+petid;
        String ab = ":25|"+goods+"|"+sum+"|#AAFFFO";
        XXGDBean bean=new XXGDBean();
        bean.setId(goods);
        bean.setSum(sum);
        RoleData roleData=RolePool.getRoleData(loginResult.getRole_id());
        AssetUpdate assetUpdate=new AssetUpdate(25);
        RoleSummoning petk=GameServer.getPet(new BigDecimal(petid));
        Goodstable goodstable = new Goodstable();
        goodstable.setGoodsid(new BigDecimal(goods));
        goodstable.setGoodsname("召唤兽【"+petk.getSummoningname()+"】九幽琉璃石");
        goodstable.setQuality(2L);
        goodstable.setValue("神兽="+petid);
        goodstable.setType(Long.parseLong("776"));
        goodstable.setSkin("cailiao4");
        goodstable.setInstruction("#G集齐一定数量可以给神兽升星，获得强大的战斗力！");
        AddGoodAction.addGood(assetUpdate,goodstable,loginResult,roleData,bean,assetUpdate.getType());
        assetUpdate.setMsg( "#R恭喜您：#Y分解成功！获得【#G"+goodstable.getGoodsname()+"#Y】【#G"+sum+"#Y】个！");
        SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().assetAgreement(GsonUtil.getGsonUtil().getgson().toJson(assetUpdate)));
        String msg="dh:"+ab+(loginResult!=null?loginResult.getRole_id():null);
        WriteOut.addtxt(msg,9999);
        return goodstable.getGoodsname();
    }
}
