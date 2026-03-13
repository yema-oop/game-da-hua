package come.tool.FightingData;

import org.come.server.GameServer;
import org.come.tool.Arith;
import org.come.tool.WriteOut;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 计算器
 *
 * @author Administrator
 */
public class Calculation {
    private static Calculation calculation;

    public static Calculation getCalculation() {
        if (calculation == null) calculation = new Calculation();
        return calculation;
    }

    //0 抗 1忽视 2强 3伤害
    //人法计算器
    public boolean renfa(ManData mydata, ManData nomyadata, double jichu, String type) {
        return renfaCalculation(jichu, mydata.getsx(2, type), mydata.getsx(1, type), nomyadata.getsx(0, type));
    }

    public boolean renfaCalculation(double jichu, double q, double hs, double k) {
        jichu = (jichu + hs - k) * (1 + q / 100);
        if (Battlefield.random.nextInt(108) < jichu) {
            return true;
        }
        return false;
    }

    public BigDecimal PTGJ(ManData mydata, ManData nomyadata, BigDecimal jichu, boolean isZM, double xs) {
        double five = FiveLineSystem.getSwing(mydata, nomyadata);
        double w = (1 + mydata.getsx(3, "无") / 100);
        if (w > five) {
            five = w;
        }
        double hs = mydata.getsx(1, TypeUtil.PTGJ) * 0.85;
        double kx = nomyadata.getsx(0, TypeUtil.PTGJ) / 100D;
        kx *= (1 - hs / 100D);
//        long hurt = (long) (jichu * (1 - kx) * five);
        BigDecimal hurt = (jichu.multiply(BigDecimal.valueOf((1 - kx) * five)));
        /**伤害上限过滤*/
//        if (hurt > 213999999900L) {
//            hurt = 1;
//            WriteOut.addtxt("伤害上限:" + mydata.getType() + ":" + mydata.getManname() + ":" + mydata.getId(), 9999);
//        }

        if (isZM) {
//            hurt += nomyadata.getHp() * 0.1 * (1 - kx);
            hurt = hurt.add(nomyadata.getHp().multiply(BigDecimal.valueOf(0.1).multiply(BigDecimal.valueOf(1 - kx))));
//            hurt += nomyadata.getHp() * 0.1 * (1 - kx);
        }
//        hurt *= xs;
        hurt = hurt.multiply(BigDecimal.valueOf(xs));
//        return hurt > 1 ? hurt : 1;
        return Arith.bigDecimalCompareD(hurt, BigDecimal.ONE) ? hurt : BigDecimal.ONE;
    }

    /**
     * 仙法鬼火计算器
     */
    public BigDecimal SMHurt(ManData mydata, ManData nomyadata, BigDecimal jichu, double wg, String type, int death) {
        BigDecimal jc = jichu;
        if (type.equals("鬼火")) {
            death += 1;
            if (death > 10) {
                death = 10;
            }
//            jichu = jichu * (1 + death / 16.0);
            jc = jc.multiply(BigDecimal.valueOf(1 + death / 16.0));
        }
        double five = FiveLineSystem.getSwing(mydata, nomyadata);
        double w = (1 + mydata.getsx(3, type) / 100000);
        if (w > five) {
            five = w;
        }
        double hs = mydata.getsx(1, type);
        FightingSkill skill = mydata.getSkillType(TypeUtil.TZ_ZCCG);
        if (skill != null && nomyadata.xzstate(TypeUtil.HS) != null) {
            hs += skill.getSkillhurt();
        }
        double kx = nomyadata.getsx(0, type);
        double qf = 1 + (mydata.getsx(2, type) / 100D);
        hs = (1 + (hs - kx) / 100);

//        BigDecimal hurt = ((jc.add(BigDecimal.valueOf(mydata.getsx(3, type)).multiply(BigDecimal.valueOf(hs).multiply(BigDecimal.valueOf(qf)).multiply(BigDecimal.valueOf(five))c)))));
//        BigDecimal hurt = BigDecimal.valueOf((jichu.doubleValue() + mydata.getsx(3, type)) * hs * qf * five);
        BigDecimal hurt = jichu.add(BigDecimal.valueOf(mydata.getsx(3, type))).multiply(BigDecimal.valueOf(hs * qf * five));
        if (wg != 0) {
            hurt = hurt.add(BigDecimal.valueOf(wg * five));
        }
//        if (hurt > 213999999900L) {
//            hurt = 1;
//            WriteOut.addtxt("伤害上限:" + mydata.getType() + ":" + mydata.getManname() + ":" + mydata.getId(), 9999);
//        }
        boolean skll1 = false;
        List<FightingSkill> mandtaskill;
        mandtaskill = mydata.getSkills();
        for (int i = mandtaskill.size() - 1; i >= 0; i--) {

            if (mandtaskill.get(i).getSkillid() == 8048) {
                skll1 = true;
            }
        }
        if (skll1) { // 75-130%
            hurt = hurt.multiply(BigDecimal.valueOf(0.75 + Battlefield.random.nextInt(50) / 100.0));
        }
        return Arith.bigDecimalCompareD(hurt, BigDecimal.ONE) ? hurt.setScale(0, BigDecimal.ROUND_HALF_UP) : BigDecimal.ONE;
    }

    public int xianfaCalculation(double jichu, double erwai, double q, double hs, double k, double five) {
        //最终数字=基础伤害*(1+强法/100)*(1+忽视/100-对方抗性/100)*五行或者无属性加成系数*(狂暴程度/100+1.5)
        jichu = (jichu + erwai) * (1 + q / 100) * (1 + hs / 100 - k / 100) * five;
        jichu = jichu > 1 ? jichu : 1;
        return (int) jichu;
    }

    //魔计算器
    //震慑hp伤害 mp伤害只算技能基础伤害
    public double mozs(ManData mydata, ManData nomyadata, double jichu) {
        return mozsCalculation(jichu, mydata.getsx(3, "无"), mydata.getsx(2, "震慑"), mydata.getsx(1, "震慑"), nomyadata.getsx(0, "震慑"), FiveLineSystem.getSwing(mydata, nomyadata));
    }

    public double mozsCalculation(double jichu, double w, double q, double hs, double k, double five) {
        /*double factor = 0.25356634D;
        double noFiveElementsAddition = w / 106.0D * factor;
        double enhancementAddition = enhancement / 40.0D * factor;
        fiveElementsAddition = (fiveElementsAddition <= 1.0D) ? 0.0D : (fiveElementsAddition / 4.0D * factor);

*/
//		(法术震慑伤害-对方抗震+忽视抗震慑)*(1+无属性伤害加成%)*(1+强吸)*五行加成;
        if ((1 + w / 100) > five) {
            five = (1 + w / 100);
        }
        jichu = (jichu - k + hs) * (1 + q / 100) * five;
        jichu = jichu < 50 ? jichu : 50;
        jichu = jichu > 0 ? jichu : 0;
        return jichu;
    }

    public double mozs2(ManData mydata, ManData nomyadata, double jichu, double q) {
        return mozsCalculation2(jichu, mydata.getsx(3, "无"), mydata.getsx(2, "震慑") + q, mydata.getsx(1, "震慑"), nomyadata.getsx(0, "震慑"), FiveLineSystem.getSwing(mydata, nomyadata));
    }

    public double mozsCalculation2(double jichu, double w, double q, double hs, double k, double five) {
        if ((1 + w / 100) > five) {
            five = (1 + w / 100);
        }
        jichu = (jichu - k + hs) * (1 + q / 100) * five;
        jichu = jichu > 0 ? jichu : 0;
        return jichu;
    }

    //魔加成   目前多少就是多少
    public double mofa(double jichu, ManData mydata, String type) {
        return mofaCalculation(jichu, mydata.getsx(2, type));
    }

    //魔加成   目前多少就是多少
    public double mofa(double jichu, ManData mydata, String type, double qian) {
        return mofaCalculation(jichu, mydata.getsx(2, type) + qian);

    }

    public double mofaCalculation(double jichu, double qian) {
        return jichu * (1 + qian / 100.0);
    }

    //鬼计算器
//	鬼三尸伤害
    public BigDecimal sssh(ManData mydata, ManData nomyadata, double jichu) {

        BigDecimal hurt = ssshCalculation(BigDecimal.valueOf(jichu).add(BigDecimal.valueOf(mydata.getsx(3, "三尸"))), mydata.getsx(2, "三尸"), nomyadata.getsx(0, "三尸"), FiveLineSystem.getSwing(mydata, nomyadata));
//        /**伤害上限过滤*/
//        if (hurt > 2139999999) {
//            hurt = 1;
//            WriteOut.addtxt("伤害上限:" + mydata.getType() + ":" + mydata.getManname() + ":" + mydata.getId(), 9999);
//        }

        return hurt;
    }

    public BigDecimal ssshCalculation(BigDecimal jichu, double q, double k, double five) {
        //(三尸法术伤害+装备强三尸伤害-抗三尸)×(1+强力克)×(1+五行伤害)
        jichu = (jichu.add(BigDecimal.valueOf(q - k)).multiply(BigDecimal.valueOf(five)));
        jichu = jichu.doubleValue() > 1 ? jichu : BigDecimal.ONE;
        return jichu;
    }

    //鬼三尸回血
    public BigDecimal sshx(ManData mydata, ManData nomyadata, double jichu, double sh) {
        return sshxCalculation(BigDecimal.valueOf(jichu), sh, mydata.getsx(2, "三尸回血"));
    }

    public BigDecimal sshxCalculation(BigDecimal jichu, double sh, double q) {
        //回血量=三尸伤害×(法术回血程度+装备回血程度)×2
        jichu = BigDecimal.valueOf(sh).multiply(BigDecimal.valueOf(2).multiply(jichu.add(BigDecimal.valueOf(q))).divide(BigDecimal.valueOf(100)));
        jichu = jichu.doubleValue() > 1 ? jichu : BigDecimal.ONE;
        return jichu;
    }

    /**
     * 浩然正气技能
     */
    public BigDecimal hrzq(ManData mydata, ManData nomyadata, double xs) {
        BigDecimal sh = hrzqCalculation(xs, mydata.getMp_z(), nomyadata.getMp_z());
        if (nomyadata.getType() == 2) {
            if (Arith.bigDecimalCompareDD(sh, BigDecimal.valueOf(72800))) {
                sh = BigDecimal.valueOf(72800);
            }
        } else {
            if (Arith.bigDecimalCompareDD(sh, BigDecimal.valueOf(59600))) {
                sh = BigDecimal.valueOf(59600);
            }
        }
        return sh;
    }

    //敌方最大法力*系数
    public BigDecimal hrzqCalculation(double xs, BigDecimal mymp, BigDecimal nomymp) {
        nomymp = (nomymp.subtract(mymp.divide(BigDecimal.valueOf(10))));
//        if (nomymp <= 0) {
        if (Arith.bigDecimalCompareXD(nomymp, BigDecimal.ZERO)) {
            nomymp = BigDecimal.ONE;
        }
        return nomymp.multiply(BigDecimal.valueOf(xs)).divide(BigDecimal.valueOf(100));
    }

    /**
     * 计算毒伤上限
     */
    //120
    public BigDecimal getzdup(ManData mydata, ManData nomyadata, FightingSkill skill, double qds, String type) {
        double hszd = mydata.getsx(1, type) + mydata.hs;
        double kx = nomyadata.getsx(0, type);
        double five = FiveLineSystem.getSwing(mydata, nomyadata);
        if (five == 1) {
            five = (1 + mydata.getsx(3, "无") / 100);
        }
        double qzds = mydata.getQuality().getQzds() + qds;
//        qzds *= 5.6;
        return BigDecimal.valueOf(skill.getSkillgain() * (1 + qzds / 100) * (1 + hszd / 100 - kx / 100) * five);
//		this.skillhurt=skilllvl>3?15:skilllvl>1?12.5:10;//毒伤上限
//		this.skillgain=(lvl+sld/100)*8+(skilllvl==5?10000:skilllvl==4?12000:8000);
    }

    /**
     * 计算毒伤上限
     */
    //120
    public int getzdup(ManData mydata, FightingSkill skill, double qds, ManData nomyadata) {

        double qzds = mydata.getQuality().getQzds();
        double js = 3.6;
        qzds *= js;
        double five = FiveLineSystem.getSwing(mydata, nomyadata);
        if (five == 1) {
            five = 1 + mydata.getsx(3, "无") / 100.0D;
        }
        return (int) (skill.getSkillgain() * (1 + qzds / 100) * five);
//		this.skillhurt=skilllvl>3?15:skilllvl>1?12.5:10;//毒伤上限
//		this.skillgain=(lvl+sld/100)*8+(skilllvl==5?10000:skilllvl==4?12000:8000);
    }

    /**
     * 计算毒伤害
     */
    public BigDecimal getzdsh(ManData mydata, ManData nomyadata, FightingSkill skill, double qds) {

        BigDecimal jichu = BigDecimal.valueOf(skill.getSkillhurt());
        if(Arith.bigDecimalCompareD(jichu,new BigDecimal(42000))) {
            jichu = new BigDecimal(42000);
        }

        double five = FiveLineSystem.getSwing(mydata, nomyadata);
        double w = (1 + mydata.getsx(3, "中毒") / 100000);
        if (w > five) {
            five = w;
        }
        double hs = mydata.getsx(1, "中毒");
//        FightingSkill skill = mydata.getSkillType(TypeUtil.TZ_ZCCG);
//        if (skill != null && nomyadata.xzstate(TypeUtil.HS) != null) {
//            hs += skill.getSkillhurt();
//        }
        double kx = nomyadata.getsx(0, "中毒");
        double qf = 1 + (mydata.getsx(2, "中毒") / 100D);
        hs = (1 + (hs - kx) / 100);

//        BigDecimal hurt = ((jc.add(BigDecimal.valueOf(mydata.getsx(3, type)).multiply(BigDecimal.valueOf(hs).multiply(BigDecimal.valueOf(qf)).multiply(BigDecimal.valueOf(five))c)))));
//        BigDecimal hurt = BigDecimal.valueOf((jichu.doubleValue() + mydata.getsx(3, type)) * hs * qf * five);
        BigDecimal hurt = jichu.add(BigDecimal.valueOf(mydata.getsx(3, "中毒"))).multiply(BigDecimal.valueOf(hs * qf * five));
//        if (wg != 0) {
//            hurt = hurt.add(BigDecimal.valueOf(wg * five));
//        }
//        if (hurt > 213999999900L) {
//            hurt = 1;
//            WriteOut.addtxt("伤害上限:" + mydata.getType() + ":" + mydata.getManname() + ":" + mydata.getId(), 9999);
//        }
        boolean skll1 = false;
        List<FightingSkill> mandtaskill;
        mandtaskill = mydata.getSkills();
        for (int i = mandtaskill.size() - 1; i >= 0; i--) {

            if (mandtaskill.get(i).getSkillid() == 8048) {
                skll1 = true;
            }
        }
        if (skll1) { // 75-130%
            hurt = hurt.multiply(BigDecimal.valueOf(0.75 + Battlefield.random.nextInt(50) / 100.0));
        }
        return Arith.bigDecimalCompareD(hurt, BigDecimal.ONE) ? hurt.setScale(0, BigDecimal.ROUND_HALF_UP) : BigDecimal.ONE;
    }

//        qds += mydata.getQuality().getQzds();
//        double five = FiveLineSystem.getSwing(mydata, nomyadata);
//        if (five == 1) {
//            five = (1 + mydata.getsx(3, "无") / 100);
//        }
////        return (int) (nomyadata.getHp_z() * (skill.getSkillhurt() / 100D + qds / 10D) * five);
//        double v = (skill.getSkillhurt() / 100D + qds / 10D) * five;
//        return (nomyadata.getHp_z().multiply(BigDecimal.valueOf(v)));
}
