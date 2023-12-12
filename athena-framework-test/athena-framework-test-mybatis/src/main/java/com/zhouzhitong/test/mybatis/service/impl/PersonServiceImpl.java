package com.zhouzhitong.test.mybatis.service.impl;

import com.zhouzhitong.test.mybatis.bean.Person;
import com.zhouzhitong.test.mybatis.dto.PersonDTO;
import com.zhouzhitong.test.mybatis.mapper.PersonMapper;
import com.zhouzhitong.test.mybatis.service.PersonService;
import lombok.extern.slf4j.Slf4j;
import org.athena.framework.mybatis.service.impl.MapperServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author zhouzhitong
 * @since 2023-12-11
 **/
@Service
@Slf4j
public class PersonServiceImpl
    extends MapperServiceImpl<PersonMapper, Person, PersonDTO>
        implements PersonService {

}
