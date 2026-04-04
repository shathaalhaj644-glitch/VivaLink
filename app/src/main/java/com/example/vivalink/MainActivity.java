package com.example.vivalink;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vivalink.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnLogin = findViewById(R.id.btnMainLogin);
        Button btnSignUp = findViewById(R.id.btnMainSignUp);


        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });


        btnSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DonorSignUpActivity.class);
            startActivity(intent);
        });
    }
}
