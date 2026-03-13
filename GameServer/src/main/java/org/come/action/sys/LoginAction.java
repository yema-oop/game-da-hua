package org.come.action.sys;

import com.gl.controller.AutoContext;
import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.StringUtils;
import org.come.action.IAction;
import org.come.bean.LoginUserBean;
import org.come.entity.Ipaddressmac;
import org.come.entity.LoginResultBean;
import org.come.entity.RegionResult;
import org.come.entity.UserTable;
import org.come.handler.SendMessage;
import org.come.protocol.Agreement;
import org.come.server.GameServer;
import org.come.tool.WriteOut;
import org.come.until.AllServiceUtil;
import org.come.until.GsonUtil;

public class LoginAction implements IAction {
    public static Integer LOGIN_MAX = 5; //崭新 登陆账号限制

    @Override
    public void action(ChannelHandlerContext ctx, String message) {
        if (GameServer.OPEN) return;
        String clientIP = getIP(ctx);
        List<Ipaddressmac> ipaddressmac = AllServiceUtil.getIpaddressmacService().selectIpaddressmac(clientIP);
        if (ipaddressmac.size() != 0) {
            String msg = Agreement.getAgreement().erroLoginAgreement();
            SendMessage.sendMessageToSlef(ctx, msg);
            return;
        }
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
        // 将用户名密码写入登入bean中
        LoginUserBean loginUserBean = GsonUtil.getGsonUtil().getgson().fromJson(message, LoginUserBean.class);
        // 用户名密码
        String username = loginUserBean.getUsername();
        String userpwd = loginUserBean.getPassword();
        String serverMes = loginUserBean.getServerMeString();
        serverMes = null;
        if (userpwd == null || userpwd.equals("")) {
            return;
        }
        // 查询是否有该用户
        //UserTable userTable = AllServiceUtil.getUserTableService().findUserByUserNameAndUserPassword(username, userpwd);
        UserTable userTable = AllServiceUtil.getUserTableService().findUserForAccountUser(username, userpwd);
        if (userTable != null) {
            // 没有手机号提示绑定手机号
			/*if( userTable.getSafety() == null ){
				SendMessage.sendMessageToSlef(ctx,Agreement.getAgreement().bindingMobileAgreement("绑定"));
				return;
			}*/
            // 如果有人已在线提示对方下线
            String msgs = Agreement.getAgreement().inlineLoginAgreement();
            // 查找对方输出流
            if (GameServer.getInlineUserNameMap().get(username) != null) {
                ChannelHandlerContext ctx2 = GameServer.getInlineUserNameMap().get(username);
                if (ctx2 != ctx || GameServer.getAllLoginRole().get(ctx2) != null) {
                    SendMessage.sendMessageByUserName(username, msgs);
                    GameServer.userDown(GameServer.getInlineUserNameMap().get(username));
                }
            }
            // 添加用户名和通道集合
            GameServer.getInlineUserNameMap().put(username, ctx);
            GameServer.getSocketUserNameMap().put(ctx, username);
            // 获得用户的角色
			/*List<LoginResult> list = AllServiceUtil.getUserTableService().findRoleByUserNameAndPassword(username, userpwd, serverMes);
			// 判断用户角色是否为空，为空返回用户信息
			if(null == list || list.size() == 0){
				list = new ArrayList<LoginResult>();
				// 将用户信息赋值
				LoginResult loginResult = new LoginResult();
				loginResult.setUser_id(userTable.getUser_id());
				loginResult.setUserName(username);
				loginResult.setUserPwd(userpwd);
				list.add(loginResult);
			}*/
//			UserRoleArrBean userbean=new UserRoleArrBean();

            String flag = userTable.getFlag();
            /*if (GameServer.isCode) {
             *//**HGC--2019-11-16*//*
				if (userTable.getPhonenumber() != null && !"".equals(userTable.getPhonenumber())) {
					String sendUNtil=sendsms.sendUNtil(userTable.getPhonenumber());
//					System.out.println("sendUNtil:"+sendUNtil);
					if (sendUNtil.equals("error")) {
//						userbean.setPhonestatues(-1);
						flag = "-1";
					} else if (sendUNtil.equals("logup")) {
//						userbean.setPhonestatues(-1);
						flag = "-1";
					} else {
//						userbean.setPhonestatues(Integer.parseInt(sendUNtil));
						flag = Integer.parseInt(sendUNtil) + "";
					}
				}	
			}*/
//			userbean.setList(list);
//			String mes = Agreement.getAgreement().successLoginAgreement(GsonUtil.getGsonUtil().getgson().toJson(userbean));
//			SendMessage.sendMessageToSlef(ctx, mes);

            //String selectAtid = AllServiceUtil.getOpenareatableService().selectAtid(userTable.getQid() + "");
            //String mes = Agreement.getAgreement().successLoginAgreement(userTable.getUser_id() + "|" + selectAtid + "|" + flag);
            LoginResultBean loginResultBean = new LoginResultBean();
            loginResultBean.setUsername(userTable.getUsername());
            loginResultBean.setUserPwd(userTable.getUserpwd());
            loginResultBean.setFlag(flag);
            loginResultBean.setTuijianma(userTable.getTuiji());
            List<RegionResult> resultList = AllServiceUtil.getUserTableService().queryResultList();
            loginResultBean.setResultList(resultList);
            String mes = Agreement.getAgreement().successLoginAgreement(userTable.getUsername() + "|" + userTable.getUserpwd() + "|" + flag + "|" + 0);
            SendMessage.sendMessageToSlef(ctx, mes);
            String mes1 = Agreement.getAgreement().successLoginAgreement(GsonUtil.getGsonUtil().getgson().toJson(loginResultBean));
            SendMessage.sendMessageToSlef(ctx, mes1);

        } else {
            String msg = Agreement.getAgreement().erroLoginAgreement();
            SendMessage.sendMessageToSlef(ctx, msg);
        }
    }

    /**
     * 获取IP
     */
    public static String getIP(ChannelHandlerContext ctx) {
        String IP = null;
        try {
            InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
            IP = insocket.getAddress().getHostAddress();
        } catch (Exception e) {
            // TODO: handle exception
            WriteOut.addtxt("IP获取异常", 9999);
        }
        return IP;
    }
}
