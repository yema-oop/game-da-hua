package org.come.action.role;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import org.come.action.IAction;
import org.come.bean.LoginResult;
import org.come.redis.RedisControl;
import org.come.redis.RedisParameterUtil;
import org.come.server.GameServer;
import java.util.HashMap;

/**
 * 修改角色名字
 * @author Administrator
 *
 */
public class ShortCutSkillUseAction implements IAction {

	@Override
	public void action(ChannelHandlerContext ctx, String message) {
		// 获取客户端发来的消息
		LoginResult roleInfo = GameServer.getAllLoginRole().get(ctx);
		HashMap<String, Object> shortCutSkill = new HashMap<>();
		try {
			shortCutSkill = JSON.parseObject(message, HashMap.class);
			RedisControl.insertKeyT(RedisParameterUtil.ShortSkillRedis + roleInfo.getRole_id(), "shortCut", shortCutSkill);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}
}
