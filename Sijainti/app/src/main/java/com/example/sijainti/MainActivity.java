package com.example.sijainti;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LocationListener {
    private TextView tvCoordinates;
    private Button btnGetLocation;
    private Button btnShowOnMap;

    private LocationManager locationManager;
    private List<Location> savedLocations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvCoordinates = findViewById(R.id.tvCoordinates);
        btnGetLocation = findViewById(R.id.btnGetLocation);
        btnShowOnMap = findViewById(R.id.btnShowOnMap);

        savedLocations = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        btnGetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            0, 0, MainActivity.this);

                } else {
                    Toast.makeText(MainActivity.this, "Location permission not granted",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnShowOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!savedLocations.isEmpty()) {
                    Intent intent = new Intent(MainActivity.this, MapActivity.class);
                    intent.putParcelableArrayListExtra("savedLocations", (ArrayList<Location>) savedLocations);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "No saved locations", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        tvCoordinates.setText("Latitude: " + latitude + "\nLongitude: " + longitude);

        savedLocations.add(location);
    }
}