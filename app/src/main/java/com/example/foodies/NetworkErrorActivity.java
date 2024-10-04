package com.example.foodies;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;


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


        retryButton.setOnClickListener(v -> checkConnectionAndProceed());

        // Start checking for internet connection
        checkConnectionAndProceed();
    }

    private void checkConnectionAndProceed() {
        new Handler().postDelayed(() -> {
            if (NetworkUtils.isInternetAvailable(NetworkErrorActivity.this)) {
                // Internet is available, navigate to RegisterActivity
                Intent intent = new Intent(NetworkErrorActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            } else {
                // Still no internet, check again
                checkConnectionAndProceed();
            }
        }, CHECK_DELAY);
    }
}