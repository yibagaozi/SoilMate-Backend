package com.soilmate.webservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.soilmate.webservice.entity.Plant;
import org.apache.ibatis.annotations.Mapper;

/**
 * Mapper for Plant entity.
 *
 * @author MA, Ruize
 * @since 1.0.0
 */
@Mapper
public interface PlantMapper extends BaseMapper<Plant> {
}
