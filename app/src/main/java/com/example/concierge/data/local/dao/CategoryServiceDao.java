package com.example.concierge.data.local.dao;

import androidx.room.*;
import com.example.concierge.data.local.entity.CategoryService;
import java.util.List;

@Dao
public interface CategoryServiceDao {
    @Insert
    long insert(CategoryService category);

    @Update
    void update(CategoryService category);

    @Delete
    void delete(CategoryService category);

    @Query("SELECT * FROM category_service WHERE id_category = :id")
    CategoryService getById(int id);

    @Query("SELECT * FROM category_service ORDER BY order_index ASC")
    List<CategoryService> getAll();

    @Query("SELECT * FROM category_service WHERE name LIKE '%' || :search || '%'")
    List<CategoryService> searchByName(String search);
}
