package com.example.concierge.data.local.dao;

import androidx.room.*;
import com.example.concierge.data.local.entity.Order;
import java.util.List;

@Dao
public interface OrderDao {
    @Insert
    long insert(Order order);

    @Update
    void update(Order order);

    @Delete
    void delete(Order order);

    @Query("SELECT * FROM `order` WHERE id_order = :id")
    Order getById(int id);

    @Query("SELECT * FROM `order` WHERE id_guest = :guestId ORDER BY created_at DESC")
    List<Order> getByGuest(int guestId);

    @Query("SELECT * FROM `order` WHERE status = :status ORDER BY created_at ASC")
    List<Order> getByStatus(String status);

    @Query("UPDATE `order` SET status = :status WHERE id_order = :orderId")
    void updateStatus(int orderId, String status);

    @Query("SELECT o.* FROM `order` o " +
            "JOIN guests g ON o.id_guest = g.id_guest " +
            "WHERE g.room_number = :roomNumber")
    List<Order> getByRoomNumber(String roomNumber);
}
