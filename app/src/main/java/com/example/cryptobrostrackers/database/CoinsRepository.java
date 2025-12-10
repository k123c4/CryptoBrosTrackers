package com.example.cryptobrostrackers.database;

import android.app.Application;

import androidx.core.util.Consumer;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CoinsRepository {

    public enum AddResult { ADDED, ALREADY_IN_LIST }

    private final CoinsDao dao;
    private final LiveData<List<Coin>> allCoins;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public CoinsRepository(Application application) {
        CoinsDatabase db = CoinsDatabase.getInstance(application);
        dao = db.coinsDao();
        allCoins = dao.getAllCoins();
    }

    public LiveData<List<Coin>> getAllCoins() {
        return allCoins;
    }

    /**
     * Save a full snapshot (name, symbol, price, etc.) if symbol is not already in watchlist.
     */
    public void addCoinSnapshotIfNotExists(
            String symbol,
            String name,
            double price,
            double change24h,
            long marketCap,
            String imageUrl,
            Consumer<AddResult> callback
    ) {
        executor.execute(() -> {
            int count = dao.exists(symbol);
            if (count > 0) {
                if (callback != null) callback.accept(AddResult.ALREADY_IN_LIST);
            } else {
                Coin coin = new Coin(symbol, name, price, change24h, marketCap, imageUrl);
                dao.insert(coin);
                if (callback != null) callback.accept(AddResult.ADDED);
            }
        });
    }

    public void delete(Coin coin) {
        executor.execute(() -> dao.delete(coin));
    }
}
