package come.tool.newTeam;

import io.netty.channel.ChannelHandlerContext;
import org.come.action.IAction;
import org.come.bean.LoginResult;
import org.come.handler.SendMessage;
import org.come.protocol.Agreement;
import org.come.server.GameServer;
import org.come.until.GsonUtil;

public class TeamflshAction implements IAction {
    @Override
    public void action(ChannelHandlerContext ctx, String message) {
        // TODO Auto-generated method stub
        LoginResult loginResult = GameServer.getAllLoginRole().get(ctx);
        TeamBean teamBean=null;
        if (loginResult.getTroop_id()!=null)
            teamBean =TeamUtil.getTeam(loginResult.getTroop_id());
        String team = loginResult.getTeam();
        String[] itemRoles = team.split("\\|");
        if (teamBean==null)return;
        for (String itemRole : itemRoles) {
            ChannelHandlerContext teamCtx = GameServer.getRoleNameMap().get(itemRole);
            if (teamCtx != null) {
                //修改每个角色的飞行数据
                LoginResult teamItemLoginResult = GameServer.getAllLoginRole().get(teamCtx);
                for (int k=0;k<=teamBean.getTeams().size()-1;k++){
                    if (teamBean.getTeams().get(k).getName().equals(teamItemLoginResult.getRolename())){
                        teamBean.getTeams().get(k).setGrade(teamItemLoginResult.getGrade());
                    }
                }

            }
        }
        SendMessage.sendMessageByRoleName(loginResult.getRolename(), Agreement.getAgreement().Teamflsh(GsonUtil.getGsonUtil().getgson().toJson(teamBean)));
    }
}
