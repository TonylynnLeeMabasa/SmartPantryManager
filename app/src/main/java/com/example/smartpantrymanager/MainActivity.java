package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private TextView connectionStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        connectionStatus = findViewById(R.id.connectionStatus);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {

                    Insets systemBars = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars()
                    );

                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                }
        );

        connectionStatus.setText("Connecting to Firebase...");

        // Connect to the Smart Pantry Manager Firebase database
        databaseReference = FirebaseDatabase
                .getInstance(
                        "https://smart-pantry-manager-7e502-default-rtdb.europe-west1.firebasedatabase.app/"
                )
                .getReference();

        // Test Firebase connection
        databaseReference
                .child("test")
                .setValue("Firebase connected!")
                .addOnSuccessListener(unused -> {

                    connectionStatus.setText(
                            "Firebase connected successfully!"
                    );

                    Toast.makeText(
                            MainActivity.this,
                            "Firebase connected successfully!",
                            Toast.LENGTH_LONG
                    ).show();
                })
                .addOnFailureListener(e -> {

                    connectionStatus.setText(
                            "Firebase connection failed"
                    );

                    Toast.makeText(
                            MainActivity.this,
                            "Firebase connection failed: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }
}