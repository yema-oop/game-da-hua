package org.come.mapper;

import org.apache.ibatis.annotations.Param;
import org.come.annotation.MyBatisAnnotation;
import org.come.entity.Baby;
import org.come.entity.DianKaEntity;
import org.come.extInterBean.Goodsrecord2;

import java.math.BigDecimal;
import java.util.List;

// 三端
@MyBatisAnnotation
public interface DianKaMapper {

	List<DianKaEntity> selectAllDian();
	int deleteDianKa(@Param("roleId") long roleId, @Param("outTime") long outTime);
	int insertDianKaRec(DianKaEntity dianKaEntity);
	List<DianKaEntity> selectSingleDian(@Param("roleId") String version, @Param("outTime") String outTime);

	int updateDianNum(@Param("roleId") long version, @Param("outTime") long outTime,
					  @Param("dianshu") long dianshu, @Param("committ") long committ);


}
