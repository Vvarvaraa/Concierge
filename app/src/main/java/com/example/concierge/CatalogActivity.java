package com.example.concierge;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class CatalogActivity extends AppCompatActivity {

    private Button btnFoodDrinks, btnSpa, btnTransfer, btnHousehold;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        btnFoodDrinks = findViewById(R.id.btn_food_drinks);
        btnSpa = findViewById(R.id.btn_spa);
        btnTransfer = findViewById(R.id.btn_transfer);
        btnHousehold = findViewById(R.id.btn_household);
    }

    private void setupClickListeners() {
        // Кнопка "Назад" - возврат к чату
        findViewById(android.R.id.content).setOnClickListener(v -> finish());

        // Обработчики для кнопок категорий
        btnFoodDrinks.setOnClickListener(v -> openFoodDrinksActivity());
        btnSpa.setOnClickListener(v -> openSpaActivity());
        btnTransfer.setOnClickListener(v -> openTransferActivity());
        btnHousehold.setOnClickListener(v -> openHouseholdActivity());
    }

    private void openFoodDrinksActivity() {
        Intent intent = new Intent(CatalogActivity.this, FoodDrinksActivity.class);
        startActivity(intent);
    }

    private void openSpaActivity() {
       // Intent intent = new Intent(CatalogActivity.this, SpaActivity.class);
        //startActivity(intent);
    }

    private void openTransferActivity() {
        //Intent intent = new Intent(CatalogActivity.this, TransferActivity.class);
        //startActivity(intent);
    }

    private void openHouseholdActivity() {
        //Intent intent = new Intent(CatalogActivity.this, HouseholdActivity.class);
        //startActivity(intent);
    }

    // Обработка кнопки "Назад" системы
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Анимация закрытия (опускание вниз)
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
    }
}