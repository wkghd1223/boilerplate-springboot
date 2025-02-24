package com.example.boilerplatespringboot.config.advice;

import jakarta.servlet.http.HttpServletRequest;
import com.example.boilerplatespringboot.common.dto.ApiResponseDto;
import com.example.boilerplatespringboot.common.enums.ApiStatus;
import com.example.boilerplatespringboot.common.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class ExceptionAdvice {

    /**
     * Exception
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDto.ErrorResponse> handleException(
        HttpServletRequest request,
        Exception e) {
        log.error("handleException : ", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .contentType(MediaType.APPLICATION_JSON)
            .body(ApiResponseDto.ErrorResponse.builder()
                .status(ApiStatus.INTERNAL_SERVER_ERROR.getCode())
                .build()
        );
    }

    /**
     * Custom Exception
     */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponseDto.ErrorResponse> handleApiException(
        HttpServletRequest request,
        ApiException e) {
        log.error("handleApiException : ", e);
        return ResponseEntity.status(e.getHttpStatus()).body(
            ApiResponseDto.ErrorResponse.builder()
                .status(e.getStatus().getCode())
                .message(e.getMessage())
                .build()
        );
    }

    /**
     * Exception
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponseDto.ErrorResponse> handle404Exception(
        HttpServletRequest request,
        Exception e) {
        log.error("handleException : ", e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            ApiResponseDto.ErrorResponse.builder()
                .status(ApiStatus.NO_HANDLER_FOUND.getCode())
                .build()
        );
    }

    /**
     * Handle Bad Request Exception - MethodArgumentNotValidException
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDto.ErrorResponse> handleMethodArgumentNotValidException(
        HttpServletRequest request,
        MethodArgumentNotValidException e) {
        log.error("handleMethodArgumentNotValidException : ", e);

        String message = "";

        List<String> messages = new ArrayList<>();
        BindingResult bindingResult = e.getBindingResult();
        for (ObjectError error : bindingResult.getGlobalErrors()) {
            messages.add(error.getObjectName() + ":" + error.getDefaultMessage());
        }

        for (FieldError error : bindingResult.getFieldErrors()) {
            messages.add(error.getField() + ":" + error.getDefaultMessage());
        }

        if (!ObjectUtils.isEmpty(messages)) {
            message = String.join(", ", messages);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ApiResponseDto.ErrorResponse.builder()
                .status(ApiStatus.METHOD_ARGUMENT_NOT_VALID.getCode())
                .message(message)
                .build()
        );
    }

    /**
     * Handle Bad Request Exception - MissingServletRequestParameterException
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponseDto.ErrorResponse> handleMissingServletRequestParameterException(
        HttpServletRequest request,
        MissingServletRequestParameterException e) {
        log.error("handleMissingServletRequestParameterException : ", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ApiResponseDto.ErrorResponse.builder()
                .status(ApiStatus.MISSING_SERVLET_REQUEST_PARAMETER.getCode())
                .build()
        );
    }

//    /**
//     * Handle Bad Request Exception - ConstraintViolationException
//     */
//    @ExceptionHandler(ConstraintViolationException.class)
//    public ResponseEntity<ApiResponseDto.ErrorResponse> handleConstraintViolationException(
//        HttpServletRequest request,
//        ConstraintViolationException e) {
//        log.error("handleConstraintViolationException : ", e);
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
//            ApiResponseDto.ErrorResponse.builder()
//                .status(ApiStatus.CONSTRAINT_VIOLATION.getCode())
//                .build()
//        );
//    }

    /**
     * Handle Bad Request Exception - MethodArgumentTypeMismatchException
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponseDto.ErrorResponse> handleMethodArgumentTypeMismatchException(
        HttpServletRequest request,
        MethodArgumentTypeMismatchException e) {
        log.error("handleMethodArgumentTypeMismatchException : ", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ApiResponseDto.ErrorResponse.builder()
                .status(ApiStatus.METHOD_ARGUMENT_TYPE_MISMATCH.getCode())
                .build()
        );
    }

    /**
     * Handle NoHandlerFoundException
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponseDto.ErrorResponse> handleNoHandlerFoundException(
        HttpServletRequest request,
        NoHandlerFoundException e) {
        log.error("handleNoHandlerFoundException : ", e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            ApiResponseDto.ErrorResponse.builder()
                .status(ApiStatus.NO_HANDLER_FOUND.getCode())
                .build()
        );
    }

    /**
     * Handle HttpRequestMethodNotSupportedException
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponseDto.ErrorResponse> handleHttpRequestMethodNotSupportedException(
        HttpServletRequest request,
        HttpRequestMethodNotSupportedException e) {
        log.error("handleHttpRequestMethodNotSupportedException : ", e);
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(
            ApiResponseDto.ErrorResponse.builder()
                .status(ApiStatus.HTTP_REQUEST_METHOD_NOT_SUPPORTED.getCode())
                .build()
        );
    }

    /**
     * Handle HttpMediaTypeNotSupportedException
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiResponseDto.ErrorResponse> handleHttpMediaTypeNotSupportedException(
        HttpServletRequest request,
        HttpMediaTypeNotSupportedException e) {
        log.error("handleHttpMediaTypeNotSupportedException : ", e);
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(
            ApiResponseDto.ErrorResponse.builder()
                .status(ApiStatus.HTTP_MEDIA_TYPE_NOT_SUPPORTED.getCode())
                .build()
        );
    }

    /**
     * Handle HttpMessageNotReadableException
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponseDto.ErrorResponse> handleHttpMessageNotReadableException(
        HttpServletRequest request,
        HttpMessageNotReadableException e) {
        log.error("handleHttpMessageNotReadableException : ", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ApiResponseDto.ErrorResponse.builder()
                .status(ApiStatus.HTTP_MESSAGE_NOT_READABLE_EXCEPTION.getCode())
                .build()
        );
    }
}
