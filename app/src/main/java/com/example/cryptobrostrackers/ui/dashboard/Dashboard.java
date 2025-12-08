package com.example.cryptobrostrackers.ui.dashboard;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
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
            fetchChartData(coinId);
        } else {
            Toast.makeText(this, "No Coin ID found", Toast.LENGTH_SHORT).show();
        }
        //VIEW COMPAT
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars    .top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void fetchChartData(String id) {
        // Use the existing RetrofitClient
        CoinGeckoAPI api = RetrofitClient.getClient().create(CoinGeckoAPI.class);

        // Fetch 14 days of history
        Call<TickerChart> call = api.getMarketChart(id, "usd", "14");

        call.enqueue(new Callback<TickerChart>() {
            @Override
            public void onResponse(Call<TickerChart> call, Response<TickerChart> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<List<Double>> prices = response.body().getPrices();
                    setupChart(prices);
                } else {
                    Log.e("Dashboard", "Error Code: " + response.code());
                    Toast.makeText(Dashboard.this, "Failed to load chart", Toast.LENGTH_SHORT).show();
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
        for (List<Double> pair : priceList) {
            // pair[0] = timestamp, pair[1] = price
            float timestamp = pair.get(0).floatValue();
            float price = pair.get(1).floatValue();
            entries.add(new Entry(timestamp, price));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Price (USD)");
        dataSet.setColor(Color.BLUE);
        dataSet.setDrawCircles(false);
        dataSet.setLineWidth(2f);
        dataSet.setDrawValues(false);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        // Format X Axis to show dates
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new ValueFormatter() {
            private final SimpleDateFormat mFormat = new SimpleDateFormat("MM/dd", Locale.ENGLISH);
            @Override
            public String getFormattedValue(float value) {
                return mFormat.format(new Date((long) value));
            }
        });

        lineChart.getDescription().setEnabled(false);
        lineChart.invalidate();
    }

}