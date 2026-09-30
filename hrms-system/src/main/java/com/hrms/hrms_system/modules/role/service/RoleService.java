package com.hrms.hrms_system.modules.role.service;

import com.hrms.hrms_system.modules.role.dto.RoleRequestDTO;
import com.hrms.hrms_system.modules.role.dto.RoleResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface RoleService {

    RoleResponseDTO create(RoleRequestDTO dto);

    RoleResponseDTO getById(UUID id);

    Page<RoleResponseDTO> getAll(String search, Pageable pageable);

    RoleResponseDTO update(UUID id, RoleRequestDTO dto);

    void delete(UUID id);
}
