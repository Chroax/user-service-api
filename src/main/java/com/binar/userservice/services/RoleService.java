package com.binar.userservice.services;

import com.binar.userservice.dto.RoleRequest;
import com.binar.userservice.dto.RoleResponse;

import java.util.List;

public interface RoleService {

    RoleResponse registerRole(RoleRequest roleRequest);
    RoleResponse updateRole(RoleRequest roleRequest, Integer roleId);
    Boolean deleteRole(Integer roleId);
    List<RoleResponse> searchAllRole();
}
