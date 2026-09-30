package com.hrms.hrms_system.modules.role.repository;

import com.hrms.hrms_system.modules.role.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {

    Optional<Role> findByName(String name);

    boolean existsByName(String name);

    Page<Role> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
