package com.example.tracker.domain.user;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Builder;
import lombok.Data;

@Table("users")
@Data
@Builder
public class User {
    @Id
    private UUID id;

    private String email;
    private String displayName;
    private String authProvider;
    private String providerUserId;
    private Instant createdAt;
    private Instant updatedAt;
    private Role role;

    @Version
    private Long version;

}
