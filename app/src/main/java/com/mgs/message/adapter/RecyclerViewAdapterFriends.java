package com.mgs.message.adapter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mgs.message.ChatActivity;
import com.mgs.message.R;
import com.mgs.message.data.User;
import com.mgs.message.utils.CurrentUser;

import java.util.HashMap;
import java.util.List;

public class RecyclerViewAdapterFriends extends RecyclerView.Adapter<RecyclerViewAdapterFriends.ViewHolder> {
    private List<User> userList;
    private HashMap<String, Bitmap> iconMap;

    public RecyclerViewAdapterFriends(List<User> userList, HashMap<String, Bitmap> iconMap) {
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
        holder.textView.setText(userList.get(position).getUsername());
        holder.imageView.setImageBitmap(iconMap.get(userList.get(position).getIcon()));
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), ChatActivity.class);
            intent.putExtra("toPosition", position);
            view.getContext().startActivity(intent);
            CurrentUser.toId = userList.get(position).getUserId();
            CurrentUser.isGroup = 0;
            Log.i("click", "clicked" + position);
        });
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
