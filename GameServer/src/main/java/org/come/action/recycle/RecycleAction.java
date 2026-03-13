package org.come.action.recycle;

import com.alibaba.fastjson.JSONObject;
import come.tool.Battle.BattleMixDeal;
import come.tool.Mixdeal.AnalysisString;
import come.tool.Role.RoleData;
import come.tool.Role.RolePool;
import come.tool.Stall.AssetUpdate;
import io.netty.channel.ChannelHandlerContext;
import org.come.action.IAction;
import org.come.action.monitor.MonitorUtil;
import org.come.action.monster.ClickMonsterAction;
import org.come.bean.Bbuy;
import org.come.bean.GiveGoodsBean;
import org.come.bean.LoginResult;
import org.come.entity.Goodstable;
import org.come.entity.Record;
import org.come.handler.SendMessage;
import org.come.model.Lshop;
import org.come.model.Robots;
import org.come.protocol.Agreement;
import org.come.server.GameServer;
import org.come.task.MapMonsterBean;
import org.come.task.MonsterUtil;
import org.come.tool.EquipTool;
import org.come.until.AllServiceUtil;
import org.come.until.GsonUtil;

import java.math.BigDecimal;
import java.util.List;

/**
 * 给于,客户端发送GiveGoodsBean,返回GoodsListResultBean
 * @author 叶豪芳
 * @date 2017年12月20日 上午11:39:43
 *
 */
public class RecycleAction implements IAction{

	@Override
	public void action(ChannelHandlerContext ctx, String message) {
		LoginResult loginResult=GameServer.getAllLoginRole().get(ctx);
		if (loginResult==null) {return;}

		List<GiveGoodsBean> giveGoodsBeans = JSONObject.parseArray(message, GiveGoodsBean.class);

		for (GiveGoodsBean giveBean : giveGoodsBeans) {

			Goodstable goodstable=getGiveGood(giveBean.getRgid(), loginResult.getRole_id(), giveBean.getSum(),false);
			if (goodstable==null) {
				SendMessage.sendMessageToSlef(ctx,Agreement.getAgreement().PromptAgreement("你给与的物品处于异常"));
				return;
			}
			Bbuy bbuy=GameServer.getBbuy(goodstable.getGoodsid());
			if (bbuy.getPrice1() > 0) {//金钱回收
				if (bbuy==null||bbuy.getPrice1()==0) {return;}
				int num=giveBean.getSum();
				if (giveBean.getSum()>25) {giveBean.setSum(25);}
				giveBean.setSum(bbuy.addNum(giveBean.getSum()));
				if (giveBean.getSum()<=0||goodstable.getUsetime()<giveBean.getSum()) {
					SendMessage.sendMessageToSlef(ctx,Agreement.getAgreement().PromptAgreement("今天回收的材料已经够用了,明天再来吧"));
					return;
				}
				goodstable.setUsetime(goodstable.getUsetime()-giveBean.getSum());
				AllServiceUtil.getGoodsTableService().updateGoodRedis(goodstable);
				AssetUpdate assetUpdate=new AssetUpdate();
				assetUpdate.setType(AssetUpdate.USEGOOD);
				long money=(bbuy.getPrice1()*giveBean.getSum());
				assetUpdate.updata("D="+money);
				assetUpdate.updata("G"+goodstable.getRgid()+"="+goodstable.getUsetime());
				if (num>25) {assetUpdate.setMsg("收购获得"+money+"银两|单次收购最大数25个");}
				else{assetUpdate.setMsg("收购获得"+money+"银两");}
				loginResult.setGold(loginResult.getGold().add(new BigDecimal(money)));
				MonitorUtil.getMoney().addD(money, 3);
				SendMessage.sendMessageToSlef(ctx,Agreement.getAgreement().assetAgreement(GsonUtil.getGsonUtil().getgson().toJson(assetUpdate)));
			}

			if (bbuy.getPrice2() > 0 ) {//绑玉回收
				if (bbuy==null||bbuy.getPrice2()==0) {return;}

				goodstable.goodxh(1);
				AllServiceUtil.getGoodsTableService().updateGoodRedis(goodstable);
				AssetUpdate assetUpdate=new AssetUpdate();
				assetUpdate.setType(AssetUpdate.USEGOOD);
				long money=(bbuy.getPrice2());
				assetUpdate.updata("S="+money);
				assetUpdate.updata("G"+ goodstable.getRgid()+"="+goodstable.getUsetime());


				assetUpdate.setMsg("收购获得"+money+"绑玉");

				loginResult.setSavegold(loginResult.getSavegold().add(new BigDecimal(money)));
				SendMessage.sendMessageToSlef(ctx,Agreement.getAgreement().assetAgreement(GsonUtil.getGsonUtil().getgson().toJson(assetUpdate)));
			}else if (giveBean.getType()==3) {//限时回收
				MapMonsterBean bean=MonsterUtil.getMonster(giveBean.getOtherID().intValue());
				if (bean==null||bean.getRobotType()!=3) {SendMessage.sendMessageToSlef(ctx,ClickMonsterAction.CHECKTS2);return;}
				Lshop lshop=bean.getShops().get(goodstable.getGoodsid().toString());
				if (lshop==null) {
					SendMessage.sendMessageToSlef(ctx,Agreement.getAgreement().PromptAgreement("不属于回收范围"));
					return;
				}
				Robots robots=GameServer.getAllRobot().get(bean.getRobotid()+"");
				if (robots==null) {return;}
				if (robots.getLvls()!=null) {
					String value=BattleMixDeal.isLvl(loginResult.getGrade(), robots.getLvls());
					if (value!=null) {SendMessage.sendMessageToSlef(ctx,value);return;}
				}
				String v=ClickMonsterAction.isTime20s(loginResult.getRole_id());
				if (v!=null) {SendMessage.sendMessageToSlef(ctx,v);return;}
				AssetUpdate assetUpdate=new AssetUpdate();
				assetUpdate.setType(AssetUpdate.USEGOOD);
				String msg=null;
				if (giveBean.getSum()>lshop.getlNum()) {
					msg="单次最大购买数量"+lshop.getlNum();
					giveBean.setSum(lshop.getlNum());
				}
				giveBean.setSum(lshop.addNum(giveBean.getSum()));
				if (giveBean.getSum()==0) {
					SendMessage.sendMessageToSlef(ctx,Agreement.getAgreement().PromptAgreement("回收的材料已经够用了"));
					return;
				}
				goodstable.setUsetime(goodstable.getUsetime()-giveBean.getSum());
				AllServiceUtil.getGoodsTableService().updateGoodRedis(goodstable);
				assetUpdate.updata("G"+goodstable.getRgid()+"="+goodstable.getUsetime());
				if (lshop.getType()==0) {
					long money=(lshop.getMoney().longValue()*giveBean.getSum());
					loginResult.setGold(loginResult.getGold().add(new BigDecimal(money)));
					assetUpdate.updata("D="+money);
					MonitorUtil.getMoney().addD(money, 3);
					if (msg!=null) {msg=msg+"|收购获得"+money+"银两";}
					else{msg="收购获得"+money+"银两";}
				}else if (lshop.getType()==1) {
					long money=(lshop.getMoney().longValue()*giveBean.getSum());
					loginResult.setCodecard(loginResult.getCodecard().add(new BigDecimal(money)));
					MonitorUtil.getMoney().addX(money, 2);
					assetUpdate.updata("X="+money);
					if (msg!=null) {msg=msg+"|收购获得"+money+"仙玉";}
					else{msg="收购获得"+money+"仙玉";}
				}
				assetUpdate.setMsg(msg);
				SendMessage.sendMessageToSlef(ctx,Agreement.getAgreement().assetAgreement(GsonUtil.getGsonUtil().getgson().toJson(assetUpdate)));
			}
			AllServiceUtil.getGoodsrecordService().insert(goodstable, null, giveBean.getSum(), 2);//添加记录

		}


	}

	/**给与物品判断*/
	public Goodstable getGiveGood(BigDecimal rgid,BigDecimal roleid,int sum,boolean isJY){
		if (sum<0) {
			AllServiceUtil.getRecordService().insert(new Record(5,"给与异常:id:"+rgid+"_角色:"+roleid+"_数量:"+sum));
			return null;
		}
		Goodstable goodstable=AllServiceUtil.getGoodsTableService().getGoodsByRgID(rgid);
		if (goodstable==null) {
			AllServiceUtil.getRecordService().insert(new Record(5,"给与异常:id:"+rgid+"_角色:"+roleid+"_数量:"+sum));
			return null;
		}
		if (goodstable.getRole_id().compareTo(roleid)!=0||goodstable.getUsetime()<sum) {
			StringBuffer buffer=new StringBuffer();
			buffer.append("给与异常:id:"+rgid+"_角色:"+roleid+"_数量:"+sum);
			buffer.append("_物品属性:");
			buffer.append(GsonUtil.getGsonUtil().getgson().toJson(goodstable));
			AllServiceUtil.getRecordService().insert(new Record(5,buffer.toString()));
			return null;
		}
		if (isJY&&AnalysisString.jiaoyi(goodstable.getQuality())) {
			StringBuffer buffer=new StringBuffer();
			buffer.append("给与异常物品绑定:id:"+rgid+"_角色:"+roleid+"_数量:"+sum);
			buffer.append("_物品属性:");
			buffer.append(GsonUtil.getGsonUtil().getgson().toJson(goodstable));
			AllServiceUtil.getRecordService().insert(new Record(5,buffer.toString()));
			return null;
		}
		return goodstable;
	}
}
