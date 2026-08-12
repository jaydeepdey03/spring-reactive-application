package com.example.tracker.domain.user;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table("users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @Column("id")
    private UUID id;

    @Column("email")
    private String email;
    @Column("display_name")
    private String displayName;
    @Column("auth_provider")
    private String authProvider;
    @Column("provider_user_id")
    private String providerUserId;
    @Column("created_at")
    private Instant createdAt;
    @Column("updated_at")
    private Instant updatedAt;
    @Column("role")
    private Role role;

    @Version
    @Column("version")
    private Long version;

}
