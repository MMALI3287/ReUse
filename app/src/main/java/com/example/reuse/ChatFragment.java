package com.example.reuse;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.reuse.databinding.FragmentChatBinding;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ChatFragment extends Fragment {

    FragmentChatBinding binding;
    ChatAdapter chatAdapter;
    ArrayList<Chats> chats;

    DatabaseReference databaseRefChat;
    FirebaseUser user;
    public ChatFragment() {

    }
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseRefChat = FirebaseDatabase.getInstance("https://reuse-20200204-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Chats");
        chats = new ArrayList<>();
        chatAdapter = new ChatAdapter(getContext(),chats);
        binding.recyclerView.setAdapter(chatAdapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        databaseRefChat.child(user.getUid()).orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chats.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Chats chat = dataSnapshot.getValue(Chats.class);
                    chats.add(chat);
                }
                Collections.reverse(chats);
                chatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }
}