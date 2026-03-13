package come.tool.Stall;

import io.netty.channel.ChannelHandlerContext;
import org.come.action.IAction;
import org.come.server.GameServer;
import org.come.until.GsonUtil;

public class StallSellAction implements IAction{
	@Override
	public void action(ChannelHandlerContext ctx, String message) {
		// TODO Auto-generated method stub
//		if (true) {
//        	SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().PromptAgreement("禁止物品流通")); 
//			return;
//		}
		try {
			StallSell stallSell = GsonUtil.getGsonUtil().getgson().fromJson(message,StallSell.class);
			StallPool.getPool().sellStall(GameServer.getAllLoginRole().get(ctx), stallSell);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
}
