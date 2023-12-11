package com.zhouzhitong.test.mybatis.controller;

import com.zhouzhitong.test.mybatis.bean.Person;
import com.zhouzhitong.test.mybatis.controller.base.BaseController;
import com.zhouzhitong.test.mybatis.dto.PersonDTO;
import com.zhouzhitong.test.mybatis.service.PersonService;
import org.authena.mybatis.base.BaseRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhouzhitong
 * @since 2023-12-11
 **/
@RestController
@RequestMapping("/test/persons")
public class PersonController extends BaseController<Person, PersonDTO, BaseRequest, PersonService> {

    @Autowired
    private PersonService service;

    @Override
    protected PersonService service() {
        return service;
    }
}
