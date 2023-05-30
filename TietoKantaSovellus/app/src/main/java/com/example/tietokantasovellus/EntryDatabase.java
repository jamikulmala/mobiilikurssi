package com.example.tietokantasovellus;

import android.content.Context;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.Room;

@Database(entities = {Entry.class}, version = 1)
public abstract class EntryDatabase extends RoomDatabase {
    private static EntryDatabase instance;

    public abstract EntryDao entryDao();

    public static synchronized EntryDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            EntryDatabase.class, "entry_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
