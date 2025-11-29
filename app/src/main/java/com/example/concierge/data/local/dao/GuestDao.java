package com.example.concierge.data.local.dao;

import androidx.room.*;
import com.example.concierge.data.local.entity.Guest;
import java.util.List;

@Dao
public interface GuestDao {
    @Insert
    long insert(Guest guest);

    @Update
    void update(Guest guest);

    @Delete
    void delete(Guest guest);

    @Query("SELECT * FROM guests WHERE id_guest = :id")
    Guest getById(int id);

    @Query("SELECT * FROM guests WHERE room_number = :room_number AND LOWER(last_name) = LOWER(:last_name) LIMIT 1")
    Guest getByRoomAndName(String room_number, String last_name);

    @Query("SELECT * FROM guests ORDER BY created_at DESC")
    List<Guest> getAll();

    @Query("SELECT * FROM guests WHERE room_number = :roomNumber")
    List<Guest> getByRoomNumber(String roomNumber);
}
