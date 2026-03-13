package org.come.mapper;

import java.math.BigDecimal;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.come.annotation.MyBatisAnnotation;
import org.come.entity.Fly;
import org.come.entity.FlyRoleUser;

/**
 * 飞行器
 *
 * @author 叶豪芳
 * @date : 2017年12月2日 下午3:46:48
 */
@MyBatisAnnotation
public interface FlyMapper {

    void saveBatch(List<Fly> addList);

    List<Fly> selectAllFlys();

    List<Fly> selectFlysByRoleID();

    Integer insertFlyToSql(Fly fly);

    Integer deleteFlysByFidList(List<BigDecimal> delList);

    Integer deleteFlysByFid(BigDecimal fid);

    Integer updateFlyList(List<Fly> addList);

    Integer updateFly(@Param("fly") Fly fly);
}