package com.example.muistutussovellus;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
/*
Sovelluksen pääohjelma. Vastaa muistutusten luomisesta ja näyttämisestä.
 */
public class MainActivity extends AppCompatActivity {

    private ListView noteListView;
    private ArrayAdapter<Note> noteListAdapter;
    private List<Note> notes;
    private Button addButton, expandButton, addTimeButton, closeTimerButton, closeEditButton, logoutButton;
    private LinearLayout editLayout, timerLayout;
    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;
    private String currentUserID;
    private EditText titleEditText, contentEditText, timeEditText, mapEditText;
    private TimePicker timePicker;
    private ActivityResultLauncher<Intent> mapLauncher;

    /*
    Asettaa kuuntelijat ja toiminnot eri sovelluksen komponentteihin ja valmistaa sovelluksen
    ottamaan vastaan karttatiedot toisesta activitysta.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            String locationName = data.getStringExtra("locationName");
                            if (locationName != null) {
                                mapEditText.setText(locationName);
                            }
                        }
                    }
                });

        noteListView = findViewById(R.id.noteListView);
        addButton = findViewById(R.id.addButton);
        titleEditText = findViewById(R.id.titleEditText);
        contentEditText = findViewById(R.id.contentEditText);
        expandButton = findViewById(R.id.expandButton);
        timePicker = findViewById(R.id.timePicker);
        addTimeButton = findViewById(R.id.addTimeButton);
        timeEditText = findViewById(R.id.timeEditText);
        closeTimerButton = findViewById(R.id.closeTimerButton);
        editLayout = findViewById(R.id.editLayout);
        timerLayout = findViewById(R.id.timerLayout);
        closeEditButton = findViewById(R.id.closeEditButton);
        mapEditText = findViewById(R.id.mapEditText);
        logoutButton = findViewById(R.id.logoutButton);


        notes = new ArrayList<>();
        noteListAdapter = new NoteListAdapter(this, notes);
        noteListView.setAdapter(noteListAdapter);

        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            currentUserID = mAuth.getCurrentUser().getUid();
        }

        mapEditText.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, MapActivity.class);
            mapLauncher.launch(intent);
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();

                Intent intent = new Intent(MainActivity.this, Start.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        closeEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandButton.setVisibility(View.VISIBLE);

                titleEditText.setVisibility(View.GONE);
                contentEditText.setVisibility(View.GONE);
                editLayout.setVisibility(View.GONE);
                timeEditText.setVisibility(View.GONE);
                mapEditText.setVisibility(View.GONE);
            }
        });

        closeTimerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePicker.setVisibility(View.GONE);
                timerLayout.setVisibility(View.GONE);

                titleEditText.setVisibility(View.VISIBLE);
                contentEditText.setVisibility(View.VISIBLE);
                editLayout.setVisibility(View.VISIBLE);
                timeEditText.setVisibility(View.VISIBLE);
                mapEditText.setVisibility(View.VISIBLE);
            }
        });

        expandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandButton.setVisibility(View.GONE);

                titleEditText.setVisibility(View.VISIBLE);
                contentEditText.setVisibility(View.VISIBLE);
                editLayout.setVisibility(View.VISIBLE);
                timeEditText.setVisibility(View.VISIBLE);
                mapEditText.setVisibility(View.VISIBLE);
            }
        });

        timeEditText.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                timePicker.setVisibility(View.VISIBLE);
                titleEditText.setVisibility(View.GONE);
                contentEditText.setVisibility(View.GONE);
                editLayout.setVisibility(View.GONE);
                timeEditText.setVisibility(View.GONE);
                mapEditText.setVisibility(View.GONE);
                timerLayout.setVisibility(View.VISIBLE);
            }
        });

        addTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int hour = timePicker.getHour();
                int minute = timePicker.getMinute();

                String selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
                timeEditText.setText(selectedTime);
                timePicker.setVisibility(View.GONE);
                timerLayout.setVisibility(View.GONE);
                titleEditText.setVisibility(View.VISIBLE);
                contentEditText.setVisibility(View.VISIBLE);
                editLayout.setVisibility(View.VISIBLE);
                timeEditText.setVisibility(View.VISIBLE);
                mapEditText.setVisibility(View.VISIBLE);
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleEditText.getText().toString();
                String content = contentEditText.getText().toString();
                String time = timeEditText.getText().toString();
                String location = mapEditText.getText().toString();

                Note note = new Note();
                note.setTitle(title);
                note.setContent(content);
                note.setUserID(currentUserID);
                note.setTime(time);
                note.setLocation(location);
                note.changeActive(false);

                if (currentUserID != null) {
                    addNoteToFirestore(note);
                    expandButton.setVisibility(View.VISIBLE);

                    titleEditText.setVisibility(View.GONE);
                    contentEditText.setVisibility(View.GONE);
                    editLayout.setVisibility(View.GONE);
                    timeEditText.setVisibility(View.GONE);
                    mapEditText.setVisibility(View.GONE);
                } else {
                    Toast.makeText(MainActivity.this, "User not authenticated", Toast.LENGTH_SHORT).show();
                }

                titleEditText.setText("");
                contentEditText.setText("");
                timeEditText.setText("");
                mapEditText.setText("");
            }
        });

        fetchNotesFromFirestore();
    }
    /*
    Hakee käyttäjän tallentamat musitiinpanot firestoresta.
     */
    private void fetchNotesFromFirestore() {
        firestore.collection("notes")
                .whereEqualTo("userID", currentUserID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        notes.clear();
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Note note = documentSnapshot.toObject(Note.class);
                            note.setId(documentSnapshot.getId());
                            notes.add(note);
                        }
                        noteListAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Failed to fetch notes", Toast.LENGTH_SHORT).show();
                        Log.d("FAIL", String.valueOf(e));
                    }
                });
    }
    /*
    Metodi lisää muistutuksen firestoreen.
     */
    private void addNoteToFirestore(Note note) {
        firestore.collection("notes")
                .add(note)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        note.setId(documentReference.getId());
                        notes.add(note);
                        noteListAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Failed to add note", Toast.LENGTH_SHORT).show();
                        Log.d("FAIL", String.valueOf(e));
                    }
                });

    }
    /*
    Luokka pääohjelman sisällä, vastaa listanäkymästä ja sen toiminnoista.
     */
    public class NoteListAdapter extends ArrayAdapter<Note> {

        private Context context;
        /*
        Rakennin perii ArrayAdapterin ja asettaa muistutukset tiedoiksi
         */
        public NoteListAdapter(Context context, List<Note> notes) {
            super(context, 0, notes);
            this.context = context;
        }
        /*
        Asettaa listviewin näkymän ja asettaa kuuntelijat muistutuksen painamiselle, poistamiselle ja
        aktiivisuudelle.
         */
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_note, parent, false);
            }

            TextView titleTextView = convertView.findViewById(R.id.noteTitleTextView);
            Button deleteButton = convertView.findViewById(R.id.deleteButton);
            CheckBox activeCheckbox = convertView.findViewById(R.id.activeCheckbox);

            Note note = getItem(position);

            if (note != null) {
                titleTextView.setText(note.getTitle());
                activeCheckbox.setChecked(note.isActive());

                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        displayNoteDetails(note);
                    }
                });

                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteNoteFromDatabase(note);
                    }
                });

                activeCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                         updateNoteActiveStatus(note, activeCheckbox.isChecked()); }
                });
            }

            return convertView;
        }
        /*
        Metodi näyttää kutsuttaessa valitun muistutuksen kaikki tiedot huomio ikkunassa.
         */
        private void displayNoteDetails(Note note) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
            dialogBuilder.setTitle("Note Details");
            dialogBuilder.setMessage("Title: " + note.getTitle() + "\n"
                    + "Content: " + note.getContent() + "\n"
                    + "Time: " + note.getTime() + "\n"
                    + "Location: " + note.getLocation());
            dialogBuilder.setPositiveButton("OK", null);
            AlertDialog dialog = dialogBuilder.create();
            dialog.show();
        }

        /*
        Metodi poistaa kutsuttaessa valitun muistutuksen firestoresta.
         */
        private void deleteNoteFromDatabase(Note note) {
            firestore.collection("notes")
                    .document(note.getId())
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            notes.remove(note);
                            noteListAdapter.notifyDataSetChanged();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "Failed to delete note", Toast.LENGTH_SHORT).show();
                            Log.d("FAIL", String.valueOf(e));
                        }
                    });
        }
        /*
        Metodi päivittää kutsuttaessa muistutuksen aktiivisuus tilan firestoreen.
         */
        private void updateNoteActiveStatus(Note note, boolean isActive) {
            DocumentReference noteRef = firestore.collection("notes").document(note.getId());

            Map<String, Object> updates = new HashMap<>();
            updates.put("active", isActive);

            noteRef.update(updates)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            note.changeActive(isActive);
                            noteListAdapter.notifyDataSetChanged();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "Failed to update note", Toast.LENGTH_SHORT).show();
                            Log.d("FAIL", String.valueOf(e));
                        }
                    });
        }
    }

}