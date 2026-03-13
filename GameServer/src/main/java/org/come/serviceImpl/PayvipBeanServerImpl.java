package org.come.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import org.come.entity.PayvipBean;
import org.come.mapper.PayvipBeanServerMapper;
import org.come.service.PayvipBeanServer;
import org.come.tool.ReadExelTool;
import org.come.tool.SettModelMemberTool;
import org.come.until.MybatisUntil;
import org.springframework.context.ApplicationContext;

import com.github.pagehelper.PageHelper;

public class PayvipBeanServerImpl implements PayvipBeanServer{
	private PayvipBeanServerMapper payvipBeanServerMapper;
	public static Integer pageNum = 10;
	public PayvipBeanServerImpl() {
		ApplicationContext ctx = MybatisUntil.getApplicationContext();
		payvipBeanServerMapper = ctx.getBean("payvipBeanServerMapper",PayvipBeanServerMapper.class);
	}
	@Override
	public List<PayvipBean> selectAllVip() {
//		return payvipBeanServerMapper.selectAllVip();
		List<PayvipBean> list = new ArrayList<>();
		String[][] result = ReadExelTool.getResult("config/payvip.xls");
		// 遍历每行为对象赋值
		for (int i = 1; i < result.length; i++) {
			if (result[i][0].equals("")) {continue;}
			PayvipBean vip = new PayvipBean();
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
	@Override
	public List<PayvipBean> selectVipPage(int page) {
		PageHelper.startPage(page, pageNum);
		return payvipBeanServerMapper.selectAllVip();
	}
	@Override
	public int deletePayvioBean(Integer id) {
		return payvipBeanServerMapper.deletePayvipBean(id);
	}
	@Override
	public int insertPayvioBean(PayvipBean payvipBean) {
		return payvipBeanServerMapper.insertPayvioBean(payvipBean);
	}
	@Override
	public int updatePayvioBean(PayvipBean payvipBean){
		return payvipBeanServerMapper.updatePayvioBean(payvipBean);
	}
}
