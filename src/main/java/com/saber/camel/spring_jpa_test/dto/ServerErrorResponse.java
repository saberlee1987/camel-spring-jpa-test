package com.saber.camel.spring_jpa_test.dto;

import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.Data;

@Data
public class ServerErrorResponse {
    private Integer code;
    private String message;
    @JsonRawValue
    private String originalMessage;
}
