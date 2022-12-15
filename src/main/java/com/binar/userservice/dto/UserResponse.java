package com.binar.userservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {

    private UUID userId;
    private String name;
    private String email;
    private String phoneNumber;
    private String message;
    private List<Integer> rolesId;
}
