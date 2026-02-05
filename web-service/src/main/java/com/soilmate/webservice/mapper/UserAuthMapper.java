package com.soilmate.webservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.soilmate.webservice.entity.UserAuth;
import org.apache.ibatis.annotations.Mapper;

/**
 * Mapper for UserAuth entity.
 *
 * @author MA, Ruize
 * @since 1.0.0
 */
@Mapper
public interface UserAuthMapper extends BaseMapper<UserAuth> {

}
