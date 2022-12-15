package com.binar.userservice.dto;

import javax.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class UserRequest {

    @NotEmpty(message = "Name is required.")
    private String name;

    @NotEmpty(message = "Email is required.")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotEmpty(message = "Password is required.")
    private String password;

    @NotEmpty(message = "Phone number is required.")
    @Pattern(regexp = "^\\d*$",message = "The phone number is invalid.")
    private String phoneNumber;

    @NotEmpty(message = "Roles is required.")
    private List<Integer> rolesId;

    @NotEmpty(message = "Status is required.")
    private Boolean status = true;
}
