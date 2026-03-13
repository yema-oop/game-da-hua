package com.gl.controller;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.github.pagehelper.PageInfo;
import com.gl.model.Param;
import com.gl.model.Result;
import com.gl.model.UpPetParam;
import com.gl.service.PlayerService;
import com.gl.service.ResultFactory;
import com.gl.token.UserToken;
import come.tool.Stall.AssetUpdate;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.come.action.reward.DrawnitemsAction;
import org.come.bean.LoginResult;
import org.come.entity.*;
import org.come.handler.SendMessage;
import org.come.mapper.RoleTableMapper;
import org.come.model.Skill;
import org.come.protocol.Agreement;
import org.come.redis.RedisControl;
import org.come.server.GameServer;
import org.come.tool.Arith;
import org.come.until.AllServiceUtil;
import org.come.until.GsonUtil;
import org.come.until.MybatisUntil;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import static come.tool.Good.UsePetAction.mathDouble;

@RestController
public class AdminPetController {
    private ConcurrentHashMap<String, Goodstable> nds = new ConcurrentHashMap<String, Goodstable>();
    @UserToken
    @PostMapping(value = "/api/cb/updUserPet")
    public Result cbUpdUserPet(UpPetParam param, HttpServletRequest request) throws UnsupportedEncodingException {
        Result ipCheckResult = UserController.IPstop(request);
        if (ipCheckResult != null) {
            return ipCheckResult;
        }
        String ndsParam = URLUtil.decode(param.getNds());
        String jineng = URLUtil.decode(param.getJineng());
        param.setJineng(jineng);
        String qljineng = URLUtil.decode(param.getQljineng());
        param.setQljineng(qljineng);
        param.setNds(ndsParam);
        RoleSummoning roleSummoning = AllServiceUtil.getRoleSummoningService().selectRoleSummoningsByRgID(new BigDecimal(param.getSid()));
        if (roleSummoning == null) {
            return ResultFactory.fail("未找到对应的召唤兽！");
        }
        roleSummoning.setGrowlevel(param.getGrowlevel());
        RoleSummoning pet = GameServer.getPet(new BigDecimal(roleSummoning.getSummoningid()));
        roleSummoning.setTurnRount(param.getTurnRount());
//        roleSummoning.setGrowlevel(pet.getGrowlevel());
        if(pet.getGrowlevel().equals(param.getGrowlevel())){
            for (int i = 0; i < param.getTurnRount(); i++) {
                BigDecimal grow = mathDouble(Double.parseDouble(roleSummoning.getGrowlevel()), 0.1);
                roleSummoning.setGrowlevel(Arith.xiaoshu3(grow.doubleValue()));
            }
        }else{
            roleSummoning.setGrowlevel(param.getGrowlevel());
        }
        //增加技能
        roleSummoning.setOpenSeal(Objects.isNull(param.getOpenSeal()) ? 1:param.getOpenSeal() > 15 ? 15:param.getOpenSeal());
        roleSummoning.setOpenql(Objects.isNull(param.getOpenql()) ? 1:param.getOpenql() > 6 ? 6:param.getOpenql());
        if(StrUtil.isNotBlank(param.getJineng())){
            int count =0;
            if (StrUtil.isNotBlank(roleSummoning.getPetSkills())) {
                count  = Arrays.stream(roleSummoning.getPetSkills().split("\\|")).collect(Collectors.toList()).size();
            }
            Integer openSeal = roleSummoning.getOpenSeal();
            if(openSeal > count){
                ConcurrentHashMap<String, Skill> getSkill = GameServer.getGetSkill();
                Map<String, Skill> collect = getSkill.values().stream().collect(Collectors.toMap(Skill::getSkillname, Function.identity(),(key,key1)->key1));
                Skill skill = collect.get(param.getJineng());
                if(Objects.nonNull(skill)){
                    if(StrUtil.isNotBlank(roleSummoning.getPetSkills())){
                        boolean contains = roleSummoning.getPetSkills().contains(skill.getSkillid() + "");
                        if(!contains){
                            String concat = roleSummoning.getPetSkills().concat("|").concat(skill.getSkillid() + "");
                            roleSummoning.setPetSkills(concat);
                        }
                    }else{
                        String concat =(skill.getSkillid() + "");
                        roleSummoning.setPetSkills(concat);
                    }
                }
            }
        }

        if(StrUtil.isNotBlank(param.getQljineng())){
            int count =0;
            if (StrUtil.isNotBlank(roleSummoning.getPetQlSkills())) {
                count  = Arrays.stream(roleSummoning.getPetQlSkills().split("\\|")).collect(Collectors.toList()).size();
            }
            Integer openSeal = roleSummoning.getOpenql();
            if(openSeal > count){
                ConcurrentHashMap<String, Skill> getSkill = GameServer.getGetSkill();
                Map<String, Skill> collect = getSkill.values().stream().collect(Collectors.toMap(Skill::getSkillname, Function.identity(),(key,key1)->key1));
                Skill skill = collect.get(param.getQljineng());
                if(Objects.nonNull(skill)) {
                    if (StrUtil.isNotBlank(roleSummoning.getPetQlSkills())) {
                        boolean contains = roleSummoning.getPetSkills().contains(skill.getSkillid() + "");
                        if (!contains) {
                            String concat = roleSummoning.getPetQlSkills().concat("|").concat(skill.getSkillid() + "");
                            roleSummoning.setPetQlSkills(concat);
                        }
                    } else {
                        String concat = (skill.getSkillid() + "");
                        roleSummoning.setPetQlSkills(concat);
                    }
                }
            }
        }

        roleSummoning.setSkill(param.getSkill());
        Integer petLvl = getPetLvl(param.getTurnRount());
        roleSummoning.setFriendliness(param.getFriendliness());
        roleSummoning.setGrade(param.getGrade() + petLvl + 1);
        roleSummoning.setBone(param.getGrade());
        roleSummoning.setSpir(param.getGrade());
        roleSummoning.setPower(param.getGrade());
        roleSummoning.setSpeed(param.getGrade());



        if(param.getDragon() > 0 && param.getDragon() != roleSummoning.getDragon()){
            roleSummoning.setDragon(param.getDragon());
            roleSummoning.setGrowlevel(mathDouble(Double.parseDouble(roleSummoning.getGrowlevel()), (param.getDragon()*0.01)).toString());// 成长率加0.01
            int lgcha = param.getDragon() - roleSummoning.getDragon();
            for (int i = 0; i < lgcha; i++) {
                while (true) {// 随机给这只召唤兽的hp、mp、ap、sp随机加6点
                    String four = roleSummoning.getFourattributes();
                    int ran1 = GameServer.random.nextInt(7);
                    int ran2 = GameServer.random.nextInt(7);
                    int ran3 = GameServer.random.nextInt(7);
                    int ran4 = GameServer.random.nextInt(7);
                    if (ran1 + ran2 + ran3 + ran4 == 6) {
                        roleSummoning.setHp(roleSummoning.getHp().add(BigDecimal.valueOf(ran1)));
                        roleSummoning.setMp(roleSummoning.getMp().add(BigDecimal.valueOf(ran2)));
                        roleSummoning.setAp(roleSummoning.getAp().add(BigDecimal.valueOf(ran3)));
                        roleSummoning.setSp(roleSummoning.getSp().add(BigDecimal.valueOf(ran4)));
                        if (ran1 != 0) {
                            four = DrawnitemsAction.Splice(four, "hp=" + ran1, 2);
                        }
                        if (ran2 != 0) {
                            four = DrawnitemsAction.Splice(four, "mp=" + ran2, 2);
                        }
                        if (ran3 != 0) {
                            four = DrawnitemsAction.Splice(four, "ap=" + ran3, 2);
                        }
                        if (ran4 != 0) {
                            four = DrawnitemsAction.Splice(four, "sp=" + ran4, 2);
                        }
                        roleSummoning.setFourattributes(four);
                        break;
                    }
                }
            }

        }


        if(param.getSpdragon() >0 && param.getSpdragon()!= roleSummoning.getSpdragon()){
            roleSummoning.setSpdragon(param.getSpdragon());
            roleSummoning.setGrowlevel(mathDouble(Double.parseDouble(pet.getGrowlevel()), (param.getSpdragon()*0.01)).toString());// 成长率加0.01
            int lgcha = param.getSpdragon() - roleSummoning.getSpdragon();
            for (int i = 0; i < lgcha; i++) {
                while (true) {// 随机给这只召唤兽的hp、mp、ap、sp随机加6点
                    String four = roleSummoning.getFourattributes();
                    int ran1 = GameServer.random.nextInt(7);
                    int ran2 = GameServer.random.nextInt(7);
                    int ran3 = GameServer.random.nextInt(7);
                    int ran4 = GameServer.random.nextInt(7);
                    if (ran1 + ran2 + ran3 + ran4 == 10) {
                        roleSummoning.setHp(roleSummoning.getHp().add(BigDecimal.valueOf(ran1)));
                        roleSummoning.setMp(roleSummoning.getMp().add(BigDecimal.valueOf(ran2)));
                        roleSummoning.setAp(roleSummoning.getAp().add(BigDecimal.valueOf(ran3)));
                        roleSummoning.setSp(roleSummoning.getSp().add(BigDecimal.valueOf(ran4)));
                        if (ran1 != 0) {
                            four = DrawnitemsAction.Splice(four, "hp=" + ran1, 2);
                        }
                        if (ran2 != 0) {
                            four = DrawnitemsAction.Splice(four, "mp=" + ran2, 2);
                        }
                        if (ran3 != 0) {
                            four = DrawnitemsAction.Splice(four, "ap=" + ran3, 2);
                        }
                        if (ran4 != 0) {
                            four = DrawnitemsAction.Splice(four, "sp=" + ran4, 2);
                        }
                        roleSummoning.setFourattributes(four);
                        break;
                    }
                }
            }
        }


//        roleSummoning.setOtherPoint(param.getOtherPoint());
        List<Goodstable> eqGoods = null;
        LoginResult loginResult = AllServiceUtil.getRoleTableService().selectRoleByRoleId(roleSummoning.getRoleid());

        if (StringUtils.isNotBlank(param.getSkill())) {

            if (StringUtils.isNotBlank(roleSummoning.getStye())) {
                eqGoods = new ArrayList<>();
                String[] v = roleSummoning.getSkill().split("\\|");
                for (int i = 1; i < v.length; i++) {
                    String[] vs = v[i].split("-");
                    if (vs.length >= 2) {
                        Goodstable good = AllServiceUtil.getGoodsTableService().getGoodsByRgID(new BigDecimal(vs[1]));
                        eqGoods.add(good);
                    }
                }
            }

            for (Goodstable eqGood : eqGoods) {
                String[] val = eqGood.getValue().split("\\|");
                int index = -1;
                for (int i = 0; i < val.length; i++) {
                    if (val[i].startsWith("觉醒技")) {
                        index = i;
                        break;
                    }
                }
                String jxSkill = "";
                if (index != -1) {
                    String[] split = val[index].split("&");
                    split[1] = param.getSkill();
                    jxSkill = ArrayUtil.join(split, "&");
                }
                val[index] = jxSkill;
                eqGood.setValue(ArrayUtil.join(val, "|"));
                AllServiceUtil.getGoodsTableService().updateGoodRedis(eqGood);
                if (loginResult != null) {
                    ChannelHandlerContext channelHandlerContext = GameServer.getRoleNameMap().get(loginResult.getRolename());
                    if (channelHandlerContext != null) {
                        AssetUpdate update = new AssetUpdate();
                        // 添加返回bean
                        List<Goodstable> goodstables = new ArrayList<>();
                        goodstables.add(eqGood);
                        update.setGoods(goodstables);
                        update.setType(AssetUpdate.GIVE);
                        update.setMsg(":#R多功能后台修改物品修改成功#23");
                        // 发送前端取回的东西
                        SendMessage.sendMessageToSlef(channelHandlerContext, Agreement.getAgreement().assetAgreement(GsonUtil.getGsonUtil().getgson().toJson(update)));
                    }
                }
            }
        }
        AssetUpdate update = new AssetUpdate();

        if (param.getLx() != null) {
            roleSummoning.setLingxi(getLx(param.getLx() - 1));
        }

        List<Goodstable> goodstables = new ArrayList<>();
        if (StringUtils.isNotBlank(param.getNds())) {
            String[] split = param.getNds().split("\\|");
            for (String nd : split) {
                GameServer.getAllGoodsMap().forEach((k, v) -> {
                    if (v.getGoodsname().equals(nd)) {
                        String[] split1 = v.getValue().split("\\|");
                        if(split1.length>2){
                            nds.put(nd, v);

                        }
                    }
                });
            }
            //原来的内丹删掉
            if (StringUtils.isNotBlank(roleSummoning.getInnerGoods())) {
                for (String s : roleSummoning.getInnerGoods().split("\\|")) {
                    Goodstable dbGood = AllServiceUtil.getGoodsTableService().getGoodsByRgID(new BigDecimal(s));
                    dbGood.goodxh(1);
                    AllServiceUtil.getGoodsTableService().updateGoodRedis(dbGood);
                    goodstables.add(dbGood);
                }
            }
            String[] ndIds = new String[split.length];
            //替换新的内丹
            for (int i = 0; i < split.length; i++) {
                Goodstable goodstable = nds.get(split[i]);
                String[] split1 = goodstable.getValue().split("\\|");

                split1[2] = "内丹等级=4转180";
                goodstable.setValue(ArrayUtil.join(split1, "|"));

                goodstable.setUsetime(1);
                if (loginResult != null) {
                    goodstable.setRole_id(loginResult.getRole_id());
                }
                goodstable.setStatus(1);
                AllServiceUtil.getGoodsTableService().insertGoods(goodstable);
                ndIds[i] = goodstable.getRgid().toString();
                goodstables.add(goodstable);
            }
            String join = ArrayUtil.join(ndIds, "|");
            roleSummoning.setInnerGoods(join);
            update.setGoods(goodstables);
        }


        AllServiceUtil.getRoleSummoningService().updateRoleSummoning(roleSummoning);
        if (loginResult != null) {
            ChannelHandlerContext channelHandlerContext = GameServer.getRoleNameMap().get(loginResult.getRolename());
            if (channelHandlerContext != null) {
                // 添加返回bean
                List<RoleSummoning> roleSummonings = new ArrayList<>();

                roleSummonings.add(roleSummoning);
                update.setPets(roleSummonings);
                update.setType(AssetUpdate.USEGOOD);
                update.setMsg(":#R多功能后台修改宠物成功#23");
                // 发送前端取回的东西
                SendMessage.sendMessageToSlef(channelHandlerContext, Agreement.getAgreement().assetAgreement(GsonUtil.getGsonUtil().getgson().toJson(update)));
            }
        }
        return ResultFactory.success(null);
    }

    public Integer getPetLvl(int zs) {
        if (zs == 1) {
            return 100;
        } else if (zs == 2) {
            return 221;
        } else if (zs == 3) {
            return 362;
        } else if (zs == 4) {
            return 543;
        } else if (zs == 0) {
            return 0;
        } else if (zs == 5) {
            return 744;
        }else if (zs == 6) {
            return 945;
        }else if (zs == 7) {
            return 1146;
        }else if (zs == 8) {
            return 1347;
        }else if (zs == 9) {
            return 1548;
        }else if (zs == 10) {
            return 1749;
        }else if (zs == 11) {
            return 1950;
        }else if (zs == 12) {
            return 2151;
        }else if (zs == 13) {
            return 2352;
        }else if (zs == 14) {
            return 2553;
        }else if (zs == 15) {
            return 2754;
        }else if (zs == 16) {
            return 2955;
        }else if (zs == 17) {
            return 3156;
        }else if (zs == 18) {
            return 3357;
        }else if (zs == 19) {
            return 3558;
        }else if (zs == 20) {
            return 3759;
        }else if (zs == 21) {
            return 3960;
        }else if (zs == 22) {
            return 4161;
        }else if (zs == 23) {
            return 4362;
        }else if (zs == 24) {
            return 4563;
        }else if (zs == 25) {
            return 4764;
        }else if (zs == 26) {
            return 4965;
        }else if (zs == 27) {
            return 5166;
        }else if (zs == 28) {
            return 5367;
        }else if (zs == 29) {
            return 5568;
        }else if (zs == 30) {
            return 5769;
        }else if (zs == 31) {
            return 5970;
        }else if (zs == 32) {
            return 6171;
        }else if (zs == 33) {
            return 6372;
        }
        return 0;
    }

    private String getLx(Integer type) {
        String lx = "";
        if (type == 0) {
            lx = "11001_0|11002_0|11003_0|11004_0|11005_0|11006_0|11007_0|11026_0|11045_0|11046_0" + "|11008_0" + "|11009_0" + "|11010_0" + "|11011_0" + "|11012_0" + "|11013_0" + "|11014_0" + "|11015_0" + "|11016_0" + "|11017_0" + "|11018_0" + "|11019_0" + "|11020_0" + "|11021_0" + "|11022_0" + "|11023_0" + "|11024_0" + "|11025_0" + "|11047_0" + "|11049_0" + "|11051_0" + "|11053_0" + "|11055_0" + "|11057_0" + "|11062_0" + "|11063_0" + "|11061_0" + "|11060_0" + "|11058_0" + "|11059_0" + "|11056_0" + "|11054_0" + "|11052_0" + "|11050_0" + "|11048_0" + "|11027_0" + "|11028_0" + "|11029_0" + "|11031_0" + "|11032_0" + "|11033_0" + "|11034_0" + "|11035_0" + "|11036_0" + "|11030_0" + "|11037_0" + "|11042_0" + "|11039_0" + "|11043_0" + "|11044_0" + "|11040_0" + "|11041_0";
        } else if (type == 1) {
            lx = "11003_0|11001_0|11004_0|11005_0|11006_0|11007_0|11008_0|11009_0|11010_0|11011_0|11012_0|11013_0|11016_0|11018_0|11013_0|11015_0|11017_0|11019_0|11020_0|11020_0|11021_0|11022_0|11023_0|11024_0|11025_0";
        } else if (type == 2) {
            lx = "11001_0|11004_0|11002_0|11005_0|11007_0|11026_0|11027_0|11028_0|11029_0|11031_0|11033_0|11035_0|11036_0|11032_0|11034_0|11030_0|11037_0|11039_0|11040_0|11041_0|11042_0|11043_0|11044_0";
        } else if (type == 3) {
            lx = "11001_0|11004_0|11002_0|11005_0|11046_0|11047_0|11048_0|11049_0|11050_0|11052_0|11054_0|11056_0|11051_0|11053_0|11055_0|11057_0|11058_0|11059_0|11060_0|11061_0|11062_0|11063_0";
        }
        String[] lhHead = {"Lx=0", "Lv=0", "Point=0", "Open="};
        String[] skillIds = lx.split("\\|");
        String[] lxs = new String[skillIds.length];
        int count = 0;
        for (int i = 0; i < skillIds.length; i++) {
            Skill skill = GameServer.getSkill(skillIds[i].split("_")[0]);
            lxs[i] = skill.getSkillid() + "_" + (int) (skill.getValue());
            count += (int) (skill.getValue());
        }
        lhHead[2] = "Point=" + count;
        lhHead[0] = "Lx=" + type;
        String join = ArrayUtil.join(lxs, "|");
        String join1 = ArrayUtil.join(lhHead, "&");
        return join1 + join;
    }


    @UserToken
    @PostMapping(value = "/api/getUserIp")
    public Result getUserIpLists(Param param) {
        return ResultFactory.success(RedisControl.getUserIpList(param.getPageNum(),param.getPageSize(),param.getRoleName()));
    }


    @UserToken
    @PostMapping(value = "/api/fengjin/ip")
    public Result fengJinIp(Param param) {
        RedisControl.fengJinIp(param.getValue1());
        PlayerService playerService = new PlayerService();
        param.setValue2("2");
        String roleName = URLUtil.decode(param.getRoleName());
        param.setValue1(roleName);
        playerService.operation(param);
        return ResultFactory.success("成功！！！");
    }

    @UserToken
    @PostMapping(value = "/api/jiefeng/ip")
    public Result jieFengIp(Param param) {
        RedisControl.jieFengIp(param.getValue1());
        return ResultFactory.success("成功！！！");
    }

    @UserToken
    @PostMapping(value = "/api/fengjin/getUserIp")
    public Result getfengjinUserIpLists(Param param) {
        return ResultFactory.success(RedisControl.getFengJinIpList(param.getPageNum(),param.getPageSize(),param.getRoleName()));
    }


    @UserToken
    @PostMapping(value = "/api/selectWeChatrecord")
    public Result selectWeChatrecord(Param param) {
        String ndsParam = URLUtil.decode(param.getValue2());
        param.setValue2(ndsParam);
        List<Wechatrecord> wechatrecords = AllServiceUtil.getWechatrecordService().selectAll(param);
        PageInfo<Wechatrecord> pageInfo = new PageInfo<>(wechatrecords);
        return ResultFactory.success(pageInfo);
    }

    @UserToken
    @PostMapping("/api/upGrade")
    public Result grade(Param param) {
//        ApplicationContext ctx = MybatisUntil.getApplicationContext();
//        // id为类名且首字母小写才能被自动扫描扫到
//        RoleTableMapper roleTableMapper = ctx.getBean("roleTableMapper", RoleTableMapper.class);
        BigDecimal roleId = new BigDecimal(param.getValue1());
        AllServiceUtil.getRoleTableService().updateRoleFullGrade(roleId);
//        roleTableMapper.updateRoleFullGrade(roleId);
        return ResultFactory.success(null);
    }

    @UserToken
    @PostMapping("/api/fullGrade")
    public Result fullGrade(Param param) {
        String type = param.getValue3();
        String rolename = param.getValue2();
        BigDecimal roleId = new BigDecimal(param.getValue1());
        if (type != null) {
            switch (type) {
                case "L":
                    fullLingbao(roleId);
                case "S":
                    zhs(roleId);
                    break;
                case "M":
                    fullMount(roleId);
                    break;
                case "B":
                    fullBaby(roleId);
                    break;
                default:
                    break;
            }
            if (GameServer.getRoleNameMap().get(rolename) != null) {
                SendMessage.sendMessageByRoleName(rolename, Agreement.getAgreement().serverstopAgreement());
            }
        }
        return ResultFactory.success(null);
    }


    private void fullLingbao(BigDecimal roleId) {
        List<Lingbao> lingbaos = AllServiceUtil.getLingbaoService().selectLingbaoByRoleID(roleId);
        if (lingbaos.size() > 0) {
            List<Lingbao> collect = lingbaos.stream().filter(f -> !f.getLingbaolvl().equals(new BigDecimal(200))).collect(Collectors.toList());
            for (Lingbao r : collect) {

                r.setLingbaolvl(new BigDecimal(200));
                r.setSkillsum(5);
                r.setFusum(5);
                r.setLingbaoexe(new BigDecimal(491985));

                AllServiceUtil.getLingbaoService().updateLingbaoRedis(r);
                System.out.println("满级灵宝" + r.getBaoname());
            }
        }
    }

    private void fullMount(BigDecimal roleId) {
        List<Mount> mounts = AllServiceUtil.getMountService().selectMountsByRoleID(roleId);

        List<Mount> collect = mounts.stream().filter(f -> !f.getMountlvl().equals(200)).collect(Collectors.toList());
        for (Mount m : collect) {
            // 统一将 mountlvl 设置为 200
            m.setMountlvl(200);
            m.setLive(100);
            if (m.getMountid().equals(1)) {
                m.setSpri(BigDecimal.valueOf(12));
                m.setPower(BigDecimal.valueOf(24));
                m.setBone(BigDecimal.valueOf(12));
            } else if (m.getMountid().equals(2)) {
                m.setSpri(BigDecimal.valueOf(12));
                m.setPower(BigDecimal.valueOf(18));
                m.setBone(BigDecimal.valueOf(18));
            } else if (m.getMountid().equals(3)) {
                m.setSpri(BigDecimal.valueOf(24));
                m.setPower(BigDecimal.valueOf(12));
                m.setBone(BigDecimal.valueOf(12));
            } else if (m.getMountid().equals(4)) {
                m.setSpri(BigDecimal.valueOf(18));
                m.setPower(BigDecimal.valueOf(12));
                m.setBone(BigDecimal.valueOf(18));
            } else if (m.getMountid().equals(5)) {
                m.setSpri(BigDecimal.valueOf(12));
                m.setPower(BigDecimal.valueOf(12));
                m.setBone(BigDecimal.valueOf(24));
            } else if (m.getMountid().equals(6)) {
                m.setSpri(BigDecimal.valueOf(3));
                m.setPower(BigDecimal.valueOf(27));
                m.setBone(BigDecimal.valueOf(9));
            } else if (m.getMountid().equals(7)) {
                m.setSpri(BigDecimal.valueOf(16));
                m.setPower(BigDecimal.valueOf(10));
                m.setBone(BigDecimal.valueOf(31));
            }

            m.setExp(1000000);
            m.setProficiency(150000);
            AllServiceUtil.getMountService().updateMount(m);
            System.out.println("满级坐骑" + m.getMountname());
        }
    }


    private void fullBaby(BigDecimal roleId) {
        List<Baby> babies = AllServiceUtil.getBabyService().selectBabyByRolename(roleId);
        List<Baby> collect = babies.stream().filter(f -> !f.getBabyage().equals(new BigDecimal(6710))).collect(Collectors.toList());
        for (Baby b : collect) {
            b.setQingmi(400);
            b.setNeili(1000);
            b.setZhili(1000);
            b.setNaili(1000);
            b.setQizhi(1000);
            b.setMingqi(1000);
            b.setDaode(400);
            b.setXiaoxin(400);
            b.setWenbao(400);
            b.setBabyage(6710);
            AllServiceUtil.getBabyService().updateBabyRedis(b);
            System.out.println("满级孩子" + b.getBabyname());
        }
    }

    private void zhs(BigDecimal roleId) {
        List<RoleSummoning> roleSummonings =
                AllServiceUtil.getRoleSummoningService().selectRoleSummoningsByRoleID(roleId);
        if (CollectionUtils.isNotEmpty(roleSummonings)) {
            List<RoleSummoning> collect = roleSummonings.stream().filter(f -> f.getGrade() != Integer.parseInt("744")).collect(Collectors.toList());
            for (RoleSummoning r : collect) {
                mockZs(r, roleId);
            }
        }
    }


    private void mockZs(RoleSummoning pet, BigDecimal roleId) {
        int petTurn = pet.getTurnRount();
        //设置这只召唤兽的根骨、灵性、力量、敏捷、经验为0
        pet.setBone(200);
        pet.setSpir(200);
        pet.setPower(200);
        pet.setSpeed(200);
        pet.setCalm(200);
        pet.setExp(new BigDecimal(999999999));
//        pet.setSummoningname("GM修改版");
//        pet.setDragon(10000);//：设置宠物的龙之骨属性值为3。
//        pet.setSpdragon(10000);//：设置宠物的超级龙之骨属性值为3。
//        pet.setOpenSeal(9);//开启技能数
//        pet.setPetSkills("1232|1220|1831|1827|1833|6020");//技能
        //等级
        pet.setGrade(744);
        pet.setTurnRount(4);

        //设置忠诚度为100
        pet.setFaithful(100);
        pet.setFriendliness(99999999L);
        pet.setOpenSeal(6);
        int g = 0;
        if (petTurn < 3) {
            g = 3 - petTurn;
        }
        BigDecimal grow = mathDouble(Double.parseDouble(pet.getGrowlevel()), g * 0.1);

        pet.setGrowlevel(Arith.xiaoshu3(grow.doubleValue()));
        pet.setBasishp(BigDecimal.ZERO);
        pet.setBasismp(BigDecimal.ZERO);


        //设置灵犀
        String fz = "Lx=3&Lv=0&Point=110&Open=11001_5|11002_0|11003_0|11004_5|11005_5|11006_0|11007_0|11026_0|11045_5|11046_0|11008_0|11009_0|11010_0|11011_0|11012_0|11013_0|11014_0|11015_0|11016_0|11017_0|11018_0|11019_0|11020_0|11021_0|11022_0|11024_0|11023_0|11025_0|11027_0|11028_0|11029_0|11031_0|11032_0|11033_0|11034_0|11035_0|11030_0|11036_0|11037_0|11039_0|11040_0|11041_0|11042_0|11043_0|11044_0|11047_3|11048_3|11049_3|11050_0|11051_3|11052_0|11053_3|11054_0|11056_0|11057_4|11058_0|11059_0|11055_3|11060_0|11061_30|11062_30|11063_0";
        String fs = "Lx=2&Lv=0&Point=110&Open=11001_4|11002_5|11003_0|11004_5|11005_0|11006_0|11007_5|11026_5|11045_0|11046_0|11008_0|11009_0|11010_0|11011_0|11012_0|11013_0|11014_0|11015_0|11016_0|11017_0|11018_0|11019_0|11020_0|11021_0|11022_0|11024_0|11023_0|11025_0|11027_3|11028_3|11029_3|11031_3|11032_0|11033_3|11034_0|11035_3|11030_0|11036_4|11037_0|11039_0|11040_0|11041_0|11042_30|11043_30|11044_0|11047_0|11048_0|11049_0|11050_0|11051_0|11052_0|11053_0|11054_0|11056_0|11057_0|11058_0|11059_0|11055_0|11060_0|11061_0|11062_0|11063_0";
        String gj = "Lx=1&Lv=0&Point=110&Open=11001_5|11002_0|11003_5|11004_5|11005_5|11006_5|11007_5|11026_0|11045_0|11046_0|11008_0|11009_1|11010_3|11011_3|11012_0|11013_3|11014_3|11015_0|11016_0|11017_3|11018_4|11019_0|11020_0|11021_0|11022_0|11024_0|11023_30|11025_30|11027_0|11028_0|11029_0|11031_0|11032_0|11033_0|11034_0|11035_0|11030_0|11036_0|11037_0|11039_0|11040_0|11041_0|11042_0|11043_0|11044_0|11047_0|11048_0|11049_0|11050_0|11051_0|11052_0|11053_0|11054_0|11056_0|11057_0|11058_0|11059_0|11055_0|11060_0|11061_0|11062_0|11063_0";
        if (pet.getStye().equals("0")) {
            pet.setLingxi(gj);
            AllServiceUtil.getRoleSummoningService().updatePetRedis(pet);
            return;
        }
        Set<String> fsset = new HashSet<>();
        fsset.add("颜如玉");
        fsset.add("妙音鸾女");
        fsset.add("去疾");
        fsset.add("乘黄");
        fsset.add("垂云搜");
        fsset.add("乐·大吕");
        if (fsset.contains(pet.getSummoningname())) {
            pet.setLingxi(fs);
            AllServiceUtil.getRoleSummoningService().updatePetRedis(pet);
            return;
        }
        pet.setLingxi(fz);
        AllServiceUtil.getRoleSummoningService().updatePetRedis(pet);
    }

}
