package com.jinlin.kuaimianshibackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jinlin.kuaimianshibackend.model.entity.Question;
import org.apache.ibatis.annotations.Mapper;

/**
 * Question mapper.
 */
@Mapper
public interface QuestionMapper extends BaseMapper<Question> {
}
