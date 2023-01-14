package com.mobdev.memorandum;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.material.button.MaterialButton;
import com.mobdev.memorandum.model.Memo;
import io.realm.Realm;

public class EditMemoActivity extends AppCompatActivity {
    public Memo memo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_memo);

        Bundle extras = getIntent().getExtras();
        String memoId = extras.getString("memo_id");

        Realm.init(getApplicationContext());
        Realm realm = Realm.getDefaultInstance();
        memo = realm.where(Memo.class)
                        .equalTo("id", memoId)
                        .findFirst();

        EditText titleInput = findViewById(R.id.title);
        EditText contentInput = findViewById(R.id.content);
        MaterialButton saveButton = findViewById(R.id.save_button);

        titleInput.setText(memo.getTitle());
        contentInput.setText(memo.getContent());

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleInput.getText().toString();
                String content = contentInput.getText().toString();
                long createdTime = System.currentTimeMillis();

                if(checkIfValid(title, content)) {
                    realm.beginTransaction();
                    memo.setTitle(title);
                    memo.setContent(content);
                    memo.setCreatedTime(createdTime);
                    memo.setAsActive();
                    realm.commitTransaction();
                    showToast("Memo has been updated");
                    finish();
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
        return true;
    }

    public void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}