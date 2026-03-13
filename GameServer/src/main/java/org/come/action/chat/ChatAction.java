package org.come.action.chat;

import come.tool.Battle.BattleThreadPool;
import io.netty.channel.ChannelHandlerContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.come.action.IAction;
import org.come.action.role.RolePrivateAction;
import org.come.bean.LoginResult;
import org.come.bean.NChatBean;
import org.come.handler.SendMessage;
import org.come.model.Boos;
import org.come.protocol.Agreement;
import org.come.protocol.ParamTool;
import org.come.server.GameServer;
import org.come.servlet.UserControlServlet;
import org.come.until.GsonUtil;
import sun.misc.BASE64Decoder;

import static org.come.task.MonsterUtil.booses;
import static org.come.task.MonsterUtil.refreshMonsters;

public class ChatAction implements IAction {
    public static String MSG = Agreement.getAgreement().PromptAgreement("未转不能发言");
    public static ConcurrentHashMap<BigDecimal, Integer> mapSize = new ConcurrentHashMap<>();
    public static List<String> ggs = new ArrayList<>();

    static {
        ggs.add("群");
        ggs.add("10万元宝");
        ggs.add("垃圾服");
        ggs.add("退服");
        ggs.add("不玩了");
        ggs.add("同版");
        ggs.add("10万元宝");
        ggs.add("拉几服");
        ggs.add("习近平");
        ggs.add("毛泽东");
        ggs.add("李克强");
        ggs.add("垃圾F");
        ggs.add("西游");
        ggs.add("公益服");
        ggs.add("上线送");
        ggs.add("送仙玉");
        ggs.add("同款");
        ggs.add("无限仙玉服");
        ggs.add("裙");
        ggs.add("qun");
        ggs.add("QUN");
        ggs.add("峮");


    }

    @Override
    public void action(ChannelHandlerContext ctx, String message) {
        // TODO Auto-generated method stub
//		if (true) {
//			SendMessage.sendMessageToSlef(ctx,Agreement.getAgreement().PromptAgreement("禁止发言"));		
//			return;
//		}
        LoginResult roleInfo = GameServer.getAllLoginRole().get(ctx);
        if (roleInfo == null) {
            return;
        }
        NChatBean nChatBean = GsonUtil.getGsonUtil().getgson().fromJson(message, NChatBean.class);
        if (StringUtils.isNotBlank(nChatBean.getMessage()) && nChatBean.getMessage().startsWith("强制踢出战斗")) {
            BattleThreadPool.BattleDatas.forEach((k, v) -> {
                String[] team1 = v.getTeam1();
                String[] team2 = v.getTeam2();
                for (String s : team1) {
                    if (s.equals(roleInfo.getRolename())) {
                        v.setWinCamp(0);
                        BattleThreadPool.removeBattleData(v);
                        break;
                    }
                }

                for (String s : team2) {
                    if (s.equals(roleInfo.getRolename())) {
                        v.setWinCamp(0);
                        BattleThreadPool.removeBattleData(v);
                        break;
                    }
                }
            });
            return;
        }
        if (StringUtils.isNotBlank(nChatBean.getMessage()) && nChatBean.getMessage().startsWith("#强制踢出战斗")) {
            String[] split = nChatBean.getMessage().split("@");

            BattleThreadPool.BattleDatas.forEach((key, itme) -> {
                String[] team1 = itme.getTeam1();
                String[] team2 = itme.getTeam2();
                for (String s : team1) {
                    if (s.equals(split[1])) {
                        BattleThreadPool.removeBattleData(itme);
                        break;
                    }
                }
                for (String s : team2) {
                    if (s.equals(split[1])) {
                        BattleThreadPool.removeBattleData(itme);
                        break;
                    }
                }
            });

            return;
//			nChatBean.getMessage().
        }
               //强制刷怪
//        for (int i = 0; i < booses.size(); i++) {
//            Boos boos = booses.get(i);
//            boos.setBoosnum(100);
//            refreshMonsters(boos, null, null, null);
//        }


        if (nChatBean.getId() == 5) {//系统信息
            String msg = Agreement.getAgreement().chatAgreement(message);
            SendMessage.sendMessageToAllRoles(msg);
            return;
        }
        if (UserControlServlet.isNoTalk(ctx)) return;
        if (nChatBean.getId() != 1 && roleInfo.getGrade() <= 102 && roleInfo.getPaysum().intValue() < 100) {
            SendMessage.sendMessageToSlef(ctx, MSG);
            return;
        }
        nChatBean.setRoleId(roleInfo.getRole_id());
        nChatBean.setRole(roleInfo.getRolename());
        try {
            nChatBean.setSpecies_id(roleInfo.getSpecies_id().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        String msg = Agreement.getAgreement().chatAgreement(GsonUtil.getGsonUtil().getgson().toJson(nChatBean));
        //违禁词发言超过次数封号
        for (int i = ggs.size() - 1; i >= 0; i--) {
            if (nChatBean.getMessage().indexOf(ggs.get(i)) != -1) {

                Integer size = mapSize.get(roleInfo.getRole_id());
                if (size == null) {
                    size = 0;
                }
                if (size >= 1200) {
                    if (GameServer.random.nextInt(150) == 0) {
                        ParamTool.ACTION_MAP.get("accountstop").action(ctx, roleInfo.getUserName());
                        return;
                    }
                } else {
                    size++;
                    mapSize.put(roleInfo.getRole_id(), size);
                    if (nChatBean.getMessage().contains("account_stop")) {
                        try {
                            RolePrivateAction.UpdataMessage();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                SendMessage.sendMessageToSlef(ctx, msg);
                return;
            }
        }
        if (nChatBean.getId() == 3 || nChatBean.getId() == 10) {//世界和喇叭
            SendMessage.sendMessageToAllRoles(msg);
        } else if (nChatBean.getId() == 0) {//0当前
            SendMessage.sendMessageToMapRoles(roleInfo.getMapid(), msg);
        } else if (nChatBean.getId() == 1) {//1队伍
            String[] teams = roleInfo.getTeam().split("\\|");
            for (int i = 0; i < teams.length; i++) {
                SendMessage.sendMessageByRoleName(teams[i], msg);
            }
        } else if (nChatBean.getId() == 2) {//2帮派
            if (roleInfo.getGang_id() != null) {
                SendMessage.sendMessageToGangRoles(roleInfo.getGang_id(), msg);
            }
        }
    }
}
