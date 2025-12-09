package com.example.Oboe.Config;

import com.example.Oboe.Entity.LoaiTaiKhoan;
import com.example.Oboe.Entity.PhuongThucXacThuc;
import com.example.Oboe.Entity.NguoiDung;
import com.example.Oboe.Entity.TrangThaiTaiKhoan;
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
        return List.of(new SimpleGrantedAuthority(nguoiDung.getVaiTro().name()));
    }

    @Override
    public String getPassword() {

        return nguoiDung.getMatKhau() != null ? nguoiDung.getMatKhau() : "";
    }

    @Override
    public String getUsername() {
        return nguoiDung.getEmail();
    }

    public PhuongThucXacThuc getAuthProvider() {
        return nguoiDung.getPhuongThucXacThuc();
    }
    public UUID getUserID() {
        return nguoiDung.getMaNguoiDung();
    }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() {
        return nguoiDung.getTrangThaiTaiKhoan() != null && nguoiDung.getTrangThaiTaiKhoan() != TrangThaiTaiKhoan.BAN;
    }
    public LoaiTaiKhoan getLoaiTaiKhoan() {
        return nguoiDung.getLoaiTaiKhoan();
    }
}

