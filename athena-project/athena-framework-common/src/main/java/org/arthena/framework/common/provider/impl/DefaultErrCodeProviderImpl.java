package org.arthena.framework.common.provider.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.arthena.framework.common.constant.ErrCodeConstant;
import org.arthena.framework.common.context.SystemContext;
import org.arthena.framework.common.properties.CommonProperties;
import org.arthena.framework.common.provider.ErrCodeProvider;
import org.arthena.framework.common.service.ErrorCodeService;
import org.arthena.framework.common.utils.PropertiesUtils;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认错误码提供器实现，负责根据错误码和当前上下文语言返回可读错误信息。
 *
 * <p>核心职责：
 * <ul>
 *     <li>按语言环境加载错误码资源文件（properties）并提供错误文案查询能力。</li>
 *     <li>支持“自定义错误码文件优先、默认错误码文件兜底”的两级查找策略。</li>
 *     <li>支持错误文案中的 {@code {}} 占位符替换，用于动态参数拼接。</li>
 *     <li>通过内存缓存减少重复 IO，提升高频查询场景下的性能。</li>
 * </ul>
 *
 * <p>加载与查找顺序：
 * <ol>
 *     <li>解析语言：从 {@link SystemContext#getLocale()} 获取 locale，默认使用 {@code zh}。</li>
 *     <li>先查外部自定义文件：{@code {errCodePath}/ErrorCode-{locale}.properties}（默认 {@code ./config}）。</li>
 *     <li>再查 classpath 自定义文件：{@code config/ErrorCode-{locale}.properties}。</li>
 *     <li>若未命中再查默认文件：{@code ErrorCode-{locale}.properties}。</li>
 *     <li>仍未命中时返回 {@link ErrCodeConstant#UN_KNOW_ERROR_MSG}。</li>
 * </ol>
 *
 * <p>缓存与并发说明：
 * <ul>
 *     <li>使用 {@link ConcurrentHashMap} 以语言维度缓存 {@link Properties}。</li>
 *     <li>文件首次加载时使用 {@code synchronized(key.intern())} 做细粒度同步，避免并发重复加载。</li>
 *     <li>{@link #reload()} 可清空缓存，触发后续请求重新从资源加载最新内容。</li>
 * </ul>
 *
 * <p>扩展建议（供后续开发）：
 * <ul>
 *     <li>若业务需要接入数据库/配置中心，可新增 {@link ErrCodeProvider}
 *     实现，并在应用中注册为 Bean，替代当前默认实现。</li>
 *     <li>若仅需扩展文案，优先通过维护自定义 properties 文件实现，不建议直接改查询逻辑。</li>
 *     <li>若需要区域化（如 {@code zh_CN}/{@code zh_TW}）精细控制，可扩展 {@link #resolveLocale()} 解析策略。</li>
 *     <li>若占位符规则需升级（命名参数、格式化模板），建议在 {@link #replacePlaceholders(String, Object[])}
 *     做向后兼容扩展，避免影响已有调用方。</li>
 * </ul>
 *
 * @author zhouzhitong
 * @since 2026/5/30
 */
@Slf4j
@Service
public class DefaultErrCodeProviderImpl implements ErrCodeProvider, ErrorCodeService {

    public static final String CUSTOM = "CUSTOM_";

    public static final String ERROR_CODE_PREFIX = "ErrorCode-";

    public static final String CUSTOM_ERROR_CODE_FILE = "config/ErrorCode-";

    public static final String FILE_TYPE = ".properties";

    public static final String CODE_MSG_PARAM_PLACEHOLDER = "{}";

    private final String errCodePath;

    private final Map<String, Properties> errorCodeMap = new ConcurrentHashMap<>();

    public DefaultErrCodeProviderImpl(CommonProperties commonProperties) {
        this.errCodePath = commonProperties == null ? "config" : commonProperties.getErrCodePath();
    }

    @Override
    public String getMsg(int code, Object[] args) {
        String msg = findMessage(code, resolveLocale());
        if (msg == null) {
            msg = ErrCodeConstant.UN_KNOW_ERROR_MSG;
        }
        if (args == null || args.length == 0) {
            return msg;
        }
        return replacePlaceholders(msg, args);
    }

    @Override
    public int order() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    @Override
    public String getMsg(Integer code, String locale) {
        if (code == null) {
            return null;
        }
        return findMessage(code, normalizeLocale(locale));
    }

    @Override
    public void reload() {
        errorCodeMap.clear();
    }

    private String findMessage(int code, String locale) {
        try {
            String msg = getCustomMsg(code, locale);
            if (msg != null) {
                return msg;
            }
        } catch (Exception e) {
            LOGGER.error("加载 CUSTOM 错误码文件({})失败.", locale, e);
        }
        return getDefaultMsg(code, locale);
    }

    private String resolveLocale() {
        return normalizeLocale(SystemContext.getLocale());
    }

    private String normalizeLocale(String localeStr) {
        if (localeStr == null || localeStr.isBlank()) {
            return "zh";
        }
        int underscoreIndex = localeStr.indexOf("_");
        if (underscoreIndex <= 0) {
            return localeStr;
        }
        return localeStr.substring(0, underscoreIndex);
    }

    private String replacePlaceholders(String msg, Object[] args) {
        String result = msg;
        for (Object arg : args) {
            int placeholderIndex = result.indexOf(CODE_MSG_PARAM_PLACEHOLDER);
            if (placeholderIndex < 0) {
                break;
            }
            String replacement = arg == null ? "" : String.valueOf(arg);
            result = result.substring(0, placeholderIndex) + replacement
                    + result.substring(placeholderIndex + CODE_MSG_PARAM_PLACEHOLDER.length());
        }
        return result;
    }

    private String getDefaultMsg(int code, String locale) {
        String name = ERROR_CODE_PREFIX + locale + FILE_TYPE;
        Properties properties = loadErrorCode(locale, name);
        if (properties == null) {
            return null;
        }
        return properties.getProperty(String.valueOf(code));
    }

    private String getCustomMsg(int code, String locale) {
        String name = "ErrorCode-" + locale + FILE_TYPE;
        Properties properties = loadCustomErrorCode(locale, name);
        if (properties == null) {
            return null;
        }
        return properties.getProperty(String.valueOf(code));
    }

    private Properties loadCustomErrorCode(String locale, String fileName) {
        String key = CUSTOM + locale;
        Properties cachedProperties = errorCodeMap.get(key);
        if (cachedProperties != null) {
            return cachedProperties;
        }
        synchronized (key.intern()) {
            cachedProperties = errorCodeMap.get(key);
            if (cachedProperties != null) {
                return cachedProperties;
            }
            Properties merged = new Properties();
            // 先加载 classpath 自定义配置，再加载外部配置，确保外部同名 key 优先级更高。
            Properties classpathProperties = loadClasspathErrorCode(CUSTOM_ERROR_CODE_FILE + locale + FILE_TYPE);
            if (classpathProperties != null) {
                merged.putAll(classpathProperties);
            }
            Properties externalProperties = loadExternalErrorCode(fileName);
            if (externalProperties != null) {
                merged.putAll(externalProperties);
            }
            if (merged.isEmpty()) {
                return null;
            }
            errorCodeMap.put(key, merged);
            return merged;
        }
    }

    private Properties loadErrorCode(String key, String fileName) {
        Properties cachedProperties = errorCodeMap.get(key);
        if (cachedProperties != null) {
            return cachedProperties;
        }
        synchronized (key.intern()) {
            cachedProperties = errorCodeMap.get(key);
            if (cachedProperties != null) {
                return cachedProperties;
            }
            Properties properties = loadClasspathErrorCode(fileName);
            if (properties == null) {
                return null;
            }
            errorCodeMap.put(key, properties);
            return properties;
        }
    }

    private Properties loadClasspathErrorCode(String fileName) {
        try {
            return PropertiesUtils.loadAllProperties(fileName);
        } catch (Exception e) {
            LOGGER.error("加载 classpath 错误码文件({})失败.", fileName, e);
            return null;
        }
    }

    private Properties loadExternalErrorCode(String fileName) {
        String configuredPath = StringUtils.defaultIfBlank(errCodePath, "config");
        Path basePath = Paths.get(configuredPath);
        if (!basePath.isAbsolute()) {
            basePath = Paths.get(System.getProperty("user.dir")).resolve(basePath);
        }
        Path filePath = basePath.resolve(fileName);
        if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
            return null;
        }
        try (InputStream stream = Files.newInputStream(filePath);
             InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            Properties properties = new Properties();
            properties.load(reader);
            return properties;
        } catch (IOException e) {
            LOGGER.error("加载外部错误码文件({})失败.", filePath, e);
            return null;
        }
    }

}
