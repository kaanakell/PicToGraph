package com.eae.busbarar.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.eae.busbarar.R;

public class OpenCameraActivity extends AppCompatActivity {

    private Button button;
    private int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opencamera);

        button = (Button)findViewById(R.id.openCamera);
        button.setOnClickListener(v -> openCameraActivityButton());

    }

    private void openCameraActivityButton(){
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        counter++;
        if(counter == 2){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }
}
