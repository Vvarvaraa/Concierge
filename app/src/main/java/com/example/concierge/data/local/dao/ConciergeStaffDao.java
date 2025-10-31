package com.example.concierge.data.local.dao;

import androidx.room.*;
import com.example.concierge.data.local.entity.ConciergeStaff;
import java.util.List;

@Dao
public interface ConciergeStaffDao {
    @Insert
    long insert(ConciergeStaff staff);

    @Update
    void update(ConciergeStaff staff);

    @Delete
    void delete(ConciergeStaff staff);

    @Query("SELECT * FROM concierge_staff WHERE id_staff = :id")
    ConciergeStaff getById(int id);

    @Query("SELECT * FROM concierge_staff WHERE email = :email AND password_hash = :passwordHash")
    ConciergeStaff login(String email, String passwordHash);

    @Query("SELECT * FROM concierge_staff WHERE is_available = 1")
    List<ConciergeStaff> getAvailableStaff();

    @Query("UPDATE concierge_staff SET is_available = :available WHERE id_staff = :staffId")
    void setAvailability(int staffId, boolean available);

    @Query("UPDATE concierge_staff SET id_current_chat = :chatId WHERE id_staff = :staffId")
    void setCurrentChat(int staffId, Integer chatId);
}
