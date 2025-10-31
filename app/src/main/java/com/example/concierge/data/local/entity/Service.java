package com.example.concierge.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;

@Entity(tableName = "service",
        foreignKeys = @ForeignKey(entity = CategoryService.class,
                parentColumns = "id_category",
                childColumns = "id_category",
                onDelete = ForeignKey.CASCADE))
public class Service {
    @PrimaryKey(autoGenerate = true)
    public int id_service;

    public int id_category;
    public String name;
    public String description;
    public double price;
    public String image_url;
    public boolean is_available;

    public Service(int id_category, String name, String description,
                   double price, String image_url, boolean is_available) {
        this.id_category = id_category;
        this.name = name;
        this.description = description;
        this.price = price;
        this.image_url = image_url;
        this.is_available = is_available;
    }
}