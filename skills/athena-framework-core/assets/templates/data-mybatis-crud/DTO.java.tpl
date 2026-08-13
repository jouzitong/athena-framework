package {{PACKAGE}}.model;

import org.athena.framework.data.mybatis.entity.dto.BaseDTO;

public class {{NAME}}DTO extends BaseDTO {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
