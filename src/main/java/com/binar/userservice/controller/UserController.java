package com.binar.userservice.controller;

import com.binar.userservice.dto.*;
import com.binar.userservice.security.JwtUtils;
import com.binar.userservice.services.UserService;
import com.binar.userservice.services.impl.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping(value = "/user", produces = {"application/json"})
public class UserController
{
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    JwtUtils jwtUtils;

    @Operation(responses = {
            @ApiResponse(responseCode = "200", content = @Content(examples = {
                    @ExampleObject(name = "Create User",
                            description = "Pastikan email valid, dan email hanya bisa didaftarkan 1 kali saja.",
                            value = """
                                      {
                                      "responseCode": 200,
                                      "responseMessage": "Register new user",
                                      "data": [
                                        {
                                          "user_id": "03aad5f0-5dda-11ed-9b6a-0242ac120002",
                                          "name": "cahyadi",
                                          "email": "cahyadisn6@gmail.com",
                                          "phone_number": "08121432478",
                                          "roles": [
                                            1
                                          ]
                                        }
                                      ]
                                    }""")
            }, mediaType = MediaType.APPLICATION_JSON_VALUE))})
    @PostMapping("/sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<MessageModel> registerUser(@RequestBody UserRequest userRequest) {
        MessageModel messageModel = new MessageModel();
        boolean isEmailValid = EmailValidator.getInstance().isValid(userRequest.getEmail());

        if(isEmailValid)
        {
            UserResponse userResponse = userService.registerUser(userRequest);

            if(userResponse.getMessage() != null)
            {
                messageModel.setStatus(HttpStatus.CONFLICT.value());
                messageModel.setMessage(userResponse.getMessage());
            }
            else
            {
                messageModel.setStatus(HttpStatus.OK.value());
                messageModel.setMessage("Register new user");
                messageModel.setData(userResponse);
            }
        }
        else
        {
            messageModel.setStatus(HttpStatus.BAD_REQUEST.value());
            messageModel.setMessage("Email is not valid");
        }

        return ResponseEntity.ok().body(messageModel);
    }

    @PostMapping("/sign-in")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<MessageModel> registerUser(@RequestBody LoginRequest loginRequest) {
        MessageModel messageModel = new MessageModel();

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        messageModel.setData(LoginResponse.build(jwt, userDetails));
        messageModel.setStatus(HttpStatus.OK.value());
        messageModel.setMessage("Success Login");

        return ResponseEntity.ok().body(messageModel);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200", content = @Content(examples = {
                    @ExampleObject(name = "Data Users",
                            description = "Menampilkan semua data user.",
                            value = """
                                      {
                                      "responseCode": 200,
                                      "responseMessage": "Update user with id : 90780f08-5dd9-11ed-9b6a-0242ac120002",
                                      "data": [
                                        {
                                          "user_id": "03aad5f0-5dda-11ed-9b6a-0242ac120002",
                                          "name": "cahyadi",
                                          "email": "cahyadisn6@gmail.com",
                                          "phone_number": "08121432478",
                                          "roles": [
                                            1,
                                            2
                                          ]
                                        },
                                        {
                                          "user_id": "09ccd5f0-5xxd-11ed-9b6a-0242ac120002",
                                          "name": "surya",
                                          "email": "surya6@gmail.com",
                                          "phone_number": "081214324745",
                                          "roles": [
                                            1
                                          ]
                                        }
                                      ]
                                    }""")
            }, mediaType = MediaType.APPLICATION_JSON_VALUE))})
    @GetMapping("/get-all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<MessageModel> getAllUsers(){
        MessageModel messageModel = new MessageModel();
        try {
            List<UserResponse> usersGet = userService.searchAllUser();
            messageModel.setMessage("Success get all user");
            messageModel.setStatus(HttpStatus.OK.value());
            messageModel.setData(usersGet);
            log.info("Success get all user");
        }catch (Exception exception)
        {
            messageModel.setMessage("Failed get all user");
            messageModel.setStatus(HttpStatus.BAD_GATEWAY.value());
            log.error("Failed get all user, error : {}", exception.getMessage());
        }
        return ResponseEntity.ok().body(messageModel);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200", content = @Content(examples = {
                    @ExampleObject(name = "Data User",
                            description = "Menampilkan data user berdasarkan id.",
                            value = """
                                      {
                                      "responseCode": 200,
                                      "responseMessage": "Update user with id : 90780f08-5dd9-11ed-9b6a-0242ac120002",
                                      "data": [
                                        {
                                          "user_id": "03aad5f0-5dda-11ed-9b6a-0242ac120002",
                                          "name": "cahyadi",
                                          "email": "cahyadisn6@gmail.com",
                                          "phone_number": "08121432478",
                                          "roles": [
                                            1
                                          ]
                                        }
                                      ]
                                    }""")
            }, mediaType = MediaType.APPLICATION_JSON_VALUE))})
    @GetMapping("/id/{userId}")
    public ResponseEntity<MessageModel> getUserById(@PathVariable UUID userId){
        MessageModel messageModel = new MessageModel();
        try {
            UserResponse userGet = userService.searchUserById(userId);
            messageModel.setMessage("Success get user");
            messageModel.setStatus(HttpStatus.OK.value());
            messageModel.setData(userGet);
            log.info("Success get user with id {}", userId);
        }catch (Exception exception)
        {
            messageModel.setMessage("Failed get user");
            messageModel.setStatus(HttpStatus.NO_CONTENT.value());
            log.error("Failed get user with id {}, error : {}", userId, exception.getMessage());
        }
        return ResponseEntity.ok().body(messageModel);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200", content = @Content(examples = {
                    @ExampleObject(name = "Data Users",
                            description = "Menampilkan semua data user berdasarkan name.",
                            value = """
                                      {
                                      "responseCode": 200,
                                      "responseMessage": "Update user with id : 90780f08-5dd9-11ed-9b6a-0242ac120002",
                                      "data": [
                                        {
                                          "user_id": "03aad5f0-5dda-11ed-9b6a-0242ac120002",
                                          "name": "cahyadi",
                                          "email": "cahyadisn6@gmail.com",
                                          "phone_number": "08121432478",
                                          "roles": [
                                            1
                                          ]
                                        },
                                        {
                                          "user_id": "09ccd5f0-5xxd-11ed-9b6a-0242ac120002",
                                          "name": "cahyadi suryss",
                                          "email": "surya6@gmail.com",
                                          "phone_number": "081214324745",
                                          "roles": [
                                            1
                                          ]
                                        }
                                      ]
                                    }""")
            }, mediaType = MediaType.APPLICATION_JSON_VALUE))})
    @GetMapping("/name/{name}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<MessageModel> getUserByName(@PathVariable String name){
        MessageModel messageModel = new MessageModel();
        try {
            List<UserResponse> usersGet = userService.searchUserByName(name);
            messageModel.setMessage("Success get user");
            messageModel.setStatus(HttpStatus.OK.value());
            messageModel.setData(usersGet);
            log.info("Success get user with name {}", name);
        }
        catch (Exception exception)
        {
            messageModel.setMessage("Failed get user");
            messageModel.setStatus(HttpStatus.NO_CONTENT.value());
            log.error("Failed get user with name {}, error : {}", name, exception.getMessage());
        }
        return ResponseEntity.ok().body(messageModel);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<MessageModel> getUserByEmail(@PathVariable String email){
        MessageModel messageModel = new MessageModel();
        try {
            UserResponse userResponse = userService.searchUserByEmail(email);
            messageModel.setMessage("Success get user");
            messageModel.setStatus(HttpStatus.OK.value());
            messageModel.setData(userResponse);
            log.info("Success get user with email {}", email);
        }
        catch (Exception exception)
        {
            messageModel.setMessage("Failed get user");
            messageModel.setStatus(HttpStatus.NO_CONTENT.value());
            log.error("Failed get user with email {}, error : {}", email, exception.getMessage());
        }
        return ResponseEntity.ok().body(messageModel);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200", content = @Content(examples = {
                    @ExampleObject(name = "Update User",
                            description = "Pastikan id user valid, data yang bisa diubah adalah name, email, phone_number dan roles.",
                            value = """
                                      {
                                      "responseCode": 200,
                                      "responseMessage": "Update user with id : 90780f08-5dd9-11ed-9b6a-0242ac120002",
                                      "data": [
                                        {
                                          "user_id": "03aad5f0-5dda-11ed-9b6a-0242ac120002",
                                          "name": "cahyadi",
                                          "email": "cahyadisn6@gmail.com",
                                          "phone_number": "08121432478",
                                          "roles": [
                                            1
                                          ]
                                        }
                                      ]
                                    }""")
            }, mediaType = MediaType.APPLICATION_JSON_VALUE))})
    @PutMapping("/update/{userId}")
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    public ResponseEntity<MessageModel> updateUser(@PathVariable UUID userId, @RequestBody UserUpdateRequest userUpdateRequest) {
        MessageModel messageModel = new MessageModel();

        if(userUpdateRequest.getEmail() != null)
        {
            boolean isEmailValid = EmailValidator.getInstance().isValid(userUpdateRequest.getEmail());

            if(isEmailValid)
                updateUserMessage(userId, userUpdateRequest, messageModel);
            else
            {
                messageModel.setStatus(HttpStatus.BAD_REQUEST.value());
                messageModel.setMessage("Email is not valid");
            }
        }
        else
            updateUserMessage(userId, userUpdateRequest, messageModel);

        return ResponseEntity.ok().body(messageModel);
    }

    private void updateUserMessage(@PathVariable UUID userId, @RequestBody UserUpdateRequest userUpdateRequest, MessageModel messageModel) {
        UserResponse userResponse = userService.updateUser(userUpdateRequest, userId);

        if(userResponse.getMessage() != null)
        {
            messageModel.setStatus(HttpStatus.CONFLICT.value());
            messageModel.setMessage(userResponse.getMessage());
        }
        else
        {
            messageModel.setStatus(HttpStatus.OK.value());
            messageModel.setMessage("Update user with id : " + userId);
            messageModel.setData(userResponse);
        }
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200", content = @Content(examples = {
                    @ExampleObject(name = "Delete User",
                            description = "Pastikan id user valid.",
                            value = """
                                    {
                                        "responseCode": 200,
                                        "responseMessage": "Success non-active user by id : 90780f08-5dd9-11ed-9b6a-0242ac120002"
                                    }""")
            }, mediaType = MediaType.APPLICATION_JSON_VALUE))})
    @DeleteMapping("/delete/{userId}")
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    public ResponseEntity<MessageModel> deleteUser(@PathVariable UUID userId){
        MessageModel messageModel = new MessageModel();
        Boolean deleteUser = userService.deleteUser(userId);
        if(Boolean.TRUE.equals(deleteUser))
        {
            messageModel.setMessage("Success non-active user by id : " + userId);
            messageModel.setStatus(HttpStatus.OK.value());
            log.info("Success non-active user with id {}", userId);
        }
        else
        {
            messageModel.setMessage("Failed non-active user by id : " + userId + ", not found");
            messageModel.setStatus(HttpStatus.NO_CONTENT.value());
            log.error("Failed non-active user with id {}", userId);
        }

        return ResponseEntity.ok().body(messageModel);
    }
}
