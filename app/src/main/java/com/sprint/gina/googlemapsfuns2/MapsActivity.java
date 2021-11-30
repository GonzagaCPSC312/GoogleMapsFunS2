package com.sprint.gina.googlemapsfuns2;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PackageManagerCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sprint.gina.googlemapsfuns2.databinding.ActivityMapsBinding;

import java.io.IOException;
import java.util.List;

// fragment: a mini activity (it has its own lifecycle)
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
                    GoogleMap.OnMyLocationClickListener {
    static final int LOCATION_REQUEST_CODE = 1; // needs to be unique within your app's requests

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // we can safely use mMap here

        // game plan
        // 1. set the map type
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        // 2. add a GU marker (plus geocoding)
        addGUMarker();

        // 3. set up the my location blue dot
        enableMyLocation();

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    private void addGUMarker() {
        String gonzagaStr = "Gonzaga University Florence";
        // need the lat, long for Gonzaga University
        // 2 ways
        // 1. hard coded
//        LatLng guLatLng = new LatLng(47.6670321,-117.403623);
        // 2. use geocoder
        LatLng guLatLng = getLatLngUsingGeocoding(gonzagaStr);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title(gonzagaStr);
        markerOptions.snippet("We are here, go zags!");
        markerOptions.position(guLatLng);
        mMap.addMarker(markerOptions);

        // move the camera and zoom it in on gonzaga
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(guLatLng, 15.0f);
        mMap.moveCamera(cameraUpdate);
    }

    private LatLng getLatLngUsingGeocoding(String addressStr) {
        // geocoding: address -> coordinates
        // reverse geocoding: coordinates -> address
        LatLng latLng = null;
        Geocoder geocoder = new Geocoder(this);
        try {
            List<Address> addressList = geocoder.getFromLocationName(addressStr, 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                latLng = new LatLng(address.getLatitude(), address.getLongitude());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return latLng;
    }

    private void enableMyLocation() {
        // we need to get the user's permission at runtime to access their FINE LOCATION
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // we have permission!!
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationClickListener(this);
        }
        else {
            // we need to request permission
            // creates an alert dialog and prompts the user to choose grant or deny
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, 
                                           @NonNull String[] permissions, 
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // this callback executes once the user has made thier choice in the alert dialog
        if (requestCode == LOCATION_REQUEST_CODE) {
            // only requested one permission
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // we finally have the user's permission
                enableMyLocation();
            }
            else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        // executes when the user clicks on their blue my location dot
        Toast.makeText(this, "You are at (" +
                location.getLatitude() + ", " +
                location.getLongitude() + ")", Toast.LENGTH_SHORT).show();
    }
}