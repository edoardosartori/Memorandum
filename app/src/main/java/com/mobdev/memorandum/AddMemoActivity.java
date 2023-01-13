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
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
    LatLng latLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_memo);

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
                String title = titleInput.getText().toString();
                String content = contentInput.getText().toString();
                long createdTime = System.currentTimeMillis();

                if(checkIfValid(title, content)) {
                    realm.beginTransaction();
                    Memo memo = realm.createObject(Memo.class, UUID.randomUUID().toString());
                    memo.setTitle(title);
                    memo.setContent(content);
                    memo.setCreatedTime(createdTime);
                    memo.setLocality(locality);
                    memo.setLatLng(latLng);
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
                if(ActivityCompat.checkSelfPermission(AddMemoActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    //permission granted
                    getLocation();

                } else {
                    // permission denied
                    ActivityCompat.requestPermissions(AddMemoActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
                }
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

    @SuppressLint("MissingPermission")
    private void getLocation(){

        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                // initialize location
                Location location = task.getResult();
                if(location != null) {
                    try {
                        //initialize geoCoder
                        Geocoder geocoder = new Geocoder(AddMemoActivity.this,
                                Locale.getDefault());
                        //initialize address list
                        List<Address> addresses = geocoder.getFromLocation(
                                location.getLatitude(), location.getLongitude(), 1);

                        locality = addresses.get(0).getAddressLine(0);
                        showToast("locality found is: " + locality);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    showToast("Location was NULL! Set to default position");
                    Location defaultLocation = new Location(LocationManager.GPS_PROVIDER);
                    defaultLocation.setLatitude(44.7650);
                    defaultLocation.setLongitude(10.3102); // Parma
                    latLng = new LatLng(44.7650, 10.3102);
                    try {
                        //initialize geoCoder
                        Geocoder geocoder = new Geocoder(AddMemoActivity.this,
                                Locale.getDefault());
                        //initialize address list
                        List<Address> addresses = geocoder.getFromLocation(
                                defaultLocation.getLatitude(), defaultLocation.getLongitude(), 1);

                        locality = addresses.get(0).getAddressLine(0);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}