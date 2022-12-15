package com.binar.userservice.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.List;

@Data
public class UserUpdateRequest {

    private String name;

    @Email(message = "Please provide a valid email address")
    private String email;

    @Pattern(regexp = "^\\d*$",message = "The phone number is invalid.")
    private String phoneNumber;

    private List<Integer> rolesId;
}
