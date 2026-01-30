package com.soilmate.common.response;

import com.soilmate.common.enums.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResponse<T> {

    private Object code;
    private String message;
    private T data;

    public static ApiResponse<?> error(ErrorCode errorCode){
        return new ApiResponse<>(errorCode.getCode(),errorCode.getMessage(),null);
    }

    public static <T> ApiResponse<T> success(T data, String message){
        return new ApiResponse<>(200, message, data);
    }

    public static <T> ApiResponse<T> success(T data){
        return new ApiResponse<>(200, "Success", data);
    }

    public static <T> ApiResponse<T> created (T data, String message){
        return new ApiResponse<>(201, message, data);
    }

}
