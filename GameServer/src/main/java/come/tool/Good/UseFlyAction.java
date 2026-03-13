//package come.tool.Good;
//
//import come.tool.Stall.AssetUpdate;
//import io.netty.channel.ChannelHandlerContext;
//import org.come.action.IAction;
//import org.come.bean.FlyResult;
//import org.come.bean.LoginResult;
//import org.come.entity.Fly;
//import org.come.entity.Goodstable;
//import org.come.handler.SendMessage;
//import org.come.protocol.Agreement;
//import org.come.server.GameServer;
//import org.come.until.AllServiceUtil;
//import org.come.until.GsonUtil;
//
//import java.math.BigDecimal;
//
//public class UseFlyAction implements IAction {
//    public void action(ChannelHandlerContext ctx, String message){
//
//
//        LoginResult loginResult = GameServer.getAllLoginRole().get(ctx);
//        if (loginResult==null) {return;}
//        String[] vs=message.split("\\|");
//
//        Fly fly=AllServiceUtil.getFlyService().selectFlysByMID(new BigDecimal(vs[1]));
//        if (fly==null) {return;}
//        if (fly.getRoleid().compareTo(loginResult.getRole_id())!=0) {return;}
//        Goodstable good=AllServiceUtil.getGoodsTableService().getGoodsByRgID(new BigDecimal(vs[0]));
//        if (good==null) {return;}
//        if (good.getRole_id().compareTo(loginResult.getRole_id())!=0) {return;}
//        if (good.getUsetime()<=0) {return;}
//        long type=good.getType();
//         if (type == 1101){// 经验
//            addFlyExp(fly, good, ctx, loginResult);
//        } else if (type == 1102){// 进阶
//            addFlystate(fly, good, ctx, loginResult);
//        }
//
//    }
//    public void addFlyExp(Fly fly,Goodstable good,ChannelHandlerContext ctx,LoginResult login){
//        //判断坐骑的等级是否达到100级
//        int lvl=fly.getFlylvl();
//        if(lvl == 100){//达到最高等级
//            SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().PromptAgreement("飞行器 "+fly.getFlyname()+" 已达最高等级100级！！"));
//            return;
//        }
//        AssetUpdate assetUpdate=new AssetUpdate();
//        assetUpdate.setType(AssetUpdate.USEGOOD);
//        UsePetAction.useGood(good, 1);
//        assetUpdate.updata("G"+good.getRgid()+"="+good.getUsetime());
//        int addexp = Integer.parseInt(good.getValue().split("=")[1]);//经验值
//        ExpUtil.FlyExp(fly, addexp);//进行升级判断
//        AllServiceUtil.getFlyService().updateFlyRedis(fly);
//        assetUpdate.setFly(fly);
//        SendMessage.sendMessageToSlef(ctx,Agreement.getAgreement().assetAgreement(GsonUtil.getGsonUtil().getgson().toJson(assetUpdate)));
//    }
//    private  void addFlystate(Fly fly,Goodstable good,ChannelHandlerContext ctx,LoginResult login){
//        int lvl=fly.getFlylvl();
//        int stat=fly.getFlystate();
//        if (stat>=5){
//
//            SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().PromptAgreement("飞行器已经是最高阶"));
//            return;
//
//        }
//
//        if (lvl!=100)
//        {
//            SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().PromptAgreement("飞行器等级不足"));
//            return;
//        }
//
//        AssetUpdate assetUpdate=new AssetUpdate();
//        assetUpdate.setType(AssetUpdate.USEGOOD);
//        UsePetAction.useGood(good, 1);
//        assetUpdate.updata("G"+good.getRgid()+"="+good.getUsetime());
//        fly.setFlylvl(0);
//        fly.setFlystate(fly.getFlystate()+1);
//        fly.setSkin(fly.getSkin()+1);
//        fly.setExp(0);
//        String name=ChangeFlyName(fly.getFlyname());
//        fly.setFlyname(name);
//        AllServiceUtil.getFlyService().updateFlyRedis(fly);
//        assetUpdate.setFly(fly);
//        SendMessage.sendMessageToSlef(ctx,Agreement.getAgreement().assetAgreement(GsonUtil.getGsonUtil().getgson().toJson(assetUpdate)));
//
//
//
//    }
//    public String ChangeFlyName(String name){
//
//        String[] vs={"香叶扇",
//                "寒露扇",
//                "雕龙玉扇",
//                "春秋乾坤扇",
//                "火凰焚天扇",
//                "富贵锦",
//                "无妄锦",
//                "玲珑素锦",
//                "遮霞闭月锦",
//                "叠彩狮王锦",
//                "奔云燕",
//                "冰霓蝶",
//                "妙音彩鱼",
//                "紫蜈追梦筝",
//                "飞龙流珠席",
//                "净心荷",
//                "定魂莲",
//                "画水尘莲",
//                "碧影琉璃台",
//                "金玉宝莲台",
//                "轻鸿羽",
//                "藏虹羽",
//                "百灵风羽",
//                "青澜牵星羽",
//                "流冥翠羽",
//                "筋斗云",
//                "旋霜云",
//                "万象星云",
//                "电闪雷鸣云",
//                "五色祥云"
//
//        };
//if (name.equals("火凰焚天扇")||name.equals("叠彩狮王锦")||name.equals("飞龙流珠席")||name.equals("金玉宝莲台")||name.equals("流冥翠羽")||name.equals("五色祥云"))
//{
//    return name;
//}
//  for (int i=0;i<= vs.length;i++){
//      if (name.equals(vs[i]))
//          return vs[i+1];
//  }
//
//     return name;
//
//    }
//}
