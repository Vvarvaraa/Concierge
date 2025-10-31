package com.example.concierge.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "category_service")
public class CategoryService {
    @PrimaryKey(autoGenerate = true)
    public int id_category;

    public String name;
    public int order_index;

    public CategoryService(String name, int order_index) {
        this.name = name;
        this.order_index = order_index;
    }
}
