package com.binar.userservice.services.impl;

import com.binar.userservice.dto.RoleRequest;
import com.binar.userservice.dto.RoleResponse;
import com.binar.userservice.exception.NotFoundException;
import com.binar.userservice.model.Roles;
import com.binar.userservice.repository.RoleRepository;
import com.binar.userservice.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public RoleResponse registerRole(RoleRequest roleRequest) {
        Roles roles = roleRequest.toRoles();

        try {
            roleRepository.save(roles);
            return RoleResponse.builder()
                    .roleId(roles.getRoleId())
                    .roleName(roles.getRoleName())
                    .build();
        }
        catch(Exception exception)
        {
            return RoleResponse.builder()
                    .message("Role already exist")
                    .build();
        }
    }

    @Override
    public RoleResponse updateRole(RoleRequest roleRequest, Integer roleId) {
        Optional<Roles> isRoles = roleRepository.findById(roleId);
        if(isRoles.isPresent()){
            Roles roles = isRoles.get();
            roles.setRoleName(roleRequest.getRoleName());
            try {
                roleRepository.save(roles);
                return RoleResponse.builder()
                        .roleId(roles.getRoleId())
                        .roleName(roles.getRoleName())
                        .build();
            }
            catch(Exception exception)
            {
                return RoleResponse.builder()
                        .message("Role already exist")
                        .build();
            }
        }
        else {
            throw new NotFoundException("Roles with id: " + roleId + " not found");
        }
    }

    @Override
    public Boolean deleteRole(Integer roleId) {
        if(roleRepository.existsById(roleId)) {
            roleRepository.deleteById(roleId);
            return true;
        }
        else
            return false;
    }

    @Override
    public List<RoleResponse> searchAllRole() {
        List<Roles> allRole = roleRepository.findAll();
        List<RoleResponse> allRoleResponse = new ArrayList<>();
        for (Roles roles: allRole) {
            RoleResponse roleResponse = RoleResponse.builder()
                    .roleId(roles.getRoleId())
                    .roleName(roles.getRoleName())
                    .build();
            allRoleResponse.add(roleResponse);
        }
        return allRoleResponse;
    }
}
