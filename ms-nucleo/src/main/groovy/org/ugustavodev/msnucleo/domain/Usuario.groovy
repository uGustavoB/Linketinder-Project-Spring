package org.ugustavodev.msnucleo.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.ugustavodev.msnucleo.domain.enums.Role

@Entity
@Table(name = "usuarios")
class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id

    @Column(unique = true, nullable = false)
    String email

    @Column(nullable = false)
    String senha

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    Role role

    @Override
    Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.getRole()))
    }

    @Override
    String getPassword() {
        return senha
    }

    @Override
    String getUsername() {
        return email
    }

    @Override
    boolean isAccountNonExpired() {
        return true
    }

    @Override
    boolean isAccountNonLocked() {
        return true
    }

    @Override
    boolean isCredentialsNonExpired() {
        return true
    }

    @Override
    boolean isEnabled() {
        return true
    }

}
