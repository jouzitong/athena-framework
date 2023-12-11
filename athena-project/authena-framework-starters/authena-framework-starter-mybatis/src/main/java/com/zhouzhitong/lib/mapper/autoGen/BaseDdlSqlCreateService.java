package com.zhouzhitong.lib.mapper.autoGen;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zhouzhitong.lib.mapper.autoGen.config.DefaultMapperProperties;
import org.arthena.lib.common.utils.FileUtils;
import org.arthena.lib.common.utils.PackageUtil;
import com.zhouzhitong.lib.mapper.entity.BaseEntity;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Description;

import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author zhouzhitong
 * @since 2023-11-12
 **/
@Slf4j
public abstract class BaseDdlSqlCreateService {
    @Resource
    private DefaultMapperProperties mapperProperties;

    protected abstract String createDdlSql(Class<? extends BaseEntity> subClass);

    @PostConstruct
    public void init() {
        if (!mapperProperties.isEnableCreateTableDdl()) {
            LOGGER.info("自动生成DDL SQL功能未开启. 如果需要该功能, 请添加配置 'lib.mapper.enableCreateTableDdl: true'");
            return;
        }
        // 获取启动目录
        String userDir = System.getProperty("user.dir");
        // 如果前缀没有 / 则添加
        String ddlSqlPath = mapperProperties.getTableDDLPathFile();

        if (!ddlSqlPath.startsWith("/")) {
            userDir += "/";
        }
        String filePath = userDir + ddlSqlPath;

        BufferedWriter bw = FileUtils.getFileOutputStream(filePath);

        List<Class<?>> subClasses = PackageUtil.getSubClasses(BaseEntity.class,mapperProperties.getBaseEntityPackages());
        for (Class<?> subClass : subClasses) {
            String ddlSql = createDdlSql((Class<? extends BaseEntity>) subClass);
            try {
                bw.write("-- " + subClass.getName() + "\n");
                bw.write(ddlSql);
                bw.write("\n\n");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            bw.flush();
            bw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }



    protected String getTableComment(Class<? extends BaseEntity> c) {
        Description annotation = c.getAnnotation(Description.class);
        if (annotation == null) {
            return "";
        }
        return annotation.value();
    }

    protected String getTableName(Class<? extends BaseEntity> c) {
        TableName annotation = c.getAnnotation(TableName.class);
        if (annotation == null || StringUtils.isBlank(annotation.value())) {
            // 类名的驼峰转下划线
            String name = c.getSimpleName();
            return getUnderlineName(name);
        }

        return annotation.value();
    }

    protected List<Field> getAllFields(Class<?> c) {
        List<Field> allFields = new LinkedList<>();
        while (c != BaseEntity.class) {
            Field[] fields = c.getDeclaredFields();
            allFields.addAll(Arrays.asList(fields));
            c = c.getSuperclass();
        }
        return allFields;
    }

    // Ljava/util/List<Ljava/lang/Long;>;
    protected Class<?> getGenericTypeName(Field field) {
        Type genericType = field.getGenericType();
        String signature = genericType.getTypeName();
        // 如果没有 < 和 > 返回空
        if (!signature.contains("<") && !signature.contains(">")) {
            return null;
        }
        int begin = signature.indexOf("<");
        int end = signature.indexOf(">");
        String substring = signature.substring(begin + 1, end);
        try {
            return Class.forName(substring);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 将字符转换成驼峰命名(小写+下划线)
     */
    protected String getUnderlineName(String name) {
        StringBuilder result = new StringBuilder();
        if (name != null && name.length() > 0) {
            // 将第一个字符处理成大写
            result.append(name.substring(0, 1).toLowerCase());
            // 循环处理其余字符
            for (int i = 1; i < name.length(); i++) {
                String s = name.substring(i, i + 1);
                // 在大写字母前添加下划线
                if (s.equals(s.toUpperCase()) && !Character.isDigit(s.charAt(0))) {
                    result.append("_");
                }
                // 其他字符直接转成小写
                result.append(s.toLowerCase());
            }
        }
        return result.toString();
    }

}
