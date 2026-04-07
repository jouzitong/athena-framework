package org.athena.framework.data.jpa.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.athena.framework.data.jpa.listener.AuditEntityListener;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditEntityListener.class)
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public abstract class AuditableEntity extends BaseEntity {

    @Getter
    @Setter
    @Column(name = "create_time", nullable = false, updatable = false, columnDefinition = "DATETIME default current_timestamp comment '创建时间'")
    @CreatedDate
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Getter
    @Setter
    @Column(name = "created_by", columnDefinition = "bigint default -1 comment '创建人'")
    @CreatedBy
    private Long createdBy;

    @Getter
    @Setter
    @Column(name = "update_time", columnDefinition = "DATETIME default current_timestamp comment '最后修改时间'")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @LastModifiedDate
    private LocalDateTime updateTime;

    @Getter
    @Setter
    @Column(name = "updated_by", columnDefinition = "bigint default -1 comment '最后修改人'")
    @LastModifiedBy
    private Long updatedBy;
}
