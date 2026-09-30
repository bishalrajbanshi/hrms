package com.hrms.hrms_system.modules.role.service.impl;

import com.hrms.hrms_system.modules.role.dto.RoleRequestDTO;
import com.hrms.hrms_system.modules.role.dto.RoleResponseDTO;
import com.hrms.hrms_system.modules.role.entity.Role;
import com.hrms.hrms_system.modules.role.mapper.RoleMapper;
import com.hrms.hrms_system.modules.role.repository.RoleRepository;
import com.hrms.hrms_system.modules.role.service.RoleService;
import com.hrms.hrms_system.shared.exception.DuplicateResourceException;
import com.hrms.hrms_system.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    @Override
    @Transactional
    public RoleResponseDTO create(RoleRequestDTO dto) {
        normalize(dto);

        if (roleRepository.existsByName(dto.getName())) {
            log.warn("Duplicate role creation attempt: {}", dto.getName());
            throw new DuplicateResourceException("Role already exists with name: " + dto.getName());
        }

        Role saved = roleRepository.save(roleMapper.toEntity(dto));
        log.info("Role created: id={}, name={}", saved.getId(), saved.getName());
        return roleMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public RoleResponseDTO getById(UUID id) {
        return roleMapper.toResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RoleResponseDTO> getAll(String search, Pageable pageable) {
        Page<Role> page = StringUtils.hasText(search)
                ? roleRepository.findByNameContainingIgnoreCase(search.trim(), pageable)
                : roleRepository.findAll(pageable);
        return page.map(roleMapper::toResponse);
    }

    @Override
    @Transactional
    public RoleResponseDTO update(UUID id, RoleRequestDTO dto) {
        normalize(dto);
        Role role = findById(id);

        roleRepository.findByName(dto.getName())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    log.warn("Duplicate role update attempt: {}", dto.getName());
                    throw new DuplicateResourceException("Role already exists with name: " + dto.getName());
                });

        roleMapper.updateEntity(dto, role);
        Role saved = roleRepository.save(role);
        log.info("Role updated: id={}, name={}", saved.getId(), saved.getName());
        return roleMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Role role = findById(id);
        roleRepository.delete(role);
        log.info("Role deleted: id={}", id);
    }

    private Role findById(UUID id) {
        return roleRepository.findById(id).orElseThrow(() -> {
            log.warn("Role not found: {}", id);
            return new ResourceNotFoundException("Role not found with id: " + id);
        });
    }

    private void normalize(RoleRequestDTO dto) {
        if (dto.getName() != null) {
            dto.setName(dto.getName().trim());
        }
        if (dto.getDescription() != null) {
            String description = dto.getDescription().trim();
            dto.setDescription(description.isEmpty() ? null : description);
        }
    }
}
