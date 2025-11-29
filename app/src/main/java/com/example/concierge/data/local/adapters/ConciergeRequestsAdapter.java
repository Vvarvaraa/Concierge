package com.example.concierge.data.local.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.concierge.R;
import com.example.concierge.data.local.entity.ConciergeSession;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ConciergeRequestsAdapter extends RecyclerView.Adapter<ConciergeRequestsAdapter.SessionViewHolder> {

    private List<ConciergeSession> sessions;
    private boolean isPendingList;
    private OnSessionClickListener listener;

    public interface OnSessionClickListener {
        void onSessionClick(ConciergeSession session); // Открыть чат или принять
        void onEndSessionClick(ConciergeSession session); // Завершить
    }

    public ConciergeRequestsAdapter(OnSessionClickListener listener, boolean isPendingList) {
        this.listener = listener;
        this.isPendingList = isPendingList;
    }

    public void setSessions(List<ConciergeSession> sessions) {
        this.sessions = sessions;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_concierge_session, parent, false);
        return new SessionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SessionViewHolder holder, int position) {
        if (sessions != null && position < sessions.size()) {
            ConciergeSession session = sessions.get(position);
            holder.bind(session, isPendingList, listener);
        }
    }

    @Override
    public int getItemCount() {
        return sessions != null ? sessions.size() : 0;
    }

    static class SessionViewHolder extends RecyclerView.ViewHolder {
        private TextView guestNameTextView;
        private TextView requestTextView;
        private TextView timeTextView;
        private Button actionButton;

        public SessionViewHolder(@NonNull View itemView) {
            super(itemView);
            guestNameTextView = itemView.findViewById(R.id.guest_name_textview);
            requestTextView = itemView.findViewById(R.id.request_textview);
            timeTextView = itemView.findViewById(R.id.time_textview);
            actionButton = itemView.findViewById(R.id.action_button);
        }

        public void bind(ConciergeSession session, boolean isPending, OnSessionClickListener listener) {
            guestNameTextView.setText(session.guest_name != null ? session.guest_name : "Гость #" + session.guest_id);
            requestTextView.setText(session.initial_request);

            if (session.start_time != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                timeTextView.setText(sdf.format(session.start_time));
            } else {
                timeTextView.setText("");
            }

            if (isPending) {
                actionButton.setText("Принять");
                // При нажатии "Принять" переходим в чат и меняем статус
                actionButton.setOnClickListener(v -> listener.onSessionClick(session));
                itemView.setOnClickListener(null); // В списке ожидания клик по элементу не должен работать, только кнопка
            } else {
                actionButton.setText("Завершить");
                actionButton.setOnClickListener(v -> listener.onEndSessionClick(session));
                // В активных сессиях клик по элементу открывает чат
                itemView.setOnClickListener(v -> listener.onSessionClick(session));
            }
        }
    }
}