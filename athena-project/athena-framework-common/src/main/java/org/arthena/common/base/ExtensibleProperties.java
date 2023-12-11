package org.arthena.common.base;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.MoreObjects;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 可扩展的属性集合
 * <p>
 * <pre>
 * {@code
 *  @Data
 *  @ToString(callSuper = true)
 *  public static class A extends ExtensibleProperties {
 *      String name;
 *  }
 *  public static void main(String[] args) throws JsonProcessingException {
 *      A a = new A();
 *      a.setName("name1");
 *      a.setExtraProperty("a", "b");
 *      System.out.println(JacksonJsonUtils.writeValueAsString(a));
 *      // {"name":"name1","a":"b"}
 *
 *      A a1 = JacksonJsonUtils.readValue(JacksonJsonUtils.writeValueAsString(a), A.class);
 *      System.out.println(a1);
 *      // ExtensibleProperties.A(super=A{extraProperties={a=b}}, name=name1)
 *  }
 * }
 * </pre>
 *
 * @author zhouzhitong
 */
public class ExtensibleProperties implements Serializable {

    @Serial
    private static final long serialVersionUID = -1575616725236867052L;

    @JsonIgnore
    private final HashMap<String, Object> extraProperties = new HashMap<>();

    @JsonAnyGetter
    public Map<String, Object> extraProperties() {
        return extraProperties;
    }

    @JsonAnySetter
    public void setExtraProperty(String name, Object value) {
        this.extraProperties.put(name, value);
    }

    public Object getExtraProperty(String name) {
        return this.extraProperties.get(name);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("extraProperties", extraProperties)
                .toString();
    }
}
