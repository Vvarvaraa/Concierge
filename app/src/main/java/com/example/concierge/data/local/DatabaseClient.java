package com.example.concierge.data.local;

import android.content.Context;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class DatabaseClient {
    private static DatabaseClient instance;
    private AppDatabase appDatabase;
    private Context context;

    private DatabaseClient(Context context) {
        this.context = context;
        appDatabase = Room.databaseBuilder(context, AppDatabase.class, "concierge_db")
                .fallbackToDestructiveMigration()
                .addCallback(new RoomDatabase.Callback() {
                    @Override
                    public void onCreate(@androidx.annotation.NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        // Заполняем базу тестовыми данными при создании
                        initializeSampleData();
                    }
                })
                .build();
    }

    public static synchronized DatabaseClient getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseClient(context);
        }
        return instance;
    }

    public AppDatabase getAppDatabase() {
        return appDatabase;
    }

    private void initializeSampleData() {
        new Thread(() -> {
            DatabaseInitializer.initializeSampleData(appDatabase);
        }).start();
    }
}