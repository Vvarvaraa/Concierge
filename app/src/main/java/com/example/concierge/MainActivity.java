package com.example.concierge;

import com.example.concierge.data.local.entity.ConciergeStaff;
import com.example.concierge.data.local.repository.ConciergeStaffRepository;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.concierge.data.local.entity.Guest;
import com.example.concierge.data.local.repository.GuestRepository;

public class MainActivity extends AppCompatActivity {

    private EditText etRoom, etFirstName, etLastName;
    private Button btnContinue;
    private GuestRepository guestRepository;
    private ConciergeStaffRepository staffRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        guestRepository = new GuestRepository(this);
        staffRepository = new ConciergeStaffRepository(this);

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        etRoom = findViewById(R.id.etRoom);
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        btnContinue = findViewById(R.id.btnContinue);
    }

    private void setupClickListeners() {
        btnContinue.setOnClickListener(v -> handleLoginOrRegistration());
    }

    private void handleLoginOrRegistration() {
        String roomNumber = etRoom.getText().toString().trim();
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();

        if (roomNumber.isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {
            showError("Заполните все поля");
            return;
        }
        new Thread(() -> {
            try {
                if (roomNumber.equals("0501") && firstName.equalsIgnoreCase("Анна") && lastName.equalsIgnoreCase("Волкова")) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "ВХОД КОНСЬЕРЖА АННЫ ВОЛКОВОЙ", Toast.LENGTH_LONG).show();
                        ConciergeStaff anna = new ConciergeStaff();
                        anna.id_staff = 1;
                        anna.first_name = "Анна";
                        anna.last_name = "Волкова";
                        anna.password_hash = "0501";
                        openConciergeDashboard(anna);
                    });
                    return;
                }
                if (roomNumber.equals("0102") && firstName.equalsIgnoreCase("Сергей") && lastName.equalsIgnoreCase("Соколов")) {
                    runOnUiThread(() -> {
                        ConciergeStaff s = new ConciergeStaff();
                        s.id_staff = 2;
                        s.first_name = "Сергей";
                        s.last_name = "Соколов";
                        s.password_hash = "0102";
                        openConciergeDashboard(s);
                    });
                    return;
                }
                // Если не консьерж — обычная логика
                ConciergeStaff staff = staffRepository.getByFullName(firstName, lastName);
                if (staff != null && staff.password_hash.equals(roomNumber)) {
                    runOnUiThread(() -> openConciergeDashboard(staff));
                    return;
                }
                Guest guest = guestRepository.getGuestByRoomAndName(roomNumber, lastName);
                if (guest != null) {
                    if (guest.first_name.equalsIgnoreCase(firstName)) {
                        runOnUiThread(() -> openChatActivity(guest));
                    } else {
                        runOnUiThread(() -> showError("Имя не совпадает"));
                    }
                    return;
                }
                runOnUiThread(() -> registerNewGuest(roomNumber, firstName, lastName));
            } catch (Exception e) {
                runOnUiThread(() -> showError("Ошибка: " + e.getMessage()));
            }
        }).start();
    }
    private void openConciergeDashboard(ConciergeStaff staff) {
        Intent intent = new Intent(MainActivity.this, ConciergeDashboardActivity.class);
        intent.putExtra("staff", staff);
        intent.putExtra("staff_id", staff.id_staff);
        startActivity(intent);
        finish();
    }

    private boolean isValidName(String name) {
        return name.matches("[a-zA-Zа-яА-ЯёЁ\\s]+");
    }

    private void registerNewGuest(String roomNumber, String firstName, String lastName) {
        new Thread(() -> {
            try {
                long guestId = guestRepository.registerGuest(roomNumber, firstName, lastName, "", "ru");

                runOnUiThread(() -> {
                    if (guestId > 0) {
                        Guest newGuest = guestRepository.getGuestById((int) guestId);
                        showSuccess("Регистрация успешна!");
                        openChatActivity(newGuest);
                    } else {
                        showError("Ошибка регистрации");
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> showError("Ошибка регистрации: " + e.getMessage()));
            }
        }).start();
    }

    private void openChatActivity(Guest guest) {
        Intent intent = new Intent(MainActivity.this, ChatActivity.class);
        intent.putExtra("guest", guest); // Передаём весь объект гостя
        startActivity(intent);
        finish();
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void showSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
