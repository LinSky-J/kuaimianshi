package com.jinlin.kuaimianshibackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jinlin.kuaimianshibackend.model.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户 Mapper
 *
 * 使用 MyBatis-Plus 的通用 BaseMapper，
 * 不需要写任何自定义方法即可完成基础 CRUD。
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
