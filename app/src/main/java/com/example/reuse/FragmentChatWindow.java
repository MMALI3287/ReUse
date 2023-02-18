package com.example.reuse;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.Timestamp;
import java.util.ArrayList;

public class FragmentChatWindow extends Fragment {

    private RecyclerView mChatRecyclerView;
    private ChatAdapter mAdapter;
    private DatabaseReference mDatabaseRef;
    private ValueEventListener mValueEventListener;
    private EditText mMessageEditText;
    private Button mSendButton;



    public FragmentChatWindow() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_chat_window, container, false);

        mChatRecyclerView = rootView.findViewById(R.id.chat_recycler_view);
        mAdapter = new ChatAdapter(new ArrayList<>());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mChatRecyclerView.setLayoutManager(layoutManager);
        mChatRecyclerView.setAdapter(mAdapter);


        mMessageEditText = rootView.findViewById(R.id.message_input);
        mSendButton = rootView.findViewById(R.id.send_button);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the message text from the EditText view
                String messageText = mMessageEditText.getText().toString();

                // Create a new ChatMessage object with the message text and user ID
                ChatMessage chatMessage = new ChatMessage(messageText, "user_id_here", System.currentTimeMillis());

                // Save the new ChatMessage object to the Firebase Realtime Database
                mDatabaseRef.push().setValue(chatMessage);

                // Clear the message EditText view
                mMessageEditText.setText("");
            }
        });

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("chat_messages");
        mDatabaseRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                ChatMessage chatMessage = snapshot.getValue(ChatMessage.class);
                mAdapter.addChatMessage(chatMessage);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Handle message changed
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                // Handle message removed
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Handle message moved
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error
                Log.e(TAG, "Error communicating with Firebase Realtime Database: " + error.getMessage());
                Toast.makeText(getContext(), "An error occurred while communicating with the database: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });

        // Use a ValueEventListener to initially load the chat messages
        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Clear the adapter before adding new messages
                mAdapter.clearChatMessages();

                // Loop through all child nodes in the "chat_messages" node
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    ChatMessage chatMessage = childSnapshot.getValue(ChatMessage.class);
                    mAdapter.addChatMessage(chatMessage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        };

        mDatabaseRef.addValueEventListener(mValueEventListener);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Remove the ValueEventListener to avoid memory leaks
        mDatabaseRef.removeEventListener(mValueEventListener);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Use a ChildEventListener to listen for new chat messages

    }

    // Rest of the FragmentChatWindow class code
}