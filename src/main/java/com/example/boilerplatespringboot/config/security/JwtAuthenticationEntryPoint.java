package com.example.boilerplatespringboot.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.example.boilerplatespringboot.common.dto.ApiResponseDto;
import com.example.boilerplatespringboot.common.enums.ApiStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.example.boilerplatespringboot.common.Constants.EXCEPTION_CODE;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        // 유효한 자격증명을 제공하지 않고 접근하려 할때 401
        Integer code = (Integer) request.getAttribute(EXCEPTION_CODE);
        ApiStatus status = code == null ? ApiStatus.UNAUTHORIZED : ApiStatus.valueOfStatusCode(code);
        ObjectMapper objectMapper = new ObjectMapper();
        ApiResponseDto.ErrorResponse errorResponse = ApiResponseDto.ErrorResponse.builder()
            .status(status.getCode())
            .message(status.getMessage())
            .build();
        response.setContentType("application/json; charset=utf-8");

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        response.getWriter().flush();
        response.getWriter().close();
    }
}