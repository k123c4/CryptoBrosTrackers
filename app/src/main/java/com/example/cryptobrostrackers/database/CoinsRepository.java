package com.example.cryptobrostrackers.database;

import android.app.Application;

import androidx.core.util.Consumer;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CoinsRepository {

    // Outcomes when trying to add a coin
    public enum AddResult { ADDED, ALREADY_IN_LIST }

    private final CoinsDao dao;
    private final LiveData<List<Coin>> allCoins;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public CoinsRepository(Application app) {
        CoinsDatabase db = CoinsDatabase.getDatabase(app);
        dao = db.coinsDao();
        allCoins = dao.getAllCoins();
    }

    // For RecyclerView
    public LiveData<List<Coin>> getAllCoins() {
        return allCoins;
    }

    // Attempts to add a new coin (ticker symbol only).
    // Runs in background and reports result via callback (like your FandomRepository).
    public void tryAddCoin(String symbol, Consumer<AddResult> callback) {
        executor.execute(() -> {
            // Normalize symbol if you want consistency
            String normSymbol = symbol.toUpperCase();

            int count = dao.exists(normSymbol);
            if (count > 0) {
                callback.accept(AddResult.ALREADY_IN_LIST);
                return;
            }

            long id = dao.insert(new Coin(normSymbol));
            if (id == -1) {
                callback.accept(AddResult.ALREADY_IN_LIST);
            } else {
                callback.accept(AddResult.ADDED);
            }
        });
    }

    // Direct insert without callback
    public void insert(Coin coin) {
        executor.execute(() -> dao.insert(coin));
    }

    public void delete(Coin coin) {
        executor.execute(() -> dao.delete(coin));
    }

}
