package com.binar.userservice.dto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageModel
{
    private Integer status;
    private String message;
    private Object data;
}
