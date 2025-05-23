package com.zzt.test.es.model;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

/**
 * @author zhouzhitong
 * @since 2025/5/23
 **/
@Document(indexName = "product")
@Data
public class Product {
    @Id
    private String id;
    private String name;
    private Double price;

}
