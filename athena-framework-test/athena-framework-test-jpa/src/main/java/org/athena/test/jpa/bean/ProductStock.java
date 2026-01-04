package org.athena.test.jpa.bean;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import org.athena.framework.data.jpa.domain.base.LogicalDeleteEntity;

@Entity
@Table(name = "product_stock")
@Data
public class ProductStock extends LogicalDeleteEntity {

    private String productName;

    private Integer totalStock;

    private Integer availableStock;
}

