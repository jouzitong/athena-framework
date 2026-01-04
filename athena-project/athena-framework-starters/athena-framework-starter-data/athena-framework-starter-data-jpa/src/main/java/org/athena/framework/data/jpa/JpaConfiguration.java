package org.athena.framework.data.jpa;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
@ComponentScan("org.athena.framework.data.jdbc")
public class JpaConfiguration {

    @PersistenceContext
    private EntityManager em;

    @PostConstruct
    public void enableFilter() {
//        em.unwrap(Session.class)
//                .enableFilter("logic_delete_filter")
//                .setParameter("deleted", false);
    }

}
