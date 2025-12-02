package com.example.cryptobrostrackers.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(
        entities = {Coin.class},
        version = 1,
        exportSchema = true
)
public abstract class CoinsDatabase extends RoomDatabase {

    public abstract CoinsDao coinsDao();
    private static volatile CoinsDatabase INSTANCE;

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase db) {
            // Example:
        }
    };

    public static CoinsDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (CoinsDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    CoinsDatabase.class,
                                    "coins_db"        // database file
                            )
                            // .addMigrations(MIGRATION_1_2)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
