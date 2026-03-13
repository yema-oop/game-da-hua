package org.come.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.gl.model.User;
import com.gl.service.UserService;
import org.come.until.AllServiceUtil;
import org.come.until.GsonUtil;

public class SelectGoodsexchangeServlet extends HttpServlet {
    public void destroy() {
        super.destroy();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
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
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        Map returnData = new HashMap();
        List list = AllServiceUtil.getGoodsExchangeService().selectListAll();
        returnData.put("list", list);
        PrintWriter pwPrintWriter = response.getWriter();
        pwPrintWriter.write(GsonUtil.getGsonUtil().getgson().toJson(returnData));
        pwPrintWriter.flush();
        pwPrintWriter.close();
    }

    public void init() throws ServletException {
    }
}
