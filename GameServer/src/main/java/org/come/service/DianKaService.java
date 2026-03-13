package org.come.service;

import org.apache.ibatis.annotations.Param;
import org.come.entity.AppVersion;
import org.come.entity.DianKaEntity;
import org.come.extInterBean.Goodsrecord2;
import org.come.extInterBean.ShopBuyRecordReqBean;
import org.come.extInterBean.ShopBuyRecordResultBean;
import org.come.extInterBean.ShopBuyTypeResult;

import java.math.BigDecimal;
import java.util.List;

// 三端
public interface DianKaService {

	List<DianKaEntity> selectAllDian();
	int deleteDianKa(long roleId, long outTime);
	int insertDianKaRec(DianKaEntity dianKaEntity);
	List<DianKaEntity> selectSingleDian(String version, String outTime);

	int updateDianNum(long version, long outTime, long dianshu, long committ);

}
