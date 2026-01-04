package org.athena.test.jpa.bean;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import org.athena.framework.data.jpa.domain.base.LogicalDeleteEntity;

@Entity
@Table(name = "biz_order")
@Data
public class BizOrder extends LogicalDeleteEntity {

    @Column
    private String orderNo;

    private Long productId;

    private Integer quantity;

    private String status; // NEW / PAID / CANCELED
}

