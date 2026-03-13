package com.gl.controller;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.github.pagehelper.util.StringUtil;
import com.gl.model.Param;
import com.gl.model.Result;
import com.gl.model.User;
import com.gl.service.GoodsService;
import com.gl.service.PlayerService;
import com.gl.service.ResultFactory;
import com.gl.service.TokenService;
import com.gl.token.UserToken;
import come.tool.Battle.BattleData;
import come.tool.Battle.BattleThreadPool;
import come.tool.Good.UsePetAction;
import come.tool.Role.PrivateData;
import come.tool.Role.RoleData;
import come.tool.Role.RolePool;
import come.tool.Scene.LaborDay.LaborScene;
import come.tool.Stall.AssetUpdate;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.httpclient.util.DateUtil;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.come.action.monitor.MonitorUtil;
import org.come.agent.Agent;
import org.come.bean.*;
import org.come.entity.*;
import org.come.handler.MainServerHandler;
import org.come.handler.SendMessage;
import org.come.model.Skill;
import org.come.protocol.Agreement;
import org.come.redis.RedisPoolUntil;
import org.come.server.GameServer;
import org.come.tool.Arith;
import org.come.tool.ReadExelTool;
import org.come.tool.WriteOut;
import org.come.until.AllServiceUtil;
import org.come.until.CreateTextUtil;
import org.come.until.GsonUtil;
import org.come.until.ReadTxtUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@RestController
public class AdminController {
    private ConcurrentHashMap<String, Goodstable> nds = new ConcurrentHashMap<>();
    private String agentGoodsIds;
    private static final int PageSize = 10;

    public AdminController() {
    }

    @PostMapping({"/api/adminLogin"})
    public Result login1(User user, HttpSession session, HttpServletRequest request) {
        // 获取用户名密码格式为 用户名|&|密码
        Result ipCheckResult = UserController.IPstop(request);
        if (ipCheckResult != null) {
            return ipCheckResult;
        }
        String up = ReadTxtUtil.readFile1(ReadExelTool.class.getResource("/").getPath() + "administrator.db");
        String[] nameAndPwd = up.split("\\|&\\|");
        if (nameAndPwd[0].equals(user.getUserName()) && nameAndPwd[1].equals(user.getPassword())) {
            TokenService tokenService = new TokenService();
            String token = tokenService.getToken(user);
            session.setAttribute("BG_NAME_XY2", user);
            return ResultFactory.success(token + "|admin");
        } else {
            return ResultFactory.fail("用户名或密码错误，请重新登录！ ");
        }
    }

    @UserToken
    @PostMapping({"/api/getUserGood"})
    public Result getUserGood(Param param, HttpServletRequest request) {
        // 获取用户名密码格式为 用户名|&|密码
        Result ipCheckResult = UserController.IPstop(request);
        if (ipCheckResult != null) {
            return ipCheckResult;
        }
        new GoodsService();
        List<Goodstable> goods = AllServiceUtil.getGoodsTableService().getGoodsByRoleID(new BigDecimal(param.getValue1()));
        return ResultFactory.success(goods);
    }

    @UserToken
    @PostMapping({"/api/agentRecharge"})
    public Result recharge(Param param, HttpServletRequest httpServletRequest) {
        // 获取用户名密码格式为 用户名|&|密码
        Result ipCheckResult = UserController.IPstop(httpServletRequest);
        if (ipCheckResult != null) {
            return ipCheckResult;
        }
        HttpSession session = httpServletRequest.getSession();
        User user = (User) session.getAttribute("BG_NAME_XY2");
        Agent agent = AllServiceUtil.getAgentService().selectByUserName(user.getUserName());
        if (agent == null) {
            return ResultFactory.fail("代理不存在！");
        } else {
            if ("1".equals(param.getValue5())) {
                Integer jf = Integer.parseInt(param.getValue3());
                if (agent.getXianyu().intValue() < jf) {
                    return ResultFactory.fail("代理元宝不足以抵扣本次充值！");
                }

                jf = Integer.parseInt(param.getValue2());
                if (agent.getJf().intValue() < jf) {
                    return ResultFactory.fail("代理充值金额不足以抵扣本次充值！");
                }
                agent.setXianyu(agent.getXianyu().subtract(new BigDecimal(param.getValue3())));
                agent.setJf(agent.getJf().subtract(new BigDecimal(param.getValue2())));
            } else if ("2".equals(param.getValue5())) {
                param.setValue2("98");
                Integer jf = Integer.parseInt(param.getValue2());
                if (agent.getJf().intValue() < jf) {
                    return ResultFactory.fail("代理充值金额不足以抵扣本次充值！");
                }

                agent.setJf(agent.getJf().subtract(new BigDecimal(param.getValue2())));
            }
            AllServiceUtil.getAgentService().upAgentXyAndJf(agent);
            new PlayerService();
            return this.agentRechargeCallBack(param, agent) ? ResultFactory.success(AllServiceUtil.getAgentService().selectByUserName(agent.getUserName())) : ResultFactory.fail("操作失败");
        }
    }

    @UserToken
    @PostMapping({"/api/getUserPet"})
    public Result getUserPet(Param param, HttpServletRequest request) {
        Result ipCheckResult = UserController.IPstop(request);
        if (ipCheckResult != null) {
            return ipCheckResult;
        }
        List<RoleSummoning> roleSummonings = AllServiceUtil.getRoleSummoningService().selectRoleSummoningsByRoleID(new BigDecimal(param.getValue1()));
        return ResultFactory.success(roleSummonings);
    }

    @UserToken
    @PostMapping({"/api/getUserMount"})
    public Result getUserMount(Param param, HttpServletRequest request) {
        Result ipCheckResult = UserController.IPstop(request);
        if (ipCheckResult != null) {
            return ipCheckResult;
        }
        List<Mount> mounts = AllServiceUtil.getMountService().selectMountsByRoleID(new BigDecimal(param.getValue1()));
        return ResultFactory.success(mounts);
    }

    @UserToken
    @PostMapping({"/api/getUserLing"})
    public Result getUserLing(Param param, HttpServletRequest request) {
        // 获取用户名密码格式为 用户名|&|密码
        Result ipCheckResult = UserController.IPstop(request);
        if (ipCheckResult != null) {
            return ipCheckResult;
        }
        List<Lingbao> lingbaos = AllServiceUtil.getLingbaoService().selectLingbaoByRoleID(new BigDecimal(param.getValue1()));
        return ResultFactory.success(lingbaos);
    }

    @UserToken
    @PostMapping({"/api/updUserLing"})
    public Result updUserLing(Lingbao param, HttpServletRequest request) throws UnsupportedEncodingException {
        // 获取用户名密码格式为 用户名|&|密码
        Result ipCheckResult = UserController.IPstop(request);
        if (ipCheckResult != null) {
            return ipCheckResult;
        }
        String baoname = URLUtil.decode(param.getBaoname());
        param.setBaoname(baoname);
        String kangxing = URLUtil.decode(param.getKangxing());
        param.setKangxing(kangxing);
        String tianfu;
        if (StrUtil.isNotBlank(param.getSkills()) && !param.getSkills().equals("null")) {
            tianfu = URLUtil.decode(param.getSkills());
            param.setSkills(tianfu);
        }

        if (param.getSkills().equals("null")) {
            param.setSkills((String) null);
        }

        tianfu = URLUtil.decode(param.getTianfuskill());
        param.setTianfuskill(tianfu);
        String goodSkill = URLUtil.decode(param.getGoodskill());
        param.setGoodskill(goodSkill);
        LoginResult loginResult = AllServiceUtil.getRoleTableService().selectRoleByRoleId(param.getRoleid());
        if (loginResult == null) {
            return ResultFactory.fail("角色不存在！");
        } else {
            Lingbao lingbao = AllServiceUtil.getLingbaoService().selectLingbaoByID(param.getBaoid());
            if (lingbao == null) {
                return ResultFactory.fail("灵宝不存在！");
            } else {
                lingbao.setBaoactive(param.getBaoactive());
                lingbao.setBaospeed(param.getBaospeed());
                lingbao.setAssistance(param.getAssistance());
                lingbao.setGoodskill(param.getGoodskill());
                lingbao.setLingbaolvl(param.getLingbaolvl());
                lingbao.setSkills(param.getSkills());
                lingbao.setTianfuskill(param.getTianfuskill());
                lingbao.setSkillsum(param.getSkillsum());
                lingbao.setFusum(param.getFusum());
                AllServiceUtil.getLingbaoService().updateLingbaoRedis(lingbao);
                if (loginResult != null) {
                    ChannelHandlerContext channelHandlerContext = GameServer.getRoleNameMap().get(loginResult.getRolename());
                    if (channelHandlerContext != null) {
                        AssetUpdate update = new AssetUpdate();
                        List<Lingbao> lingbaos = new ArrayList<>();
                        lingbaos.add(lingbao);
                        update.setLingbaos(lingbaos);
                        update.setType(AssetUpdate.USEGOOD);
                        update.setMsg(":#R多功能后台修改灵宝成功#23");
                        SendMessage.sendMessageToSlef(channelHandlerContext, Agreement.getAgreement().assetAgreement(GsonUtil.getGsonUtil().getgson().toJson(update)));
                    }
                }

                return ResultFactory.success(null);
            }
        }
    }

    @UserToken
    @PostMapping({"/api/getUserBaby"})
    public Result getUserBaby(Param param, HttpServletRequest request) {
        // 获取用户名密码格式为 用户名|&|密码
        Result ipCheckResult = UserController.IPstop(request);
        if (ipCheckResult != null) {
            return ipCheckResult;
        }
        List<Baby> babys = AllServiceUtil.getBabyService().selectBabyByRolename(new BigDecimal(param.getValue1()));
        return ResultFactory.success(babys);
    }

    @UserToken
    @PostMapping({"/api/getAgentLog"})
    public Result getAgentLog(Param param, HttpServletRequest httpServletRequest, HttpServletRequest request) {
        // 获取用户名密码格式为 用户名|&|密码
        Result ipCheckResult = UserController.IPstop(request);
        if (ipCheckResult != null) {
            return ipCheckResult;
        }
        HttpSession session = httpServletRequest.getSession();
        User user = (User) session.getAttribute("BG_NAME_XY2");
        Agent agent = AllServiceUtil.getAgentService().selectByUserName(user.getUserName());
        ExpensesReceipts expensesReceipts = new ExpensesReceipts();
        if (agent != null) {
            expensesReceipts.setManagerid(new BigDecimal(agent.getAgentId()));
        }

        List<ExpensesReceipts> expensesReceipts1 = AllServiceUtil.getExpensesReceiptsService().selectAll(expensesReceipts);
        return ResultFactory.success(expensesReceipts1);
    }

//    @UserToken
//    @GetMapping({"/api/getAgentJurisdiction"})
//    public Result getAgentJurisdiction(HttpServletRequest httpServletRequest, HttpServletRequest request) {
//        // 获取用户名密码格式为 用户名|&|密码
//        Result ipCheckResult = UserController.IPstop(request);
//        if (ipCheckResult != null) {
//            return ipCheckResult;
//        }
//        HttpSession session = httpServletRequest.getSession();
//        User user = (User) session.getAttribute("BG_NAME_XY2");
//        String up = ReadTxtUtil.readFile1(ReadExelTool.class.getResource("/").getPath() + "administrator.db");
//        String[] nameAndPwd = up.split("\\|&\\|");
//        Agent agent;
//        if (user.getUserName().equals(nameAndPwd[0])) {
//            agent = new Agent();
//            //admin 返回所有权限
//            agent.setJurisdiction("admin");
//            return ResultFactory.success(agent);
//        } else {
//            agent = AllServiceUtil.getAgentService().selectByUserName(user.getUserName());
//            return agent != null ? ResultFactory.success(agent) : ResultFactory.success("");
//        }
//    }

//    @UserToken
//    @GetMapping({"/api/agentGoods"})
//    public Result agentGoods(HttpServletRequest httpServletRequest, HttpServletRequest request) {
//        // 获取用户名密码格式为 用户名|&|密码
//        Result ipCheckResult = UserController.IPstop(request);
//        if (ipCheckResult != null) {
//            return ipCheckResult;
//        }
//        Map<String, String> goodsMap = (new GoodsService()).goodsMap();
//        HttpSession session = httpServletRequest.getSession();
//        User user = (User) session.getAttribute("BG_NAME_XY2");
//        String up = ReadTxtUtil.readFile1(ReadExelTool.class.getResource("/").getPath() + "administrator.db");
//        String[] nameAndPwd = up.split("\\|&\\|");
//        if (user.getUserName().equals(nameAndPwd[0])) {
//            return ResultFactory.success(goodsMap);
//        } else {
//            Agent agent = AllServiceUtil.getAgentService().selectByUserName(user.getUserName());
//            if (agent != null && StringUtils.isNotBlank(agent.getJurisdiction()) && agent.getJurisdiction().contains("物品") && StringUtils.isNotBlank(this.agentGoodsIds)) {
//                String[] items = this.agentGoodsIds.split("\\|");
//                Map<String, String> AgentGoodsMap = new ConcurrentHashMap<>();
//                String[] var10 = items;
//                int var11 = items.length;
//
//                for (int var12 = 0; var12 < var11; ++var12) {
//                    String item = var10[var12];
//                    goodsMap.forEach((k, v) -> {
//                        if (v.equals(item)) {
//                            AgentGoodsMap.put(k, v);
//                        }
//
//                    });
//                }
//
//                return ResultFactory.success(AgentGoodsMap);
//            } else {
//                return ResultFactory.success(new ConcurrentHashMap<>());
//            }
//        }
//    }

//    @UserToken
//    @PostMapping({"/api/agentRole"})
//    public Result roles(Param param, HttpServletRequest httpServletRequest, HttpServletRequest request) {
//        // 获取用户名密码格式为 用户名|&|密码
//        Result ipCheckResult = UserController.IPstop(request);
//        if (ipCheckResult != null) {
//            return ipCheckResult;
//        }
//        HttpSession session = httpServletRequest.getSession();
//        User user = (User) session.getAttribute("BG_NAME_XY2");
//        String up = ReadTxtUtil.readFile1(ReadExelTool.class.getResource("/").getPath() + "administrator.db");
//        String[] nameAndPwd = up.split("\\|&\\|");
//        if (user.getUserName().equals(nameAndPwd[0])) {
//            PlayerService service = new PlayerService();
//            BackRoleInfo role = service.getRole(param);
//            //admin 返回所有权限
//            return ResultFactory.success(role);
//        } else {
//            BackRoleInfo role = new BackRoleInfo();
//            Agent agent = AllServiceUtil.getAgentService().selectByUserName(user.getUserName());
//            if (agent != null) {
//                param.setValue3(agent.getQid());
//                role = this.getRole(param);
//            }
//
//            return ResultFactory.success(role);
//        }
//    }

//    @UserToken
//    @GetMapping({"/api/getAgentSendGoods"})
//    public Result getAgendSendGoods(HttpServletRequest httpServletRequest, HttpServletRequest request) {
//        // 获取用户名密码格式为 用户名|&|密码
//        Result ipCheckResult = UserController.IPstop(request);
//        if (ipCheckResult != null) {
//            return ipCheckResult;
//        }
//        if (StringUtils.isBlank(this.agentGoodsIds)) {
//            Properties properties = new Properties();
//            InputStream in = GameServer.class.getClassLoader().getResourceAsStream("agent.properties");
//            try {
//                properties.load(in);// 使用properties对象加载输入流
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            agentGoodsIds = properties.get("agentGoods").toString();
//        }
//        return ResultFactory.success(this.agentGoodsIds);
//    }

    @UserToken
    @PostMapping({"/api/upAgentSendGoods"})
    public Result upAgentSendGoods(Param param, HttpServletRequest request) {
        // 获取用户名密码格式为 用户名|&|密码
        Result ipCheckResult = UserController.IPstop(request);
        if (ipCheckResult != null) {
            return ipCheckResult;
        }
        if (StringUtils.isNotBlank(param.getValue1())) {
            this.agentGoodsIds = param.getValue1();
            try {
                byte[] bs = this.agentGoodsIds.getBytes();
                CreateTextUtil.createFile(ReadExelTool.class.getResource("/").getPath() + "agentGoods.txt", bs);
            } catch (Exception var3) {
                var3.printStackTrace();
            }
        }
        return ResultFactory.success(true);
    }

    @UserToken
    @PostMapping({"/api/agentSendGoods"})
    public Result agentSendGoods(Param param, HttpServletRequest request) {
        // 获取用户名密码格式为 用户名|&|密码
        Result ipCheckResult = UserController.IPstop(request);
        if (ipCheckResult != null) {
            return ipCheckResult;
        }
        GoodsService service = new GoodsService();
        return service.sendGoods(param) ? ResultFactory.success(null) : ResultFactory.fail("物品发送失败，请确认玩家或物品是否存在");
    }

//    @UserToken
//    @GetMapping({"/api/getMenuList"})
//    public Result getMenuList(Param param, HttpServletRequest httpServletRequest) {
//        Result ipCheckResult = UserController.IPstop(httpServletRequest);
//        if (ipCheckResult != null) {
//            return ipCheckResult;
//        }
//        HttpSession session = httpServletRequest.getSession();
//        User user = (User) session.getAttribute("BG_NAME_XY2");
//        String up = ReadTxtUtil.readFile1(ReadExelTool.class.getResource("/").getPath() + "user.db");
//        String[] nameAndPwd = up.split("\\|&\\|");
//        //admin 返回所有权限
//        //查询代理表.获取指定代理权限
//        return user.getUserName().equals(nameAndPwd[0]) ? ResultFactory.success("admin") : ResultFactory.success("test");
//    }

    @UserToken
    @GetMapping({"/api/getAgentAll"})
    public Result getMenuList(HttpServletRequest httpServletRequest) {
        // 获取用户名密码格式为 用户名|&|密码
        Result ipCheckResult = UserController.IPstop(httpServletRequest);
        if (ipCheckResult != null) {
            return ipCheckResult;
        }
        List<Agent> agents = AllServiceUtil.getAgentService().selectAll();
        //admin 返回所有权限
        return ResultFactory.success(agents);
    }

//    @UserToken
//    @GetMapping({"/api/getOpenAll"})
//    public Result getOpenAll(HttpServletRequest httpServletRequest) {
//        // 获取用户名密码格式为 用户名|&|密码
//        Result ipCheckResult = UserController.IPstop(httpServletRequest);
//        if (ipCheckResult != null) {
//            return ipCheckResult;
//        }
//        List<Openareatable> openareatables = AllServiceUtil.getOpenareatableService().selectAllOpenareatable();
//        //admin 返回所有权限
//        return ResultFactory.success(openareatables);
//    }

    @UserToken
    @PostMapping({"/api/addAgent"})
    public Result addAgent(Agent agent, HttpServletRequest request) {
        // 获取用户名密码格式为 用户名|&|密码
        Result ipCheckResult = UserController.IPstop(request);
        if (ipCheckResult != null) {
            return ipCheckResult;
        }
//        agent.setCreatTime(new Date());
        List<BigDecimal> bigDecimals = AllServiceUtil.getOpenareatableService().selectTuijiNum(agent.getUserName());
        if (bigDecimals.size() > 0) {
            return ResultFactory.fail("当前代理已存在");
        }

        //插入推荐表
        Openareatable openareatable1 = new Openareatable();
        List<Openareatable> openareatables = AllServiceUtil.getOpenareatableService().selectAllOpenareatable();
        if (openareatables.size() == 1) {
            openareatable1 = openareatables.get(0);
            openareatable1.setOt_areaid(new BigDecimal(openareatable1.getOt_areaid().intValue() + 1));
            openareatable1.setOt_atid(agent.getUserName());
            AllServiceUtil.getOpenareatableService().insertOpenareatable(openareatable1);
        } else if (openareatables.size() == 0) {

        } else {
            List<BigDecimal> collect = openareatables.stream().map(item -> item.getOt_areaid()).collect(Collectors.toList());
            Collections.sort(collect);
            openareatable1 = openareatables.get(0);
            openareatable1.setOt_areaid(new BigDecimal(collect.get(collect.size() - 1).intValue() + 1));
            openareatable1.setOt_atid(agent.getUserName());
            AllServiceUtil.getOpenareatableService().insertOpenareatable(openareatable1);

        }
        agent.setQid(openareatable1.getOt_areaid().toString());
        agent.setXianyu(BigDecimal.ZERO);
        agent.setJf(BigDecimal.ZERO);
        AllServiceUtil.getAgentService().addAgent(agent);
        return ResultFactory.success(true);
    }

    @UserToken
    @PostMapping(value = "/api/delAgent")
    public Result delAgent(Agent agent, HttpServletRequest request) {
        // 获取用户名密码格式为 用户名|&|密码
        Result ipCheckResult = UserController.IPstop(request);
        if (ipCheckResult != null) {
            return ipCheckResult;
        }
        AllServiceUtil.getAgentService().deleteById(agent.getAgentId().toString());
        List<Openareatable> openareatables = AllServiceUtil.getOpenareatableService().selectAllOpenareatable();
        Openareatable openareatable = openareatables.stream().filter(item -> item.getOt_atid().equals(agent.getUserName())).findFirst().get();
        AllServiceUtil.getOpenareatableService().deleteOpenareatable(openareatable.getTb_id());
        return ResultFactory.success(true);
    }

    @UserToken
    @PostMapping(value = "/api/upAgentPwd")
    public Result upAgentPwd(Agent agent, HttpServletRequest request) {
        // 获取用户名密码格式为 用户名|&|密码
        Result ipCheckResult = UserController.IPstop(request);
        if (ipCheckResult != null) {
            return ipCheckResult;
        }
        AllServiceUtil.getAgentService().upAgent(agent);
        return ResultFactory.success(true);
    }

    @UserToken
    @PostMapping(value = "/api/addPay")
    public Result addPay(Agent agent, HttpServletRequest request) {
        // 获取用户名密码格式为 用户名|&|密码
        Result ipCheckResult = UserController.IPstop(request);
        if (ipCheckResult != null) {
            return ipCheckResult;
        }
        Agent dbAgent = AllServiceUtil.getAgentService().selectByUserName(agent.getUserName());
        dbAgent.setXianyu(agent.getXianyu().add(dbAgent.getXianyu()));
        dbAgent.setJf(agent.getJf().add(dbAgent.getJf()));
        AllServiceUtil.getAgentService().upAgentXyAndJf(dbAgent);
        return ResultFactory.success(true);
    }

    @UserToken
    @PostMapping(value = "/api/updUserBaby")
    public Result updUserBaby(Baby baby, HttpServletRequest request)  throws UnsupportedEncodingException {
        // 获取用户名密码格式为 用户名|&|密码
        Result ipCheckResult = UserController.IPstop(request);
        if (ipCheckResult != null) {
            return ipCheckResult;
        }
        String babyName = new String(baby.getBabyname().getBytes("ISO8859-1"), "utf-8");
        String come = new String(baby.getOutcome().getBytes("ISO8859-1"), "utf-8");

        LoginResult loginResult = AllServiceUtil.getRoleTableService().selectRoleByRoleId(baby.getRoleid());
        if (loginResult == null)
            return ResultFactory.fail("角色不存在！");

        Baby dbBaby = AllServiceUtil.getBabyService().selectBabyById(baby.getBabyid());


        if (dbBaby == null)
            return ResultFactory.fail("孩子不存在！");
        dbBaby.setTalents(baby.getTalents());
        dbBaby.setQizhi(baby.getQizhi());
        dbBaby.setNeili(baby.getNeili());
        dbBaby.setZhili(baby.getZhili());
        dbBaby.setNaili(baby.getNaili());
        dbBaby.setMingqi(baby.getMingqi());
        dbBaby.setDaode(baby.getDaode());
        dbBaby.setPanni(baby.getPanni());
        dbBaby.setWanxing(baby.getWanxing());
        dbBaby.setQingmi(baby.getQingmi());
        dbBaby.setXiaoxin(baby.getXiaoxin());
        dbBaby.setWenbao(baby.getWenbao());
        dbBaby.setBabyage(baby.getBabyage());
        dbBaby.setOutcome(come);
        dbBaby.setQingmi(baby.getQingmi());
        dbBaby.setNaili(baby.getNaili());
        AllServiceUtil.getBabyService().updateBaby(dbBaby);
        if (loginResult != null) {
            ChannelHandlerContext channelHandlerContext = GameServer.getRoleNameMap().get(loginResult.getRolename());
            if (channelHandlerContext != null) {
                AssetUpdate update = new AssetUpdate();
                // 添加返回bean
                List<Baby> babies = new ArrayList<>();
                babies.add(dbBaby);
                update.setBabys(babies);
                update.setType(AssetUpdate.USEGOOD);
                update.setMsg("#R多功能后台修改孩子成功#23");
                // 发送前端取回的东西
                SendMessage.sendMessageToSlef(channelHandlerContext, Agreement.getAgreement().assetAgreement(GsonUtil.getGsonUtil().getgson().toJson(update)));
            }
        }
        return ResultFactory.success(null);
    }

    @UserToken
    @PostMapping(value = "/api/updUserMount")
    public Result updUserMount(Mount mount, HttpServletRequest request)  {
        // 获取用户名密码格式为 用户名|&|密码
        Result ipCheckResult = UserController.IPstop(request);
        if (ipCheckResult != null) {
            return ipCheckResult;
        }
        LoginResult loginResult = AllServiceUtil.getRoleTableService().selectRoleByRoleId(mount.getRoleid());
        if (loginResult == null)
            return ResultFactory.fail("角色不存在！");
        Mount dbMount = AllServiceUtil.getMountService().selectMountsByMID(mount.getMid());
        dbMount.setExp(mount.getExp());
        dbMount.setMountlvl(mount.getMountlvl());
        dbMount.setPower(mount.getPower());
        dbMount.setBone(mount.getBone());
        dbMount.setSpri(mount.getSpri());
        dbMount.setLive(mount.getLive());
        dbMount.setProficiency(mount.getProficiency());
        AllServiceUtil.getMountService().updateMount(dbMount);
        if (loginResult != null) {
            ChannelHandlerContext channelHandlerContext = GameServer.getRoleNameMap().get(loginResult.getRolename());
            if (channelHandlerContext != null) {
                AssetUpdate update = new AssetUpdate();
                // 添加返回bean
                List<Mount> mounts = new ArrayList<>();
                mounts.add(dbMount);
                update.setMounts(mounts);
                update.setType(AssetUpdate.USEGOOD);
                update.setMsg("#R多功能后台修改坐骑成功#23");
                // 发送前端取回的东西
                SendMessage.sendMessageToSlef(channelHandlerContext, Agreement.getAgreement().assetAgreement(GsonUtil.getGsonUtil().getgson().toJson(update)));
            }
        }
        return ResultFactory.success(null);
    }



    @UserToken
    @PostMapping({"/api/updUserGood"})
    public Result updUserGood(Goodstable goodstable, HttpServletRequest request)  throws UnsupportedEncodingException {
        // 获取用户名密码格式为 用户名|&|密码
        Result ipCheckResult = UserController.IPstop(request);
        if (ipCheckResult != null) {
            return ipCheckResult;
        }
        new GoodsService();
        Goodstable dbGoods = AllServiceUtil.getGoodsTableService().getGoodsByRgID(goodstable.getRgid());
        if (dbGoods != null) {
            String v = new String(goodstable.getValue().getBytes("ISO8859-1"), StandardCharsets.UTF_8);
            dbGoods.setValue(v);
            AllServiceUtil.getGoodsTableService().updateGoodRedis(dbGoods);
            LoginResult loginResult = AllServiceUtil.getRoleTableService().selectRoleByRoleId(dbGoods.getRole_id());
            if (loginResult != null) {
                ChannelHandlerContext channelHandlerContext = GameServer.getRoleNameMap().get(loginResult.getRolename());
                if (channelHandlerContext != null) {
                    AssetUpdate update = new AssetUpdate();
                    List<Goodstable> goodstables = new ArrayList<>();
                    goodstables.add(dbGoods);
                    update.setGoods(goodstables);
                    update.setType(AssetUpdate.GIVE);
                    update.setMsg("#R多功能后台修改物品修改成功#23");
                    SendMessage.sendMessageToSlef(channelHandlerContext, Agreement.getAgreement().assetAgreement(GsonUtil.getGsonUtil().getgson().toJson(update)));
                }
            }
        }
        return ResultFactory.success("111111111");
    }

    public BackRoleInfo getRole(Param param) {
        String type = param.getValue1();
        String value = param.getValue2();
        int pageNum = param.getPageNum();
        int status = param.getStatus();
        int size = param.getPageSize();
        if (size < 10) {
            size = 10;
        }

        BackRoleInfo list = null;
        RoleTable roleTable = new RoleTable();
        if (StringUtils.isNotBlank(param.getValue3())) {
            roleTable.setQid(new BigDecimal(param.getValue3()));
        } else {
            roleTable.setQid(null);
        }

        roleTable.setStart((pageNum - 1) * size);
        roleTable.setEnd(pageNum * size);
        switch (status) {
            case 3:
                roleTable.setUnknown("1");
                break;
            case 4:
                roleTable.setActivity(new Short("1"));
                break;
            case 5:
                roleTable.setActivity(new Short("0"));
                break;
            default:
                roleTable.setActivity((Short) null);
        }

        if (StringUtil.isNotEmpty(type) && !"undefined".equals(type) && StringUtil.isNotEmpty(value) && !"undefined".equals(value)) {
            if (type.equals("1") && NumberUtils.isDigits(value)) {
                roleTable.setRole_id(new BigDecimal(value));
            } else if (type.equals("2")) {
                roleTable.setRolename(value);
            } else if (type.equals("3")) {
                roleTable.setLocalname(value);
            }
        }

        int total = AllServiceUtil.getUserTableService().selectSumForRoleUserHaterNumber(roleTable);
        int page = total / size;
        if (total % size > 0) {
            ++page;
        }

        roleTable.setUserString(" Order By role_id ASC");
        List<RoleTable> listall = AllServiceUtil.getUserTableService().selectSumForRoleUserHaterList(roleTable);
        list = new BackRoleInfo();
        Iterator var12 = listall.iterator();

        while (var12.hasNext()) {
            RoleTable roleInfo = (RoleTable) var12.next();
            if (!org.apache.commons.lang.StringUtils.isBlank(roleInfo.getRolename())) {
                if (GameServer.getRoleNameMap().get(roleInfo.getRolename()) != null) {
                    roleInfo.setStatues("在线");
                } else {
                    roleInfo.setStatues("离线");
                }

                roleInfo.setUnknown(StringUtil.isEmpty(roleInfo.getUnknown()) ? "0" : roleInfo.getUnknown());
                roleInfo.setPassword(null);
            }
        }

        list.setList(listall);
        list.setPages(page);
        list.setPageNum(pageNum);
        list.setTotal(total);
        return list;
    }

    public boolean agentRechargeCallBack(Param param, Agent agent) {
        // 用户ID
        String user_id = param.getValue1();

        // 金额/VIP天数
        String recharge = param.getValue2();

        // 仙玉
        String yuanbao = param.getValue3();

        // 赠送抽奖次数
        String count = param.getValue4();

        // 充值类型
        String saveType = param.getValue5();

        if (StringUtil.isEmpty(saveType)) {
            return false;
        }

        int type = Integer.parseInt(saveType);

        if (StringUtil.isEmpty(user_id)) {
            return false;
        }

        if (StringUtil.isEmpty(yuanbao)) {
            yuanbao = "0";
        }

        BigDecimal userId = new BigDecimal(user_id);

        UserTable userTable = AllServiceUtil.getUserTableService().selectByPrimaryKey(userId);

        Random r = new Random(System.currentTimeMillis());
        ExpensesReceipts expensesReceipts = new ExpensesReceipts();
        expensesReceipts.setErid(new BigDecimal(System.currentTimeMillis() + "" + r.nextInt(9999)));
        expensesReceipts.setPlayeracc(userTable.getUsername());
        expensesReceipts.setSid(userTable.getQid());
        expensesReceipts.setRecharge(new BigDecimal(recharge));
        expensesReceipts.setYuanbao(new BigDecimal(yuanbao));
        expensesReceipts.setType(type);
        expensesReceipts.setManagerid(new BigDecimal(agent.getAgentId()));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR_OF_DAY, 8);
        expensesReceipts.setPaytime(DateUtil.formatDate(calendar.getTime(), "yyyy-MM-dd HH:mm:ss"));

        Jedis jedis = RedisPoolUntil.getJedis();

        try {
            ApplyBean applyBean = new ApplyBean();
            applyBean.setUserNameS(expensesReceipts.getPlayeracc());// 充值的帐户名
            applyBean.setRealmoney(expensesReceipts.getRecharge() + "");// 实际充值金额
            BigDecimal addC = new BigDecimal(applyBean.getRealmoney());
            // 支付类型 1仙玉充值 2周月卡充值 3小资冲级礼包充值 4土豪冲级礼包字段

            userTable.setPayintegration(userTable.getPayintegration() + addC.intValue());
            ChannelHandlerContext ctx = GameServer.getInlineUserNameMap().get(applyBean.getUserNameS());
            LoginResult login = ctx != null ? GameServer.getAllLoginRole().get(ctx) : null;
            if (login != null) {// 在线充值
                AllServiceUtil.getUserTableService().updateUser(userTable);
                login.setPaysum(login.getPaysum().add(addC));// 累计充值
                login.setDaypaysum(login.getDaypaysum().add(addC));// 每日累计充值
                if (StringUtil.isNotEmpty(count) && !"undefined".equals(count)) {
                    LaborScene.addRankValue(0, Integer.parseInt(count) * 10, login);//劳动节活动
                }
                ApplyPayBean applyPayBean = new ApplyPayBean();
                applyPayBean.setAddM(addC);
                expensesReceipts.setRoleid(login.getRole_id());
                expensesReceipts.setBuyroleName(login.getRolename());
                RoleData roleData = RolePool.getRoleData(login.getRole_id());
                PayvipBean vipBean = GameServer.getVIP(login.getPaysum().longValue());
//                if (vipBean != null && roleData != null) {//修改取消SVIP
//                    UseCardBean limit = roleData.getLimit("SVIP");
//                    if (limit == null) {
//                        limit = new UseCardBean("VIP" + vipBean.getGrade(), "SVIP", "S" + (19 + vipBean.getGrade()), -1, vipBean.getIncreationtext());
//                        roleData.addLimit(limit);
//                        applyPayBean.setVIPBean(limit);
//                    } else if (!limit.getName().equals("VIP" + vipBean.getGrade())) {
//                        limit.setName("VIP" + vipBean.getGrade());
//                        limit.setSkin("S" + (19 + vipBean.getGrade()));
//                        limit.setValue(vipBean.getIncreationtext());
//                        applyPayBean.setVIPBean(limit);
//                    }
//                }
                if (type == 2) {
                    long time = 1000L * 60L * 60L * 24L * 30;
                    if (time != 0 && roleData != null) {
                        UseCardBean limit = roleData.getLimit("VIP");
                        if (limit != null) {
                            limit.setTime(limit.getTime() + time);
                        } else {
                            limit = new UseCardBean("VIP", "VIP", "1", System.currentTimeMillis() + time, "掉落率=1|经验加成=5|加强全系法术=5|召唤兽死亡不掉忠诚,血法|人物死亡惩罚减半");
                            roleData.addLimit(limit);
                        }
                        applyPayBean.setUseCardBean(limit);
                        applyPayBean.setMsg("激活了" + (time / 1000 / 60 / 60 / 24) + "天VIP特权");
                    }
                } else if (type == 3 && login.getLowOrHihtpack() == 0) {
                    login.setLowOrHihtpack(1);
                    applyPayBean.setLowOrHihtpack(new BigDecimal(1));
                    applyPayBean.setMsg("开通了小资冲级礼包");
                } else if (type == 4 && login.getLowOrHihtpack() == 0) {
                    login.setLowOrHihtpack(2);
                    applyPayBean.setLowOrHihtpack(new BigDecimal(2));
                    applyPayBean.setMsg("开通了土豪冲级礼包");
                } else {
                    applyBean.setPaymoney(expensesReceipts.getYuanbao() + "");// 充值的元宝数量
                    login.setCodecard(login.getCodecard().add(new BigDecimal(applyBean.getPaymoney())));
                    MonitorUtil.getMoney().addX(new BigDecimal(applyBean.getPaymoney()).longValue(), 0);
                    MonitorUtil.getMoney().addC(addC.longValue());
                    login.setMoney((login.getMoney() != null ? login.getMoney() : 0) + addC.intValue());
                    applyPayBean.setAddX(new BigDecimal(applyBean.getPaymoney()));
                    applyPayBean.setAddC(addC);
                    if (addC.longValue() >= 30 && login.getDayfirstinorno() == 0) {// 在线充值
                        // 添加连充天数
                        login.setDayandpayorno(login.getDayandpayorno().add(new BigDecimal(1)));
                        login.setDayfirstinorno(1);
                        applyPayBean.setDayandpayorno(login.getDayandpayorno());
                    }
                    StringBuffer buffer = new StringBuffer();
                    if (type == 3 || type == 4) {
                        buffer.append("小资冲级礼包和土豪冲级礼包只能同时拥有一个,你已经有了");
                        buffer.append(login.getLowOrHihtpack() == 2 ? "土豪冲级礼包" : "小资冲级礼包");
                        buffer.append("本次充值变为正常仙玉充值.");
                    }
                    buffer.append("你充值积分:");
                    buffer.append(addC.intValue());
                    buffer.append(",获得仙玉:");
                    buffer.append(applyBean.getPaymoney());
                    applyPayBean.setMsg(buffer.toString());
                }
                // 在线也要同步数据库
                AllServiceUtil.getRoleTableService().updateRoleWhenExit(login);
                SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().applyPay(GsonUtil.getGsonUtil().getgson().toJson(applyPayBean)));
                // 确保第一次处理订单(确保充值成功)
                jedis.hset("order_number_control_orno", expensesReceipts.getErid() + "", expensesReceipts.getPaytime() + ":金额" + expensesReceipts.getRecharge());
                jedis.hset("payReturnForpayServer", expensesReceipts.getErid() + "", "Sinmahod" + "=" + GsonUtil.getGsonUtil().getgson().toJson(expensesReceipts) + "");
            } else {// 不在线充值
                if (expensesReceipts.getRoleid() != null) {
                    login = AllServiceUtil.getRoleTableService().selectRoleID(expensesReceipts.getRoleid());
                } else {
                    List<LoginResult> loginResults = AllServiceUtil.getUserTableService().findRoleByUserNameAndPassword(applyBean.getUserNameS(), null, null);
                    if (loginResults.size() != 0) {
                        login = loginResults.get(0);
                    }
                }
                if (login != null) {
                    login.setPaysum(login.getPaysum().add(addC));// 累计充值
                    login.setDaypaysum(login.getDaypaysum().add(addC));// 每日累计充值
                    if (StringUtil.isNotEmpty(count) && !"undefined".equals(count)) {
                        LaborScene.addRankValue(0, Integer.parseInt(count) * 10, login);//劳动节活动
                    }
                    expensesReceipts.setRoleid(login.getRole_id());
                    expensesReceipts.setBuyroleName(login.getRolename());
                    if (type == 2) {
                        long time = 1000L * 60L * 60L * expensesReceipts.getRecharge().intValue();
                        PrivateData privateData = new PrivateData();
                        privateData.setTimingGood(login.getTimingGood());
                        ConcurrentHashMap<String, UseCardBean> limitMap = privateData.initLimit(0);
                        UseCardBean limit = limitMap.get("VIP");
                        if (limit != null) {
                            limit.setTime(limit.getTime() + time);
                        } else {
                            limit = new UseCardBean("VIP", "VIP", "1", System.currentTimeMillis() + time, "掉落率=1|经验加成=5|加强全系法术=5|召唤兽死亡不掉忠诚,血法|人物死亡惩罚减半");
                            limitMap.put("VIP", limit);
                        }
                        StringBuffer buffer = new StringBuffer();
                        for (UseCardBean cardBean : limitMap.values()) {
                            if (buffer.length() != 0) {
                                buffer.append("^");
                            }
                            buffer.append(cardBean.getName());
                            buffer.append("#");
                            buffer.append(cardBean.getType());
                            buffer.append("#");
                            buffer.append(cardBean.getSkin());
                            buffer.append("#");
                            buffer.append(cardBean.getTime() / 60000);
                            if (cardBean.getValue() != null && !cardBean.getValue().equals("")) {
                                buffer.append("#");
                                buffer.append(cardBean.getValue());
                            }
                        }
                        login.setTimingGood(buffer.toString());
                    } else if (type == 3 && login.getLowOrHihtpack() == 0) {
                        login.setLowOrHihtpack(1);
                    } else if (type == 4 && login.getLowOrHihtpack() == 0) {
                        login.setLowOrHihtpack(2);
                    } else {
                        applyBean.setPaymoney(expensesReceipts.getYuanbao() + "");// 充值的元宝数量
                        userTable.setCodecard(userTable.getCodecard().add(new BigDecimal(applyBean.getPaymoney())));
                        userTable.setMoney(userTable.getMoney() + addC.intValue());
                        MonitorUtil.getMoney().addX(new BigDecimal(applyBean.getPaymoney()).longValue(), 0);
                        MonitorUtil.getMoney().addC(addC.longValue());
                        if (addC.longValue() >= 30 && login.getDayfirstinorno() == 0) {// 在线充值
                            // 添加连充天数
                            login.setDayandpayorno(login.getDayandpayorno().add(new BigDecimal(1)));
                            login.setDayfirstinorno(1);
                        }
                    }
                    try {
                        AllServiceUtil.getRoleTableService().updateRoleWhenExit(login);
                    } catch (Exception e) {
                        WriteOut.addtxt("人物数据保存报错:" + GsonUtil.getGsonUtil().getgson().toJson(login), 9999);
                    }
                } else {
                    userTable.setCodecard(userTable.getCodecard().add(new BigDecimal(applyBean.getPaymoney())));
                    userTable.setMoney(userTable.getMoney() + addC.intValue());
                    MonitorUtil.getMoney().addX(new BigDecimal(applyBean.getPaymoney()).longValue(), 0);
                    MonitorUtil.getMoney().addC(addC.longValue());
                }
                AllServiceUtil.getUserTableService().updateUser(userTable);
                jedis.hset("order_number_control_orno", expensesReceipts.getErid() + "", expensesReceipts.getPaytime() + ":金额" + expensesReceipts.getRecharge());
            }
        } catch (Exception e) {
            e.printStackTrace();
            WriteOut.addtxt("充值报错:" + MainServerHandler.getErrorMessage(e), 9999);
        }
        RedisPoolUntil.returnResource(jedis);
        //充值日志
        AllServiceUtil.getRecordService().insert(new Record(8, GsonUtil.getGsonUtil().getgson().toJson(expensesReceipts)));
        return true;
    }
    //退出战斗
    @UserToken
    @PostMapping(value = "/api/remove/battle")
    public Result removeBattle(Param param, HttpServletRequest request)  {
        // 获取用户名密码格式为 用户名|&|密码
        Result ipCheckResult = UserController.IPstop(request);
        if (ipCheckResult != null) {
            return ipCheckResult;
        }
        if (BattleThreadPool.BattleDatas.values().size() > 0) {
            BattleData battleData = BattleThreadPool.BattleDatas.values().stream().filter(f -> f.getRoleId1().equals(new BigDecimal(param.getValue1()))).findFirst().get();
            if (battleData != null) {
                BattleThreadPool.removeBattleData(battleData);
            }
        }
        return ResultFactory.success("暂时不可用！！！");
    }
}
