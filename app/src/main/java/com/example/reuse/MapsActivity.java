package com.example.reuse;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.PermissionRequest;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class MapsActivity extends AppCompatActivity {
    FusedLocationProviderClient fusedLocationClient;
    SupportMapFragment mapFragment;
    Marker marker;
    PlacesClient placesClient;
    GoogleMap googleMap;
    private EditText searchBar;
    private Button searchBtn, returnBtn;
    private TextView lat, lng,placeName;

    private LatLng selectedLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        searchBar = findViewById(R.id.search_bar);
        searchBtn = findViewById(R.id.search_button);
        returnBtn = findViewById(R.id.return_button);
        lat = findViewById(R.id.latitude);
        lng = findViewById(R.id.longitude);
        placeName = findViewById(R.id.place_name);

        Dexter.withContext(getApplicationContext())
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        getMyLocation();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            //open settings

                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(com.karumi.dexter.listener.PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    private void getMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        Task<Location> task = fusedLocationClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        if (location != null) {
                            selectedLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            MarkerOptions markerOptions = new MarkerOptions().position(selectedLocation).title("Your Selected Location");
                            marker=googleMap.addMarker(markerOptions);
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(selectedLocation, 16));
                            lat.setText("Latitude:\n"+selectedLocation.latitude);
                            lng.setText("Longitude:\n"+selectedLocation.longitude);
                            Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
                            List<Address> addressList = null;
                            try {
                                addressList = geocoder.getFromLocation(selectedLocation.latitude, selectedLocation.longitude, 1);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            placeName.setText("Place name: "+addressList.get(0).getAddressLine(0));
                            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                                @SuppressLint("SetTextI18n")
                                @Override
                                public void onMapClick(LatLng point) {
                                    lat.setText("Latitude:\n"+point.latitude);
                                    lng.setText("Longitude:\n"+point.longitude);
                                    Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
                                    try {
                                        List<Address> addressList = geocoder.getFromLocation(point.latitude, point.longitude, 1);
                                        placeName.setText("Place name: "+addressList.get(0).getAddressLine(0));
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                    marker.setPosition(new LatLng(point.latitude, point.longitude));
                                }
                            });
                            searchBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String location = searchBar.getText().toString();
                                    if (!location.equals("")) {
                                        Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
                                        try {
                                            List<Address> addressList = geocoder.getFromLocationName(location, 1);
                                            if (addressList.size() > 0) {
                                                LatLng point = new LatLng( addressList.get(0).getLatitude(),  addressList.get(0).getLongitude());
                                                lat.setText("Latitude:\n"+point.latitude);
                                                lng.setText("Longitude:\n"+point.longitude);
                                                placeName.setText("Place name: "+addressList.get(0).getAddressLine(0));
                                                marker.setPosition(new LatLng(point.latitude, point.longitude));
                                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 16));
                                                selectedLocation = point;
                                            } else {
                                                Toast.makeText(MapsActivity.this, "Location not found", Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            });
                            returnBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent resultIntent = new Intent();
                                    String placeNameString = placeName.getText().toString();
                                    int indexOfColon = placeNameString.indexOf(":");
                                    String placeName = placeNameString.substring(indexOfColon + 2);
                                    resultIntent.putExtra("place_name", placeName);
                                    resultIntent.putExtra("latitude", selectedLocation.latitude);
                                    resultIntent.putExtra("longitude", selectedLocation.longitude);
                                    setResult(RESULT_OK, resultIntent);
                                    finish();
                                }
                            });
                        }
                        else if (location == null) {
                            System.out.println("Location is null");
                        }
                        else{
                            System.out.println("Something went wrong");
                        }
                    }
                });
            }
        });
    }
}