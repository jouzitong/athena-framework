package com.zhouzhitong.test.mybatis.mapper;

import com.zhouzhitong.lib.mapper.mapper.CrudMapper;
import com.zhouzhitong.test.mybatis.bean.Person;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author zhouzhitong
 * @since 2023-12-11
 **/
@Mapper
public interface PersonMapper extends CrudMapper<Person> {
}
