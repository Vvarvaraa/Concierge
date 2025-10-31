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

    @Query("SELECT * FROM guests WHERE room_number = :roomNumber AND last_name = :lastName")
    Guest getByRoomAndName(String roomNumber, String lastName);

    @Query("SELECT * FROM guests ORDER BY created_at DESC")
    List<Guest> getAll();

    @Query("SELECT * FROM guests WHERE room_number = :roomNumber")
    List<Guest> getByRoomNumber(String roomNumber);
}
