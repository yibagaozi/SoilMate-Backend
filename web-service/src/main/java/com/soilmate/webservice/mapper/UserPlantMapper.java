package com.soilmate.webservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.soilmate.webservice.entity.UserPlant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;

/**
 * Mapper for UserPlant entity.
 *
 * @author MA, Ruize
 * @since 1.0.0
 */
@Mapper
public interface UserPlantMapper extends BaseMapper<UserPlant> {

    /**
     * Count total plants for a user.
     */
    @Select("SELECT COUNT(*) FROM user_plant WHERE user_id = #{userId}")
    Integer countTotalPlants(@Param("userId") Long userId);

    /**
     * Count active plants for a user.
     */
    @Select("SELECT COUNT(*) FROM user_plant WHERE user_id = #{userId} AND is_active = true")
    Integer countActivePlants(@Param("userId") Long userId);
}
