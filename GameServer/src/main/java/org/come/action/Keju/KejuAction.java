package org.come.action.Keju;

import come.tool.FightingData.Battlefield;
import io.netty.channel.ChannelHandlerContext;
import org.come.action.IAction;
import org.come.entity.Keju;
import org.come.handler.SendMessage;
import org.come.protocol.Agreement;
import org.come.until.GsonUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class KejuAction  implements IAction {
    public static boolean iskeju=false;
    public static  List<String[]>role= new ArrayList<>();
    public void action(ChannelHandlerContext ctx, String message){
        if (!iskeju){

            SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().PromptAgreement("#R请先在魏征处领取科举任务"));

        }

       // role.clear();

        String[] m=message.split("-");


        if (role.size()==0){
            String[] v=Keju.result[Battlefield.random.nextInt(Keju.result.length-1)];
            KejuhuidaAction.vs=v;
            String msg = Agreement.getAgreement().KejuAgreement(GsonUtil.getGsonUtil().getgson().toJson(v));
            SendMessage.sendMessageToSlef(ctx, msg);
            role.add(0,m);
            role.get(0)[1]= String.valueOf(Integer.parseInt(role.get(0)[1])+1);
            role.get(0)[2]=v[6];
           if (role.get(0)[5].equals("0")){
               Date now = new Date();
               SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd-HH-mm-ss");//
               String hehe = dateFormat.format( now );
               role.get(0)[5]=hehe;
           }

            return;
        }else {
          for (int i=0;i<=role.size()-1;i++){
              if (role.get(i)[0].equals(m[0])){
                  if (!role.get(i)[1].equals(m[1])){
                      if (m[1].equals("10019")){
                          SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().PromptAgreement("#R本次乡试答题完毕，共耗时"+role.get(i)[5]+"秒！"));

                      }else SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().PromptAgreement("#R请前往下一个考官出继续答题！"));


                      return;
                  }else {
                      String[] v=Keju.result[Battlefield.random.nextInt(Keju.result.length-1)];
                      KejuhuidaAction.vs=v;
                      String msg = Agreement.getAgreement().KejuAgreement(GsonUtil.getGsonUtil().getgson().toJson(v));
                      SendMessage.sendMessageToSlef(ctx, msg);
                      //role.get(i)[1]=v[1];
                      role.get(i)[1]= String.valueOf(Integer.parseInt(role.get(i)[1])+1);
                      role.get(i)[2]=v[6];
                      return;
                  }
              }
          }
        }
        String[] v=Keju.result[Battlefield.random.nextInt(Keju.result.length-1)];
        KejuhuidaAction.vs=v;
        String msg = Agreement.getAgreement().KejuAgreement(GsonUtil.getGsonUtil().getgson().toJson(v));
        SendMessage.sendMessageToSlef(ctx, msg);
       // role[role.length][1]=v[1];

        role.add(role.size(),m);
        role.get(role.size())[2]=v[6];
        role.get(role.size()-1)[1]= String.valueOf(Integer.parseInt(role.get(role.size()-1)[1])+1);
        if (role.get(role.size()-1)[5].equals("0")){
            Date now = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd-HH-mm-ss");//
            String hehe = dateFormat.format( now );
            role.get(role.size()-1)[5]=hehe;
        }

    }
}
