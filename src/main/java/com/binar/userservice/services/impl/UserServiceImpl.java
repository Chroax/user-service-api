package com.binar.userservice.services.impl;

import com.binar.userservice.dto.UserRequest;
import com.binar.userservice.dto.UserResponse;
import com.binar.userservice.dto.UserUpdateRequest;
import com.binar.userservice.exception.NotFoundException;
import com.binar.userservice.model.Roles;
import com.binar.userservice.model.Users;
import com.binar.userservice.repository.RoleRepository;
import com.binar.userservice.repository.UserRepository;
import com.binar.userservice.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Override
    public UserResponse registerUser(UserRequest userRequest) {
        String message;

        if(Boolean.FALSE.equals(isUserExist(userRequest.getEmail())))
        {
            if(Boolean.FALSE.equals(isPhoneNumberExist(userRequest.getPhoneNumber())))
            {
                try {
                    List<Integer> allRolesId = new ArrayList<>();
                    List<Roles> allRoles = new ArrayList<>();
                    for (Integer roleId: userRequest.getRolesId()) {
                        Optional<Roles> roles = roleRepository.findById(roleId);
                        if(roles.isPresent())
                        {
                            allRolesId.add(roleId);
                            allRoles.add(roles.get());
                        }
                        else
                            return UserResponse.builder()
                                    .message("Roles id not exist")
                                    .build();
                    }

                    Users users = Users.builder()
                            .name(userRequest.getName())
                            .email(userRequest.getEmail())
                            .password(userRequest.getPassword())
                            .phoneNumber(userRequest.getPhoneNumber())
                            .rolesUsers(allRoles)
                            .status(userRequest.getStatus())
                            .build();

                    users.setPassword(encoder.encode(users.getPassword()));

                    userRepository.saveAndFlush(users);
                    return UserResponse.builder()
                            .userId(users.getUserId())
                            .name(users.getName())
                            .email(users.getEmail())
                            .phoneNumber(users.getPhoneNumber())
                            .rolesId(allRolesId)
                            .build();
                }
                catch (Exception ignore){
                    return UserResponse.builder()
                            .message("Create user failed")
                            .build();
                }
            }
            else
                message = "Phone number already exist";
        }
        else
            message = "Email already exist";
        return UserResponse.builder()
                .message(message)
                .build();
    }

    @Override
    public UserResponse updateUser(UserUpdateRequest userUpdateRequest, UUID userId) {
        Optional<Users> isUser = userRepository.findById(userId);
        String message = null;
        if (isUser.isPresent()) {
            Users users = isUser.get();
            if (userUpdateRequest.getName() != null)
                users.setName(userUpdateRequest.getName());
            if (userUpdateRequest.getEmail() != null)
            {
                if(Boolean.FALSE.equals(isUserExist(userUpdateRequest.getEmail())))
                    users.setEmail(userUpdateRequest.getEmail());
                else
                    message = "Email already exist";
            }
            if (userUpdateRequest.getPhoneNumber() != null)
            {
                if(Boolean.FALSE.equals(isPhoneNumberExist(userUpdateRequest.getPhoneNumber())))
                    users.setPhoneNumber(userUpdateRequest.getPhoneNumber());
                else
                    message = "Phone number already exist";
            }
            if (userUpdateRequest.getRolesId() != null)
            {
                List<Roles> allRoles = new ArrayList<>();
                for (Integer roleId: userUpdateRequest.getRolesId()) {
                    Optional<Roles> roles = roleRepository.findById(roleId);
                    if(roles.isPresent())
                    {
                        allRoles.add(roles.get());
                    }
                    else
                        return UserResponse.builder()
                                .message("Roles id not exist")
                                .build();
                }
                users.setRolesUsers(allRoles);
            }
            userRepository.saveAndFlush(users);
            return UserResponse.builder()
                    .userId(users.getUserId())
                    .name(users.getName())
                    .email(users.getEmail())
                    .phoneNumber(users.getPhoneNumber())
                    .message(message)
                    .build();
        } else {
            throw new NotFoundException("User with id: " + userId + " not found");
        }
    }

    @Override
    public Boolean deleteUser(UUID userId) {
        Optional<Users> checkUser = userRepository.findById(userId);
        if(checkUser.isPresent())
        {
            Users users = checkUser.get();
            users.setStatus(false);
            userRepository.saveAndFlush(users);
            return true;
        }
        else
            return false;
    }

    @Override
    public UserResponse searchUserById(UUID userId) {
        Optional<Users> isUsers = userRepository.findById(userId);
        if (isUsers.isEmpty()) {
            throw new NotFoundException("User with id: " + userId + " not found");
        } else {
            Users users = isUsers.get();
            return UserResponse.builder()
                    .userId(users.getUserId())
                    .name(users.getName())
                    .email(users.getEmail())
                    .phoneNumber(users.getPhoneNumber())
                    .build();
        }
    }

    @Override
    public UserResponse searchUserByEmail(String email) {
        Optional<Users> isUsers = userRepository.findByEmail(email);
        if (isUsers.isEmpty()) {
            throw new NotFoundException("User with email: " + email + " not found");
        } else {
            Users users = isUsers.get();
            return UserResponse.builder()
                    .userId(users.getUserId())
                    .name(users.getName())
                    .email(users.getEmail())
                    .phoneNumber(users.getPhoneNumber())
                    .build();
        }
    }

    @Override
    public List<UserResponse> searchAllUser() {
        List<Users> allUser = userRepository.findAll();
        return toListUserResponses(allUser);
    }

    @Override
    public List<UserResponse> searchUserByName(String name) {
        List<Users> allUser = userRepository.findByName(name);
        return toListUserResponses(allUser);
    }

    @Override
    public Boolean isUserExist(String email) {
        Optional<Users> users = userRepository.findByEmail(email);
        return users.isPresent();
    }

    @Override
    public Boolean isPhoneNumberExist(String phoneNumber) {
        Users users = userRepository.findPhoneNumber(phoneNumber);
        return users != null;
    }

    private List<UserResponse> toListUserResponses(List<Users> allUser) {
        List<UserResponse> allUserResponse = new ArrayList<>();
        for (Users user : allUser) {
            UserResponse userResponse = UserResponse.builder()
                    .userId(user.getUserId())
                    .name(user.getName())
                    .email(user.getEmail())
                    .phoneNumber(user.getPhoneNumber())
                    .build();
            allUserResponse.add(userResponse);
        }
        return allUserResponse;
    }
}
