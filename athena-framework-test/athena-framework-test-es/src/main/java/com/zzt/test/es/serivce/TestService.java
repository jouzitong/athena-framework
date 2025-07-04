package com.zzt.test.es.serivce;

import com.zzt.test.es.model.Product;
import com.zzt.test.es.repository.ProductRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
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

    @PostConstruct
    public void test() {
//        Product product = new Product();
//        product.setName("测试");
//        product.setPrice(100.0);
//        product.setId("1");
//        productRepository.save(product);

        List<Product> products = productRepository.findByName("测试");

        System.out.println(products);

//        NativeSearchQuery query = new NativeSearchQueryBuilder()
//                .withQuery(QueryBuilders,()
//                        .must(QueryBuilders.matchQuery("title", "phone"))
//                        .must(QueryBuilders.termQuery("category.id", "c1"))
//                        .must(QueryBuilders.rangeQuery("price").gte(1000).lte(3000))
//                        .must(QueryBuilders.nestedQuery("attributes",
//                                QueryBuilders.boolQuery()
//                                        .must(QueryBuilders.termQuery("attributes.name", "color"))
//                                        .must(QueryBuilders.matchQuery("attributes.value", "black")),
//                                ScoreMode.Avg
//                        ))
//                )
//                .withSort(SortBuilders.fieldSort("price").order(SortOrder.ASC))
//                .withPageable(PageRequest.of(1, 10))
//                .build();
//
//        SearchHits<Product> hits = elasticsearchRestTemplate.search(query, Product.class);



    }

}
