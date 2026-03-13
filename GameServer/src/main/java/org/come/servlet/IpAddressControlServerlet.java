package org.come.servlet;

import java.io.IOException;
import java.io.PrintWriter;

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
import org.come.nettyClient.UrlUntil;

/**
 * IP地址操作类
 * @author Administrator
 *
 */
public class IpAddressControlServerlet extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public IpAddressControlServerlet() {
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
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
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
		String conttype = "3";
		String IPAddress = request.getParameter("IPAddress");

		String type = request.getParameter("type");

		if (!(IPAddress.equals("") || IPAddress == null)) {
			/** zrikka 2020 0416 **/
			// 发送给账号服务器 进行 ip 的 封禁 解封 删除
			conttype = "3";

			if (type.equals("2")) {
				// 插入Ip
				int a = AllServiceUtil.getUserTableService().insertFromIpaddressmac(IPAddress);
				if (a > 0) {
					/** zrikka 2020 0416 封禁 **/
					UrlUntil.ipAction("banIp", IPAddress);
					/***/
					conttype = "1";
				} else {
					conttype = "3";
				}

			}
			if (type.equals("3")) {
				// 删除ip
				int a = AllServiceUtil.getUserTableService().deleteFromIpaddressmac(IPAddress);
				if (a > 0) {
					/** zrikka 2020 0416 解封 **/
					UrlUntil.ipAction("liftIp", IPAddress);
					/***/
					conttype = "1";
				} else {
					conttype = "3";
				}

			}
		}

		/** 原来的操作 */
		// if (!(IPAddress.equals("") || IPAddress == null)) {
		//
		// /** zrikka 2020 0416 **/
		// // 发送给账号服务器 进行 ip 的 封禁 解封 删除
		//
		// conttype = "1";
		// if (type.equals("1")) {
		// // 查询是否存在
		// Ipaddressmac ipaddressmac =
		// AllServiceUtil.getUserTableService().selectFromIpaddressmac(IPAddress);
		// if (ipaddressmac != null)
		// conttype = "1";
		//
		// }
		// if (type.equals("2")) {
		// // 插入Ip
		// int a =
		// AllServiceUtil.getUserTableService().insertFromIpaddressmac(IPAddress);
		// if (a > 0)
		// conttype = "1";
		// else
		// conttype = "3";
		//
		// }
		// if (type.equals("3")) {
		// // 删除ip
		// int a =
		// AllServiceUtil.getUserTableService().deleteFromIpaddressmac(IPAddress);
		// if (a > 0)
		// conttype = "1";
		// else
		// conttype = "3";
		//
		// }
		// }

		PrintWriter pwPrintWriter = response.getWriter();
		pwPrintWriter.write(conttype);
		pwPrintWriter.flush();
		pwPrintWriter.close();
//		    request.setCharacterEncoding("utf-8");
//			response.setCharacterEncoding("utf-8");
//			response.setContentType("text/html;charset=utf-8");
//			response.setHeader("Access-Control-Allow-Origin", "*");
//			String conttype="3";
//	        String IPAddress=request.getParameter("IPAddress");
//	        
//	        String type=request.getParameter("type");
//	
//	        if(!(IPAddress.equals("")||IPAddress==null)){
//	        	if(type.equals("1")){
//	        		//查询是否存在
//	        	Ipaddressmac ipaddressmac= AllServiceUtil.getUserTableService().selectFromIpaddressmac(IPAddress);
//	        		
//	        	if(ipaddressmac!=null)  conttype="1";
//	        		
//	        	}if(type.equals("2")){
//	        		//插入Ip 
//	        		int a=AllServiceUtil.getUserTableService().insertFromIpaddressmac(IPAddress);
//	        		if(a>0) conttype="1";
//	        		else conttype="3";
//	        		
//	        	}if(type.equals("3")){
//	        		//删除ip
//	        		int a=AllServiceUtil.getUserTableService().deleteFromIpaddressmac(IPAddress);
//	        		
//	        		if(a>0) conttype="1";
//	        		else conttype="3";
//	        		
//	        		
//	        	}
//	        	    	
//	        	
//	        }
//
//	
//	  	PrintWriter pwPrintWriter = response.getWriter();
//		pwPrintWriter.write(conttype);
//		pwPrintWriter.flush();
//		pwPrintWriter.close();
	}

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	@Override
	public void init() throws ServletException {
		// Put your code here
	}

}
