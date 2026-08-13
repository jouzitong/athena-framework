package {{PACKAGE}}.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.athena.framework.data.jpa.domain.BaseEntity;

@Entity
@Table(name = "{{TABLE_NAME}}")
public class {{NAME}}Entity extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
