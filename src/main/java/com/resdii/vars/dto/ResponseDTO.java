package com.resdii.vars.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Map;

@Data
@NoArgsConstructor
public class ResponseDTO<T> {
    String code;
    String subCode;
    String status;
    String message;
    T data;
}
