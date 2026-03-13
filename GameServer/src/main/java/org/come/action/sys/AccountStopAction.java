package org.come.action.sys;

import io.netty.channel.ChannelHandlerContext;

import org.come.action.IAction;
import org.come.entity.Record;
import org.come.entity.UserTable;
import org.come.handler.SendMessage;
import org.come.protocol.Agreement;
import org.come.server.GameServer;
import org.come.tool.WriteOut;
import org.come.until.AllServiceUtil;
import sun.misc.BASE64Decoder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 账号封号
 * @author Administrator
 *
 */
public class AccountStopAction implements IAction {

	@Override
	public void action(ChannelHandlerContext ctx, String message) {
		// 修复：添加详细日志，追踪 accountstop 的调用来源
		org.come.tool.WriteOut.addtxt("accountstop 被调用！username=" + message + ", ctx=" + ctx, 9999);
		org.come.tool.WriteOut.addtxt("accountstop 调用堆栈:", 9999);
		Exception e = new Exception("accountstop call stack");
		e.printStackTrace();
		
		// GameServer.userDown(ctx);
		// // 断开连接
		// SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().serverstopAgreement());
		// // 获取账号名
		// UserTable table = new UserTable();
		// table.setUsername(message);
		// table.setActivity((short)1);
		// // 修改用户信息
		// AllServiceUtil.getUserTableService().updateUser(table);
		// AllServiceUtil.getRecordService().insert(new Record(5,message));
		
		// 修复：暂时禁用封号，只记录日志
		WriteOut.addtxt("警告：accountstop 被封禁，不会真正封号！username=" + message, 9999);
	}

}
