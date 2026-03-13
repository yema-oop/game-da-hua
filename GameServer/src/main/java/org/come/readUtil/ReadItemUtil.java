package org.come.readUtil;

import come.tool.Calculation.PalEquipQl;
import org.come.entity.Goodstable;
import org.come.entity.RoleSummoning;
import org.come.handler.MainServerHandler;
import org.come.model.Itemrecycling;
import org.come.model.PalEquip;
import org.come.model.PetExchange;
import org.come.model.ItemExchange;
import org.come.readBean.allItemRecycling;
import org.come.readBean.allPetExchange;
import org.come.readBean.allItemExchange;
import org.come.server.GameServer;
import org.come.servlet.UpXlsAndTxtFile;
import org.come.tool.ReadExelTool;
import org.come.tool.SettModelMemberTool;
import org.come.until.GsonUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 读取召唤兽信息
 * @author 叶豪芳
 * @date 2018年1月4日 上午11:59:53
 *
 */
public class ReadItemUtil {
    /**读取召唤信息表*/
    public static ConcurrentHashMap<BigDecimal, Goodstable> allitemId(String path, StringBuffer buffer) {
        // 储存所有召唤兽信息
        ConcurrentHashMap<BigDecimal, Goodstable> allitem = new ConcurrentHashMap<BigDecimal, Goodstable>();
        // 读取召唤兽文件获得所有召唤兽信息
        String[][] result = ReadExelTool.getResult("config/"+path+".xls");
        // 遍历每行为对象赋值
        for (int i = 2; i < result.length; i++) {
            if (result[i][0].equals("")) {continue;}
            Goodstable item = new Goodstable();
            for (int j = 0; j < result[i].length; j++) {
                try {
                    SettModelMemberTool.setReflect(item,result[i][j], j);
                } catch (Exception e) {
                    UpXlsAndTxtFile.addStringBufferMessage(buffer, i, j, result[i][j],MainServerHandler.getErrorMessage(e));
                    return null;
                }
            }
            if (!item.getGoodsid().equals("")) {
                item.toString();
                allitem.put(new BigDecimal(String.valueOf(item.getGoodsid())),item);
            }
        }
        return allitem;
    }
    /**读取召唤兽兑换表*/
    public static ConcurrentHashMap<Integer, ItemExchange> allPetExchangeMap(String path, StringBuffer buffer) {
        ConcurrentHashMap<Integer, ItemExchange> allItemExchange = new ConcurrentHashMap<Integer, ItemExchange>();
        String[][] result = ReadExelTool.getResult("config/"+path+".xls");
        for (int i = 1; i < result.length; i++) {
            if (result[i][0].equals("")) {continue;}
            ItemExchange ItemExchange=new ItemExchange();
            for (int j = 0; j < result[i].length; j++) {
                try {
                    SettModelMemberTool.setReflect(ItemExchange,result[i][j], j);
                } catch (Exception e) {
                    UpXlsAndTxtFile.addStringBufferMessage(buffer, i, j, result[i][j],MainServerHandler.getErrorMessage(e));
                    return null;
                }
            }
            if (ItemExchange.geteId()!=0) {
                Goodstable pet=GameServer.getgoods(ItemExchange.getGoods());
                if (pet==null) {
                    buffer.append("未找到对应的装备ID:"+ItemExchange.getGoods());
                    return null;
                }
                ItemExchange.initPet(pet);
                allItemExchange.put(ItemExchange.geteId(), ItemExchange);
            }
        }
        return allItemExchange;
    }
    /**获取召唤兽兑换表txt数据*/
    public static String createTxtPetExchange(ConcurrentHashMap<Integer, ItemExchange> map){
        allItemExchange allItemExchange = new allItemExchange();
        allItemExchange.setAllItemExchange(map);
        String msgString = GsonUtil.getGsonUtil().getgson().toJson(allItemExchange);
        return msgString;
    }

    /**读取物品回收兑换表*/
    public static ConcurrentHashMap<Integer, Itemrecycling> allrecyclingExchangeMap(String path, StringBuffer buffer) {
        ConcurrentHashMap<Integer, Itemrecycling> allItemrecycling = new ConcurrentHashMap<Integer, Itemrecycling>();
        String[][] result = ReadExelTool.getResult("config/"+path+".xls");
        for (int i = 1; i < result.length; i++) {
            if (result[i][0].equals("")) {continue;}
            Itemrecycling itemrecycling=new Itemrecycling();
            for (int j = 0; j < result[i].length; j++) {
                try {
                    SettModelMemberTool.setReflect(itemrecycling,result[i][j], j);
                } catch (Exception e) {
                    UpXlsAndTxtFile.addStringBufferMessage(buffer, i, j, result[i][j],MainServerHandler.getErrorMessage(e));
                    return null;
                }
            }
            if (itemrecycling.geteId()!=0) {
                Goodstable pet=GameServer.getgoods(itemrecycling.getGoods());
                if (pet==null) {
                    buffer.append("未找到对应的装备ID:"+itemrecycling.getGoods());
                    return null;
                }
                itemrecycling.initPet(pet);
                allItemrecycling.put(itemrecycling.geteId(), itemrecycling);
            }
        }
        return allItemrecycling;
    }

    /**获取物品回收兑换表txt数据*/
    public static String createTxItemRecycling(ConcurrentHashMap<Integer, Itemrecycling> map){
        allItemRecycling allItemRecycling = new allItemRecycling();
        allItemRecycling.setAllItemExchange(map);
        String msgString = GsonUtil.getGsonUtil().getgson().toJson(allItemRecycling);
        return msgString;
    }
    /**
     * HGC预加载
     *
     * @time 2020年1月7日 下午5:58:15<br>
     * @param path
     * @return
     */
    public static List<Goodstable> selectRoleSummonings(String path, StringBuffer buffer) {
        List<Goodstable> item = new ArrayList<Goodstable>();
        // 读取召唤兽文件获得所有召唤兽信息
        String[][] result = ReadExelTool.getResultRelative(path);
        // 遍历每行为对象赋值
        for (int i = 2; i < result.length; i++) {
            if (result[i][0].equals("")) {continue;}
            Goodstable g = new Goodstable();
            for (int j = 0; j < result[i].length; j++) {
                try {
                    SettModelMemberTool.setReflectRelative(g, result[i][j], j);
                } catch (Exception e) {
                    UpXlsAndTxtFile.addStringBufferMessage(buffer, i, j, result[i][j],MainServerHandler.getErrorMessage(e));
                    return null;
                }
            }
            item.add(g);
        }
        return item;
    }
}
