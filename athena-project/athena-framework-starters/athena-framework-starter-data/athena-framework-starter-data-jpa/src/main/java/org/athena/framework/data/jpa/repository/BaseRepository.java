package org.athena.framework.data.jpa.repository;

import org.athena.framework.data.jpa.domain.base.LogicalDeleteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseRepository<T, ID> extends JpaRepository<T, ID> {

    default void logicalDelete(ID id) {
        findById(id).ifPresent(e -> {
            if (e instanceof LogicalDeleteEntity) {
                ((LogicalDeleteEntity) e).setDeleted(true);
                save(e);
            }
        });
    }
}
