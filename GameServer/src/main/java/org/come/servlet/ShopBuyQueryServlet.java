package org.come.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

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
import org.come.extInterBean.ShopBuyRecordReqBean;
import org.come.extInterBean.ShopBuyRecordResultBean;
import org.come.until.AllServiceUtil;
import org.come.until.GsonUtil;

public class ShopBuyQueryServlet extends HttpServlet {

	public ShopBuyQueryServlet() {
		super();
	}

	@Override
	public void destroy() {
		super.destroy();
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
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
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		response.setHeader("Access-Control-Allow-Origin", "*");

		// 购买类型
		String buyType = request.getParameter("buyType");
		// 记录时间
		String recordTime = request.getParameter("recordTime");
		// 购买账号
		String userName = request.getParameter("userName");
		// 购买角色名
		String roleName = request.getParameter("roleName");
		// 当前页
		String nowPage = request.getParameter("nowPage");

		ShopBuyRecordReqBean reqBean = new ShopBuyRecordReqBean();
		reqBean.setBuyType(buyType);
		reqBean.setRecordTime(recordTime);
		reqBean.setUserName(userName);
		reqBean.setRoleName(roleName);
		reqBean.setNowPage(nowPage);

		List<ShopBuyRecordResultBean> selectShopBuyRecord = AllServiceUtil.getAppVersionService().selectShopBuyRecord(reqBean);

		// 返回给用户信息
		PrintWriter pwPrintWriter = response.getWriter();
		pwPrintWriter.write(GsonUtil.getGsonUtil().getgson().toJson(selectShopBuyRecord));
		pwPrintWriter.flush();
		pwPrintWriter.close();

	}

	@Override
	public void init() throws ServletException {
		// Put your code here
	}

}
