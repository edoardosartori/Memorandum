package com.mobdev.memorandum;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.mobdev.memorandum.model.Memo;
import io.realm.Realm;
import io.realm.RealmResults;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    Context context;
    RealmResults<Memo> memoList;

    public MyAdapter(Context context, RealmResults<Memo> memoList) {
        this.context = context;
        this.memoList = memoList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Memo memo = memoList.get(position);
        holder.titleOutput.setText(memo.getTitle());
        holder.contentOutput.setText(setContentPreview(memo.getContent()));
        holder.locationOutput.setText(setLocality(memo.getLocality()));
        holder.timeOutput.setText(memo.getFormattedTime());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditMemoActivity.class);
                intent.putExtra("memo_id", memo.id);
                v.getContext().startActivity(intent);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                PopupMenu menu = new PopupMenu(context, v);
                menu.getMenu().add("Mark as 'EXPIRED'");
                menu.getMenu().add("Mark as 'COMPLETED'");
                menu.getMenu().add("DELETE");
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getTitle().equals("DELETE")) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage("Are you sure you want to delete this memo?")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Realm realm = Realm.getDefaultInstance();
                                            realm.beginTransaction();
                                            memo.deleteFromRealm();
                                            realm.commitTransaction();
                                            showToast("Memo has been deleted");
                                        }
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();

                        }
                        else if(item.getTitle().equals("Mark as 'EXPIRED'")) {
                            Realm realm = Realm.getDefaultInstance();
                            realm.beginTransaction();
                            memo.setAsExpired();
                            realm.commitTransaction();
                            showToast("Memo has been deleted");
                            showToast("Memo marked as 'EXPIRED'");
                        }
                        else if(item.getTitle().equals("Mark as 'COMPLETED'")) {
                            Realm realm = Realm.getDefaultInstance();
                            realm.beginTransaction();
                            memo.setAsCompleted();
                            realm.commitTransaction();
                            showToast("Memo marked as 'COMPLETED'");
                        }
                        return true;
                    }
                });
                menu.show();

                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return memoList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView titleOutput;
        TextView contentOutput;
        TextView locationOutput;
        TextView timeOutput;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            titleOutput = itemView.findViewById(R.id.title_output);
            contentOutput = itemView.findViewById(R.id.content_output);
            locationOutput = itemView.findViewById(R.id.location_output);
            timeOutput = itemView.findViewById(R.id.time_output);
        }
    }

    public void showToast(String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public String setContentPreview(String content) {
        int maxLength = 100; // max number of chars showed in content preview
        if(content.length() > maxLength) {
            return content.substring(0, maxLength) + "...";
        }
        return content;
    }

    public String setLocality(String locality) {
        int maxLength = 37; // max number of chars of locality showed
        if(locality.length() > maxLength) {
            return locality.substring(0, maxLength) + "...";
        }
        return locality;
    }
}
