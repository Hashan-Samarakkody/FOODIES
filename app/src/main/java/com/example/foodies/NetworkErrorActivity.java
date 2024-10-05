package com.example.foodies;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

// IM/2021/007 - Activity to display a network error message and retry connection
public class NetworkErrorActivity extends AppCompatActivity {

    // IM/2021/007 - Delay time for checking network connection in milliseconds
    private final int CHECK_DELAY = 2000;

    // IM/2021/007 - Create the activity and set up UI elements
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_error);

        // IM/2021/007 - Find the error message TextView and retry Button by their IDs
        TextView errorMessage = findViewById(R.id.errorMessage);
        Button retryButton = findViewById(R.id.retryButton);

        // IM/2021/007 - Set an initial error message for the user
        errorMessage.setText("No Internet Connection!\nPlease check your network settings");

        // IM/2021/007 - Set up click listener for the retry button
        retryButton.setOnClickListener(v -> checkConnectionAndProceed());

        // IM/2021/007 - Start checking for internet connection immediately
        checkConnectionAndProceed();
    }

    // IM/2021/007 - Method to check internet connection and proceed if available
    private void checkConnectionAndProceed() {
        new Handler().postDelayed(() -> {
            // IM/2021/007 - Check if internet is available using a utility method
            if (NetworkUtils.isInternetAvailable(NetworkErrorActivity.this)) {
                // IM/2021/007 - If internet is available, navigate to RegisterActivity
                Intent intent = new Intent(NetworkErrorActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish(); // IM/2021/007 - Finish current activity
            } else {
                // IM/2021/007 - If no internet, check again after the delay
                checkConnectionAndProceed();
            }
        }, CHECK_DELAY); // IM/2021/007 - Delay before the next check
    }
}
