package com.example.Oboe.Repository;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class UserRepositoryCustomImpl implements UserRepositoryCustom {
    @PersistenceContext
    private EntityManager em;


    @Override
    @Transactional
    public void deleteUserWithDependencies(UUID userId) {
        em.createNativeQuery("""
                    DELETE c1 FROM binh_luan c1
                    INNER JOIN (
                        SELECT ma_binh_luan FROM binh_luan WHERE ma_nguoi_dung = :id
                    ) AS c2 ON c1.ma_binh_luan_cha = c2.ma_binh_luan
                """).setParameter("id", userId).executeUpdate();


        em.createNativeQuery("DELETE FROM binh_luan WHERE ma_nguoi_dung = :id")
                .setParameter("id", userId).executeUpdate();

        // Các bảng khác
        em.createNativeQuery("DELETE FROM user_answers WHERE ma_nguoi_dung = :id").setParameter("id", userId).executeUpdate();
        em.createNativeQuery("DELETE FROM yeu_thich WHERE ma_nguoi_dung = :id").setParameter("id", userId).executeUpdate();
        em.createNativeQuery("DELETE FROM message WHERE senderid = :id OR receiverid = :id").setParameter("id", userId).executeUpdate();
        em.createNativeQuery("DELETE FROM notifications WHERE ma_nguoi_dung = :id").setParameter("id", userId).executeUpdate();
        em.createNativeQuery("DELETE FROM payment WHERE ma_nguoi_dung = :id").setParameter("id", userId).executeUpdate();
        em.createNativeQuery("DELETE FROM quiz_results WHERE ma_nguoi_dung = :id").setParameter("id", userId).executeUpdate();
        em.createNativeQuery("DELETE FROM reports WHERE ma_nguoi_dung = :id").setParameter("id", userId).executeUpdate();

        // Xoá flashcard & quiz liên quan
        em.createNativeQuery("DELETE FROM muc_the WHERE ma_bo_the IN (SELECT ma_bo_the FROM bo_the WHERE ma_nguoi_dung = :id)")
                .setParameter("id", userId).executeUpdate();

        em.createNativeQuery("DELETE FROM questions WHERE quizzesid IN (SELECT quizzesid FROM quizzes WHERE ma_nguoi_dung = :id)")
                .setParameter("id", userId).executeUpdate();

        em.createNativeQuery("DELETE FROM flash_cards WHERE ma_nguoi_dung = :id").setParameter("id", userId).executeUpdate();
        em.createNativeQuery("DELETE FROM quizzes WHERE ma_nguoi_dung = :id").setParameter("id", userId).executeUpdate();
        em.createNativeQuery("DELETE FROM blogs WHERE ma_nguoi_dung = :id")
                .setParameter("id", userId).executeUpdate();

        // Cuối cùng xoá user
        em.createNativeQuery("DELETE FROM users WHERE ma_nguoi_dung = :id").setParameter("id", userId).executeUpdate();
    }

}

