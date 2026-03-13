package org.come.serviceImpl;

import come.tool.FightingDataAction.Transfer;
import org.apache.ibatis.annotations.Param;
import org.come.entity.AppVersion;
import org.come.entity.DianKaEntity;
import org.come.extInterBean.Goodsrecord2;
import org.come.extInterBean.ShopBuyRecordReqBean;
import org.come.extInterBean.ShopBuyRecordResultBean;
import org.come.extInterBean.ShopBuyTypeResult;
import org.come.handler.MainServerHandler;
import org.come.mapper.AppVersionMapper;
import org.come.mapper.DianKaMapper;
import org.come.service.AppVersionService;
import org.come.service.DianKaService;
import org.come.tool.WriteOut;
import org.come.until.AllServiceUtil;
import org.come.until.GsonUtil;
import org.come.until.MybatisUntil;
import org.springframework.context.ApplicationContext;

import javax.swing.text.html.parser.Entity;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

// 三端
public class DianKaServiceImpl implements DianKaService {

	private DianKaMapper dianKaMapper;

	private static List<DianKaEntity> DianKas=new ArrayList<>();

	private static boolean isSearch = false;

	private static Object lock=new ArrayList<>();
	public static void add(DianKaEntity dianKaEntity){
		synchronized (lock) {
			DianKas.add(dianKaEntity);
		}
	}

	public static void remove(int i){
		synchronized (lock) {
			DianKas.remove(i);
		}
	}

	public synchronized static void setDian(List<DianKaEntity> dianKas){
		synchronized (lock) {
			DianKas = dianKas;
		}

		isSearch = true;
	}

	public DianKaServiceImpl() {
		ApplicationContext ctx = MybatisUntil.getApplicationContext();
		// id为类名且首字母小写才能被自动扫描扫到
		dianKaMapper = (DianKaMapper) ctx.getBean("dianKaMapper");
	}
	
	@Override
	public List<DianKaEntity> selectAllDian() {
		// TODO Auto-generated method stub
		return dianKaMapper.selectAllDian();
	}

	@Override
	public int deleteDianKa(long roleId, long outTime) {
		// TODO Auto-generated method stub
		return dianKaMapper.deleteDianKa(roleId, outTime);
	}

	@Override
	public int insertDianKaRec(DianKaEntity dianKaEntity) {
		// TODO Auto-generated method stub
		return dianKaMapper.insertDianKaRec(dianKaEntity);
	}

	@Override
	public List<DianKaEntity> selectSingleDian(String version, String outTime) {
		// TODO Auto-generated method stub
		return dianKaMapper.selectSingleDian(version, outTime);
	}

	@Override
	public int updateDianNum(long version, long outTime, long dianshu, long committ) {
		// TODO Auto-generated method stub
		return dianKaMapper.updateDianNum(version, outTime, dianshu, committ);
	}

	public DianKaMapper getDianKaMapper() {
		return dianKaMapper;
	}

	public void setDianKaMapper(DianKaMapper dianKaMapper) {
		this.dianKaMapper = dianKaMapper;
	}

	public static List<DianKaEntity> getDianKas() {
		return DianKas;
	}

	public static void setDianKas(List<DianKaEntity> dianKas) {
		DianKas = dianKas;
	}

	public static boolean isIsSearch() {
		return isSearch;
	}

	public static void setIsSearch(boolean isSearch) {
		DianKaServiceImpl.isSearch = isSearch;
	}
}
