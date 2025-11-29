package com.example.concierge.data.local;

import android.content.Context;
import androidx.room.Room;
import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import com.example.concierge.data.local.dao.*;
import com.example.concierge.data.local.entity.*;
import com.example.concierge.data.local.DateConverter;

@Database(entities = {
        Guest.class,
        CategoryService.class,
        Service.class,
        Order.class,
        ChatMessage.class,
        PushNotification.class,
        ConciergeStaff.class,
        ConciergeSession.class
}, version = 7, exportSchema = false)

@TypeConverters({DateConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract GuestDao guestDao();
    public abstract CategoryServiceDao categoryServiceDao();
    public abstract ServiceDao serviceDao();
    public abstract OrderDao orderDao();
    public abstract ChatMessageDao chatMessageDao();
    public abstract PushNotificationDao pushNotificationDao();
    public abstract ConciergeStaffDao conciergeStaffDao();
    public abstract ConciergeSessionDao conciergeSessionDao();
    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "concierge_db")
                            .fallbackToDestructiveMigration()
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}