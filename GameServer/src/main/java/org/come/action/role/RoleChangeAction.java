package org.come.action.role;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import come.tool.Battle.BattleMixDeal;
import come.tool.Calculation.BaseQl;
import come.tool.Calculation.BaseValue;
import come.tool.Calculation.RoleReborn;
import come.tool.Good.ExpUtil;
import come.tool.Mixdeal.AnalysisString;
import come.tool.Role.PrivateData;
import come.tool.Role.RoleData;
import come.tool.Role.RolePool;
import come.tool.Stall.AssetUpdate;
import io.netty.channel.ChannelHandlerContext;
import org.come.action.IAction;
import org.come.action.monitor.MonitorUtil;
import org.come.action.sys.enterGameAction;
import org.come.bean.LoginResult;
import org.come.bean.Meridians;
import org.come.entity.RoleSummoning;
import org.come.handler.SendMessage;
import org.come.model.Alchemy;
import org.come.protocol.Agreement;
import org.come.readBean.AllMeridians;
import org.come.server.GameServer;
import org.come.until.AllServiceUtil;
import org.come.until.GsonUtil;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import io.netty.util.internal.StringUtil;
import java.text.DecimalFormat;
import come.tool.Calculation.BaseXingpans;
import org.come.entity.Goodstable;
import org.apache.commons.lang.math.NumberUtils;
import org.come.action.reward.DrawnitemsAction;

import static org.come.action.reward.DrawnitemsAction.Splice;
import static org.come.action.sys.enterGameAction.forceExiteGame;


/**
 * 角色改变属性
 * @author 叶豪芳
 * @date 2018年1月5日 下午3:31:05
 *
 */
public class RoleChangeAction implements IAction {

	@Override
	public void action(ChannelHandlerContext ctx, String message) {
		LoginResult loginResult = GameServer.getAllLoginRole().get(ctx);
		//D确定加点根骨=灵性=力量=敏捷=定力|
		//H血量蓝量改变|
		//M坐骑乘骑等级=mid|
		//P召唤兽参战休息|
		//B孩子参战休息|
		//T天演策点数兑换|
		//1设置密码/修改密码|
		//2更改帮派抗性|
		//3修为点转换
		//4修为点升级
		//5属性点兑换
		//6小成/大成修炼
		//7小成/大成修炼分配
		//经脉

		if (message.startsWith("D")) {//确定加点
			DDJD(loginResult, message);
		}else if (message.startsWith("H")) {//血量蓝量改变
			String[] vs=message.split("=");
			if (vs.length==2) {
				loginResult.setHp(new BigDecimal(vs[0].substring(1)));
				loginResult.setMp(new BigDecimal(vs[1]));
				// 修复：同步到 Redis 缓存，防止切换角色时 HP/MP 回退到旧值
				come.tool.Role.RoleData roleData = come.tool.Role.RolePool.getRoleData(loginResult.getRole_id());
				if (roleData != null) {
					// 先将数据存入 Redis，然后标记为修改
					org.come.redis.RedisControl.insertKeyT(org.come.redis.RedisParameterUtil.COPY_LOGIN, 
						loginResult.getRole_id().toString(), loginResult);
					org.come.redis.RedisControl.insertController(org.come.redis.RedisParameterUtil.COPY_LOGIN, 
						loginResult.getRole_id().toString(), org.come.redis.RedisControl.CALTER);
				}
			}
		}else if (message.startsWith("zy")) {//确定加点
			String[] s = message.split("=");
			BigDecimal zy = loginResult.getScoretype("支援");
			if (zy.intValue() == 0)
				loginResult.setScore(Splice(loginResult.getScore(), "支援=" + s[1], 2));
			loginResult.setScore(Splice(loginResult.getScore(), "支援=" + s[1], 1));
			zy = loginResult.getScoretype("支援");
			AssetUpdate assetUpdate = new AssetUpdate();
			assetUpdate.setMsg("召唤兽支援已" + (loginResult.getScoretype("支援").intValue() == 1 ? "开启" : "关闭"));
			assetUpdate.updata("支援=" + zy);
			SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().assetAgreement(GsonUtil.getGsonUtil().getgson().toJson(assetUpdate)));
		}  else if (message.startsWith("XZ")) {//修正卡
			message = message.substring(3);
			String[] v = message.trim().split("-");
			if (v.length > loginResult.getTurnAround() || v.length > 3) {
				SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().PromptAgreement("只能输入于自身相同的转生次数"));
				return;
			}
			int[] vv = getbz(v);
			if (vv == null) {
				SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().PromptAgreement("输入格式有误"));
				return;
			}
			// 开始进入转生
			String yuben = null;
			RoleData roleData = RolePool.getRoleData(loginResult.getRole_id());
			for (int i = 0; i < vv.length; i++) {
				yuben = RoleReborn.reborn(RoleSkill.getRoleSkill().getAllSkill(vv[i], i * 5000 + 10000), yuben);
			}
			PrivateData privateData = roleData.getPrivateData();
			privateData.setBorn(yuben);
			roleData.upPrivateData(privateData);
			roleData.setBorns(BaseValue.reborn(roleData.getPrivateData().getBorn()));
			SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().PromptAgreement("你的修正已更换"));
			return;
		} else if (message.startsWith("sf")) {//确定加点
			String[] s = message.split("=");
			BigDecimal zy = loginResult.getScoretype("首发");
			if (zy.intValue() == 0)
				loginResult.setScore(Splice(loginResult.getScore(), "首发=" + s[1], 2));
			loginResult.setScore(Splice(loginResult.getScore(), "首发=" + s[1], 1));
			zy = loginResult.getScoretype("首发");
			AssetUpdate assetUpdate = new AssetUpdate();
			assetUpdate.setMsg("首发召唤兽支援已" + (loginResult.getScoretype("首发").intValue() == 1 ? "开启" : "关闭"));
			assetUpdate.updata("首发=" + zy);
			SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().assetAgreement(GsonUtil.getGsonUtil().getgson().toJson(assetUpdate)));
//			String[] vs = message.split("=");
//			if (vs.length == 2 && loginResult != null) {
//				loginResult.setHp(new BigDecimal(vs[0].substring(1)));
//				loginResult.setMp(new BigDecimal(vs[1]));
//			}
		}else if (message.startsWith("F")){
			if (message.length()!=1){
				String[] vs=message.split("=");
//				loginResult.setFly_id(Integer.parseInt(vs[0].substring(1)));
				RolePool.getRoleData(loginResult.getRole_id()).setMid(new BigDecimal(vs[1]));
				loginResult.setFlyskin(vs[2]);
			}else {
				loginResult.setFly_id(null);
				RolePool.getRoleData(loginResult.getRole_id()).setMid(null);
				loginResult.setFlyskin(null);
			}
			SendMessage.sendMessageToMapRoles(loginResult.getMapid(),Agreement.getAgreement().upRoleShowAgreement(GsonUtil.getGsonUtil().getgson().toJson(loginResult.getRoleShow())));
		}

		else if (message.startsWith("M")) {//坐骑乘骑
			if (message.length()!=1) {
				String[] vs=message.split("=");
				loginResult.setMount_id(Integer.parseInt(vs[0].substring(1)));
				RolePool.getRoleData(loginResult.getRole_id()).setMid(new BigDecimal(vs[1]));
			}else {
				loginResult.setMount_id(null);
				RolePool.getRoleData(loginResult.getRole_id()).setMid(null);
			}
			SendMessage.sendMessageToMapRoles(loginResult.getMapid(),Agreement.getAgreement().upRoleShowAgreement(GsonUtil.getGsonUtil().getgson().toJson(loginResult.getRoleShow())));
		}else if (message.startsWith("P")) {//召唤兽参战休息
			if (message.length()!=1) {
				loginResult.setSummoning_id(new BigDecimal(message.substring(1)));
			}else {
				loginResult.setSummoning_id(null);
			}
			//宠物跟随
//			loginResult.setSummoning_id(new BigDecimal(message.substring(1)));
//			List<RoleSummoning> roleSummonings = AllServiceUtil.getRoleSummoningService().selectRoleSummoningsByRoleID(loginResult.getSummoning_id());
//			loginResult.setShowRoleSummoningList(roleSummonings);
		}else if (message.startsWith("B")) {//孩子参战休息
			if (message.length()!=1) {
				loginResult.setBabyId(new BigDecimal(message.substring(1)));
			}else {
				loginResult.setBabyId(null);
			}
		}else if (message.startsWith("T")) {//T天演策点数兑换
			TYCDH(loginResult,message);
		}else if (message.startsWith("1")) {//设置密码/修改密码
			loginResult.setPassword(message.substring(1));
		}else if (message.startsWith("2")) {//更改帮派抗性
			loginResult.setResistance(message.substring(1));
			GangKX(loginResult, message);
			String resistance = loginResult.getResistance();
			String[] v = resistance.split("\\|");
			for (String s1 : v) {
				String[] v1 = s1.split("#");
				for (String string : v1) {
					String[] split = string.split("=");
					if (split.length == 2) {
						double d = Double.parseDouble(split[1]);
						if (d > 40) {
							SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().PromptAgreement("检测到你的帮派抗性异常，30秒后将封号！！！"));
							loginResult.setResistance("主-|副-");
							enterGameAction.forceExiteGame(loginResult);
							return;
						}
					}
				}
			}
		}else if (message.startsWith("3")) {//3修为点转换
			XWDZH(loginResult, message);
		}else if (message.startsWith("4")) {//4修为点升级
			XWDSJ(loginResult, message);
		}else if (message.startsWith("5")) {//5属性点兑换
			SXDDH(loginResult, message);
		}else if (message.startsWith("6")) {//6小成/大成修炼
			GangXL(loginResult, message);
		}else if (message.startsWith("7")) {//7小成/大成修炼分配
			GangFP(loginResult, message);
		}else if (message.startsWith("X")){ //经脉修炼
			meridians(ctx, loginResult, message);
		}else if (message.startsWith("Z")) {//星盘
			XP(loginResult, message);
		}
	}
	/**
	 * TODO 帮派抗性计算加载
	 * @param loginResult
	 * @param message
	 */
	public void GangKX(LoginResult loginResult,String message){
		String v = message.substring(1);
		if (v == null || v.length() == 0) {
			return;
		}
		String[] kx = v.split("\\|");
		if (kx.length < 2) {
			return;
		}
		if (!kx[0].startsWith("主-") || !kx[1].startsWith("副-")) {
			return;
		}
		int count = 0;
		String zhu = kx[0].substring(2);
		if (zhu.length() > 0) {
			count += 1;
		}
		String fu = kx[1].substring(2);
		if (fu.length() > 0) {
			count += 2;
		}
		if (count == 0) {
			return;
		}
		RoleData data=RolePool.getRoleData(loginResult.getRole_id());
		if (data==null) {return;}

		//获取捐献帮派的金额
		BigDecimal achi = loginResult.getAchievement();
		//计算是否选择了主守护和副守护
		BaseQl[] xls=new BaseQl[count > 2 ? 2 : 1];
		if (count == 1 || count == 3) {
			xls[0] = new BaseQl(zhu, BigDecimal.valueOf(BaseValue.getBangQuality(achi, true)));
			if (count == 3) {
				xls[1] = new BaseQl(fu, BigDecimal.valueOf(BaseValue.getBangQuality(achi, false)));
			}
		} else {
			xls[0] = new BaseQl(fu, BigDecimal.valueOf(BaseValue.getBangQuality(achi, false)));
		}

		data.setXls(40, xls);
		AssetUpdate assetUpdate=new AssetUpdate(AssetUpdate.USEGOOD);
		assetUpdate.setResistance(v);
//		assetUpdate.setData( );
		String msg=Agreement.getAgreement().assetAgreement(GsonUtil.getGsonUtil().getgson().toJson(assetUpdate));
		SendMessage.sendMessageByRoleName(loginResult.getRolename(),msg);
	}

	/**小成/大成修炼分配*/
	public void GangFP(LoginResult loginResult,String message){
		String V=message.substring(1,2);
		int type=V.equals("X")?1:V.equals("D")?2:0;
		if (type==0) {
			return;
		}
		RoleData data=RolePool.getRoleData(loginResult.getRole_id());
		if (data==null) {return;}
		BaseQl[] qls=data.getXls(type);
		if (message.length()==2) {//洗点
			if (qls==null) {
				SendMessage.sendMessageByRoleName(loginResult.getRolename(),Agreement.getAgreement().PromptAgreement("你还没加点怎么洗点"));
				return;
			}
			long money=500000;//金钱
			if (money>loginResult.getGold().longValue()) {
				SendMessage.sendMessageByRoleName(loginResult.getRolename(),Agreement.getAgreement().PromptAgreement("金钱不足"+money));
				return;
			}
			data.setXls(type, null);
			AssetUpdate assetUpdate=new AssetUpdate(AssetUpdate.USEGOOD);
			loginResult.setGold(new BigDecimal(loginResult.getGold().longValue()-money));
			assetUpdate.updata("D=-"+money);
			assetUpdate.setResistance(loginResult.setResistance(type, null));
			String msg=Agreement.getAgreement().assetAgreement(GsonUtil.getGsonUtil().getgson().toJson(assetUpdate));
			SendMessage.sendMessageByRoleName(loginResult.getRolename(),msg);
			return;
		}
		int max=loginResult.getExtraPointInt(V);
		String kx=message.substring(2);
		BaseQl[] xls=BaseValue.isXls(qls,kx.split("#"), max,type);
		if (xls==null) {
			SendMessage.sendMessageByRoleName(loginResult.getRolename(),Agreement.getAgreement().PromptAgreement("分配方案异常"));
			return;
		}
		data.setXls(type, xls);
		AssetUpdate assetUpdate=new AssetUpdate(AssetUpdate.USEGOOD);
		assetUpdate.setResistance(loginResult.setResistance(type, kx));
		String msg=Agreement.getAgreement().assetAgreement(GsonUtil.getGsonUtil().getgson().toJson(assetUpdate));
		SendMessage.sendMessageByRoleName(loginResult.getRolename(),msg);
	}
	/**小成/大成修炼*/
	public void GangXL(LoginResult loginResult,String message){
		String V=message.substring(1,2);
		int type=V.equals("X")?1:V.equals("D")?2:0;
		if (type==0) {
			return;
		}
		int E=loginResult.getExtraPointInt(V)+1;
		if (E>(type==1?30:60)) {
			SendMessage.sendMessageByRoleName(loginResult.getRolename(),Agreement.getAgreement().PromptAgreement("修炼等级达到上限"));
			return;
		}
		if (type==2) {
			if (loginResult.getExtraPointInt("X")<30) {
				SendMessage.sendMessageByRoleName(loginResult.getRolename(),Agreement.getAgreement().PromptAgreement("小成修炼完才能进行大成修炼"));
				return;
			}
			if (loginResult.getGrade()<419) {
				SendMessage.sendMessageByRoleName(loginResult.getRolename(),Agreement.getAgreement().PromptAgreement("3转140级才能开始进行大成修炼"));
				return;
			}
		}
		long money=type==1?2000000:5000000;//金钱
		long exp=type==1?2000000:5000000;//经验
		long contribution=type==1?300:1500;//帮贡
		if (money>loginResult.getGold().longValue()) {
			SendMessage.sendMessageByRoleName(loginResult.getRolename(),Agreement.getAgreement().PromptAgreement("金钱不足"+money));
			return;
		}
		if (exp>loginResult.getExperience().longValue()) {
			SendMessage.sendMessageByRoleName(loginResult.getRolename(),Agreement.getAgreement().PromptAgreement("经验不足"+exp));
			return;
		}
		if (contribution>loginResult.getContribution().longValue()) {
			SendMessage.sendMessageByRoleName(loginResult.getRolename(),Agreement.getAgreement().PromptAgreement("金钱不足"+contribution));
			return;
		}
		AssetUpdate assetUpdate=new AssetUpdate(AssetUpdate.USEGOOD);
		assetUpdate.setMsg("你的修炼等级提升一级");
		loginResult.setGold(new BigDecimal(loginResult.getGold().longValue()-money));
		assetUpdate.updata("D=-"+money);
		loginResult.setExperience(new BigDecimal(loginResult.getExperience().longValue()-exp));
		assetUpdate.updata("R"+loginResult.getGrade()+"="+loginResult.getExperience());
		loginResult.setContribution(new BigDecimal(loginResult.getContribution().longValue()-contribution));
		assetUpdate.updata("B=-"+contribution);
		loginResult.setExtraPoint(V, 1);
		assetUpdate.updata("E"+V+"="+E);
		String msg=Agreement.getAgreement().assetAgreement(GsonUtil.getGsonUtil().getgson().toJson(assetUpdate));
		SendMessage.sendMessageByRoleName(loginResult.getRolename(),msg);
	}
	/**属性点兑换*/
	public void SXDDH(LoginResult loginResult,String message){
		int E = loginResult.getExtraPointInt("F") + 1;
		if (E > loginResult.getXiuwei()) {
			SendMessage.sendMessageByRoleName(loginResult.getRolename(),Agreement.getAgreement().PromptAgreement("修为点不够兑换"));
			return;
		}
		if (E >= 41) {
			SendMessage.sendMessageByRoleName(loginResult.getRolename(),Agreement.getAgreement().PromptAgreement("属性点兑换达到上限"));
			return;
		}
		loginResult.setXiuwei(loginResult.getXiuwei() - E);
		loginResult.setExtraPoint("F", 1);
	}
	/**修为点升级*/
	public void XWDSJ(LoginResult loginResult,String message){
		int lvl = BattleMixDeal.lvlint(loginResult.getGrade());
		if (lvl >= 200) {
			SendMessage.sendMessageByRoleName(loginResult.getRolename(),Agreement.getAgreement().PromptAgreement("已达到等级上限"));
			return;
		}
		int sxXW = xiuwei(lvl);
		if (sxXW > loginResult.getXiuwei()) {
			SendMessage.sendMessageByRoleName(loginResult.getRolename(),Agreement.getAgreement().PromptAgreement("不够修为点升级"));
			return;
		}
		loginResult.setXiuwei(loginResult.getXiuwei() - sxXW);
		ExpUtil.increasePointAndValue(loginResult);
	}
	/**修为点转换*/
	public void XWDZH(LoginResult loginResult,String message){
		long exp = loginResult.getExperience().longValue();
		if (exp < 1000000000) {
			SendMessage.sendMessageByRoleName(loginResult.getRolename(),Agreement.getAgreement().PromptAgreement("当前经验不足10E"));
			return;
		}
		int up = xwUP(BattleMixDeal.lvlint(loginResult.getGrade()));
		if (loginResult.getXiuwei() >= up) {
			SendMessage.sendMessageByRoleName(loginResult.getRolename(),Agreement.getAgreement().PromptAgreement("修为点以达到上限,无法继续转换"));
			return;
		}
		exp-=1000000000;
		loginResult.setExperience(new BigDecimal(exp));
		loginResult.setXiuwei(loginResult.getXiuwei()+1);
	}
	/**天演策点数兑换*/
	public void TYCDH(LoginResult loginResult,String message){
		int num=Integer.parseInt(message.substring(1));
		if (num<=0) {
			return;
		}
		long gold=loginResult.getGold().longValue();
		long exp=loginResult.getExperience().longValue();
		gold-=num*750000L;
		exp -=num*5000000L;
		if (gold<0) {
			SendMessage.sendMessageByRoleName(loginResult.getRolename(),Agreement.getAgreement().PromptAgreement("金钱不足"));
			return;
		}
		if (exp<0) {
			SendMessage.sendMessageByRoleName(loginResult.getRolename(),Agreement.getAgreement().PromptAgreement("经验不足"));
			return;
		}
		loginResult.setGold(new BigDecimal(gold));
		loginResult.setExperience(new BigDecimal(exp));
		loginResult.setExtraPoint("T",num);
		MonitorUtil.getMoney().useD(num*750000L);
	}
	/**人物确定加点*/
	public void DDJD(LoginResult loginResult,String message){
		String[] vs=message.split("=");
		if (vs.length!=5) {
			return;
		}
		int lvl=BattleMixDeal.lvlint(loginResult.getGrade());
		int trun=BattleMixDeal.lvltrue(loginResult.getGrade());
		int gg=Integer.parseInt(vs[0].substring(1));
		int lx=Integer.parseInt(vs[1]);
		int ll=Integer.parseInt(vs[2]);
		int mj=Integer.parseInt(vs[3]);
		int dl=Integer.parseInt(vs[4]);
		if (lvl>gg||lvl>lx||lvl>ll||lvl>mj||(trun==4&&lvl>dl)) {
			System.out.println("修改点数异常:"+message);
			return;
		}
		int canpoint=lvl*8;
		if (trun<4) {canpoint+=trun*60;}
		else {canpoint+=180+lvl;}
		canpoint+=loginResult.getExtraPointInt() +loginResult.getExtPoint().longValue();
		canpoint-=gg;
		canpoint-=lx;
		canpoint-=ll;
		canpoint-=mj;
		canpoint-=dl;
		if (canpoint<0) {
			System.out.println("修改点数异常:"+message+":"+canpoint);
			return;
		}
		loginResult.setBone(gg);
		loginResult.setSpir(lx);
		loginResult.setPower(ll);
		loginResult.setSpeed(mj);
		loginResult.setCalm(dl);
	}
	/**获取升级需要的修为点*/
	public static int xiuwei(int lvl){
		if (lvl<150) {return 6;}
		else {return lvl-144;}
	}
	/**获取修为点上限*/
	public static int xwUP(int lvl){
		return xiuwei(lvl)*5;
	}


	void meridians(ChannelHandlerContext ctx, LoginResult loginResult, String message) {
		if (message.length() < 3) {
			return;
		}
		String act = message.substring(1, 2);
		if (StrUtil.isBlankIfStr(act)) {
			return;
		}
		String str = loginResult.getMeridians();
		List<come.tool.Calculation.BaseMeridians> list = come.tool.Calculation.BaseMeridians.createBaseMeridiansList(str);

		if (act.equals("1")) {
			int id = Convert.toInt(message.substring(2));
			Meridians mer = GameServer.getAllMeridians().getFirstByType(id);
			// 开通，查看，点击
			int lv = AnalysisString.lvldirection(mer.getLevel());
			if (lv > loginResult.getGrade()) {
				// 等级不足
				SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().PromptAgreement(mer.getLevel() + "级开通"));
				return;
			} else {
				// 是否开通
				boolean isContains = come.tool.Calculation.BaseMeridians.isContains(list, id);
				if (isContains) {
					// 已经开通
				} else {
					// 进行开通
					if (list.size()+1 != id) {
						SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().PromptAgreement("需要按照顺序开通"));
						return;
					}
					mer = GameServer.getAllMeridians().getRandomByType(id);
					come.tool.Calculation.BaseMeridians bm = AllMeridians.createRandomBaseMeridians(mer);
					list.add(bm);
					saveAndSendMeridians(ctx, loginResult, list, bm);
//					String newstr = BaseMeridians.createBaseMeridiansString(list);
//					loginResult.setMeridians(newstr);
//					AllServiceUtil.getMeridiansService().saveMeridians(loginResult.getRole_id().longValue(), newstr);
//					String msg = Agreement.getAgreement().rolechangeAgreement(bm.toString());
//					SendMessage.sendMessageToSlef(ctx, msg);
				}
			}
		} else if (act.equals("2")) {
			// 重洗
			String reset = message.substring(2);
			String[] ss = reset.split("_");
			int id = Convert.toInt(ss[0]);
			int lockAttr = Convert.toInt(ss[1]);
			int lockQuality = Convert.toInt(ss[2]);
			come.tool.Calculation.BaseMeridians bm = come.tool.Calculation.BaseMeridians.getBaseMeridians(list, id);
			Meridians mer = GameServer.getAllMeridians().getByQuality(id, bm.getQuality());// (id);
			if (loginResult.getGold().longValue() < mer.getResetGold()) {
				SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().PromptAgreement("金币不足!!!"));
				return;
			}
			int sum = 0;
			if (lockAttr == 1) {
				sum += 10000000;
			}
			if (lockQuality == 1) {
				sum += 10000000;
			}
			if (sum > 0) {
				if (loginResult.getGold().longValue() < sum) {
					SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().PromptAgreement("金币不足"));
					return;
				}
			}
			loginResult.setGold(loginResult.getGold().subtract(new BigDecimal(mer.getResetGold()+sum)));
			MonitorUtil.getMoney().useD(mer.getResetGold()+sum);
//			loginResult.setCodecard(loginResult.getCodecard().subtract(new BigDecimal(sum)));
//			loginResult.setGold(loginResult.getGold().subtract(new BigDecimal(sum)));
//			MonitorUtil.getMoney().useX(sum);
			//发送货币消耗
			AssetUpdate assetUpdate = new AssetUpdate();
			assetUpdate.setData("D=" + (-mer.getResetGold()));
			SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().assetAgreement(GsonUtil.getGsonUtil().getgson().toJson(assetUpdate)));
			if (sum > 0) {
				assetUpdate = new AssetUpdate();
//				assetUpdate.setData("X=" + (-sum));
				assetUpdate.setData("D=" + (-sum));
				SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().assetAgreement(GsonUtil.getGsonUtil().getgson().toJson(assetUpdate)));
			}

//			bm = AllMeridians.resetBaseMeridians(bm, lockAttr, lockQuality);
			bm.reset(lockAttr, lockQuality);
			saveAndSendMeridians(ctx, loginResult, list, bm);
//			String newstr = BaseMeridians.createBaseMeridiansString(list);
//			loginResult.setMeridians(newstr);
//			AllServiceUtil.getMeridiansService().saveMeridians(loginResult.getRole_id().longValue(), newstr);
//
//			String msg = Agreement.getAgreement().rolechangeAgreement(bm.toString());
//			SendMessage.sendMessageToSlef(ctx, msg);
//			mer = GameServer.getAllMeridians().getRandomByType(id);
		} else if (act.equals("3") || act.equals("4")) {
			// 金币修炼--3
			// 仙玉修炼--4
			int id = Convert.toInt(message.substring(2));
			come.tool.Calculation.BaseMeridians bm = come.tool.Calculation.BaseMeridians.getBaseMeridians(list, id);
			int yzjf = yzjf(bm.getStage());
			Meridians mer = GameServer.getAllMeridians().getByQuality(id, bm.getQuality());// (id);
			if(bm.getStage() >= mer.getStagesName().split("_").length) {
				SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().PromptAgreement("等级已满"));
				return;
			}
//			int exp = mer.getExp();
			if (mer.getExp() > loginResult.getExperience().longValue()) {
				SendMessage.sendMessageToSlef(ctx,
						Agreement.getAgreement().PromptAgreement("经验不足"));
				return;
			}
			BigDecimal lxz = loginResult.getScoretype("勇者积分");
			if (lxz.compareTo(BigDecimal.valueOf(yzjf)) < 0) {
				SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().PromptAgreement("勇者积分不足!!!"));
				return;
			}//灵修值不足
			AssetUpdate assetUpdate = new AssetUpdate();
			if (act.equals("3")) {
				if (loginResult.getGold().longValue() < mer.getGold()) {
					SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().PromptAgreement("金币不足!!!"));
					return;
				}

				loginResult.setGold(loginResult.getGold().subtract(new BigDecimal(mer.getGold())));
				MonitorUtil.getMoney().useD(mer.getGold());
				bm.setExp(bm.getExp() + mer.getGoldExp());
				//发送货币消耗

				assetUpdate.setData("D=" + (-mer.getGold()));
				//SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().assetAgreement(GsonUtil.getGsonUtil().getgson().toJson(assetUpdate)));
			} else if (act.equals("4")) {
				if (loginResult.getCodecard().longValue() < mer.getMoney()) {
					SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().PromptAgreement("仙玉不足"));
					return;
				}
				loginResult.setCodecard(loginResult.getCodecard().subtract(new BigDecimal(mer.getMoney())));
				MonitorUtil.getMoney().useX(mer.getMoney());
				bm.setExp(bm.getExp() + mer.getMoneyExp());
				//发送货币消耗
//				AssetUpdate assetUpdate = new AssetUpdate();
				assetUpdate.setData("X=" + (-mer.getMoney()));
				//SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().assetAgreement(GsonUtil.getGsonUtil().getgson().toJson(assetUpdate)));
			}
			loginResult.setExperience(new BigDecimal(loginResult.getExperience().longValue() - mer.getExp()));
			assetUpdate.updata("R" + loginResult.getGrade() + "=" + loginResult.getExperience());
			SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().assetAgreement(GsonUtil.getGsonUtil().getgson().toJson(assetUpdate)));
			// 在线商城购买
//			assetUpdate.setType(AssetUpdate.INTEGRATION);
//			 扣除积分
//			assetUpdate.updata("勇者积分=-"+yzjf);
			loginResult.setScore(DrawnitemsAction.Splice(loginResult.getScore(), "勇者积分=" + yzjf, 3));
			if (bm.getExp() >= mer.getAllExp()) {
				int length = mer.getStages().split("_").length;
				if (bm.getStage() + 1 >= length) {
					// 满级
					bm.setExp(mer.getAllExp());
				} else {
					bm.setStage(bm.getStage() + 1);
					bm.setExp(bm.getExp() - mer.getAllExp());
				}
			}
			saveAndSendMeridians(ctx, loginResult, list, bm);
		}
	}
	public int yzjf(int lvl) {
		if (lvl==0) {
			return 10;
		} else if (lvl==1) {
			return 20;
		} else if (lvl==2) {
			return 30;
		} else if (lvl==3) {
			return 40;
		} else  {
			return 50;
		}
//		return 0;
	}
	void saveAndSendMeridians(ChannelHandlerContext ctx, LoginResult loginResult, List<come.tool.Calculation.BaseMeridians> list,
							  come.tool.Calculation.BaseMeridians bm) {
		String newstr = come.tool.Calculation.BaseMeridians.createBaseMeridiansString(list);
		loginResult.setMeridians(newstr);
		AllServiceUtil.getMeridiansService().saveMeridians(loginResult.getRole_id().longValue(), newstr);

		String msg = Agreement.getAgreement().rolechangeAgreement(bm.toString());
		SendMessage.sendMessageToSlef(ctx, msg);
	}
	public void XP(LoginResult loginResult,String message){
		if (StringUtil.isNullOrEmpty(message)) {
			return;
		}
		String V=message.substring(1,2);
		if (!NumberUtils.isDigits(V)) {
			return;
		}
		// 编号
		String numStr = message.substring(2,3);
		if (StringUtil.isNullOrEmpty(numStr) || !NumberUtils.isDigits(numStr)) {
			return;
		}
		//星盘号
		String numStr1 = message.substring(3);
		if (StringUtil.isNullOrEmpty(numStr1) || !NumberUtils.isDigits(numStr1)) {
			return;
		}
		int daoju =Integer.valueOf(numStr1) + 50999;

		int num = Integer.parseInt(numStr);
		String num1 = numStr1;
		BaseXingpans xingpans = loginResult.getXingpans(num);

		if ("1".equals(V)) {

			if(xingpans == null){
				//道具
				ChannelHandlerContext ctx=GameServer.getRoleNameMap().get(loginResult.getRolename());
				List<Goodstable> good = AllServiceUtil.getGoodsTableService().selectGoodsByRoleIDAndGoodsID(loginResult.getRole_id(), new BigDecimal(daoju));
				if (good.size()==0) {
					SendMessage.sendMessageByRoleName(loginResult.getRolename(),Agreement.getAgreement().PromptAgreement("道具不足"));
					return;
				}
				Goodstable goodstable=good.get(0);
				if (goodstable!=null) {
					goodstable.goodxh(1);
					AssetUpdate assetUpdate=new AssetUpdate(AssetUpdate.USEGOOD);
					assetUpdate.setData("G"+goodstable.getRgid()+"="+goodstable.getUsetime());
					SendMessage.sendMessageToSlef(ctx,Agreement.getAgreement().assetAgreement(GsonUtil.getGsonUtil().getgson().toJson(assetUpdate)));
					AllServiceUtil.getGoodsTableService().updateGoodRedis(goodstable);
				}else {
					SendMessage.sendMessageByRoleName(loginResult.getRolename(),Agreement.getAgreement().PromptAgreement("道具不足"));
					return;
				}


				Alchemy alchemy = randXP(num);
				Alchemy alchemy1 = randXP(num);
				while (alchemy1.getAlchemykey().equals(alchemy.getAlchemykey())) {
					alchemy1 = randXP(num);
				}
				BaseXingpans upMer = new BaseXingpans(num, num1, 0, alchemy.getAlchemykey(), Double.valueOf(alchemy.getAlchemysv()), alchemy1.getAlchemykey(), Double.valueOf(alchemy1.getAlchemysv()));
				loginResult.setXingpans(num, upMer);
				String msg=Agreement.getAgreement().skillxplearnAgreement(GsonUtil.getGsonUtil().getgson().toJson(upMer.toString()));
				SendMessage.sendMessageByRoleName(loginResult.getRolename(),msg);
				SendMessage.sendMessageByRoleName(loginResult.getRolename(),Agreement.getAgreement().PromptAgreement("#G激活星魂"));
			}else{
				if(xingpans.getExp().contains(num1)){
					SendMessage.sendMessageByRoleName(loginResult.getRolename(),Agreement.getAgreement().PromptAgreement("#G你已经激活过了"));
				}else{
					ChannelHandlerContext ctx=GameServer.getRoleNameMap().get(loginResult.getRolename());
					List<Goodstable> good = AllServiceUtil.getGoodsTableService().selectGoodsByRoleIDAndGoodsID(loginResult.getRole_id(), new BigDecimal(daoju));
					if (good.size()==0) {
						SendMessage.sendMessageByRoleName(loginResult.getRolename(),Agreement.getAgreement().PromptAgreement("道具不足"));
						return;
					}
					Goodstable goodstable=good.get(0);
					if (goodstable!=null) {
						goodstable.goodxh(1);
						AssetUpdate assetUpdate=new AssetUpdate(AssetUpdate.USEGOOD);
						assetUpdate.setData("G"+goodstable.getRgid()+"="+goodstable.getUsetime());
						SendMessage.sendMessageToSlef(ctx,Agreement.getAgreement().assetAgreement(GsonUtil.getGsonUtil().getgson().toJson(assetUpdate)));
						AllServiceUtil.getGoodsTableService().updateGoodRedis(goodstable);
					}else {
						SendMessage.sendMessageByRoleName(loginResult.getRolename(),Agreement.getAgreement().PromptAgreement("道具不足"));
						return;
					}
					xingpans.setExp(xingpans.getExp()+"="+num1);
					loginResult.setXingpans(num, xingpans);
					SendMessage.sendMessageByRoleName(loginResult.getRolename(),Agreement.getAgreement().PromptAgreement("#G激活星魂"));
					String msg=Agreement.getAgreement().skillxplearnAgreement(GsonUtil.getGsonUtil().getgson().toJson(xingpans.toString()));
					SendMessage.sendMessageByRoleName(loginResult.getRolename(),msg);
				}

			}
		}

		if ("2".equals(V)) {
			if (xingpans == null) {
				String msg = "还没有激活，不能分解#99";
				SendMessage.sendMessageByRoleName(loginResult.getRolename(),Agreement.getAgreement().PromptAgreement(msg));
				return;
			}
			ChannelHandlerContext ctx=GameServer.getRoleNameMap().get(loginResult.getRolename());
			List<Goodstable> good = AllServiceUtil.getGoodsTableService().selectGoodsByRoleIDAndGoodsID(loginResult.getRole_id(), new BigDecimal(daoju));
			if (good.size()==0) {
				SendMessage.sendMessageByRoleName(loginResult.getRolename(),Agreement.getAgreement().PromptAgreement("道具不足"));
				return;
			}
			Goodstable goodstable=good.get(0);
			if (goodstable!=null) {
				int hunzhi = num*10;
				goodstable.goodxh(1);
				loginResult.setScore(Splice(loginResult.getScore(), "魂值" + "=" + hunzhi, 2));
				AssetUpdate assetUpdate = new AssetUpdate(AssetUpdate.USEGOOD);
				assetUpdate.setData("魂值" + "=" + hunzhi);
				assetUpdate.setMsg("分解成功,获得了" + hunzhi + "个魂值");
				assetUpdate.setData("G"+goodstable.getRgid()+"="+goodstable.getUsetime());
				SendMessage.sendMessageToSlef(ctx,Agreement.getAgreement().assetAgreement(GsonUtil.getGsonUtil().getgson().toJson(assetUpdate)));
				AllServiceUtil.getGoodsTableService().updateGoodRedis(goodstable);
			}else {
				SendMessage.sendMessageByRoleName(loginResult.getRolename(),Agreement.getAgreement().PromptAgreement("道具不足"));
				return;
			}

		}

		if ("3".equals(V)) {
			if (xingpans == null) {
				String msg = "还为激活星魂不能强化#99";

				SendMessage.sendMessageByRoleName(loginResult.getRolename(),Agreement.getAgreement().PromptAgreement(msg));
				return;
			} else {
				if (xingpans.getXs() >= 15) {
					String msg = "已经强化满级了#126";
					SendMessage.sendMessageByRoleName(loginResult.getRolename(),Agreement.getAgreement().PromptAgreement(msg));
					return;
				}
				int haoshi = 0;
				if(xingpans.getXs()<5) {
					haoshi = 99001;
				}else if(xingpans.getXs()<10) {
					haoshi = 99002;
				}else if(xingpans.getXs()<15) {
					haoshi = 99003;
				}

				ChannelHandlerContext ctx=GameServer.getRoleNameMap().get(loginResult.getRolename());
				List<Goodstable> good = AllServiceUtil.getGoodsTableService().selectGoodsByRoleIDAndGoodsID(loginResult.getRole_id(), new BigDecimal(haoshi));
				int shuliang = 0;
				if(xingpans.getXs()==0||xingpans.getXs()==5||xingpans.getXs()==10) {
					shuliang =1;
				}else if(xingpans.getXs()==1||xingpans.getXs()==6||xingpans.getXs()==11) {
					shuliang =2;
				}else if(xingpans.getXs()==2||xingpans.getXs()==7||xingpans.getXs()==12) {
					shuliang =3;
				}else if(xingpans.getXs()==3||xingpans.getXs()==8||xingpans.getXs()==13) {
					shuliang =4;
				}else if(xingpans.getXs()==4||xingpans.getXs()==9||xingpans.getXs()==14) {
					shuliang =5;
				}
				if (good.size()==0) {
					SendMessage.sendMessageByRoleName(loginResult.getRolename(),Agreement.getAgreement().PromptAgreement("道具不足"));
					return;
				}


				Goodstable goodstable=good.get(0);
				if (goodstable!=null) {
					if (shuliang>goodstable.getUsetime()) {
						SendMessage.sendMessageByRoleName(loginResult.getRolename(),Agreement.getAgreement().PromptAgreement("道具不足"));
						return;
					}
					goodstable.goodxh(shuliang);
					AssetUpdate assetUpdate=new AssetUpdate(AssetUpdate.USEGOOD);
					assetUpdate.setData("G"+goodstable.getRgid()+"="+goodstable.getUsetime());
					SendMessage.sendMessageToSlef(ctx,Agreement.getAgreement().assetAgreement(GsonUtil.getGsonUtil().getgson().toJson(assetUpdate)));
					AllServiceUtil.getGoodsTableService().updateGoodRedis(goodstable);
				}else {
					SendMessage.sendMessageByRoleName(loginResult.getRolename(),Agreement.getAgreement().PromptAgreement("道具不足"));
					return;
				}
				int s= 1+(int)(Math.random()*1000);
				if(xingpans.getXs()==1&&s>250) {
					SendMessage.sendMessageByRoleName(loginResult.getRolename(),Agreement.getAgreement().PromptAgreement("#G强化失败，再接再厉"));
					return;
				}else if(xingpans.getXs()==2&&s>200) {
					SendMessage.sendMessageByRoleName(loginResult.getRolename(),Agreement.getAgreement().PromptAgreement("#G强化失败，再接再厉"));
					return;
				}else if(xingpans.getXs()==3&&s>200) {
					SendMessage.sendMessageByRoleName(loginResult.getRolename(),Agreement.getAgreement().PromptAgreement("#G强化失败，再接再厉"));
					return;
				}else if(xingpans.getXs()==4&&s>100) {
					SendMessage.sendMessageByRoleName(loginResult.getRolename(),Agreement.getAgreement().PromptAgreement("#G强化失败，再接再厉"));
					return;
				}else if(xingpans.getXs()==5&&s>300) {
					SendMessage.sendMessageByRoleName(loginResult.getRolename(),Agreement.getAgreement().PromptAgreement("#G强化失败，再接再厉"));
					return;
				}else if(xingpans.getXs()==6&&s>250) {
					SendMessage.sendMessageByRoleName(loginResult.getRolename(),Agreement.getAgreement().PromptAgreement("#G强化失败，再接再厉"));
					return;
				}else if(xingpans.getXs()==7&&s>200) {
					SendMessage.sendMessageByRoleName(loginResult.getRolename(),Agreement.getAgreement().PromptAgreement("#G强化失败，再接再厉"));
					return;
				}else if(xingpans.getXs()==8&&s>200) {
					SendMessage.sendMessageByRoleName(loginResult.getRolename(),Agreement.getAgreement().PromptAgreement("#G强化失败，再接再厉"));
					return;
				}else if(xingpans.getXs()==9&&s>100) {
					SendMessage.sendMessageByRoleName(loginResult.getRolename(),Agreement.getAgreement().PromptAgreement("#G强化失败，再接再厉"));
					return;
				}else if(xingpans.getXs()==10&&s>300) {
					SendMessage.sendMessageByRoleName(loginResult.getRolename(),Agreement.getAgreement().PromptAgreement("#G强化失败，再接再厉"));
					return;
				}else if(xingpans.getXs()==11&&s>250) {
					SendMessage.sendMessageByRoleName(loginResult.getRolename(),Agreement.getAgreement().PromptAgreement("#G强化失败，再接再厉"));
					return;
				}else if(xingpans.getXs()==12&&s>200) {
					SendMessage.sendMessageByRoleName(loginResult.getRolename(),Agreement.getAgreement().PromptAgreement("#G强化失败，再接再厉"));
					return;
				}else if(xingpans.getXs()==13&&s>200) {
					SendMessage.sendMessageByRoleName(loginResult.getRolename(),Agreement.getAgreement().PromptAgreement("#G强化失败，再接再厉"));
					return;
				}else if(xingpans.getXs()==14&&s>100) {
					SendMessage.sendMessageByRoleName(loginResult.getRolename(),Agreement.getAgreement().PromptAgreement("#G强化失败，再接再厉"));
					return;
				}else if(xingpans.getXs()==0&&s>300) {
					SendMessage.sendMessageByRoleName(loginResult.getRolename(),Agreement.getAgreement().PromptAgreement("#G强化失败，再接再厉"));
					return;
				}
				xingpans.setXs((xingpans.getXs()+1));
				loginResult.setXingpans(num, xingpans);
				SendMessage.sendMessageByRoleName(loginResult.getRolename(),Agreement.getAgreement().PromptAgreement("#G强化成功"));
				String msg=Agreement.getAgreement().skillxplearnAgreement(GsonUtil.getGsonUtil().getgson().toJson(xingpans.toString()));
				SendMessage.sendMessageByRoleName(loginResult.getRolename(),msg);

			}
		}

		if ("4".equals(V)) {
			if (xingpans == null) {
				String msg = "为激活的经脉不能洗练#99";

				SendMessage.sendMessageByRoleName(loginResult.getRolename(),Agreement.getAgreement().PromptAgreement(msg));
				return;
			} else {
				if (xingpans.getXs() < 10) {
					String msg = "强化等级大于10级才能洗练";
					SendMessage.sendMessageByRoleName(loginResult.getRolename(),Agreement.getAgreement().PromptAgreement(msg));
					return;
				}
				// 扣除魂值
				int JM_SCORE1 = num*10;
				if (loginResult.getScoretype("魂值").longValue() < JM_SCORE1) {
					SendMessage.sendMessageByRoleName(loginResult.getRolename(),Agreement.getAgreement().PromptAgreement("#R你的魂值不足"));
					return;
				}
				loginResult.setScore(Splice(loginResult.getScore(), "魂值" + "=" + JM_SCORE1, 3));
				AssetUpdate assetUpdate = new AssetUpdate(AssetUpdate.USEGOOD);
				assetUpdate.updata("魂值=-" + JM_SCORE1);
				SendMessage.sendMessageByRoleName(loginResult.getRolename(), Agreement.getAgreement().assetAgreement(GsonUtil.getGsonUtil().getgson().toJson(assetUpdate)));
				Alchemy alchemy = randXP(num);
				Alchemy alchemy1 = randXP(num);
				while (alchemy1.getAlchemykey().equals(alchemy.getAlchemykey())) {
					alchemy1 = randXP(num);
				}

				double max=Double.valueOf(alchemy.getAlchemymv());
				double min=Double.valueOf(alchemy.getAlchemysv());
				double s = min + new Random().nextDouble() * (max - min);

				double max1=Double.valueOf(alchemy1.getAlchemymv());
				double min1=Double.valueOf(alchemy1.getAlchemysv());
				double s1 = min1 + new Random().nextDouble() * (max1 - min1);
				DecimalFormat df = new DecimalFormat("######0.00");

				xingpans.setKey(alchemy.getAlchemykey());
				xingpans.setValue(Double.valueOf(df.format(s)));
				xingpans.setKey1(alchemy1.getAlchemykey());
				xingpans.setValue1(Double.valueOf(df.format(s1)));
				loginResult.setXingpans(num, xingpans);
				String msg=Agreement.getAgreement().skillxplearnAgreement(GsonUtil.getGsonUtil().getgson().toJson(xingpans.toString()));
				SendMessage.sendMessageByRoleName(loginResult.getRolename(),msg);

			}
		}

		RoleData data=RolePool.getRoleData(loginResult.getRole_id());
		if (data==null) {return;}
		data.setXls(42, loginResult.getBaseQl1());
	}
	public static void useGood(Goodstable good,int sum){
		good.goodxh(sum);//添加记录
		AllServiceUtil.getGoodsrecordService().insert(good, null, 1, 9);
		AllServiceUtil.getGoodsTableService().updateGoodRedis(good);
	}
	public Alchemy randXP(int num) {
		List<Alchemy> alchemies=GameServer.getAllAlchemy().get(num + "号星盘");
		if (alchemies.size() > 0) {
			int i = new Random().nextInt(alchemies.size());
			return alchemies.get(i);
		}
		return null;
	}
	/**
	 * 检验输入格式
	 */
	public static int[] getbz(String[] v) {
		try {
			int[] a = new int[v.length];
			for (int i = 0; i < v.length; i++) {
				a[i] = Integer.parseInt(v[i]);
				if (a[i] < 1 || a[i] > 10)
					return null;
			}
			return a;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
}
