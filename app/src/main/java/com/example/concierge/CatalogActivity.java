package com.example.concierge;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.concierge.data.local.entity.Guest;

public class CatalogActivity extends AppCompatActivity {

    private Button btnFoodDrinks, btnSpa, btnTransfer, btnHousehold;
    private androidx.cardview.widget.CardView catalogCard;
    private Guest currentGuest;
    private ActivityResultLauncher<Intent> orderActivityLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        currentGuest = (Guest) getIntent().getSerializableExtra("guest");

        setupActivityResultLauncher();

        initViews();
        setupClickListeners();
        animateCatalogIn();
        setupBackPressHandler();
    }
    private void setupActivityResultLauncher() {
        orderActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.hasExtra("order_message")) {
                            String orderMessage = data.getStringExtra("order_message");

                            Intent resultToChat = new Intent();
                            resultToChat.putExtra("final_message", orderMessage); // Используем другой ключ для ChatActivity
                            setResult(Activity.RESULT_OK, resultToChat);
                            finish();
                        }
                    }
                }
        );
    }

    private void initViews() {
        catalogCard = findViewById(R.id.catalog_card);
        btnFoodDrinks = findViewById(R.id.btn_food_drinks);
        btnSpa = findViewById(R.id.btn_spa);
        btnTransfer = findViewById(R.id.btn_transfer);
        btnHousehold = findViewById(R.id.btn_household);
    }

    private void setupClickListeners() {
        btnFoodDrinks.setOnClickListener(v -> openFoodDrinksActivity());
        btnSpa.setOnClickListener(v -> openSpaActivity());
        btnTransfer.setOnClickListener(v -> openTransferActivity());
        btnHousehold.setOnClickListener(v -> openHouseholdActivity());
    }


    private void openFoodDrinksActivity() {
        Intent intent = new Intent(CatalogActivity.this, FoodDrinksActivity.class);
        intent.putExtra("guest", currentGuest);
        orderActivityLauncher.launch(intent);
    }

    private void openSpaActivity() {
        Intent intent = new Intent(CatalogActivity.this, SpaActivity.class);
        intent.putExtra("guest", currentGuest);
        orderActivityLauncher.launch(intent);
    }

    private void openTransferActivity() {
        Intent intent = new Intent(CatalogActivity.this, TransferActivity.class);
        intent.putExtra("guest", currentGuest);
        orderActivityLauncher.launch(intent);
    }

    private void openHouseholdActivity() {
        Intent intent = new Intent(CatalogActivity.this, HouseholdActivity.class);
        intent.putExtra("guest", currentGuest);
        orderActivityLauncher.launch(intent);
    }

    private void animateCatalogIn() {
        catalogCard.setVisibility(View.VISIBLE);
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        catalogCard.startAnimation(slideUp);
        catalogCard.startAnimation(fadeIn);
    }

    private void setupBackPressHandler() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Animation slideDown = AnimationUtils.loadAnimation(CatalogActivity.this, R.anim.slide_down);
                Animation fadeOut = AnimationUtils.loadAnimation(CatalogActivity.this, R.anim.fade_out);
                catalogCard.startAnimation(slideDown);
                catalogCard.startAnimation(fadeOut);
                slideDown.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        catalogCard.setVisibility(View.GONE);
                        finish();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }
}