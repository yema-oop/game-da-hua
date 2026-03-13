package org.come.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
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
import org.come.entity.RoleTable;
import org.come.extInterBean.GodsRecordResultModel;
import org.come.extInterBean.Goodsrecord2;
import org.come.until.AllServiceUtil;
import org.come.until.GsonUtil;

/**
 * 物品记录查询
 * 
 * @author zengr
 * 
 */
public class GoodsRecordQueryServlet extends HttpServlet {

	public GoodsRecordQueryServlet() {
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
		// 参数
		// 物品记录查询
		String nowPage = request.getParameter("nowPage");
		String gDState = request.getParameter("gDState");
		String gDRoleName = request.getParameter("gDRoleName");
		String gDOtherName = request.getParameter("gDOtherName");
		String gDGoodsName = request.getParameter("gDGoodsName");
		String gDTime = request.getParameter("gDTime");
		// 物品追踪
		String rgid = request.getParameter("rgid");
		String quid = request.getParameter("quid");
		String page = request.getParameter("page");

		String gDType = request.getParameter("gDType");

		String goodsRecordres = "";
		if ("goodsrecord".equals(gDType)) {
			goodsRecordres = goodsRecordQuery(nowPage, gDState, gDRoleName, gDOtherName, gDGoodsName, gDTime);
		} else if ("trackGoods".equals(gDType)) {
			goodsRecordres = trackGoods(rgid, quid, page);
		}

		// 发送
		PrintWriter pwPrintWriter = response.getWriter();
		pwPrintWriter.write(goodsRecordres);
		pwPrintWriter.flush();
		pwPrintWriter.close();
	}

	// 物品记录查询
	public String goodsRecordQuery(String nowPage, String goodsDetailState, String goodsDetailRoleName, String goodsDetailOtherName, String goodsDetailGoodsName, String goodsDetailTime) {
		String result = "";
		if (nowPage == null || goodsDetailState == null || goodsDetailRoleName == null || goodsDetailOtherName == null || goodsDetailGoodsName == null || goodsDetailTime == null) {
			return result;
		}
		String sql = "";
		// 通过账号 和 区号 查询某角色的 角色id
		if (!"".equals(goodsDetailRoleName)) {
			RoleTable role = AllServiceUtil.getRoleTableService().selectRoleTableByRoleName(goodsDetailRoleName);
			if (role == null) {
				sql += " AND ROLEID = 0 ";
			} else {
				sql += " AND ROLEID = " + role.getRole_id();
			}
		}
		if (!"".equals(goodsDetailOtherName)) {
			RoleTable role = AllServiceUtil.getRoleTableService().selectRoleTableByRoleName(goodsDetailOtherName);
			if (role == null) {
				sql += " AND OTHERROLE = 0 ";
			} else {
				sql += " AND OTHERROLE = " + role.getRole_id();
			}
		}
		// 参数设置
		if (!"".equals(goodsDetailState)) {
			sql += " AND RECORDTYPE = " + goodsDetailState;
		}
		if (!"".equals(goodsDetailGoodsName)) {
			sql += " AND GOODSNAME = '" + goodsDetailGoodsName + "'";
		}
		if (!"".equals(goodsDetailTime)) {
			sql += " AND RECORDTIME between to_timestamp('" + goodsDetailTime + " 00:00:0.000000000','yyyy-mm-dd hh24:mi:ss.ff9') and to_timestamp('" + goodsDetailTime + " 23:59:59.000000000','yyyy-mm-dd hh24:mi:ss.ff9')";
		}
		List<Goodsrecord2> goodsRecord = AllServiceUtil.getAppVersionService().selectGoodsRecordByPage(sql, Integer.valueOf(nowPage));
		// 设置角色名 (通过角色id 和 区id)
		for (int i = 0; i < goodsRecord.size(); i++) {
			// 角色名
			BigDecimal myRole = goodsRecord.get(i).getRoleid();
			if (myRole != null) {
				RoleTable role = AllServiceUtil.getRoleTableService().selectGang(myRole);
				if (role != null) {
					goodsRecord.get(i).setRoleName(role.getRolename());
				} else {
					goodsRecord.get(i).setRoleName("");
				}
			}
			// 对象角色 (如: 交易对象..)
			BigDecimal otherRole = goodsRecord.get(i).getOtherrole();
			if (otherRole != null && !"0".equals(otherRole)) {
				RoleTable role = AllServiceUtil.getRoleTableService().selectGang(otherRole);
				if (role != null) {
					goodsRecord.get(i).setOtherRole(role.getRolename());
				} else {
					goodsRecord.get(i).setOtherRole("");
				}
			}
		}
		GodsRecordResultModel godsRec = new GodsRecordResultModel(goodsRecord, Integer.valueOf(nowPage), 0);
		String godsRecJson = GsonUtil.getGsonUtil().getgson().toJson(godsRec);
		return godsRecJson;
	}

	// 物品追踪
	public String trackGoods(String rgid, String quid, String page) {
		String result = "";
		if (rgid == null || "".equals(rgid) || quid == null || "".equals(quid) || page == null || "".equals(page)) {
			return result;
		}
		int rgidi = Integer.valueOf(rgid);
		int quidi = Integer.valueOf(quid);
		int pagei = Integer.valueOf(page);
		List<Goodsrecord2> trackGoods = AllServiceUtil.getAppVersionService().trackGoods(rgidi, quidi, pagei);
		// 设置角色名 (通过角色id 和 区id)
		for (int i = 0; i < trackGoods.size(); i++) {
			// 角色名
			BigDecimal myRole = trackGoods.get(i).getRoleid();
			Integer sid = trackGoods.get(i).getSid();
			if (myRole != null) {
				RoleTable role = AllServiceUtil.getRoleTableService().selectGang(myRole);
				if (role != null) {
					trackGoods.get(i).setRoleName(role.getRolename());
				} else {
					trackGoods.get(i).setRoleName("");
				}
			}
			// 对象角色 (如: 交易对象..)
			BigDecimal otherRole = trackGoods.get(i).getOtherrole();
			if (otherRole != null && !"0".equals(otherRole)) {
				RoleTable role = AllServiceUtil.getRoleTableService().selectGang(otherRole);
				if (role != null) {
					trackGoods.get(i).setOtherRole(role.getRolename());
				} else {
					trackGoods.get(i).setOtherRole("");
				}
			}
		}
		GodsRecordResultModel model = new GodsRecordResultModel(trackGoods, pagei, 0);
		String godsRecoJson = GsonUtil.getGsonUtil().getgson().toJson(model);
		return godsRecoJson;
	}

	@Override
	public void init() throws ServletException {

	}

	public static String getStr(List<String> list) {
		String str = list.get(0);
		for (int i = 1; i < list.size(); i++) {
			str += "," + list.get(i);
		}
		return str;
	}

}
