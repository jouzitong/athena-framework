package com.zhouzhitong.test.mybatis.service;

import com.zhouzhitong.test.mybatis.bean.Person;
import com.zhouzhitong.test.mybatis.dto.PersonDTO;
import org.athena.framework.mybatis.service.MapperService;

/**
 * @author zhouzhitong
 * @since 2023-12-11
 **/
public interface PersonService extends MapperService<Person, PersonDTO> {

    @Override
    default PersonDTO newDTO() {
        return new PersonDTO();
    }

    @Override
    default Person newEntity() {
        return new Person();
    }

}
