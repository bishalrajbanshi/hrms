package com.hrms.hrms_system.modules.role.mapper;

import com.hrms.hrms_system.modules.role.dto.RoleRequestDTO;
import com.hrms.hrms_system.modules.role.dto.RoleResponseDTO;
import com.hrms.hrms_system.modules.role.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Role toEntity(RoleRequestDTO dto);

    RoleResponseDTO toResponse(Role role);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(RoleRequestDTO dto, @MappingTarget Role role);
}
