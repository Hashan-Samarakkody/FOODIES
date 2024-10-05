package com.example.foodies;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

@SuppressLint("CustomSplashScreen")
// Suppresses lint warning for custom splash screen usage IM/2022/070
public class SplashActivity extends AppCompatActivity {

    final int DELAY = 1500; // Delay in milliseconds IM/2022/070

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Calls the superclass constructor IM/2022/070
        setContentView(R.layout.activity_splash); // Sets the layout for the splash screen IM/2022/070

        if (!NetworkUtils.isInternetAvailable(this)) { // Checks if internet is available IM/2022/070
            // No internet connection, navigate to NetworkError activity IM/2022/070
            Intent intent = new Intent(SplashActivity.this, NetworkErrorActivity.class); // Creates intent for
            // NetworkErrorActivity
            // IM/2022/070
            startActivity(intent); // Starts the NetworkErrorActivity IM/2022/070
            finish(); // Closes the current activity IM/2022/070
        } else {
            // Internet is available, proceed to RegisterActivity after a delay IM/2022/070
            new Handler().postDelayed(() -> { // Delays the execution of the following code block IM/2022/070
                Intent intent = new Intent(SplashActivity.this, RegisterActivity.class); // Creates intent for
                // RegisterActivity IM/2022/070
                startActivity(intent); // Starts the RegisterActivity IM/2022/070
                finish(); // Closes the current activity IM/2022/070
            }, DELAY); // Sets the delay to the defined constant IM/2022/070
        }
    }
}
