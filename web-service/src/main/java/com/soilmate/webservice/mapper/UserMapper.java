package com.soilmate.webservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.soilmate.webservice.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * Mapper for User entity.
 *
 * @author MA, Ruize
 * @since 1.0.0
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
