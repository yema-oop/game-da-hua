
package com.gl.controller;

import com.gl.model.Param;
import com.gl.model.Result;
import com.gl.service.PlayerService;
import com.gl.service.ResultFactory;
import com.gl.token.UserToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class PlayerController {
   public PlayerController() {
   }

   @UserToken
   @PostMapping({"/api/role"})
   public Result roles(Param param, HttpServletRequest request) {
      // 获取用户名密码格式为 用户名|&|密码
      Result ipCheckResult = UserController.IPstop(request);
      if (ipCheckResult != null) {
         return ipCheckResult;
      }
      PlayerService service = new PlayerService();
      return ResultFactory.success(service.getRole(param));
   }

   @UserToken
   @PostMapping({"/api/lockpwd"})
   public Result lockpwd(Param param, HttpServletRequest request) {
      // 获取用户名密码格式为 用户名|&|密码
      Result ipCheckResult = UserController.IPstop(request);
      if (ipCheckResult != null) {
         return ipCheckResult;
      }
      PlayerService service = new PlayerService();
      return service.editLockPassword(param) ? ResultFactory.success(null) : ResultFactory.fail("操作失败");
   }

   @UserToken
   @PostMapping({"/api/roleoperation"})
   public Result operation(Param param, HttpServletRequest request) {
      // 获取用户名密码格式为 用户名|&|密码
      Result ipCheckResult = UserController.IPstop(request);
      if (ipCheckResult != null) {
         return ipCheckResult;
      }
      PlayerService service = new PlayerService();
      return service.operation(param) ? ResultFactory.success(null) : ResultFactory.fail("操作失败，请确认该玩家是否存在");
   }

   @UserToken
   @PostMapping({"/api/recharge"})
   public Result recharge(Param param, HttpServletRequest request) {
      // 获取用户名密码格式为 用户名|&|密码
      Result ipCheckResult = UserController.IPstop(request);
      if (ipCheckResult != null) {
         return ipCheckResult;
      }
      PlayerService service = new PlayerService();
      return service.rechargeCallBack(param) ? ResultFactory.success(null) : ResultFactory.fail("操作失败");
   }
   /**
    * 查询配置数据库
    * @param param
    * @return
    */
   @UserToken
   @PostMapping(value = "/api/selectConfigure")
   public Result selectConfigure(Param param, HttpServletRequest request) {
      Result ipCheckResult = UserController.IPstop(request);
      if (ipCheckResult != null) {
         return ipCheckResult;
      }
      PlayerService service = new PlayerService();
      return ResultFactory.success(service.selectConfigure());
   }

   @UserToken
   @PostMapping({"/api/rechargeinfo"})
   public Result rechargeinfo(Param param, HttpServletRequest request) {
      // 获取用户名密码格式为 用户名|&|密码
      Result ipCheckResult = UserController.IPstop(request);
      if (ipCheckResult != null) {
         return ipCheckResult;
      }
      PlayerService service = new PlayerService();
      return ResultFactory.success(service.getReceipts(param));
   }
   /**
    * 查询配置数据库
    * @param param
    * @return
    */
   @UserToken
   @PostMapping(value = "/api/updateConfigure")
   public Result updateConfigure(HttpServletRequest request,Param param) {
      Result ipCheckResult = UserController.IPstop(request);
      if (ipCheckResult != null) {
         return ipCheckResult;
      }
      String fsd = request.getParameter("fsd");
      String cjlzg = request.getParameter("cjlzg");
      if(fsd!=null &&fsd!="") {
         param.setValue1(fsd);
      }
      if(cjlzg!=null &&cjlzg!="") {
         param.setValue2(cjlzg);
      }
      PlayerService service = new PlayerService();
      return ResultFactory.success(service.updateConfigure(param));
   }
}
