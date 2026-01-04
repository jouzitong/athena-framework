package org.athena.framework.data.jpa.domain;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@MappedSuperclass
public class SecureEntity {

    @Setter
    @Getter
    @Column(name = "tenant_id")
    private Long tenantId;

    @Setter
    @Getter
    @Column(name = "owner_id")
    private Long ownerId;

    @Getter
    @Setter
    @Column(name = "role_ids", columnDefinition = "json comment '包含角色'")
    private List<Long> roleIds;

}
