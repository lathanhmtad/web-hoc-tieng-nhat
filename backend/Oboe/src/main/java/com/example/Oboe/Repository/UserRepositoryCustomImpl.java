package com.example.Oboe.Repository;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class UserRepositoryCustomImpl  implements  UserRepositoryCustom{
    @PersistenceContext
    private EntityManager em;


    @Override
    @Transactional
    public void deleteUserWithDependencies(UUID userId){
        em.createNativeQuery("""
        DELETE c1 FROM binhLuans c1
        INNER JOIN (
            SELECT comment_id FROM binhLuans WHERE maNguoiDung = :id
        ) AS c2 ON c1.parent_comment_id = c2.comment_id
    """).setParameter("id", userId).executeUpdate();


        em.createNativeQuery("DELETE FROM binhLuans WHERE maNguoiDung = :id")
                .setParameter("id", userId).executeUpdate();

        // Các bảng khác
        em.createNativeQuery("DELETE FROM user_answers WHERE maNguoiDung = :id").setParameter("id", userId).executeUpdate();
        em.createNativeQuery("DELETE FROM favorites WHERE maNguoiDung = :id").setParameter("id", userId).executeUpdate();
        em.createNativeQuery("DELETE FROM message WHERE senderid = :id OR receiverid = :id").setParameter("id", userId).executeUpdate();
        em.createNativeQuery("DELETE FROM notifications WHERE maNguoiDung = :id").setParameter("id", userId).executeUpdate();
        em.createNativeQuery("DELETE FROM payment WHERE maNguoiDung = :id").setParameter("id", userId).executeUpdate();
        em.createNativeQuery("DELETE FROM quiz_results WHERE maNguoiDung = :id").setParameter("id", userId).executeUpdate();
        em.createNativeQuery("DELETE FROM reports WHERE maNguoiDung = :id").setParameter("id", userId).executeUpdate();

        // Xoá flashcard & quiz liên quan
        em.createNativeQuery("DELETE FROM muc_the WHERE ma_bo_the IN (SELECT ma_bo_the FROM bo_the WHERE maNguoiDung = :id)")
                .setParameter("id", userId).executeUpdate();

        em.createNativeQuery("DELETE FROM questions WHERE quizzesid IN (SELECT quizzesid FROM quizzes WHERE maNguoiDung = :id)")
                .setParameter("id", userId).executeUpdate();

        em.createNativeQuery("DELETE FROM flash_cards WHERE maNguoiDung = :id").setParameter("id", userId).executeUpdate();
        em.createNativeQuery("DELETE FROM quizzes WHERE maNguoiDung = :id").setParameter("id", userId).executeUpdate();
        em.createNativeQuery("DELETE FROM blogs WHERE maNguoiDung = :id")
                .setParameter("id", userId).executeUpdate();

        // Cuối cùng xoá user
        em.createNativeQuery("DELETE FROM users WHERE maNguoiDung = :id").setParameter("id", userId).executeUpdate();
    }

    }

