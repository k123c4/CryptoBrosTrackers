package com.example.cryptobrostrackers.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "coins")
public class Coin {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    public String symbol;   // e.g. "BTC"

    public Coin(@NonNull String symbol) {
        this.symbol = symbol;
    }
}