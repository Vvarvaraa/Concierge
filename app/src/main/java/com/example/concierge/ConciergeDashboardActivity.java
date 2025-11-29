package com.example.concierge;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.concierge.data.local.adapters.ConciergeRequestsAdapter;
import com.example.concierge.data.local.entity.ConciergeSession;
import com.example.concierge.data.local.entity.ConciergeStaff;
import com.example.concierge.data.local.repository.ConciergeSessionRepository;
import com.example.concierge.data.local.repository.ConciergeStaffRepository;

public class ConciergeDashboardActivity extends AppCompatActivity implements ConciergeRequestsAdapter.OnSessionClickListener {

    private ConciergeSessionRepository sessionRepository;
    private RecyclerView pendingRequestsRecyclerView;
    private RecyclerView activeSessionsRecyclerView;
    private ConciergeRequestsAdapter pendingAdapter;
    private ConciergeRequestsAdapter activeAdapter;
    private ConciergeStaff currentStaff;
    private TextView tvHeaderTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_concierge_dashboard);

        // Получаем консьержа
        if (getIntent().hasExtra("staff")) {
            currentStaff = (ConciergeStaff) getIntent().getSerializableExtra("staff");
        } else if (getIntent().hasExtra("staff_id")) {
            int staffId = getIntent().getIntExtra("staff_id", -1);
            if (staffId != -1) {
                currentStaff = new ConciergeStaffRepository(this).getStaffById(staffId);
            }
        }

        if (currentStaff == null) {
            Toast.makeText(this, "Ошибка авторизации консьержа", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        tvHeaderTitle = findViewById(R.id.header_title);
        tvHeaderTitle.setText("Консьерж: " + currentStaff.first_name + " " + currentStaff.last_name);

        sessionRepository = new ConciergeSessionRepository(getApplication());

        initViews();
        setupObservers();
    }

    private void initViews() {
        pendingRequestsRecyclerView = findViewById(R.id.pending_requests_recycler);
        activeSessionsRecyclerView = findViewById(R.id.active_sessions_recycler);

        pendingAdapter = new ConciergeRequestsAdapter(this, true);
        activeAdapter = new ConciergeRequestsAdapter(this, false);

        pendingRequestsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        pendingRequestsRecyclerView.setAdapter(pendingAdapter);

        activeSessionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        activeSessionsRecyclerView.setAdapter(activeAdapter);
    }

    private void setupObservers() {
        sessionRepository.getPendingRequests().observe(this, sessions -> pendingAdapter.setSessions(sessions));
        sessionRepository.getActiveSessions().observe(this, sessions -> activeAdapter.setSessions(sessions));
    }

    @Override
    public void onSessionClick(ConciergeSession session) {
        if ("pending".equals(session.status)) {
            new Thread(() -> sessionRepository.startSession(session.id_session, (long) currentStaff.id_staff)).start();
        }

        Intent intent = new Intent(this, ConciergeChatActivity.class);
        intent.putExtra("session_id", session.id_session);
        intent.putExtra("guest_id", session.guest_id);
        intent.putExtra("guest_name", session.guest_name);
        startActivity(intent);
    }

    @Override
    public void onEndSessionClick(ConciergeSession session) {
        new Thread(() -> {
            sessionRepository.endSession(session.guest_id);
            runOnUiThread(() -> Toast.makeText(this, "Сессия с " + session.guest_name + " завершена", Toast.LENGTH_SHORT).show());
        }).start();
    }
}