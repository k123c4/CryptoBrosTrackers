package com.example.cryptobrostrackers.ui.Watchlist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cryptobrostrackers.R;
import com.example.cryptobrostrackers.database.Coin;
import com.example.cryptobrostrackers.database.CoinsRepository;

import java.util.ArrayList;

public class Watchlist extends AppCompatActivity {
    private RecyclerView wlCoins;
    private WatchlistAdapter adapter;
    private CoinsRepository repository;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_watchlist);

        // back to home
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Watchlist");
        }
        wlCoins = findViewById(R.id.wlCoins);
        wlCoins.setLayoutManager(new LinearLayoutManager(this));

        repository = new CoinsRepository(getApplication());

        adapter = new WatchlistAdapter(new WatchlistAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClick(Coin coin) {
                repository.delete(coin);
            }
        });
        wlCoins.setAdapter(adapter);

        // Observe the Room LiveData and update the list
        repository.getAllCoins().observe(this, coins -> {
            adapter.submitList(coins);
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish(); //action button support
        return true;
    }

}