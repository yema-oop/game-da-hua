package org.come.service;

import org.apache.ibatis.annotations.Param;
import org.come.bean.TtModel;
import org.come.entity.Fly;

import java.math.BigDecimal;
import java.util.List;

// 三端
public interface FlyService{

	List<Fly> selectFlyByRoleID(BigDecimal roleID );

	/**
	 * 查找角色坐骑
	 * @return
	 */

	Fly selectFlyByFID(String fid );

	/**
	 * 删除角色坐骑
	 */
	void deleteFlyByMid( BigDecimal roleID );

	/**修改坐骑属性*/
	void updateFly( Fly fly );
	/**修改坐骑属性刚从redis取出*/
	void updateFlyRedis(Fly fly);

	/**
	 * 插入数据
	 * @param fly
	 */
	void insertFly( Fly fly );

	void saveBatch(@Param("list") List<Fly> addList);

	List<Fly> selectAllFlys();

	Integer insertFlyToSql(Fly fly);

	Integer deleteFlysByFidList(List<BigDecimal> delList);

	Integer deleteFlyByMidsql(BigDecimal bigDecimal);

	Integer updateFlyList(List<Fly> addList);

	Integer updateFlyToSql(Fly fly);

	void removeByIds(List<BigDecimal> delList);

	void removeById(BigDecimal bigDecimal);

	void updateById(Fly fly);
}
