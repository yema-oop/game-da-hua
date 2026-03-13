package org.come.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;

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
import org.come.entity.UserTable;
import org.come.nettyClient.UrlUntil;
import org.come.until.AllServiceUtil;
import org.come.until.GsonUtil;
import org.come.until.PayMd5;

/**
 * 用户表安全码修改
 * 
 * @author Administrator
 * 
 */
public class UsertableSafenumberServerlet extends HttpServlet {
	private String controlResult = null;

	private UserTable userTable;

	/**
	 * Constructor of the object.
	 */
	public UsertableSafenumberServerlet() {
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
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		response.setHeader("Access-Control-Allow-Origin", "*");
		// 用户ID
		String userid = request.getParameter("userid");
		// 修改的安全码
		String goodsecret = request.getParameter("goodsecret");
		// 秘钥
		String secret = request.getParameter("secret");
		// 状态 type 1标识修改安全码 2表示修改密码
		String type = request.getParameter("type");

		// 进行加密验证签名
		String getsecret = PayMd5.encryption(userid + goodsecret);
		userTable = new UserTable();

		String account_flag = AllServiceUtil.getUserTableService().selectUserFlagById(new BigDecimal(userid));
		if (getsecret.equals(secret)) {
			int flag = 0;
			// 验证签名成功，修改安全码处理
			if (type.equals(1 + "")) {
				userTable.setUser_id(new BigDecimal(userid));
				userTable.setSafety(goodsecret);
				flag = AllServiceUtil.getUserTableService().updateUsterWithUid(userTable);
				/** zrikka 2020 0416 **/
				// 发送给账号服务器 进行修改 安全码
				UrlUntil.accountAction("updateSafely", account_flag, userTable.getSafety());
				/***/
			} else if (type.equals(2 + "")) {
				userTable.setUser_id(new BigDecimal(userid));
				userTable.setUserpwd(goodsecret);
				flag = AllServiceUtil.getUserTableService().updateUsterWithUidforuserpasswd(userTable);
				/** zrikka 2020 0416 **/
				// 发送给账号服务器 进行修改 密码
				UrlUntil.accountAction("updatePasw", account_flag, userTable.getUserpwd());
				/***/
			}

			if (flag > 0) {
				controlResult = "Success";
			} else {
				controlResult = "Erore";
			}
		} else {
			controlResult = "Erore";
		}

		PrintWriter pwPrintWriter = response.getWriter();
		pwPrintWriter.write(GsonUtil.getGsonUtil().getgson().toJson(controlResult));
		pwPrintWriter.flush();
		pwPrintWriter.close();

		// // 进行加密验证签名
		// String getsecret = PayMd5.encryption(userid + goodsecret);
		// userTable = new UserTable();
		// if (getsecret.equals(secret)) {
		// int flag = 0;
		// // 验证签名成功，修改安全码处理
		// if (type.equals(1 + "")) {
		// userTable.setUser_id(new BigDecimal(userid));
		// userTable.setSafety(goodsecret);
		//
		// flag =
		// AllServiceUtil.getUserTableService().updateUsterWithUid(userTable);
		//
		// } else if (type.equals(2 + "")) {
		// userTable.setUser_id(new BigDecimal(userid));
		// userTable.setUserpwd(goodsecret);
		// flag =
		// AllServiceUtil.getUserTableService().updateUsterWithUidforuserpasswd(userTable);
		//
		// }
		//
		// if (flag > 0) {
		// controlResult = "Success";
		// } else {
		// controlResult = "Erore";
		// }
		//
		// } else {
		//
		// controlResult = "Erore";
		// }
		//
		// PrintWriter pwPrintWriter = response.getWriter();
		// pwPrintWriter.write(GsonUtil.getGsonUtil().getgson().toJson(controlResult));
		// pwPrintWriter.flush();
		// pwPrintWriter.close();

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
