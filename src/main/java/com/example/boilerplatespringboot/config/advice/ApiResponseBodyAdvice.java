package com.example.boilerplatespringboot.config.advice;

import com.example.boilerplatespringboot.common.dto.ApiResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * The type Api response body advice.
 */
@Slf4j
@ControllerAdvice
@SuppressWarnings("PMD")
public class ApiResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType,
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
            ServerHttpResponse response
    ) {
        ApiResponseDto.Base<?> res;
        String uri = request.getURI().getPath();
        if (body instanceof ApiResponseDto.Base<?>) {
            res = (ApiResponseDto.Base<?>) body;
        } else if (body == null) {
            res = new ApiResponseDto.Base<Map<String, Object>>(new HashMap<>());
        } else if (body instanceof byte[]
                || selectedConverterType.equals(StringHttpMessageConverter.class)
                || body instanceof ApiResponseDto.ErrorResponse
                || uri.contains("/swagger-config")
        ) {
            return body;
//        } else if (uri.contains("/api/error") && body instanceof LinkedHashMap<?,?>) {
//            return ApiResponseDto.ErrorResponse.builder()
//                .method(request.getMethod().toString())
//                .status(((LinkedHashMap<String, Integer>) body).get("status"))
//                .path(((LinkedHashMap<String, String>) body).get("path"))
//                .message(((LinkedHashMap<String, String>) body).get("error"))
//                .timestamp(new SimpleDateFormat(YYYYMMDDHHMMSS).format(((LinkedHashMap<String, String>) body).get("timestamp")))
//                .build();
        } else {
            res = new ApiResponseDto.Base<>(body);
        }
        return res;
    }
}
