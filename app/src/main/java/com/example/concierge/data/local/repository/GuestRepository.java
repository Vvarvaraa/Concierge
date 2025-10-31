package com.example.concierge.data.local.repository;

import android.content.Context;
import androidx.annotation.WorkerThread; // Добавлено для явного указания потока
import com.example.concierge.data.local.DatabaseClient;
import com.example.concierge.data.local.entity.Guest;
import java.util.List;
import java.util.Date;

public class GuestRepository {
    private final DatabaseClient databaseClient;


    public GuestRepository(Context context) {
        Context appContext = context.getApplicationContext();
        this.databaseClient = DatabaseClient.getInstance(appContext);
    }

    @WorkerThread
    public long insertGuest(Guest guest) {
        return databaseClient.getAppDatabase().guestDao().insert(guest);
    }

    @WorkerThread
    public void updateGuest(Guest guest) {
        databaseClient.getAppDatabase().guestDao().update(guest);
    }

    @WorkerThread
    public void deleteGuest(Guest guest) {
        databaseClient.getAppDatabase().guestDao().delete(guest);
    }

    @WorkerThread
    public Guest getGuestById(int id) {
        return databaseClient.getAppDatabase().guestDao().getById(id);
    }

    @WorkerThread
    public Guest getGuestByRoomAndName(String roomNumber, String lastName) {
        return databaseClient.getAppDatabase().guestDao().getByRoomAndName(roomNumber, lastName);
    }

    @WorkerThread
    public List<Guest> getAllGuests() {
        return databaseClient.getAppDatabase().guestDao().getAll();
    }

    @WorkerThread
    public List<Guest> getGuestsByRoomNumber(String roomNumber) {
        return databaseClient.getAppDatabase().guestDao().getByRoomNumber(roomNumber);
    }

    @WorkerThread
    public long registerGuest(String roomNumber, String firstName, String lastName,
                              String phoneNumber, String language) {
        Guest guest = new Guest(roomNumber, firstName, lastName, phoneNumber, language, new Date(System.currentTimeMillis()));
        return insertGuest(guest);
    }
}