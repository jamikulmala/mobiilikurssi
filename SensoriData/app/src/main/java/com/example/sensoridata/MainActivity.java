package com.example.sensoridata;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor lightSensor;
    private Sensor proximitySensor;
    private Sensor accelerometerSensor;
    private Sensor magnetometerSensor;

    private TextView lightSensorValueTextView;
    private TextView proximitySensorValueTextView;
    private TextView orientationSensorValueTextView;

    private float[] accelerometerValues = new float[3];
    private float[] magnetometerValues = new float[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lightSensorValueTextView = findViewById(R.id.lightSensorValue);
        proximitySensorValueTextView = findViewById(R.id.proximitySensorValue);
        orientationSensorValueTextView = findViewById(R.id.orientationSensorValue);

        // Initialize the sensor manager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // Retrieve the light sensor
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        // Retrieve the proximity sensor
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        // Retrieve the accelerometer and magnetometer sensors
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Register sensor listeners
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, magnetometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Unregister sensor listeners to save battery
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Retrieve the sensor type
        int sensorType = event.sensor.getType();

        // Update the corresponding TextView based on the sensor type
        switch (sensorType) {
            case Sensor.TYPE_LIGHT:
                float lightValue = event.values[0];
                lightSensorValueTextView.setText("Light Sensor: " + lightValue);
                break;
            case Sensor.TYPE_PROXIMITY:
                float proximityValue = event.values[0];
                proximitySensorValueTextView.setText("Proximity Sensor: " + proximityValue);
                break;
            case Sensor.TYPE_ACCELEROMETER:
                accelerometerValues = event.values.clone();
                updateOrientation();
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                magnetometerValues = event.values.clone();
                updateOrientation();
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used in this example
    }

    private void updateOrientation() {
        float[] rotationMatrix = new float[9];
        SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerValues, magnetometerValues);

        // Remap coordinate system based on device orientation
        float[] remappedRotationMatrix = new float[9];
        SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_X, SensorManager.AXIS_Z, remappedRotationMatrix);

        // Convert rotation matrix to orientation values
        float[] orientationValues = new float[3];
        SensorManager.getOrientation(remappedRotationMatrix, orientationValues);

        // Convert radians to degrees
        float azimuthDegrees = (float) Math.toDegrees(orientationValues[0]);
        float pitchDegrees = (float) Math.toDegrees(orientationValues[1]);
        float rollDegrees = (float) Math.toDegrees(orientationValues[2]);

        orientationSensorValueTextView.setText("Orientation: " +
                azimuthDegrees + ", " +
                pitchDegrees + ", " +
                rollDegrees);
    }
}