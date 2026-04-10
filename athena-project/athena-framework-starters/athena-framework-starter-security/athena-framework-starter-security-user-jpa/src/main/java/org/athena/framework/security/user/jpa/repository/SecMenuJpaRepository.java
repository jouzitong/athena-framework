package org.athena.framework.security.user.jpa.repository;

import org.athena.framework.data.jpa.repository.BaseRepository;
import org.athena.framework.security.user.jpa.entity.SecMenuEntity;

import java.util.List;

public interface SecMenuJpaRepository extends BaseRepository<SecMenuEntity> {

    List<SecMenuEntity> findAllByOrderBySortOrderAscIdAsc();
}
