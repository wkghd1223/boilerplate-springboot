package com.example.boilerplatespringboot.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.example.boilerplatespringboot.common.dto.ApiResponseDto;
import com.example.boilerplatespringboot.common.enums.ApiStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.example.boilerplatespringboot.common.Constants.EXCEPTION_CODE;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        Integer code = (Integer) request.getAttribute(EXCEPTION_CODE);
        ApiStatus status = code == null ? ApiStatus.FORBIDDEN_REQUEST : ApiStatus.valueOfStatusCode(code);
        // 필요한 권한이 없이 접근하려 할때 403
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