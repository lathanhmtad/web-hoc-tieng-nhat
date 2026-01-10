package com.example.Oboe.Repository;

import com.example.Oboe.DTOs.UserSearchResultDTO;
import com.example.Oboe.Entity.PHUONG_THUC_XAC_THUC;
import com.example.Oboe.Entity.NGUOI_DUNG;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<NGUOI_DUNG, UUID> {

    @Query("SELECT u FROM NGUOI_DUNG u WHERE u.email = :userName AND u.phuongThucXacThuc = :authProvider")
    List<NGUOI_DUNG> findAllByUserNameAndAuthProvider(@Param("userName") String userName,
                                                      @Param("authProvider") PHUONG_THUC_XAC_THUC phuongThucXacThuc);

    Optional<NGUOI_DUNG> findByMaNguoiDung(UUID maNguoiDung);

    boolean existsByEmailAndPhuongThucXacThuc(String email, PHUONG_THUC_XAC_THUC phuongThucXacThuc);

    @Query("SELECT u FROM NGUOI_DUNG u WHERE LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<NGUOI_DUNG> searchUsers(@Param("keyword") String keyword);

    @Query("SELECT u FROM NGUOI_DUNG u WHERE u.maNguoiDung IN :ids")
    List<NGUOI_DUNG> findByUserIdIn(@Param("ids") List<UUID> ids);

    @Query("SELECT COUNT(u) FROM NGUOI_DUNG u")
    Long countAllUsers();

    @Query("SELECT COUNT(u) FROM NGUOI_DUNG u WHERE FUNCTION('MONTH', u.ngayTao) = FUNCTION('MONTH', CURRENT_DATE)")
    Long countUsersThisMonth();

    @Query("SELECT u.email, u.ngayTao FROM NGUOI_DUNG u ORDER BY u.ngayTao DESC")
    List<Object[]> findLatestRegisteredUser();

    @Query("SELECT new com.example.Oboe.DTOs.UserSearchResultDTO(u.maNguoiDung, u.email, u.anhDaiDien, COUNT(f)) " +
            "FROM NGUOI_DUNG u LEFT JOIN BO_THE_GHI_NHO f ON f.nguoiDung.maNguoiDung = u.maNguoiDung " +
            "WHERE LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "GROUP BY u.maNguoiDung, u.email, u.anhDaiDien")
    Page<UserSearchResultDTO> searchUsersWithFlashcardCount(@Param("keyword") String keyword, Pageable pageable);
}