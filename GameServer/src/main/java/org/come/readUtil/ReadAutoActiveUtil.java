package org.come.readUtil;

import org.come.handler.MainServerHandler;
import org.come.model.AutoActiveBase;
import org.come.servlet.UpXlsAndTxtFile;
import org.come.tool.ReadExelTool;
import org.come.tool.SettModelMemberTool;

import java.util.ArrayList;
import java.util.List;

public class ReadAutoActiveUtil {

    /** 扫描怪兽文件，获得全部怪兽信息*/
    public static List<AutoActiveBase> selectActives(String path, StringBuffer buffer){
        String[][] result = ReadExelTool.getResult("config/"+path+".xls");
        if (result.length<=1) {
            UpXlsAndTxtFile.addStringBufferMessage(buffer, 1, 0,"未设置自动任务", "");
            return null;
        }
        List<AutoActiveBase> baseList=new ArrayList<>();
        for (int i = 1; i < result.length; i++) {
            if (result[i][0].equals("")) {continue;}
            AutoActiveBase activeBase=new AutoActiveBase();
            for (int j = 0; j < result[i].length; j++) {
                try {
                    SettModelMemberTool.setReflectRelative(activeBase, result[i][j],j);
                } catch (Exception e) {
                    UpXlsAndTxtFile.addStringBufferMessage(buffer, i, j,result[i][j], MainServerHandler.getErrorMessage(e));
                    return null;
                }
            }
            if (activeBase.getId()!=0) {baseList.add(activeBase);}
        }
        return baseList;
    }
}
