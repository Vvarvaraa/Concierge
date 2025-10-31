package com.example.concierge.data.local.repository;

import android.content.Context;
import com.example.concierge.data.local.DatabaseClient;
import com.example.concierge.data.local.entity.ActiveChatSession;
import com.example.concierge.data.local.entity.ConciergeStaff;
import java.util.List;

public class ActiveChatSessionRepository {
    private final DatabaseClient databaseClient;
    private final Context context;

    public ActiveChatSessionRepository(Context context) {
        this.databaseClient = DatabaseClient.getInstance(context);
        this.context = context;
    }

    public long insertSession(ActiveChatSession session) {
        return databaseClient.getAppDatabase().activeChatSessionDao().insert(session);
    }

    public void updateSession(ActiveChatSession session) {
        databaseClient.getAppDatabase().activeChatSessionDao().update(session);
    }

    public void deleteSession(ActiveChatSession session) {
        databaseClient.getAppDatabase().activeChatSessionDao().delete(session);
    }

    public ActiveChatSession getSessionById(int id) {
        return databaseClient.getAppDatabase().activeChatSessionDao().getById(id);
    }

    public List<ActiveChatSession> getAwaitingSessions() {
        return databaseClient.getAppDatabase().activeChatSessionDao().getAwaitingSessions();
    }

    public List<ActiveChatSession> getAssignedSessions(int staffId) {
        return databaseClient.getAppDatabase().activeChatSessionDao().getAssignedSessions(staffId);
    }

    public ActiveChatSession getActiveSessionByGuest(int guestId) {
        return databaseClient.getAppDatabase().activeChatSessionDao().getActiveSessionByGuest(guestId);
    }

    public void assignSessionToStaff(int sessionId, int staffId) {
        databaseClient.getAppDatabase().activeChatSessionDao().assignToStaff(sessionId, staffId, "assigned");
    }

    public void resolveSession(int sessionId) {
        databaseClient.getAppDatabase().activeChatSessionDao().resolveSession(sessionId, new java.util.Date());
    }

    // Бизнес-логика: создание новой сессии чата
    public long createChatSession(int guestId) {
        ActiveChatSession session = new ActiveChatSession(guestId, null, "awaiting",
                new java.util.Date(), null);
        return insertSession(session);
    }

    // Бизнес-логика: назначение консьержа на сессию
    public boolean assignConciergeToSession(int sessionId) {
        ConciergeStaffRepository staffRepo = new ConciergeStaffRepository(context);
        ConciergeStaff availableStaff = staffRepo.getRandomAvailableStaff();

        if (availableStaff != null) {
            assignSessionToStaff(sessionId, availableStaff.id_staff);
            staffRepo.setStaffAvailability(availableStaff.id_staff, false);
            staffRepo.setStaffCurrentChat(availableStaff.id_staff, sessionId);
            return true;
        }
        return false;
    }

    // Бизнес-логика: завершение сессии чата
    public void completeChatSession(int sessionId, int staffId) {
        resolveSession(sessionId);

        ConciergeStaffRepository staffRepo = new ConciergeStaffRepository(context);
        staffRepo.releaseStaffFromChat(staffId);
    }

    // Бизнес-логика: получение сессии по гостю (или создание новой)
    public ActiveChatSession getOrCreateSessionForGuest(int guestId) {
        ActiveChatSession session = getActiveSessionByGuest(guestId);
        if (session == null) {
            long sessionId = createChatSession(guestId);
            session = getSessionById((int) sessionId);
        }
        return session;
    }
}