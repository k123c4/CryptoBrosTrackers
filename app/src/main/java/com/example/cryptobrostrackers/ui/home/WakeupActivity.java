package com.example.cryptobrostrackers.ui.home;

import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Looper;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.cryptobrostrackers.R;
import com.example.cryptobrostrackers.network.RetrofitClient;
import com.example.cryptobrostrackers.network.WakeupAPI;
import com.google.gson.JsonObject;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WakeupActivity extends AppCompatActivity {

    private TextView statusText;
    private int retryCount = 0;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final int RETRY_DELAY_MS = 15000;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_wakeup);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        statusText = findViewById(R.id.statusText);
        ImageView waterImage = findViewById(R.id.waterImage);

        // Start the loop
        handler.post(wakeupRunnable);
    }

    // Runnable loop
    private final Runnable wakeupRunnable = new Runnable() {
        @Override
        public void run() {
            pingServer();
        }
    };

    private void pingServer() {
        new Thread(() -> {

            boolean awake =  false;

            try {
                Retrofit retrofit = RetrofitClient.getClient();

                WakeupAPI api = retrofit.create(WakeupAPI.class);
                Response<JsonObject> response = api.wakeup().execute();

                Log.d("WAKEUP", "Response code = " + response.code());

                awake = response.isSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if(awake) {
                runOnUiThread(() -> goToHome());
            } else {
                retry();
                handler.postDelayed(wakeupRunnable, RETRY_DELAY_MS);
            }
        }).start();
    }
    private void retry() {
        retryCount++;
        runOnUiThread(() ->
                statusText.setText("Backend sleeping... Attempt " + retryCount));
    }

    private void goToHome() {
        startActivity(new Intent(this, Home.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(wakeupRunnable); // cleanup
    }

}

