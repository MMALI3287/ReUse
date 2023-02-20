package com.example.reuse;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    FeedAdapter feedAdapter;
    AutoCompleteTextView autoCompleteTextView;
    ArrayList<UnfilteredPosts> unfilteredPosts;
    EditText searchEditText;
    public HomeFragment() {

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerView);
        databaseReference = FirebaseDatabase.getInstance("https://reuse-20200204-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("UnfilteredPosts");
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        searchEditText = view.findViewById(R.id.searchEditText);
        unfilteredPosts = new ArrayList<>();
        feedAdapter = new FeedAdapter(getContext(),unfilteredPosts);
        recyclerView.setAdapter(feedAdapter);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                unfilteredPosts.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    UnfilteredPosts unfilteredPost = dataSnapshot.getValue(UnfilteredPosts.class);
                    unfilteredPosts.add(unfilteredPost);
                }
                feedAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        String[] type = new String[]{"All","Tools","Furniture","Appliances","Books","Video Games","Electronics","Clothing","Bags","Toys","Electronics","Mobile Phones","Antiques","Miscellaneous"};
        ArrayAdapter<String> adapter= new ArrayAdapter<>(getContext(), R.layout.drop_down_item, type);
        autoCompleteTextView = view.findViewById(R.id.filled_exposed);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        unfilteredPosts.clear();
                        String category = adapterView.getItemAtPosition(i).toString();
                        if(searchEditText.getText().toString().equals("")){
                            for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                                UnfilteredPosts unfilteredPost = dataSnapshot.getValue(UnfilteredPosts.class);
                                if(category.equals("All")){
                                    unfilteredPosts.add(unfilteredPost);
                                }
                                else if(unfilteredPost.getCategory().equals(category)){
                                    unfilteredPosts.add(unfilteredPost);
                                }
                            }
                        }
                        else{
                            for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                                UnfilteredPosts unfilteredPost = dataSnapshot.getValue(UnfilteredPosts.class);
                                if(unfilteredPost.getTitle().toLowerCase().contains(searchEditText.getText().toString().toLowerCase())) {
                                    if(category.equals("All")){
                                        unfilteredPosts.add(unfilteredPost);
                                    }
                                    else if(unfilteredPost.getCategory().equals(category)){
                                        unfilteredPosts.add(unfilteredPost);
                                    }
                                }
                            }
                        }

                        feedAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        unfilteredPosts.clear();
                        String category = autoCompleteTextView.getText().toString();
                        if(searchEditText.getText().toString().equals("")){
                            for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                                UnfilteredPosts unfilteredPost = dataSnapshot.getValue(UnfilteredPosts.class);
                                if(category.equals("All")){
                                    unfilteredPosts.add(unfilteredPost);
                                }
                                else if(unfilteredPost.getCategory().equals(category)){
                                    unfilteredPosts.add(unfilteredPost);
                                }
                            }
                        }
                        else{
                            for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                                UnfilteredPosts unfilteredPost = dataSnapshot.getValue(UnfilteredPosts.class);
                                if(unfilteredPost.getTitle().toLowerCase().contains(searchEditText.getText().toString().toLowerCase())) {
                                    if(category.equals("All")){
                                        unfilteredPosts.add(unfilteredPost);
                                    }
                                    else if(unfilteredPost.getCategory().equals(category)){
                                        unfilteredPosts.add(unfilteredPost);
                                    }
                                }
                            }
                        }

                        feedAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }
}