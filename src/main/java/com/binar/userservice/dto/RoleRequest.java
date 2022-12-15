package com.binar.userservice.dto;

import com.binar.userservice.model.Roles;
import javax.validation.constraints.*;
import lombok.Data;

@Data
public class RoleRequest {

    @NotEmpty(message = "Role name is required.")
    private String roleName;

    public Roles toRoles() {
        return Roles.builder()
                .roleName(this.roleName)
                .build();
    }
}
