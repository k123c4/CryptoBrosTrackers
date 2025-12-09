package com.example.cryptobrostrackers.ui.dashboard;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.cryptobrostrackers.R;
import com.example.cryptobrostrackers.model.TickerChart;
import com.example.cryptobrostrackers.network.CoinGeckoAPI;
import com.example.cryptobrostrackers.network.RetrofitClient;
import com.example.cryptobrostrackers.ui.Watchlist.Watchlist;
import com.example.cryptobrostrackers.ui.home.Home;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Dashboard extends AppCompatActivity {

    private LineChart lineChart;
    private String coinId;

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

        lineChart = findViewById(R.id.lineChart);

        // Retrieve the Coin ID passed from the Home screen adapter
        if (getIntent().hasExtra("coin_id")) {
            coinId = getIntent().getStringExtra("coin_id");
            String name = getIntent().getStringExtra("coin_name");
            String symbol = getIntent().getStringExtra("coin_symbol");
            double price = getIntent().getDoubleExtra("coin_price", 0);
            double change = getIntent().getDoubleExtra("coin_change", 0);
            long cap = getIntent().getLongExtra("coin_cap", 0);

            //defaults to 7 days
            fetchChartData(coinId, "7");
            //update UI text
            updateUI(name, symbol, price, change, cap);
        } else {
            Toast.makeText(this, "No Coin ID found", Toast.LENGTH_SHORT).show();
        }

        //time buttons for chart
        Button btn1D = findViewById(R.id.btn1D);
        Button btn7D = findViewById(R.id.btn7D);
        Button btn30D = findViewById(R.id.btn30D);

        if (btn1D != null) btn1D.setOnClickListener(v -> fetchChartData(coinId, "1"));
        if (btn7D != null) btn7D.setOnClickListener(v -> fetchChartData(coinId, "7"));
        if (btn30D != null) btn30D.setOnClickListener(v -> fetchChartData(coinId, "30"));

        //VIEW COMPAT
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void updateUI(String name, String symbol, double price, double change, long cap) {
        TextView tvName = findViewById(R.id.tvDetailName);
        TextView tvPrice = findViewById(R.id.tvDetailPrice);
        TextView tvChange = findViewById(R.id.tvDetailChange);
        TextView tvCap = findViewById(R.id.tvMarketCap);

        // check in case views aren't in XML yet
        if (tvName == null || tvPrice == null) return;

        if (name != null) {
            tvName.setText(name + " (" + (symbol != null ? symbol.toUpperCase() : "") + ")");
        }

        tvPrice.setText("$" + price);


        tvChange.setText(String.format("%.2f%%", change));
        if (change >= 0) {
            tvChange.setTextColor(Color.GREEN);
        } else {
            tvChange.setTextColor(Color.RED);
        }

        tvCap.setText("Market Cap: $" + cap);
    }

    private void fetchChartData(String id, String days) {
        // Use the existing RetrofitClient
        CoinGeckoAPI api = RetrofitClient.getClient().create(CoinGeckoAPI.class);

        // Fetch days of history
        Call<TickerChart> call = api.getMarketChart(id, "usd", days);

        call.enqueue(new Callback<TickerChart>() {
            @Override
            public void onResponse(Call<TickerChart> call, Response<TickerChart> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<List<Double>> prices = response.body().getPrices();
                    setupChart(prices);
                } else {
                    //warns you of rate limit instead of just saying failed to load
                    if (response.code() == 429) {
                        Log.e("Dashboard", "Rate Limit Hit (429)");
                        Toast.makeText(Dashboard.this, "Too fast! Please wait a moment.", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("Dashboard", "Error Code: " + response.code());
                        Toast.makeText(Dashboard.this, "Failed to load chart", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<TickerChart> call, Throwable t) {
                Log.e("Dashboard", "Network Fail: " + t.getMessage());
            }
        });
    }

    private void setupChart(List<List<Double>> priceList) {
        if (priceList == null || priceList.isEmpty()) return;

        List<Entry> entries = new ArrayList<>();

        long firstTimestamp = priceList.get(0).get(0).longValue();  // normalize

        for (List<Double> pair : priceList) {
            long time = pair.get(0).longValue();
            float normalizedX = (time - firstTimestamp) / 1000f; // seconds for precision
            float price = pair.get(1).floatValue();

            entries.add(new Entry(normalizedX, price));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Price (USD)");
        dataSet.setColor(Color.BLUE);
        dataSet.setDrawCircles(false);
        dataSet.setLineWidth(2f);
        dataSet.setDrawValues(false);

        lineChart.setData(new LineData(dataSet));

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        xAxis.setValueFormatter(new ValueFormatter() {
            private final SimpleDateFormat mFormat = new SimpleDateFormat("MM/dd", Locale.ENGLISH);

            @Override
            public String getFormattedValue(float value) {
                long millis = firstTimestamp + ((long) value * 1000L);
                return mFormat.format(new Date(millis));
            }
        });

        lineChart.getDescription().setEnabled(false);
        lineChart.invalidate();
    }


}