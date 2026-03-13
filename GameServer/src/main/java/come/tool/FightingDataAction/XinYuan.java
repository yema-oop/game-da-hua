package come.tool.FightingDataAction;

import come.tool.FightingData.*;
import come.tool.FightingSpellAction.ControlAction;
import come.tool.FightingSpellAction.HurtAction;
import come.tool.FightingSpellAction.SSCAction;
import come.tool.FightingSpellAction.ZSAction;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.lang3.StringUtils;
import org.come.bean.LoginResult;
import org.come.bean.PathPoint;
import org.come.model.Skill;
import org.come.server.GameServer;
import org.come.tool.Arith;
import org.come.until.GsonUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class XinYuan implements DataAction {
    private String skin;
    private String bSkin;

    @Override
    public void analysisAction(ManData manData, FightingEvents fightingEvents, String type, Battlefield battlefield) {
        Boolean xyZj = false;
//		9384|迎霜隔雪|受到物理攻击时,有（2%*等级）几率打断对方的连击。(仅在与玩家之间战斗有效。)
        //追击数//分裂数
        int z = 0, fl = 0, zwzb = 0;
        FightingSkill skill = manData.getSkillType(TypeUtil.TJ_PXKG);
        if (skill != null) {
            DataActionType.getActionById(28).analysisAction(manData, fightingEvents, type, battlefield);
            return;
        }


        skill = manData.getSkillType(TypeUtil.BB_TJLH);
        if (skill != null) {
            //TODO 莲生技能触发概率计算
            if (Battlefield.random.nextInt(100) < skill.getSkillgain()) {
                DataActionType.getActionById(34).analysisAction(manData, fightingEvents, type, battlefield);
                return;
            }
        }

        FightingSkill skill_BB_LHFM = null;

        boolean isHSSF = TypeUtil.BB_E_HSSF.equals(type);
        if (isHSSF) {
            type = TypeUtil.PTGJ;
        }
        if (type.equals("分裂")) {
            fl = 1;
            type = TypeUtil.PTGJ;
        } else if (type.equals("追击")) {
            z = 1;
            type = TypeUtil.PTGJ;
        } else {
            AddAttack addAttack = null;
            if (!type.equals("混乱技")) {
                addAttack = manData.getAttacks("暗影离魂");
                if (addAttack != null) {
                    skill = addAttack.getSkill();
                    if (skill.getSkillhitrate() <= battlefield.CurrentRound) {
                        skill.setSkillhitrate(battlefield.CurrentRound + 3);
                        DataActionType.getActionById(20).analysisAction(manData, fightingEvents, type, battlefield);
                        return;
                    }
                }
                addAttack = manData.getAttacks(TypeUtil.BB_LHFM);
                if (addAttack != null) {
                    skill = addAttack.getSkill();
                    if (skill.getSkillhitrate() <= battlefield.CurrentRound) {
                        skill.setSkillhurt(1);
                        skill.setSkillhitrate(battlefield.CurrentRound + 10);
                        skill_BB_LHFM = skill;
                    }
                }
            }
            skill = null;
            addAttack = manData.getAttacks("亢龙有悔");
            if (addAttack != null) {
                skill = addAttack.getSkill();
            }
        }
        FightingSkill skill4 = manData.getSkillType("雾里看花");
        double mzjc = 0, ljjc = 0, dsjc = 0, dsl = 0;//命中加成,连击加成,躲闪加成,躲闪率
        //判断是否触发孩子技能
        ManData child = battlefield.getChild(manData);
        FightingSkill skill2 = manData.getAppendSkill(9203);
        if (skill2 != null) {
            mzjc += skill2.getSkillhurt();
            skill2 = null;
        }
        if (child != null) {
            skill2 = child.getChildSkill("强普攻");
            if (skill2 != null) {
                mzjc = 10000;
                ljjc = skill2.getSkillgain();
            }
        }
        GroupBuff buff = battlefield.getBuff(manData.getMan(), TypeUtil.YBYT);
        if (buff != null) {
            mzjc += buff.getValue();
        }
        buff = battlefield.getBuff(manData.getMan(), TypeUtil.BB_E_HYMB);
        if (buff != null) {
            dsjc += buff.getValue();
        }
        int g = 0, maxf = 0, f = 0;//已攻击次数,最大反击数,已反击数
        BigDecimal ap = manData.getAp();//基础伤害
        AddState addState = manData.xzstate(TypeUtil.L_CB);
        if (addState != null) {
            mzjc -= addState.getStateEffect();
        }
        int nocamp = MixDeal.getcamp(type, manData.getCamp(), battlefield.nomy(manData.getCamp()));
        FightingSkill skill3 = manData.getSkillType("水中探月");
        FightingSkill skill11 = manData.getSkillType("剑荡八荒");
        FightingSkill skill88 = manData.getSkillType("大威天龙");
        FightingSkill skill77 = manData.getSkillType("大日如来神掌");
        FightingSkill skill9 = manData.getSkillType(TypeUtil.BB_TJTT);
        FightingSkill skill5 = manData.getAppendSkill(9347);
        if (skill5 != null) {
            mzjc -= skill5.getSkillhurt();
            ljjc -= skill5.getSkillhurt();
        }

        mzjc += manData.getQuality().getRolehsds() + manData.executeYszd(3) - manData.executeYszd(2);

        skill5 = manData.getAppendSkill(9811);
        if (skill5 != null) {
            ljjc -= skill5.getSkillhurt();
        }
        skill5 = manData.getAppendSkill(9146);
        FightingSkill skill6 = manData.getAppendSkill(9150);
        if (skill3 != null && Battlefield.random.nextInt(100) > skill3.getSkillhurt()) {
            skill3 = null;
        }
        if (skill11 != null && Battlefield.random.nextInt(100) > skill11.getSkillhurt()) {
            skill11 = null;
        }
        if (!(nocamp == manData.getCamp() || nocamp == -1)) {
            skill3 = null;
            skill11 = null;
        }

        AddState state1 = manData.xzstate(TypeUtil.TZ_FHJY);
        if (!(nocamp == manData.getCamp() || nocamp == -1)) {
            state1 = null;
        }
        ManData nomyData = getdaji(nocamp, fightingEvents, battlefield, manData, skill5 != null ? skill5.getSkillhurt() : skill6 != null ? skill6.getSkillhurt() : 0);
        List<ManData> guiwei = new ArrayList<>();
        int maxg = GMax(manData, nomyData, ljjc, battlefield);
        if (skill_BB_LHFM != null) {
            maxg = 1;
        }
        FightingSkill skill8 = null;
        BigDecimal zap = BigDecimal.ZERO;
        long zap7 = 0;//大圣附身
        long zap8 = 0;//乾坤一掷
//		1302	箭无虚发								物理攻击有时有33%几率触发箭无虚发，必定命中对方。
//		1303	一帆风顺								物理攻击时有45%几率忽视反击、反震。
//		1304	一鼓作气								一鼓作气，再而衰，三而竭。高级分裂攻击、分花拂柳之后，有33%几率再进行一次攻击（召唤兽力量要求=450）。
//		1305	同仇敌忾								物理攻击时有25%几率召唤本方所有在场召唤兽一起作战，本次攻击获得其它召唤兽的攻击力加成(上限15万)。
        FightingSkill skill_1 = null, skill_2 = null, skill_3 = null, skill_4 = null,
                skill_5 = null, skill_6 = null, skill_7 = null, skill_8 = null;//箭无虚发,一帆风顺,一鼓作气,同仇敌忾,一击毙命,横扫四方,大圣附身，乾坤一掷
        FightingSkill skill_9 = null, skill_10 = null, skill_11 = null, skill_12 = null, skill_12004 = null, skill_12012 = null, skill_12014 = null;
        ;
        int attackType = 0;//0正常   1:孙小圣攻击模式
        FightingEvents sxsEvents = null;

        FightingEvents gjEvents = new FightingEvents();

        // 全被动技能判断
        for (int i = manData.getSkills().size() - 1; i >= 0; i--) {
            FightingSkill sl = manData.getSkills().get(i);
            if (sl.getSkillbeidong() != 1) {
                continue;
            }
            if (sl.getSkilltype().equals(TypeUtil.BB_E_JWXF)) {
                skill_1 = sl;
            } else if (sl.getSkilltype().equals(TypeUtil.BB_E_YFFS)) {
                skill_2 = sl;
            } else if (sl.getSkilltype().equals(TypeUtil.BB_E_YGZQ)) {
                skill_3 = sl;
            } else if (sl.getSkilltype().equals(TypeUtil.BB_E_TCDK)) {
                skill_4 = sl;
            } else if (sl.getSkilltype().equals(TypeUtil.BB_E_YJBM)) {
                if (!PK_MixDeal.isPK(battlefield.CurrentRound)) {
                    skill_5 = sl;
                }
            } else if (sl.getSkilltype().equals(TypeUtil.BB_E_HSSF)) {
                skill_6 = sl;
            } else if (sl.getSkilltype().equals(TypeUtil.BB_E_DSFS)) {
                skill_7 = sl;
                zap7 = (long) (manData.getKangluobao() * 175);
            } else if (sl.getSkilltype().equals(TypeUtil.BB_E_QKYZ)) {
                skill_8 = sl;
                zap8 = getMoney(manData, battlefield);
                if (zap8 >= 500000) {
                    zap8 = 500000;
                }
                zap8 = (long) (zap8 / 2.8);
            } else if (sl.getSkilltype().equals(TypeUtil.BB_CZQG)) {
                skill_9 = sl;
                if (manData.getSkin() != null && manData.getSkin().equals("400318")) {
                    attackType = 1;
                }
            } else if (sl.getSkilltype().equals(TypeUtil.TZ_ZWZB)) {
                skill_10 = sl;
            } else if (sl.getSkilltype().equals(TypeUtil.BB_BCJF)) {
                skill_11 = sl;
            } else if (sl.getSkillid() == 12004) {
                skill_12004 = sl;
            }
            //大神神通
            else if (sl.getSkillid() == 12006 && Battlefield.isV(100) && nomyData.getStates() == 0) {
                skill_12 = sl;
                List<FightingState> zls = new ArrayList<>();
                if (PK_MixDeal.isPK(battlefield.BattleType)) {
                    // 此代码块仅在玩家PK时执行
//                    if (("10015".equals(manData.getSkin()) || "10016".equals(manData.getSkin())) && Battlefield.isV(200)) {
                    skin = manData.getSkin();
                    // 修复心猿PVP0.5倍伤害
                    manData = GsonUtil.getGsonUtil().getgson().fromJson(GsonUtil.getGsonUtil().getgson().toJson(manData), ManData.class);
                    manData.setAp((manData.getAp().multiply(BigDecimal.valueOf(0.5d))));
//                    }
                }
//                if(("10015".equals(manData.getSkin()) || "10016".equals(manData.getSkin())) && Battlefield.isV(200)) {
//                    skin = manData.getSkin();
//                    //修复心猿PVP0.5倍伤害
//                    manData =  GsonUtil.getGsonUtil().getgson().fromJson(GsonUtil.getGsonUtil().getgson().toJson(manData),ManData.class);
//                    manData.setAp((int) (manData.getAp() * 0.2d));
//                }
                gjEvents = new FightingEvents();
                FightingState gjz = new FightingState();
                gjz.setSkillskin(manData.getSkin() + "-f");

                if (StringUtils.isNotBlank(skin)) {
                    gjz.setSkin("10018");
                    gjz.setSkillskin("10018-f");
                    bSkin = "10018";
                }
                gjz.setStartState("代价");
                gjz.setCamp(manData.getCamp());
                gjz.setMan(manData.getMan());
                gjz.setEndState(nomyData.getCamp() + "|" + nomyData.getMan() + "|3");
                zls.add(gjz);
                gjz = new FightingState();
                gjz.setSkillskin("yun");

                gjz.setStartState("代价");
                gjz.setCamp(manData.getCamp());
                gjz.setMan(manData.getMan());
                zls.add(gjz);
                gjEvents.setAccepterlist(zls);
                battlefield.NewEvents.add(gjEvents);
            } else if (sl.getSkillid() == 12012) {
                skill_12012 = sl;
            } else if (sl.getSkillid() == 12014) {
                skill_12014 = sl;
                List<FightingState> zls = new ArrayList<>();
                FightingState gjz = new FightingState();

                gjz = new FightingState();
                gjz.setSkillskin("yun");
                gjz.setStartState("代价");
                gjz.setCamp(manData.getCamp());
                gjz.setMan(manData.getMan());
                zls.add(gjz);
                if (PK_MixDeal.isPK(battlefield.BattleType)) {
                    // 此代码块仅在玩家PK时执行
//                    if (("10015".equals(manData.getSkin()) || "10016".equals(manData.getSkin())) && Battlefield.isV(200)) {
                    skin = manData.getSkin();
                    // 修复心猿PVP0.5倍伤害
                    manData = GsonUtil.getGsonUtil().getgson().fromJson(GsonUtil.getGsonUtil().getgson().toJson(manData), ManData.class);
                    manData.setAp((manData.getAp().multiply(BigDecimal.valueOf(0.5d))));
//                    }
                }
//                if (("10015".equals(manData.getSkin()) || "10016".equals(manData.getSkin())) && Battlefield.isV(200)) {
//                    skin = manData.getSkin();
//                    manData =  GsonUtil.getGsonUtil().getgson().fromJson(GsonUtil.getGsonUtil().getgson().toJson(manData),ManData.class);
//                    //修复心猿0.5倍伤害
//                    manData.setAp((int) (manData.getAp() * 0.2d));
//                }

                if (StringUtils.isNotBlank(skin)) {
                    gjz.setSkin("10019");
                }

                gjz.setStartState("代价");
                gjz.setCamp(manData.getCamp());
                gjz.setMan(manData.getMan());
                gjz.setEndState(nomyData.getCamp() + "|" + nomyData.getMan() + "|3");
                zls.add(gjz);
                gjEvents.setAccepterlist(zls);
            }
        }

        List<ManData> zzs = null;
        if (skill_4 != null && Battlefield.isV(skill_4.getSkillgain())) {
            zzs = new ArrayList<>();
            for (int i = battlefield.fightingdata.size() - 1; i >= 0; i--) {
                ManData data = battlefield.fightingdata.get(i);
                if (data.getType() != 1 || data.getCamp() != manData.getCamp() || data.getStates() != 0) {
                    continue;
                }
                if (data.getMan() == manData.getMan()) {
                    continue;
                }
                zap = zap.add(data.getAp());
                zzs.add(data);
            }
//            if (zap >= 250000) {
            if (Arith.bigDecimalCompareDD(zap, BigDecimal.valueOf(250000))) {

                zap = BigDecimal.valueOf(250000);
            }
        }
        BigDecimal Zap = manData.getAp();

        // 怒不可揭叠加层数
        int nbkj = 0;
        // 是否触发怒不可揭
        boolean nb = false;
        // 是否触发战意分裂
        boolean zy = false;

        // 势不可遏额外攻击
        long sbke = 0;

        // 将功补过判定，等于true则回合末尾执行
        boolean jgbg = (maxg == 1);

        int hsfjv = manData.executeYszd(3) - manData.executeYszd(2);

        // 正式攻击回合
        while (nomyData != null && g < maxg) {

            if (g == 1) {
                skill8 = nomyData.getSkillType(TypeUtil.TY_SSC_YSGX);
            } else if (g == 0) {
                zwzb = 0;
                dsl = nomyData.getsx(4, TypeUtil.SX_SBL);
            }

            if (maxg > 1 && skill != null) {
                if (fl == 1) {
                    DataActionType.getActionById(21).analysisAction(manData, fightingEvents, "分裂", battlefield);
                } else if (z != 0) {
                    DataActionType.getActionById(21).analysisAction(manData, fightingEvents, "追击", battlefield);
                } else {
                    DataActionType.getActionById(21).analysisAction(manData, fightingEvents, isHSSF ? TypeUtil.BB_E_HSSF : TypeUtil.PTGJ, battlefield);
                }
                return;
            }
            guiwei.clear();
            int gjw = 3;
            //移动一个流程 出手移动
            if (g == 0) {
                if (skill9 != null) {
                    MixDeal.move(manData.getCamp(), manData.getMan(), "瞬移", nomyData.getCamp() + "|" + nomyData.getMan() + "|" + gjw, battlefield);
                } else if (skill4 != null) {
                    MixDeal.move(manData.getCamp(), manData.getMan(), "遁地", nomyData.getCamp() + "|" + nomyData.getMan() + "|" + gjw, battlefield);
                } else if (attackType == 1) {//孙小圣
                    sxsEvents = MixDeal.sxsmove(nomyData, manData, battlefield);
                } else {
                    MixDeal.move(manData.getCamp(), manData.getMan(), "移动", nomyData.getCamp() + "|" + nomyData.getMan() + "|" + gjw, battlefield);
                }
                if (skill_9 != null && skill_9.getSkillcontinued() == 1) {
                    skill_9.setSkillcontinued(0);
                    battlefield.NewEvents.get(battlefield.NewEvents.size() - 1).getAccepterlist().get(0).setSkillskin("1233");
                } else if (fl == 1) {
                    battlefield.NewEvents.get(battlefield.NewEvents.size() - 1).getAccepterlist().get(0).setSkillskin("1832");
                } else if (z != 0) {
                    battlefield.NewEvents.get(battlefield.NewEvents.size() - 1).getAccepterlist().get(0).setSkillskin("1831");
                }
                if (skill2 != null) {
                    battlefield.NewEvents.get(battlefield.NewEvents.size() - 1).getAccepterlist().add(MixDeal.getChildSkill(child, skill2.getSkillname()));
                    skill2 = null;
                }
            }


            //一次攻击一个流程  先反震  在  反击
            gjEvents = new FightingEvents();
            List<FightingState> zls = new ArrayList<>();
            FightingState ace = new FightingState();
            FightingState bao = new FightingState();

            // 处理灵犀-火冒三丈、攻守兼备
            int hmgs = manData.executeHmsz(zls);
            if (hmgs == 2) {
                // 势不可揭额外攻击
                sbke = Zap.longValue();
            } else {
                sbke = 0;
            }

            // 本次攻击为连击，并且为连击的第一次攻击
            if (g == 0 && maxg > 1) {
                // 处理灵犀-惊涛拍岸
                maxg = manData.executeJtpa(zls, maxg);
                jgbg = false;
            }
            // 是否闪避
            boolean sb = false;
            int bb_e = 0;
            if (skill_1 != null && Battlefield.isV(skill_1.getSkillgain())) {
                bb_e = 1;
            } else if (skill_2 != null && Battlefield.isV(skill_2.getSkillgain())) {
                bb_e = 2;
            } else if (skill_7 != null && Battlefield.isV(skill_7.getSkillgain())) {
                bb_e = 7;
            } else if (skill_8 != null && Battlefield.isV(skill_8.getSkillgain())) {
                bb_e = 8;
            }
            g++;
            if (g > 1 && skill8 != null && Battlefield.isV(skill8.getSkillhurt())) {
                // 连击被打断判定将功补过
                manData.executeJgbg(zls);
                g = maxg;//打断连击成功
                ace.setCamp(nomyData.getCamp());
                ace.setMan(nomyData.getMan());
                ace.setStartState(TypeUtil.JN);
                ace.setSkillskin(skill8.getSkilltype());
                zls.add(ace);
            } else {
                if (bb_e == 1 || skill3 != null || skill11 != null || !Battlefield.isV(dsl + dsjc - manData.getQuality().getRolefmzl() - mzjc - manData.mz + manData.executeYszd(3) - manData.mz)) {
                    if (skill_BB_LHFM != null && g == 1) {
                        FightingState lhfm = new FightingState();
                        ChangeFighting change = new ChangeFighting();
                        if (Battlefield.isV(skill_BB_LHFM.getSkillgain())) {
                            change.setChangetype(skill_BB_LHFM.getSkilltype());
                            change.setChangesum(skill_BB_LHFM.getSkillcontinued());
                        }
                        nomyData.ChangeData(change, lhfm);
                        lhfm.setSkillskin(skill_BB_LHFM.getSkilltype());
                        lhfm.setStartState(TypeUtil.JN);
                        zls.add(lhfm);
                    }
                    ChangeFighting acec = new ChangeFighting();
                    if (skill4 != null) {
                        wulikanhua(acec, skill4, battlefield);
                    }
                    ap = Hurt(Zap.add(zap.add(BigDecimal.valueOf(sbke + (bb_e == 7 ? zap7 : bb_e == 8 ? zap8 : 0)))), g, manData, nomyData, type, ace, zls, battlefield, 0, 0);

                    if (ap.longValue() > 0 && manData.getCamp() != nomyData.getCamp() && nomyData.getStates() == 0 && nomyData.executeAdfh()) {
                        FightingSkill adfh = nomyData.getSkillName("烈火骄阳");
                        List<FightingSkill> skills = ControlAction.getSkills(nomyData, adfh, battlefield.BattleType);
                        ManData mubiao = getdaji(nomyData.getCamp(), null, battlefield, nomyData, 0);

                        FightingState Accepter = MixDeal.DSMY(nomyData, mubiao, adfh.getSkillid(), battlefield);
                        if (Accepter == null) {
                            mubiao.addBear(adfh.getSkilltype());
                            Accepter = new FightingState();
                            HurtAction.hurt(BigDecimal.ZERO, 1, 1000000, 0, skills, zls, Accepter, nomyData, mubiao, adfh, battlefield);
                        } else {
                            zls.add(Accepter);
                        }
                        Accepter.setSkillskin(adfh.getSkillid() + "");
                        FightingState Originator = new FightingState();
                        Originator.setEndState(adfh.getSkillname());
                        Originator.setCamp(nomyData.getCamp());
                        Originator.setMan(nomyData.getMan());
                        Originator.setStartState("法术攻击");
                        Originator.setText("上一个敢靠近我的人已经被他的队友打死了，怎么还有人不听话#55");
                        Originator.setSkillsy(adfh.getSkillname());
                        zls.add(Originator);
                    }

                    ManData baodata = battlefield.bhpd(nomyData.getCamp(), nomyData.getMan());
                    if (dhs(type, manData.getCamp(), nomyData.getCamp())) baodata = null;
                    if (skill3 != null) baodata = null;
                    if (skill11 != null) baodata = null;
                    if (baodata == null) {
                        acec.setChangehp(ap.negate());
                        if (skill_5 != null && Battlefield.isV(skill_5.getSkillgain())) {
                            FightingPackage.ChangeProcess(acec, bb_e == 2 ? null : manData, nomyData, ace, TypeUtil.ZSSH, zls, battlefield);
                            ace.setText("不堪一击的选手#2");
                        } else if (skill88 != null && Battlefield.isV(100) && Arith.bigDecimalCompareD(nomyData.getHp_z().multiply(BigDecimal.ONE), nomyData.getHp()) && nomyData.getKangluobao() >= 500) {//新增技能 大威天龙 未保护的目标才能被致死
                            FightingPackage.ChangeProcess(acec, bb_e == 2 ? null : manData, nomyData, ace, TypeUtil.ZSSH, zls, battlefield);
                            ace.setSkillskin("1248");
                            ace.setText("大威天龙#2");
                        } else {
                            FightingPackage.ChangeProcess(acec, bb_e == 2 ? null : manData, nomyData, ace, TypeUtil.PTGJ, zls, battlefield);
                        }
                    } else {
                        ChangeFighting baoc = new ChangeFighting();
                        if (skill4 != null) {
                            wulikanhua(baoc, skill4, battlefield);
                        }
                        baoc.setChangehp((ap.multiply(BigDecimal.valueOf(0.7))).negate());
                        baoc.setChangehp((ap.multiply(BigDecimal.valueOf(0.4))).negate());
                        FightingPackage.ChangeProcess(acec, bb_e == 2 ? null : manData, nomyData, ace, TypeUtil.PTGJ, zls, battlefield);
                        FightingPackage.ChangeProcess(baoc, bb_e == 2 ? null : manData, baodata, bao, TypeUtil.PTGJ, zls, battlefield);
                        //躲闪成功
                        ace.setStartState("瞬移");
                        ace.setEndState(nomyData.getCamp() + "|" + nomyData.getMan() + "|" + getdir(gjw));
                        bao.setStartState("瞬移");
                        bao.setEndState(nomyData.getCamp() + "|" + nomyData.getMan());
                        guiwei.add(baodata);
                        guiwei.add(nomyData);
                    }
                    ace.setSkillskin(bb_e == 7 ? TypeUtil.BB_E_DSFS : bb_e == 8 ? TypeUtil.BB_E_QKYZ : null);

                    if (skill77 != null && Battlefield.isV(50)) {
                        drrlsz(type, manData, nomyData, ap, battlefield, zls);
                        List<ManData> datas = battlefield.getZW(nomyData);
                        for (int i = datas.size() - 1; i >= 0; i--) {
                            ManData nomyData2 = datas.get(i);
                            if (nomyData2.getStates() != 0) continue;
                            drrlsz(type, manData, nomyData2, ap, battlefield, zls);
                        }
                    }

                    feedback(type, manData, ap, battlefield, zls);
                    PhyAttack.neidan(type, manData, nomyData, ap, battlefield, zls, g, 0, 0);
                    if (skill_6 != null && (isHSSF || Battlefield.isV(skill_6.getSkillgain() / 20))) {
                        List<ManData> datas = battlefield.getZW(nomyData);
                        for (int i = datas.size() - 1; i >= 0; i--) {
                            FightingState ace1 = new FightingState();
                            ManData nomyData2 = datas.get(i);
                            if (nomyData2.getStates() != 0) continue;
                            ChangeFighting acec1 = new ChangeFighting();
                            ap = Hurt(Zap.multiply(BigDecimal.valueOf(skill_6.getSkillgain() / 100D)).add(BigDecimal.valueOf(sbke)), g, manData, nomyData2, type, ace1, zls, battlefield, 0, 0);
                            acec1.setChangehp(ap.negate());
                            FightingPackage.ChangeProcess(acec1, bb_e == 2 ? null : manData, nomyData2, ace1, type, zls, battlefield);
                        }
                    }           //魔威
                    if (skill_12012 != null) {
                        FightingState ace1 = new FightingState();
                        ace1.setCamp(nomyData.getCamp());
                        ace1.setMan(nomyData.getMan());
                        ace1.setStartState("普通攻击");
                        ace1.setText("魔威#2");

                        ChangeFighting acec1 = new ChangeFighting();
                        ap = PhyAttack.Hurt((Zap), g, manData, nomyData, TypeUtil.PTGJ, ace1, zls, battlefield, 0, 0);
//                        neidan(type, manData, nomyData, ap, battlefield, zls, g, 0, 0);
//                        if(xyZj) {
//                            ap *= 1.5;
//                            ace1.setProcessState("重击");
//                            ace.setProcessState("重击");
//                        }
                        acec1.setChangehp(ap.negate());
                        FightingPackage.ChangeProcess(acec1, null, nomyData, ace1, TypeUtil.PTGJ, zls, battlefield);
                    }
                    if (skill3 != null) {
                        List<ManData> datas = battlefield.getZW(nomyData);
                        for (int i = datas.size() - 1; i >= 0; i--) {
                            FightingState ace1 = new FightingState();
                            ManData nomyData2 = datas.get(i);
                            if (nomyData2.getStates() != 0) continue;
                            ChangeFighting acec1 = new ChangeFighting();
                            if (skill4 != null)
                                wulikanhua(acec1, skill4, battlefield);
                            ap = Hurt(Zap.add(BigDecimal.valueOf(sbke)), g, manData, nomyData2, type, ace1, zls, battlefield, 0, 0);
                            acec1.setChangehp(ap.negate());
                            FightingPackage.ChangeProcess(acec1, bb_e == 2 ? null : manData, nomyData2, ace1, type, zls, battlefield);
                        }
                    } else if (skill11 != null) {
                        List<ManData> datas = battlefield.getZW(nomyData);
                        for (int i = datas.size() - 1; i >= 0; i--) {
                            FightingState ace1 = new FightingState();
                            ManData nomyData2 = datas.get(i);
                            if (nomyData2.getStates() != 0) continue;
                            ChangeFighting acec1 = new ChangeFighting();
                            ap = Hurt(Zap.add(BigDecimal.valueOf(sbke)), g, manData, nomyData2, "至圣", ace1, zls, battlefield, 0, 0);
                            acec1.setChangehp(ap.negate());
                            ace1.setText("剑荡八荒#2");
                            ace1.setSkillskin("1244");
                            FightingPackage.ChangeProcess(acec1, bb_e == 2 ? null : manData, nomyData2, ace1, "至圣", zls, battlefield);
                        }
                    } else if (skill_12 != null && g == 1 && manData.getStates() == 0) {
//                    } else if (skill_12 != null ) {
                        int skillsum = skill_12.getSkillsum();
                        FightingSkill skill12008 = manData.getSkillId(12008);
                        if (skill12008 != null && Battlefield.isV(skill12008.getSkillcontinued())) {
                            FightingState gjz = new FightingState();
                            gjz.setSkillskin("jbzx");
                            gjz.setStartState("代价");
                            gjz.setCamp(manData.getCamp());
                            gjz.setMan(manData.getMan());
                            gjz.setEndState(nomyData.getCamp() + "|" + nomyData.getMan() + "|3");
                            zls.add(gjz);
                            skillsum += new Random().nextInt(7);
                        }
                        List<ManData> datas = MixDeal.get(false, manData, 0, battlefield.nomy(nomyData.getCamp()), 0, 0, 0, 0, skillsum, 1, battlefield, 1);
//                        List<ManData> datas = battlefield.getZW(nomyData);
//                        MixDeal.BB_DNTG(datas, battlefield, manData, null, skill);
//                        MixDeal.skillmove(datas, battlefield, manData, "10");
                        if (datas.size() > 0) {
                            int gan = Battlefield.random.nextInt(datas.size());
                            MixDeal.move(manData.getCamp(), manData.getMan(), "瞬移", battlefield.nomy(1) + "|" + gan + "|3", battlefield);
                            gjEvents = new FightingEvents();
                            FightingState gjz = new FightingState();
                            gjz.setCamp(manData.getCamp());
                            gjz.setMan(manData.getMan());
                            gjz.setEndState(Battlefield.random.nextInt(8) + "");///ddd
                            gjz.setStartState("普通攻击");
//                        gjz.setText("神通演示");
                            zls.add(gjz);
                            gjEvents.setAccepterlist(zls);
                            Random random = new Random();
                            //修复圣猿第一刀伤害
                            BigDecimal ap1 = Zap.add(Zap.multiply(BigDecimal.ONE));
                            for (int j = datas.size() - 1; j >= 0; j--) {
                                FightingState ace1 = new FightingState();
                                ManData nomyData2 = datas.get(j);
                                if (nomyData2 == nomyData)
                                    continue;
                                if (nomyData2.getStates() != 0) continue;
                                AddState sf = nomyData2.xzstate("分身");
                                if (sf != null) continue;
                                ChangeFighting acec1 = new ChangeFighting();
                                ap = PhyAttack.Hurt(ap1, g, manData, nomyData2, TypeUtil.PTGJ, ace1, zls, battlefield, 0, 0);
//                                neidan(type, manData, nomyData2, ap, battlefield, zls, g, 0, 0);
                                acec1.setChangehp(ap.negate());
                                Integer[] dirs = new Integer[]{0, 2};
                                int i = dirs[random.nextInt(dirs.length)];
                                nomyData2.addAddState("分身", 1000, i, Long.parseLong(manData.getSkin()), 1);
                                FightingState org = new FightingState();
                                org.setCamp(nomyData2.getCamp());
                                org.setMan(nomyData2.getMan());
                                org.setStartState("代价");
                                org.setEndState_1("分身=" + manData.getCamp() + "&" + manData.getMan());
                                org.setEndState(i + "");
                                org.setSkillskin("yun");
                                zls.add(org);
                                FightingPackage.ChangeProcess(acec1, null, nomyData2, ace1, TypeUtil.PTGJ, zls, battlefield);
                            }
                        }
                    } else if (skill_12004 != null && Battlefield.isV(skill_12004.getSkillcontinued())) {
                        FightingState gjz = new FightingState();
                        gjz.setSkillskin("jbzx");
                        gjz.setStartState("代价");
                        gjz.setCamp(manData.getCamp());
                        gjz.setMan(manData.getMan());
                        gjz.setEndState(nomyData.getCamp() + "|" + nomyData.getMan() + "|3");
                        zls.add(gjz);
                        int skillsum = skill_12004.getSkillsum();
                        FightingSkill skill12008 = manData.getSkillId(12008);
                        if (skill12008 != null && Battlefield.isV(skill12008.getSkillcontinued())) {
                            skillsum += new Random().nextInt(2);
                        }

                        List<ManData> datas = MixDeal.get(false, manData, 0, manData.getCamp(), 0, 0, 0, 0, skillsum, -1, battlefield, 1);
                        for (ManData item : datas) {

                            if (item.getStates() != 0) continue;
                            FightingState ace1 = new FightingState();
                            ace1.setCamp(item.getCamp());
                            ace1.setMan(item.getMan());
                            ace1.setStartState("普通攻击");

                            ChangeFighting acec1 = new ChangeFighting();
                            ap = PhyAttack.Hurt((Zap), g, manData, item, TypeUtil.PTGJ, ace1, zls, battlefield, 0, 0);
                            acec1.setChangehp(ap.negate());
                            FightingPackage.ChangeProcess(acec1, null, item, ace1, TypeUtil.PTGJ, zls, battlefield);

                            FightingState move = MixDeal.skillmove(item, manData, "9");
//                            if (StringUtils.isBlank(skin))
                            move.setSkillskin("10016-by");

                            zls.add(move);
                        }
                    } else if (skill_12014 != null) {
                        if (g % 3 == 0) {
                            xyZj = true;
                        }
                        FightingState gjz = new FightingState();
                        gjz.setSkillskin("mv2");
                        gjz.setStartState("代价");
                        gjz.setCamp(manData.getCamp());
                        gjz.setMan(manData.getMan());
                        gjz.setEndState(nomyData.getCamp() + "|" + nomyData.getMan() + "|3");
                        zls.add(gjz);
//                        int skillsum = skill_12014.getSkillsum();
//                        FightingSkill skill12008 = manData.getSkillId(12008);
//                        if (skill12008 != null && Battlefield.isV(skill12008.getSkillcontinued())) {
//                            skillsum += new Random().nextInt(2);
//                        }
                        List<ManData> datas = battlefield.getZW(nomyData);

                        for (ManData item : datas) {

                            if (item.getStates() != 0) continue;
                            FightingState ace1 = new FightingState();
                            ace1.setCamp(item.getCamp());
                            ace1.setMan(item.getMan());
                            ace1.setStartState("普通攻击");

                            ChangeFighting acec1 = new ChangeFighting();
                            ap = PhyAttack.Hurt(Zap, g, manData, item, TypeUtil.PTGJ, ace1, zls, battlefield, 0, 0);
                            if (xyZj) {
                                ap = ap.multiply(BigDecimal.valueOf(1.5));
                                ace1.setProcessState("重击");
                                ace.setProcessState("重击");
                            }
                            acec1.setChangehp(ap.negate());
                            FightingPackage.ChangeProcess(acec1, null, item, ace1, TypeUtil.PTGJ, zls, battlefield);

//                            FightingState move = MixDeal.skillmove(item, manData, "9");
////                            if (StringUtils.isBlank(skin))
//                            move.setSkillskin("10016-by");
//
//                            zls.add(move);
                        }
                    } else if (state1 != null) {
                        List<ManData> datas = battlefield.getZW(nomyData);
                        for (int i = datas.size() - 1; i >= 0; i--) {
                            FightingState ace1 = new FightingState();
                            ManData nomyData2 = datas.get(i);
                            if (nomyData2.getStates() != 0) continue;
                            ChangeFighting acec1 = new ChangeFighting();
                            if (skill4 != null) wulikanhua(acec1, skill4, battlefield);
                            ap = Hurt(Zap.add(BigDecimal.valueOf(sbke)), g, manData, nomyData2, type, ace1, zls, battlefield, 0, 0);
                            ap = (ap.multiply(BigDecimal.valueOf(state1.getStateEffect() / 100)));
                            acec1.setChangehp(ap.negate());
                            FightingPackage.ChangeProcess(acec1, bb_e == 2 ? null : manData, nomyData2, ace1, type, zls, battlefield);
                        }
                    }

                } else {
                    if (g == 1) {
                        // 第一下攻击被闪避被打断判定将功补过
                        manData.executeJgbg(zls);
                    }
                    //躲闪成功
                    ace.setCamp(nomyData.getCamp());
                    ace.setMan(nomyData.getMan());
                    ace.setStartState("瞬移");
                    ace.setProcessState("躲闪");
                    ace.setEndState(nomyData.getCamp() + "|" + nomyData.getMan() + "|" + getdir(gjw));
                    zls.add(ace);
                    guiwei.add(nomyData);
                    sb = true;
                }
            }

            ManData oldm = manData;
            FightingEvents oldf = fightingEvents;
            Battlefield oldb = battlefield;
            if (skill_11 != null) {
                System.out.println(skill_11.getSkillname());
                //TODO 万种风情技能触发概率计算
                if (Battlefield.random.nextInt(100) < skill_11.getSkillgain()) {
                    DataActionType.getActionById(38).analysisAction(manData, fightingEvents, "", battlefield);
                }
            }

            manData = oldm;
            fightingEvents = oldf;
            battlefield = oldb;
            if (manData.getStates() == 0)
                MixDeal.move(manData.getCamp(), manData.getMan(), "瞬移", nomyData.getCamp() + "|" + nomyData.getMan() + "|3", battlefield);

            if (manData.getCamp() == nomyData.getCamp()) gjw = getdir(gjw);
            FightingState gjz = null;
            if (attackType == 1) {
                gjz = MixDeal.skillmove(nomyData, manData, "9", -1);
            } else if (attackType == 2) {
                //gjz=MixDeal.skillmove(nomyData,manData,"3");
            } else {
                gjz = new FightingState();
                gjz.setCamp(manData.getCamp());
                gjz.setMan(manData.getMan());
                if (skill11 == null) gjz.setStartState(TypeUtil.PTGJ);
                else {
                    if (manData.getSkin() != null && manData.getSkin().equals("500636")) {
                        gjz.setStartState("特效1");
                    } else {
                        gjz.setStartState(TypeUtil.PTGJ);
                    }
                }
                if (skill3 == null) gjz.setStartState(TypeUtil.PTGJ);
                else {
                    if (manData.getSkin() != null && manData.getSkin().equals("400315")) {
                        gjz.setStartState("特效" + g);
                    } else {
                        gjz.setStartState(TypeUtil.PTGJ);
                    }
                }
                if (skill_12014 != null && xyZj) {
                    gjz.setStartState("特效" + 1);
                    xyZj = false;
                }
                gjz.setEndState(gjw + "");
            }
            gjz.setSkillsy("attack");
            //触发箭无虚发喊话
            if (bb_e == 1) {
                gjz.setText("箭无虚发#2");
            } else if (bb_e == 2) {
                gjz.setText("一帆风顺#2");
            } else if (bb_e == 7) {
                gjz.setText("大圣附身#2");
            } else if (bb_e == 8) {
                gjz.setText("乾坤一掷#2");
            }
            zls.add(gjz);

            if (zap.longValue() != 0 && zzs != null) {
                gjz.setText("合力一击#2");
                for (int i = 0; i < zzs.size(); i++) {
                    ManData data = zzs.get(i);
                    zls.add(MixDeal.skillmove(nomyData, data, "9"));
                }
            }

            if (!sb) {
                // 灵犀-化险为夷
                manData.addDun(ap.longValue(), gjz);
            }
            List<ManData> fightingdata = battlefield.getFightingdata();
            for (ManData item : fightingdata) {
                AddState sf = item.xzstate("分身");

                if (item.getCamp() != manData.getCamp() && new Random().nextBoolean()) {
                    if (item.getStates() != 0) continue;
                    if (item == nomyData) continue;
                    if (sf != null) {
                        gjz = new FightingState();
                        gjz.setCamp(item.getCamp());
                        gjz.setMan(item.getMan());
                        gjz.setStartState("代价");
//                        gjz.setEndState_2("分身");
                        gjz.setEndState(new BigDecimal(sf.getStateEffect2()).intValue() + "");
                        zls.add(gjz);
                        FightingState ace1 = new FightingState();
                        ace1.setCamp(item.getCamp());
                        ace1.setMan(item.getMan());
                        ace1.setStartState("普通攻击");
                        ace1.setEndState(new BigDecimal(sf.getStateEffect2()).intValue() + "");
                        ChangeFighting acec1 = new ChangeFighting();
                        ap = PhyAttack.Hurt((Zap), g, manData, item, TypeUtil.PTGJ, ace1, zls, battlefield, 0, 0);
                        acec1.setChangehp(ap.negate());
                        FightingPackage.ChangeProcess(acec1, manData, item, ace1, TypeUtil.PTGJ, zls, battlefield);
//                        gjz = new FightingState();
//                        gjz.setCamp(item.getCamp());
//                        gjz.setMan(item.getMan());
//                        gjz.setEndState(new BigDecimal(sf.getStateEffect2() + "").intValue() + "");///ddd
//                        gjz.setStartState("代价");
//                        gjz.setSkillskin("10018");
//                        gjz.setCamp(item.getCamp());
//                        gjz.setMan(item.getMan());
////                        gjz.setEndState_2("分身");
//                        zls.add(gjz);
                        //标记需要再次添加分身的对象
//                        sf.setStateEffect3(1d);
//                        FightingState gjz1 = new FightingState();
//                        gjz1.setCamp(item.getCamp());
//                        gjz1.setMan(item.getMan());
//                        gjz1.setStartState("代价");
//                        gjz1.setEndState_1("分身");
//                        gjz1.setEndState(new BigDecimal(sf.getStateEffect2()).intValue()+"");
//                        zls1.add(gjz1);
                    }
                } else {
//                    if (sf != null) {
//                        gjz = new FightingState();
//                        gjz.setCamp(item.getCamp());
//                        gjz.setMan(item.getMan());
//                        gjz.setStartState("代价");
//                        gjz.setEndState_1("分身");
//                        gjz.setEndState(new BigDecimal(sf.getStateEffect2()).intValue() + "");
//                        zls.add(gjz);
//                    }

                }
            }
            gjEvents.setAccepterlist(zls);
            battlefield.NewEvents.add(gjEvents);

            /*归位*/
            if (guiwei.size() != 0) {
                FightingEvents moveEvents = new FightingEvents();
                List<FightingState> moves = new ArrayList<>();
                for (int j = 0; j < guiwei.size(); j++) {
                    FightingState move = new FightingState();
                    move.setCamp(guiwei.get(j).getCamp());
                    move.setMan(guiwei.get(j).getMan());
                    move.setStartState("瞬移");
                    move.setEndState(guiwei.get(j).getCamp() + "|" + guiwei.get(j).getMan());
                    moves.add(move);
                    moveEvents.setAccepterlist(moves);
                }
                moveEvents.setAccepterlist(moves);
                battlefield.NewEvents.add(moveEvents);
            }

            //判断是否反击
            if (bb_e != 2 && skill3 == null && skill_9 == null && guiwei.size() == 0 && nomyData.getStates() == 0 && manData.getStates() == 0) {
                if (maxf == 0)
                    maxf = AttackSum(nomyData.getQuality().getRoleffjl() - manData.getQuality().getRolehsfj() - hsfjv, nomyData.getQuality().getRoleffjv());
                if (f < maxf) {
                    //反击出现
                    f++;
                    FightingEvents fjEvents = new FightingEvents();
                    List<FightingState> fjs = new ArrayList<>();
                    FightingState fj = new FightingState();
                    ChangeFighting fjc = new ChangeFighting();
                    ap = Hurt(nomyData.getAp(), g, nomyData, manData, "反击", fj, zls, battlefield, 0, 0);
                    fjc.setChangehp(ap.negate());
                    FightingPackage.ChangeProcess(fjc, nomyData, manData, fj, type, fjs, battlefield);
                    FightingState fjgjz = new FightingState();
                    fjgjz.setCamp(nomyData.getCamp());
                    fjgjz.setMan(nomyData.getMan());
                    fjgjz.setStartState(TypeUtil.PTGJ);
                    fjgjz.setEndState("3");
                    fjs.add(fjgjz);
                    fjEvents.setAccepterlist(fjs);
                    battlefield.NewEvents.add(fjEvents);
                }

            }


            if (skill3 != null) {
                maxg = 1 + manData.ljs(ljjc);
                if (maxg > 3) maxg = 3;
            }

            if (nomyData.getStates() != 0 && g < maxg && zwzb < 2 && skill_10 != null && Battlefield.isV(skill_10.getSkillhurt())) {//追亡逐北
                zwzb++;
                List<ManData> datas = MixDeal.get(false, manData, 0, battlefield.nomy(nomyData.getCamp()), 0, 0, 0, 0, 1, 1, battlefield, 1);
                if (datas.size() != 0) {//找到换人目标 换人接着连击
                    nomyData = datas.get(0);
                    skill8 = nomyData.getSkillType(TypeUtil.TY_SSC_YSGX);
                    dsl = nomyData.getsx(4, TypeUtil.SX_SBL);
                    dsjc = 0;
                    FightingSkill skill7 = nomyData.getAppendSkill(9226);
                    if (skill7 != null) {
                        dsjc = skill7.getSkillhurt();
                    }
                    addState = nomyData.xzstate(TypeUtil.BB_TJTT);
                    if (addState != null) {
                        dsjc -= addState.getStateEffect();
                    }

                    if (skill9 != null) {//移动一个流程 出手移动
                        MixDeal.move(manData.getCamp(), manData.getMan(), "瞬移", nomyData.getCamp() + "|" + nomyData.getMan() + "|" + 3, battlefield);
                    } else if (skill4 != null) {
                        MixDeal.move(manData.getCamp(), manData.getMan(), "遁地", nomyData.getCamp() + "|" + nomyData.getMan() + "|" + 3, battlefield);
                    } else if (attackType == 1) {//孙小圣
                        sxsEvents = MixDeal.sxsmove(nomyData, manData, battlefield);
                    } else {
                        MixDeal.move(manData.getCamp(), manData.getMan(), "移动", nomyData.getCamp() + "|" + nomyData.getMan() + "|" + 3, battlefield);
                    }
                    continue;
                }
            }

            if (nomyData.getStates() != 0 || nomyData.xzstate(TypeUtil.FY) != null) {//死亡中断连击
                g = maxg;

                if (nomyData.getStates() != 0) {
                    // 目标死亡判定一往无前
                    manData.executeYwwq(gjz);
                }
                if (nomyData.getStates() != 0 && (type.equals("神力加身") || type.equals("力挽狂澜") || type.equals("势如破竹"))) {
                    // 目标死亡判定法门
                    manData.fmPTGJ(gjz);
                }
            }


            boolean is = true;
            boolean isGW = false;//归为

            if (skill_BB_LHFM != null) {
                if (skill_BB_LHFM.getSkillhurt() < skill_BB_LHFM.getSkillsum()) {
                    skill_BB_LHFM.setSkillhurt(skill_BB_LHFM.getSkillhurt() + 1);
                    maxg = 1;
                    g = 0;
                    maxf = 0;
                    fl = 0;
                    skill8 = null;
                    nomyData = getdaji(nocamp, null, battlefield, manData, skill5 != null ? skill5.getSkillhurt() : skill6 != null ? skill6.getSkillhurt() : 0);
                    if (nomyData != null) {
                        dsjc = 0;
                        FightingSkill skill7 = nomyData.getAppendSkill(9226);
                        if (skill7 != null) {
                            dsjc = skill7.getSkillhurt();
                        }
                        addState = nomyData.xzstate(TypeUtil.BB_TJTT);
                        if (addState != null) {
                            dsjc -= addState.getStateEffect();
                        }
                    }
                    is = false;
                    zap = BigDecimal.ZERO;
                    zzs = null;
                    if (skill_4 != null && Battlefield.isV(skill_4.getSkillgain())) {
                        zzs = new ArrayList<>();
                        for (int i = battlefield.fightingdata.size() - 1; i >= 0; i--) {
                            ManData data = battlefield.fightingdata.get(i);
                            if (data.getType() != 1 || data.getCamp() != manData.getCamp() || data.getStates() != 0) {
                                continue;
                            }
                            if (data.getMan() == manData.getMan()) {
                                continue;
                            }
                            zap = zap.add(data.getAp());
                            zzs.add(data);
                        }
//                        if (zap >= 250000) {
                        if (Arith.bigDecimalCompareDD(zap, BigDecimal.valueOf(250000))) {

                            zap = BigDecimal.valueOf(250000);
                        }
                    }
                }
            }


            nb = false;
            zy = false;

            // 灵犀-战意分裂（不加追击数）
            if (is && g == maxg && manData != null && manData.getStates() == 0) {
                if (manData.executeZhanyi()) {
                    List<ManData> datas = MixDeal.get(false, nomyData, 0, nocamp, 0, 0, 0, 0, 1, -1, battlefield, 1);
                    if (datas.size() != 0) {
                        nomyData = datas.get(0);
                        if (nomyData != null) {
                            fl = 0;
                            maxg = 1;
                            g = 0;
                            maxf = 0;
                            skill8 = null;
                            // 怒不可揭判定
                            nb = manData.executeNbkj(nbkj++);
                            zy = true;
                            dsjc = 0;
                            FightingSkill skill7 = nomyData.getAppendSkill(9226);
                            if (skill7 != null) {
                                dsjc = skill7.getSkillhurt();
                            }
                            if (skill3 != null) {
                                maxg = GMax(manData, nomyData, ljjc, battlefield);
                                if (maxg > 3) {
                                    maxg = 3;
                                }
                            } else {
                                maxg = GMax(manData, nomyData, ljjc, battlefield);
                            }
                            isGW = true;
                        }
                        is = false;
                        zap = BigDecimal.ZERO;
                        zzs = null;
                        if (skill_4 != null && Battlefield.isV(skill_4.getSkillgain())) {
                            zzs = new ArrayList<>();
                            for (int i = battlefield.fightingdata.size() - 1; i >= 0; i--) {
                                ManData data = battlefield.fightingdata.get(i);
                                if (data.getType() != 1 || data.getCamp() != manData.getCamp() || data.getStates() != 0) {
                                    continue;
                                }
                                if (data.getMan() == manData.getMan()) {
                                    continue;
                                }
                                zap = zap.add(data.getAp());
                                zzs.add(data);
                            }
                            if (Arith.bigDecimalCompareDD(zap, BigDecimal.valueOf(250000))) {

                                zap = BigDecimal.valueOf(250000);
                            }
                        }
                    }
                }
            }


            //追击判断
            if (is && manData != null && manData.getStates() == 0 && nomyData.getStates() != 0) {
                if (z < 3 && manData.getSkillType("追击") != null) {
                    if (Battlefield.random.nextInt(100) + 1 < manData.getSkillType("追击").getSkillhurt()) {//追击成功
                        // 修改追击时目标为上一个死亡方的人
                        nomyData = getdaji(nocamp, null, battlefield, manData, skill5 != null ? skill5.getSkillhurt() : skill6 != null ? skill6.getSkillhurt() : 0);
                        if (nomyData != null) {
                            z++;
                            maxg = 1;
                            g = 0;
                            maxf = 0;
                            fl = 0;
                            skill8 = null;
                            // 怒不可揭判定
                            nb = manData.executeNbkj(nbkj++);
                            dsjc = 0;
                            FightingSkill skill7 = nomyData.getAppendSkill(9226);
                            if (skill7 != null) {
                                dsjc = skill7.getSkillhurt();
                            }
                            addState = nomyData.xzstate(TypeUtil.BB_TJTT);
                            if (addState != null) {
                                dsjc -= addState.getStateEffect();
                            }
                            if (skill3 != null) {
                                maxg = GMax(manData, nomyData, ljjc, battlefield);
                                if (maxg > 3) {
                                    maxg = 3;
                                }
                            } else {
                                maxg = GMax(manData, nomyData, ljjc, battlefield);
                            }
                            isGW = true;
                        }
                        is = false;
                        zap = BigDecimal.ZERO;
                        zzs = null;
                        if (skill_4 != null && Battlefield.isV(skill_4.getSkillgain())) {
                            zzs = new ArrayList<>();
                            for (int i = battlefield.fightingdata.size() - 1; i >= 0; i--) {
                                ManData data = battlefield.fightingdata.get(i);
                                if (data.getType() != 1 || data.getCamp() != manData.getCamp() || data.getStates() != 0) {
                                    continue;
                                }
                                if (data.getMan() == manData.getMan()) {
                                    continue;
                                }
                                zap = zap.add(data.getAp());
                                zzs.add(data);
                            }
                            if (Arith.bigDecimalCompareDD(zap, BigDecimal.valueOf(250000))) {

                                zap = BigDecimal.valueOf(250000);
                            }
                        }
                    }
                } else if (z > 0) {
                    if (skill_3 != null && Battlefield.isV(skill_3.getSkillgain())) {
                        skill_3 = null;
                        gjz.setText("一鼓作气#2");
                        z++;
                        maxg = 1;
                        g = 0;
                        maxf = 0;
                        skill8 = null;
                        //nomyData=getdaji(nocamp,null,battlefield,manData,skill5!=null?skill5.getSkillhurt():skill6!=null?skill6.getSkillhurt():0);
                        // 修改追击时目标为上一个死亡方的人
                        nomyData = getdaji(nocamp, null, battlefield, manData, skill5 != null ? skill5.getSkillhurt() : skill6 != null ? skill6.getSkillhurt() : 0);
                        if (nomyData != null) {
                            dsjc = 0;
                            FightingSkill skill7 = nomyData.getAppendSkill(9226);
                            if (skill7 != null) {
                                dsjc = skill7.getSkillhurt();
                            }
                            addState = nomyData.xzstate(TypeUtil.BB_TJTT);
                            if (addState != null) {
                                dsjc -= addState.getStateEffect();
                            }
                            if (skill3 != null) {
                                maxg = GMax(manData, nomyData, ljjc, battlefield);
                                if (maxg > 3) {
                                    maxg = 3;
                                }
                            } else {
                                maxg = GMax(manData, nomyData, ljjc, battlefield);
                            }
                            isGW = true;
                        }
                        is = false;
                        zap = BigDecimal.ZERO;
                        zzs = null;
                        if (skill_4 != null && Battlefield.isV(skill_4.getSkillgain())) {
                            zzs = new ArrayList<>();
                            for (int i = battlefield.fightingdata.size() - 1; i >= 0; i--) {
                                ManData data = battlefield.fightingdata.get(i);
                                if (data.getType() != 1 || data.getCamp() != manData.getCamp() || data.getStates() != 0) {
                                    continue;
                                }
                                if (data.getMan() == manData.getMan()) {
                                    continue;
                                }
                                zap = zap.add(data.getAp());
                                zzs.add(data);
                            }
                            if (Arith.bigDecimalCompareDD(zap, BigDecimal.valueOf(250000))) {

                                zap = BigDecimal.valueOf(250000);
                            }
                        }
                    }
                }
            }
            if (is && g == maxg && nomyData != null) {
                if (skill_9 != null && nomyData.getStates() == 0 && skill_9.getSkillhitrate() < battlefield.CurrentRound && Battlefield.isV(skill_9.getSkillgain())) {
                    skill_9.setSkillhitrate(battlefield.CurrentRound);
                    skill_9.setSkillcontinued(1);
                    g = 0;
                    isGW = true;
                } else if (fl == 0 && manData.getSkillType("分裂") != null) {//分裂判断
//                    if (Battlefield.random.nextInt(100) + 1 < manData.getSkillType("分裂").getSkillhurt()) {
                    if (30 < manData.getSkillType("分裂").getSkillhurt()) {
                        nomyData = getdaji(nocamp, null, battlefield, manData, skill5 != null ? skill5.getSkillhurt() : skill6 != null ? skill6.getSkillhurt() : 0);
                        if (nomyData != null) {
                            fl++;
                            maxg = 1;
                            g = 0;
                            maxf = 0;
                            skill8 = null;
                            // 怒不可揭判定
                            nb = manData.executeNbkj(nbkj++);
                            dsjc = 0;
                            FightingSkill skill7 = nomyData.getAppendSkill(9226);
                            if (skill7 != null) {
                                dsjc = skill7.getSkillhurt();
                            }
                            if (skill3 != null) {
                                maxg = GMax(manData, nomyData, ljjc, battlefield);
                                if (maxg > 3) {
                                    maxg = 3;
                                }
                            } else {
                                maxg = GMax(manData, nomyData, ljjc, battlefield);
                            }
                            isGW = true;
                        }
                        zap = BigDecimal.ZERO;
                        zzs = null;
                        if (skill_4 != null && Battlefield.isV(skill_4.getSkillgain())) {
                            zzs = new ArrayList<>();
                            for (int i = battlefield.fightingdata.size() - 1; i >= 0; i--) {
                                ManData data = battlefield.fightingdata.get(i);
                                if (data.getType() != 1 || data.getCamp() != manData.getCamp() || data.getStates() != 0) {
                                    continue;
                                }
                                if (data.getMan() == manData.getMan()) {
                                    continue;
                                }
                                zap = zap.add(data.getAp());
                                zzs.add(data);
                            }
                            if (Arith.bigDecimalCompareDD(zap, BigDecimal.valueOf(250000))) {

                                zap = BigDecimal.valueOf(250000);
                            }
                        }
                    }
                } else if (fl == 0 && (type.equals("神力加身") || type.equals("力挽狂澜") || type.equals("势如破竹")) && Battlefield.isV(manData.getFmsld() / 455)) {
                    nomyData = getdaji(nocamp, null, battlefield, manData, skill5 != null ? skill5.getSkillhurt() : skill6 != null ? skill6.getSkillhurt() : 0);
                    if (nomyData != null) {
                        fl++;
                        maxg = 1;
                        g = 0;
                        maxf = 0;
                        skill8 = null;
                        // 怒不可揭判定
                        nb = manData.executeNbkj(nbkj++);
                        dsjc = 0;
                        FightingSkill skill7 = nomyData.getAppendSkill(9226);
                        if (skill7 != null) {
                            dsjc = skill7.getSkillhurt();
                        }
                        if (skill3 != null) {
                            maxg = GMax(manData, nomyData, ljjc, battlefield);
                            if (maxg > 3) {
                                maxg = 3;
                            }
                        } else {
                            maxg = GMax(manData, nomyData, ljjc, battlefield);
                        }
                        isGW = true;
                    }
                    zap = BigDecimal.ZERO;
                    zzs = null;
                    if (skill_4 != null && Battlefield.isV(skill_4.getSkillgain())) {
                        zzs = new ArrayList<>();
                        for (int i = battlefield.fightingdata.size() - 1; i >= 0; i--) {
                            ManData data = battlefield.fightingdata.get(i);
                            if (data.getType() != 1 || data.getCamp() != manData.getCamp() || data.getStates() != 0) {
                                continue;
                            }
                            if (data.getMan() == manData.getMan()) {
                                continue;
                            }
                            zap = zap.add(data.getAp());
                            zzs.add(data);
                        }
                        if (Arith.bigDecimalCompareDD(zap, BigDecimal.valueOf(250000))) {

                            zap = BigDecimal.valueOf(250000);
                        }
                    }
                } else if (fl == 1) {
                    if (skill_3 != null && Battlefield.isV(skill_3.getSkillgain())) {
                        skill_3 = null;
                        gjz.setText("一鼓作气#2");
                        fl++;
                        maxg = 1;
                        g = 0;
                        maxf = 0;
                        skill8 = null;
                        nomyData = getdaji(nocamp, null, battlefield, manData, skill5 != null ? skill5.getSkillhurt() : skill6 != null ? skill6.getSkillhurt() : 0);
                        if (nomyData != null) {
                            dsjc = 0;
                            FightingSkill skill7 = nomyData.getAppendSkill(9226);
                            if (skill7 != null) {
                                dsjc = skill7.getSkillhurt();
                            }
                            if (skill3 != null) {
                                maxg = GMax(manData, nomyData, ljjc, battlefield);
                                if (maxg > 3) {
                                    maxg = 3;
                                }
                            } else {
                                maxg = GMax(manData, nomyData, ljjc, battlefield);
                            }
                            isGW = true;
                        }
                        zap = BigDecimal.ZERO;
                        zzs = null;
                        if (skill_4 != null && Battlefield.isV(skill_4.getSkillgain())) {
                            zzs = new ArrayList<>();
                            for (int i = battlefield.fightingdata.size() - 1; i >= 0; i--) {
                                ManData data = battlefield.fightingdata.get(i);
                                if (data.getType() != 1 || data.getCamp() != manData.getCamp() || data.getStates() != 0) {
                                    continue;
                                }
                                if (data.getMan() == manData.getMan()) {
                                    continue;
                                }
                                zap = zap.add(data.getAp());
                                zzs.add(data);
                            }
                            if (Arith.bigDecimalCompareDD(zap, BigDecimal.valueOf(250000))) {

                                zap = BigDecimal.valueOf(250000);
                            }
                        }
                    }
                }
            }
            if (nomyData == null || nomyData.getStates() != 0 || manData.getStates() != 0) {//死亡中断连击
                g = maxg;
            }
            //结束归位
            if (isGW) {
                if (attackType == 1) {
                    if (sxsEvents != null) {
                        battlefield.NewEvents.add(sxsEvents);
                    }
                } else {
                    MixDeal.move(manData.getCamp(), manData.getMan(), "移动", manData.getCamp() + "|" + manData.getMan(), battlefield);

                }
            } else if (g >= maxg && manData.getStates() == 0) {
                if (skill4 != null) {
                    MixDeal.move(manData.getCamp(), manData.getMan(), "遁地", manData.getCamp() + "|" + manData.getMan(), battlefield);
                } else if (skill9 != null) {
                    if (Battlefield.isV(skill9.getSkillgain())) {
                        MixDeal.BB_TJTT(manData, Zap.longValue(), nocamp, skill9, battlefield);
                    } else {
                        MixDeal.move(manData.getCamp(), manData.getMan(), "瞬移", manData.getCamp() + "|" + manData.getMan(), battlefield);
                    }
                } else {
                    if (attackType == 1) {
                        if (sxsEvents != null) {
                            battlefield.NewEvents.add(sxsEvents);
                        }
                    } else {
                        MixDeal.move(manData.getCamp(), manData.getMan(), "瞬移", manData.getCamp() + "|" + manData.getMan(), battlefield);
                        gjEvents = new FightingEvents();
                        gjz = new FightingState();
                        gjz.setSkin(skin);
                        gjz.setStartState("代价");
                        gjz.setSkillskin("yun");
                        gjz.setCamp(manData.getCamp());
                        gjz.setMan(manData.getMan());
                        zls = new ArrayList<>();
                        zls.add(gjz);
                        if (StringUtils.isNotBlank(skin)) {
                            skin = null;
                        }
                        fightingdata = battlefield.getFightingdata();
                        for (ManData item : fightingdata) {
                            AddState fs = item.xzstate("分身");
                            if (item.getCamp() != manData.getCamp() && fs != null) {
                                gjz = new FightingState();
//                                    gjz.setSkin(skin);
                                gjz.setStartState("代价");
                                gjz.setSkillskin("yun1");
                                gjz.setEndState_2("分身");
                                gjz.setCamp(item.getCamp());
                                gjz.setMan(item.getMan());
                                zls.add(gjz);
                            }
                        }

                        gjEvents.setAccepterlist(zls);
                        battlefield.NewEvents.add(gjEvents);
                    }
                }
            } else if (g >= maxg) {
                if (attackType == 1) {
                    if (sxsEvents != null) {
                        battlefield.NewEvents.add(sxsEvents);
                    }
                } else {
                    MixDeal.move(manData.getCamp(), manData.getMan(), "瞬移", manData.getCamp() + "|" + manData.getMan(), battlefield);
                    gjEvents = new FightingEvents();
                    gjz = new FightingState();
                    gjz.setSkin(skin);
                    gjz.setStartState("代价");
                    gjz.setSkillskin("yun");
                    gjz.setCamp(manData.getCamp());
                    gjz.setMan(manData.getMan());
                    zls = new ArrayList<>();
                    zls.add(gjz);
                    if (StringUtils.isNotBlank(skin)) {
                        skin = null;
                    }
                    fightingdata = battlefield.getFightingdata();
                    for (ManData item : fightingdata) {
                        AddState fs = item.xzstate("分身");
                        if (item.getCamp() != manData.getCamp() && fs != null) {
                            gjz = new FightingState();
//                                    gjz.setSkin(skin);
                            gjz.setStartState("代价");
                            gjz.setSkillskin("yun1");
                            gjz.setEndState_2("分身");
                            gjz.setCamp(item.getCamp());
                            gjz.setMan(item.getMan());
                            zls.add(gjz);
                        }
                    }

                    gjEvents.setAccepterlist(zls);
                    battlefield.NewEvents.add(gjEvents);
                }
            }

            if (nb || zy) {
                FightingEvents gjEvents2 = new FightingEvents();
                List<FightingState> zls2 = new ArrayList<>();
                if (nb) {
                    FightingState gjz2 = new FightingState();
                    gjz2.setCamp(manData.getCamp());
                    gjz2.setMan(manData.getMan());
                    ;
                    gjz2.setText("看我的#G怒不可揭");
                    gjz2.setStartState("代价");
                    zls2.add(0, gjz2);
                }
                if (zy) {
                    FightingState gjz2 = new FightingState();
                    gjz2.setCamp(manData.getCamp());
                    gjz2.setMan(manData.getMan());
                    ;
                    gjz2.setText("看我的#G冲冠一怒");
                    gjz2.setStartState("代价");
                    gjz2.setSkillskin("cgyn");
                    zls2.add(0, gjz2);
                }
                gjEvents2.setAccepterlist(zls2);
                battlefield.NewEvents.add(gjEvents2);
            }
        }

        // 从头到尾没有触发连击，判定将功补过
        if (jgbg && battlefield.NewEvents != null && battlefield.NewEvents.size() != 0) {
            manData.executeJgbg(battlefield.NewEvents.get(battlefield.NewEvents.size() - 1).getAccepterlist());
        }
    }

    /**
     * 伤害计算
     *
     * @param ap
     * @param g
     * @param mydaData
     * @param nomaData
     * @param type
     * @param ace
     * @param battlefield
     * @param zm          附加致命
     * @param kb          附加狂暴
     * @return
     */
    public static BigDecimal Hurt(BigDecimal ap, int g, ManData mydaData, ManData nomaData, String type, FightingState ace, List<FightingState> zls, Battlefield battlefield, double zm, double kb) {
        double xs = 1D;
        boolean isZM = false;
        if (!type.equals("反击") && dhs(type, nomaData.getCamp(), mydaData.getCamp())) {
            xs = 0D;
        } else if (ace != null) {
            double jc = mydaData.getQuality().getRolefzml() - nomaData.getQuality().getKzml();
            jc += zm + mydaData.zm + mydaData.executeQtpl(nomaData, zls);// 晴天霹雳判定

            if (type.equals("神力加身") || type.equals("力挽狂澜") || type.equals("势如破竹")) {
                ap = ap.add(BigDecimal.valueOf(mydaData.getFmsld() * 1.5));

            }

            if (Battlefield.isV(jc)) {
                // 绝地反击判定
                mydaData.executeJdfj(zls);
                ace.setProcessState("致命");
                ap = ap.multiply(BigDecimal.valueOf(1.5));
                isZM = true;
            }
            if (ace.getProcessState() == null || !ace.getProcessState().equals("致命")) {
                jc = mydaData.getQuality().getRolefkbl();
                GroupBuff buff = battlefield.getBuff(mydaData.getCamp(), TypeUtil.YBYT);
                if (buff != null) {
                    jc += buff.getValue();
                }

                AddState addState = mydaData.xzstate("神龙摆尾");
                if (addState != null) {
                    jc = jc + (mydaData.getFmsld() / 600);
                }

                jc += kb + mydaData.kb;
                mydaData.kb = 0;
                if (Battlefield.isV(jc)) {
                    ace.setProcessState("狂暴");
                    ap = ap.multiply(BigDecimal.valueOf(1.5));
                }
            }
            if (type.equals(TypeUtil.PTGJ)) {
                ap = rlsz(ap, mydaData, ace);
            } else if (type.equals("奋蹄扬威")) {
                ap = ap.multiply(BigDecimal.valueOf(2.5));
                ap = rlsz(ap, mydaData, ace);
            } else if (type.equals("兵临城下")) {
                double bs = 2;
                if (mydaData.fbgs) {
                    mydaData.fbgs = false;
                    int fbgs = mydaData.executeFbgs(3, null);
                    if (fbgs != 0) {
                        bs = Battlefield.random.nextDouble() * (fbgs - 2.5) + 2.5;
                    }
                } else {
                    if (Battlefield.isV(mydaData.executeFbgs(4, null))) {
                        int fbgs = mydaData.executeFbgs(5, null);
                        if (fbgs != 0) {
                            bs = Battlefield.random.nextDouble() * (fbgs - 1) + 1;
                        }
                    }
                }
                ap = ap.multiply(BigDecimal.valueOf(bs));
                ap = rlsz(ap, mydaData, ace);
            } else if (type.equals("混乱技")) {
                ap = rlsz(ap, mydaData, ace);
            }
            if (g > 1) {
                // 存在惊涛拍岸效果时恒定攻击
                xs *= Math.pow(0.7, g - 1);
                if (mydaData.jtpa) {
                    xs = mydaData.getJtpaXs(xs);
                }
                //法门恒定攻击
                if (type.equals("积健为雄")) {
                    if (Battlefield.isV(mydaData.getFmsld() / 600)) {
                        xs = 0.7;
                    }
                }
            }

            // 灵犀如释重负判定
            mydaData.executeRfzs(nomaData, ace, zls, battlefield);
        }
        return Calculation.getCalculation().PTGJ(mydaData, nomaData, ap, isZM, xs);
    }

    /**
     * 如来神掌
     */
    public static BigDecimal rlsz(BigDecimal ap, ManData mydaData, FightingState ace) {
        FightingSkill skill = mydaData.getSkillType("如来神掌");// 双倍伤害30%几率//3倍伤害20%//4倍伤害10%。
        if (skill != null) {
            int sjl = Battlefield.random.nextInt(100);
            if (sjl < 10) {
                ace.setProcessState("四倍");
                ap = ap.multiply(BigDecimal.valueOf(4));
            } else if (sjl < 30) {
                ace.setProcessState("三倍");
                ap = ap.multiply(BigDecimal.valueOf(3));
            } else if (sjl < 60) {
                ace.setProcessState("双倍");
                ap = ap.multiply(BigDecimal.valueOf(2));
            }
        }
        return ap;
    }

    /**
     * 连击次数 反击次数
     */
    public static int AttackSum(double ljl, double ljs) {
        if (Battlefield.random.nextInt(100) + 1 < ljl) return (int) (ljs);
        return 0;
    }

    /**
     * 判断是否为同阵营打断昏睡
     */
    public static boolean dhs(String type, int camp, int mycamp) {
        if (!type.equals("混乱技") && camp == mycamp) {
            return true;
        }
        return false;
    }

    /**
     * 方向调整
     * 71  35
     * 53  17
     */
    public static int getdir(int dir) {
        if (dir == 7) dir = 3;
        else if (dir == 3) dir = 7;
        else if (dir == 5) dir = 1;
        else if (dir == 1) dir = 5;
        return dir;
    }

    /**
     * 返回一个主要打击人
     */
    public static ManData getdaji(int nocamp, FightingEvents fightingEvents, Battlefield battlefield, ManData data, double jc) {
        if (nocamp != -1) {
            if (fightingEvents != null && fightingEvents.getAccepterlist() != null && fightingEvents.getAccepterlist().size() != 0) {
                int path = battlefield.Datapathhuo(fightingEvents.getAccepterlist().get(0).getCamp(), fightingEvents.getAccepterlist().get(0).getMan());
                if (path != -1) {
                    ManData data2 = battlefield.fightingdata.get(path);
                    if (data2 != data) {
                        if (data2.zuoyong(0, -1, 0, 0, new PathPoint(-1, -1), 0, 0, 1))
                            return data2;
                    }
                }
            }
        } else {
            if (Battlefield.isV(50 - jc)) {
                nocamp = data.getCamp();
            } else {
                nocamp = battlefield.nomy(data.getCamp());
            }
            List<ManData> datas = MixDeal.get(false, data, 0, nocamp, 0, 0, 0, 0, 1, -1, battlefield, 1);
            if (datas.size() == 0) {
                datas = MixDeal.get(false, data, 0, battlefield.nomy(data.getCamp()), 0, 0, 0, 0, 1, -1, battlefield, 1);
            }
            if (datas.size() != 0) {
                return datas.get(0);
            }
            return null;
        }
        List<ManData> datas = MixDeal.get(false, data, 0, nocamp, 0, 0, 0, 0, 1, -1, battlefield, 1);
        if (datas.size() != 0)
            return datas.get(0);
        return null;
    }

    /**
     * 雾里看花状态附加
     */
    public static void wulikanhua(ChangeFighting fighting, FightingSkill skill, Battlefield battlefield) {
        fighting.setChangetype(skill.getSkilltype());
        fighting.setChangesum(1);
        fighting.setChangevlaue(skill.getSkillhurt());
        battlefield.isWLKH = true;
    }

    /**
     * 封装的连击次数
     */
    public static int GMax(ManData manData, ManData nomyData, double jc, Battlefield battlefield) {
        jc += manData.ljv;
        if (nomyData == null) {
            return 1 + manData.ljs(jc);
        }
        int maxg = 1 + manData.ljs(jc - (nomyData.getSkillType(TypeUtil.TJ_YCDY) == null ? 0 : 15));
        if (PK_MixDeal.isPK(battlefield.BattleType)) {
            GroupBuff buff = battlefield.getBuff(nomyData.getCamp(), TypeUtil.BB_QZ);
            if (buff != null) {
                if (maxg > 3) {
                    maxg = 3;
                }
            }
        }
        return maxg;
    }

    /**
     * 封装的 慈乌反哺 反哺之私处理
     */
    public static void feedback(String type, ManData manData, BigDecimal hurt, Battlefield battlefield, List<FightingState> zls) {
        if (type.equals(TypeUtil.HL) || manData.getMan() < 5 || manData.getMan() > 9) {
            return;
        }
        ManData data = battlefield.getPetParents(manData);
        if (data == null || data.getStates() != 0) {
            return;
        }
        //先判断身上是否有这种技能
        FightingSkill skill = manData.getFeedback();
        if (skill == null) {
            return;
        }
        FightingState fightingState = new FightingState();
        fightingState.setStartState("代价");
        fightingState.setSkillskin(skill.getSkilltype());
        ChangeFighting fighting = new ChangeFighting();
        if (skill.getSkilltype().equals(TypeUtil.BB_CWFB)) {
            fighting.setChangehp(hurt.multiply(BigDecimal.valueOf(0.3)).negate());
        } else {
//            fighting.setChangemp(hurt * 0.3));
            fighting.setChangemp(hurt.multiply(BigDecimal.valueOf(0.3)).negate());
        }
        FightingPackage.ChangeProcess(fighting, manData, data, fightingState, TypeUtil.JN, zls, battlefield);
    }

    public static void drrlsz(String type, ManData manData, ManData nomyData, BigDecimal hurt, Battlefield battlefield, List<FightingState> zls) {

        FightingState fightingState = new FightingState();
        fightingState.setStartState("代价");
        fightingState.setSkillskin("1253");
        fightingState.setText("大日如来神掌#2");
        fightingState.setProcessState("狂暴");
        ChangeFighting changeFighting = new ChangeFighting();
        changeFighting.setChangemp(hurt.negate());//掉蓝
//      changeFighting.setChangemp((int)(hurt*0.3));


        FightingPackage.ChangeProcess(changeFighting, null, nomyData, fightingState, TypeUtil.JN, zls, battlefield);
    }

    /**
     * 封装的内丹处理
     */
    public static void neidan(String type, ManData manData, ManData nomyData, long ap2, Battlefield battlefield, List<FightingState> zls, int g, int z, double gsjc) {
        if (dhs(type, manData.getCamp(), nomyData.getCamp())) {
            return;
        }
        Map<String, AddAttack> attacks = manData.initAttacks();
        if (nomyData.getStates() == 0) {//修罗内丹判断
            FightingSkill fightingSkill = manData.getxlnd();
            if (fightingSkill != null) {
                FightingState fightingState = new FightingState();
                ChangeFighting changeFighting = new ChangeFighting();
                BigDecimal Hurt = Calculation.getCalculation().SMHurt(manData, nomyData, BigDecimal.valueOf(fightingSkill.getSkillhurt()), 0, fightingSkill.getSkilltype(), manData.getCamp() == 1 ? battlefield.MyDeath : battlefield.NoDeath);
                FightingSkill skill = manData.getSkillType("法天相地");
                if (skill != null && manData.getShanghai() >= 500) {
                    FightingState gjz2 = new FightingState();
                    gjz2.setCamp(manData.getCamp());
                    gjz2.setMan(manData.getMan());
                    ;
                    gjz2.setSkillskin("1246");
                    zls.add(0, gjz2);
                    Hurt = Hurt.add(BigDecimal.valueOf(skill.getSkillhurt()));
                    if (Battlefield.isV(10)) {
                        fightingState.setProcessState("狂暴");
                        Hurt = Hurt.multiply(BigDecimal.valueOf(1.5));
                    }
                }
                changeFighting.setChangehp(Hurt.negate());
                FightingPackage.ChangeProcess(changeFighting, null, nomyData, fightingState, fightingSkill.getSkilltype(), zls, battlefield);
                fightingState.setSkillskin(fightingSkill.getSkillid() + "");
            }
        }
        if (nomyData.getStates() == 0) {//判断是否触发浩然
            AddAttack attack = attacks.get("浩然正气");
            if (attack != null) {
                FightingSkill skill = attack.getSkill();
                FightingSkill skill2 = null;
                if (attack.getAddSkill() != null && attack.getAddSkill().length >= 1) {
                    skill2 = attack.getAddSkill()[0];
                }
                double xs = skill.getSkillgain() + (skill2 != null ? skill2.getSkillgain() : 0);
                double value = Battlefield.random.nextInt(100);
                if (xs > value) {
                    FightingState fightingState = new FightingState();
                    ChangeFighting hr = new ChangeFighting();
                    BigDecimal ap1 = Calculation.getCalculation().hrzq(manData, nomyData, skill.getSkillhurt());
                    ap1 = ap1.subtract(BigDecimal.valueOf(nomyData.getQuality().getK_ndhr()));
                    if (ap1.longValue() <= 1) {
                        ap1 = BigDecimal.ONE;
                    }
                    hr.setChangehp(ap1.negate());
                    fightingState.setSkillskin("浩然正气");
                    if (skill2 != null) {
                        fightingState.setText(value > skill.getSkillgain() ? "一年一度#2" : null);
                    }
                    FightingPackage.ChangeProcess(hr, null, nomyData, fightingState, TypeUtil.ND, zls, battlefield);
                }
            }
        }
        //判断是否触发隔山
        AddAttack attack = attacks.get("隔山打牛");
        if (attack != null) {
            FightingSkill skill = attack.getSkill();
            if (Battlefield.isV(skill.getSkillgain() + gsjc)) {
                int size = 1;

                // 惊涛拍岸增加隔山数量
                if (manData.jtpa && manData.getJtpaGs()) {
                    size++;
                }

                int gscamp = battlefield.nomy(nomyData.getCamp());
                FightingSkill skill2 = null;
                if (attack.getAddSkill() != null && attack.getAddSkill().length >= 1) {
                    skill2 = attack.getAddSkill()[0];
                    if (skill2 != null && Battlefield.isV(skill2.getSkillgain())) {
                        size++;
                        if (Battlefield.isV(skill2.getSkillgain() * 0.7)) {
                            size++;
                        }
                        if (Battlefield.isV(skill2.getSkillgain() * 0.3)) {
                            size++;
                        }
                    } else {
                        skill2 = null;
                    }
                }
                List<ManData> gsdatas = MixDeal.get(false, nomyData, 0, gscamp, 0, 0, 0, 0, size, -1, battlefield, 1);
                for (int i = gsdatas.size() - 1; i >= 0; i--) {
                    ManData bjData = gsdatas.get(0);
                    ChangeFighting bjacec = new ChangeFighting();
                    FightingState bjace = new FightingState();
                    BigDecimal ap = Hurt(manData.getAp().multiply(BigDecimal.valueOf(skill.getSkillhurt() / 100)), g, manData, bjData, TypeUtil.PTGJ, null, zls, null, 0, 0);
                    bjacec.setChangehp(ap.negate());
                    if (i == 0 && skill2 != null) {
                        bjace.setText("乱舞狂刀#2");
                    }
                    FightingPackage.ChangeProcess(bjacec, null, bjData, bjace, type, zls, battlefield);
                }
            }
        }
        //判断是否触发幽冥鬼手
        AddState addState = manData.xzstate(TypeUtil.FB_YMGS);
        if (addState != null) {
            int gscamp = battlefield.nomy(nomyData.getCamp());
            List<ManData> gsdatas = MixDeal.get(false, nomyData, 0, gscamp, 0, 0, 0, 0, 1, -1, battlefield, 1);
            if (gsdatas.size() != 0) {
                ManData bjData = gsdatas.get(0);
                ChangeFighting bjacec = new ChangeFighting();
                FightingState bjace = new FightingState();
                BigDecimal ap = Hurt((manData.getAp().multiply(BigDecimal.valueOf(addState.getStateEffect() / 100))), g, manData, bjData, TypeUtil.PTGJ, null, zls, null, 0, 0);
                bjacec.setChangehp(ap.negate());
                FightingPackage.ChangeProcess(bjacec, null, bjData, bjace, type, zls, battlefield);
            }
        }
        //附加攻击攻击判断
        if (z == 0) {
            for (int i = 0; i < 9; i++) {//0-3仙法 4毒 5混乱 6封印 7震慑 8三尸
                double jc = 0;
                String lei = null;
                String id = null;
                ;
                if (i == 0) {
                    jc = manData.getQuality().getF_xf();
                    lei = TypeUtil.F;
                    id = "1044";
                } else if (i == 1) {
                    jc = manData.getQuality().getF_xh();
                    lei = TypeUtil.H;
                    id = "1059";
                } else if (i == 2) {
                    jc = manData.getQuality().getF_xs();
                    lei = TypeUtil.S;
                    id = "1054";
                } else if (i == 3) {
                    jc = manData.getQuality().getF_xl();
                    lei = TypeUtil.L;
                    id = "1049";
                } else if (i == 4) {
                    jc = manData.getQuality().getF_d();
                    lei = TypeUtil.ZD;
                    id = "1019";
                } else if (i == 5) {
                    jc = manData.getQuality().getF_h();
                    lei = TypeUtil.HL;
                    id = "1004";
                } else if (i == 6) {
                    jc = manData.getQuality().getF_f();
                    lei = TypeUtil.FY;
                    id = "1009";
                } else if (i == 7) {
                    jc = manData.getQuality().getF_zs();
                    lei = TypeUtil.ZS;
                    id = "1024";
                } else if (i == 8) {
                    jc = manData.getQuality().getF_sc();
                    lei = TypeUtil.SSC;
                    id = "1069";
                }
                if (jc > Battlefield.random.nextInt(100)) {
                    FightingSkill skill = PhyAttack.skills[i];
                    if (i < 4) {
                        FightingState fightingState = new FightingState();
                        fightingState.setStartState("代价");
                        ChangeFighting changeFighting = new ChangeFighting();
//						int Hurt=Calculation.getCalculation().xianfa(manData,nomyData,MixDeal.addition(fightingState,skill.getSkillhurt(),manData, lei),  lei,manData.getCamp()==1?battlefield.MyDeath:battlefield.NoDeath);
                        BigDecimal Hurt = Calculation.getCalculation().SMHurt(manData, nomyData, BigDecimal.valueOf(MixDeal.addition(fightingState, skill.getSkillhurt(), manData, lei)), 0, lei, manData.getCamp() == 1 ? battlefield.MyDeath : battlefield.NoDeath);

                        changeFighting.setChangehp(Hurt.negate());
                        FightingPackage.ChangeProcess(changeFighting, null, nomyData, fightingState, lei, zls, battlefield);
                        fightingState.setSkillskin(id);
                    } else if (i == 4) {
                        FightingState fightingState = new FightingState();
                        fightingState.setStartState("代价");
                        ChangeFighting changeFighting = new ChangeFighting();
                        changeFighting.setChangetype(lei);
                        changeFighting.setChangesum(1);
                        int up = Calculation.getCalculation().getzdup(manData, skill, 0, nomyData);
                        BigDecimal hurt = Calculation.getCalculation().getzdsh(manData, nomyData, skill, 0);
                        FightingSkill fightingSkill = manData.getSkillType(TypeUtil.TZ_SJDG);
                        if (fightingSkill != null) {
                            up *= (1 + fightingSkill.getSkillhurt() / 100.0);
                            changeFighting.setChangesum(2);
                        }
//                        if (hurt > up) {
                        if (Arith.bigDecimalCompareD(hurt, BigDecimal.valueOf(up))) {
                            hurt = BigDecimal.valueOf(up);
                        }
                        hurt = hurt.subtract(BigDecimal.valueOf(nomyData.getQuality().getKzds()));
//                        if (hurt <= 0) {
                        if (Arith.bigDecimalCompareXD(hurt, BigDecimal.ZERO)) {
                            hurt = BigDecimal.ONE;
                        }
                        changeFighting.setChangehp(hurt.negate());
                        fightingSkill = manData.getSkillType(TypeUtil.TJ_MTL);
                        if (fightingSkill != null) {
                            changeFighting.setChangemp((changeFighting.getChangehp().multiply(BigDecimal.valueOf(0.15))));
                        }
                        changeFighting.setChangevlaue(hurt.doubleValue() / 2);
                        //强化曼陀罗
                        fightingSkill = manData.getSkillType(TypeUtil.TJ_FSSS);
                        if (fightingSkill != null) {
                            changeFighting.setChangemp((changeFighting.getChangehp().multiply(BigDecimal.valueOf(0.2))));
                        }
                        changeFighting.setChangevlaue(hurt.doubleValue() / 2);
                        FightingPackage.ChangeProcess(changeFighting, null, nomyData, fightingState, lei, zls, battlefield);
                        fightingState.setSkillskin(id);
                    } else if (i == 7) {
                        FightingState fightingState = new FightingState();
                        fightingState.setStartState("代价");
                        ChangeFighting changeFighting = ZSAction.TypeHurtCurrent(manData, nomyData, skill.getSkillhurt());
                        FightingPackage.ChangeProcess(changeFighting, null, nomyData, fightingState, lei, zls, battlefield);
                        fightingState.setSkillskin(id);
                    } else if (i == 8) {
                        FightingState fightingState = new FightingState();
                        fightingState.setStartState("代价");

                        ChangeFighting changeFighting = new ChangeFighting();
                        BigDecimal sh = Calculation.getCalculation().sssh(manData, nomyData, MixDeal.addition(fightingState, skill.getSkillhurt(), manData, type));
                        changeFighting.setChangehp(sh.negate());
                        FightingPackage.ChangeProcess(changeFighting, null, nomyData, fightingState, lei, zls, battlefield);
                        fightingState.setSkillskin(id);

                        BigDecimal Vampire = Calculation.getCalculation().sshx(manData, nomyData, skill.getSkillgain(), sh.doubleValue());
                        List<ManData> datas = SSCAction.minhp(manData.getCamp(), 1, battlefield);
                        for (int j = 0; j < 1 && j < datas.size(); j++) {
                            FightingState f2 = new FightingState();
                            ManData data = datas.get(j);
                            AddState s2 = data.xzstate("伤害加深");
                            if (s2 != null) {
                                Vampire = (Vampire.multiply(BigDecimal.valueOf(1 - s2.getStateEffect() / 100)));
                            }
                            changeFighting.setChangehp(Vampire);
                            data.ChangeData(changeFighting, f2);
                            f2.setStartState("药");
                            zls.add(f2);
                        }
                    } else {
                        FightingState fightingState = new FightingState();
                        fightingState.setStartState("代价");
                        ChangeFighting changeFighting = new ChangeFighting();

                        changeFighting.setChangetype(lei);
                        changeFighting.setChangesum(1);

                        FightingPackage.ChangeProcess(changeFighting, null, nomyData, fightingState, lei, zls, battlefield);
                        fightingState.setSkillskin(id);
                    }
                }
            }
        }
    }


    /**
     * 获取4法一熟练度的skill
     * 0-3仙法 4毒 5混乱 6封印 7震慑 8三尸
     */
    public static FightingSkill[] skills;

    /**
     * 初始化技能
     */
    public static void initSkill() {
        skills = new FightingSkill[9];
        for (int i = 0; i < 9; i++) {
            int id = 0;
            if (i == 0) {
                id = 1044;
            } else if (i == 1) {
                id = 1059;
            } else if (i == 2) {
                id = 1054;
            } else if (i == 3) {
                id = 1049;
            } else if (i == 4) {
                id = 1019;
            } else if (i == 5) {
                id = 1004;
            } else if (i == 6) {
                id = 1009;
            } else if (i == 7) {
                id = 1024;
            } else if (i == 8) {
                id = 1069;
            }
            Skill skill = GameServer.getSkill(id + "");//根据技能id获取
            skills[i] = new FightingSkill(skill, 120, 3, 1, 0, 0);
            if (skills[i].getSkillcontinued() != 0) {
                skills[i].setSkillcontinued(1);
            }
        }
    }

    /**
     * 获取金钱
     */
    public static long getMoney(ManData data, Battlefield battlefield) {
        if (data.getType() == 1) {
            data = battlefield.getPetParents(data);
        } else if (data.getType() != 0) {
            return 0;
        }
        if (data == null) {
            return 0;
        }
        ChannelHandlerContext ctx = GameServer.getRoleNameMap().get(data.getManname());
        if (ctx == null) {
            return 0;
        }
        LoginResult login = GameServer.getAllLoginRole().get(ctx);
        if (login == null) {
            return 0;
        }
        return login.getGold().longValue();
    }
}
