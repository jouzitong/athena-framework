package com.zzt.test.es.serivce;

import com.zzt.test.es.model.Product;
import com.zzt.test.es.repository.ProductRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.QueryBuilders;
import org.springframework.data.elasticsearch.client.erhlc.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.client.erhlc.NativeSearchQuery;
import org.springframework.data.elasticsearch.client.erhlc.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zhouzhitong
 * @since 2025/5/23
 **/
@Service
@Slf4j
public class TestService {

    @Resource
    private ProductRepository productRepository;

    @Resource
    private ElasticsearchRestTemplate template;

    @PostConstruct
    public void test() {
//        Product product = new Product();
//        product.setName("测试");
//        product.setPrice(100.0);
//        product.setId("1");
//        productRepository.save(product);

        List<Product> products = productRepository.findByName("测试");

        System.out.println(products);


        SearchHits<Product> hits = template.search(query, Product.class);



    }

}
