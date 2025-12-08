package com.example.cryptobrostrackers.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TickerChart {
    @SerializedName("prices")
    private List<List<Double>> prices;

    public List<List<Double>> getPrices() {
        return prices;
    }
}
