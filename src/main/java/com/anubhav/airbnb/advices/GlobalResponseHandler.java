package com.anubhav.airbnb.advices;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.springframework.http.ResponseEntity;

@RestControllerAdvice
public class GlobalResponseHandler implements ResponseBodyAdvice<Object>
{

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType)
    {
        return true; // sabhi endpoints par apply hoga
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response)
    {

        // If already an ApiResponse, don't wrap
        if (body instanceof ApiResponse)
        {
            return body;
        }

        HttpStatus status = HttpStatus.OK;  // default
        Object responseBody = body;

        // If controller returned ResponseEntity, unwrap it
        if (body instanceof ResponseEntity<?>)
        {
            ResponseEntity<?> entity = (ResponseEntity<?>) body;
            status = HttpStatus.valueOf(entity.getStatusCode().value());    // ✅ extract status
            responseBody = entity.getBody();   // unwrap actual data
        }

        ApiResponse<Object> apiResponse = new ApiResponse<>(responseBody);
        apiResponse.setStatus(status);

        return apiResponse;
    }

}

