package com.example.Oboe.Config;

import com.example.Oboe.Entity.LOAI_TAI_KHOAN;
import com.example.Oboe.Entity.PHUONG_THUC_XAC_THUC;
import com.example.Oboe.Entity.NGUOI_DUNG;
import com.example.Oboe.Entity.TRANG_THAI_TAI_KHOAN;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class CustomUserDetails implements UserDetails {
    private final NGUOI_DUNG nguoiDung;

    public CustomUserDetails(NGUOI_DUNG nguoiDung) {
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

    public PHUONG_THUC_XAC_THUC getAuthProvider() {
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
        return nguoiDung.getTrangThaiTaiKhoan() != null && nguoiDung.getTrangThaiTaiKhoan() != TRANG_THAI_TAI_KHOAN.BAN;
    }
    public LOAI_TAI_KHOAN getLoaiTaiKhoan() {
        return nguoiDung.getLoaiTaiKhoan();
    }
}

