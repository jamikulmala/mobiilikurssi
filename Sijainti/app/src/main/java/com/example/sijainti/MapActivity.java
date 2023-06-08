package com.example.sijainti;

import android.location.Location;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.ScaleBarOverlay;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity {
    private MapView mapView;
    private List<Location> savedLocations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        savedLocations = getIntent().getParcelableArrayListExtra("savedLocations");

        Configuration.getInstance().setUserAgentValue(getPackageName());
        mapView = findViewById(R.id.mapView);

        GeoPoint mapCenter = new GeoPoint(savedLocations.get(0).getLatitude(), savedLocations.get(0).getLongitude());
        mapView.getController().setCenter(mapCenter);
        mapView.getController();

        List<OverlayItem> overlayItems = new ArrayList<>();
        for (Location location : savedLocations) {
            GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
            overlayItems.add(new OverlayItem("Location", "Coordinates", geoPoint));
        }

        ItemizedIconOverlay<OverlayItem> itemizedIconOverlay = new ItemizedIconOverlay<>(
                this, overlayItems, null);
        mapView.getOverlays().add(itemizedIconOverlay);

        ScaleBarOverlay scaleBarOverlay = new ScaleBarOverlay(mapView);
        mapView.getOverlays().add(scaleBarOverlay);
    }

}