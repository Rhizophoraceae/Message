package com.rjgc.zhs.adapter;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rjgc.zhs.R;
import com.rjgc.zhs.SystemInfoActivity;
import com.rjgc.zhs.UserinfoActivity;
import com.rjgc.zhs.data.Setting;

import java.util.List;

public class RecyclerViewAdapterSettings extends RecyclerView.Adapter<RecyclerViewAdapterSettings.ViewHolder> {
    private List<Setting> settingList;

    public RecyclerViewAdapterSettings(List<Setting> settingList) {
        this.settingList = settingList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_setting, null);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.imageView.setImageResource(settingList.get(position).getIcon());
        holder.textView.setText(settingList.get(position).getDescribe());
        holder.itemView.setOnClickListener(view -> {
            Intent intent;
            if (position == 0) {
                intent = new Intent(view.getContext(), UserinfoActivity.class);
            } else {
                intent = new Intent(view.getContext(), SystemInfoActivity.class);
            }
            view.getContext().startActivity(intent);
            Log.i("click", "clicked" + position);
        });
    }

    @Override
    public int getItemCount() {
        return settingList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textView;

        public ViewHolder(View view) {
            super(view);
            itemView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
            imageView = view.findViewById(R.id.imageViewIcon);
            textView = view.findViewById(R.id.textViewDescribe);
        }
    }
}
