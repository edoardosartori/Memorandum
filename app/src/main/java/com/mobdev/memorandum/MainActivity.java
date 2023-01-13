package com.mobdev.memorandum;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import com.google.android.gms.location.LocationRequest;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.mobdev.memorandum.model.Memo;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

public class MainActivity extends AppCompatActivity {
    TextView empty_memos_message, header;
    MaterialButton add_memo_button;
    ImageButton map_button;
    RecyclerView recyclerView;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();
        add_memo_button = findViewById(R.id.memo_creation_button);
        map_button = findViewById(R.id.map_button);
        empty_memos_message = findViewById(R.id.empty_memos_view);
        header = findViewById(R.id.header);
        recyclerView = findViewById(R.id.memos_list);

        Realm.init(context);
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Memo> activeMemoList = realm.where(Memo.class)
                .equalTo("status", "ACTIVE")
                .sort("createdTime", Sort.ASCENDING)
                .findAll();


        setVisibility(activeMemoList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        MyAdapter myAdapter = new MyAdapter(MainActivity.this, activeMemoList);
        recyclerView.setAdapter(myAdapter);

        activeMemoList.addChangeListener(new RealmChangeListener<RealmResults<Memo>>() {
            @Override
            public void onChange(RealmResults<Memo> memos) {
                setVisibility(memos);
                myAdapter.notifyDataSetChanged();
            }
        });

        add_memo_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AddMemoActivity.class));
            }
        });

        map_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MapsActivity.class));

            }
        });
    }

    private void setVisibility(RealmResults<Memo> activeMemoList) {
        if(activeMemoList.size() > 0) {
            empty_memos_message.setVisibility(View.GONE);
            header.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        else {
            empty_memos_message.setVisibility(View.VISIBLE);
            header.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    private void showToast(String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}