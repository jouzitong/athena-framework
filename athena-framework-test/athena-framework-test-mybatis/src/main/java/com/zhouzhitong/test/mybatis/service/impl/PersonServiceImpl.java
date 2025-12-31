package com.zhouzhitong.test.mybatis.service.impl;

import com.zhouzhitong.test.mybatis.bean.Address;
import com.zhouzhitong.test.mybatis.bean.Person;
import com.zhouzhitong.test.mybatis.dto.PersonDTO;
import com.zhouzhitong.test.mybatis.mapper.PersonMapper;
import com.zhouzhitong.test.mybatis.service.PersonService;
import com.zhouzhitong.test.mybatis.type.Gender;
import lombok.extern.slf4j.Slf4j;
import org.athena.framework.data.mybatis.service.impl.MapperServiceImpl;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * @author zhouzhitong
 * @since 2023-12-11
 **/
@Service
@Slf4j
@Order()
public class PersonServiceImpl
        extends MapperServiceImpl<Person, PersonMapper, PersonDTO, Long>
        implements PersonService, CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        initTest();
    }

    public void initTest() {
        LOGGER.info("person 测试");

        // Insert two Person records
        Person person1 = new Person();
        person1.setName("John Doe");
        person1.setGender(Gender.MAN);
        person1.setAge(30);
        person1.setAddress(Address.builder()
                .province("北京市")
                .city("北京市")
                .district("西城区").build());
        save(person1);

        Person person2 = new Person();
        person2.setName("Jane Smith");
        person2.setGender(Gender.WOMAN);
        person2.setAge(25);
        save(person2);

        // Query the inserted Person records
        List<Person> persons = listByIds(Arrays.asList(person1.getId(), person2.getId()));

        // Log the queried Person records
        for (Person p : persons) {
            LOGGER.info("Queried Person: {}", p);
        }
    }

}
