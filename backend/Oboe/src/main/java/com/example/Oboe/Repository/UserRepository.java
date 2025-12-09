package com.example.Oboe.Repository;

import com.example.Oboe.DTOs.UserSearchResultDTO;
import com.example.Oboe.Entity.PhuongThucXacThuc;
import com.example.Oboe.Entity.NguoiDung;
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
public interface UserRepository extends JpaRepository<NguoiDung, UUID> {

    @Query("SELECT u FROM NguoiDung u WHERE u.email = :userName AND u.phuongThucXacThuc = :authProvider")
    List<NguoiDung> findAllByUserNameAndAuthProvider(@Param("userName") String userName,
                                                     @Param("authProvider") PhuongThucXacThuc phuongThucXacThuc);

    @Query(value = "SELECT * FROM nguoi_dung WHERE maNguoiDung = :userId", nativeQuery = true)
    Optional<NguoiDung> findBymaNguoiDung(@Param("userId") UUID userId);

    boolean existsByEmailAndPhuongThucXacThuc(String email, PhuongThucXacThuc phuongThucXacThuc);

    @Query("SELECT u FROM NguoiDung u WHERE LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<NguoiDung> searchUsers(@Param("keyword") String keyword);

    @Query("SELECT u FROM NguoiDung u WHERE u.maNguoiDung IN :ids")
    List<NguoiDung> findByUserIdIn(@Param("ids") List<UUID> ids);

    @Query("SELECT COUNT(u) FROM NguoiDung u")
    Long countAllUsers();

    @Query("SELECT COUNT(u) FROM NguoiDung u WHERE FUNCTION('MONTH', u.ngayTao) = FUNCTION('MONTH', CURRENT_DATE)")
    Long countUsersThisMonth();

    @Query("SELECT u.email, u.ngayTao FROM NguoiDung u ORDER BY u.ngayTao DESC")
    List<Object[]> findLatestRegisteredUser();

    @Query("SELECT new com.example.Oboe.DTOs.UserSearchResultDTO(u.maNguoiDung, u.email, u.anhDaiDien, COUNT(f)) " +
            "FROM NguoiDung u LEFT JOIN BoTheGhiNho f ON f.nguoiDung.maNguoiDung = u.maNguoiDung " +
            "WHERE LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "GROUP BY u.maNguoiDung, u.email, u.anhDaiDien")
    Page<UserSearchResultDTO> searchUsersWithFlashcardCount(@Param("keyword") String keyword, Pageable pageable);
}