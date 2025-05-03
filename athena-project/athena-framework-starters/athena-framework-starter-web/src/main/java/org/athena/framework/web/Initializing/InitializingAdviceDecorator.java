package org.athena.framework.web.Initializing;

import jakarta.servlet.ServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.athena.framework.web.annotation.IgnoredResultWrapper;
import org.arthena.framework.common.vo.ResultVO;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 业务: 包装 controller 返回值的拦截器. 用于统一返回值格式: new ResultVO<>(data);
 *
 * @author zhouzhitong
 * @since 2023-12-12
 **/
@Component
@Slf4j
public class InitializingAdviceDecorator implements InitializingBean {
    private final RequestMappingHandlerAdapter adapter;

    @Autowired
    public InitializingAdviceDecorator(RequestMappingHandlerAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //获取所有的handler对象
        var returnValueHandlers = adapter.getReturnValueHandlers();
        //因为上面返回的是unmodifiableList，所以需要新建list处理
        assert returnValueHandlers != null;
        List<HandlerMethodReturnValueHandler> handlers = new ArrayList<>(returnValueHandlers);
        this.decorateHandlers(handlers);
        //将增强的返回值回写回去
        adapter.setReturnValueHandlers(handlers);
    }

    /**
     * 使用自定义的返回值控制类
     *
     * @param handlers
     */
    private void decorateHandlers(List<HandlerMethodReturnValueHandler> handlers) {
        for (HandlerMethodReturnValueHandler handler : handlers) {
            if (handler instanceof RequestResponseBodyMethodProcessor) {
                //找到返回值的handler并将起包装成自定义的handler
                ControllerReturnValueHandler decorator = new ControllerReturnValueHandler((RequestResponseBodyMethodProcessor) handler);
                int index = handlers.indexOf(handler);
                handlers.set(index, decorator);
                break;
            }
        }
    }

    /**
     * 自定义返回值的Handler
     * 采用装饰者模式
     */
    private static class ControllerReturnValueHandler implements HandlerMethodReturnValueHandler {
        //持有一个被装饰者对象
        private final HandlerMethodReturnValueHandler handler;

        ControllerReturnValueHandler(RequestResponseBodyMethodProcessor handler) {
            this.handler = handler;
        }

        @Override
        public boolean supportsReturnType(MethodParameter returnType) {
            return true;
        }

        /**
         * 增强被装饰者的功能
         *
         * @param returnValue  返回值
         * @param returnType   返回类型
         * @param mavContainer view
         * @param webRequest   请求对象
         * @throws Exception 抛出异常
         */
        @Override
        public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
            LOGGER.trace("method-handleReturnValue: {} - {}", Objects.requireNonNull(returnType.getMethod()).getName()
                    , returnValue);

            //如果是下载文件跳过包装
            IgnoredResultWrapper ignoredResultWrapper = returnType.getMethodAnnotation(IgnoredResultWrapper.class);

            if (ignoredResultWrapper != null) {
                handler.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
                return;
            }

            if (returnValue == null) {
                Optional<String> contentType = Optional.of(webRequest)
                        .map(nativeWebRequest -> ((ServletWebRequest) webRequest))
                        .map(ServletRequestAttributes::getResponse)
                        .map(ServletResponse::getContentType);
                if (contentType.isPresent() && contentType.get().contains("application/vnd.openxmlformats-officedocument")) {
                    return;
                }
            }
            //如果已经封装了结构体就直接放行
            if (returnValue instanceof ResultVO) {
                handler.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
                return;
            }
            //正常返回success
            ResultVO<Object> success = ResultVO.success(returnValue);
            handler.handleReturnValue(success, returnType, mavContainer, webRequest);
        }
    }

}
