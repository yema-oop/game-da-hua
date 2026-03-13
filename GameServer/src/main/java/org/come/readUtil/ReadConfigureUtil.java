package org.come.readUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.come.handler.MainServerHandler;
import org.come.model.Configure;
import org.come.readBean.AllConfigure;

import org.come.server.GameServer;
import org.come.servlet.UpXlsAndTxtFile;
import org.come.tool.ReadExelTool;
import org.come.tool.SettModelMemberTool;
import org.come.until.AESUtil;
import org.come.until.CreateTextUtil;
import org.come.until.GsonUtil;

public class ReadConfigureUtil
{
    /**扫描文件，获得全部信息*/
    public static ConcurrentHashMap<Integer, Configure> selectConfigure(String path, StringBuffer buffer){
//		if(!GameServer.mcCode.equals(GameServer.getProperty())) {
//			return null;
//		}
        ConcurrentHashMap<Integer, Configure> configureConcurrentHashMap= new ConcurrentHashMap<>();
        String[][] result = ReadExelTool.getResult("config/"+path+".xls");
        for (int i = 2; i < result.length; i++) {
            if (result[i][0].equals(""))continue;
            Configure scheme=new Configure();
            for (int j = 0; j < result[i].length; j++) {
                try {
                    SettModelMemberTool.setReflectRelative(scheme, result[i][j], j);
                } catch (Exception e) {
                    UpXlsAndTxtFile.addStringBufferMessage(buffer, i, j, result[i][j], MainServerHandler.getErrorMessage(e));
                    return null;
                }
            }
            configureConcurrentHashMap.put(Integer.parseInt(scheme.getId()),scheme);

        }

        return configureConcurrentHashMap;
    }
    public static String createConfigure(ConcurrentHashMap<Integer, Configure> map) {
        AllConfigure allTalent = new AllConfigure();
        Map<BigDecimal, Configure> allMap=new HashMap<>();
        for (Configure configure:map.values()) {
            allMap.put(BigDecimal.valueOf(Long.parseLong(configure.getId())), configure);
        }
        allTalent.setAllConfigure(allMap);
        String msgString = GsonUtil.getGsonUtil().getgson().toJson(allTalent);
        return msgString;
    }
}
