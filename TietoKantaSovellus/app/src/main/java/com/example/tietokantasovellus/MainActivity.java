package com.example.tietokantasovellus;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView entryListView;
    private ArrayAdapter<Entry> entryListAdapter;
    private List<Entry> entries;
    private EditText editNumero, editNimi, editPainos, editHankinta;
    private Button addButton, deleteButton;

    private EntryDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        entryListView = findViewById(R.id.entryListView);
        editNumero = findViewById(R.id.editNumero);
        editNimi = findViewById(R.id.editNimi);
        editPainos = findViewById(R.id.editPainos);
        editHankinta = findViewById(R.id.editHankinta);
        addButton = findViewById(R.id.addButton);
        deleteButton = findViewById(R.id.deleteButton);

        entries = new ArrayList<>();
        entryListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, entries);
        entryListView.setAdapter(entryListAdapter);

        database = Room.databaseBuilder(getApplicationContext(), EntryDatabase.class, "taskari-db")
                .build();

        loadEntriesFromDatabase();

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numero = editNumero.getText().toString();
                String nimi = editNimi.getText().toString();
                String painos = editPainos.getText().toString();
                String hankinta = editHankinta.getText().toString();

                Entry entry = new Entry();
                entry.setNimi(nimi);
                entry.setNumero(Integer.valueOf(numero));
                entry.setHankinta(hankinta);
                entry.setPainos(Integer.valueOf(painos));

                addEntry(entry);

                editNumero.setText("");
                editNimi.setText("");
                editPainos.setText("");
                editHankinta.setText("");
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });

        sortEntriesByNumero();
    }

    private void deleteFirstEntry() {
        if (entries.isEmpty()) {
            Toast.makeText(this, "No entries to delete", Toast.LENGTH_SHORT).show();
            return;
        }

        Entry firstEntry = entries.get(0);

        new Thread(new Runnable() {
            @Override
            public void run() {
                database.entryDao().deleteEntry(firstEntry);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        entries.remove(firstEntry);
                        entryListAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    private void addEntry(Entry entry) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                database.entryDao().insert(entry);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        entries.add(entry);
                        sortEntriesByNumero();
                        entryListAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    private void loadEntriesFromDatabase() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Entry> loadedEntries = database.entryDao().getAllEntries();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        entries.clear();
                        entries.addAll(loadedEntries);
                        entryListAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    private void sortEntriesByNumero() {
        Collections.sort(entries, new Comparator<Entry>() {
            @Override
            public int compare(Entry entry1, Entry entry2) {
                return Integer.compare(entry1.getNumero(), entry2.getNumero());
            }
        });
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation")
                .setMessage("Delete First?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteFirstEntry();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}