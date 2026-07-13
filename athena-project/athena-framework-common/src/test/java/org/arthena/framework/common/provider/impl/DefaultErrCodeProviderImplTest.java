package org.arthena.framework.common.provider.impl;

import org.arthena.framework.common.constant.ErrCodeConstant;
import org.arthena.framework.common.properties.CommonProperties;
import org.arthena.framework.common.service.ErrorCodeService;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.Ordered;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class DefaultErrCodeProviderImplTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void shouldLoadExternalMessagesBeforeClasspathDefaults() throws IOException {
        writeErrorCodes("zh", "1=外置错误文案\n53010001=参数缺失：消息不能为空\n");
        DefaultErrCodeProviderImpl provider = createProvider();

        assertEquals("外置错误文案", provider.getMsg(1, "zh"));
        assertEquals("参数缺失：消息不能为空", provider.getMsg(53010001, "zh_CN"));
        assertEquals("资源不存在", provider.getMsg(4, "zh"));
        assertEquals(Ordered.LOWEST_PRECEDENCE, provider.order());
    }

    @Test
    public void shouldReturnNullToTheServiceChainWhenCodeDoesNotExist() {
        DefaultErrCodeProviderImpl provider = createProvider();

        assertNull(provider.getMsg(987654321, "zh"));
        assertNull(provider.getMsg(null, "zh"));
        assertEquals(ErrCodeConstant.UN_KNOW_ERROR_MSG, provider.getMsg(987654321, new Object[0]));
    }

    @Test
    public void shouldReloadExternalErrorCodes() throws IOException {
        writeErrorCodes("en", "53010001=Original message\n");
        DefaultErrCodeProviderImpl provider = createProvider();

        assertEquals("Original message", provider.getMsg(53010001, "en"));
        writeErrorCodes("en", "53010001=Updated message\n");
        assertEquals("Original message", provider.getMsg(53010001, "en"));

        provider.reload();

        assertEquals("Updated message", provider.getMsg(53010001, "en"));
    }

    @Test
    public void shouldRegisterAsTheDefaultErrorCodeService() {
        CommonProperties properties = new CommonProperties();
        properties.setErrCodePath(temporaryFolder.getRoot().toPath().toString());

        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.registerBean(CommonProperties.class, () -> properties);
            context.scan(DefaultErrCodeProviderImpl.class.getPackageName());
            context.refresh();

            assertSame(
                    context.getBean(DefaultErrCodeProviderImpl.class),
                    context.getBean(ErrorCodeService.class)
            );
        }
    }

    private DefaultErrCodeProviderImpl createProvider() {
        CommonProperties properties = new CommonProperties();
        properties.setErrCodePath(temporaryFolder.getRoot().toPath().toString());
        return new DefaultErrCodeProviderImpl(properties);
    }

    private void writeErrorCodes(String locale, String content) throws IOException {
        Files.writeString(
                temporaryFolder.getRoot().toPath().resolve("ErrorCode-" + locale + ".properties"),
                content,
                StandardCharsets.UTF_8
        );
    }
}
