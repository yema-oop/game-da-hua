package org.come.servlet;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.gl.model.User;
import com.gl.service.UserService;
import io.netty.channel.ChannelHandlerContext;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.come.handler.SendMessage;
import org.come.protocol.Agreement;
import org.come.server.GameServer;
import org.come.until.GsonUtil;

public class SetPlayerOffLineServlet extends HttpServlet {
    public void destroy() {
        super.destroy();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User)  request.getSession().getAttribute(UserService.USERNAME);

        Object manger = request.getSession().getAttribute("manger");
        String token = request.getHeader("token");

        if (user == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"用户登录验证不正确");
            return ;
        }
        // 执行认证
        if (token == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"用户登录验证不正确");
            return ;
        }
        // 验证 token
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(user.getPassword())).build();
        try {
            jwtVerifier.verify(token);
        } catch (JWTVerificationException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"用户登录验证不正确");
            return;
        }
        Map returnData = new HashMap();
        Iterator entries = GameServer.getRoleNameMap().entrySet().iterator();

        while(entries.hasNext()) {
            Entry entrys = (Entry)entries.next();
            SendMessage.sendMessageToSlef((ChannelHandlerContext)entrys.getValue(), Agreement.getAgreement().serverstopAgreement());
        }

        returnData.put("status", 200);
        PrintWriter printWriter = response.getWriter();
        printWriter.write(GsonUtil.getGsonUtil().getgson().toJson(returnData));
        printWriter.flush();
        printWriter.close();
    }
}
