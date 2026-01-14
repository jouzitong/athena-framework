package org.athena.test.jpa.repository;

import jakarta.persistence.LockModeType;
import org.athena.framework.data.jpa.repository.BaseRepository;
import org.athena.test.jpa.bean.ProductStock;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductStockRepository extends BaseRepository<ProductStock> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from ProductStock p where p.id = :id")
    Optional<ProductStock> lockById(@Param("id") Long id);
}


