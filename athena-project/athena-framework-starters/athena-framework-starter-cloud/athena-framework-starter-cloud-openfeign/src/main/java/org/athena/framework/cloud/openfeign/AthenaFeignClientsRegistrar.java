package org.athena.framework.cloud.openfeign;

import feign.Request;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientFactoryBean;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 按配置数组扫描并注册 Feign 客户端。
 */
public class AthenaFeignClientsRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware {

    private ResourceLoader resourceLoader;

    private Environment environment;

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        registerFeignClients(registry);
    }

    private void registerFeignClients(BeanDefinitionRegistry registry) {
        Set<String> basePackages = resolveBasePackages();
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false, environment);
        scanner.setResourceLoader(resourceLoader);
        scanner.addIncludeFilter(new AnnotationTypeFilter(FeignClient.class));

        for (String basePackage : basePackages) {
            for (BeanDefinition candidateComponent : scanner.findCandidateComponents(basePackage)) {
                if (!(candidateComponent instanceof AnnotatedBeanDefinition beanDefinition)) {
                    continue;
                }
                AnnotationMetadata annotationMetadata = beanDefinition.getMetadata();
                Assert.isTrue(annotationMetadata.isInterface(), "@FeignClient can only be specified on an interface");
                Map<String, Object> attributes = annotationMetadata.getAnnotationAttributes(FeignClient.class.getCanonicalName());
                if (attributes == null) {
                    continue;
                }
                registerFeignClient(registry, annotationMetadata, attributes);
            }
        }
    }

    private Set<String> resolveBasePackages() {
        String[] configuredPackages = Binder.get(environment)
            .bind("athena.cloud.openfeign.base-packages", String[].class)
            .orElse(new String[]{"org.athena"});
        Set<String> basePackages = new HashSet<>();
        if (configuredPackages != null) {
            Arrays.stream(configuredPackages)
                .filter(StringUtils::hasText)
                .map(String::trim)
                .forEach(basePackages::add);
        }
        if (basePackages.isEmpty()) {
            basePackages.add("org.athena");
        }
        return basePackages;
    }

    private void registerFeignClient(BeanDefinitionRegistry registry,
                                     AnnotationMetadata annotationMetadata,
                                     Map<String, Object> attributes) {
        String className = annotationMetadata.getClassName();
        validate(attributes);

        BeanDefinitionBuilder definition = BeanDefinitionBuilder.genericBeanDefinition(FeignClientFactoryBean.class);
        definition.addPropertyValue("url", resolveString((String) attributes.get("url")));
        definition.addPropertyValue("path", resolveString((String) attributes.get("path")));
        definition.addPropertyValue("name", getName(attributes));
        definition.addPropertyValue("contextId", getContextId(attributes));
        definition.addPropertyValue("type", resolveClass(className));
        definition.addPropertyValue("dismiss404", Boolean.parseBoolean(String.valueOf(attributes.get("dismiss404"))));
        definition.addPropertyValue("inheritParentContext",
            Boolean.parseBoolean(String.valueOf(attributes.get("inheritParentContext"))));
        definition.addPropertyValue("refreshableClient", isClientRefreshEnabled());

        Object fallback = attributes.get("fallback");
        if (fallback != null) {
            definition.addPropertyValue("fallback", fallback instanceof Class ? fallback : resolveClass(fallback.toString()));
        }
        Object fallbackFactory = attributes.get("fallbackFactory");
        if (fallbackFactory != null) {
            definition.addPropertyValue("fallbackFactory",
                fallbackFactory instanceof Class ? fallbackFactory : resolveClass(fallbackFactory.toString()));
        }

        String[] qualifiers = getQualifiers(attributes);
        if (ObjectUtils.isEmpty(qualifiers)) {
            qualifiers = new String[]{getContextId(attributes) + "FeignClient"};
        }
        definition.addPropertyValue("qualifiers", qualifiers);

        AbstractBeanDefinition beanDefinition = definition.getBeanDefinition();
        beanDefinition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
        beanDefinition.setPrimary(Boolean.parseBoolean(String.valueOf(attributes.get("primary"))));
        beanDefinition.setAttribute(FactoryBean.OBJECT_TYPE_ATTRIBUTE, resolveClass(className));

        BeanDefinitionHolder holder = new BeanDefinitionHolder(beanDefinition, className, qualifiers);
        BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
    }

    private void validate(Map<String, Object> attributes) {
        Object fallback = attributes.get("fallback");
        if (fallback instanceof Class<?> fallbackClass) {
            Assert.isTrue(!fallbackClass.isInterface(), "Fallback class must implement the interface annotated by @FeignClient");
        }
        Object fallbackFactory = attributes.get("fallbackFactory");
        if (fallbackFactory instanceof Class<?> fallbackFactoryClass) {
            Assert.isTrue(!fallbackFactoryClass.isInterface(),
                "Fallback factory must produce instances of fallback classes that implement the interface annotated by @FeignClient");
        }
    }

    private Class<?> resolveClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException ex) {
            throw new IllegalStateException("Cannot resolve class: " + className, ex);
        }
    }

    private String getName(Map<String, Object> attributes) {
        String name = resolveString((String) attributes.get("serviceId"));
        if (!StringUtils.hasText(name)) {
            name = resolveString((String) attributes.get("name"));
        }
        if (!StringUtils.hasText(name)) {
            name = resolveString((String) attributes.get("value"));
        }
        if (!StringUtils.hasText(name)) {
            throw new IllegalStateException("Either 'name' or 'value' must be provided in @FeignClient");
        }
        return name;
    }

    private String getContextId(Map<String, Object> attributes) {
        String contextId = resolveString((String) attributes.get("contextId"));
        return StringUtils.hasText(contextId) ? contextId : getName(attributes);
    }

    private String resolveString(String value) {
        if (!StringUtils.hasText(value)) {
            return value;
        }
        if (environment == null) {
            return value;
        }
        return environment.resolvePlaceholders(value);
    }

    private String[] getQualifiers(Map<String, Object> attributes) {
        Object qualifiersAttr = attributes.get("qualifiers");
        if (qualifiersAttr instanceof String[] qualifiers) {
            return Arrays.stream(qualifiers).filter(StringUtils::hasText).toArray(String[]::new);
        }
        Object qualifier = attributes.get("qualifier");
        if (qualifier instanceof String qualifierValue && StringUtils.hasText(qualifierValue)) {
            return new String[]{qualifierValue};
        }
        return null;
    }

    private boolean isClientRefreshEnabled() {
        return environment != null
            && environment.getProperty("spring.cloud.openfeign.client.refresh-enabled", Boolean.class, false);
    }
}
