package com.mgs.message.adapter;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mgs.message.R;
import com.mgs.message.data.Message;
import com.mgs.message.utils.CurrentUser;

import java.util.HashMap;
import java.util.List;

public class RecyclerViewAdapterMessage extends RecyclerView.Adapter<RecyclerViewAdapterMessage.ViewHolder> {
    private final List<Message> messageList;
    private final HashMap<String, Bitmap> iconMap;

    public RecyclerViewAdapterMessage(List<Message> messageList, HashMap<String, Bitmap> iconMap) {
        this.messageList = messageList;
        this.iconMap = iconMap;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        ViewHolder holder = new ViewHolder(view);
        switch (viewType) {
            case 0:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
                holder = new ViewHolder(view);
                break;
            case 1:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_my, parent, false);
                holder = new ViewHolder(view);
                break;
        }
        return holder;
    }

    @Override
    public int getItemViewType(int position) {
        if (messageList.get(position).getFromId() == CurrentUser.user.getUserId()) {
            return 1;
        }
        return 0;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textViewMessage.setText(messageList.get(position).getContent());
        holder.textViewUsername.setText(messageList.get(position).getUsername());
        holder.imageViewIcon.setImageBitmap(iconMap.get(messageList.get(position).getIcon()));
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewMessage;
        public TextView textViewUsername;
        public ImageView imageViewIcon;

        public ViewHolder(View view) {
            super(view);
            textViewMessage = view.findViewById(R.id.textViewMessage);
            textViewUsername = view.findViewById(R.id.textViewUsername);
            imageViewIcon = view.findViewById(R.id.imageViewIcon);
        }
    }
}
