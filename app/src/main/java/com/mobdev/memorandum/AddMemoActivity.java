package com.mobdev.memorandum;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.mobdev.memorandum.model.Memo;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import io.realm.Realm;

public class AddMemoActivity extends AppCompatActivity {
    FusedLocationProviderClient fusedLocationProviderClient;
    EditText titleInput, contentInput;
    MaterialButton saveButton, addLocation;
    String locality;
    String title;
    String content;
    long createdTime;
    double latitude;
    double longitude;
    private final static int REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_memo);

        // initialize fusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        titleInput = findViewById(R.id.title);
        contentInput = findViewById(R.id.content);
        saveButton = findViewById(R.id.save_button);
        addLocation = findViewById(R.id.add_location);

        Realm.init(getApplicationContext());
        Realm realm = Realm.getDefaultInstance();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = titleInput.getText().toString();
                content = contentInput.getText().toString();
                createdTime = System.currentTimeMillis();

                if(checkIfValid(title, content)) {
                    realm.beginTransaction();
                    Memo memo = realm.createObject(Memo.class, UUID.randomUUID().toString());
                    memo.setTitle(title);
                    memo.setContent(content);
                    memo.setCreatedTime(createdTime);
                    memo.setLocality(locality);
                    memo.setLatLng(latitude, longitude);
                    memo.setAsActive();
                    realm.commitTransaction();
                    showToast("Memo has been saved");
                    finish();
                }
            }
        });

        addLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableUserLocation();
            }
        });
    }

    public boolean checkIfValid(String title, String content) {
        if(title == null || title.trim().length() == 0) {
            showToast("Title field cannot be empty");
            return false;
        }
        if(content == null || content.trim().length() == 0) {
            showToast("Content field cannot be empty");
            return false;
        }
        if(locality == null || locality.trim().length() == 0) {
            showToast("Add the location before saving");
            return false;
        }
        return true;
    }

    private void enableUserLocation() {
        if(ActivityCompat.checkSelfPermission(AddMemoActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //permission granted
            getLocationData();
        } else { // ask for permission
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, // is this useless?
                    Manifest.permission.ACCESS_FINE_LOCATION))
                ActivityCompat.requestPermissions(AddMemoActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            else
                ActivityCompat.requestPermissions(AddMemoActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        }
    }

    @SuppressLint("MissingPermission")
    private void getLocationData() {
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if(location != null) {
                            //initialize geoCoder
                            Geocoder geocoder = new Geocoder(AddMemoActivity.this,
                                    Locale.getDefault());
                            try {
                                //initialize address list
                                List<Address> addresses = geocoder.getFromLocation(
                                        location.getLatitude(),
                                        location.getLongitude(),
                                        1);

                                latitude = addresses.get(0).getLatitude();
                                longitude = addresses.get(0).getLongitude();
                                locality = addresses.get(0).getAddressLine(0);
                                showToast("Location has been added");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        else {
                            showToast("Retrieving location failed. Set to default position");
                            latitude = 44.7650; // Parma set as default
                            longitude = 10.3102;
                            locality = "Parco Area Delle Scienze, Parma PR";
                        }
                    }
                });
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CODE) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                //permission granted
                getLocationData();
        }
        else {
            //permission not granted
            showToast("Failed to grant location permission");
        }
    }

    public void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}