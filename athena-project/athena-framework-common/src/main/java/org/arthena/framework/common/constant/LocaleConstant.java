package org.arthena.framework.common.constant;

import lombok.Data;

/**
 * @author zhouzhitong
 * @since 2023/5/29
 */
public interface LocaleConstant {

    /**
     * 默认语言环境: 中文
     */
    String DEFAULT_LOCALE = "zh_CN";

    /**
     * 中文
     */
    String ZH = "zh_CN";

    /**
     * 英文
     */
    String EN = "en_US";

    /**
     * 日文
     */
    String JA = "ja_JP";

    /**
     * 韩文
     */
    String KO = "ko_KR";

    /**
     * 法文
     */
    String FR = "fr_FR";

    /**
     * 德文
     */
    String DE = "de_DE";



    @Data
    class LocaleType {

        private String locale;

        private String name;

        public LocaleType(String locale, String name) {
            this.locale = locale;
            this.name = name;
        }

    }

}
