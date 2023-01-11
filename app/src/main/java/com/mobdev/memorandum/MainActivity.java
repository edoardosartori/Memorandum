package com.mobdev.memorandum;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.google.android.material.button.MaterialButton;
import com.mobdev.memorandum.model.Memo;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

public class MainActivity extends AppCompatActivity {
    TextView empty_memos_message;
    TextView header;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialButton add_memo_button = findViewById(R.id.memo_creation_button);
        add_memo_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AddMemoActivity.class));
            }
        });

        Realm.init(getApplicationContext());
        Realm realm = Realm.getDefaultInstance();

        RealmResults<Memo> activeMemoList = realm.where(Memo.class)
                                                .equalTo("status", "ACTIVE")
                                                .sort("createdTime", Sort.ASCENDING)
                                                .findAll();

        empty_memos_message = findViewById(R.id.empty_memos_view);
        header = findViewById(R.id.header);
        recyclerView = findViewById(R.id.memos_list);
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
    }

    public void setVisibility(RealmResults<Memo> activeMemoList) {
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
}