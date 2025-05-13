package org.arthena.framework.common.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;

/**
 * json 工具类
 *
 * @author zhouzhitong
 * @version 1.0
 * @since 2022/5/21 1:40
 */
@Component
public class JacksonJsonUtils {

    public final static ObjectMapper JSON = new ObjectMapper();

    static {
        // 允许 json 生成器自动补全未匹配的括号
        JSON.configure(JsonGenerator.Feature.AUTO_CLOSE_JSON_CONTENT, true);
        // 允许 json 生成器在写入完成后自动关闭写入的流
        // JSON.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
        // 允许 json 存在没用引号括起来的 field
        JSON.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        // 允许 json 存在使用单引号括起来的 field
        JSON.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        // 允许 json 存在没用引号括起来的 ascii 控制字符
        // JSON.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        // 允许 json number 类型的数存在前导 0 (like: 0001)
        // JSON.configure(JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS, true);
        // 允许 json 存在 NaN, INF, -INF 作为 number 类型
        JSON.configure(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS, true);
        // 允许 json 存在形如 // 或 /**/ 的注释
        // JSON.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        // 序列化时, 禁止自动缩进 (格式化) 输出的 json (压缩输出)
        // JSON.configure(SerializationFeature.INDENT_OUTPUT, false);
        // 序列化时, 将各种时间日期类型统一序列化为 timestamp 而不是其字符串表示
        // JSON.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        // 序列化时, 对于没有任何 public methods / properties 的类, 序列化不报错
        JSON.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        // 忽略未知的字段
        JSON.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 所有实例中的 空字段, null 字段, 都要参与序列化
        JSON.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        JSON.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // 允许基本类型的值为null
        JSON.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
        // 控制枚举值是否被允许序列化/反序列化为数字
        // JSON.configure(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, false);
        // 可以将结果生成为数组
        JSON.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
        JSON.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        JSON.registerModule(new JavaTimeModule());
    }

    public static <T> T toBean(String content, Class<T> valueType) {
        return readValue(content, valueType);
    }

    public static String toStr(Object value) {
        return writeValueAsString(value);
    }

    @SneakyThrows
    public static <T> T readValue(String content, Class<T> valueType) {
        if (null != content) {
            return JSON.readValue(content, valueType);
        }
        return null;
    }

    @SneakyThrows
    public static <T> T readValue(File file, Class<T> valueType) {
        if (null != file) {
            return JSON.readValue(file, valueType);
        }
        return null;
    }

    @SneakyThrows
    public static <T> T readValue(URL url, Class<T> valueType) {
        if (null != url) {
            return JSON.readValue(url, valueType);
        }
        return null;
    }

    @SneakyThrows
    public static <T> T readValue(String content, TypeReference<T> valueTypeRef) {
        if (null != content) {
            return JSON.readValue(content, valueTypeRef);
        }
        return null;
    }

    @SneakyThrows
    public static String writeValueAsString(Object value) {
        if (null != value) {
            return JSON.writeValueAsString(value);
        }
        return null;
    }

    /**
     * 深拷贝
     *
     * @param value 原始对象
     * @param <T>   对象类型
     * @return 拷贝后的对象
     */
    @SneakyThrows
    public static <T> T copy(T value) {
        if (null != value) {
            String json = JSON.writeValueAsString(value);
            return JSON.readValue(json, new TypeReference<>() {
            });
        }
        return null;
    }


    @SneakyThrows
    public static JsonNode readTree(String content) {
        if (null != content) {
            return JSON.readTree(content);
        }
        return null;
    }

    public static String readStringField(String content, String field) {
        JsonNode jsonNode = readTree(content);
        if (null != jsonNode) {
            JsonNode jsonNodeField = jsonNode.get(field);
            if (null != jsonNodeField) {
                return jsonNodeField.asText();
            }
        }
        return null;
    }

    public static int readIntField(String content, String field) {
        JsonNode jsonNode = readTree(content);
        if (null != jsonNode) {
            JsonNode jsonNodeField = jsonNode.get(field);
            if (null != jsonNodeField) {
                return jsonNodeField.asInt();
            }
        }
        return 0;
    }

    public static double readDoubleField(String content, String field) {
        JsonNode jsonNode = readTree(content);
        if (null != jsonNode) {
            JsonNode jsonNodeField = jsonNode.get(field);
            if (null != jsonNodeField) {
                return jsonNodeField.asDouble();
            }
        }
        return 0.0;
    }

    public static long readLongField(String content, String field) {
        JsonNode jsonNode = readTree(content);
        if (null != jsonNode) {
            JsonNode jsonNodeField = jsonNode.get(field);
            if (null != jsonNodeField) {
                return jsonNodeField.asLong();
            }
        }
        return 0L;
    }

    public static boolean readBooleanField(String content, String field) {
        JsonNode jsonNode = readTree(content);
        if (null != jsonNode) {
            JsonNode jsonNodeField = jsonNode.get(field);
            if (null != jsonNodeField) {
                return jsonNodeField.asBoolean();
            }
        }
        return false;
    }
}
