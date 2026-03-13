package org.come.servlet;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.gl.model.User;
import com.gl.service.UserService;
import come.tool.Scene.LaborDay.LaborScene;
import come.tool.Stall.AssetUpdate;
import org.come.ApiValid;
import org.come.bean.LoginResult;
import org.come.bean.managerTable;
import org.come.handler.SendMessage;
import org.come.protocol.Agreement;
import org.come.until.AllServiceUtil;
import org.come.until.GsonUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;


public class LaborClearServlet extends HttpServlet {

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
        Map<String,Object> returnData = new HashMap<>();
        String roleId = request.getParameter("roleId");
        BigDecimal roleIdDec = null;
        try{
            roleIdDec = new BigDecimal(roleId);
        }catch (Exception e){
            returnData.put("status", 500);
            PrintWriter printWriter = response.getWriter();
            printWriter.write(GsonUtil.getGsonUtil().getgson().toJson(returnData));
            printWriter.flush();
            printWriter.close();
            return ;
        }
        //删除该角色抽奖次数
        LaborScene.clearRoleMapByRoleId(roleIdDec);
        AssetUpdate assetUpdate=new AssetUpdate();
        assetUpdate.setType(25);
        assetUpdate.setMsg("你的抽奖次数被清理为0次");
        LoginResult loginResult= AllServiceUtil.getRoleTableService().selectRoleByRoleId(roleIdDec);
        SendMessage.sendMessageByRoleName(loginResult.getRolename(), Agreement.getAgreement().assetAgreement(GsonUtil.getGsonUtil().getgson().toJson(assetUpdate)));
        returnData.put("status", 200);
        PrintWriter printWriter = response.getWriter();
        printWriter.write(GsonUtil.getGsonUtil().getgson().toJson(returnData));
        printWriter.flush();
        printWriter.close();

    }


}
