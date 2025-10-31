package com.example.concierge;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        guestRepository = new GuestRepository(this);
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

        if (!isValidName(firstName) || !isValidName(lastName)) {
            showError("Имя и фамилия должны содержать только буквы");
            return;
        }

        new Thread(() -> {
            try {
                Guest guest = guestRepository.getGuestByRoomAndName(roomNumber, lastName);

                runOnUiThread(() -> {
                    if (guest != null) {
                        if (!guest.first_name.equalsIgnoreCase(firstName)) {
                            showError("Имя не совпадает с данными бронирования");
                        } else {
                            showSuccess("Добро пожаловать, " + guest.first_name + "!");
                            openChatActivity(guest);
                        }
                    } else {
                        registerNewGuest(roomNumber, firstName, lastName);
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> showError("Ошибка: " + e.getMessage()));
            }
        }).start();
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
