package com.example.concierge.data.local.repository;

import android.content.Context;
import com.example.concierge.data.local.DatabaseClient;
import com.example.concierge.data.local.entity.CategoryService;
import java.util.List;

public class CategoryServiceRepository {
    private final DatabaseClient databaseClient;

    public CategoryServiceRepository(Context context) {
        this.databaseClient = DatabaseClient.getInstance(context);
    }

    public long insertCategory(CategoryService category) {
        return databaseClient.getAppDatabase().categoryServiceDao().insert(category);
    }

    public void updateCategory(CategoryService category) {
        databaseClient.getAppDatabase().categoryServiceDao().update(category);
    }

    public void deleteCategory(CategoryService category) {
        databaseClient.getAppDatabase().categoryServiceDao().delete(category);
    }

    public CategoryService getCategoryById(int id) {
        return databaseClient.getAppDatabase().categoryServiceDao().getById(id);
    }

    public List<CategoryService> getAllCategories() {
        return databaseClient.getAppDatabase().categoryServiceDao().getAll();
    }

    public List<CategoryService> searchCategories(String search) {
        return databaseClient.getAppDatabase().categoryServiceDao().searchByName(search);
    }

    // Бизнес-логика: получение категорий с услугами
    public List<CategoryService> getCategoriesWithServices() {
        return getAllCategories(); // В будущем можно добавить JOIN с услугами
    }
}
