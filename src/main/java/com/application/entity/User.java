package com.application.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.application.model.auth.request.RegisterUserDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "user")
public class User  implements UserDetails {
    @Id
    private String id;
    @Indexed(unique = true)
    private String username;
    private String name;
    @Indexed(unique = true)
    private String email;
    private String password;
    private Boolean isActive;
    private List<Role> roles = new ArrayList<>();
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    private static PasswordEncoder passwordEncoder;

    static {
        passwordEncoder = new BCryptPasswordEncoder();
    }

    public User (String name, String username, String email, String password, Boolean isActive) {
        this.email = email;
        this.password = passwordEncoder.encode(password);
    }

    public User (RegisterUserDto userDto) {
        this.name = userDto.name();
        this.username = userDto.userName();
        this.email = userDto.email();
        this.password = passwordEncoder.encode(userDto.password());
        this.roles.add(Role.ROLE_USER);
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream().map(authority -> new SimpleGrantedAuthority(authority.name())).collect(Collectors.toList());
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.isActive != null && this.isActive;
    }
}
