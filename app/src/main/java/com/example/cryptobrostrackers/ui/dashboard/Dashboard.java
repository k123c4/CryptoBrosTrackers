package com.example.cryptobrostrackers.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.cryptobrostrackers.R;
import com.example.cryptobrostrackers.ui.Watchlist.Watchlist;
import com.example.cryptobrostrackers.ui.home.Home;

public class Dashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



//        ImageButton WatchlistButton = findViewById(R.id.addWlBt);
//        WatchlistButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(Dashboard.this, Home.class);
//                Toast.makeText(Dashboard.this, "Added to Watchlist", Toast.LENGTH_SHORT).show();
//                startActivity(intent);
//            }
//        });
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars    .top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}