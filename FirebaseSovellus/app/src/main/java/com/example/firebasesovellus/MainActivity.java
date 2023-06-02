package com.example.firebasesovellus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView entryListView;
    private ArrayAdapter<Entry> entryListAdapter;
    private List<Entry> entries;
    private EditText editNumero, editNimi, editPainos, editHankinta;
    private Button addButton, deleteButton;

    private FirebaseFirestore firestore;

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

        firestore = FirebaseFirestore.getInstance();

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numero = editNumero.getText().toString();
                String nimi = editNimi.getText().toString();
                String painos = editPainos.getText().toString();
                String hankinta = editHankinta.getText().toString();

                Entry entry = new Entry();
                entry.setNimi(nimi);
                entry.setNumero(Integer.parseInt(numero));
                entry.setHankinta(hankinta);
                entry.setPainos(Integer.parseInt(painos));

                addEntryToFirestore(entry);

                editNumero.setText("");
                editNimi.setText("");
                editPainos.setText("");
                editHankinta.setText("");
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (entries.size() > 0) {
                    deleteEntryFromFirestore(entries.get(0).getId());
                }
            }
        });

        fetchEntriesFromFirestore();
    }

    private void fetchEntriesFromFirestore() {
        firestore.collection("entries").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        entries.clear();
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Entry entry = documentSnapshot.toObject(Entry.class);
                            entry.setId(documentSnapshot.getId());
                            entries.add(entry);
                        }
                        entryListAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void addEntryToFirestore(Entry entry) {
        firestore.collection("entries").add(entry)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        entry.setId(documentReference.getId());
                        entries.add(entry);
                        entryListAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void deleteEntryFromFirestore(String entryId) {
        firestore.collection("entries").document(entryId).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        for (Entry entry : entries) {
                            if (entry.getId().equals(entryId)) {
                                entries.remove(entry);
                                break;
                            }
                        }
                        entryListAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
}