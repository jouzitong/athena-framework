package org.athena.framework.data.jdbc.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author zhouzhitong
 * @since 2025/7/6
 **/
public interface IEntity extends Serializable {

    Long getId();

}
