package com.example.cryptobrostrackers.ui.Watchlist;

import android.os.Bundle;
import android.util.Log;

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
import com.example.cryptobrostrackers.model.CoinMarket;
import com.example.cryptobrostrackers.network.CoinGeckoAPI;
import com.example.cryptobrostrackers.network.RetrofitClient;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Watchlist extends AppCompatActivity {

    private RecyclerView wlCoins;
    private WatchlistAdapter adapter;
    private CoinsRepository repository;

    private final Set<String> watchSymbols = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_watchlist);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Watchlist");
        }

        wlCoins = findViewById(R.id.wlCoins);
        wlCoins.setLayoutManager(new LinearLayoutManager(this));

        repository = new CoinsRepository(getApplication());

        adapter = new WatchlistAdapter(symbol -> {
            // delete from DB when trash icon pressed
            repository.deleteBySymbol(symbol.toLowerCase(Locale.ENGLISH));
        });

        wlCoins.setAdapter(adapter);

        // Observe Room for symbols
        repository.getAllCoins().observe(this, coins -> {
            watchSymbols.clear();
            for (Coin c : coins) {
                if (c.symbol != null) {
                    watchSymbols.add(c.symbol.toLowerCase(Locale.ENGLISH));
                }
            }

            if (watchSymbols.isEmpty()) {
                adapter.submitList(new ArrayList<>());
            } else {
                fetchWatchlistFromApi();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void fetchWatchlistFromApi() {
        CoinGeckoAPI api = RetrofitClient.getClient().create(CoinGeckoAPI.class);

        // Reuse the same markets call you use on the Home screen
        // Adjust the params to match your CoinGeckoAPI interface
        Call<List<CoinMarket>> call = api.getMarkets(
                "usd",
                "market_cap_desc",
                10,   // per_page
                1,     // page
                false  // sparkline
        );

        call.enqueue(new Callback<List<CoinMarket>>() {
            @Override
            public void onResponse(Call<List<CoinMarket>> call, Response<List<CoinMarket>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Log.e("Watchlist", "Markets API failed: " + response.code());
                    adapter.submitList(new ArrayList<>());
                    return;
                }

                List<CoinMarket> all = response.body();
                List<CoinMarket> filtered = new ArrayList<>();

                for (CoinMarket m : all) {
                    if (m.getSymbol() == null) continue;
                    String sym = m.getSymbol().toLowerCase(Locale.ENGLISH);
                    if (watchSymbols.contains(sym)) {
                        filtered.add(m);
                    }
                }

                adapter.submitList(filtered);
            }

            @Override
            public void onFailure(Call<List<CoinMarket>> call, Throwable t) {
                Log.e("Watchlist", "Markets API error", t);
                adapter.submitList(new ArrayList<>());
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
