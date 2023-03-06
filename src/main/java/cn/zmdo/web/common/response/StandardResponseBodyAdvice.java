package cn.zmdo.web.common.response;

import cn.zmdo.web.common.exception.IStatusCodeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 标准响应结果处理
 */
@RestControllerAdvice
public class StandardResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    @Autowired
    private ResponseBodyHandlerManager responseBodyHandlerManager;

    @Override
    public boolean supports(
            MethodParameter returnType,
            Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response) {

        if (body instanceof R) {

            R<?> result;

            // 对返回值进行处理
            if (responseBodyHandlerManager != null) {
                // 如果是正常返回值就正常返回
                result =  responseBodyHandlerManager.handle((R<?>)body);
            } else {
                result = ((R<?>) body).clone();
            }

            // 设置前缀
            if (result.getMessagePrefix() != null) {
                result.setMessage(
                        result.getMessagePrefix() + result.getMessage()
                );
            }

            return result;
        } else if (body instanceof IStatusCodeException) {
            // 如果是可预测的服务器异常，那么就返回异常的错误代码
            return new R<>(
                    ((IStatusCodeException) body ).getCode(),
                    StandardCode.SERVER_ERROR_MESSAGE
            );
        } else {
            // 如果不是正常的返回结果，就认为发生了异常，
            // 进行对异常的统一异常处理
            return new R<>(
                    StandardCode.SERVER_ERROR,
                    StandardCode.SERVER_ERROR_MESSAGE
            );
        }
    }

}
