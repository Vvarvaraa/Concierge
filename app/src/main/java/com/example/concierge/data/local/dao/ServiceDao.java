package com.example.concierge.data.local.dao;

import androidx.room.*;
import com.example.concierge.data.local.entity.Service;
import java.util.List;

@Dao
public interface ServiceDao {
    @Insert
    long insert(Service service);

    @Update
    void update(Service service);

    @Delete
    void delete(Service service);

    @Query("SELECT * FROM service WHERE id_service = :id")
    Service getById(int id);

    @Query("SELECT * FROM service WHERE id_category = :categoryId AND is_available = 1")
    List<Service> getByCategory(int categoryId);

    @Query("SELECT * FROM service WHERE is_available = 1 ORDER BY name ASC")
    List<Service> getAllAvailable();

    @Query("SELECT s.* FROM service s " +
            "JOIN category_service cs ON s.id_category = cs.id_category " +
            "WHERE s.name LIKE '%' || :search || '%' OR cs.name LIKE '%' || :search || '%'")
    List<Service> searchServices(String search);
}
