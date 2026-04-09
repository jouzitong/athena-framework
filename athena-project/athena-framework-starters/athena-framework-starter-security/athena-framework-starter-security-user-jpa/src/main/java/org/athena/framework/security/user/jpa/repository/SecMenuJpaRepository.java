package org.athena.framework.security.user.jpa.repository;

import org.athena.framework.security.user.jpa.entity.SecMenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SecMenuJpaRepository extends JpaRepository<SecMenuEntity, Long> {

    List<SecMenuEntity> findAllByOrderBySortOrderAscIdAsc();
}
