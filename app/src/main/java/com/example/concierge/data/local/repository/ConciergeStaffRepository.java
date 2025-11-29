package com.example.concierge.data.local.repository;

import android.content.Context;

import com.example.concierge.data.local.AppDatabase;
import com.example.concierge.data.local.dao.ConciergeStaffDao;
import com.example.concierge.data.local.entity.ConciergeStaff;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConciergeStaffRepository {
    private final ConciergeStaffDao staffDao;
    private final ExecutorService executorService;

    public ConciergeStaffRepository(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        this.staffDao = database.conciergeStaffDao();
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public ConciergeStaff getRandomAvailableStaff() {
        List<ConciergeStaff> availableStaff = staffDao.getAvailableStaff();
        if (availableStaff != null && !availableStaff.isEmpty()) {
            return availableStaff.get(0);
        }
        return null;
    }

    public ConciergeStaff getByFullName(String firstName, String lastName) {
        return staffDao.getByFullName(firstName, lastName);
    }

    public void setStaffAvailability(int staffId, boolean isAvailable) {
        executorService.execute(() -> staffDao.setAvailability(staffId, isAvailable));
    }

    public void setStaffCurrentChat(int staffId, Integer sessionId) {
        executorService.execute(() -> staffDao.setCurrentChat(staffId, sessionId));
    }

    public void releaseStaffFromChat(int staffId) {
        executorService.execute(() -> {
            staffDao.setAvailability(staffId, true);
            staffDao.setCurrentChat(staffId, null);
        });
    }

    public ConciergeStaff getStaffById(int staffId) {
        return staffDao.getById(staffId);
    }

    public void updateStaff(ConciergeStaff staff) {
        executorService.execute(() -> staffDao.update(staff));
    }

    public void insertStaff(ConciergeStaff staff) {
        executorService.execute(() -> staffDao.insert(staff));
    }

    public void deleteStaff(ConciergeStaff staff) {
        executorService.execute(() -> staffDao.delete(staff));
    }

    public ConciergeStaff login(String email, String passwordHash) {
        return staffDao.login(email, passwordHash);
    }

    public List<ConciergeStaff> getAvailableStaff() {
        return staffDao.getAvailableStaff();
    }
}