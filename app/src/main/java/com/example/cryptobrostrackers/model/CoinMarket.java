package com.example.cryptobrostrackers.model;

import com.google.gson.annotations.SerializedName;
public class CoinMarket {

    private String id;
    private String symbol;
    private String name;

    @SerializedName("image")
    private String imageUrl;

    @SerializedName("current_price")
    private double currentPrice;

    @SerializedName("market_cap")
    private long marketCap;

    @SerializedName("price_change_percentage_24h")
    private double priceChangePct24h;

    public String getId(){
        return id;
    }

    public String getSymbol(){
        return symbol;
    }

    public String getName(){
        return name;
    }

    public String getImageUrl(){
        return imageUrl;
    }

    public double getCurrentPrice(){
        return currentPrice;
    }

    public long getMarketCap(){
        return marketCap;
    }

    public double getPriceChangePct24h(){
        return priceChangePct24h;
    }
}
