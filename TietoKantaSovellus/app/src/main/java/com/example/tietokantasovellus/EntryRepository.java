package com.example.tietokantasovellus;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class EntryRepository {
    private EntryDao entryDao;
    private LiveData<List<Entry>> allEntries;

    public EntryRepository(Application application) {
        EntryDatabase database = EntryDatabase.getInstance(application);
        entryDao = database.entryDao();
        allEntries = (LiveData<List<Entry>>) entryDao.getAllEntries();
    }

    public void insert(Entry entry) {
        // Perform database insertion in a background thread
        new InsertEntryAsyncTask(entryDao).execute(entry);
    }

    public LiveData<List<Entry>> getAllEntries() {
        return allEntries;
    }

    private static class InsertEntryAsyncTask extends AsyncTask<Entry, Void, Void> {
        private EntryDao entryDao;

        private InsertEntryAsyncTask(EntryDao entryDao) {
            this.entryDao = entryDao;
        }

        @Override
        protected Void doInBackground(Entry... entries) {
            entryDao.insert(entries[0]);
            return null;
        }
    }
}
