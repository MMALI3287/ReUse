package com.example.reuse;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.reuse.databinding.FragmentItemBinding;
import com.example.reuse.databinding.FragmentPostBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ItemFragment extends Fragment {

    FragmentItemBinding binding;
    UnfilteredPosts post;
    DatabaseReference databaseRef;
    DatabaseReference databaseRefChat;
    FirebaseUser user;


    public ItemFragment() {

    }
    public ItemFragment(UnfilteredPosts post){
        this.post = post;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.descriptionText.setText(post.getDescription());
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user.getUid().equals(post.getUid())){
            binding.messageButton.setVisibility(View.INVISIBLE);
        }
        binding.titleText.setText(post.getTitle());
        databaseRef = FirebaseDatabase.getInstance("https://reuse-20200204-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users");
        databaseRefChat = FirebaseDatabase.getInstance("https://reuse-20200204-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Chats");
        databaseRef.child(post.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String url = String.valueOf(snapshot.child("profilePhotoUrl").getValue());
                Log.d("Fahad",url);
                String displayName = String.valueOf(snapshot.child("displayName").getValue());
                binding.profileNameText.setText(displayName);
                if(url.length()<10)
                    return;
                Picasso.get().load(url).into(binding.profileImage);
                ArrayList<String> imageUrls = new ArrayList<>();
                for(int i=1;i<=Integer.parseInt(post.getImageCount());i++){
                    String temp = "";
                    if(i==1){
                        temp = post.getImages().getImage1();
                    }
                    else if(i==2){
                        temp = post.getImages().getImage2();
                    }
                    else if(i==3){
                        temp = post.getImages().getImage3();
                    }
                    imageUrls.add(temp);
                }
                ItemSliderAdapter itemSliderAdapter = new ItemSliderAdapter(imageUrls);
                binding.photos.setSliderAdapter(itemSliderAdapter);
                binding.photos.setIndicatorAnimation(IndicatorAnimationType.WORM);
                binding.photos.setSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        binding.messageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("FAHAD","HELLO MESSAGE CLICKED");
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("title",post.getTitle());
                map.put("postId",post.getPostId());
                map.put("posterId",post.getUid());
                map.put("messageSenderId",user.getUid());
                map.put("postImageUrl",post.getImages().getImage1());
                databaseRefChat.child(user.getUid()).child(post.getPostId()+user.getUid()).updateChildren(map);
                databaseRefChat.child(post.getUid()).child(post.getPostId()+user.getUid()).updateChildren(map);
                FragmentManager fragmentManager = ((AppCompatActivity)getContext()).getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame, new MessageFragment(post.getPostId()+user.getUid()), null)
                        .setReorderingAllowed(true)
                        .addToBackStack("theFragment")
                        .commit();
            }
        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentItemBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }
}