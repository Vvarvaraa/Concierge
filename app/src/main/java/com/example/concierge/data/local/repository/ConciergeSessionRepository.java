package com.example.concierge.data.local.repository;

import android.content.Context;
import androidx.lifecycle.LiveData;
import com.example.concierge.data.local.DatabaseClient;
import com.example.concierge.data.local.dao.ConciergeSessionDao;
import com.example.concierge.data.local.entity.ConciergeSession;
import java.util.Date;
import java.util.List;

public class ConciergeSessionRepository {
    private final ConciergeSessionDao sessionDao;

    public ConciergeSessionRepository(Context context) {
        this.sessionDao = DatabaseClient.getInstance(context).getAppDatabase().conciergeSessionDao();
    }

    public void createConciergeRequest(long guestId, String guestName, String initialRequest) {
        ConciergeSession session = new ConciergeSession(guestId, guestName, initialRequest);
        new Thread(() -> sessionDao.insert(session)).start();
    }

    public boolean hasActiveSession(long guestId) {
        try {
            return sessionDao.hasActiveSession(guestId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public LiveData<List<ConciergeSession>> getPendingRequests() {
        return sessionDao.getPendingRequests();
    }

    public LiveData<List<ConciergeSession>> getActiveSessions() {
        return sessionDao.getActiveSessions();
    }

    public void startSession(Long sessionId, Long conciergeId) {
        new Thread(() -> sessionDao.startSession(sessionId, conciergeId, new Date())).start();
    }

    public void endSession(Long guestId) {
        new Thread(() -> sessionDao.endSession(guestId, new Date())).start();
    }
}