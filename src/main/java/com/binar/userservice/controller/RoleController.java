package com.binar.userservice.controller;

import com.binar.userservice.dto.MessageModel;
import com.binar.userservice.dto.RoleRequest;
import com.binar.userservice.dto.RoleResponse;
import com.binar.userservice.services.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/role", produces = {"application/json"})
public class RoleController
{
    private static final Logger log = LoggerFactory.getLogger(RoleController.class);

    @Autowired
    RoleService roleService;

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<MessageModel> createRole(@RequestBody RoleRequest roleRequest)
    {
        MessageModel messageModel = new MessageModel();

        RoleResponse roleResponse = roleService.registerRole(roleRequest);

        if(roleResponse.getMessage() != null)
        {
            messageModel.setStatus(HttpStatus.CONFLICT.value());
            messageModel.setMessage(roleResponse.getMessage());
            log.error("Failed create new role, error : {}", roleResponse.getMessage());
        }
        else
        {
            messageModel.setStatus(HttpStatus.OK.value());
            messageModel.setMessage("Register new role");
            messageModel.setData(roleResponse);
            log.info("Success create new role with id {} ", roleResponse.getRoleId());
        }

        return ResponseEntity.ok().body(messageModel);
    }

    @GetMapping("/get-all")
    public ResponseEntity<MessageModel> getAllRole()
    {
        MessageModel messageModel = new MessageModel();
        try {
            List<RoleResponse> rolesGet = roleService.searchAllRole();
            messageModel.setMessage("Success get all role");
            messageModel.setStatus(HttpStatus.OK.value());
            messageModel.setData(rolesGet);
            log.info("Success get all role");
        }catch (Exception exception)
        {
            messageModel.setMessage("Failed get all role");
            messageModel.setStatus(HttpStatus.BAD_GATEWAY.value());
            log.error("Failed get all role, error : {}", exception.getMessage());
        }
        return ResponseEntity.ok().body(messageModel);
    }


    @PutMapping("/update/{roleId}")
    public ResponseEntity<MessageModel> updateRole(@PathVariable Integer roleId, @RequestBody RoleRequest roleRequest)
    {
        MessageModel messageModel = new MessageModel();
        RoleResponse roleResponse = roleService.updateRole(roleRequest, roleId);

        if(roleResponse.getMessage() != null)
        {
            messageModel.setStatus(HttpStatus.CONFLICT.value());
            messageModel.setMessage(roleResponse.getMessage());
            log.error("Failed update role with id {}, error : {} ", roleId, roleResponse.getMessage());
        }
        else
        {
            messageModel.setStatus(HttpStatus.OK.value());
            messageModel.setMessage("Update role with id : " + roleId);
            messageModel.setData(roleResponse);
            log.info("Success update role with id {}", roleId);
        }

        return ResponseEntity.ok().body(messageModel);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200", content = @Content(examples = {
                    @ExampleObject(name = "Delete Role",
                            description = "Pastikan id role valid.",
                            value = "{\"responseCode\": 200, \"responseMessage\": \"Success delete role by id : 1\"}")
            }, mediaType = MediaType.APPLICATION_JSON_VALUE))})
    @DeleteMapping("/delete/{roleId}")
    public ResponseEntity<MessageModel> deleteRole(@PathVariable Integer roleId)
    {
        MessageModel messageModel = new MessageModel();
        Boolean deleteRole = roleService.deleteRole(roleId);
        if(Boolean.TRUE.equals(deleteRole))
        {
            messageModel.setMessage("Success delete role by id : " + roleId);
            messageModel.setStatus(HttpStatus.OK.value());
            log.error("Failed delete role with id {} , error : {} ", roleId, "id not found");
        }
        else
        {
            messageModel.setMessage("Failed delete role by id : " + roleId + ", not found");
            messageModel.setStatus(HttpStatus.NO_CONTENT.value());
            log.info("Success delete role with id {}", roleId);
        }

        return ResponseEntity.ok().body(messageModel);
    }
}
