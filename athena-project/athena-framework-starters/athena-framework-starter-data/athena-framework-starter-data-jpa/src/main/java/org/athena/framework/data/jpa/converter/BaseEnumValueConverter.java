/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.athena.framework.data.jpa.converter;

import jakarta.persistence.AttributeConverter;
import lombok.extern.slf4j.Slf4j;
import org.arthena.framework.common.enums.IEnum;

import java.io.Serializable;
import java.sql.SQLException;

/**
 * BasicValueConverter handling the conversion of an enum based on
 * JPA {@link javax.persistence.EnumType#ORDINAL} strategy (storing the ordinal)
 *
 * @author Steve Ebersole
 */
@Slf4j
public class BaseEnumValueConverter<E extends IEnum> implements AttributeConverter<E, Integer>, Serializable {
    private static final long serialVersionUID = -4467561563537482266L;

    protected Class<E> clazz;

//    protected BaseEnumValueConverter(Class<E> clazz) {
//        this.clazz = clazz;
//    }

    @Override
    public Integer convertToDatabaseColumn(E attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getCode();
    }

    @Override
    public E convertToEntityAttribute(Integer dbData) {
        E[] enumConstants = clazz.getEnumConstants();
        try {
            for (E e : enumConstants) {
                if (e.getCode() == dbData) {
                    return e;
                }
            }
            throw new SQLException("EnumValue not match:" + dbData);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
