package org.come.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.gl.controller.UserController;
import com.gl.model.Result;
import com.gl.model.User;
import com.gl.service.UserService;
import org.come.bean.Account;
import org.come.entity.UserTable;
import org.come.redis.RedisCacheUtil;
import org.come.until.AllServiceUtil;
import org.come.until.GsonUtil;

// 三端(/servlet/selectAccInfo)
public class SelectAccInfoServlet extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public SelectAccInfoServlet() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	@Override
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 *
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		doPost(request, response);
	}

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to
	 * post.
	 *
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
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
		// 获取用户名密码格式为 用户名|&|密码
		Result ipCheckResult = UserController.IPstop(request);
		if (ipCheckResult != null) {
			return;
		}
		String type = request.getParameter("type");
		String userName;
		if (!"autoReg".equals(type)) {
			if (type.equals("all")) {
				List list = AllServiceUtil.getUserTableService().findAllUser();
				if (list == null) {
					list = new ArrayList();
				}

				PrintWriter pwPrintWriter = response.getWriter();
				pwPrintWriter.write(GsonUtil.getGsonUtil().getgson().toJson(list));
				pwPrintWriter.flush();
				pwPrintWriter.close();
			} else if (type.equals("one")) {
				userName = request.getParameter("userName");
				UserTable result = AllServiceUtil.getUserTableService().findUserByUserNameAndUserPassword(userName, null);
				List list = new ArrayList();
				if (result != null) {
					list.add(result);
				}

				PrintWriter pwPrintWriter = response.getWriter();
				pwPrintWriter.write(GsonUtil.getGsonUtil().getgson().toJson(list));
				pwPrintWriter.flush();
				pwPrintWriter.close();
			}
		} else {
			userName = request.getParameter("info");
			Account ac = GsonUtil.getGsonUtil().getgson().fromJson(userName, Account.class);
			String username = ac.getAc_account();
			String password = ac.getAc_pasw();
			String safely = ac.getAc_safely();
			String userflag = ac.getAc_flag();
			String tuiji = ac.getAc_tuijian();
			String phone = ac.getAc_phone();
			String registerip = ac.getAc_regip();
			UserTable usertable = AllServiceUtil.getUserTableService().selectByFlag(userflag);
			if (usertable == null) {
				while(true) {
					UserTable result = AllServiceUtil.getUserTableService().findUserByUserNameAndUserPassword(username, null);
					if (result == null) {
						List sid = AllServiceUtil.getOpenareatableService().selectTuijiNum(tuiji);
						UserTable userTable = new UserTable();
						userTable.setUser_id(RedisCacheUtil.getUser_pk());
						userTable.setUsername(username);
						userTable.setUserpwd(password);
						userTable.setSafety(safely);
						userTable.setFlag(userflag);
						userTable.setTuiji(tuiji);
						userTable.setQid((BigDecimal)sid.get(0));
						userTable.setPhonenumber(phone);
						userTable.setRegisterip(registerip);
						int isSuccess = AllServiceUtil.getUserTableService().insertIntoUser(userTable);
						String res = "";
						if (isSuccess <= 0) {
							res = "no";
						} else {
							res = "yes";
						}

						PrintWriter pwPrintWriter = response.getWriter();
						pwPrintWriter.write(res);
						pwPrintWriter.flush();
						pwPrintWriter.close();
						return;
					}

					username = username + "zr";
				}
			}

			PrintWriter pwPrintWriter = response.getWriter();
			pwPrintWriter.write("exist");
			pwPrintWriter.flush();
			pwPrintWriter.close();
		}

	}

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException
	 *             if an error occurs
	 */
	@Override
	public void init() throws ServletException {
		// Put your code here
	}

}
