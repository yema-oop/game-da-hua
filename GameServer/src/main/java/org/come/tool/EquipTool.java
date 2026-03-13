package org.come.tool;

import java.util.List;


/**
 * 判断是否为可叠加
 * @author 叶豪芳
 * @date 2017年12月24日 下午12:31:36
 *
 */
public class EquipTool {
	static List<Long> strings = SplitStringTool.splitLong("0-1|8|49-52|88|99|100|111|112|118|119|120|123-127|189|190" +
			"|191|212|214|492-505|507|513-515|521-524|29998|776|29999|715|716|721|901-910|2040-2043|2053|2070-2079|2080|2113-2116|" +
			"7005|7010|801-802|1002|1003|1005|1006|1008|9001|2323|66788|2324|728|556|557|889|888|891|744|8889-8893|66677|2233|2234|" +
			"60003|7500-7502|7511|22111|22112|22113|22114|225|28955|915|918|10086|919|667|923|932|935|66655|938|7222|7223|7224|7225|939|9002|8002|8003|8004|1101|1102|1103|1104|1105|666|2013|1000" +
			"|888888|88888|936|668|5055");
	public static boolean canSuper( long goodstype ){// 可重叠返回true
		return strings.contains(goodstype);
	}
	public static boolean contains(String[] vs,String key){
		if (vs!=null) {
			for (int i = 0; i < vs.length; i++) {
				if (vs[i].equals(key)) {
					return true;
				}
			}
		}
		return false;
	}
}
