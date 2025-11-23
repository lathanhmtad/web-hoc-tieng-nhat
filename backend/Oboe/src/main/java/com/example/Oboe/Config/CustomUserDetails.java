package com.example.Oboe.Config;

import com.example.Oboe.Entity.AccountType;
import com.example.Oboe.Entity.AuthProvider;
import com.example.Oboe.Entity.NguoiDung;
import com.example.Oboe.Entity.Status;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class CustomUserDetails implements UserDetails {
    private final NguoiDung nguoiDung;

    public CustomUserDetails(NguoiDung nguoiDung) {
        this.nguoiDung = nguoiDung;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(nguoiDung.getRole().name()));
    }

    @Override
    public String getPassword() {

        return nguoiDung.getPassWord() != null ? nguoiDung.getPassWord() : "";
    }

    @Override
    public String getUsername() {
        return nguoiDung.getUserName();
    }
    public AuthProvider getAuthProvider() {
        return nguoiDung.getAuthProvider();
    }
    public UUID getUserID() {
        return nguoiDung.getUser_id();
    }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() {
        return nguoiDung.getStatus() != null && nguoiDung.getStatus() != Status.BAN;
    }
    public AccountType getAccountType() {
        return nguoiDung.getAccountType();
    }
}

