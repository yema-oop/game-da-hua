package org.come.action.role;

import come.tool.Role.RoleData;
import come.tool.Role.RolePool;
import io.netty.channel.ChannelHandlerContext;
import org.come.action.IAction;
import org.come.bean.LoginResult;
import org.come.entity.RoleTable;
import org.come.entity.UserTable;
import org.come.handler.SendMessage;
import org.come.protocol.Agreement;
import org.come.server.GameServer;
import org.come.until.AllServiceUtil;
import org.come.until.GsonUtil;

import java.io.PrintWriter;

public class DeleRoleAction implements IAction {
    public void action(ChannelHandlerContext ctx, String message){
        int m=0;
        //LoginResult createRole = GsonUtil.getGsonUtil().getgson().fromJson(message, LoginResult.class);
        RoleTable role = AllServiceUtil.getRoleTableService().selectRoleTableByRoleName(message);
      if (role!=null){
           m= AllServiceUtil.getUserTableService().delectUsertableForUsername(role.getRolename());

      }
       if (m>0){
           //String mes = Agreement.getAgreement().errorCreateAgreement();
           SendMessage.sendMessageToSlef(ctx, "删除角色成功");


       }
    }
}
