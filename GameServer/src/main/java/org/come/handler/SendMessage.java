package org.come.handler;

import io.netty.channel.ChannelHandlerContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.come.action.sys.ChangeRoleAction;
import org.come.bean.LoginResult;
import org.come.server.GameServer;

import come.tool.newGang.GangDomain;
import come.tool.newGang.GangUtil;
import sun.misc.BASE64Decoder;

public class SendMessage {
	static String FB="\n";
	public static String split = "XXXXXXXX";
	/**发送消息给自己*/
	public static void sendMessageToSlef(ChannelHandlerContext ctx, String msg) {
		if (ctx == null) {
			return;
		}

		if (msg.startsWith(split)) {
			LoginResult loginResult = GameServer.getAllLoginRole().get(ctx);
			if (loginResult != null && loginResult.getRole_id() != null) {
				msg = loginResult.getRole_id().toString() + msg;
			} else {
				if (ChangeRoleAction.currentLoginMap.get(ctx) != ctx) {
					return;
				}
				msg = msg.replace(split, "");
			}
			ChannelHandlerContext mappedCtx = ChangeRoleAction.reverseMap.get(ctx);
			if (mappedCtx != null) {
				mappedCtx.writeAndFlush(msg);
			} else {
				ctx.writeAndFlush(msg);
			}
		} else {
			ChannelHandlerContext mappedCtx = ChangeRoleAction.reverseMap.get(ctx);
			if (mappedCtx != null) {
				ChannelHandlerContext currentCtx = ChangeRoleAction.currentLoginMap.get(mappedCtx);
				if (currentCtx != null && currentCtx.equals(ctx)) {
					mappedCtx.writeAndFlush(msg);
				}
			} else {
				ctx.writeAndFlush(msg);
			}
		}
	}
	/**根据角色名字发送消息*/
	public static void sendMessageByRoleName(String roleName, String msg) {
		sendMessageToSlef(GameServer.getRoleNameMap().get(roleName), msg);
	}
	/**根据账号名发送消息*/
	public static void sendMessageByUserName(String userName, String msg) {
		sendMessageToSlef(GameServer.getInlineUserNameMap().get(userName), msg);
	}
	/**遍历共享集合写出数据,进行广播*/
	public static void sendMessageToAllRoles(String msg) {
		Iterator<Map.Entry<String, ChannelHandlerContext>> entries = GameServer.getRoleNameMap().entrySet().iterator();
		while (entries.hasNext()) {
			Entry<String,ChannelHandlerContext> entrys = entries.next();
			sendMessageToSlef(entrys.getValue(), msg);
		}
	}
	/**根据地图ID遍历地图共享集合写出数据*/
	public static void sendMessageToMapRoles(Long mapID, String msg) {
		Iterator<Map.Entry<String, ChannelHandlerContext>> entries = GameServer.getMapRolesMap().get(mapID).entrySet().iterator();
		while (entries.hasNext()) {
			Entry<String, ChannelHandlerContext> entrys = entries.next();
			sendMessageToSlef(entrys.getValue(), msg);
		}
	}
	/**根据地图ID遍历地图共享集合写出数据*/
	public static void sendMessageToMapRoles(ChannelHandlerContext ctx,Long mapID, String msg) {
		Iterator<Map.Entry<String, ChannelHandlerContext>> entries = GameServer.getMapRolesMap().get(mapID).entrySet().iterator();
		while (entries.hasNext()) {
			Entry<String, ChannelHandlerContext> entrys = entries.next();
			ChannelHandlerContext ctx2=entrys.getValue();
			if (ctx!=ctx2) {
				sendMessageToSlef(ctx2, msg);
			}
		}
	}
	/**根据帮派ID遍历帮派表共享集合写出数据*/
	public static void sendMessageToGangRoles(BigDecimal gangID, String msg) {
		GangDomain gangDomain=GangUtil.getGangDomain(gangID);
		if (gangDomain!=null) {
			Iterator<Entry<BigDecimal, ChannelHandlerContext>> entries = gangDomain.getRoleMap().entrySet().iterator();
			while (entries.hasNext()) {
				Entry<BigDecimal, ChannelHandlerContext> entrys = entries.next();
				sendMessageToSlef(entrys.getValue(), msg);
			}
		}
	}
	public static void sendMessageToGangMap(BigDecimal gangID,long mapID, String msg) {
		GangDomain gangDomain=GangUtil.getGangDomain(gangID);
		if (gangDomain!=null) {
			Iterator<Entry<BigDecimal, ChannelHandlerContext>> entries = gangDomain.getRoleMap().entrySet().iterator();
			while (entries.hasNext()) {
				Entry<BigDecimal, ChannelHandlerContext> entrys = entries.next();
				ChannelHandlerContext ctx=entrys.getValue();
				LoginResult roleInfo = GameServer.getAllLoginRole().get(ctx);
				if (roleInfo!=null&&roleInfo.getMapid()==mapID) {
					sendMessageToSlef(ctx, msg);
				}
			}
		}
	}
}
