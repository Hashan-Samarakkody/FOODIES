package com.example.foodies;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.credentials.exceptions.domerrors.NetworkError;

public class NetworkErrorActivity extends AppCompatActivity {

    private final int CHECK_DELAY = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_error);

        TextView errorMessage = findViewById(R.id.errorMessage);
        Button retryButton = findViewById(R.id.retryButton);

        // Set an initial message
        errorMessage.setText("No Internet Connection!\nPlease check you network settings");


        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkConnectionAndProceed();
            }
        });

        // Start checking for internet connection
        checkConnectionAndProceed();
    }

    private void checkConnectionAndProceed() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (NetworkUtils.isInternetAvailable(NetworkErrorActivity.this)) {
                    // Internet is available, navigate to RegisterActivity
                    Intent intent = new Intent(NetworkErrorActivity.this, RegisterActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Still no internet, check again
                    checkConnectionAndProceed();
                }
            }
        }, CHECK_DELAY);
    }
}