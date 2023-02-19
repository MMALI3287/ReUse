package com.example.reuse;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder>{
    Context context;
    ArrayList<Chats> chats;
    FirebaseUser user;
    DatabaseReference databaseRef;
    DatabaseReference databaseRefChat;
    public ChatAdapter(Context context,ArrayList<Chats> chats){
        this.context = context;
        this.chats = chats;
        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseRef = FirebaseDatabase.getInstance("https://reuse-20200204-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users");
        databaseRefChat = FirebaseDatabase.getInstance("https://reuse-20200204-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Chats");
    }

    @NonNull
    @Override
    public ChatAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.chat_item,parent,false);
        return new ChatAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.MyViewHolder holder, int position) {
        String url = chats.get(position).getPostImageUrl();
        Picasso.get().load(url).into(holder.image);
        holder.titleText.setText(chats.get(position).getTitle());
        String messageSenderId = chats.get(position).getMessageSenderId();
        String posterId = chats.get(position).getPosterId();
        if(!messageSenderId.equals(user.getUid())){
            databaseRef.child(messageSenderId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String name = String.valueOf(snapshot.child("displayName").getValue());
                    holder.nameText.setText(name);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else
        {
            databaseRef.child(posterId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String name = String.valueOf(snapshot.child("displayName").getValue());
                    holder.nameText.setText(name);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = ((AppCompatActivity)context).getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame, new MessageFragment(chats.get(position).getPostId()+chats.get(position).getMessageSenderId()), null)
                        .setReorderingAllowed(true)
                        .addToBackStack("theFragment")
                        .commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView nameText;
        TextView titleText;
        CardView parent;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            nameText = itemView.findViewById(R.id.nameText);
            titleText = itemView.findViewById(R.id.titleText);
            parent = itemView.findViewById(R.id.parent);
        }
    }
}
