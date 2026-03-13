package org.come.action.Keju;

import come.tool.Battle.BattleMixDeal;
import come.tool.Good.DropUtil;
import io.netty.channel.ChannelHandlerContext;
import org.come.action.IAction;
import org.come.bean.LoginResult;
import org.come.handler.SendMessage;
import org.come.model.Robots;
import org.come.protocol.Agreement;
import org.come.server.GameServer;
import org.come.until.GsonUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

public class KejuhuidaAction  implements IAction {
    public static String []vs=null;
    public void action(ChannelHandlerContext ctx, String message){
    String[] m=message.split("-");
    int time=0;
    for (int i=0;i<=KejuAction.role.size()-1;i++){

        if (KejuAction.role.get(i)[0].equals(m[1])){
            if (KejuAction.role.get(i)[2].equals(m[0])){
                reward(ctx);
                  KejuAction.role.get(i)[3]= String.valueOf(Integer.parseInt(KejuAction.role.get(i)[3])+1);
                  if (Integer.parseInt(KejuAction.role.get(i)[3])+Integer.parseInt(KejuAction.role.get(i)[4])==20){
                          Date now = new Date();
                          SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd-HH-mm-ss");//
                          String hehe = dateFormat.format( now );
                          String[] k=hehe.split("-");
                          String[] l=KejuAction.role.get(i)[5].split("-");
                          time=(Integer.parseInt(k[1])-Integer.parseInt(l[1]))*60*60+(Integer.parseInt(k[2])-Integer.parseInt(l[2]))*60+(Integer.parseInt(k[3])-Integer.parseInt(l[3]));
                      KejuAction.role.get(i)[5]= String.valueOf(time);
                      SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().PromptAgreement("#G恭喜你回答正确！#Y本次乡试答题完毕！共用时"+time+"秒！"));
                  }else  SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().PromptAgreement("#G恭喜你回答正确！#Y请前往下一个官员处继续答题"));

                  return;
            }else {

                 KejuAction.role.get(i)[4]= String.valueOf((Integer.parseInt(KejuAction.role.get(i)[4])+1));
                if (Integer.parseInt(KejuAction.role.get(i)[3])+Integer.parseInt(KejuAction.role.get(i)[4])==20){
                    Date now = new Date();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd-HH-mm-ss");//
                    String hehe = dateFormat.format( now );
                    String[] k=hehe.split("-");
                    String[] l=KejuAction.role.get(i)[5].split("-");
                    time=(Integer.parseInt(k[1])-Integer.parseInt(l[1]))*60*60+(Integer.parseInt(k[2])-Integer.parseInt(l[2]))*60+(Integer.parseInt(k[3])-Integer.parseInt(l[3]));
                    KejuAction.role.get(i)[5]= String.valueOf(time);
                    SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().PromptAgreement("#G回答失败！#Y本次乡试答题完毕！共用时"+time+"秒！"));
                }else  SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().PromptAgreement("#R回答失败！#Y请前往下一个官员处继续答题"));
                return;
            }

        }

    }

    }
    public static  void reward(ChannelHandlerContext ctx){
        Robots robots= GameServer.getAllRobot().get("10003");
        LoginResult loginResult = GameServer.getAllLoginRole().get(ctx);
        if (robots!=null) {

            String value= BattleMixDeal.isLvl(loginResult.getGrade(), robots.getLvls());
            if (value!=null) {SendMessage.sendMessageToSlef(ctx,value);return;}
            DropUtil.getDrop(loginResult, robots.getRobotreward(), robots.getUnknow(),21,1D,null);
        }








    }
}
