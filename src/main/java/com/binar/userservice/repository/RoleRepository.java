package com.binar.userservice.repository;

import com.binar.userservice.model.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Roles, Integer> {

}
