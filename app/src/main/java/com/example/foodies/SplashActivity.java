package com.example.foodies;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    final int DELAY = 1500;  // Delay in milliseconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (!NetworkUtils.isInternetAvailable(this)) {
            // No internet connection, navigate to NetworkError activity
            Intent intent = new Intent(SplashActivity.this, NetworkErrorActivity.class);
            startActivity(intent);
            finish();
        } else {
            // Internet is available, proceed to RegisterActivity after a delay
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(SplashActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }, DELAY);
        }
    }
}
