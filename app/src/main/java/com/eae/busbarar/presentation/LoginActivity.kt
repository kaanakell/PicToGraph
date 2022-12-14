package com.eae.busbarar.presentation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;


import com.eae.busbarar.R;
import com.google.android.material.button.MaterialButton;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextView username = findViewById(R.id.username);
        TextView password = findViewById(R.id.password);

        MaterialButton loginbutton = (MaterialButton) findViewById(R.id.loginbutton);

        loginbutton.setOnClickListener(view -> {
            if(username.getText().toString().equals("admin") && password.getText().toString().equals("admin")) {
                startActivity(new Intent(LoginActivity.this, OpenCameraActivity.class));
                Toast.makeText(LoginActivity.this,"LOGIN SUCCESFULL",Toast.LENGTH_SHORT).show();
            }else
                Toast.makeText(LoginActivity.this,"LOGIN FAILED",Toast.LENGTH_SHORT).show();
        });

    }
}