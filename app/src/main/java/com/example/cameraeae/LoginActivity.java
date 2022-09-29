package com.example.cameraeae;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cameraeae.CameraActivity;
import com.google.android.material.button.MaterialButton;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        TextView username = findViewById(R.id.username);
        TextView password = findViewById(R.id.password);

        MaterialButton loginbutton = (MaterialButton) findViewById(R.id.loginbutton);

        //admin and admin

        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(username.getText().toString().equals("admin") && password.getText().toString().equals("admin")){
                    //correct
                    startActivity(new Intent(LoginActivity.this, OpenCameraActivity.class));
                    Toast.makeText(LoginActivity.this,"LOGIN SUCCESFULL",Toast.LENGTH_SHORT).show();
                }else
                    Toast.makeText(LoginActivity.this,"LOGIN FAILED",Toast.LENGTH_SHORT).show();
                //incorrect
            }
        });

    }
}