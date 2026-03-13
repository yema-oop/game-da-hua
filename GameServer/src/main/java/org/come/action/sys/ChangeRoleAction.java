package org.come.action.sys;

import com.gl.controller.AutoContext;
import come.tool.Battle.BattleData;
import come.tool.Battle.BattleThreadPool;
import come.tool.Role.RoleData;
import come.tool.Role.RolePool;
import come.tool.Role.RoleShow;
import come.tool.Scene.SceneUtil;
import come.tool.Stall.StallPool;
import come.tool.newTeam.TeamBean;
import come.tool.newTeam.TeamRole;
import come.tool.newTeam.TeamUtil;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.lang.StringUtils;
import org.come.action.IAction;
import org.come.bean.GetClientUserMesageBean;
import org.come.bean.LoginResult;
import org.come.bean.enterGameBean;
import org.come.entity.*;
import org.come.handler.SendMessage;
import org.come.protocol.Agreement;
import org.come.protocol.AgreementUtil;
import org.come.protocol.ParamTool;
import org.come.server.GameServer;
import org.come.task.MonsterUtil;
import org.come.until.AllServiceUtil;
import org.come.until.GsonUtil;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.come.action.sys.LoginAction.getIP;

/**
 * （切换角色）.
 */
public class ChangeRoleAction implements IAction {

    /**
     * 记录切换角色时的上下文关系：一个主上下文对应多个子上下文
     */
    public static final Map<ChannelHandlerContext, List<ChannelHandlerContext>> allMapping = new ConcurrentHashMap<>();

    /**
     * 记录切换角色时的上下文关系：子上下文对应主上下文
     */
    public static final Map<ChannelHandlerContext, ChannelHandlerContext> reverseMap = new ConcurrentHashMap<>();

    /**
     * 记录当前登录人的上下文映射。
     */
    public static final Map<ChannelHandlerContext, ChannelHandlerContext> currentLoginMap = new ConcurrentHashMap<>();



    /**
     * action 方法
     *
     * @param ctx   当前客户端通道上下文
     * @param msg   来自客户端的消息字符串
     */
    @Override
    public void action(ChannelHandlerContext ctx, String msg) {
        String[] params = msg.split("&");
        if (params.length == 0) {
            return;
        }

        LoginResult loginResult = GameServer.getAllLoginRole().get(ctx);
        if (loginResult == null) {
            return;
        }
        String cmd = params[0];
        switch (cmd) {
            case "c":
                if (params.length > 1) {
                    assignCurrentLogin(ctx, loginResult, params[1]);
                }
                break;
            case "a":
                switchRole(ctx);
                break;
            default:
                break;
        }
    }

    /**
     * 给当前 ctx 分配要切换的目标人 (currentLoginMap).
     *
     * @param ctx         主上下文
     * @param loginResult 主角色信息
     * @param targetRoleIdStr 目标角色 ID（字符串）
     */
    private void assignCurrentLogin(ChannelHandlerContext ctx, LoginResult loginResult, String targetRoleIdStr) {
        List<LoginResult> matchList = GameServer.getAllLoginRole().values().stream()
                .filter(lr -> lr.getRole_id().intValue() == Integer.parseInt(targetRoleIdStr))
                .collect(Collectors.toList());

        if (matchList.isEmpty()) {
            return;
        }
        LoginResult targetLoginResult = matchList.get(0);
        String targetRoleName = targetLoginResult.getRolename();
        ChannelHandlerContext targetCtx = GameServer.getRoleNameMap().get(targetRoleName);
        if (targetCtx != null) {
//            System.out.println("找到上下文切换到:"+targetRoleName);
            currentLoginMap.put(ctx, targetCtx);
        }
    }
    /**
     * 切换角色（）.
     *
     * @param ctx 当前上下文
     */
    private void switchRole(ChannelHandlerContext ctx) {
        LoginResult loginResult = GameServer.getAllLoginRole().get(ctx);
        if (loginResult == null) {
            SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().PromptAgreement("请下线再上!"));
            return;
        }

        // 根据 user_id 查询所有角色
        BigDecimal userId = loginResult.getUser_id();
        List<RoleTableNew> roleList = AllServiceUtil.getRegionService().selectRole(userId, null);


        if (roleList.size() <= 1) {
            SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().PromptAgreement("只有一个角色，无法切换!"));
            return;
        }

        roleList.removeIf(r -> r.getRole_id().intValue() == loginResult.getRole_id().intValue());

        if (roleList.size() > 4) {
            roleList = roleList.subList(0, 4);
        }

        List<ChannelHandlerContext> subCtxList = new ArrayList<>();
        allMapping.put(ctx, subCtxList);
        reverseMap.put(ctx, ctx);
        subCtxList.add(ctx);

        List<enterGameBean> enterInfoList = new ArrayList<>();
        List<LoginResult> childLoginList = new ArrayList<>();

        for (RoleTableNew r : roleList) {
            ChannelHandlerContext autoCtx = GameServer.getRoleNameMap().get(r.getRolename());
            if (autoCtx != null) {
                // 修复：检查目标角色是否在战斗中
                // 如果在战斗中，不能调用 userDownTwosDK，否则会将其从战斗中移除导致状态不一致
                LoginResult existingLogin = GameServer.getAllLoginRole().get(autoCtx);
                if (existingLogin != null && existingLogin.getFighting() != 0) {
                    // 目标角色在战斗中，直接使用已有 ctx，不调用 userDownTwosDK
                    // 一键多开角色已经在战斗中，Tab 切换只是切换到已有角色
                } else {
                    // 不在战斗中，可以安全调用 userDownTwosDK 重新创建
                    GameServer.userDownTwosDK(autoCtx);
                    autoCtx = null;
                }
            }
            if (autoCtx == null) {
                // 修复：优先从 RolePool 获取内存缓存，防止 HP/MP 回退到数据库旧值
                come.tool.Role.RoleData cachedRoleData = come.tool.Role.RolePool.getRoleData(r.getRole_id());
                LoginResult newLogin;
                if (cachedRoleData != null && cachedRoleData.getLoginResult() != null) {
                    // 使用内存缓存中的数据
                    newLogin = cachedRoleData.getLoginResult();
                } else {
                    // 缓存不存在时才从数据库读取
                    RoleTable roleTable = AllServiceUtil.getRoleTableService().selectRoleTableByRoleName(r.getRolename());
                    newLogin = AllServiceUtil.getRoleTableService().selectRoleID(roleTable.getRole_id());
                }
                autoCtx = new AutoContext();
                // 加锁
                synchronized (GameServer.userLock) {
                    GameServer.addOuts1(autoCtx, newLogin);
                }
                newLogin.setMapid(loginResult.getMapid());
                newLogin.setX(loginResult.getX());
                newLogin.setY(loginResult.getY());
            }
            enterGameBean egBean = enterGame(autoCtx);
            enterInfoList.add(egBean);
            subCtxList.add(autoCtx);
            reverseMap.put(autoCtx, ctx);
            childLoginList.add(GameServer.getAllLoginRole().get(autoCtx));
            TeamBean childTeam = TeamUtil.getTeamRole(r.getRole_id());
            if (childTeam != null) {
                childTeam.dismissTeam();
            }
        }

        TeamBean mainTeam = TeamUtil.getTeamRole(loginResult.getRole_id());
        if (mainTeam != null) {
            mainTeam.dismissTeam();
        }

        IAction teamAction = ParamTool.ACTION_MAP.get(AgreementUtil.team1);
        if (teamAction != null) {
            teamAction.action(ctx, null);
        }

        TeamBean mainFullTeam = TeamUtil.getTeam(loginResult.getTroop_id());
        if (mainFullTeam != null) {
            for (LoginResult lr : childLoginList) {
                //偶然性报空?结果发现是上次的队伍还在不知道什么原因引起的 重上退出下队伍就好了
                if (lr==null){
                    SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().PromptAgreement("其他队员正在组队请先上号退出队伍!"));
                    return;
                }
                TeamRole childTeamRole = lr.getTeamRole();
                if (mainFullTeam.getTeamRole(lr.getRole_id()) == null) {
                    mainFullTeam.addTeamRole(childTeamRole, 0);
                }
            }
        }

        String json = GsonUtil.getGsonUtil().getgson().toJson(enterInfoList);
        String msg = Agreement.getAgreement().ChangeroleAgreement(json);
        SendMessage.sendMessageToSlef(ctx, msg);

    }

    /**
     * 进入游戏 (用于生成 enterGameBean 给前端).
     *
     * @param ctx 通道上下文
     * @return  enterGameBean
     */
    public enterGameBean enterGame(ChannelHandlerContext ctx) {
        String ip = getIP(ctx);
        LoginResult loginResult = GameServer.getAllLoginRole().get(ctx);
        if (loginResult == null) {
            return null;
        }

        TeamBean team = TeamUtil.getTeam(loginResult.getTroop_id());
        if (team != null) {
            team.getTeamRole(loginResult.getRole_id());
        }

        loginResult.setTeamInfo(null);
        loginResult.setTroop_id(null);
        enterGameAction.Reset(loginResult, System.currentTimeMillis());
        loginResult.setUserPwd(null);

        if (loginResult.getFighting() != 0) {
            BattleData bd = BattleThreadPool.BattleDatas.get(loginResult.getFighting());
            if (bd == null) {
                loginResult.setFighting(0);
            }
        }

        // 查询角色相关数据
        BigDecimal roleId = loginResult.getRole_id();
        List<Goodstable> goodsList = AllServiceUtil.getGoodsTableService().getGoodsByRoleID(roleId);
        List<RoleSummoning> summoningList = AllServiceUtil.getRoleSummoningService().selectRoleSummoningsByRoleID(roleId);
        List<Lingbao> lingbaoList = AllServiceUtil.getLingbaoService().selectLingbaoByRoleID(roleId);
        List<Baby> babyList = AllServiceUtil.getBabyService().selectBabyByRolename(roleId);
        List<Mount> mountList = AllServiceUtil.getMountService().selectMountsByRoleID(roleId);
        List<Pal> palList = AllServiceUtil.getPalService().selectPalByRoleID(roleId);
        List<Fly> flyList = AllServiceUtil.getFlyService().selectFlyByRoleID(roleId);
        RoleData roleData;
        synchronized (GameServer.userLock) {
            roleData = RolePool.addRoleData(loginResult, goodsList, summoningList, lingbaoList,
                    babyList, mountList, flyList);
            roleData.setIP(ip);

        }

        Long mapId = loginResult.getMapid();
        BigDecimal gangId = null;
        if (mapId != null && mapId == 3000L) {
            gangId = loginResult.getGang_id();
        }

        ConcurrentHashMap<String, ChannelHandlerContext> mapRoles
                = GameServer.getMapRolesMap().get(mapId);
        if (mapRoles == null) {
            mapRoles = new ConcurrentHashMap<>();
            GameServer.getMapRolesMap().put(mapId, mapRoles);
        }

        GetClientUserMesageBean onlineMsgBean = new GetClientUserMesageBean();
        List<RoleShow> roleShows = new ArrayList<>(Collections.singletonList(loginResult.getRoleShow()));
        onlineMsgBean.setRoleShows(roleShows);
        String onlineMsg = Agreement.getAgreement().intogameAgreement(GsonUtil.getGsonUtil().getgson().toJson(onlineMsgBean));

        List<RoleShow> otherRoleShowList = new ArrayList<>();
        for (Map.Entry<String, ChannelHandlerContext> entry : mapRoles.entrySet()) {
            ChannelHandlerContext chx = entry.getValue();
            if (chx == null) {
                continue;
            }
            LoginResult otherLogin = GameServer.getAllLoginRole().get(chx);
            if (otherLogin == null) {
                continue;
            }
            if (gangId != null && gangId.compareTo(otherLogin.getGang_id()) != 0) {
                continue;
            }
            // 通知上线
            SendMessage.sendMessageToSlef(chx, onlineMsg);
            otherRoleShowList.add(otherLogin.getRoleShow());
        }

        mapRoles.put(loginResult.getRolename(), ctx);

        enterGameBean egBean = new enterGameBean();
        egBean.setLoginResult(loginResult);
        egBean.setPrivateData(roleData.getPrivateData());
        egBean.setRoleShows(otherRoleShowList);
        egBean.setList(goodsList);
        egBean.setRoleSummonings(summoningList);
        egBean.setMounts(mountList);
        egBean.setLingbaos(lingbaoList);
        egBean.setBabys(babyList);
        egBean.setPals(palList);

        // 摊位信息
        egBean.setStallBeans(StallPool.getPool().getmap(loginResult.getMapid().intValue()));
        if (loginResult.getBooth_id() != null) {
            egBean.setStall(StallPool.getPool().getAllStall().get(loginResult.getBooth_id().intValue()));
        }

        // 怪物信息
        egBean.setMonster(MonsterUtil.getMapMonster(mapId, loginResult.getGang_id()));

        // 背包记录 + 角色系统配置
        egBean.setPackRecord(roleData.getPackRecord());
        egBean.setRoleSystem(roleData.getRoleSystem());

        // 场景消息
        egBean.setSceneMsg(SceneUtil.getSceneMsg(loginResult, 0L, loginResult.getMapid()));

        // 切换角色时重新计算 HP/MP 最大值，确保称号等属性加成被正确应用
        // 非战斗状态下，应该将当前 HP/MP 补满到最大值，防止旧快照覆盖
        try {
            come.tool.FightingData.ManData manData = new come.tool.FightingData.ManData(0, 0);
            come.tool.Calculation.CalculationUtil.loadRoleBattle(manData, loginResult, roleData, null, null, null, null, null);
            if (manData.getHp_z() != null) {
                loginResult.setHp(manData.getHp_z());
            }
            if (manData.getMp_z() != null) {
                loginResult.setMp(manData.getMp_z());
            }
            // 修复：同步到 Redis 缓存，确保数据一致性
            org.come.redis.RedisControl.insertKeyT(org.come.redis.RedisParameterUtil.COPY_LOGIN, 
                loginResult.getRole_id().toString(), loginResult);
            org.come.redis.RedisControl.insertController(org.come.redis.RedisParameterUtil.COPY_LOGIN, 
                loginResult.getRole_id().toString(), org.come.redis.RedisControl.CALTER);
        } catch (Exception ignored) {
        }

        String msg = Agreement.getAgreement().enterGameAgreement(GsonUtil.getGsonUtil().getgson().toJson(egBean));
        SendMessage.sendMessageToSlef(ctx, msg);

        // 好友信息
        IAction friendAction = ParamTool.ACTION_MAP.get(AgreementUtil.friend);
        if (friendAction != null) {
            friendAction.action(ctx, loginResult.getRole_id().toString());
        }

        return egBean;
    }
}
