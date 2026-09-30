package com.hrms.hrms_system.modules.role.repository;

import com.hrms.hrms_system.modules.role.entity.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void shouldPersistAndFindByName() {
        Role saved = roleRepository.saveAndFlush(role("Admin"));

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getId()).isInstanceOf(UUID.class);
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
        assertThat(roleRepository.findByName("Admin")).isPresent();
        assertThat(roleRepository.existsByName("Admin")).isTrue();
        assertThat(roleRepository.existsByName("admin")).isFalse();
    }

    @Test
    void shouldRejectDuplicateNamesAtDatabaseLevel() {
        roleRepository.saveAndFlush(role("Admin"));

        assertThatThrownBy(() -> roleRepository.saveAndFlush(role("Admin")))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void shouldSearchByNameIgnoreCase() {
        roleRepository.saveAndFlush(role("Admin"));
        roleRepository.saveAndFlush(role("Manager"));

        Page<Role> page = roleRepository.findByNameContainingIgnoreCase("adm", PageRequest.of(0, 10));

        assertThat(page.getContent()).extracting(Role::getName).containsExactly("Admin");
    }

    private Role role(String name) {
        return Role.builder()
                .name(name)
                .description("Test role")
                .build();
    }
}
