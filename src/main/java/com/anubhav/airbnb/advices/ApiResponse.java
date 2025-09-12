package com.anubhav.airbnb.advices;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T>
{

    private HttpStatus status;
    private List<String> subErrors;
    private boolean success;
    private String message;
    private T data;

    @JsonFormat(pattern = "hh:mm:ss dd-MM-yyyy")
    private LocalDateTime timestamp;

    // Generic success constructor
    public ApiResponse(boolean success, String message)
    {
        this.timestamp = LocalDateTime.now();
        this.success = success;
        this.message = message;
        this.status = HttpStatus.OK;
    }

    // Success + data constructor
    public ApiResponse(T data)
    {
        this(true, "Request successful");
        this.data = data;
    }

    // Validation errors constructor
    public ApiResponse(String message, HttpStatus status, List<String> subErrors)
    {
        this.timestamp = LocalDateTime.now();
        this.success = false;
        this.message = message;
        this.status = status;
        this.subErrors = subErrors;
    }
}
