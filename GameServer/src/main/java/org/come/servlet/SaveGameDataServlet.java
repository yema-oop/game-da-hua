package org.come.servlet;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.gl.model.User;
import com.gl.service.UserService;
import come.tool.BangBattle.BangBattlePool;
import come.tool.BangBattle.BangFileSystem;
import come.tool.Role.RoleData;
import come.tool.Role.RolePool;
import come.tool.Scene.Scene;
import come.tool.Scene.SceneUtil;
import come.tool.Scene.LTS.LTSUtil;
import come.tool.Scene.LaborDay.LaborScene;
import come.tool.Scene.PKLS.PKLSScene;
import come.tool.Scene.PKLS.lsteamBean;
import come.tool.Scene.RC.RCScene;
import come.tool.Stall.StallPool;
import come.tool.newGang.GangUtil;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.come.action.festival.HatchvalueAction;
import org.come.action.monitor.MonitorUtil;
import org.come.bean.LoginResult;
import org.come.redis.RedisControl;
import org.come.server.GameServer;
import org.come.task.RefreshMonsterTask;
import org.come.thread.RedisEqualWithSqlThread;
import org.come.tool.ReadExelTool;
import org.come.tool.WriteOut;
import org.come.until.CreateTextUtil;
import org.come.until.GsonUtil;

public class SaveGameDataServlet extends HttpServlet {
    public void destroy() {
        super.destroy();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HashMap returnData = new HashMap();
        User user = (User)  request.getSession().getAttribute(UserService.USERNAME);

        Object manger = request.getSession().getAttribute("manger");
        String token = request.getHeader("token");

        if (user == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"用户登录验证不正确");
            return ;
        }
        // 执行认证
        if (token == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"用户登录验证不正确");
            return ;
        }
        // 验证 token
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(user.getPassword())).build();
        try {
            jwtVerifier.verify(token);
        } catch (JWTVerificationException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"用户登录验证不正确");
            return;
        }
        try {
            System.err.println("开始处理摆摊物品");
            StallPool.getPool().guanbi();
            System.err.println("开始保存擂台赛积分数据");
            LTSUtil.getLtsUtil().BCLts();
        } catch (Exception var13) {
            var13.printStackTrace();
        }

        try {
            Thread.sleep(2000L);
        } catch (Exception var12) {
            var12.printStackTrace();
        }

        try {
            BangFileSystem.getBangFileSystem().DataSaving(BangBattlePool.getBangBattlePool());
            GangUtil.upGangs(false);
        } catch (Exception var11) {
            var11.printStackTrace();
        }

        System.err.println("开始备份玩家数据");
        Iterator entries = GameServer.getAllLoginRole().entrySet().iterator();

        while(entries.hasNext()) {
            Entry entrys = (Entry)entries.next();
            LoginResult loginResult = (LoginResult)entrys.getValue();
            if (loginResult != null) {
                try {
                    loginResult.setUptime(String.valueOf(System.currentTimeMillis()));
                    RoleData roleData = RolePool.getRoleData(loginResult.getRole_id());
                    roleData.roleRecover(loginResult);
                    // 保存前重新计算 HP/MP 最大值，确保称号等属性加成被正确应用
                    // 修复：只在 HP/MP 为 0 或负数时补满（升级/转生等异常情况）
                    // 否则保持当前实际 HP/MP 值，避免理论最大值覆盖实际当前值（战斗中受伤、时效药等状态）
                    try {
                        come.tool.FightingData.ManData manData = new come.tool.FightingData.ManData(0, 0);
                        come.tool.Calculation.CalculationUtil.loadRoleBattle(manData, loginResult, roleData, null, null, null, null, null);
                        if (manData.getHp_z() != null && (loginResult.getHp() == null || loginResult.getHp().longValue() <= 0)) {
                            loginResult.setHp(manData.getHp_z());
                        }
                        if (manData.getMp_z() != null && (loginResult.getMp() == null || loginResult.getMp().longValue() <= 0)) {
                            loginResult.setMp(manData.getMp_z());
                        }
                        // 修复：同步到 Redis 缓存，确保数据一致性
                        org.come.redis.RedisControl.insertKeyT(org.come.redis.RedisParameterUtil.COPY_LOGIN, 
                            loginResult.getRole_id().toString(), loginResult);
                        org.come.redis.RedisControl.insertController(org.come.redis.RedisParameterUtil.COPY_LOGIN, 
                            loginResult.getRole_id().toString(), org.come.redis.RedisControl.CALTER);
                    } catch (Exception calcEx) {
                        // 计算失败时继续使用原值保存
                    }
                    RedisControl.addUpDate(loginResult, roleData.getPackRecord());
                } catch (Exception var10) {
                    System.err.println("处理玩家备份失败" + loginResult.getRolename());
                    var10.printStackTrace();
                }
            }
        }

        RedisEqualWithSqlThread.AllToDataRole();

        try {
            Thread.sleep(10000L);
            RedisEqualWithSqlThread.AllToDatabase();
        } catch (Exception var9) {
            var9.printStackTrace();
        }

        if (WriteOut.buffer != null) {
            WriteOut.writeTxtFile(WriteOut.buffer.toString());
        }

        try {
            if (LaborScene.laborScene != null) {
                LaborScene.Save(true);
            }

            CreateTextUtil.createFile(ReadExelTool.class.getResource("/").getPath() + "hatch.txt", HatchvalueAction.hatch.toString().getBytes());
            Scene scene = SceneUtil.getScene(1009);
            if (scene != null) {
                RCScene rcScene = (RCScene)scene;
                CreateTextUtil.createFile(ReadExelTool.class.getResource("/").getPath() + "bbRecord.txt", GsonUtil.getGsonUtil().getgson().toJson(rcScene.getBbRecord()).getBytes());
            }

            scene = SceneUtil.getScene(1010);
            if (scene != null) {
                PKLSScene pklsScene = (PKLSScene)scene;
                lsteamBean lsteamBean = new lsteamBean();
                lsteamBean.setLSTeams(pklsScene.getLSTeams());
                CreateTextUtil.createFile(ReadExelTool.class.getResource("/").getPath() + "lsteam.txt", GsonUtil.getGsonUtil().getgson().toJson(lsteamBean).getBytes());
            }

            CreateTextUtil.createFile(ReadExelTool.class.getResource("/").getPath() + "money.txt", GsonUtil.getGsonUtil().getgson().toJson(MonitorUtil.getMoney()).getBytes());
            RefreshMonsterTask.upBuyCount(-1, false);
        } catch (IOException var8) {
            var8.printStackTrace();
        }

        returnData.put("status", 200);
        returnData.put("mes", "系统将会在5S后完成数据保存");
        PrintWriter printWriter = response.getWriter();
        printWriter.write(GsonUtil.getGsonUtil().getgson().toJson(returnData));
        printWriter.flush();
        printWriter.close();
    }
}
