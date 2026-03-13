package org.come.action.sys;

import com.github.pagehelper.util.StringUtil;
import com.gl.controller.AutoContext;
import come.tool.Mixdeal.AnalysisString;
import io.netty.channel.ChannelHandlerContext;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import oracle.net.aso.s;
import org.apache.commons.lang.StringUtils;
import org.come.action.IAction;
import org.come.action.reward.DrawnitemsAction;
import org.come.bean.*;
import org.come.entity.*;
import org.come.handler.SendMessage;
import org.come.model.Configure;
import org.come.model.Title;
import org.come.protocol.Agreement;
import org.come.protocol.AgreementUtil;
import org.come.protocol.ParamTool;
import org.come.redis.RedisControl;
import org.come.redis.RedisParameterUtil;
import org.come.redis.RedisPoolUntil;
import org.come.server.GameServer;
import org.come.task.MonsterUtil;
import org.come.task.RefreshMonsterTask;
import org.come.tool.WriteOut;
import org.come.until.AllServiceUtil;
import org.come.until.GsonUtil;

import come.tool.BangBattle.BangBattlePool;
import come.tool.BangBattle.BangFight;
import come.tool.Battle.BattleData;
import come.tool.Battle.BattleThreadPool;
import come.tool.Role.RoleData;
import come.tool.Role.RolePool;
import come.tool.Role.RoleShow;
import come.tool.FightingData.ManData;
import come.tool.Calculation.CalculationUtil;
import come.tool.Scene.Scene;
import come.tool.Scene.SceneUtil;
import come.tool.Scene.DNTG.DNTGScene;
import come.tool.Scene.LaborDay.LaborScene;
import come.tool.Stall.StallPool;
import come.tool.Title.TitleUtil;
import come.tool.newTeam.TeamBean;
import come.tool.newTeam.TeamRole;
import come.tool.newTeam.TeamUtil;
import redis.clients.jedis.Jedis;

import static org.come.action.sys.LoginAction.LOGIN_MAX;
import static org.come.action.sys.LoginAction.getIP;

public class enterGameAction implements IAction {
    static BigDecimal ZERO = new BigDecimal(0);

    @Override
    public void action(ChannelHandlerContext ctx, String message) {
        // TODO Auto-generated method stub
//        Configure configure = s.get(1);
        String IP = getIP(ctx);
        String[] ms = message.split("\\|");
        BigDecimal role_id = new BigDecimal(ms[0]);
        LoginResult roleInfo = null;
        synchronized (GameServer.userLock) {
            RoleData data = RolePool.getRoleData(role_id);
            if (data != null) {//先判断是否为掉线重连或者顶号
                ChannelHandlerContext ctx1 = GameServer.getRoleNameMap().get(data.getLoginResult().getRolename());
                if (ctx1 != null) {
                    roleInfo = GameServer.getAllLoginRole().get(ctx1);
                    GameServer.userDownTwos(ctx1);
                }
            }
            if (roleInfo == null) {
                roleInfo = AllServiceUtil.getRoleTableService().selectRoleID(role_id);
            }
        }
        if (roleInfo == null) {
            return;
        }
        String clientIP = getIP(ctx);
        AtomicInteger count = new AtomicInteger();
        if (StringUtils.isNotBlank(clientIP)) {
            GameServer.getAllLoginRole().forEach((k, v) -> {
                if(!(k instanceof AutoContext)) {
                    String ip = getIP(k);
                    if (ip.equals(clientIP))
                        count.addAndGet(1);
                }
            });
        }
        if(count.get()>LOGIN_MAX-1){
            String msg = Agreement.getAgreement().erroLoginAgreement("登陆已达上限");
            SendMessage.sendMessageToSlef(ctx, msg);
            return;
        }
        if (ms.length == 1) {//判断是否是冒名顶替
//            ChannelHandlerContext ctx1 = GameServer.getInlineUserNameMap().get(roleInfo.getUserName());
//            if (ctx1 == null || ctx1 != ctx) {
//                System.out.println("冒名顶替:" + roleInfo.getRolename());
//                WriteOut.addtxt("冒名顶替:" + roleInfo.getRolename() + "====" + GameServer.getSocketUserNameMap().get(ctx), 9999);
//                SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().inlineLoginAgreement());
//                return;
//            }
        } else {//重连验证
            String userName = ms[1];
            String userPwd = null;
            for (int i = 2; i < ms.length; i++) {
                if (i != 2) {
                    userPwd += "|";
                    userPwd += ms[i];
                } else {
                    userPwd = ms[i];
                }
            }
            if (userPwd == null) {
                SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().inlineLoginAgreement());
                return;
            }
            UserTable userTable = AllServiceUtil.getUserTableService().findUserByUserNameAndUserPassword(userName, userPwd);
            if (userTable == null || userTable.getUser_id().compareTo(roleInfo.getUser_id()) != 0) {
                WriteOut.addtxt("冒名顶替:" + message, 9999);
                SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().inlineLoginAgreement());
                return;
            }
        }
        //队伍中角色退出
        TeamBean teamBean = TeamUtil.getTeam(roleInfo.getTroop_id());
        TeamRole teamRole = teamBean != null ? teamBean.getTeamRole(roleInfo.getRole_id()) : null;
        roleInfo.setTeamInfo(null);
        roleInfo.setTroop_id(null);
        if (teamRole != null) {
            roleInfo.setTroop_id(teamBean.getTeamId());
            roleInfo.initTeamRole(teamRole);
            if (teamRole.getState() == -2) {
                LoginResult login = null;
                if (teamBean.isCaptian(roleInfo.getRole_id())) {
                    login = roleInfo;
                } else {
                    String teamName = teamBean.getTeamName();
                    ChannelHandlerContext tCtx = GameServer.getRoleNameMap().get(teamName);
                    login = tCtx != null ? GameServer.getAllLoginRole().get(tCtx) : null;
                }
//                if (login != null && login.getMapid().longValue() == roleInfo.getMapid().longValue()) {
//                    teamBean.stateCome(teamRole);
//                    roleInfo.setTeamInfo(teamBean.getTeamInfo());
//                }
                //修复断线重连不归队的问题，修复重连上没有跟队长的地图
                if (login != null) {
                    teamBean.stateCome(teamRole);
                    roleInfo.setTeamInfo(teamBean.getTeamInfo());
                    roleInfo.setMapid(login.getMapid());
                    roleInfo.setX(login.getX());
                    roleInfo.setY(login.getY());
                }
            }
        }
        //先判断任务重置 在把下线时间 同步和服务器同步时间
        String lastDownTime = roleInfo.getUptime();
        Reset(roleInfo, System.currentTimeMillis());
        roleInfo.setUserPwd(null);
        //roleInfo.setServerMeString(null);
        if (roleInfo.getFighting() != 0) {
            BattleData battleData = BattleThreadPool.BattleDatas.get(roleInfo.getFighting());
            if (battleData == null) {
                roleInfo.setFighting(0);
            } else {
                if (battleData.isMultiopen()) {
                    battleData.setWinCamp(-2);
                    BattleThreadPool.removeBattleData(battleData);
                    roleInfo.setFighting(0);
                }
            }
        }
        //地图默认称谓
        BangFight bangFight = null;
        if (roleInfo.getMapid() == 3315) {
            roleInfo.setTitle(roleInfo.getGangname() + "帮" + roleInfo.getGangpost());
            bangFight = BangBattlePool.getBangBattlePool().getBangFight(roleInfo.getGang_id());
        } else if (roleInfo.getMapid() == DNTGScene.mapIdZ || roleInfo.getMapid() == DNTGScene.mapIdF) {
            Scene scene = SceneUtil.getScene(SceneUtil.DNTGID);
            if (scene == null || !scene.isEnd() || ((DNTGScene) scene).getRole(roleInfo.getRole_id()) == null) {
                roleInfo.setMapid(1207L);
                roleInfo.setX(4294L);
                roleInfo.setY(2887L);
            }
        }
        // 查询摆摊
        if (!StallPool.getPool().updateState(roleInfo.getBooth_id(), StallPool.OFF, roleInfo.getRole_id())) {
            roleInfo.setBooth_id(null);
        }
        // 根据角色ID查询物品
        List<Goodstable> goods = AllServiceUtil.getGoodsTableService().getGoodsByRoleID(roleInfo.getRole_id());
        // 获得全部召唤兽
        List<RoleSummoning> pets = AllServiceUtil.getRoleSummoningService().selectRoleSummoningsByRoleID(roleInfo.getRole_id());

//        List<RoleSummoning> pets = new ArrayList<>();
        List<BigDecimal> petid = new ArrayList<>();



//        if (petss != null && petss.size() > 0) {
//            for (RoleSummoning summoning : petss) {
//                try {
//
//                    if (StringUtils.isNotBlank(summoning.getLingxi()) && summoning.getLingxi().split("&").length != 5) {
//                        summoning.setLingxi(null);
//                    }
//                } catch (Exception e) {
//                    summoning.setLingxi(null);
//                }
//                if (summoning.getQuality() != null) {
//                    if (summoning.getQuality().equals("1")) {
//                        if (summoning.getSurplusTimes() != null) {
//                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                            Date date;
//                            try {
//                                date = simpleDateFormat.parse(summoning.getSurplusTimes());
//                                Long sy = date.getTime();
//                                Long cd = new Date().getTime();
//                                if (sy > cd) {
//                                    pets.add(summoning);
//                                } else {
//                                    petid.add(summoning.getSid());
//                                    AllServiceUtil.getRoleSummoningService().deleteRoleSummoningBySid(summoning.getSid());
//                                }
//                            } catch (ParseException e) {
//                                // TODO Auto-generated catch block
//                                e.printStackTrace();
//                            }
//                        }
//                    } else {
//                        pets.add(summoning);
//                    }
//                } else {
//                    pets.add(summoning);
//                }
//            }
//        }



        // 获得全部召唤兽
        List<RoleSummoning> petAll = AllServiceUtil.getRoleSummoningService().selectRoleSummoningsByRoleID(roleInfo.getRole_id());
        pets = petAll.stream().filter(item -> item.getDeposit() == null
                || item.getDeposit() == 0).collect(Collectors.toList());
        List<RoleSummoning> depositPets = petAll.stream().filter(item -> item.getDeposit() != null && item.getDeposit() == 1).collect(Collectors.toList());
        if (petid != null && petid.size() > 0) {
            AllServiceUtil.getRoleSummoningService().deleteRoleSummoningBySidList(petid);
        }
        // 获得角色所有的灵宝
        List<Lingbao> lingbaos = AllServiceUtil.getLingbaoService().selectLingbaoByRoleID(roleInfo.getRole_id());
        // 返回该角色所有宝宝
        List<Baby> babys = AllServiceUtil.getBabyService().selectBabyByRolename(roleInfo.getRole_id());
        // 获得角色所有坐骑
        List<Mount> mounts = AllServiceUtil.getMountService().selectMountsByRoleID(roleInfo.getRole_id());
        // 获取角色所有的伙伴
        List<Pal> pals = AllServiceUtil.getPalService().selectPalByRoleID(roleInfo.getRole_id());
        List<Fly> flys = AllServiceUtil.getFlyService().selectFlyByRoleID(roleInfo.getRole_id());
        RoleData roleData = null;

        //宠物跟随
//		if (roleInfo.getSummoning_id()!=null){
//			List<RoleSummoning> roleSummonings = AllServiceUtil.getRoleSummoningService().selectRoleSummoningsByRoleID(roleInfo.getRole_id());
//			roleInfo.setShowRoleSummoningList(roleSummonings);
//
//		}
        synchronized (GameServer.userLock) {
            // 添加用户信息，将socket与角色信息匹配
            roleData = RolePool.addRoleData(roleInfo, goods, pets, lingbaos, babys, mounts, flys);
            roleData.setIP(IP);
            GameServer.addOuts(ctx, roleInfo);
        }

        // 登录时按当前称号/装备等服务端真实属性计算最大 HP/MP，并将当前值补到最大，
        // 防止数据库中存的旧 hp/mp（未含称号加成）导致重登后血蓝回退
        try {
            // camp/man 只是战斗位标识，这里随便给一个合法值即可
            ManData manData = new ManData(0, 0);
            CalculationUtil.loadRoleBattle(manData, roleInfo, roleData, null, null, null, null, null);
            if (manData.getHp_z() != null) {
                roleInfo.setHp(manData.getHp_z());
            }
            if (manData.getMp_z() != null) {
                roleInfo.setMp(manData.getMp_z());
            }
        } catch (Exception ignored) {
        }

        GameServer.LogIn(IP, roleInfo.getRolename(), true);
        if (GameServer.inlineNum.get() > GameServer.inlineMax) {
            GameServer.inlineMax = GameServer.inlineNum.get();
        }

        // 储存修改后的传送角色信息
        List<RoleShow> roleShows = new ArrayList<>();
        roleShows.add(roleInfo.getRoleShow());
        GetClientUserMesageBean getClientUserMesageBean = new GetClientUserMesageBean();
        getClientUserMesageBean.setRoleShows(roleShows);
        String mes = Agreement.getAgreement().intogameAgreement(GsonUtil.getGsonUtil().getgson().toJson(getClientUserMesageBean));
        roleShows.clear();
        //遍历需要的条件
        BigDecimal gang_id = null;
        // 地图ID转格式
        long mapid = roleInfo.getMapid();//获取地图ID
        Map<String, ChannelHandlerContext> mapRoleMap = GameServer.getMapRolesMap().get(roleInfo.getMapid());
        if (mapid == 1208 && roleInfo.getX().intValue() == 700 && roleInfo.getY().intValue() == 500) {
            if(pets.size() > 1){
                roleInfo.setSummoning_id(pets.get(0).getSid());
            }
        }
        Iterator<Map.Entry<String, ChannelHandlerContext>> entries = mapRoleMap.entrySet().iterator();
        if (mapid == 3000) {
            gang_id = roleInfo.getGang_id();
        }
        while (entries.hasNext()) {
            Entry<String, ChannelHandlerContext> entrys = entries.next();
            ChannelHandlerContext value = entrys.getValue();
            LoginResult loginResult = GameServer.getAllLoginRole().get(value);
            if (value == null || loginResult == null) {
                continue;
            }
            if (gang_id != null && gang_id.compareTo(loginResult.getGang_id()) != 0) {
                continue;
            }
            if (bangFight != null && bangFight.getMap(loginResult.getGang_id()) == null) {
                continue;
            }
            SendMessage.sendMessageToSlef(value, mes);
            //添加新地图角色
            roleShows.add(loginResult.getRoleShow());
        }
        mapRoleMap.put(roleInfo.getRolename(), ctx);

        enterGameBean gameBean = new enterGameBean();
        Jedis jedis = RedisPoolUntil.getJedis();
        String autoSwitch = jedis.hget(RedisParameterUtil.autoSwitch, roleInfo.getRole_id().toString());
        RedisPoolUntil.returnResource(jedis);
        if (StringUtils.isNotEmpty(autoSwitch)) {
            AutoSwitchBean autoSwitchBean = GsonUtil.getGsonUtil().getgson().fromJson(autoSwitch, AutoSwitchBean.class);
            roleInfo.setAutoSwitchBean(autoSwitchBean);
        }
        gameBean.setLoginResult(roleInfo);
        gameBean.setPrivateData(roleData.getPrivateData());
        //玩家数据
        gameBean.setRoleShows(roleShows);
        // 物品
        gameBean.setList(goods);
        // 角色召唤兽
        gameBean.setRoleSummonings(pets);
        // 角色坐骑
        gameBean.setMounts(mounts);
        //角色飞行器
        gameBean.setFlys(flys);
        // 角色寄存召唤兽
        gameBean.setRoleDepositSummonings(depositPets);
        // 角色灵宝
        gameBean.setLingbaos(lingbaos);
        // 角色宝宝
        gameBean.setBabys(babys);
        // 角色伙伴
        gameBean.setPals(pals);
        // 摆摊列表
        gameBean.setStallBeans(StallPool.getPool().getmap(roleInfo.getMapid().intValue()));
        // 摆摊数据
        if (roleInfo.getBooth_id() != null) {
            gameBean.setStall(StallPool.getPool().getAllStall().get(roleInfo.getBooth_id().intValue()));
        }
        // 怪物列表
        gameBean.setMonster(MonsterUtil.getMapMonster(mapid, roleInfo.getGang_id()));
        //一些记录数据
        gameBean.setPackRecord(roleData.getPackRecord());
        //系统设置
        gameBean.setRoleSystem(roleData.getRoleSystem());
        gameBean.setSceneMsg(SceneUtil.getSceneMsg(roleInfo, 0, roleInfo.getMapid()));
        // 单独返回的消息
        String messages = Agreement.getAgreement().enterGameAgreement(GsonUtil.getGsonUtil().getgson().toJson(gameBean));
        SendMessage.sendMessageToSlef(ctx, messages);
        ParamTool.ACTION_MAP.get(AgreementUtil.friend).action(ctx, roleInfo.getRole_id().toString());
        if (bangFight != null) {
            if (bangFight.BangState == 1) {
                bangFight.addMember(roleInfo.getRolename(), roleInfo.getGang_id());
                bangFight.getzk(roleInfo.getRolename(), true);//送进帮战
            }
        }
        //判断是否处于战斗中
        if (roleInfo.getFighting() != 0) {
            BattleThreadPool.addConnection(ctx, roleInfo.getFighting(), roleInfo.getRolename());
        } else if (StringUtil.isNotEmpty(lastDownTime)) {
            if (ms.length == 1) {
                SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().PromptAgreement("您当前登录的IP为:#G" + IP + "#Y祝你游戏开心"));
                UseCardBean limit = roleData.getLimit("VIP");
                PayvipBean vipBean = GameServer.getVIP(roleInfo.getPaysum().longValue());
                NChatBean bean = new NChatBean();
                bean.setId(5);
                String str = "";
                if (vipBean != null) {
                    //str = "#Y尊贵的#R【VIP" + vipBean.getGrade() + "】#Y玩家：";
                    str = "#W玩家";
                } else {
                    str = "#W玩家";
                }
                String mapName = GameServer.getMapName(mapid + "");
                bean.setMessage(str + "#Y" + roleInfo.getRolename() + "#W在#G" + mapName + "#W上线了");
                String msg = Agreement.getAgreement().chatAgreement(GsonUtil.getGsonUtil().getgson().toJson(bean));
                SendMessage.sendMessageToAllRoles(msg);
            }
//            UserTable userTable = AllServiceUtil.getUserTableService().selectByPrimaryKey(roleInfo.getUser_id());
//            SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().PromptAgreement("您上次的登录的IP:#G" + userTable.getLoginip() + "#Y,您当前登陆器的IP:#G" + IP + "#Y祝你游戏开心"));
        }
//        if (petss != null && petss.size() > 0) {
//            for (RoleSummoning summoning : petss) {
//                summoning.setShow(false);//重新登录去除 召唤兽跟随
//                if (summoning.getQuality() != null) {
//                    if (summoning.getQuality().equals("1")) {
//                        if (summoning.getSurplusTimes() != null) {
//                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                            Date date;
//                            try {
//                                date = simpleDateFormat.parse(summoning.getSurplusTimes());
//                                Long sy = date.getTime();
//                                Long cd = new Date().getTime();
//                                if (sy < cd) {
////										AllServiceUtil.getRoleSummoningService().deleteRoleSummoningBySid(new BigDecimal(summoning.getSummoningid()));
//                                    SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().PromptAgreement("#R提示：您的召唤兽#G【" + summoning.getSummoningname() + "】#Y已过期！"));
//                                }
//                            } catch (ParseException e) {
//                                // TODO Auto-generated catch block
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                }
//            }
////        }
//    }

        //获取快捷键
        HashMap<String, String> shortCut = RedisControl.getV(RedisParameterUtil.ShortSkillRedis + roleInfo.getRole_id(), "shortCut", HashMap.class);
        if (shortCut != null) {
            SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().shortCutSkillUseAgreement(
                    GsonUtil.getGsonUtil().getgson().toJson(shortCut)));
        }
        BigDecimal zy = roleInfo.getScoretype("支援");
        if (zy.intValue() == 0) {
            roleInfo.setScore(DrawnitemsAction.Splice(roleInfo.getScore(), "支援=1", 2));
        } else {
            roleInfo.setScore(DrawnitemsAction.Splice(roleInfo.getScore(), "支援=1", 1));
        }
        //补发数据
        StringBuffer buffer = null;
        if (teamRole != null) {
            if (buffer == null) {
                buffer = new StringBuffer();
            }
            buffer.append(Agreement.getAgreement().team1Agreement(GsonUtil.getGsonUtil().getgson().toJson(teamBean)));
        }
        //添加劳动节图标
        if (LaborScene.I != 0) {
            LaborScene.bindRole(roleInfo);
            if (buffer == null) {
                buffer = new StringBuffer();
            }
            buffer.append(LaborScene.LABOR);
        }
        if (buffer != null) {
            SendMessage.sendMessageToSlef(ctx, buffer.toString());
        }
        //判断属性是否正常  抗性高于75封号
        if (pets != null && pets.size() > 0) {
            for (RoleSummoning roleSummoning : pets) {
                int errorCount = 0;
                String[] lys = null;
                String lyschecked = "";
                double l = 0.0;
                if (roleSummoning.getLyk() != null && !roleSummoning.getLyk().equals("")) {
                    lys = roleSummoning.getLyk().split("\\|");
                    if (lys != null) {
                        for (int j2 = 0; j2 < lys.length; ++j2) {
                            l = Double.parseDouble(lys[j2].split("=")[1]);
                            if (l >= 200) {
                                errorCount += 1;
                            }
                        }
                    }
                }
//                if (errorCount >= 5) {
//                    SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().PromptAgreement("系统检测到您召唤兽属性异常，30秒后将封号！！！"));
//                    forceExiteGame(roleInfo);
//                }
            }
        }
        String resistance = roleInfo.getResistance();
        String[] v = resistance.split("\\|");
        for (String s1 : v) {
            String[] v1 = s1.split("#");
            for (String string : v1) {
                String[] split = string.split("=");
                if (split.length == 2) {
                    double d = Double.parseDouble(split[1]);
                    if (d > 40) {
                        SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().PromptAgreement("检测到你的帮派抗性异常，30秒后将封号！！！"));
                        roleInfo.setResistance("主-|副-");
                        forceExiteGame(roleInfo);
                        return;
                    }
                }
            }
        }
        if(pets!=null &&pets.size()>0) {
//            for (RoleSummoning roleSummoning : pets) {
//                int lvl = AnalysisString.petLvlint(roleSummoning.getGrade());
//                int Turn=AnalysisString.petTurnRount(roleSummoning.getGrade());
//                if (Turn < 4) {
//                    if (roleSummoning.getBone() < lvl || roleSummoning.getSpir() < lvl || roleSummoning.getPower() < lvl || roleSummoning.getSpeed() < lvl) {
//                        SendMessage.sendMessageToSlef(ctx,Agreement.getAgreement().PromptAgreement("系统检测到您召唤兽["+roleSummoning.getSummoningname()+"]属性点异常，请尽快使用混元丹，30秒后将封号！！！"));
//                        forceExiteGame(roleInfo);
//                    }
//                }else{
//                    if (roleSummoning.getBone() < lvl || roleSummoning.getSpir() < lvl || roleSummoning.getPower() < lvl || roleSummoning.getSpeed() < lvl || roleSummoning.getCalm() < lvl) {
//                        SendMessage.sendMessageToSlef(ctx,Agreement.getAgreement().PromptAgreement("系统检测到您召唤兽["+roleSummoning.getSummoningname()+"]属性点异常，请尽快使用混元丹，30秒后将封号！！！"));
//                        forceExiteGame(roleInfo);
//                    }
//                }
//            }
        }
        /**
         * 飞行器升级参数
         */
        StringBuilder sb = new StringBuilder();
        sb.append("flyLvlConfig").append("&").append(GsonUtil.getGsonUtil().getgson().toJson(GameServer.getFlyConfig()));
        SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().getGameConfig(sb.toString()));

    }
    public static void forceExiteGame(LoginResult roleInfo) {
        TimerTask timerTask = new TimerTask() {
            public void run() { //从这里开始，
                try {
                    Thread.sleep(30000);
                    ParamTool.ACTION_MAP.get("accountstop").action(GameServer.getRoleNameMap().get(roleInfo.getRolename()), roleInfo.getUserName());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        Timer timer = new Timer();
        timer.schedule(timerTask, 0);
    }
//    public static void ExiteGame(LoginResult roleInfo){
//        TimerTask timerTask = new TimerTask() {
//            public void run() { //从这里开始，
//                try {
//                    Thread.sleep(30000);
//                    if (GameServer.getRoleNameMap().get(roleInfo.getRolename()) != null) {
//                        SendMessage.sendMessageByRoleName(roleInfo.getRolename(), Agreement.getAgreement().serverstopAgreement());
//                    }
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//        Timer timer = new Timer();
//        timer.schedule(timerTask, 0);
//    }

    /**
     * 重置判断
     * 第一个时间是离线时间
     */
    public static void Reset(LoginResult loginResult, long time2) {
        /** 最近作用时间 */
        try {
            Title title = GameServer.getTitle(loginResult.getTitle());
            if (title != null) {
                if (title.getExist() != null && !title.getExist().equals("")) {
                    if (title.getExist().startsWith("称谓任务")) {
                        int value = Integer.parseInt(title.getExist().split("=")[1]);
                        int max = loginResult.getJQId("S");
                        if (max < value) {
                            loginResult.setTitle(null);
                        }
                    } else if (title.getExist().startsWith("击杀煞星")) {
                        int value = Integer.parseInt(title.getExist().split("=")[1]);
                        double max = loginResult.getKilltype("击杀煞星");
                        if (max < value) {
                            loginResult.setTitle(null);
                        }
                    } else if (title.getExist().startsWith("充值")) {
                        if (AllServiceUtil.getTitletableService().selectRoleTitle(loginResult.getRole_id(), title.getTitlename()) == null) {
                            loginResult.setTitle(null);
                        }
                    } else if (!TitleUtil.isTitle(loginResult.getTitle(), loginResult.getRole_id())) {
                        loginResult.setTitle(null);
                    }
                } else if (AllServiceUtil.getTitletableService().selectRoleTitle(loginResult.getRole_id(), title.getTitlename()) == null) {
                    loginResult.setTitle(null);
                }
            }
            if (loginResult.getMapid() >= 3329 && loginResult.getMapid() <= 3332) {
                loginResult.setMapid(new Long(1207));
                loginResult.setX(new Long(4294));
                loginResult.setY(new Long(2887));
            }
            int type = 0;
            int type2 = 0;
            long time1 = 0;
            if (loginResult.getUptime() != null && !loginResult.getUptime().equals("")) {
                time1 = Long.parseLong(loginResult.getUptime());
            }
//			if (loginResult.getPaysum().longValue()>=30) {
//				ExchangeUtil.addCompensation(loginResult.getRole_id(),time1);	
//			}
            Calendar hc = Calendar.getInstance();
            hc.setTimeInMillis(time2);
            hc.set(Calendar.HOUR_OF_DAY, 0);
            hc.set(Calendar.MINUTE, 0);
            hc.set(Calendar.SECOND, 0);
            hc.set(Calendar.MILLISECOND, 0);
            Calendar qc = Calendar.getInstance();
            qc.setTimeInMillis(time1);
            qc.set(Calendar.HOUR_OF_DAY, 0);
            qc.set(Calendar.MINUTE, 0);
            qc.set(Calendar.SECOND, 0);
            qc.set(Calendar.MILLISECOND, 0);
            //相差日期数
            long cha = (hc.getTime().getTime() - qc.getTime().getTime()) / (1000 * 60 * 60 * 24);
            if (cha > 0) {
                type = 1;
                if (cha > 1) {
                    type2 = 1;
                }//超过一天时间间隔
                int week = qc.get(Calendar.DAY_OF_WEEK);
                for (int i = 0; i < cha; i++) {
                    week++;
                    if (week > 7) {
                        week = 1;
                    } else if (week == 2) {//每逢周一
                        type = 2;
                        break;
                    }
                }
            }
            taskReset(loginResult, type, type2);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        loginResult.setUptime(time2 + "");
    }

    /**
     * 重置
     */
    public static void taskReset(LoginResult loginResult, int type, int type2) {
        if (type == 0) {
            return;
        }
        if (type2 == 0) {//隔天上线
            //如果每日充值为0 中断连冲 //连冲大于等于7天 中断连冲
            if (loginResult.getDayfirstinorno() == 0 || loginResult.getDayandpayorno().longValue() >= 7) {
                loginResult.setDayandpayorno(new BigDecimal(0));
                loginResult.removeVipget("3");
            }
        } else {//超过一天未上线
            //中断连冲
            loginResult.setDayandpayorno(new BigDecimal(0));
            loginResult.removeVipget("3");
        }
        loginResult.setDayfirstinorno(0);//清空连充叠加标志
        loginResult.setDaypaysum(new BigDecimal(0));//清空每日充值
        loginResult.setDaygetorno(new BigDecimal(2));//清空每日特惠领取
        loginResult.removeVipget("2");//清空每日充值领取

        loginResult.setTaskComplete(RefreshMonsterTask.ResetTask(loginResult.getTaskComplete(), type));
        loginResult.setDBExp(0);//百分百重置双倍时间
    }

    /**
     * 获取皮肤字符串
     */
    public static String getskin(String skin, List<String> txs, String title, String cb, boolean xqtx,boolean isgw,String gwid) {
//        public static String getskin(String skin,List<String> txs,String title,String cb
//		S:皮肤 X:特效 P:装饰品 J:足迹
//		S1231|X1230_1|P123_1|J12312
        StringBuffer buffer = new StringBuffer();
        if (skin != null && !skin.equals("")) {
            buffer.append("S");
            buffer.append(skin);
        }
        if (isgw) {  //设置光环
            buffer.append("|");
            buffer.append("V");
            buffer.append(gwid);
        }
        if (txs != null) {
            for (int i = 0; i < txs.size(); i++) {
                int id = Integer.parseInt(txs.get(i));
                RoleTxBean bean = GameServer.getTxBean(id);
                if (bean != null) {
                    if (buffer.length() != 0) {
                        buffer.append("|");
                    }
//					/**类型（1特效2装饰品3足迹）*/
                    if (bean.getRdType() == 1) {
                        buffer.append("X");
                    } else if (bean.getRdType() == 2) {
                        buffer.append("P");
                    } else {
                        buffer.append("J");
                    }
                    buffer.append(bean.getRdId());
                    if (bean.getRdType() == 1 || bean.getRdType() == 2) {
                        buffer.append("_");
                        buffer.append(bean.getRdStatues() - bean.getRdType());
                    }
                }
            }
        }
        if (title != null) {
            Title te = GameServer.getTitle(title);
            if (te != null && te.getSkin() != null) {
                if (buffer.length() != 0) {
                    buffer.append("|");
                }
                buffer.append("C");
                buffer.append(te.getSkin());
            }
        }
        if (cb != null) {
            if (buffer.length() != 0) {
                buffer.append("|");
            }
            buffer.append("B");
            buffer.append(cb);
        }
        if (xqtx) {
            if (buffer.length() != 0) {
                buffer.append("|");
            }
            buffer.append("Q");
            buffer.append("205_8");
        }
        if (buffer.length() == 0) {
            return null;
        }
        return buffer.toString();
    }
}
