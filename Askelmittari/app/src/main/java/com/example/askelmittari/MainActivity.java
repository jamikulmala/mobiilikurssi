package com.example.askelmittari;

import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private boolean countingSteps = false;
    private int stepCount = 0;
    private TextView stepCountText;
    private Button startButton;
    private Button stopButton;
    private Button saveButton;
    private SharedPreferences sharedPreferences;

    // Alustaa käyttöliittymän komponentit sovelluksen luodessa,
    // hakee viittaukset painikkeisiin ja tekstikenttään, sekä määrittelee
    // anturien hallinnan ja kiihtyvyysanturin.
    // Lisäksi se alustaa SharedPreferences-olion askelmäärän tallentamiseen.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stepCountText = findViewById(R.id.stepCountText);
        startButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);
        saveButton = findViewById(R.id.saveButton);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        sharedPreferences = getSharedPreferences("AskelMäärä", MODE_PRIVATE);
    }

    // Kun "Aloita" painiketta klikataan, Tämä metodi aloittaa askelmäärän
    // laskennan asettamalla countingSteps muuttujan arvoksi true ja nollaa askelmäärän.
    // Metodi päivittää painikkeiden tilan niiden aktivoimiseksi tai poiskytkemiseksi.
    // Lopuksi se rekisteröi kuuntelijan aloittaakseen kiihtyvyysanturin lukemisen.
    public void startCounting(View view) {
        countingSteps = true;
        stepCount = 0;
        startButton.setEnabled(false);
        stopButton.setEnabled(true);
        saveButton.setEnabled(false);
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    // Tätä metodia kutsutaan, kun "Lopeta" painiketta klikataan. Se poistaa askelmäärän laskennan
    // käytöstä asettamalla countingSteps muuttujan arvoksi false. Metodi päivittää painikkeiden
    // tilan. Lopuksi se poistaa kuuntelijan käytöstä kiihtyvyysanturin lukemisen lopettamiseksi.
    public void stopCounting(View view) {
        countingSteps = false;
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
        saveButton.setEnabled(true);
        sensorManager.unregisterListener(this);
    }

    // Hakee SharedPreferences.Editor-olion ja käyttää sitä tallentamaan nykyisen askelmäärän
    // jaetun tiedon varastoon avaimella "AskelMäärä". Muutokset viedään käyttöön apply()-metodia kutsuen.
    public void saveScore(View view) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("AskelMäärä", stepCount);
        editor.apply();
        stepCountText.setText("Askel Määrä: 0");
    }

    // Tätä metodia kutsutaan aina, kun kiihtyvyysanturin lukemissa tapahtuu muutoksia.
    // Metodi hakee kiihtyvyysanturin lukemat x-, y- ja z-akseleilta SensorEvent-oliosta.
    // Se laskee kiihtyvyyden magnitudin. Jos magnitudi on suurempi kuin 10, askelmäärää kasvatetaan
    // ja askelmäärätekstikenttä päivitetään näyttämään uusi arvo.
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (countingSteps) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            double magnitude = Math.sqrt(x * x + y * y + z * z);

            if (magnitude > 10) {
                stepCount++;
                stepCountText.setText("Askel Määrä: " + stepCount);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}