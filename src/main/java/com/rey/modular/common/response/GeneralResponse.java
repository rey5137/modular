package com.rey.modular.common.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//public class GeneralResponse<T> {
//
//    public static final String CODE_SUCCESS = "success";
//    public static final String MESSAGE_SUCCESS = "Success";
//
//    private T data;
//    private Status status;
//
//    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
//    @Getter
//    @Setter
//    @NoArgsConstructor
//    @AllArgsConstructor
//    public static class Status{
//        private String code;
//        private String message;
//    }
//
//    public static <T> GeneralResponse<T> success(T data) {
//        return new GeneralResponse<>(data, new Status(CODE_SUCCESS, MESSAGE_SUCCESS));
//    }
//
//    public static <T> GeneralResponse<T> error(String code, String message) {
//        return new GeneralResponse<>(null, new Status(code, message));
//    }
//
//}


@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record GeneralResponse<T>(
        T data,
        Status status
) {

    public static final String CODE_SUCCESS = "success";
    public static final String MESSAGE_SUCCESS = "Success";

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record Status(
            String code,
            String message
    ) {
    }

    public static <T> GeneralResponse<T> success(T data) {
        return new GeneralResponse<>(data, new Status(CODE_SUCCESS, MESSAGE_SUCCESS));
    }

    public static <T> GeneralResponse<T> error(String code, String message) {
        return new GeneralResponse<>(null, new Status(code, message));
    }
}
