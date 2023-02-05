package com.example.reuse;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ProfileFragment extends Fragment {

    TextView profileName;
    Button editProfileButton, signoutButton;
    DatabaseReference databaseRef;
    DatabaseReference databaseRefListedItems;
    FirebaseUser user;
    ImageView profileImage;

    RecyclerView recyclerView;
    ListedItemsAdapter listedItemsAdapter;
    ArrayList<Posts> posts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        posts = new ArrayList<>();
        listedItemsAdapter = new ListedItemsAdapter(getContext(),posts);
        recyclerView.setAdapter(listedItemsAdapter);

        databaseRef = FirebaseDatabase.getInstance("https://reuse-20200204-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users");
        databaseRefListedItems = FirebaseDatabase.getInstance("https://reuse-20200204-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Posts");
        editProfileButton = view.findViewById(R.id.editProfileButton);
        signoutButton = view.findViewById(R.id.signoutButton);
        profileName = view.findViewById(R.id.profileName);
        profileImage = view.findViewById(R.id.profileImage);
        user = FirebaseAuth.getInstance().getCurrentUser();

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame, EditProfileFragment.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack("theFragment")
                        .commit();
            }
        });
        signoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), LoginRegister.class);
                startActivity(intent);
                getActivity().finish();
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build();
                GoogleSignIn.getClient(getContext(), gso).signOut();
            }
        });
        load();
    }
    public void load(){
        databaseRef.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String url = String.valueOf(snapshot.child("profilePhotoUrl").getValue());
                Log.d("Fahad",url);
                String displayName = String.valueOf(snapshot.child("displayName").getValue());
                profileName.setText(displayName);
                if(url.length()<10)
                    return;
                Picasso.get().load(url).into(profileImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        databaseRefListedItems.child(user.getUid()).addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                posts.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Posts post = dataSnapshot.getValue(Posts.class);
                    posts.add(post);
                }
                listedItemsAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}