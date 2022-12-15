package com.binar.userservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoleResponse {

    private Integer roleId;
    private String roleName;
    private String message;
}
