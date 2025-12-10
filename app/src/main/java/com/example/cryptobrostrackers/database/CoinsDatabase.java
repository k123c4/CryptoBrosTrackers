package com.example.cryptobrostrackers.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(
        entities = {Coin.class},
        version = 1,
        exportSchema = false
)
public abstract class CoinsDatabase extends RoomDatabase {

    private static volatile CoinsDatabase INSTANCE;

    public abstract CoinsDao coinsDao();

    public static CoinsDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (CoinsDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    CoinsDatabase.class,
                                    "coins_db"
                            )
                            .fallbackToDestructiveMigration() // ok while developing
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
