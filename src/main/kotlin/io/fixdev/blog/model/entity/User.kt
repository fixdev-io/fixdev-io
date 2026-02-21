package io.fixdev.blog.model.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "users")
class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "keycloak_sub", nullable = false, unique = true)
    val keycloakSub: String,

    @Column(nullable = false)
    var name: String,

    var email: String? = null,

    @Column(name = "avatar_url")
    var avatarUrl: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var role: UserRole = UserRole.USER,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now()
)
