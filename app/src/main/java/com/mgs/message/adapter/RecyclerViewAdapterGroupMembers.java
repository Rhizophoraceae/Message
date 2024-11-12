package com.mgs.message.adapter;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mgs.message.R;
import com.mgs.message.data.User;

import java.util.HashMap;
import java.util.List;

public class RecyclerViewAdapterGroupMembers extends RecyclerView.Adapter<RecyclerViewAdapterGroupMembers.ViewHolder> {
    private List<User> userList;
    private HashMap<String, Bitmap> iconMap;

    public RecyclerViewAdapterGroupMembers(List<User> userList, HashMap<String, Bitmap> iconMap) {
        this.userList = userList;
        this.iconMap = iconMap;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_user, null);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position == 0)
            holder.textView.setTextColor(Color.parseColor("#D7AC57"));
        holder.textView.setText(userList.get(position).getUsername());
        holder.imageView.setImageBitmap(iconMap.get(userList.get(position).getIcon()));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            itemView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
            textView = view.findViewById(R.id.textViewUsername);
            imageView = view.findViewById(R.id.imageViewIcon);
        }
    }
}
