package com.example.foodies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;

// IM/2021/007 - Utility class for checking internet connectivity
public class NetworkUtils {

    // IM/2021/007 - Method to check if internet is available
    public static boolean isInternetAvailable(Context context) {
        // IM/2021/007 - Get the ConnectivityManager system service
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            // IM/2021/007 - Get the capabilities of the active network
            NetworkCapabilities capabilities = connectivityManager
                    .getNetworkCapabilities(connectivityManager.getActiveNetwork());
            // IM/2021/007 - Check if the network has internet capability
            return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
        }
        // IM/2021/007 - Return false if ConnectivityManager is not available
        return false;
    }
}
