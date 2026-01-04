package org.athena.test.jpa.repository;

import org.athena.framework.data.jpa.repository.BaseRepository;
import org.athena.test.jpa.bean.BizOrder;

public interface OrderRepository extends BaseRepository<BizOrder, Long> {
}

