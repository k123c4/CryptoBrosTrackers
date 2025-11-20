package com.example.cryptobrostrackers.ui.home;

import android.os.Bundle;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Looper;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.example.cryptobrostrackers.R;
import com.example.cryptobrostrackers.network.WakeupAPI;
import com.example.cryptobrostrackers.ui.home.Home;
import com.google.gson.JsonObject;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WakeupActivity extends AppCompatActivity {

    private TextView statusText;
    private int retryCount = 0;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final int RETRY_DELAY_MS = 3000;



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
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://cryptobros-backend.onrender.com/")
                        .addConverterFactory(GsonConverterFactory.create()).build();

                WakeupAPI api = retrofit.create(WakeupAPI.class);
                Response<JsonObject> response = api.wakeup().execute();

                awake = response.isSuccessful();
            } catch (Exception ignored) {

            }

            if(awake) {
                runOnUiThread(this::goToHome);
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

