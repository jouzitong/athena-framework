package org.athena.test.log4j2.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.arthena.framework.common.utils.JacksonJsonUtils;
import org.athena.test.log4j2.jsonmd.Cat;
import org.athena.test.log4j2.jsonmd.Dog;
import org.athena.test.log4j2.jsonmd.base.Animal;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author zhouzhitong
 * @since 2026/1/11
 */
@Service
public class JsonTestService {

    @PostConstruct
    void test() throws JsonProcessingException {
        JsonW jsonw = new JsonW();
        jsonw.getJsonObjects().add(new Dog());
        jsonw.getJsonObjects().add(new Cat());


        ObjectMapper objectMapper = new ObjectMapper();

        String str = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonw);
//        String str = JacksonJsonUtils.toStr(jsonw);
//        System.out.println(str);

        JsonW jsonw2 = JacksonJsonUtils.toBean(str, JsonW.class);
        System.out.println(jsonw2);

    }

    @Setter
    @Getter
    public static class JsonW {
        private List<Animal> jsonObjects = new ArrayList<>();
    }

}
