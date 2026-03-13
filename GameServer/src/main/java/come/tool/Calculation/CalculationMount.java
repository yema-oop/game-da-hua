package come.tool.Calculation;

import come.tool.FightingData.GetqualityUntil;
import come.tool.FightingData.Ql;
import org.come.entity.Mount;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CalculationMount {
	/** 坐骑技能效果系数 */
	public static double[] xishu = { 0.3, 0.3, 0.7, 0.7, 0, 0, 10000, 10000, 1.2 };
	public static double[][] zuoqi = { { 4.115226337, 1.141552511 }, { 14.40329218, 3.99543379 }, { 4.8, 1.333333333 },
			{ 14.4, 4 }, { 14.4, 4 }, { 3.6, 1 }, { 7.2, 2 }, { 4.8, 1.333333333 }, { 7.2, 2 }, { 4.8, 1.333333333 },
			{ 7.2, 2 }, { 4.8, 1.333333333 }, { 7.2, 2 }, { 7.2, 2 }, { 4.8, 1.333333333 }, { 7.2, 2 }, { 14.4, 4 },
			{ 4.8, 1.333333333 }, { 7.2, 2 }, { 4.8, 1.333333333 }, { 3.6, 1 }, { 14.4, 4 }, { 7.2, 2 } };
	/**计算坐骑技能加成的方法*/
	public static String calculateAddition(Mount mount, String skillname,Ql ql) {
		if (skillname.equals("夺命追魂")) {
			double xs=returnsCalculation("连击率", mount, skillname);
			GetqualityUntil.AddR(ql, "连击率", xs);
			GetqualityUntil.AddR(ql, "致命",   xs);
			GetqualityUntil.AddR(ql, "命中",   xs);
			return "SP="+(returnsCalculation("SP", mount, skillname)/100);
		} else if (skillname.equals("天雷怒火")) {
			double xs1=returnsCalculation("火法伤害", mount, skillname)*100;
			GetqualityUntil.AddR(ql, "火法伤害",   xs1);
			GetqualityUntil.AddR(ql, "雷法伤害",   xs1);
			double xs2=returnsCalculation("抗火", mount, skillname);
			GetqualityUntil.AddR(ql, "抗火",   xs2);
			GetqualityUntil.AddR(ql, "抗雷",   xs2);
			GetqualityUntil.AddR(ql, "抗鬼火", xs2);
			return "MP="+(returnsCalculation("MP", mount, skillname)/100);
		} else if (skillname.equals("金身不坏")) {
			GetqualityUntil.AddR(ql, "抗物理", returnsCalculation("抗物理", mount, skillname));
			GetqualityUntil.AddR(ql, "抗震慑", returnsCalculation("抗震慑", mount, skillname));
			GetqualityUntil.AddR(ql, "抗中毒", returnsCalculation("抗中毒", mount, skillname));
			GetqualityUntil.AddR(ql, "抗三尸", returnsCalculation("抗三尸虫", mount, skillname));
			return "HP="+(returnsCalculation("HP", mount, skillname)/100);
		} else if (skillname.equals("破釜沉舟")) {
			GetqualityUntil.AddR(ql, "狂暴率", returnsCalculation("狂暴", mount, skillname));
			double xs=returnsCalculation("忽视防御几率", mount, skillname);
			GetqualityUntil.AddR(ql, "忽视防御几率", xs);
			GetqualityUntil.AddR(ql, "忽视防御程度", xs);
			return "AP="+(returnsCalculation("AP", mount, skillname)/100);
		} else if (skillname.equals("兴风作浪")) {
			double xs1=returnsCalculation("风法伤害", mount, skillname)*100;
			GetqualityUntil.AddR(ql, "风法伤害",   xs1);
			GetqualityUntil.AddR(ql, "水法伤害",   xs1);
			double xs2=returnsCalculation("抗风", mount, skillname);
			GetqualityUntil.AddR(ql, "抗风",   xs2);
			GetqualityUntil.AddR(ql, "抗水",   xs2);
			GetqualityUntil.AddR(ql, "抗鬼火", xs2);
			return "MP="+(returnsCalculation("MP", mount, skillname)/100);
		} else if (skillname.equals("天神护体")) {
			double xs=returnsCalculation("抗风", mount, skillname);
			GetqualityUntil.AddR(ql, "抗风",   xs);
			GetqualityUntil.AddR(ql, "抗火",   xs);
			GetqualityUntil.AddR(ql, "抗水",   xs);
			GetqualityUntil.AddR(ql, "抗雷",   xs);
			GetqualityUntil.AddR(ql, "抗鬼火", xs);
			return "SP="+(returnsCalculation("SP", mount, skillname)/100);
		} else if (skillname.equals("后发制人")) {
			GetqualityUntil.AddR(ql, "狂暴率", returnsCalculation("狂暴", mount, skillname));
			return "HP="+(returnsCalculation("HP", mount, skillname)/100);
		} else if (skillname.equals("万劫不复")) {
			double xs=returnsCalculation("加强风", mount, skillname);
			GetqualityUntil.AddR(ql, "加强风",   xs);
			GetqualityUntil.AddR(ql, "加强火",   xs);
			GetqualityUntil.AddR(ql, "加强水",   xs);
			GetqualityUntil.AddR(ql, "加强雷",   xs);
			return "MP="+(returnsCalculation("MP", mount, skillname)/100);
		} else if (skillname.equals("心如止水")) {
			double xs=returnsCalculation("抗昏睡", mount, skillname);
			GetqualityUntil.AddR(ql, "抗昏睡",   xs);
			GetqualityUntil.AddR(ql, "抗封印",   xs);
			GetqualityUntil.AddR(ql, "抗中毒",   xs);
			GetqualityUntil.AddR(ql, "抗混乱",   xs);
			GetqualityUntil.AddR(ql, "抗遗忘",   xs);
			return "HP="+(returnsCalculation("HP", mount, skillname)/100);
		}else if (skillname.equals("山外青山")) {
			double xs = returnsCalculation("增加强克效果", mount, "山外青山");
			GetqualityUntil.AddR(ql, "增加强克效果",   xs);
			return "SP="+(returnsCalculation("SP", mount, skillname)/100);
		} else if (skillname.equals("游刃有余")) {
			double xs=returnsCalculation("抗灵宝伤害", mount, skillname);
			GetqualityUntil.AddR(ql, "抗灵宝伤害",   xs);
			GetqualityUntil.AddR(ql, "法术躲闪",   xs);
			return "SP="+(returnsCalculation("SP", mount, skillname)/100);
		}else if (skillname.equals("反客为主")) {
			double xs=returnsCalculation("反击率", mount, skillname);
			GetqualityUntil.AddR(ql, "反击率",   xs);
			double xs1=returnsCalculation("忽视防御程度", mount, skillname);
			GetqualityUntil.AddR(ql, "忽视防御程度",   xs1);
			GetqualityUntil.AddR(ql, "忽视防御几率",   xs1);
			return "AP="+(returnsCalculation("AP", mount, skillname)/100);
		}else if (skillname.equals("反治其身")) {
			double xs=returnsCalculation("躲闪率", mount, skillname);
			GetqualityUntil.AddR(ql, "躲闪率",   xs);
			double xs1=returnsCalculation("反震率", mount, skillname);
			GetqualityUntil.AddR(ql, "反震率",   xs1);
			GetqualityUntil.AddR(ql, "反震程度",   xs1);
			return "HP="+(returnsCalculation("HP", mount, skillname)/100);
		}else if (skillname.equals("视险如夷")) {
			double xs=returnsCalculation("抵御强克效果", mount, skillname);
			GetqualityUntil.AddR(ql, "抵御强克效果",   xs);
			return "HP="+(returnsCalculation("HP", mount, skillname)/100);
		}else if (skillname.equals("得心应手")) {
			double xs=returnsCalculation("忽视抗风", mount, skillname);
			GetqualityUntil.AddR(ql, "忽视抗风",   xs);
			GetqualityUntil.AddR(ql, "忽视抗火",   xs);
			GetqualityUntil.AddR(ql, "忽视抗雷",   xs);
			GetqualityUntil.AddR(ql, "忽视抗水",   xs);
			GetqualityUntil.AddR(ql, "忽视抗鬼火",   xs);
			GetqualityUntil.AddR(ql, "风法狂暴",   xs);
			GetqualityUntil.AddR(ql, "火法狂暴",   xs);
			GetqualityUntil.AddR(ql, "雷法狂暴",   xs);
			GetqualityUntil.AddR(ql, "水法狂暴",   xs);
			GetqualityUntil.AddR(ql, "鬼火狂暴",   xs);
			return "MP="+(returnsCalculation("MP", mount, skillname)/100);
		}
		return null;
	}
	/**
	 * 计算技能效果的方法
	 */
	public static double returnsCalculation(String mes, Mount mount, String skillname) {
		BigDecimal zjxz = BigDecimal.ONE;
		if (mount.getMountid() == 2 || mount.getMountid() == 4) {
			zjxz = new BigDecimal("1.2");
		}
		int grade = mount.getMountlvl();// 等级
		if (grade > 100) {
			grade -= 100;
		}
		BigDecimal gradeDecimal = BigDecimal.valueOf(grade);
		// 最新的坐骑属性
		BigDecimal lingxing = mount.getSpri()
				.add(gradeDecimal.divide(BigDecimal.TEN, 10, RoundingMode.HALF_UP)
						.multiply(mount.getSpri())
						.divide(BigDecimal.valueOf(2), 10, RoundingMode.HALF_UP))
				.setScale(0, RoundingMode.FLOOR);
		BigDecimal liliang = mount.getPower()
				.add(gradeDecimal.divide(BigDecimal.TEN, 10, RoundingMode.HALF_UP)
						.multiply(mount.getPower())
						.divide(BigDecimal.valueOf(2), 10, RoundingMode.HALF_UP))
				.setScale(0, RoundingMode.FLOOR);
		BigDecimal genggu = mount.getBone()
				.add(gradeDecimal.divide(BigDecimal.TEN, 10, RoundingMode.HALF_UP)
						.multiply(mount.getBone())
						.divide(BigDecimal.valueOf(2), 10, RoundingMode.HALF_UP))
				.setScale(0, RoundingMode.FLOOR);
		int shulian = mount.getProficiency();// 熟练度
		// 计算出来的技能效果值
		BigDecimal jnxgz = BigDecimal.ZERO;
		if (mes.equals("HP")) {
			if (skillname.equals("金身不坏")) {
				jnxgz = genggu.multiply(BigDecimal.valueOf(xishu[2]))
						.add(lingxing.multiply(BigDecimal.valueOf(xishu[4])))
						.add(liliang.multiply(BigDecimal.valueOf(xishu[1])))
						.multiply(zjxz)
						.divide(BigDecimal.valueOf(zuoqi[4][0]), 10, RoundingMode.HALF_UP)
						.add(BigDecimal.valueOf(shulian)
								.divide(BigDecimal.valueOf(10000), 10, RoundingMode.HALF_UP)
								.divide(BigDecimal.valueOf(zuoqi[4][1]), 10, RoundingMode.HALF_UP));
			} else if (skillname.equals("后发制人")) {
				jnxgz = genggu.multiply(BigDecimal.valueOf(xishu[0]))
						.add(lingxing.multiply(BigDecimal.valueOf(xishu[4])))
						.add(liliang.multiply(BigDecimal.valueOf(xishu[3])))
						.multiply(zjxz)
						.divide(BigDecimal.valueOf(zuoqi[16][0]), 10, RoundingMode.HALF_UP)
						.add(BigDecimal.valueOf(shulian)
								.divide(BigDecimal.valueOf(xishu[7]), 10, RoundingMode.HALF_UP)
								.divide(BigDecimal.valueOf(zuoqi[16][1]), 10, RoundingMode.HALF_UP));
			} else if (skillname.equals("心如止水")) {
				jnxgz = genggu.multiply(BigDecimal.valueOf(xishu[2]))
						.add(lingxing.multiply(BigDecimal.valueOf(xishu[1])))
						.add(liliang.multiply(BigDecimal.valueOf(xishu[5])))
						.multiply(zjxz)
						.divide(BigDecimal.valueOf(zuoqi[1][0]), 10, RoundingMode.HALF_UP)
						.add(BigDecimal.valueOf(shulian)
								.divide(BigDecimal.valueOf(xishu[6]), 10, RoundingMode.HALF_UP)
								.divide(BigDecimal.valueOf(zuoqi[1][1]), 10, RoundingMode.HALF_UP));
			} else if (skillname.equals("反治其身")) {
				jnxgz = genggu.multiply(BigDecimal.valueOf(xishu[2]))
						.add(lingxing.multiply(BigDecimal.valueOf(xishu[1])))
						.add(liliang.multiply(BigDecimal.valueOf(xishu[5])))
						.multiply(zjxz)
						.divide(BigDecimal.valueOf(zuoqi[1][0]), 10, RoundingMode.HALF_UP)
						.add(BigDecimal.valueOf(shulian)
								.divide(BigDecimal.valueOf(xishu[7]), 10, RoundingMode.HALF_UP)
								.divide(BigDecimal.valueOf(zuoqi[1][1]), 10, RoundingMode.HALF_UP));
			} else if (skillname.equals("视险如夷")) {
				jnxgz = genggu.multiply(BigDecimal.valueOf(xishu[2]))
						.add(lingxing.multiply(BigDecimal.valueOf(xishu[1])))
						.add(liliang.multiply(BigDecimal.valueOf(xishu[5])))
						.multiply(zjxz)
						.divide(BigDecimal.valueOf(zuoqi[1][0]), 10, RoundingMode.HALF_UP)
						.add(BigDecimal.valueOf(shulian)
								.divide(BigDecimal.valueOf(xishu[7]), 10, RoundingMode.HALF_UP)
								.divide(BigDecimal.valueOf(zuoqi[1][1]), 10, RoundingMode.HALF_UP));
			}
		} else if (mes.equals("MP")) {
			if (skillname.equals("天雷怒火")) {
				jnxgz = genggu.multiply(BigDecimal.valueOf(xishu[4]))
						.add(lingxing.multiply(BigDecimal.valueOf(xishu[3])))
						.add(liliang.multiply(BigDecimal.valueOf(xishu[0])))
						.multiply(zjxz)
						.divide(BigDecimal.valueOf(zuoqi[13][0]), 10, RoundingMode.HALF_UP)
						.add(BigDecimal.valueOf(shulian)
								.divide(BigDecimal.valueOf(xishu[6]), 10, RoundingMode.HALF_UP)
								.divide(BigDecimal.valueOf(zuoqi[13][1]), 10, RoundingMode.HALF_UP));
			} else if (skillname.equals("兴风作浪")) {
				jnxgz = genggu.multiply(BigDecimal.valueOf(xishu[1]))
						.add(lingxing.multiply(BigDecimal.valueOf(xishu[2])))
						.add(liliang.multiply(BigDecimal.valueOf(xishu[5])))
						.multiply(zjxz)
						.divide(BigDecimal.valueOf(zuoqi[10][0]), 10, RoundingMode.HALF_UP)
						.add(BigDecimal.valueOf(shulian)
								.divide(BigDecimal.valueOf(xishu[6]), 10, RoundingMode.HALF_UP)
								.divide(BigDecimal.valueOf(zuoqi[10][1]), 10, RoundingMode.HALF_UP));
			} else if (skillname.equals("万劫不复")) {
				jnxgz = genggu.multiply(BigDecimal.valueOf(xishu[5]))
						.add(lingxing)
						.add(liliang.multiply(BigDecimal.valueOf(xishu[4])))
						.multiply(zjxz)
						.divide(BigDecimal.valueOf(zuoqi[8][0]), 10, RoundingMode.HALF_UP)
						.add(BigDecimal.valueOf(shulian)
								.divide(BigDecimal.valueOf(xishu[6]), 10, RoundingMode.HALF_UP)
								.divide(BigDecimal.valueOf(zuoqi[8][1]), 10, RoundingMode.HALF_UP));
			}else if (skillname.equals("得心应手")) {
				jnxgz = genggu
						.add(lingxing.multiply(BigDecimal.valueOf(xishu[4])))
						.add(liliang.multiply(BigDecimal.valueOf(xishu[4])))
						.multiply(zjxz)
						.divide(BigDecimal.valueOf(zuoqi[3][0]), 10, RoundingMode.HALF_UP)
						.add(BigDecimal.valueOf(shulian)
								.divide(BigDecimal.valueOf(xishu[6]), 10, RoundingMode.HALF_UP)
								.divide(BigDecimal.valueOf(zuoqi[3][1]), 10, RoundingMode.HALF_UP));
			}
		} else if (mes.equals("AP")) {
			jnxgz = genggu.multiply(BigDecimal.valueOf(xishu[5]))
					.add(lingxing.multiply(BigDecimal.valueOf(xishu[4])))
					.add(liliang)
					.multiply(zjxz)
					.divide(BigDecimal.valueOf(zuoqi[18][0]), 10, RoundingMode.HALF_UP)
					.add(BigDecimal.valueOf(shulian)
							.divide(BigDecimal.valueOf(xishu[7]), 10, RoundingMode.HALF_UP)
							.divide(BigDecimal.valueOf(zuoqi[18][1]), 10, RoundingMode.HALF_UP));
		} else if (mes.equals("SP")) {
			if (skillname.equals("夺命追魂")) {
				jnxgz = genggu.multiply(BigDecimal.valueOf(xishu[4]))
						.add(lingxing.multiply(BigDecimal.valueOf(xishu[0])))
						.add(liliang.multiply(BigDecimal.valueOf(xishu[3])))
						.multiply(zjxz)
						.divide(BigDecimal.valueOf(zuoqi[21][0]), 10, RoundingMode.HALF_UP)
						.add(BigDecimal.valueOf(shulian)
								.divide(BigDecimal.valueOf(xishu[6]), 10, RoundingMode.HALF_UP)
								.divide(BigDecimal.valueOf(zuoqi[21][1]), 10, RoundingMode.HALF_UP));
			} else if (skillname.equals("天神护体")) {
				jnxgz = genggu
						.add(lingxing.multiply(BigDecimal.valueOf(xishu[4])))
						.add(liliang.multiply(BigDecimal.valueOf(xishu[4])))
						.multiply(zjxz)
						.divide(BigDecimal.valueOf(zuoqi[3][0]), 10, RoundingMode.HALF_UP)
						.add(BigDecimal.valueOf(shulian)
								.divide(BigDecimal.valueOf(xishu[6]), 10, RoundingMode.HALF_UP)
								.divide(BigDecimal.valueOf(zuoqi[3][1]), 10, RoundingMode.HALF_UP));
			} else if (skillname.equals("游刃有余")) {
				jnxgz = genggu
						.add(lingxing.multiply(BigDecimal.valueOf(xishu[4])))
						.add(liliang.multiply(BigDecimal.valueOf(xishu[4])))
						.multiply(zjxz)
						.divide(BigDecimal.valueOf(zuoqi[3][0]), 10, RoundingMode.HALF_UP)
						.add(BigDecimal.valueOf(shulian)
								.divide(BigDecimal.valueOf(xishu[6]), 10, RoundingMode.HALF_UP)
								.divide(BigDecimal.valueOf(zuoqi[3][1]), 10, RoundingMode.HALF_UP));
			}else if (skillname.equals("山外青山")) {
				jnxgz = genggu.multiply(BigDecimal.valueOf(xishu[5]))
						.add(lingxing)
						.add(liliang.multiply(BigDecimal.valueOf(xishu[4])))
						.multiply(zjxz)
						.divide(BigDecimal.valueOf(zuoqi[8][0]), 10, RoundingMode.HALF_UP)
						.add(BigDecimal.valueOf(shulian)
								.divide(BigDecimal.valueOf(xishu[6]), 10, RoundingMode.HALF_UP)
								.divide(BigDecimal.valueOf(zuoqi[8][1]), 10, RoundingMode.HALF_UP));
			}
		} else if (mes.equals("连击率") || mes.equals("致命") || mes.equals("命中")|| mes.equals("躲闪率")|| mes.equals("反击率")) {
			jnxgz = genggu.multiply(BigDecimal.valueOf(xishu[5]))
					.add(lingxing.multiply(BigDecimal.valueOf(xishu[1])))
					.add(liliang.multiply(BigDecimal.valueOf(xishu[2])))
					.multiply(zjxz)
					.divide(BigDecimal.valueOf(zuoqi[22][0]), 10, RoundingMode.HALF_UP)
					.add(BigDecimal.valueOf(shulian)
							.divide(BigDecimal.valueOf(10000), 10, RoundingMode.HALF_UP)
							.divide(BigDecimal.valueOf(zuoqi[22][1]), 10, RoundingMode.HALF_UP));
		} else if (mes.equals("抗风") || mes.equals("抗火") || mes.equals("抗水") || mes.equals("抗雷")||mes.equals("抗鬼火")) {
			if (skillname.equals("天神护体")) {
				jnxgz = genggu
						.add(lingxing.multiply(BigDecimal.valueOf(xishu[4])))
						.add(liliang.multiply(BigDecimal.valueOf(xishu[4])))
						.multiply(zjxz)
						.divide(BigDecimal.valueOf(zuoqi[2][0]), 10, RoundingMode.HALF_UP)
						.add(BigDecimal.valueOf(shulian)
								.divide(BigDecimal.valueOf(xishu[6]), 10, RoundingMode.HALF_UP)
								.divide(BigDecimal.valueOf(zuoqi[2][1]), 10, RoundingMode.HALF_UP));
			} else if (skillname.equals("兴风作浪")) {
				jnxgz = genggu.multiply(BigDecimal.valueOf(xishu[1]))
						.add(lingxing.multiply(BigDecimal.valueOf(xishu[2])))
						.add(liliang.multiply(BigDecimal.valueOf(xishu[5])))
						.multiply(zjxz)
						.divide(BigDecimal.valueOf(zuoqi[12][0]), 10, RoundingMode.HALF_UP)
						.add(BigDecimal.valueOf(shulian)
								.divide(BigDecimal.valueOf(xishu[6]), 10, RoundingMode.HALF_UP)
								.divide(BigDecimal.valueOf(zuoqi[12][1]), 10, RoundingMode.HALF_UP));
			} else if (skillname.equals("天雷怒火")) {
				jnxgz = genggu.multiply(BigDecimal.valueOf(xishu[4]))
						.add(lingxing.multiply(BigDecimal.valueOf(xishu[2])))
						.add(liliang.multiply(BigDecimal.valueOf(xishu[1])))
						.multiply(zjxz)
						.divide(BigDecimal.valueOf(zuoqi[15][0]), 10, RoundingMode.HALF_UP)
						.add(BigDecimal.valueOf(shulian)
								.divide(BigDecimal.valueOf(xishu[6]), 10, RoundingMode.HALF_UP)
								.divide(BigDecimal.valueOf(zuoqi[15][1]), 10, RoundingMode.HALF_UP));
			}
		} else if (mes.equals("火法伤害") || mes.equals("雷法伤害") || mes.equals("火雷伤害")) {
			jnxgz = genggu.multiply(BigDecimal.valueOf(xishu[5]))
					.add(lingxing.multiply(BigDecimal.valueOf(xishu[3])))
					.add(liliang.multiply(BigDecimal.valueOf(xishu[0])))
					.multiply(zjxz)
					.divide(BigDecimal.valueOf(zuoqi[14][0]), 10, RoundingMode.HALF_UP)
					.add(BigDecimal.valueOf(shulian)
							.divide(BigDecimal.valueOf(10000), 10, RoundingMode.HALF_UP)
							.divide(BigDecimal.valueOf(zuoqi[14][1]), 10, RoundingMode.HALF_UP));
		}  else if (mes.equals("反震率") || mes.equals("反震程度") ) {
			jnxgz = genggu.multiply(BigDecimal.valueOf(xishu[3]))
					.add(lingxing.multiply(BigDecimal.valueOf(xishu[1])))
					.add(liliang.multiply(BigDecimal.valueOf(xishu[2])))
					.multiply(zjxz)
					.divide(BigDecimal.valueOf(zuoqi[22][0]), 10, RoundingMode.HALF_UP)
					.add(BigDecimal.valueOf(shulian)
							.divide(BigDecimal.valueOf(10000), 10, RoundingMode.HALF_UP)
							.divide(BigDecimal.valueOf(zuoqi[22][1]), 10, RoundingMode.HALF_UP));
		}else if (mes.equals("抗火雷")) {
			jnxgz = genggu.multiply(BigDecimal.valueOf(xishu[4]))
					.add(lingxing.multiply(BigDecimal.valueOf(xishu[2])))
					.add(liliang.multiply(BigDecimal.valueOf(xishu[1])))
					.multiply(zjxz)
					.divide(BigDecimal.valueOf(zuoqi[15][0]), 10, RoundingMode.HALF_UP)
					.add(BigDecimal.valueOf(shulian)
							.divide(BigDecimal.valueOf(xishu[6]), 10, RoundingMode.HALF_UP)
							.divide(BigDecimal.valueOf(zuoqi[15][1]), 10, RoundingMode.HALF_UP));
		} else if (mes.equals("抗物理")) {
			jnxgz = genggu.multiply(BigDecimal.valueOf(xishu[3]))
					.add(lingxing.multiply(BigDecimal.valueOf(xishu[5])))
					.add(liliang.multiply(BigDecimal.valueOf(xishu[0])))
					.multiply(zjxz)
					.divide(BigDecimal.valueOf(zuoqi[5][0]), 10, RoundingMode.HALF_UP)
					.add(BigDecimal.valueOf(shulian)
							.divide(BigDecimal.valueOf(xishu[6]), 10, RoundingMode.HALF_UP)
							.divide(BigDecimal.valueOf(zuoqi[5][1]), 10, RoundingMode.HALF_UP));
		} else if (mes.equals("抗震慑")) {
			jnxgz = genggu.multiply(BigDecimal.valueOf(xishu[3]))
					.add(lingxing.multiply(BigDecimal.valueOf(xishu[4])))
					.add(liliang.multiply(BigDecimal.valueOf(xishu[1])))
					.multiply(zjxz)
					.divide(BigDecimal.valueOf(zuoqi[6][0]), 10, RoundingMode.HALF_UP)
					.add(BigDecimal.valueOf(shulian)
							.divide(BigDecimal.valueOf(xishu[6]), 10, RoundingMode.HALF_UP)
							.divide(BigDecimal.valueOf(zuoqi[6][1]), 10, RoundingMode.HALF_UP));
		} else if (mes.equals("抗中毒")) {
			if (skillname.equals("金身不坏")) {
				jnxgz = genggu.multiply(BigDecimal.valueOf(xishu[2]))
						.add(lingxing.multiply(BigDecimal.valueOf(xishu[4])))
						.add(liliang.multiply(BigDecimal.valueOf(xishu[0])))
						.multiply(zjxz)
						.divide(BigDecimal.valueOf(zuoqi[7][0]), 10, RoundingMode.HALF_UP)
						.add(BigDecimal.valueOf(shulian)
								.divide(BigDecimal.valueOf(10000), 10, RoundingMode.HALF_UP)
								.divide(BigDecimal.valueOf(zuoqi[7][1]), 10, RoundingMode.HALF_UP));
			} else if (skillname.equals("心如止水")) {
				jnxgz = genggu.multiply(BigDecimal.valueOf(xishu[3]))
						.add(lingxing.multiply(BigDecimal.valueOf(xishu[0])))
						.add(liliang.multiply(BigDecimal.valueOf(xishu[4])))
						.multiply(zjxz)
						.divide(BigDecimal.valueOf(zuoqi[0][0]), 10, RoundingMode.HALF_UP)
						.add(BigDecimal.valueOf(shulian)
								.divide(BigDecimal.valueOf(xishu[7]), 10, RoundingMode.HALF_UP)
								.divide(BigDecimal.valueOf(zuoqi[0][1]), 10, RoundingMode.HALF_UP));
			}
		} else if (mes.equals("抗三尸虫")) {
			jnxgz = genggu.multiply(BigDecimal.valueOf(xishu[2]))
					.add(lingxing.multiply(BigDecimal.valueOf(xishu[4])))
					.add(liliang.multiply(BigDecimal.valueOf(xishu[0])))
					.multiply(BigDecimal.valueOf(125))
					.divide(BigDecimal.valueOf(3), 10, RoundingMode.HALF_UP)
					.add(BigDecimal.valueOf(shulian)
							.multiply(BigDecimal.valueOf(1500))
							.divide(BigDecimal.valueOf(100000), 10, RoundingMode.HALF_UP));
		} else if (mes.equals("狂暴")) {
			if (skillname.equals("破釜沉舟")) {
				jnxgz = genggu.multiply(BigDecimal.valueOf(xishu[4]))
						.add(lingxing.multiply(BigDecimal.valueOf(xishu[4])))
						.add(liliang)
						.multiply(zjxz)
						.divide(BigDecimal.valueOf(zuoqi[19][0]), 10, RoundingMode.HALF_UP)
						.add(BigDecimal.valueOf(shulian)
								.divide(BigDecimal.valueOf(xishu[6]), 10, RoundingMode.HALF_UP)
								.divide(BigDecimal.valueOf(zuoqi[19][1]), 10, RoundingMode.HALF_UP));
			} else if (skillname.equals("后发制人")) {
				jnxgz = genggu.multiply(BigDecimal.valueOf(xishu[1]))
						.add(lingxing.multiply(BigDecimal.valueOf(xishu[5])))
						.add(liliang.multiply(BigDecimal.valueOf(xishu[3])))
						.multiply(zjxz)
						.divide(BigDecimal.valueOf(zuoqi[17][0]), 10, RoundingMode.HALF_UP)
						.add(BigDecimal.valueOf(shulian)
								.divide(BigDecimal.valueOf(xishu[6]), 10, RoundingMode.HALF_UP)
								.divide(BigDecimal.valueOf(zuoqi[17][1]), 10, RoundingMode.HALF_UP));
			}
		} else if (mes.equals("忽视防御几率") || mes.equals("忽视防御程度")) {
			jnxgz = genggu.multiply(BigDecimal.valueOf(xishu[4]))
					.add(lingxing.multiply(BigDecimal.valueOf(xishu[5])))
					.add(liliang)
					.multiply(zjxz)
					.divide(BigDecimal.valueOf(zuoqi[20][0]), 10, RoundingMode.HALF_UP)
					.add(BigDecimal.valueOf(shulian)
							.divide(BigDecimal.valueOf(xishu[6]), 10, RoundingMode.HALF_UP)
							.divide(BigDecimal.valueOf(zuoqi[20][1]), 10, RoundingMode.HALF_UP));
		} else if (mes.equals("风法伤害") || mes.equals("水法伤害")) {
			jnxgz = genggu.multiply(BigDecimal.valueOf(xishu[0]))
					.add(lingxing.multiply(BigDecimal.valueOf(xishu[3])))
					.add(liliang.multiply(BigDecimal.valueOf(xishu[5])))
					.multiply(zjxz)
					.divide(BigDecimal.valueOf(zuoqi[11][0]), 10, RoundingMode.HALF_UP)
					.add(BigDecimal.valueOf(shulian)
							.divide(BigDecimal.valueOf(xishu[6]), 10, RoundingMode.HALF_UP)
							.divide(BigDecimal.valueOf(zuoqi[11][1]), 10, RoundingMode.HALF_UP));
		} else if (mes.equals("加强风") || mes.equals("加强火") || mes.equals("加强水") || mes.equals("加强雷")) {
			jnxgz = genggu.multiply(BigDecimal.valueOf(xishu[5]))
					.add(lingxing)
					.add(liliang.multiply(BigDecimal.valueOf(xishu[5])))
					.multiply(zjxz)
					.divide(BigDecimal.valueOf(zuoqi[9][0]), 10, RoundingMode.HALF_UP)
					.add(BigDecimal.valueOf(shulian)
							.divide(BigDecimal.valueOf(xishu[6]), 10, RoundingMode.HALF_UP)
							.divide(BigDecimal.valueOf(zuoqi[9][1]), 10, RoundingMode.HALF_UP));
		} else if (mes.equals("抗昏睡") || mes.equals("抗封印") || mes.equals("抗混乱") || mes.equals("抗遗忘")) {
			jnxgz = genggu.multiply(BigDecimal.valueOf(xishu[3]))
					.add(lingxing.multiply(BigDecimal.valueOf(xishu[0])))
					.add(liliang.multiply(BigDecimal.valueOf(xishu[4])))
					.multiply(zjxz)
					.divide(BigDecimal.valueOf(zuoqi[0][0]), 10, RoundingMode.HALF_UP)
					.add(BigDecimal.valueOf(shulian)
							.divide(BigDecimal.valueOf(xishu[7]), 10, RoundingMode.HALF_UP)
							.divide(BigDecimal.valueOf(zuoqi[0][1]), 10, RoundingMode.HALF_UP));
		}else if (!mes.equals("反震率") && !mes.equals("反震程度")) {
			if (mes.equals("法术躲闪")) {
				jnxgz = genggu.multiply(BigDecimal.valueOf(xishu[3]))
						.add(lingxing.multiply(BigDecimal.valueOf(xishu[0])))
						.add(liliang.multiply(BigDecimal.valueOf(xishu[4])))
						.multiply(zjxz)
						.divide(BigDecimal.valueOf(zuoqi[0][0]), 10, RoundingMode.HALF_UP)
						.add(BigDecimal.valueOf(shulian)
								.divide(BigDecimal.valueOf(xishu[7]), 10, RoundingMode.HALF_UP)
								.divide(BigDecimal.valueOf(zuoqi[0][1]), 10, RoundingMode.HALF_UP));
			} else if (mes.equals("抵御强克效果")) {
				jnxgz = genggu.multiply(BigDecimal.valueOf(xishu[3]))
						.add(lingxing.multiply(BigDecimal.valueOf(xishu[0])))
						.add(liliang.multiply(BigDecimal.valueOf(xishu[4])))
						.multiply(zjxz)
						.divide(BigDecimal.valueOf(zuoqi[0][0]), 10, RoundingMode.HALF_UP)
						.add(BigDecimal.valueOf(shulian)
								.divide(BigDecimal.valueOf(xishu[7]), 10, RoundingMode.HALF_UP)
								.divide(BigDecimal.valueOf(zuoqi[0][1]), 10, RoundingMode.HALF_UP));
			} else if (mes.equals("抗灵宝伤害")) {
				jnxgz = genggu.multiply(BigDecimal.valueOf(xishu[3]))
						.add(lingxing.multiply(BigDecimal.valueOf(xishu[0])))
						.add(liliang.multiply(BigDecimal.valueOf(xishu[4])))
						.multiply(zjxz)
						.divide(BigDecimal.valueOf(zuoqi[0][0]), 10, RoundingMode.HALF_UP)
						.add(BigDecimal.valueOf(shulian)
								.divide(BigDecimal.valueOf(xishu[7]), 10, RoundingMode.HALF_UP)
								.divide(BigDecimal.valueOf(zuoqi[0][1]), 10, RoundingMode.HALF_UP));
			} else if (mes.equals("增加强克效果")) {
				jnxgz = genggu.multiply(BigDecimal.valueOf(xishu[3]))
						.add(lingxing.multiply(BigDecimal.valueOf(xishu[0])))
						.add(liliang.multiply(BigDecimal.valueOf(xishu[4])))
						.multiply(zjxz)
						.divide(BigDecimal.valueOf(zuoqi[0][0]), 10, RoundingMode.HALF_UP)
						.add(BigDecimal.valueOf(shulian)
								.divide(BigDecimal.valueOf(xishu[7]), 10, RoundingMode.HALF_UP)
								.divide(BigDecimal.valueOf(zuoqi[0][1]), 10, RoundingMode.HALF_UP));
			} else if (mes.equals("忽视抗水")) {
				jnxgz = genggu.multiply(BigDecimal.valueOf(xishu[3]))
						.add(lingxing.multiply(BigDecimal.valueOf(xishu[0])))
						.add(liliang.multiply(BigDecimal.valueOf(xishu[4])))
						.multiply(zjxz)
						.divide(BigDecimal.valueOf(zuoqi[0][0]), 10, RoundingMode.HALF_UP)
						.add(BigDecimal.valueOf(shulian)
								.divide(BigDecimal.valueOf(xishu[7]), 10, RoundingMode.HALF_UP)
								.divide(BigDecimal.valueOf(zuoqi[0][1]), 10, RoundingMode.HALF_UP));
			} else if (mes.equals("忽视抗雷")) {
				jnxgz = genggu.multiply(BigDecimal.valueOf(xishu[3]))
						.add(lingxing.multiply(BigDecimal.valueOf(xishu[0])))
						.add(liliang.multiply(BigDecimal.valueOf(xishu[4])))
						.multiply(zjxz)
						.divide(BigDecimal.valueOf(zuoqi[0][0]), 10, RoundingMode.HALF_UP)
						.add(BigDecimal.valueOf(shulian)
								.divide(BigDecimal.valueOf(xishu[7]), 10, RoundingMode.HALF_UP)
								.divide(BigDecimal.valueOf(zuoqi[0][1]), 10, RoundingMode.HALF_UP));
			} else if (mes.equals("忽视抗风")) {
				jnxgz = genggu.multiply(BigDecimal.valueOf(xishu[3]))
						.add(lingxing.multiply(BigDecimal.valueOf(xishu[0])))
						.add(liliang.multiply(BigDecimal.valueOf(xishu[4])))
						.multiply(zjxz)
						.divide(BigDecimal.valueOf(zuoqi[0][0]), 10, RoundingMode.HALF_UP)
						.add(BigDecimal.valueOf(shulian)
								.divide(BigDecimal.valueOf(xishu[7]), 10, RoundingMode.HALF_UP)
								.divide(BigDecimal.valueOf(zuoqi[0][1]), 10, RoundingMode.HALF_UP));
			} else if (mes.equals("忽视抗火")) {
				jnxgz = genggu.multiply(BigDecimal.valueOf(xishu[3]))
						.add(lingxing.multiply(BigDecimal.valueOf(xishu[0])))
						.add(liliang.multiply(BigDecimal.valueOf(xishu[4])))
						.multiply(zjxz)
						.divide(BigDecimal.valueOf(zuoqi[0][0]), 10, RoundingMode.HALF_UP)
						.add(BigDecimal.valueOf(shulian)
								.divide(BigDecimal.valueOf(xishu[7]), 10, RoundingMode.HALF_UP)
								.divide(BigDecimal.valueOf(zuoqi[0][1]), 10, RoundingMode.HALF_UP));
			} else if (mes.equals("忽视鬼火")) {
				jnxgz = genggu.multiply(BigDecimal.valueOf(xishu[3]))
						.add(lingxing.multiply(BigDecimal.valueOf(xishu[0])))
						.add(liliang.multiply(BigDecimal.valueOf(xishu[4])))
						.multiply(zjxz)
						.divide(BigDecimal.valueOf(zuoqi[0][0]), 10, RoundingMode.HALF_UP)
						.add(BigDecimal.valueOf(shulian)
								.divide(BigDecimal.valueOf(xishu[7]), 10, RoundingMode.HALF_UP)
								.divide(BigDecimal.valueOf(zuoqi[0][1]), 10, RoundingMode.HALF_UP));
			} else if (mes.equals("鬼火狂暴")) {
				jnxgz = genggu.multiply(BigDecimal.valueOf(xishu[3]))
						.add(lingxing.multiply(BigDecimal.valueOf(xishu[0])))
						.add(liliang.multiply(BigDecimal.valueOf(xishu[4])))
						.multiply(zjxz)
						.divide(BigDecimal.valueOf(zuoqi[0][0]), 10, RoundingMode.HALF_UP)
						.add(BigDecimal.valueOf(shulian)
								.divide(BigDecimal.valueOf(xishu[7]), 10, RoundingMode.HALF_UP)
								.divide(BigDecimal.valueOf(zuoqi[0][1]), 10, RoundingMode.HALF_UP));
			} else if (mes.equals("水法狂暴")) {
				jnxgz = genggu.multiply(BigDecimal.valueOf(xishu[3]))
						.add(lingxing.multiply(BigDecimal.valueOf(xishu[0])))
						.add(liliang.multiply(BigDecimal.valueOf(xishu[4])))
						.multiply(zjxz)
						.divide(BigDecimal.valueOf(zuoqi[0][0]), 10, RoundingMode.HALF_UP)
						.add(BigDecimal.valueOf(shulian)
								.divide(BigDecimal.valueOf(xishu[7]), 10, RoundingMode.HALF_UP)
								.divide(BigDecimal.valueOf(zuoqi[0][1]), 10, RoundingMode.HALF_UP));
			} else if (mes.equals("雷法狂暴")) {
				jnxgz = genggu.multiply(BigDecimal.valueOf(xishu[3]))
						.add(lingxing.multiply(BigDecimal.valueOf(xishu[0])))
						.add(liliang.multiply(BigDecimal.valueOf(xishu[4])))
						.multiply(zjxz)
						.divide(BigDecimal.valueOf(zuoqi[0][0]), 10, RoundingMode.HALF_UP)
						.add(BigDecimal.valueOf(shulian)
								.divide(BigDecimal.valueOf(xishu[7]), 10, RoundingMode.HALF_UP)
								.divide(BigDecimal.valueOf(zuoqi[0][1]), 10, RoundingMode.HALF_UP));
			} else if (mes.equals("风法狂暴")) {
				jnxgz = genggu.multiply(BigDecimal.valueOf(xishu[3]))
						.add(lingxing.multiply(BigDecimal.valueOf(xishu[0])))
						.add(liliang.multiply(BigDecimal.valueOf(xishu[4])))
						.multiply(zjxz)
						.divide(BigDecimal.valueOf(zuoqi[0][0]), 10, RoundingMode.HALF_UP)
						.add(BigDecimal.valueOf(shulian)
								.divide(BigDecimal.valueOf(xishu[7]), 10, RoundingMode.HALF_UP)
								.divide(BigDecimal.valueOf(zuoqi[0][1]), 10, RoundingMode.HALF_UP));
			} else if (mes.equals("火法狂暴")) {
				jnxgz = genggu.multiply(BigDecimal.valueOf(xishu[3]))
						.add(lingxing.multiply(BigDecimal.valueOf(xishu[0])))
						.add(liliang.multiply(BigDecimal.valueOf(xishu[4])))
						.multiply(zjxz)
						.divide(BigDecimal.valueOf(zuoqi[0][0]), 10, RoundingMode.HALF_UP)
						.add(BigDecimal.valueOf(shulian)
								.divide(BigDecimal.valueOf(xishu[7]), 10, RoundingMode.HALF_UP)
								.divide(BigDecimal.valueOf(zuoqi[0][1]), 10, RoundingMode.HALF_UP));
			}
		}
		return jnxgz.doubleValue();
	}
	/**保留两位小数的方法*/
	public static double keepTwoDecimals(Double value) {
		BigDecimal b = new BigDecimal(value);
		return b.setScale(2, RoundingMode.HALF_UP).doubleValue();
	}
}
