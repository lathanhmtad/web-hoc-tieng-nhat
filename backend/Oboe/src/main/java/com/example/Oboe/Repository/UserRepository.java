package com.example.Oboe.Repository;

import com.example.Oboe.DTOs.UserSearchResultDTO;
import com.example.Oboe.Entity.AuthProvider;
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


    @Query("SELECT u FROM NguoiDung u WHERE u.userName = :userName AND u.authProvider = :authProvider")
    List<NguoiDung> findAllByUserNameAndAuthProvider(@Param("userName") String userName,
                                                     @Param("authProvider") AuthProvider authProvider);

    @Query(value = "SELECT * FROM nguoi_dung WHERE user_id = :userId", nativeQuery = true)
    Optional<NguoiDung> findByUser_id(@Param("userId") UUID userId);

    boolean existsByUserNameAndAuthProvider(String userName, AuthProvider authProvider);

    @Query("SELECT u FROM NguoiDung u WHERE LOWER(u.userName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<NguoiDung> searchUsers(@Param("keyword") String keyword);



    @Query("SELECT u FROM NguoiDung u WHERE u.user_id IN :ids")
    List<NguoiDung> findByUserIdIn(@Param("ids") List<UUID> ids);
    @Query("SELECT COUNT(u) FROM NguoiDung u")
    Long countAllUsers();

    @Query("SELECT COUNT(u) FROM NguoiDung u WHERE FUNCTION('MONTH', u.create_at) = FUNCTION('MONTH', CURRENT_DATE)")
    Long countUsersThisMonth();

    @Query("SELECT u.userName, u.create_at FROM NguoiDung u ORDER BY u.create_at DESC")
    List<Object[]> findLatestRegisteredUser();

@Query("SELECT new com.example.Oboe.DTOs.UserSearchResultDTO(u.user_id, u.userName, u.avatarUrl, COUNT(f)) " +
            "FROM NguoiDung u LEFT JOIN BoThe f ON f.nguoiDung.user_id = u.user_id " +
            "WHERE LOWER(u.userName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "GROUP BY u.user_id, u.userName, u.avatarUrl")
    Page<UserSearchResultDTO> searchUsersWithFlashcardCount(@Param("keyword") String keyword, Pageable pageable);

}