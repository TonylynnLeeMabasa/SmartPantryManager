package com.example.smartpantrymanager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsActivity extends AppCompatActivity {

    private static final String PREFS_NAME =
            "smart_pantry_settings";

    private static final String EXPIRY_ALERTS_KEY =
            "expiry_alerts_enabled";

    private static final String LOW_STOCK_ALERTS_KEY =
            "low_stock_alerts_enabled";

    private SwitchMaterial expiryAlertsSwitch;
    private SwitchMaterial lowStockAlertsSwitch;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_settings
        );

        expiryAlertsSwitch =
                findViewById(
                        R.id.expiryAlertsSwitch
                );

        lowStockAlertsSwitch =
                findViewById(
                        R.id.lowStockAlertsSwitch
                );

        TextView backButton =
                findViewById(
                        R.id.backButton
                );

        preferences =
                getSharedPreferences(
                        PREFS_NAME,
                        MODE_PRIVATE
                );

        loadSavedSettings();

        // Save the expiry alert preference when changed.
        expiryAlertsSwitch.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    preferences
                            .edit()
                            .putBoolean(
                                    EXPIRY_ALERTS_KEY,
                                    isChecked
                            )
                            .apply();
                }
        );

        // Save the low-stock alert preference when changed.
        lowStockAlertsSwitch.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    preferences
                            .edit()
                            .putBoolean(
                                    LOW_STOCK_ALERTS_KEY,
                                    isChecked
                            )
                            .apply();
                }
        );

        // Return to the previous screen.
        backButton.setOnClickListener(
                v -> finish()
        );
    }

    // Load previously saved preferences.
    private void loadSavedSettings() {

        boolean expiryAlertsEnabled =
                preferences.getBoolean(
                        EXPIRY_ALERTS_KEY,
                        true
                );

        boolean lowStockAlertsEnabled =
                preferences.getBoolean(
                        LOW_STOCK_ALERTS_KEY,
                        true
                );

        expiryAlertsSwitch.setChecked(
                expiryAlertsEnabled
        );

        lowStockAlertsSwitch.setChecked(
                lowStockAlertsEnabled
        );
    }
}