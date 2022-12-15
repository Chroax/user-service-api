package com.binar.userservice.dto;

import com.binar.userservice.services.impl.security.UserDetailsImpl;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponse {
    private String token;
    private String type;
    private UUID userId;
    private String name;
    private String email;
    private String telephone;
    private String message;

    public static LoginResponse build(String jwt, UserDetailsImpl userDetails) {
        return LoginResponse.builder()
                .token(jwt)
                .type("Bearer")
                .userId(userDetails.getUserId())
                .name(userDetails.getName())
                .email(userDetails.getEmail())
                .telephone(userDetails.getTelephone())
                .build();
    }
}
