package com.zhouzhitong.test.mybatis.service;

import com.zhouzhitong.test.mybatis.bean.Person;
import com.zhouzhitong.test.mybatis.dto.PersonDTO;
import org.athena.framework.data.jdbc.serivce.IMapperService;

/**
 * @author zhouzhitong
 * @since 2023-12-11
 **/
public interface PersonService extends IMapperService<PersonDTO> {

    default PersonDTO newDTO() {
        return new PersonDTO();
    }

    default Person newEntity() {
        return new Person();
    }

}
