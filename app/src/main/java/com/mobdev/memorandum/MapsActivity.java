package com.mobdev.memorandum;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.mobdev.memorandum.databinding.ActivityMapsBinding;
import com.mobdev.memorandum.model.Memo;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import io.realm.Realm;
import io.realm.RealmResults;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    Context context;
    LatLngBounds bounds;
    RealmResults<Memo> activeMemoList;
    double lat;
    double lng;
    GoogleMap mMap;
    private GeofencingClient geofencingClient;
    ActivityMapsBinding binding;
    public FusedLocationProviderClient fusedLocationProviderClient;
    private final static int REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();

        // initialize fusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        // initialize geofencingClient
        geofencingClient = LocationServices.getGeofencingClient(this);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Realm.init(context);
        Realm realm = Realm.getDefaultInstance();
        activeMemoList = realm.where(Memo.class)
                .equalTo("status", "ACTIVE")
                .findAll();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //adding memos' markers
        LatLng latLng = new LatLng(0, 0);
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        int counter = 0;
        if (mMap != null) {
            for (Memo memo : activeMemoList) {
                MarkerOptions myMarkerOptions = new MarkerOptions();
                myMarkerOptions.title(memo.getTitle());
                myMarkerOptions.snippet(setContentPreview(memo));
                latLng = memo.getLatLng();
                myMarkerOptions.position(latLng);
                //add the marker to the map
                mMap.addMarker(myMarkerOptions);
                counter++;
                builder.include(latLng);
            }
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            if (counter > 0) {
                bounds = builder.build();
                int width = getResources().getDisplayMetrics().widthPixels;
                int height = getResources().getDisplayMetrics().heightPixels;
                int padding = (int) (width * 0.15); // offset from edges of the map 15% of screen
                // camera animates to cover all the markers
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
                mMap.animateCamera(cu);
            }

            enableUserLocation();

            // adding on click listener to marker of google maps.
            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    String[] lines = marker.getSnippet().split("\\n");
                    Intent intent = new Intent(context, EditMemoActivity.class);
                    intent.putExtra("memo_id", lines[1]);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(intent);
                }
            });
        }
        else showToast("Something went wrong");
    }

    private void enableUserLocation() {
        if(ActivityCompat.checkSelfPermission(MapsActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //permission granted
            mMap.setMyLocationEnabled(true);
            setCameraOnUserLocation();

        } else { // ask for permission
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION))
            ActivityCompat.requestPermissions(MapsActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            else
                ActivityCompat.requestPermissions(MapsActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        }
    }

    @SuppressLint({"MissingSuperCall", "MissingPermission"})
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults){
        if(requestCode == REQUEST_CODE) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //permission granted
                mMap.setMyLocationEnabled(true);
                setCameraOnUserLocation();
            }
            else {
                // not having permission
                showToast("Failed to grant location permission");
            }
        }

    }

    public String setContentPreview(Memo memo) {
        int maxLength = 30; // max number of chars showed in content preview
        String content = memo.getContent();
        if(content.length() > maxLength) {
            content = content.substring(0, maxLength) + "...";
        }
        return content  + "\n" + memo.getId();
    }

    @SuppressLint("MissingPermission")
    private void setCameraOnUserLocation() {
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    // initialize location
                    if(location != null) {
                        //initialize geoCoder
                        Geocoder geocoder = new Geocoder(MapsActivity.this,
                                Locale.getDefault());
                        try {
                            //initialize address list
                            List<Address> addresses = geocoder.getFromLocation(
                                    location.getLatitude(),
                                    location.getLongitude(),
                                    1);

                            lat = addresses.get(0).getLatitude();
                            lng = addresses.get(0).getLongitude();
                            LatLng latLng = new LatLng(lat, lng);
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12.0f));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        showToast("Failed to retrieve location");
                    }
                }
            });
    }


    public void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}