package com.jinlin.kuaimianshibackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jinlin.kuaimianshibackend.model.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户 Mapper。
 *
 * <p>用户表的基础增删改查全部交由 MyBatis-Plus {@link BaseMapper} 处理。</p>
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
