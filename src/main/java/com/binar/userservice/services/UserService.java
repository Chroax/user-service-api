package com.binar.userservice.services;

import com.binar.userservice.dto.UserRequest;
import com.binar.userservice.dto.UserResponse;
import com.binar.userservice.dto.UserUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface UserService {

    UserResponse registerUser(UserRequest userRequest);
    UserResponse updateUser(UserUpdateRequest userUpdateRequest, UUID userId);
    Boolean deleteUser(UUID userId);
    UserResponse searchUserById(UUID userId);
    UserResponse searchUserByEmail(String email);
    List<UserResponse> searchAllUser();
    List<UserResponse> searchUserByName(String name);
    Boolean isUserExist(String email);
    Boolean isPhoneNumberExist(String phoneNumber);
}
