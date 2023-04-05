package com.example.reuse;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.reuse.databinding.FragmentItemBinding;
import com.example.reuse.databinding.FragmentMessageBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MessageFragment extends Fragment {

    String chatId;
    String postId;
    FragmentMessageBinding binding;
    DatabaseReference databaseRef;
    DatabaseReference databaseRefChat;
    DatabaseReference databaseRefChatMessages;
    FirebaseUser user;
    String postImageUrl;
    String posterId;
    String title;
    String messageSenderId;
    MessageAdapter messageAdapter;
    ArrayList<Messages> messages;
    String ownImageUrl;
    String otherImageUrl;
    volatile boolean flag1 = false;
    volatile boolean flag2 = false;
    volatile boolean loaded = false;
    public MessageFragment(String chatId) {
        this.chatId = chatId;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseRef = FirebaseDatabase.getInstance("https://reuse-20200204-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users");
        databaseRefChat = FirebaseDatabase.getInstance("https://reuse-20200204-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Chats");
        databaseRefChatMessages = FirebaseDatabase.getInstance("https://reuse-20200204-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("ChatMessages");
        databaseRefChat.child(user.getUid()).child(chatId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postId = String.valueOf(snapshot.child("postId").getValue());
                postImageUrl = String.valueOf(snapshot.child("postImageUrl").getValue());
                posterId = String.valueOf(snapshot.child("posterId").getValue());
                title = String.valueOf(snapshot.child("title").getValue());
                messageSenderId = String.valueOf(snapshot.child("messageSenderId").getValue());
                Picasso.get().load(postImageUrl).into(binding.postImage);
                binding.itemText.setText(title);
                databaseRef.child(posterId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String posterName = String.valueOf(snapshot.child("displayName").getValue());
                        if(posterId.equals(user.getUid())){
                            ownImageUrl = String.valueOf(snapshot.child("profilePhotoUrl").getValue());
                        }
                        else{
                            binding.senderText.setText(String.valueOf(snapshot.child("displayName").getValue()));
                            otherImageUrl = String.valueOf(snapshot.child("profilePhotoUrl").getValue());
                        }
                        System.out.println("!!!!!!!!!!!!!!!!!!     COMPLETED1");
                        flag1 = true;
                        if(!loaded)
                            loadAdapter();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                databaseRef.child(messageSenderId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(messageSenderId.equals(user.getUid())){
                            ownImageUrl = String.valueOf(snapshot.child("profilePhotoUrl").getValue());
                        }
                        else{
                            binding.senderText.setText(String.valueOf(snapshot.child("displayName").getValue()));
                            otherImageUrl = String.valueOf(snapshot.child("profilePhotoUrl").getValue());
                        }
                        System.out.println("!!!!!!!!!!!!!!!!!!     COMPLETED2");
                        flag2 = true;
                        if(!loaded)
                            loadAdapter();

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        binding.sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.messageEditText.getText().toString().equals("")){
                    Toast.makeText(getContext(), "Message cant be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("message",binding.messageEditText.getText().toString());
                map.put("senderId",user.getUid());
                long time = System.currentTimeMillis();
                map.put("timestamp",time);
                databaseRefChatMessages.child(chatId).push().setValue(map);
                binding.messageEditText.setText("");
            }
        });
    }
    public void loadAdapter(){
        if(flag1==false || flag2==false){
            System.out.println("!!!!!!!!!!!!!!!!!!!!! FALSE      !!");
            return;
        }
        loaded = true;
        System.out.println("!!!!!!!!!!!!!!!!!!!  LOADING ADAPTER   !!");
        messages = new ArrayList<>();
        messageAdapter = new MessageAdapter(getContext(),messages,ownImageUrl,otherImageUrl);
        binding.messageRecyclerView.setAdapter(messageAdapter);
        binding.messageRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        databaseRefChatMessages.child(chatId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messages.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Messages message = dataSnapshot.getValue(Messages.class);
                    messages.add(message);
                }
                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMessageBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }
}