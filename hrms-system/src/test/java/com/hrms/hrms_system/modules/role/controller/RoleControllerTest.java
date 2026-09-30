package com.hrms.hrms_system.modules.role.controller;

import com.hrms.hrms_system.modules.role.dto.RoleRequestDTO;
import com.hrms.hrms_system.modules.role.dto.RoleResponseDTO;
import com.hrms.hrms_system.modules.role.service.RoleService;
import com.hrms.hrms_system.shared.exception.DuplicateResourceException;
import com.hrms.hrms_system.shared.exception.GlobalExceptionHandler;
import com.hrms.hrms_system.shared.exception.ResourceNotFoundException;
import com.hrms.hrms_system.shared.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RoleController.class)
@Import({GlobalExceptionHandler.class, SecurityConfig.class})
class RoleControllerTest {

    private static final String BASE_URL = "/api/v1/roles";
    private static final UUID ROLE_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID MISSING_ROLE_ID = UUID.fromString("99999999-9999-9999-9999-999999999999");

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RoleService roleService;

    @Test
    void create_shouldReturn201() throws Exception {
        when(roleService.create(any(RoleRequestDTO.class))).thenReturn(sampleResponse());

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Admin","description":"Full access"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Admin"));
    }

    @Test
    void create_shouldReturn400WhenNameBlank() throws Exception {
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"   "}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.name").exists());
    }

    @Test
    void create_shouldReturn409WhenDuplicate() throws Exception {
        when(roleService.create(any(RoleRequestDTO.class)))
                .thenThrow(new DuplicateResourceException("Role already exists with name: Admin"));

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Admin"}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Role already exists with name: Admin"));
    }

    @Test
    void getById_shouldReturnExistingRole() throws Exception {
        when(roleService.getById(ROLE_ID)).thenReturn(sampleResponse());

        mockMvc.perform(get(BASE_URL + "/" + ROLE_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(ROLE_ID.toString()));
    }

    @Test
    void getById_shouldReturn404WhenMissing() throws Exception {
        when(roleService.getById(MISSING_ROLE_ID))
                .thenThrow(new ResourceNotFoundException("Role not found with id: " + MISSING_ROLE_ID));

        mockMvc.perform(get(BASE_URL + "/" + MISSING_ROLE_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Role not found with id: " + MISSING_ROLE_ID));
    }

    @Test
    void getById_shouldReturn400WhenIdInvalid() throws Exception {
        mockMvc.perform(get(BASE_URL + "/not-a-uuid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAll_shouldReturnPaginatedRoles() throws Exception {
        when(roleService.getAll(isNull(), any()))
                .thenReturn(new PageImpl<>(List.of(sampleResponse()), PageRequest.of(0, 20), 1));

        mockMvc.perform(get(BASE_URL).param("page", "0").param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].name").value("Admin"))
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test
    void getAll_shouldPassSearchParam() throws Exception {
        when(roleService.getAll(eq("admin"), any()))
                .thenReturn(new PageImpl<>(List.of(sampleResponse()), PageRequest.of(0, 20), 1));

        mockMvc.perform(get(BASE_URL).param("search", "admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].name").value("Admin"));
    }

    @Test
    void update_shouldReturnUpdatedRole() throws Exception {
        when(roleService.update(eq(ROLE_ID), any(RoleRequestDTO.class))).thenReturn(sampleResponse());

        mockMvc.perform(put(BASE_URL + "/" + ROLE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Admin","description":"Updated"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Admin"));
    }

    @Test
    void delete_shouldReturn204() throws Exception {
        doNothing().when(roleService).delete(ROLE_ID);

        mockMvc.perform(delete(BASE_URL + "/" + ROLE_ID))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_shouldReturn404WhenMissing() throws Exception {
        doThrow(new ResourceNotFoundException("Role not found with id: " + MISSING_ROLE_ID))
                .when(roleService).delete(MISSING_ROLE_ID);

        mockMvc.perform(delete(BASE_URL + "/" + MISSING_ROLE_ID))
                .andExpect(status().isNotFound());
    }

    private RoleResponseDTO sampleResponse() {
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 10, 0);
        return RoleResponseDTO.builder()
                .id(ROLE_ID)
                .name("Admin")
                .description("Full access")
                .createdAt(now)
                .updatedAt(now)
                .build();
    }
}
