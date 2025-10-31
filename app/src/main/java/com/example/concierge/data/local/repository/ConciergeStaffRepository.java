package com.example.concierge.data.local.repository;

import android.content.Context;
import com.example.concierge.data.local.DatabaseClient;
import com.example.concierge.data.local.entity.ConciergeStaff;
import java.util.List;

public class ConciergeStaffRepository {
    private final DatabaseClient databaseClient;

    public ConciergeStaffRepository(Context context) {
        this.databaseClient = DatabaseClient.getInstance(context);
    }

    public long insertStaff(ConciergeStaff staff) {
        return databaseClient.getAppDatabase().conciergeStaffDao().insert(staff);
    }

    public void updateStaff(ConciergeStaff staff) {
        databaseClient.getAppDatabase().conciergeStaffDao().update(staff);
    }

    public void deleteStaff(ConciergeStaff staff) {
        databaseClient.getAppDatabase().conciergeStaffDao().delete(staff);
    }

    public ConciergeStaff getStaffById(int id) {
        return databaseClient.getAppDatabase().conciergeStaffDao().getById(id);
    }

    public ConciergeStaff loginStaff(String email, String passwordHash) {
        return databaseClient.getAppDatabase().conciergeStaffDao().login(email, passwordHash);
    }

    public List<ConciergeStaff> getAvailableStaff() {
        return databaseClient.getAppDatabase().conciergeStaffDao().getAvailableStaff();
    }

    public void setStaffAvailability(int staffId, boolean available) {
        databaseClient.getAppDatabase().conciergeStaffDao().setAvailability(staffId, available);
    }

    public void setStaffCurrentChat(int staffId, Integer chatId) {
        databaseClient.getAppDatabase().conciergeStaffDao().setCurrentChat(staffId, chatId);
    }

    // Бизнес-логика: авторизация сотрудника
    public ConciergeStaff authenticateStaff(String email, String password) {
        // В реальном приложении пароль должен хэшироваться
        String passwordHash = hashPassword(password);
        return loginStaff(email, passwordHash);
    }

    // Бизнес-логика: получение случайного доступного консьержа
    public ConciergeStaff getRandomAvailableStaff() {
        List<ConciergeStaff> availableStaff = getAvailableStaff();
        if (!availableStaff.isEmpty()) {
            return availableStaff.get(0); // Берем первого доступного
        }
        return null;
    }

    // Бизнес-логика: освобождение сотрудника (завершение чата)
    public void releaseStaffFromChat(int staffId) {
        setStaffCurrentChat(staffId, null);
        setStaffAvailability(staffId, true);
    }

    private String hashPassword(String password) {
        // Простая заглушка - в реальном приложении используйте BCrypt или аналоги
        return String.valueOf(password.hashCode());
    }
}
