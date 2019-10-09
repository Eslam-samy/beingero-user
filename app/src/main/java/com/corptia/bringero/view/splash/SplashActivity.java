package com.corptia.bringero.view.splash;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.corptia.bringero.R;
import com.corptia.bringero.view.Main.MainActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {

            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();


        }, 1000);
    }
}
