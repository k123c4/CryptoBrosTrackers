package com.example.cryptobrostrackers.database;

import androidx.lifecycle.LiveData;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;
public interface CoinsDao {
    @Query("SELECT * FROM coins ORDER BY symbol ASC")
    LiveData<List<Coin>> getAllCoins();

    // Insert coin into database
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(Coin coin);

    // Delete by object
    @Delete
    void delete(Coin coin);

    @Query("DELETE FROM coins WHERE symbol = :symbol")
    void deleteBySymbol(String symbol);

    @Query("SELECT COUNT(*) FROM coins WHERE symbol = :symbol")
    int exists(String symbol);
}
