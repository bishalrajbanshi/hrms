package com.hrms.hrms_system.modules.role.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request payload for creating or updating a role")
public class RoleRequestDTO {

    @Schema(description = "Unique role name", example = "Admin", maxLength = 100)
    @NotBlank(message = "name is required")
    @Size(max = 100, message = "name must not exceed 100 characters")
    private String name;

    @Schema(description = "Optional role description", example = "Full system access", maxLength = 500)
    @Size(max = 500, message = "description must not exceed 500 characters")
    private String description;
}
