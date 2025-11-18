package com.example.cryptobrostrackers.ui.home;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cryptobrostrackers.R;
import com.example.cryptobrostrackers.adapter.CoinAdapter;
import com.example.cryptobrostrackers.model.CoinMarket;
import com.example.cryptobrostrackers.network.CoinGeckoAPI;
import com.example.cryptobrostrackers.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Home extends AppCompatActivity {

    private RecyclerView rvCoins;
    private CoinAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        rvCoins = findViewById(R.id.rvCoins);
        rvCoins.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CoinAdapter(this, new ArrayList<>());
        rvCoins.setAdapter(adapter);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loadCoinsFromApi();
    }

    private void loadCoinsFromApi() {
        CoinGeckoAPI api = RetrofitClient.getClient().create(CoinGeckoAPI.class);

        Call<List<CoinMarket>> call = api.getMarkets("usd", "market_cap_desc",
                100, 1, false);

        call.enqueue(new Callback<List<CoinMarket>>() {
            @Override
            public void onResponse(Call<List<CoinMarket>> call, Response<List<CoinMarket>> response) {
                if(response.isSuccessful() && response.body() != null) {
                    adapter.updateData(response.body());
                } else {
                    Toast.makeText(Home.this, "API returned empty response", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<CoinMarket>> call, Throwable t) {
                Toast.makeText(Home.this, "API error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}