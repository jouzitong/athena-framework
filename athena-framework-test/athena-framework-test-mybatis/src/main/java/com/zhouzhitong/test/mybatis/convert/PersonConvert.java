package com.zhouzhitong.test.mybatis.convert;

import com.zhouzhitong.test.mybatis.bean.Person;
import com.zhouzhitong.test.mybatis.dto.PersonDTO;
import org.athena.framework.data.jdbc.convert.IConvert;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PersonConvert extends IConvert<Person, PersonDTO> {
}
