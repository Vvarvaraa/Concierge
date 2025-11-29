package com.example.concierge.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.Query;

import com.example.concierge.data.local.entity.ConciergeStaff;

import java.util.List;

@Dao
public interface ConciergeStaffDao {

    @Insert
    void insert(ConciergeStaff staff);

    @Update
    void update(ConciergeStaff staff);

    @Delete
    void delete(ConciergeStaff staff);

    @Query("SELECT * FROM concierge_staff WHERE LOWER(first_name) = LOWER(:firstName) AND LOWER(last_name) = LOWER(:lastName) LIMIT 1")
    ConciergeStaff getByFullName(String firstName, String lastName);

    @Query("SELECT * FROM concierge_staff WHERE is_available = 1")
    List<ConciergeStaff> getAvailableStaff();

    // По ID
    @Query("SELECT * FROM concierge_staff WHERE id_staff = :staffId LIMIT 1")
    ConciergeStaff getById(int staffId);

    // Обновить статус доступности
    @Query("UPDATE concierge_staff SET is_available = :isAvailable WHERE id_staff = :staffId")
    void setAvailability(int staffId, boolean isAvailable);

    // Обновить текущую сессию чата
    @Query("UPDATE concierge_staff SET current_chat_session_id = :sessionId WHERE id_staff = :staffId")
    void setCurrentChat(int staffId, Integer sessionId);

    // Логин по email и паролю (если вдруг понадобится)
    @Query("SELECT * FROM concierge_staff WHERE email = :email AND password_hash = :passwordHash LIMIT 1")
    ConciergeStaff login(String email, String passwordHash);
}