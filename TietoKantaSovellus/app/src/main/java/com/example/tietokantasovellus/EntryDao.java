package com.example.tietokantasovellus;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface EntryDao {
    @Insert
    void insert(Entry entry);

    @Query("SELECT * FROM entries")
    List<Entry> getAllEntries();

    @Delete
    void deleteEntry(Entry entry);
}
