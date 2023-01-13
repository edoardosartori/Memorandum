package com.mobdev.memorandum;

import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mobdev.memorandum.databinding.ActivityMapsBinding;
import com.mobdev.memorandum.model.Memo;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    Context context;
    RealmResults<Memo> activeMemoList;
    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getApplicationContext();

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
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
        LatLng latLng = new LatLng(0,0);
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for(Memo memo : activeMemoList) {
            MarkerOptions myMarkerOptions = new MarkerOptions();
            myMarkerOptions.title(memo.getTitle());
            myMarkerOptions.snippet(setContentPreview(memo.getContent()));
            latLng = memo.getLatLng();
            myMarkerOptions.position(latLng);
            //add the marker to the map
            mMap.addMarker(myMarkerOptions);
            builder.include(latLng);
        }
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        if(builder != null) {
            LatLngBounds bounds = builder.build();
            int width = getResources().getDisplayMetrics().widthPixels;
            int height = getResources().getDisplayMetrics().heightPixels;
            int padding = (int) (width * 0.15); // offset from edges of the map 15% of screen
            // to animate camera cover all the markers
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
            mMap.animateCamera(cu);
        }
        else {
            latLng = new LatLng(44.7650, 10.3102); // parma
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        }
    }

    public String setContentPreview(String content) {
        int maxLength = 30; // max number of chars showed in content preview
        if(content.length() > maxLength) {
            return content.substring(0, maxLength) + "...";
        }
        return content;
    }
}