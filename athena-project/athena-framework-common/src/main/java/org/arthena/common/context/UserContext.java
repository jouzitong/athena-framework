package org.arthena.common.context;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author zhouzhitong
 * @since 2023-12-11
 **/
@Data
public class UserContext implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long userId;

    /**
     * 账户名
     */
    private String username;

    /**
     * 登录IP
     */
    private String ip;

    /**
     * 账户当前使用的语言 (默认中文)
     */
    private String lang = SystemContext.getDefaultLocale();

    /**
     * 账户登陆唯一标识
     */
    private String token;

    /**
     * 账户登陆时间
     */
    private LocalDateTime loginTime;

    public UserContext(String username) {
        this.username = username;
    }

    public void setLang(String lang) {
        if (StringUtils.isNotBlank(lang)) {
            this.lang = lang;
        }
    }

    public boolean isLoginUser() {
        return Objects.equals(SystemContext.DEFAULT_OPERATOR, getUserId());
    }


}
