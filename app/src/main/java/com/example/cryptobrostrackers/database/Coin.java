package com.example.cryptobrostrackers.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "coins")
public class Coin {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    public String symbol;      // e.g. "btc"

    @NonNull
    public String name;        // e.g. "Bitcoin"

    public double price;       // snapshot price in USD
    public double change24h;   // 24h % change
    public long marketCap;     // market cap
    public String imageUrl;    // icon URL (can be null)

    public Coin(@NonNull String symbol,
                @NonNull String name,
                double price,
                double change24h,
                long marketCap,
                String imageUrl) {

        this.symbol = symbol;
        this.name = name;
        this.price = price;
        this.change24h = change24h;
        this.marketCap = marketCap;
        this.imageUrl = imageUrl;
    }
}
