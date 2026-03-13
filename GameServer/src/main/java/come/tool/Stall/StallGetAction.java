package come.tool.Stall;

import io.netty.channel.ChannelHandlerContext;

import org.come.action.IAction;
import org.come.handler.SendMessage;
import org.come.protocol.Agreement;
import org.come.until.GsonUtil;

public class StallGetAction implements IAction {

    @Override
    public void action(ChannelHandlerContext ctx, String message) {
        // TODO Auto-generated method stub
        String[] mes = message.split("==");
        int id = Integer.parseInt(mes[0]);
        Stall stall = StallPool.getPool().getAllStall().get(id);
        Stall dt = new Stall();
        dt.setId(stall.getId());
        dt.setMapid(stall.getMapid());
        dt.setState(stall.getState());
        dt.setRole(stall.getRole());
        dt.setStall(stall.getStall());
        dt.setRoleid(stall.getRoleid());
        dt.setPets(stall.getPets());
        dt.setSpid(stall.getSpid());
        dt.setX(stall.getX());
        dt.setY(stall.getY());
        dt.setSellMsgs(stall.getSellMsgs());
        dt.setCollectMsg(stall.getCollectMsg());
        Commodity[] goodstables = stall.getGoodstables();
        Commodity[] dtg = new Commodity[24];
        if (goodstables != null && goodstables.length > 0) {
            if (mes[1].equals("0")) {
                for (int i = 0; i < goodstables.length; i++) {
                    if (goodstables[i] != null && goodstables[i].getShougou() == 0) {
                        dtg[i] = goodstables[i];
                    }
                }
                dt.setPets(stall.getPets());
            } else if (mes[1].equals("1")) {
                for (int i = 0; i < goodstables.length; i++) {
                    if (goodstables[i] != null && goodstables[i].getShougou() == 1) {
                        dtg[i] = goodstables[i];
                    }
                }
            }
        }
        if (stall != null) {
            dt.setGoodstables(dtg);
            SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().stallAgreement(GsonUtil.getGsonUtil().getgson().toJson(dt)));
        }
    }

}
