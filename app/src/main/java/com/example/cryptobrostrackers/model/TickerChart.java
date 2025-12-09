package com.example.cryptobrostrackers.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TickerChart {
    @SerializedName("prices")
    private List<List<Double>> prices;

    @SerializedName("market_caps")
    private List<List<Double>> marketCaps;

    @SerializedName("total_volumes")
    private List<List<Double>> totalVolumes;

    public List<List<Double>> getPrices() {
        return prices;
    }

    public List<List<Double>> getMarketCaps() {
        return marketCaps;
    }

    public List<List<Double>> getTotalVolumes() {
        return totalVolumes;
    }

}




