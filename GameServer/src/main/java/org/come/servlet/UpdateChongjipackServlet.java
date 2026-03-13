package org.come.servlet;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.gl.model.User;
import com.gl.service.UserService;
import org.come.ApiValid;
import org.come.bean.managerTable;
import org.come.entity.ChongjipackBean;
import org.come.until.AllServiceUtil;
import org.come.until.GsonUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;


public class UpdateChongjipackServlet extends HttpServlet {

    @Override
    public void destroy() {
        super.destroy();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    @Override
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
        //managerTable manege= (managerTable)request.getSession().getAttribute("xy2o");
        //String token = request.getHeader("manage_token");
        //String VALID_NAME = request.getParameter(ApiValid.VALID_NAME);
        //if(null == VALID_NAME || !VALID_NAME.equals(ApiValid.VALID_VALUE) || manege ==null || !ApiValid.vaildToken(token,manege.getUsername())){
        //System.out.println("【UpdateChongjipackServlet】非法请求！！,已踢出");
        //  return ;
        // }
        // response.setHeader("manage_token",ApiValid.getToken(manege.getUsername()));
        Map<String,Object> returnData = new HashMap<>();

        String chongjipack = request.getParameter("chongjipack");
        String type = request.getParameter("type");
        ChongjipackBean chongjipackBean = GsonUtil.getGsonUtil().getgson().fromJson(chongjipack, ChongjipackBean.class);
        if(type.equals("insert")){
            AllServiceUtil.getChongjipackServeice().insertChongjipack(chongjipackBean);
        }else if(type.equals("del")){
            String id = request.getParameter("id");
            AllServiceUtil.getChongjipackServeice().deleteChongjipack(Integer.parseInt(id));
        } else{
            AllServiceUtil.getChongjipackServeice().updateChongjipack(chongjipackBean);
        }

        returnData.put("status", 200);
        PrintWriter printWriter = response.getWriter();
        printWriter.write(GsonUtil.getGsonUtil().getgson().toJson(returnData));
        printWriter.flush();
        printWriter.close();

    }


}