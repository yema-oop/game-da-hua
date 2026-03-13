package come.tool.Mixdeal;


import java.math.BigDecimal;

/**
 * 基础值
 * @author Administrator
 *
 */
public class PetGrade {
	//测试
	public static void main(String[] args) {
		int lvl = 70;
		int base = 6;
		double G = 1.5;
//		System.out.println(getRoleValue(lvl, lvl * 8, G, base,0));
	}
	//(base*(1+lvl*0.02))+lvl
    //HP=int(级别*成长*点数)+int((0.7*级别*成长+1)*初值) 
    //MP=int(级别*成长*点数)+int((0.7*级别*成长+1)*初值)
    //AP=int(级别*成长*点数/5)+int((0.14*级别*成长+1)*初值)
    //SP=int(初敏+点数）*成长  
    //获取召唤兽的属性值                               等级   点数 成长 基础值 0hp 1mp 2ap 3sp 
	public static BigDecimal getRoleValue(BigDecimal lvl, BigDecimal P, BigDecimal G, BigDecimal base, BigDecimal type){
		//int E=(100-lvl)/5;
		//int LEPG=(int) ((lvl+E)*P*G);
		if (type.intValue()==0||type.intValue()==1) {
//			return  ( (lvl*P*G)+((0.7*lvl*G+1)*base));
			return lvl.multiply(P).multiply(G).add(BigDecimal.valueOf(0.7).multiply(lvl).multiply(G).add(BigDecimal.ONE).multiply(base));
		}else if (type.intValue()==2) {
			return (lvl.multiply(P).multiply(G).divide(BigDecimal.valueOf(5))).add(BigDecimal.valueOf(0.14).multiply(lvl).multiply(G).add(BigDecimal.ONE)).multiply(base);
		}else {

			return ((base.add(P)).multiply(G));
		}	
	}
}
