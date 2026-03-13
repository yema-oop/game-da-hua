package come.tool.Battle;

import come.tool.Stall.AssetUpdate;
import io.netty.channel.ChannelHandlerContext;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.come.action.IAction;
import org.come.action.chat.FriendChatAction;
import org.come.action.monitor.MonitorUtil;
import org.come.action.suit.SuitMixdeal;
import org.come.action.sys.ChangeMapAction;
import org.come.bean.ChangeMapBean;
import org.come.bean.LoginResult;
import org.come.bean.NChatBean;
import org.come.bean.UseCardBean;
import org.come.entity.Goodstable;
import org.come.entity.Lingbao;
import org.come.entity.Mount;
import org.come.entity.RoleSummoning;
import org.come.handler.SendMessage;
import org.come.model.*;
import org.come.protocol.Agreement;
import org.come.protocol.AgreementUtil;
import org.come.protocol.ParamTool;
import org.come.server.GameServer;
import org.come.tool.Arith;
import org.come.until.AllServiceUtil;
import org.come.until.GsonUtil;

import com.gl.util.EggUtil;
import com.gl.util.GLUtil;

import come.tool.FightingData.Battlefield;
import come.tool.FightingData.ManData;
import come.tool.FightingData.PK_MixDeal;
import come.tool.Good.DropModel;
import come.tool.Good.DropUtil;
import come.tool.Good.ExpUtil;
import come.tool.Good.UsePetAction;
import come.tool.Role.RoleData;
import come.tool.Role.RolePool;
import come.tool.Scene.Scene;
import come.tool.Scene.SceneUtil;
import come.tool.newTask.Task;
import come.tool.newTask.TaskRecord;
import come.tool.newTask.TaskState;
import come.tool.newTask.TaskUtil;
import come.tool.oneArena.OneArenaAction;
import come.tool.oneArena.OneArenaBean;
import come.tool.oneArena.OneArenaNotes;

//TODO 战斗结算
public class RewardLimit {
    private static ConcurrentHashMap<String, Integer> boosDropMap = new ConcurrentHashMap<>();//boos掉落

    private static ConcurrentHashMap<Integer, ConcurrentHashMap<BigDecimal, Integer>> rewardMap = new ConcurrentHashMap<>();

    public static void Reward(BattleEnd battleEnd, BattleData battleData) {
        OneArenaBean arenaBean = null;
        if (battleData.getBattleType() == 101) {
            OneArenaNotes notes = OneArenaAction.OneArenaEnd(battleData.getOneArenaRole1(), battleData.getOneArenaRole2(), battleEnd.getCamp() == 1 ? 0 : 1);
            if (battleEnd.getCamp() == 1) {
                arenaBean = OneArenaAction.getOneArenaRole(battleData.getOneArenaRole1());
            }
            if (arenaBean == null) {
                arenaBean = new OneArenaBean();
            }
            arenaBean.setNotes(notes);
        }
        if (battleEnd.getCamp() == 1) {
            RewardSL(battleEnd, battleData, battleData.getTeam1(), arenaBean);
        } else {
            RewardSB(battleEnd, battleData, battleData.getTeam1(), arenaBean);
        }
        if (battleData.getTeam2().length != 0) {
            if (battleEnd.getCamp() == 2) {
                RewardSL(battleEnd, battleData, battleData.getTeam2(), null);
            } else {
                RewardSB(battleEnd, battleData, battleData.getTeam2(), null);
            }
        }
    }

    static String KRD = "枯荣丹";
    static String VIP = "VIP";
    static String HPY = "六脉化神丸";
    static String MPY = "玉枢返虚丸";
    static String blood = "超级六脉化神丸_月";
    static String Mana = "超级玉枢返虚丸_月";

    /**
     * TODO 战斗胜利
     *
     * @param battleEnd
     * @param battleData
     * @param teams
     * @param arenaBean
     */
    public static void RewardSL(BattleEnd battleEnd, BattleData battleData, String[] teams, OneArenaBean arenaBean) {

        if (battleData.getWssType() != 0) {
            GLUtil.getStringToRole(battleData, teams, battleData.getWssType());
        }

        Boos boos = battleData.getBoosID() != null ? GameServer.boosesMap.get(battleData.getBoosID()) : null;
        int sj = boos != null ? Battlefield.random.nextInt(teams.length) : -1;
        int doorId = 0;
        int Tsize = teams.length;

        //TODO 装载本次战斗所参与的玩家(元气蛋判定)
        List<RoleData> list = new ArrayList<RoleData>();

        if (teams.length != 0) {
            ChannelHandlerContext ctx = GameServer.getRoleNameMap().get(teams[0]);
            LoginResult loginResult = ctx != null ? GameServer.getAllLoginRole().get(ctx) : null;
            RoleData roleData = loginResult != null ? RolePool.getRoleData(loginResult.getRole_id()) : null;
            if (roleData != null) {
                Tsize += roleData.PSize();
            }
        }
        for (int i = teams.length - 1; i >= 0; i--) {
            battleEnd.clean();
            doorId = 0;
            if (!battleData.getParticipantlist().remove(teams[i])) {
                continue;
            }
            ManData manData = battleData.getBattlefield().getBattleEndData(teams[i]);
            ChannelHandlerContext ctx = GameServer.getRoleNameMap().get(teams[i]);
            LoginResult loginResult = ctx != null ? GameServer.getAllLoginRole().get(ctx) : null;
            if (manData == null || loginResult == null) {
                continue;
            }
            RoleData roleData = RolePool.getRoleData(loginResult.getRole_id());
            list.add(roleData);
            //TODO 普通时效药
            boolean isH = roleData.getLimit(HPY) == null;
            boolean isM = roleData.getLimit(MPY) == null;
            //TODO 月药
            boolean isHS = roleData.getLimit(blood) == null;
            boolean isMS = roleData.getLimit(Mana) == null;

            if (battleData.getBattlefield().isFightType()) {
//                if (manData.getHp() <= 0) {
                if (Arith.bigDecimalCompareXD( manData.getHp(),BigDecimal.ZERO)) {
//                    loginResult.setHp(new BigDecimal((int) (manData.getHp_z() * 0.25)));
//                    loginResult.setMp(new BigDecimal((int) (manData.getMp_z() * 0.25)));
                    loginResult.setHp((manData.getHp_z().multiply(BigDecimal.valueOf(0.25))));
                    loginResult.setMp((manData.getMp_z().multiply(BigDecimal.valueOf(0.25))));
                } else {
                    //TODO 普通时效药
//                    loginResult.setHp(new BigDecimal(isHS ? manData.getHp() : manData.getHp_z()));
//                    loginResult.setMp(new BigDecimal(isMS ? manData.getMp() : manData.getMp_z()));
                    loginResult.setHp((isHS ? manData.getHp() : manData.getHp_z()));
                    loginResult.setMp((isMS ? manData.getMp() : manData.getMp_z()));
                }
                if (roleData.getLimit(blood) == null && roleData.getLimit(Mana) == null) {
                    if (isM == false) {
                        isH = false;
                    }//如果吃了自动回蓝满的道具 让血量也同时加满
                    loginResult.setHp((isH ? manData.getHp() : manData.getHp_z()));
                    loginResult.setMp((isM ? manData.getMp() : manData.getMp_z()));

//                    loginResult.setHp(new BigDecimal(isH ? manData.getHp() : manData.getHp_z()));
//                    loginResult.setMp(new BigDecimal(isM ? manData.getMp() : manData.getMp_z()));

                }
            }
            long goodExp = 0;
            RoleSummoning pet = null;
            if (loginResult.isGolem()) {
//                loginResult.setHp(new BigDecimal(manData.getHp_z()));
//                loginResult.setMp(new BigDecimal(manData.getMp_z()));
                loginResult.setHp((manData.getHp_z()));
                loginResult.setMp((manData.getMp_z()));
                pet = manData.getPet(false, false, false, false);
            } else if (battleData.getBattlefield().isFightType()) {
                UseCardBean limit = roleData.getLimit(KRD);
                if (limit != null) {
                    if (roleData.getLimit(VIP) == null) {
                        pet = manData.getPet(isH, isM, isHS, isMS);
                        if (pet != null) {
                            if (!(isH || isM || isHS || isMS)) {
                                AllServiceUtil.getRoleSummoningService().updatePetRedis(pet);
                            }
                            battleEnd.upAssetData("P" + pet.getSid() + "=" + pet.getGrade() + "=" + pet.getExp() + "=" + pet.getFriendliness() + "=" + pet.getBasishp().toString() + "=" + pet.getBasismp().toString());
                        }
                    } else {
                        pet = manData.getPet(false, false, false, false);
                        if (pet != null) {
                            battleEnd.upAssetData("P" + pet.getSid() + "=" + pet.getGrade() + "=" + pet.getExp() + "=" + pet.getFriendliness() + "=" + pet.getBasishp().toString() + "=" + pet.getBasismp().toString());
                        }
                    }
                    pet = AllServiceUtil.getRoleSummoningService().selectRoleSummoningsByRgID(new BigDecimal(limit.getValue()));
                } else if (roleData.getLimit(VIP) != null) {
                    pet = manData.getPet(false, false, false, false);
                } else {
                    pet = manData.getPet(isH, isM, isHS, isMS);
                }
            }
            if (battleData.getBattleType() == 4) {
                if (manData.getMoney() != 0) {
                    long money = manData.getMoney() * 2;
                    battleEnd.upAssetData("D=" + money);
                    battleEnd.upAssetData("偷钱=" + money);
                    loginResult.setGold(loginResult.getGold().add(new BigDecimal(money)));
                    MonitorUtil.getMoney().addD(money, 0);
                    if (money >= 10000000) {
                        SuitMixdeal.Stealing(loginResult.getRolename(), money);
                    }
                    battleEnd.upAssetMsg("你获得金钱" + money);
                    MonitorUtil.getDropHM().add(money);
                }
            }

            if (battleData.getRobots() != null && battleData.getRobots().getRobotID() == 744) {
                int sum = pet.getOpenSeal();
                String value;
                Skill skill = null;
                String skillid = null;
                String skillid1 = "几率=100|五行技能=";
                String skillid3;
                int SKILLID = 1800;
                Random q = new Random();
                int l = q.nextInt(80);
                int m = SKILLID + l;
                skillid = String.valueOf(m);
                skillid3 = skillid1 + skillid;
                skill = UsePetAction.skillid(skillid3);

                if (skill != null) {
                    List<String> skills = new ArrayList<>();
                    if (pet.getPetSkills() != null && !pet.getPetSkills().equals("")) {
                        String[] vs = pet.getPetSkills().split("\\|");
                        for (int a = 0; a < vs.length; a++) {
                            if (!vs[i].equals("")) {
                                skills.add(vs[a]);
                            }
                        }
                    }


                    if (sum <= skills.size() || skills.size() < 8) {//召唤兽技能格子已经满了
                        value = UsePetAction.chongfu(skill, pet, skills, true);
                        if (value == null) {

                            skills.add(skill.getSkillid() + "");
                            StringBuffer buffer = new StringBuffer();
                            for (int b = 0; b < skills.size(); b++) {
                                if (buffer.length() != 0) {
                                    buffer.append("|");
                                }
                                buffer.append(skills.get(b));
                            }
                            AssetUpdate assetUpdate = new AssetUpdate();
                            assetUpdate.setType(AssetUpdate.USEGOOD);
                            pet.setPetSkills(buffer.toString());
                            UsePetAction.getskills(skills, pet.getSkill());
                            UsePetAction.getskills(skills, pet.getBeastSkills());
                            pet.setSkillData(UsePetAction.skillData(skills));
                            AllServiceUtil.getRoleSummoningService().updatePetRedis(pet);
                            assetUpdate.setPet(pet);
                            assetUpdate.setMsg("你的召唤兽学会了" + skill.getSkillname());
                            int Id = skill.getSkillid();
                            if ((Id >= 1606 && Id <= 1608) || (Id >= 1828 && Id <= 1830) || (Id >= 1840 && Id <= 1842) || (Id >= 1867 && Id <= 1869) || Id == 3034) {//学习终极技能
                                assetUpdate.updata("T悟技");
                                SuitMixdeal.JN(loginResult.getRolename(), pet.getSummoningname(), skill.getSkillname(), "终级");
                            } else if ((Id >= 1815 && Id <= 1827) || (Id >= 1600 && Id <= 1605)
                                    || (Id >= 1610 && Id <= 1612) || Id == 1811 || Id == 1831 || Id == 1833 || (Id >= 1871 && Id <= 1880)) {//学习高级技能
                                assetUpdate.updata("T悟技");
                                SuitMixdeal.JN(loginResult.getRolename(), pet.getSummoningname(), skill.getSkillname(), "高级");
                            }
                            SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().assetAgreement(GsonUtil.getGsonUtil().getgson().toJson(assetUpdate)));

                        } else SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().PromptAgreement(value));

                    } else
                        SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().PromptAgreement("召唤兽技能格子已经满了"));


                }


            }

            if (sj == i) {
                Robots robots = GameServer.getAllRobot().get(boos.getBoosrobot());
                goodExp += DropUtil.getDrop(loginResult, pet, boos.getDropModel(), robots != null ? robots.getUnknow() : null, battleEnd, manData.getExpXS(), 0, 0, 25, 0, 0, 0);
            }
            boolean isDrop = true;
            double expXS = manData.getExp2XS();
            int ndXS = 0;
            Robots robots = battleData.getRobots();
            int size = 0;
            int BSum = MonitorUtil.getBSum(loginResult.getRole_id());
            //队长加成
//            if (i == 0 && teams != null) {
//                int sum = teams.length - 2;
//                if (sum > 0) {
//                    expXS += 0.1;
//                    expXS += sum * 0.05;
//                    if (sum == 3) {
//                        expXS += 0.1;
//                    }
//                }
//            }
            String robotName = "大话精灵";//机器人
            if (robots != null) {
                robotName = robots.getRobotname();//机器人
                if (robots.getDropLimit() == 1) {
                    isDrop = false;
                }//需要参与任务才有奖励
                if (robots.getTaskIds() != null && battleData.getBattlefield().yename != null) {
                    for (int j = robots.getTaskIds().size() - 1; j >= 0; j--) {
                        Task task = roleData.getTask(robots.getTaskIds().get(j));
                        if (task == null) {
                            continue;
                        }
                        int part = task.PartFinish(Integer.parseInt(robots.getRobotid()), 1, "击杀");
                        if (part == 3) {
                            ndXS = battleData.getBattlefield().ndXS;
                            if (ndXS > task.getTaskData().getNd()) {
                                ndXS = task.getTaskData().getNd();
                            }

                            isDrop = true;//参与任务
                            StringBuffer buffer = new StringBuffer();
                            boolean is = task.isFinish();
                            buffer.append(task.getTaskId());
                            buffer.append("=");
                            if (is) {//开启下一个任务
                                buffer.append(TaskState.finishTask);
                                roleData.removeTask(task.getTaskId());
                                TaskUtil.addSumLimit(task.getTaskData(), loginResult);
                                StringBuffer buffer2 = TaskUtil.addTaskL(null, task.getTaskId(), task.getTaskSet());
                                if (buffer2 != null) {
                                    buffer.append("|");
                                    buffer.append(buffer2);
                                }
                                if (task.getTaskData().getDropModel() != null) {
                                    int num = roleData.getTaskWC(task.getTaskData().getTaskSetID());
                                    expXS += task.getTaskSet().XSReward(num);
                                    goodExp += DropUtil.getDrop(loginResult, pet, task.getTaskData().getDropModel(), task.getTaskData().getTalk(), battleEnd, manData.getExpXS(), expXS, num, 23, BSum, battleData.getBattlefield().DropXS, ndXS);
                                }
                                //判断是否有下一个任务
                                int newTaskId = task.getTaskData().getNewTaskId();
                                if (newTaskId >= 8008 && newTaskId <= 8011) {
                                    Task task2 = TaskUtil.TaskReceive(newTaskId, Tsize, 0, loginResult, roleData, buffer, task.getProgress().get(0));
                                    if (i == 0 && task2 != null) {
                                        doorId = task2.getTaskData().getDoorID();
                                    }
                                } else if (newTaskId != 0) {
                                    Task task2 = TaskUtil.TaskReceive(newTaskId, Tsize, 0, loginResult, roleData, buffer);
                                    if (i == 0 && task2 != null) {
                                        doorId = task2.getTaskData().getDoorID();
                                    }
                                }

                            } else {
                                buffer.append(task.getTaskState());
                                TaskUtil.Progress(task, buffer);
                            }
                            battleEnd.setTaskn(buffer.toString());
                            // 机器人队长任务完成次数处理
                            if (loginResult.isGolem() && i == 0) {
                                GameServer.golemServer.addSumLimit(loginResult.getRolename(), task.getTaskSetId());
                            }
                        }
                        break;
                    }
                }
                TaskSet taskSet = GameServer.getRobotTaskSet(robots.getRobotID());
                if (taskSet != null) {
                    size = roleData.addTaskRecordWC(taskSet.getTaskSetID());
                    if (taskSet.getSumlimit() != 0 && size >= taskSet.getSumlimit()) {
                        isDrop = false;
                    }
                    battleEnd.upTaskn("C" + taskSet.getTaskSetID() + "=L");
                }

                if (battleData.getHJv() > 0) { // 幻境试炼
                    TaskRecord taskRecord = roleData.getTaskRecord(6);
                    if (taskRecord == null) {
                        taskRecord = new TaskRecord(6);
                        roleData.addTaskRecord(taskRecord);
                    }

                    int curr = battleData.getRobots().getRobotID() - 9000;
                    if (taskRecord.getNewID() < curr) {  // 晋级挑战不扣除次数
                        taskRecord.setNewID(curr);
                        if (roleData.getLoginResult().getHjmax() < curr) {
                            roleData.getLoginResult().setHjmax(curr);
                        }
                        battleEnd.upTaskn("C6=N" + curr);
                    } else {
                        taskRecord.addCSum(1);
                        battleEnd.upTaskn("C6=L");
                    }

                    battleEnd.upMsg("幻境试炼挑战成功");
                }
                // 机器人怪物击杀处理
                if (loginResult.isGolem()) {
                    GameServer.golemServer.addSumLimit(loginResult.getRolename(), robots.getRobotid(), i == 0);
                }
            }
            if (battleData.getBattlefield().dropModel != null && isDrop) {
                if (size == 0 && robots != null && robots.isJL()) {
                    ConcurrentHashMap<BigDecimal, Integer> map = get(robots.getRobotID());
                    Integer num2 = map.get(loginResult.getRole_id());
                    if (num2 == null) {
                        num2 = 0;
                    } else {
                        num2++;
                    }
                    map.put(loginResult.getRole_id(), num2);
                    size = num2;
                }
                goodExp += DropUtil.getDrop(loginResult, pet, battleData.getBattlefield().dropModel, robots != null ? robots.getUnknow() : null, battleEnd, manData.getExpXS(), expXS, size, 24, BSum, battleData.getBattlefield().DropXS, ndXS);
            }
            if (arenaBean != null) {
                battleEnd.setArenaBean(arenaBean);
                TaskRecord taskRecord = roleData.getTaskRecord(4);
                if (taskRecord == null) {
                    taskRecord = new TaskRecord(4);
                    roleData.addTaskRecord(taskRecord);
                }
                taskRecord.addRSum(1);
                taskRecord.addCSum(1);
                battleEnd.upTaskn("C4=R=L");
                if (arenaBean.getPlace() != 0) {
                    battleEnd.upMsg("你当前竞技场排名:" + arenaBean.getPlace());
                }
            }
            if (battleData.getMonsterBean() != null && battleData.getMonsterBean().getExp() != null) {
                DropModel dropModel = battleData.getMonsterBean().getExp().getDropModel(teams.length);
                if (dropModel != null) {
                    battleEnd.upAssetMsg("你参与击杀" + battleData.getMonsterBean().getRobotname() + "瓜分累积经验:" + dropModel.getExp());
                    goodExp += DropUtil.getDrop(loginResult, pet, dropModel, null, battleEnd, manData.getExpXS(), expXS, size, 24, BSum, 0, 0);
                }
            }


            // 经验结算后，按当前称号/装备等重新计算并回满 HP/MP，防止旧快照压低血蓝
            ExpUtil.refreshRoleHpMpByBattleFormula(loginResult);
            // 这种结算是按 manData 中的剩余血蓝直接给玩家（例如奖励扣血），不再额外回满
            battleEnd.upAssetData("R" + loginResult.getGrade() + "=" + loginResult.getExperience() + "=" + loginResult.getHp() + "=" + loginResult.getMp());
            if (pet != null) {
                if (battleData.getBattleType() == 1 || battleData.getBattleType() == 2) {
                    if (BSum < 1000) {
                        pet.addqm(50);
                    }
                    int type = 0;
                    if (loginResult.getMapid() == 3321 && roleData.getTask(3154) != null) {
                        type = 2;
                    } else if (loginResult.getMapid() == 3322 && roleData.getTask(3153) != null) {
                        type = 1;
                    }
                    if (!UsePetAction.PetSkill(pet, ctx, loginResult, type)) {
                        AllServiceUtil.getRoleSummoningService().updatePetRedis(pet);
                    }
                } else if (battleData.getBattleType() == 0) {
                    if (BSum < 1000) {
                        pet.addqm(50);
                    }
                    AllServiceUtil.getRoleSummoningService().updatePetRedis(pet);
                } else if (battleData.getBattlefield().isFightType()) {
                    AllServiceUtil.getRoleSummoningService().updatePetRedis(pet);
                }
                battleEnd.upAssetData("P" + pet.getSid() + "=" + pet.getGrade() + "=" + pet.getExp() + "=" + pet.getFriendliness() + "=" + pet.getBasishp() + "=" + pet.getBasismp());
                if (goodExp != 0) {
                    if (pet.getInnerGoods() != null && !pet.getInnerGoods().equals("")) {
                        String[] vs = pet.getInnerGoods().split("\\|");
                        for (int j = 0; j < vs.length; j++) {
                            Goodstable goodstable = AllServiceUtil.getGoodsTableService().getGoodsByRgID(new BigDecimal(vs[j]));
                            if (goodstable != null && goodstable.getType() == 750) {
                                ExpUtil.IncreaseNedanExp(pet, goodstable, battleEnd, goodExp);
                            }
                        }
                    }
                }
            }
            if (BSum < 1000 && (battleData.getBattleType() == 0 || battleData.getBattleType() == 1 || battleData.getBattleType() == 2)) {//加坐骑和灵宝经验
                Lingbao lingbao = roleData.getLs() != null ? AllServiceUtil.getLingbaoService().selectLingbaoByID(roleData.getLs().getId()) : null;
                if (lingbao != null) {
                    lingbao.setLingbaoqihe(lingbao.getLingbaoqihe() + 20);
                    int lvl = lingbao.getLingbaolvl().intValue();
                    long exp = lingbao.getLingbaoexe().longValue();
                    long maxexp = ExpUtil.LFExp(lvl);
                    if (exp >= maxexp && lvl != 0 && lvl % 30 == 0) {
                        battleEnd.upMsg("灵宝突破后才可继续升级");
                    } else if (lvl < 200) {
                        battleEnd.upMsg(ExpUtil.LFExp(lingbao, 15));
                    }
                    AllServiceUtil.getLingbaoService().updateLingbaoRedis(lingbao);
                    battleEnd.upAssetData("L" + lingbao.getBaoid() + "=" + lingbao.getLingbaolvl() + "=" + lingbao.getLingbaoexe() + "=" + lingbao.getLingbaoqihe());
                }
                for (int k = 0; k < roleData.getFs().size() && k < 2; k++) {
                    Lingbao fabao = AllServiceUtil.getLingbaoService().selectLingbaoByID(roleData.getFs().get(k).getId());
                    if (fabao != null) {
                        int lvl = fabao.getLingbaolvl().intValue();
                        long exp = fabao.getLingbaoexe().longValue();
                        long maxexp = ExpUtil.LFExp(lvl);
                        if (exp >= maxexp && lvl != 0 && lvl % 30 == 0) {
                            battleEnd.upMsg("法宝突破后才可继续升级");
                        } else if (lvl < 200) {
                            battleEnd.upMsg(ExpUtil.LFExp(fabao, 15));
                            AllServiceUtil.getLingbaoService().updateLingbaoRedis(fabao);
                            battleEnd.upAssetData("L" + fabao.getBaoid() + "=" + fabao.getLingbaolvl() + "=" + fabao.getLingbaoexe() + "=" + fabao.getLingbaoqihe());
                        }
                    }
                }
                Mount mount = roleData.getMid() != null ? AllServiceUtil.getMountService().selectMountsByMID(roleData.getMid()) : null;
                if (mount != null) {
                    ExpUtil.MountAddES(battleEnd, mount, 15, 5);
                }
//	    		//3000
//	    		if (roleData.getPs().size()>0) {
//	    			Pal pal=AllServiceUtil.getPalService().selectPalByID(roleData.getPs().get(0));
//	    			if (pal!=null) {
//	    				ExpUtil.PalExp(battleEnd, pal, 3000);
//				    }
//				}
            }
            //强P添加点数
            if (battleData.getBattleType() == 10 && manData.getCamp() == 1) {
                int[] dailys = loginResult.getDaily();
                dailys[0] += 1;
                if (dailys[0] >= 8) {
                    doorId = 1;
                    dailys[1] = 1;
                    dailys[2] += 1;
                    dailys[3] += 1;
                }
                try {
                    loginResult.getRoleData().removeLimit("杀人香");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                battleEnd.setTaskDaily(loginResult.upDaily(dailys));
                battleEnd.upAssetMsg("你获得 #G 1 #Y点PK标识,现在PK标志有 #G " + dailys[0] + " #Y点,PK标志超过4点时进城有概率遇到捕快,8点强制坐牢");
            }
            if (battleData.getSceneId() != null) {
                Scene scene = SceneUtil.getScene(battleData.getSceneId());
                if (scene != null) {
                    int door = 0;
                    if (battleData.getSceneId() == SceneUtil.DNTGID && PK_MixDeal.isPK(battleData.getBattlefield().BattleType)) {
                        door = scene.battleEnd(battleEnd, loginResult, null, i == 0 ? 0 : 1);
                    } else {
                        door = scene.battleEnd(battleEnd, loginResult, battleData.getMonsterBean(), i == 0 ? 0 : 1);
                    }
                    if (i == 0 && door != 0) {
                        doorId = door;
                    }
                }
            }
	//获得双份物品1
            AssetUpdate assetUpdate;
            if ((assetUpdate = battleEnd.getAssetUpdate()) != null) {
                assetUpdate.setTask(battleEnd.getTaskn());
                battleEnd.setAssetUpdate(null);
                battleEnd.setTaskn(null);
            }
            if (assetUpdate != null) {
                SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().assetAgreement(GsonUtil.getGsonUtil().getgson().toJson(assetUpdate)));
            }
            battleEnd.setAssetUpdate(null);
            String msg= Agreement.FightingendAgreement(GsonUtil.getGsonUtil().getgson().toJson(battleEnd));
            SendMessage.sendMessageToSlef(ctx, msg);
            if (doorId == 1) {
                IAction action = ParamTool.ACTION_MAP.get("getout");
                if (action != null) action.action(ctx, loginResult.getTaskDaily());
            } else if (i == 0 && doorId != 0) {
                Door door = GameServer.getDoor(doorId);
                if (door != null) {
                    ChangeMapBean changeMapBean = new ChangeMapBean();
                    changeMapBean.setMapid(door.getDoormap());
                    String[] vs = door.getDoorpoint().split("\\|");
                    changeMapBean.setMapx(Integer.parseInt(vs[0]));
                    changeMapBean.setMapy(Integer.parseInt(vs[1]));
                    ChangeMapAction.ChangeMap(changeMapBean, ctx);
                }
            }
            if (i == 0 && loginResult.isGolem())
                GameServer.golemServer.endFighting(teams[i], "成功击杀【" + robotName + "】");//机器人
        }
        EggUtil.success(list);
        battleEnd.clean();
    }

    public static void RewardSB(BattleEnd battleEnd, BattleData battleData, String[] teams, OneArenaBean arenaBean) {
        int doorId = 0;
        for (int i = teams.length - 1; i >= 0; i--) {
            battleEnd.clean();
            doorId = 0;
            if (!battleData.getParticipantlist().remove(teams[i])) {
                continue;
            }
            ManData manData = battleData.getBattlefield().getBattleEndData(teams[i]);
            ChannelHandlerContext ctx = GameServer.getRoleNameMap().get(teams[i]);
            LoginResult loginResult = ctx != null ? GameServer.getAllLoginRole().get(ctx) : null;
            if (manData == null || loginResult == null) {
                continue;
            }
            if (loginResult.isGolem()) {
//                loginResult.setHp(new BigDecimal(manData.getHp_z()));
//                loginResult.setMp(new BigDecimal(manData.getMp_z()));
                loginResult.setHp((manData.getHp_z()));
                loginResult.setMp((manData.getMp_z()));
            } else if (battleData.getBattlefield().isFightType()) {
//                if (manData.getHp() <= 0) {
//                    loginResult.setHp(new BigDecimal((int) (manData.getHp_z() * 0.25)));
//                    loginResult.setMp(new BigDecimal((int) (manData.getMp_z() * 0.25)));
//                } else {
//                    loginResult.setHp(new BigDecimal(manData.getHp() > 0 ? manData.getHp() : 1));
//                    loginResult.setMp(new BigDecimal(manData.getMp()));
//                }

                if (Arith.bigDecimalCompareXD(manData.getHp(),BigDecimal.ZERO)) {
                    loginResult.setHp (manData.getHp_z().multiply(BigDecimal.valueOf(0.25)));
                    loginResult.setMp (manData.getMp_z().multiply(BigDecimal.valueOf(0.25)));
                } else {
                    loginResult.setHp(Arith.bigDecimalCompareD(manData.getHp(),BigDecimal.ZERO) ? manData.getHp() : BigDecimal.ONE);
                    loginResult.setMp(manData.getMp());
                }
            }
            battleEnd.upAssetData("R" + loginResult.getGrade() + "=" + loginResult.getExperience() + "=" + loginResult.getHp() + "=" + loginResult.getMp());
            RoleData roleData = RolePool.getRoleData(loginResult.getRole_id());
            if (battleData.getBattlefield().isFightType() && roleData.getLimit(VIP) == null) {
                RoleSummoning pet;
                if (loginResult.isGolem()) {
                    pet = manData.getPet(false, false, false, false);
                } else {
                    pet = manData.getPet(true, true, true, true);
                }
                if (pet != null) {
                    battleEnd.upAssetData("P" + pet.getSid() + "=" + pet.getGrade() + "=" + pet.getExp() + "=" + pet.getFriendliness() + "=" + pet.getBasishp() + "=" + pet.getBasismp());
                    AllServiceUtil.getRoleSummoningService().updatePetRedis(pet);
                }
            }
            if (arenaBean != null) {
                battleEnd.setArenaBean(arenaBean);
                TaskRecord taskRecord = roleData.getTaskRecord(4);
                if (taskRecord == null) {
                    taskRecord = new TaskRecord(4);
                    roleData.addTaskRecord(taskRecord);
                }
                taskRecord.addRSum(1);
                battleEnd.upTaskn("C4=R");
            }
            if (battleData.getHJv() > 0) { // 幻境试炼
                TaskRecord taskRecord = roleData.getTaskRecord(6);
                if (taskRecord == null) {
                    taskRecord = new TaskRecord(6);
                    roleData.addTaskRecord(taskRecord);
                }

                int curr = battleData.getRobots().getRobotID() - 9000;
                if (taskRecord.getNewID() >= curr) {
                    taskRecord.addCSum(1);
                    battleEnd.upTaskn("C6=L");
                }
                battleEnd.upMsg("幻境试炼挑战失败");
            }
            if (battleData.getBattleType() == 10) {//强P经验损失
                doorId = 365;//送回轮回司
                long expmax = ExpUtil.getRoleExp(loginResult.getTurnAround(), BattleMixDeal.lvlint(loginResult.getGrade()));
                expmax = (long) (expmax * (manData.getCamp() == 1 ? 0.5 : 0.3));
                int lvl = loginResult.getGrade();
                ExpUtil.RoleRemoveExp(loginResult, expmax);
                lvl -= loginResult.getGrade();
                StringBuffer buffer = new StringBuffer();
                if (manData.getCamp() != 1) {
                    buffer.append("你在强行杀人PK中被#R" + battleData.getTeam1()[0] + "#W击杀了!");
                } else if (manData.getCamp() == 1) {
                    buffer.append("你在强行杀人PK中被#R" + battleData.getTeam2()[0] + "#W击杀了!");
                }
                //buffer.append("你在强行杀人PK中被杀了!");
                if (manData.getCamp() == 1) {
                    buffer.append("由于你是发起方惩罚加重.");
                }

                long moeny = loginResult.getGold().longValue();
                moeny = (long) (moeny * (manData.getCamp() == 1 ? 0.5 : 0.3));
                loginResult.setGold(loginResult.getGold().subtract(new BigDecimal(moeny)));
                MonitorUtil.getMoney().useD(moeny);
                buffer.append("你损失了" + moeny + "大话币.");
                buffer.append("你损失了" + expmax + "经验.");
                if (lvl != 0) {
                    buffer.append("由于经验不足掉落了");
                    buffer.append(lvl);
                    buffer.append("级");
                }
                FriendChatAction.createChatBeanForServer(buffer.toString(), loginResult.getRolename());

                StringBuffer roleBuffer = new StringBuffer();
                roleBuffer.append("R");
                roleBuffer.append(loginResult.getGrade());
                roleBuffer.append("=");
                roleBuffer.append(loginResult.getExperience());
                roleBuffer.append("=");
                roleBuffer.append(loginResult.getHp());
                roleBuffer.append("=");
                roleBuffer.append(loginResult.getMp());

                roleBuffer.append("=");
                roleBuffer.append(loginResult.getBone());
                roleBuffer.append("=");
                roleBuffer.append(loginResult.getSpir());
                roleBuffer.append("=");
                roleBuffer.append(loginResult.getPower());
                roleBuffer.append("=");
                roleBuffer.append(loginResult.getSpeed());
                roleBuffer.append("=");
                roleBuffer.append(loginResult.getCalm());
                battleEnd.upAssetData("D=-" + moeny);
                battleEnd.upAssetData(roleBuffer.toString());
            } else if (battleData.getBattleType() == 15 && i == 0) {//抓捕
                doorId = 1;
                int[] dailys = loginResult.getDaily();
                dailys[1] = 1;
                dailys[3] = dailys[3] + 1;
                battleEnd.setTaskDaily(loginResult.upDaily(dailys));
            }
            if (battleData.getSceneId() != null) {
                Scene scene = SceneUtil.getScene(battleData.getSceneId());
                if (scene != null) {
                    int door = scene.battleEnd(battleEnd, loginResult, null, i == 0 ? 2 : 3);
                    if (i == 0 && door != 0) {
                        doorId = door;
                    }
                }
            }
            else if (battleData.getSceneId() == null && doorId == 0 && battleData.getBattleType() != 5 && battleData.getBattleType() != 34 && battleData.getBattleType() != 2
                    && battleData.getBattleType() != 101
                    && battleData.getBattleType() != 21 && battleData.getBattleType() != 11 && loginResult.getMapid() != 3315) {
                //使用月卡不进轮回
            }else if(roleData.getLimit(VIP)== null){
                StringBuffer buffer = new StringBuffer();
                NChatBean bean = new NChatBean();

                bean.setId(5);
                buffer.append("玩家#R" + loginResult.getRolename() + "#Y");
                if (battleData.getBattlefield().BattleType == 0) {
                    buffer.append("怂逼被野怪送去见小白了#24");
                }
                if (battleData.getRobots() != null) {
                    Robots robots = battleData.getRobots();
                    if (robots.getTaskIds() != null && battleData.getBattlefield().yename != null) {
                        boolean b = false;
                        for (int j = robots.getTaskIds().size() - 1; j >= 0; j--) {
                            Task task = roleData.getTask(robots.getTaskIds().get(j));
                            if (task == null) {
                                continue;
                            }
                            buffer.append("在" + task.getTaskData().getTaskName() + "怂逼被#G" + robots.getRobotname() + "送去见小白了#24");
                            b = true;
                            break;
                        }
                        if (!b)
                            buffer.append("怂逼被#G" + robots.getRobotname() + "送去见小白了#24");
                    } else {
                        buffer.append("怂逼被#G" + robots.getRobotname() + "送去见小白了#24");
                    }

                }
                bean.setMessage(buffer.toString());
                String msg = Agreement.getAgreement().chatAgreement(GsonUtil.getGsonUtil().getgson().toJson(bean));
                SendMessage.sendMessageToAllRoles(msg);
                doorId = 365; // 非活动场景死回轮回
                AssetUpdate assetUpdate = new AssetUpdate();
                assetUpdate.updata("Y死亡地府");
                SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().assetAgreement(GsonUtil.getGsonUtil().getgson().toJson(assetUpdate)));
            }
            String msg= Agreement.FightingendAgreement(GsonUtil.getGsonUtil().getgson().toJson(battleEnd));
            SendMessage.sendMessageToSlef(ctx, msg);
            AssetUpdate assetUpdate;
            if ((assetUpdate = battleEnd.getAssetUpdate()) != null) {
                assetUpdate.setTask(battleEnd.getTaskn());
                battleEnd.setAssetUpdate(null);
                battleEnd.setTaskn(null);
            }

            if (assetUpdate != null) {
                SendMessage.sendMessageToSlef(ctx, Agreement.getAgreement().assetAgreement(GsonUtil.getGsonUtil().getgson().toJson(assetUpdate)));
            }
            if (i == 0) {
                if (battleData.getBattleType() == 11) {//回帮派营地
                    IAction action = ParamTool.ACTION_MAP.get(AgreementUtil.gangbattle);
                    if (action != null) action.action(ctx, "5");
                } else if (doorId == 1) {//回监狱
                    IAction action = ParamTool.ACTION_MAP.get("getout");
                    if (action != null) action.action(ctx, loginResult.getTaskDaily());
                } else if (doorId != 0) {
                    Door door = GameServer.getDoor(doorId);
                    if (door != null) {
                        ChangeMapBean changeMapBean = new ChangeMapBean();
                        changeMapBean.setMapid(door.getDoormap());
                        String[] vs = door.getDoorpoint().split("\\|");
                        changeMapBean.setMapx(Integer.parseInt(vs[0]));
                        changeMapBean.setMapy(Integer.parseInt(vs[1]));
                        ChangeMapAction.ChangeMap(changeMapBean, ctx);
                    }
                }
            }
            String robotName = battleData.getRobots() == null ? "" : battleData.getRobots().getRobotname();//机器人
            if (i == 0 && loginResult.isGolem())
                GameServer.golemServer.endFighting(teams[i], "成功被【" + robotName + "】击杀");//机器人
        }
    }

    public static void reward(String[] team, String value) {
        int wei = Battlefield.random.nextInt(team.length);
        ChannelHandlerContext ctx = GameServer.getRoleNameMap().get(team[wei]);
        if (ctx != null) {
            LoginResult loginResult = GameServer.getAllLoginRole().get(ctx);
            if (loginResult == null) {
                return;
            }
            DropUtil.getDrop(loginResult, value, null, 22, 1D, null);
        }
    }

    public static void rewardS(String[] team, String value) {
        for (int i = 0; i < team.length; i++) {
            ChannelHandlerContext ctx = GameServer.getRoleNameMap().get(team[i]);
            if (ctx != null) {
                LoginResult loginResult = GameServer.getAllLoginRole().get(ctx);
                if (loginResult == null) {
                    continue;
                }
                DropUtil.getDrop(loginResult, value, null, 22, 1D, null);
            }
        }
    }

    /***/
    public static synchronized ConcurrentHashMap<BigDecimal, Integer> get(int robotid) {
        ConcurrentHashMap<BigDecimal, Integer> map = rewardMap.get(robotid);
        if (map == null) {
            map = new ConcurrentHashMap<>();
            rewardMap.put(robotid, map);
        }
        return map;
    }

    /**
     * 判断怪物是否产生掉落
     */
    public static String isBoosDrop(Boos boos) {
        if (boos.getBoosDropMax() == 0) {
            return null;
        }
        Integer num = boosDropMap.get(boos.getBoosid());
        if (num == null) {
            num = 0;
        }
        if (num >= boos.getBoosDropMax()) {
            return null;
        }
        num++;
        boosDropMap.put(boos.getBoosid(), num);
        return boos.getBoosid();
    }

    /**
     * 重置
     */
    public static void reset() {
        rewardMap.clear();
        boosDropMap.clear();
    }
}
