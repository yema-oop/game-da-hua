

package org.come.action.role;

import io.netty.channel.ChannelHandlerContext;
import org.come.action.IAction;
import org.come.bean.AutoSwitchBean;
import org.come.bean.LoginResult;
import org.come.redis.RedisParameterUtil;
import org.come.redis.RedisPoolUntil;
import org.come.server.GameServer;
import org.come.until.GsonUtil;
import redis.clients.jedis.Jedis;

//一键换装
public class AutoSwitchAction implements IAction {
    public AutoSwitchAction() {
    }

    public void action(ChannelHandlerContext ctx, String message) {
        AutoSwitchBean autoSwitchBean = GsonUtil.getGsonUtil().getgson().fromJson(message, AutoSwitchBean.class);
        LoginResult roleInfo = GameServer.getAllLoginRole().get(ctx);
        Jedis jedis = RedisPoolUntil.getJedis();
        jedis.hset(RedisParameterUtil.autoSwitch, roleInfo.getRole_id().toString(), GsonUtil.getGsonUtil().getgson().toJson(autoSwitchBean));
//        jedis.hset(RedisParameterUtil.autoSwitch, roleInfo.getRole_id().toString(), GsonUtil.getGsonUtil().getgson().toJson(autoSwitchBean));
        RedisPoolUntil.returnResource(jedis);
    }
}
