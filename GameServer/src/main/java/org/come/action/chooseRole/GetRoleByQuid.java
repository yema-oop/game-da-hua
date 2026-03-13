package org.come.action.chooseRole;

import io.netty.channel.ChannelHandlerContext;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang.StringUtils;
import org.come.action.IAction;
import org.come.bean.LoginResult;
import org.come.entity.*;
import org.come.handler.SendMessage;
import org.come.protocol.Agreement;
import org.come.server.GameServer;
import org.come.until.AllServiceUtil;
import org.come.until.GsonUtil;

/**
 *
 * @author zz
 * @time 2019年7月17日10:37:02
 *
 */
public class GetRoleByQuid implements IAction {

    @Override
    public void action(ChannelHandlerContext ctx, String message) {
        // TODO Auto-generated method stub
        if (GameServer.OPEN)
            return;

        // 信息解析
        ChooseArea mes = GsonUtil.getGsonUtil().getgson().fromJson(message, ChooseArea.class);
        Integer userId = AllServiceUtil.getRegionService().findUserIdIs(mes.getUid());
        if (userId == null) {
            AllServiceUtil.getRegionService().addUserTable(mes);
        }
        BigDecimal uid=null;
        // 用户id
        if (StringUtils.isBlank(mes.getUid())){//mao 登录报错
            return;
        }
        uid = new BigDecimal(mes.getUid());
        // 区id
        if(!Objects.isNull(mes.getQid())){
            Integer qid = Integer.valueOf(mes.getQid());
        }

        // -- 20200212 zrikka
        // 通过 区id 获取 总区id
		/*String belongId = AllServiceUtil.getOpenareatableService().selectBelong(qid + "");
		if (belongId == null) {
			return;
		}*/
        // 通过用户id和 总区id 查询 所有角色 -- 20200212 zrikka
        List<RoleTableNew> role = AllServiceUtil.getRegionService().selectRole(uid, null);
        // 查询是否有该用户
        UserTable userTable = AllServiceUtil.getUserTableService().findUserByUserNameAndUserPassword(mes.getUsername(), mes.getPassword());
//        if (userTable==null||userTable.getActivity()==1){
//            String msg = Agreement.getAgreement().erroLoginAgreement("#G账号存在非法物资！#R以封号处理！#Y请联系管理员说明情况，给与解封。！！！错误代码：1105");
//            SendMessage.sendMessageToSlef(ctx, msg);
//            return;
//        }
        RoleTableList list = new RoleTableList();
        list.setRoleList(role);
        String content = GsonUtil.getGsonUtil().getgson().toJson(list);

        // 选区返回属于该账号的所有角色数据
        String msg = Agreement.getAgreement().uidAndQidForRoleAgreement(content);
        SendMessage.sendMessageToSlef(ctx, msg);
    }
}
