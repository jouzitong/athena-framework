package com.zzt.test.es.repository;

import com.zzt.test.es.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author zhouzhitong
 * @since 2025/5/23
 **/
@Repository
public interface ProductRepository extends ElasticsearchRepository<Product, String> {
    List<Product> findByName(String name);

    Page<Product> findByNameLike(String name, Pageable pageable);

}
