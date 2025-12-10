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
    private static final int MAX_API_RETRIES = 5;
    private static final long BASE_RETRY_DELAY_MS = 500L;
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
        fetchWatchlistFromApi(0);
    }

    private void fetchWatchlistFromApi(int attempt) {
        // Stop if too many tries
        if (attempt > MAX_API_RETRIES) {
            runOnUiThread(() ->
                    android.widget.Toast.makeText(
                            Watchlist.this,
                            "Could not load watchlist after several attempts.",
                            android.widget.Toast.LENGTH_LONG
                    ).show()
            );
            return;
        }

        CoinGeckoAPI api = RetrofitClient.getClient().create(CoinGeckoAPI.class);

        Call<List<CoinMarket>> call = api.getMarkets(
                "usd",
                "market_cap_desc",
                20,   // per_page
                1,     // page
                false  // sparkline
        );

        call.enqueue(new Callback<List<CoinMarket>>() {
            @Override
            public void onResponse(Call<List<CoinMarket>> call, Response<List<CoinMarket>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    int code = response.code();
                    Log.e("Watchlist", "Markets API failed: " + code + " (attempt " + attempt + ")");

                    if (code >= 500 && code < 600) {
                        // 5xx = server problem → retry with backoff
                        scheduleRetry(attempt);
                    } else {
                        // 4xx or other issues: don't hammer API
                        runOnUiThread(() ->
                                android.widget.Toast.makeText(
                                        Watchlist.this,
                                        "Error loading watchlist (API " + code + ")",
                                        android.widget.Toast.LENGTH_SHORT
                                ).show()
                        );
                    }
                    return;
                }

                List<CoinMarket> all = response.body();
                List<CoinMarket> filtered = new ArrayList<>();

                for (CoinMarket m : all) {
                    if (m.getSymbol() == null) continue;
                    String sym = m.getSymbol().toLowerCase(java.util.Locale.ENGLISH);
                    if (watchSymbols.contains(sym)) {
                        filtered.add(m);
                    }
                }

                adapter.submitList(filtered);
            }

            @Override
            public void onFailure(Call<List<CoinMarket>> call, Throwable t) {
                Log.e("Watchlist", "Markets API error on attempt " + attempt, t);
                // Network failure – retry with backoff
                scheduleRetry(attempt);
            }
        });
    }

    private void scheduleRetry(int currentAttempt) {
        int nextAttempt = currentAttempt + 1;

        // Exponential backoff: 1s, 2s, 4s, 8s... capped at 30s
        long delay = (long) Math.min(
                30000,
                BASE_RETRY_DELAY_MS * Math.pow(2, currentAttempt)
        );

        Log.d("Watchlist", "Scheduling retry " + nextAttempt + " in " + delay + " ms");

        new android.os.Handler(android.os.Looper.getMainLooper())
                .postDelayed(() -> fetchWatchlistFromApi(nextAttempt), delay);
    }



    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
