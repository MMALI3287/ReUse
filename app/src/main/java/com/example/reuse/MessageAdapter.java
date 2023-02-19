package com.example.reuse;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder>{
    Context context;
    ArrayList<Messages> messages;
    FirebaseUser user;
    String ownImageUrl;
    String otherImageUrl;

    public MessageAdapter(Context context,ArrayList<Messages> messages,String ownImageUrl,String otherImageUrl){
        this.context = context;
        this.messages = messages;
        this.ownImageUrl = ownImageUrl;
        this.otherImageUrl = otherImageUrl;
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public MessageAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==1){
            View v = LayoutInflater.from(context).inflate(R.layout.message_item_right,parent,false);
            return new MessageAdapter.MyViewHolder(v);
        }
        else
        {
            View v = LayoutInflater.from(context).inflate(R.layout.message_item_left,parent,false);
            return new MessageAdapter.MyViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.MyViewHolder holder, int position) {
        holder.messageText.setText(messages.get(position).getMessage());
        if(messages.get(position).getSenderId().equals(user.getUid())){
            if(ownImageUrl.length()>10){
                Picasso.get().load(ownImageUrl).into(holder.image);
            }
        }
        else{
            if(otherImageUrl.length()>10){
                Picasso.get().load(otherImageUrl).into(holder.image);
            }
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(messages.get(position).getSenderId().equals(user.getUid()))
            return 1;
        return 0;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView messageText;
        ImageView image;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageText);
            image = itemView.findViewById(R.id.image);
        }
    }

}
