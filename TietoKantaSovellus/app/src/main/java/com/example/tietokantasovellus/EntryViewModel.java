package com.example.tietokantasovellus;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class EntryViewModel extends AndroidViewModel {
    private EntryRepository entryRepository;
    private LiveData<List<Entry>> allEntries;

    public EntryViewModel(Application application) {
        super(application);
        entryRepository = new EntryRepository(application);
        allEntries = entryRepository.getAllEntries();
    }

    public void insert(Entry entry) {
        entryRepository.insert(entry);
    }

    public LiveData<List<Entry>> getAllEntries() {
        return allEntries;
    }
}
