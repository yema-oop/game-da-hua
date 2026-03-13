package org.come.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import org.come.entity.ChongjipackBean;
import org.come.mapper.ChongjipackMapper;
import org.come.service.ChongjipackServeice;
import org.come.tool.ReadExelTool;
import org.come.tool.SettModelMemberTool;
import org.come.until.MybatisUntil;
import org.springframework.context.ApplicationContext;

import com.github.pagehelper.PageHelper;

public class ChongjipackServeiceImpl implements ChongjipackServeice{
	private ChongjipackMapper chongjipackMapper;
	public static Integer pageNum = 10;
	public ChongjipackServeiceImpl() {
		ApplicationContext ctx = MybatisUntil.getApplicationContext();
		chongjipackMapper = ctx.getBean("chongjipackMapper",ChongjipackMapper.class);
	}
	@Override
	public List<ChongjipackBean> selectChongjipack(int type,int page) {
		PageHelper.startPage(page, pageNum);
		return chongjipackMapper.selectChongjipack(type);
	}
	@Override
	public int deleteChongjipack(Integer id) {
		return chongjipackMapper.deleteChongjipack(id);
	}
	@Override
	public int insertChongjipack(ChongjipackBean chongjipackBean) {
		return chongjipackMapper.insertChongjipack(chongjipackBean);
	}
	@Override
	public int updateChongjipack(ChongjipackBean chongjipackBean){
		return chongjipackMapper.updateChongjipack(chongjipackBean);
	}
	@Override
	public List<ChongjipackBean> selectAllPack() {
		// TODO Auto-generated method stub
//		return chongjipackMapper.selectAllPack();
		List<ChongjipackBean> list = new ArrayList<>();
		String[][] result = ReadExelTool.getResult("config/chongjipack.xls");
		// 遍历每行为对象赋值
		for (int i = 1; i < result.length; i++) {
			if (result[i][0].equals("")) {continue;}
			ChongjipackBean vip = new ChongjipackBean();
			for (int j = 0; j < result[i].length; j++) {
				try {
					SettModelMemberTool.setReflectRelative(vip, result[i][j], j);
				} catch (Exception e) {
					System.out.println(result[i][j]);
					e.printStackTrace();
//                    UpXlsAndTxtFile.addStringBufferMessage(buffer, i, j, result[i][j],  MainServerHandler.getErrorMessage(e));
					return null;
				}
			}
			list.add(vip);
		}
		return list;
	}

}
