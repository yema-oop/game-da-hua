package come.tool.FightingDataAction;

import come.tool.FightingData.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 风熊-巽风连珠
 */
public class FX_YFLZ implements DataAction {

    @Override
    public void analysisAction(ManData manData, FightingEvents fightingEvents,
                               String type, Battlefield battlefield) {
        // TODO Auto-generated method stub暗影
        List<ManData> nomyDatas = new ArrayList<>();
        if ("巽风连珠强化".equals(type))
            nomyDatas = MixDeal.getdaji(99, manData.getCamp(), fightingEvents, battlefield);
        else
            nomyDatas = MixDeal.getdaji(10, manData.getCamp(), fightingEvents, battlefield);

        if (nomyDatas.size() == 0) return;
        List<FightingEvents> gjEventss = new ArrayList<>();
        BigDecimal zap = BigDecimal.ZERO;
        BigDecimal zap7 = BigDecimal.ZERO;
        BigDecimal zap8 = BigDecimal.ZERO;
//		1305	同仇敌忾								物理攻击时有25%几率召唤本方所有在场召唤兽一起作战，本次攻击获得其它召唤兽的攻击力加成(上限15万)。
        FightingSkill skill_4 = null, skill_5 = null, skill_6 = null, skill_7 = null, skill_8 = null;//箭无虚发,一帆风顺,一鼓作气,同仇敌忾,一击毙命,横扫四方
        for (int i = manData.getSkills().size() - 1; i >= 0; i--) {
            FightingSkill sl = manData.getSkills().get(i);
            if (sl.getSkillbeidong() != 1) {
                continue;
            }
            if (sl.getSkilltype().equals(TypeUtil.BB_E_TCDK)) {
                skill_4 = sl;
            } else if (sl.getSkilltype().equals(TypeUtil.BB_E_YJBM)) {
                if (!PK_MixDeal.isPK(battlefield.CurrentRound)) {
                    skill_5 = sl;
                }
            } else if (sl.getSkilltype().equals(TypeUtil.BB_E_HSSF)) {
                skill_6 = sl;
            } else if (sl.getSkilltype().equals(TypeUtil.BB_E_DSFS)) {
                skill_7 = sl;
                zap7 = BigDecimal.valueOf(manData.getKangluobao() * 175);
            } else if (sl.getSkilltype().equals(TypeUtil.BB_E_QKYZ)) {
                skill_8 = sl;
                zap8 = BigDecimal.valueOf(PhyAttack.getMoney(manData, battlefield));
                if (zap8.longValue() >= 500000) {
                    zap8 = BigDecimal.valueOf(500000);
                }
                zap8 = (zap8.divide(BigDecimal.valueOf(2.8)));
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
            if (zap.longValue() >= 500000) {
                zap = BigDecimal.valueOf(500000);
            }
        }


        BigDecimal Zap = manData.getAp();

        int zong = 10;//扫射人数
        if(PK_MixDeal.isPK(battlefield.BattleType)){
            zong = 3;
        }
        for (int i = 0; i < nomyDatas.size(); i++) {
            if (i == zong) break;

            ManData data = nomyDatas.get(i);
            //最大攻击次数
            int maxg = (int) (1 + ((manData.getQuality().getRolefljv() - 1)));//连击次数
            if (maxg <= 2) {
                maxg = 2;
                if(PK_MixDeal.isPK(battlefield.BattleType)){
                    maxg = 1;
                }
            }
            if (PK_MixDeal.isPK(battlefield.BattleType)) {
                GroupBuff buff = battlefield.getBuff(data.getCamp(), TypeUtil.BB_QZ);
                if (buff != null) {
                    if (maxg > 3) {
                        maxg = 3;
                    }
                }

            }
            if (maxg > 7) {
                maxg = 7;
                if(PK_MixDeal.isPK(battlefield.BattleType)){
                    maxg = Battlefield.random.nextInt(4);
                }
            }//连击次数限制

            //已攻击次数
            int g = 0;
            while (data.getStates() == 0 && g < maxg) {

                if (gjEventss.size() <= g) {
                    //一个风熊抬头吐珠动作
                    FightingEvents gjEvents = new FightingEvents();
                    List<FightingState> zls1 = new ArrayList<>();
                    FightingState qishou = new FightingState();
                    qishou.setCamp(manData.getCamp());
                    qishou.setMan(manData.getMan());
                    qishou.setStartState("特效1");//这个快
//                qishou.setStartState("特效2");//官方的
//                qishou.setStartState("法术攻击");//慢的一比
                    zls1.add(qishou);
                    gjEvents.setAccepterlist(zls1);
                    gjEventss.add(gjEvents);
                }

                List<FightingState> zls = gjEventss.get(g).getAccepterlist();
                g++;
                BigDecimal ap = i == 0 ? Zap : Zap;//攻击加成修改处
                if (i == 0) {
                    ap = ap.add(zap);
                }
                FightingState ace = new FightingState();
                if (skill_7 != null && Battlefield.isV(skill_7.getSkillgain())) {
                    ap = ap.add(zap7);
                    ace.setText("大圣附身#2");
                    ace.setSkillskin(TypeUtil.BB_E_DSFS);
                } else if (skill_8 != null && Battlefield.isV(skill_8.getSkillgain())) {
                    ap = ap.add(zap8);
                    ace.setText("乾坤一掷#2");
                    ace.setSkillskin(TypeUtil.BB_E_QKYZ);
                }
                ace.setStartState("被攻击");
//                ace.setText("啊！");
                ace.setSkillskin("1298");//风熊特效
                ace.setEndState(manData.getCamp() + "|" + manData.getMan() + "|" + data.getCamp() + "|" + data.getMan() + "|111");

//                ap=PhyAttack.Hurt(ap,g+1,manData,data,TypeUtil.PTGJ,ace,zls,battlefield,0,0);//连击递减伤害
                ap = PhyAttack.Hurt(ap, 1, manData, data, TypeUtil.PTGJ, ace, zls, battlefield, 0, 0);//连击不递减伤害
                ChangeFighting acec = new ChangeFighting();
                acec.setChangehp(ap.negate());
                if (i == 0 && skill_5 != null && Battlefield.isV(skill_5.getSkillgain())) {
                    FightingPackage.ChangeProcess(acec, null, data, ace, TypeUtil.ZSSH, zls, battlefield);
                    ace.setText("不堪一击的选手#2");
                } else {
                    FightingPackage.ChangeProcess(acec, null, data, ace, TypeUtil.PTGJ, zls, battlefield);
                }
                if (i == 0) {
                    PhyAttack.feedback(TypeUtil.PTGJ, manData, ap, battlefield, zls);
                }
                PhyAttack.neidan(TypeUtil.PTGJ, manData, data, Zap, battlefield, zls, i == 0 ? 3 : 4, i, 0);//触发内丹
                if (i == 0 && skill_6 != null && (Battlefield.isV(skill_6.getSkillgain() / 20))) {
                    List<ManData> datas = battlefield.getZW(data);
                    for (int j = datas.size() - 1; j >= 0; j--) {
                        FightingState ace1 = new FightingState();
                        ManData nomyData2 = datas.get(j);
                        if (nomyData2.getStates() != 0) continue;
                        ChangeFighting acec1 = new ChangeFighting();
                        ap = PhyAttack.Hurt(Zap.multiply(BigDecimal.valueOf(skill_6.getSkillgain() / 100D)), g, manData, nomyData2, TypeUtil.PTGJ, ace1, zls, battlefield, 0, 0);
                        acec1.setChangehp(ap.negate());
                        FightingPackage.ChangeProcess(acec1, null, nomyData2, ace1, TypeUtil.PTGJ, zls, battlefield);
                    }
                }

                if (data.getStates() != 0 && g < maxg ) {//追亡逐北
                    List<ManData> datas = MixDeal.getdaji(99, manData.getCamp(), fightingEvents, battlefield);
                    if (datas.size() != 0) {//找到换人目标 换人接着连击
                        data = datas.get(0);
                        continue;
                    }
                }
            }
        }

//        //一个风熊抬头吐珠动作
//        FightingEvents jiqiangEvents=new FightingEvents();
//        List<FightingState> list1=new ArrayList<>();
//        FightingState qishou=new FightingState();
//        qishou.setCamp(manData.getCamp());
//        qishou.setMan(manData.getMan());
//        qishou.setStartState("特效2");
//        move.setEndState(data.getCamp()+"|"+data.getMan()+"|3");
//        list1.add(qishou);
//        jiqiangEvents.setAccepterlist(list1);


        for (int i = 0; i < gjEventss.size(); i++) {
//            battlefield.NewEvents.add(jiqiangEvents);
            battlefield.NewEvents.add(gjEventss.get(i));
        }
    }

}





