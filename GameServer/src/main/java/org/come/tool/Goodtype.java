package org.come.tool;

public class Goodtype {
	/**
	 * 不需要对功能进行描述的类型
	 */
	public static boolean CancelMsg(long type) {
		if (type == 100 || type == 8 || type == 49 || type == 88 || type == 99
				|| type == 111 || type == 113 || type == 212 || type == 213
				|| type == 501 || type == 502 || type == 503 || type == 504
				|| type == 716 || type == 717 || type == 718 || type == 719
				|| type == 720 || type == 721 || type == 7005 || type == 118
				|| type == 190|| type == 112 || type == 1008) {
			return true;
		}
		return false;
	}
	/**判断是否是真护身符*/
	public static boolean Amulet2(long type) {
		if (type == 612) {return true;}
		return false;
	}
	/**判断是否是仙器礼盒*/
	public static boolean xianlihe(long type) {
		if (type == 7005) {
			return true;
		}
		return false;
	}
	/**判断是否是宝石*/
	public static boolean baoshi(long type) {
		if (type == 746||type == 749||type == 752||type == 755
			||type == 758||type == 761||type == 764||type == 767) {
			return true;
		}
		return false;
	}
	/**判断是否是强化宝石*/
	public static boolean QHbaoshi(long type) {
		if (type>=123&&type<=127) {
			return true;
		}
		return false;
	}
	/**判断该类型是否可以装备宝石*/
	public static boolean EquipGem(long type){
		return Weapons(type)||Helmet(type)||Necklace(type)||Clothes(type)||Shoes(type);
	}
	/**判断装备类型是否使用对应类型的强化石  type1 装备类型  type2 强化石类型*/
	public static boolean QHEquipGem(long type1,long type2){
		if (type2==123&&Weapons(type1)) {
			return true;
		}else if (type2==123&&Necklace(type1)) {
			return true;
		}else if (type2==123&&Shoes(type1)) {
			return true;
		}else if (type2==123&&Helmet(type1)) {return true;
		}else if (type2==123&&Clothes(type1)) {return true;
		}
		return false;
	}
	/**
	 * 判断是否是超级药丸
	 */
	public static boolean TimingGood(long type) {
		if (type == 493 || type == 492) {
			return true;
		}
		return false;
	}
	/**
	 * 判断是否是超级药丸月
	 */
   public  static boolean Medicine(long type){
		if (type == 921|| type == 922){
			return true;
		}
	   return false;
   }
	/**
	 * 判断是否是回蓝符
	 */
	public static boolean BlueBack(long type) {
		if (type == 494 || type == 495 || type == 496) {
			return true;
		}
		return false;
	}
	public static boolean YQBack(long type) {
		if (type == 2) {
			return true;
		}
		return false;
	}

	/**
	 * 判断是否是飞行旗
	 */
	public static boolean Flightchess(long type) {
		if (type == 2010 || type == 2011 || type == 2012) {
			return true;
		}
		return false;
	}

	/**
	 * 物品vlaue解析
	 */
	public static String[] StringParsing(String vlaue) {
		return vlaue.split("\\|");
	}

	/**
	 * 判断是否是钱 点卡 经验丹 亲密丹 技能熟练度
	 */
	public static boolean Consumption(long type) {
		if (type == 888 || type == 100 || type == 715 || type == 2041
				|| type == 2040 || type == 2042||type==935) {
			return true;
		}
		return false;
	}

	/**
	 * 判断是否是炼妖石
	 */
	public static boolean ExerciseMonsterOre(long type) {
		if (type >= 702 && type <= 711

		) {
			return true;
		}
		return false;
	}

	/**
	 * 判断是否是战斗使用的药品
	 */
	public static boolean FightingMedicine(long type) {
		if (type == 0) {
			return true;
		}
		return false;
	}

	/**
	 * 判断是否是配饰
	 */
	public static boolean Accessories(long type) {
		if (Mask(type)) {
			return true;
		} else if (Belt(type)) {
			return true;
		} else if (Cloak(type)) {
			return true;
		} else if (Pendant(type)) {
			return true;
		} else if (Ring(type)) {
			return true;
		} else if (Amulet(type)) {
			return true;
		}
		return false;
	}

	/**
	 * 判断是否是神兵
	 */
	public static boolean GodEquipment_God(long type) {
		if (type == 6500 || type == 6900 || type == 6601 || type == 6600
				|| type == 6701 || type == 6700  || type == 6800|| type == 66788) {
			return true;
		}
		return false;
	}

	/**
	 * 判断是否是仙器
	 */
	public static boolean GodEquipment_xian(long type) {
		if (type >= 7000 && type <= 7004
				|| type >=  1201 && type <=  1212
				|| type >=  1112 && type <=  1123
				|| type >=  1301 && type <=  1302
				|| type >= 1220 && type <= 1224
		) {
			return true;
		}
		return false;
	}

	/**
	 * 判断是否是定制装备
	 */
	public static boolean GodEquipment_Ding(long type) {
		if (type >=  8868&&type <= 8872) {
			return true;
		}
		return false;
	}
	/**
	 * 判断是否是矿石
	 */
	public static boolean Ore(long type) {
		if (type == 500) {
			return true;
		}
		return false;
	}

	/**
	 * 判断是否是普通装备
	 */
	public static boolean OrdinaryEquipment(long type) {
		if ((type == 800) || (type >= 600 && type <= 605)) {
			return true;
		}
		return false;
	}

	/**
	 * 判断装备的类型 -1 不是装备 0武器 1头盔 2项链 3衣服 4护身符 5鞋子 6面具 7腰带 8披风 9挂件 10 11戒指  12翅膀 13星卡
	 */ 
	public static int EquipmentType(long type) {

		if (Weapons(type)) {
			return 0;
		} else if (Helmet(type)) {
			return 1;
		} else if (Necklace(type)) {
			return 2;
		} else if (Clothes(type)) {
			return 3;
		} else if (Amulet(type)) {
			return 4;
		} else if (Shoes(type)) {
			return 5;
		} else if (Mask(type)) {
			return 6;
		} else if (Belt(type)) {
			return 7;
		} else if (Cloak(type)) {
			return 8;
		} else if (Pendant(type)) {
			return 9;
		} else if (Ring(type)) {
			return 10;
		}else if (type==8888) {
			return 12;
		}else if (type==520) {
			return 13;
		} else if (wwww(type)) {
			return 28;
		} else if (wwwww(type)) {
			return 29;
		} else if (wwwwww(type)) {
			return 42;
		} else if (wwwwwww(type)) {
			return 43;
		} else if (wwwwwwww(type)) {
			return 44;


		} else if (XXXXXXXXXXXXXXXX(type)) {return 21;
		} else if (XXXXXXXXXXXXXXXXX(type)) {return 22;
		} else if (xx(type)) {return 23;
		} else if (xxx(type)) {return 24;
		} else if (xxxx(type)) {return 25;
		} else if (xxxxx(type)) {return 26;
		} else if (xxxxxx(type)) {return 27;




		} else if (XXXX(type)) {return 30;
		} else if (XXXXX(type)) {return 31;
		} else if (XXXXXX(type)) {return 32;
		} else if (XXXXXXX(type)) {return 33;
		} else if (XXXXXXXX(type)) {return 34;
		} else if (XXXXXXXXX(type)) {return 35;
		} else if (XXXXXXXXXX(type)) {return 36;
		} else if (XXXXXXXXXXX(type)) {return 37;
		} else if (XXXXXXXXXXXX(type)) {return 38;
		} else if (XXXXXXXXXXXXX(type)) {return 39;
		} else if (XXXXXXXXXXXXXX(type)) {return 40;
		} else if (XXXXXXXXXXXXXXX(type)) {return 41;

		} else if (a(type)) {return 45;
		} else if (aa(type)) {return 46;
		} else if (aaa(type)) {return 47;
		} else if (aaaa(type)) {return 48;
		} else if (aaaaa(type)) {return 49;
		}

		return -1;
	}
	public static boolean XXXX(long type) {
		if ((type == 1112)) {return true;}return false;}

	public static boolean XXXXX(long type) {
		if ((type == 1113)) {return true;}return false;}

	public static boolean XXXXXX(long type) {
		if ((type == 1114)) {return true;}return false;}

	public static boolean XXXXXXX(long type) {
		if ((type == 1115)) {return true;}return false;}
	public static boolean XXXXXXXX(long type) {
		if ((type == 1116)) {return true;}return false;}
	public static boolean XXXXXXXXX(long type) {
		if ((type == 1117)) {return true;}return false;}
	public static boolean XXXXXXXXXX(long type) {
		if ((type == 1118)) {return true;}return false;}
	public static boolean XXXXXXXXXXX(long type) {
		if ((type == 1119)) {return true;}return false;}
	public static boolean XXXXXXXXXXXX(long type) {
		if ((type == 1120)) {return true;}return false;}
	public static boolean XXXXXXXXXXXXX(long type) {
		if ((type == 1121)) {return true;}return false;}
	public static boolean XXXXXXXXXXXXXX(long type) {
		if ((type == 1122)) {return true;}return false;}
	public static boolean XXXXXXXXXXXXXXX(long type) {
		if ((type == 1123)) {return true;}return false;}

	public static boolean a(long type) {
		if ((type == 1220)) {return true;}return false;}
	public static boolean aa(long type) {
		if ((type == 1221)) {return true;}return false;}
	public static boolean aaa(long type) {
		if ((type == 1222)) {return true;}return false;}
	public static boolean aaaa(long type) {
		if ((type == 1223)) {return true;}return false;}
	public static boolean aaaaa(long type) {
		if ((type == 1224)) {return true;}return false;}




	public static boolean XXXXXXXXXXXXXXXX(long type) {
		if ((type == 1211)|| (type == 1311)) {return true;}return false;}
	public static boolean XXXXXXXXXXXXXXXXX(long type) {
		if ((type == 1212)|| (type == 1312)) {return true;}return false;}

	public static boolean wwww(long type) {
		if ((type == 1201)|| (type == 1301)) {return true;}return false;}

	public static boolean wwwww(long type) {
		if ((type == 1202)|| (type == 1302)) {return true;}return false;}

	public static boolean wwwwww(long type) {
		if ((type == 1203)|| (type == 1303)) {return true;}return false;}

	public static boolean wwwwwww(long type) {
		if ((type == 1204)|| (type == 1304)) {return true;}return false;}
	public static boolean wwwwwwww(long type) {
		if ((type == 1205)|| (type == 1305)) {return true;}return false;}



	public static boolean xx(long type) {
		if ((type == 1206)|| (type == 1306)) {return true;}return false;}

	public static boolean xxx(long type) {
		if ((type == 1207)|| (type == 1307)) {return true;}return false;}

	public static boolean xxxx(long type) {
		if ((type == 1208)|| (type == 1308)) {return true;}return false;}

	public static boolean xxxxx(long type) {
		if ((type == 1209)|| (type == 1309)) {return true;}return false;}
	public static boolean xxxxxx(long type) {
		if ((type == 1210)|| (type == 1310)) {return true;}return false;}

	/**
	 * 判断是否是武器
	 */
	public static boolean Weapons(long type) {
		if ((type == 800) || (type == 6500) || (type == 7004)) {
			return true;
		}
		return false;

	}

	/**
	 * 判断是否是头盔
	 */
	public static boolean Helmet(long type) {
		if ((type == 601) || (type == 600) || (type == 6600) || (type == 6601)
				|| (type == 600) || (type == 7001)) {
			return true;
		}
		return false;
	}
	/**=================================================*/
	public static boolean Xqitouk(long type) {
		if ((type == 7001)) {
			return true;
		}
		return false;
	}
	public static boolean xqiwuqi(long type) {
		if ((type == 7004)) {
			return true;
		}
		return false;
	}

	public static boolean xqixianglian(long type) {
		if ((type == 7002)) {
			return true;
		}
		return false;
	}
	public static boolean xqiwyf(long type) {
		if ((type == 7000)) {
			return true;
		}
		return false;
	}
	public static boolean xqixiezi(long type) {
		if ((type == 7003)) {
			return true;
		}
		return false;
	}
	/**=================================================*/
	/**
	 * 判断是否是项链
	 */
	public static boolean Necklace(long type) {
		if ((type == 603) || (type == 7002) || type == 6800) {
			return true;
		}
		return false;
	}

	/**
	 * 判断是否是衣服
	 */
	public static boolean Clothes(long type) {
		if ((type == 605) || (type == 604) || (type == 6700) || (type == 6701)
				|| (type == 7000)) {
			return true;
		}
		return false;
	}
	/**
	 * 判断是否是护身符
	 */
	public static boolean Amulet(long type) {
		if (type == 612||type == 611||type == 1407) {
			return true;
		}
		return false;
	}
	/**
	 * 判断是否是鞋子
	 */
	public static boolean Shoes(long type) {
		if ((type == 602) || (type == 6900) || (type == 7003)) {
			return true;
		}
		return false;
	}

	/**判断是否是面具*/
	public static boolean Mask(long type) {
		if ((type == 609)||(type==927||type == 1403)) {
			return true;
		}
		return false;
	}

	/**
	 * 判断是否是戒指
	 */
	public static boolean Ring(long type) {
		if ((type == 606)||(type==928||type == 1400||type == 1401)) {
			return true;
		}
		return false;
	}

	/**
	 * 判断是否是腰带
	 */
	public static boolean Belt(long type) {
		if ((type == 608)||(type==929||type == 1405)){
			return true;
		}
		return false;
	}

	/**
	 * 判断是否是披风
	 */
	public static boolean Cloak(long type) {
		if ((type == 610)||(type==930||type == 1400||type == 1401)) {
			return true;
		}
		return false;
	}

	/**
	 * 判断是否是挂件
	 */
	public static boolean Pendant(long type) {
		if ((type == 607)||(type==931||type == 1402)){
			return true;
		}
		return false;
	}

	/**
	 * 判断是否是召唤兽装备所需物品<br>
	 * 498 九彩<br>
	 * 497 内丹<br>
	 * 513玄铁晶石 <br>
	 * 514千年魂石<br>
	 * 515隐月神石<br>
	 * 
	 * @param type
	 * @return
	 */
	public static boolean isSummonGoods(long type) {
		if (type == 498 || type == 497 || type == 513 || type == 514 || type == 515) {
			return true;
		}
		return false;
	}

	/**
	 * 判断是否是召唤兽装备<br>
	 * 510兽环<br>
	 * 511兽铃<br>
	 * 512兽甲<br>
	 * 
	 * @param type
	 * @return
	 */
	public static boolean isSummonEquip(long type) {
		if (type == 510 || type == 511 || type == 512) {
			return true;
		}
		return false;
	}
	/**伙伴装备*/
	public static boolean isPalEquip(long type) {
		if (type>=7503&&type<=7509) {
			return true;
		}
		return false;
	}
}
