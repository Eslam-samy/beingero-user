package com.corptia.bringero.ui.splash;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.corptia.bringero.R;
import com.corptia.bringero.ui.Main.MainActivity;
import com.facebook.shimmer.ShimmerFrameLayout;

public class SplashActivity extends AppCompatActivity {

    ShimmerFrameLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        container = findViewById(R.id.shimmer_view_container1);

        container.startShimmer();
        new Handler().postDelayed(() -> {

            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();


        }, 5000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        container.stopShimmer();
    }
}
