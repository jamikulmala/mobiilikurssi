package com.example.ominaisuuksientarkastelu;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.ActivityRecognition;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView stepCounterText;
    private TextView stepDetectorText;
    private TextView activityRecognitionText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stepCounterText = findViewById(R.id.stepCounterText);
        stepDetectorText = findViewById(R.id.stepDetectorText);
        activityRecognitionText = findViewById(R.id.activityRecognitionText);

        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        PackageManager packageManager = getPackageManager();

        Sensor stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (stepCounterSensor != null) {
            stepCounterText.setText("TYPE_STEP_COUNTER saatavilla");
        } else {
            stepCounterText.setText("TYPE_STEP_COUNTER sensor ei saatavilla");
        }

        Sensor stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        if (stepDetectorSensor != null) {
            stepDetectorText.setText("TYPE_STEP_DETECTOR saatavilla");
        } else {
            stepDetectorText.setText("TYPE_STEP_DETECTOR ei saatavilla");
        }

        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int result = apiAvailability.isGooglePlayServicesAvailable(this);
        if (result == ConnectionResult.SUCCESS) {
            activityRecognitionText.setText("ACTIVITY_RECOGNITION saatavilla");
        } else {
            activityRecognitionText.setText("ACTIVITY_RECOGNITION ei saatavilla");
        }

    }
}