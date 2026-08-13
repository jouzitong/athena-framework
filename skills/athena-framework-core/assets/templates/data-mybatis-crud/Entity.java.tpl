package {{PACKAGE}}.persistence;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import org.athena.framework.data.mybatis.entity.BaseEntity;

@TableName("{{TABLE_NAME}}")
public class {{NAME}}Entity extends BaseEntity {

    @TableField("name")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
