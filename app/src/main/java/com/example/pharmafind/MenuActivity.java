package com.example.pharmafind;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {

    private TextView welcomeTextView;
    private Button findHospitalsButton;
    private Button aboutButton;
    private Button profileButton;
    private Button logoutButton;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        mAuth = FirebaseAuth.getInstance();

        welcomeTextView = findViewById(R.id.welcomeTextView);
        findHospitalsButton = findViewById(R.id.findHospitalsButton);
        aboutButton = findViewById(R.id.aboutButton);
        profileButton = findViewById(R.id.profileButton);
        logoutButton = findViewById(R.id.logoutButton);

        // Retrieve the logged-in username from the intent
        String username = getIntent().getStringExtra("username");

        // Set the welcome message with the logged-in username
        String welcomeMessage = "Welcome, " + username + "!";
        welcomeTextView.setText(welcomeMessage);

        // Set click listeners for the buttons
        findHospitalsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start the FindHospitalsActivity
                startActivity(new Intent(MenuActivity.this, MapsActivity.class));
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start the ProfileActivity
                startActivity(new Intent(MenuActivity.this, ProfileActivity.class));
            }
        });

        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start the ProfileActivity
                startActivity(new Intent(MenuActivity.this, WebActivity.class));
            }
        });
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });

    }
    private void logoutUser() {
        mAuth.signOut();
        // Redirect to the login page
        Intent intent = new Intent(MenuActivity.this, WelcomeActivity.class);
        startActivity(intent);
        finish(); // Optional: finish the MenuActivity so that the user can't navigate back to it using the back button
        Toast.makeText(MenuActivity.this, "Succesfully logged out", Toast.LENGTH_SHORT).show();
    }
}