package com.example.muistutussovellus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
/*
Luokka vastaa sovelluksen karttanäkymästä toteuttaa MapEventsReceiver rajapinnan.
 */
public class MapActivity extends AppCompatActivity implements MapEventsReceiver {
    private MapView mapView;
    /*
    Asettaa karttanäkymän activityn alkaessa
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Configuration.getInstance().setUserAgentValue(getPackageName());
        mapView = findViewById(R.id.mapView);

        mapView.addOnFirstLayoutListener((v, left, top, right, bottom) -> {
            mapView.getOverlayManager().add(new MapEventsOverlay(this));
        });
    }
    /*
    Vastaa käyttäjän valitseman sijainnin tallentamisesta. Kun käyttäjä painaa kohdetta kartalla
    Metodi ottaa pisteen koordinaatit ja Geocoder API:lla muuttaa pisteen paikannimeksi ja sitten
    palaa sovelluksen päänäkymään paikannimi extrana.
     */
    @Override
    public boolean singleTapConfirmedHelper(GeoPoint p) {

        double latitude = p.getLatitude();
        double longitude = p.getLongitude();

        Geocoder geocoder = new Geocoder(MapActivity.this, Locale.getDefault());
        List<Address> addresses;
        String locationName = "";
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                locationName = address.getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Intent resultIntent = new Intent();
        resultIntent.putExtra("locationName", locationName);
        setResult(RESULT_OK, resultIntent);
        finish();

        return true;
    }

    @Override
    public boolean longPressHelper(GeoPoint p) {
        return false;
    }
}