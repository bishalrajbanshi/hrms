package com.hrms.hrms_system.modules.role.service;

import com.hrms.hrms_system.modules.role.dto.RoleRequestDTO;
import com.hrms.hrms_system.modules.role.dto.RoleResponseDTO;
import com.hrms.hrms_system.modules.role.entity.Role;
import com.hrms.hrms_system.modules.role.mapper.RoleMapper;
import com.hrms.hrms_system.modules.role.repository.RoleRepository;
import com.hrms.hrms_system.modules.role.service.impl.RoleServiceImpl;
import com.hrms.hrms_system.shared.exception.DuplicateResourceException;
import com.hrms.hrms_system.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    private static final UUID ROLE_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID OTHER_ROLE_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID MISSING_ROLE_ID = UUID.fromString("99999999-9999-9999-9999-999999999999");

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private RoleMapper roleMapper;

    @InjectMocks
    private RoleServiceImpl roleService;

    private Role role;
    private RoleRequestDTO request;
    private RoleResponseDTO response;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 10, 0);
        role = Role.builder()
                .id(ROLE_ID)
                .name("Admin")
                .description("Full access")
                .createdAt(now)
                .updatedAt(now)
                .build();
        request = RoleRequestDTO.builder()
                .name("Admin")
                .description("Full access")
                .build();
        response = RoleResponseDTO.builder()
                .id(ROLE_ID)
                .name("Admin")
                .description("Full access")
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    @Test
    void create_shouldCreateRole() {
        when(roleRepository.existsByName("Admin")).thenReturn(false);
        when(roleMapper.toEntity(request)).thenReturn(role);
        when(roleRepository.save(role)).thenReturn(role);
        when(roleMapper.toResponse(role)).thenReturn(response);

        RoleResponseDTO created = roleService.create(request);

        assertThat(created.getName()).isEqualTo("Admin");
        verify(roleRepository).save(role);
    }

    @Test
    void create_shouldTrimName() {
        request.setName("  Admin  ");
        when(roleRepository.existsByName("Admin")).thenReturn(false);
        when(roleMapper.toEntity(request)).thenReturn(role);
        when(roleRepository.save(role)).thenReturn(role);
        when(roleMapper.toResponse(role)).thenReturn(response);

        roleService.create(request);

        assertThat(request.getName()).isEqualTo("Admin");
        verify(roleRepository).existsByName("Admin");
    }

    @Test
    void create_shouldRejectDuplicateName() {
        when(roleRepository.existsByName("Admin")).thenReturn(true);

        assertThatThrownBy(() -> roleService.create(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Role already exists with name: Admin");
        verify(roleRepository, never()).save(any());
    }

    @Test
    void getById_shouldReturnRole() {
        when(roleRepository.findById(ROLE_ID)).thenReturn(Optional.of(role));
        when(roleMapper.toResponse(role)).thenReturn(response);

        RoleResponseDTO found = roleService.getById(ROLE_ID);

        assertThat(found.getId()).isEqualTo(ROLE_ID);
        assertThat(found.getName()).isEqualTo("Admin");
    }

    @Test
    void getById_shouldThrowWhenMissing() {
        when(roleRepository.findById(MISSING_ROLE_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roleService.getById(MISSING_ROLE_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Role not found with id: " + MISSING_ROLE_ID);
    }

    @Test
    void getAll_shouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 20);
        when(roleRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(role), pageable, 1));
        when(roleMapper.toResponse(role)).thenReturn(response);

        Page<RoleResponseDTO> page = roleService.getAll(null, pageable);

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getTotalElements()).isEqualTo(1);
    }

    @Test
    void getAll_shouldFilterByName() {
        Pageable pageable = PageRequest.of(0, 20);
        when(roleRepository.findByNameContainingIgnoreCase("adm", pageable))
                .thenReturn(new PageImpl<>(List.of(role), pageable, 1));
        when(roleMapper.toResponse(role)).thenReturn(response);

        Page<RoleResponseDTO> page = roleService.getAll(" adm ", pageable);

        assertThat(page.getContent()).extracting(RoleResponseDTO::getName).containsExactly("Admin");
        verify(roleRepository).findByNameContainingIgnoreCase("adm", pageable);
    }

    @Test
    void update_shouldUpdateRole() {
        RoleRequestDTO updateRequest = RoleRequestDTO.builder()
                .name("Manager")
                .description("Team lead")
                .build();
        when(roleRepository.findById(ROLE_ID)).thenReturn(Optional.of(role));
        when(roleRepository.findByName("Manager")).thenReturn(Optional.empty());
        when(roleRepository.save(role)).thenReturn(role);
        when(roleMapper.toResponse(role)).thenReturn(
                RoleResponseDTO.builder().id(ROLE_ID).name("Manager").description("Team lead").build()
        );

        RoleResponseDTO updated = roleService.update(ROLE_ID, updateRequest);

        assertThat(updated.getName()).isEqualTo("Manager");
        verify(roleMapper).updateEntity(updateRequest, role);
    }

    @Test
    void update_shouldThrowWhenMissing() {
        when(roleRepository.findById(MISSING_ROLE_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roleService.update(MISSING_ROLE_ID, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Role not found with id: " + MISSING_ROLE_ID);
    }

    @Test
    void update_shouldRejectDuplicateName() {
        Role other = Role.builder().id(OTHER_ROLE_ID).name("Admin").build();
        when(roleRepository.findById(ROLE_ID)).thenReturn(Optional.of(role));
        when(roleRepository.findByName("Admin")).thenReturn(Optional.of(other));

        RoleRequestDTO updateRequest = RoleRequestDTO.builder().name("Admin").build();

        assertThatThrownBy(() -> roleService.update(ROLE_ID, updateRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Role already exists with name: Admin");
    }

    @Test
    void delete_shouldDeleteExistingRole() {
        when(roleRepository.findById(ROLE_ID)).thenReturn(Optional.of(role));

        roleService.delete(ROLE_ID);

        verify(roleRepository).delete(role);
    }

    @Test
    void delete_shouldThrowWhenMissing() {
        when(roleRepository.findById(MISSING_ROLE_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roleService.delete(MISSING_ROLE_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Role not found with id: " + MISSING_ROLE_ID);
        verify(roleRepository, never()).delete(any());
    }
}
