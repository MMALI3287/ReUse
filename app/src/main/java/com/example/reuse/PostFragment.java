package com.example.reuse;

import static android.app.Activity.RESULT_OK;

import android.content.ContentResolver;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.reuse.databinding.FragmentPostBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;



public class PostFragment extends Fragment {

    DatabaseReference databaseRef;
    DatabaseReference databaseRefPost;
    DatabaseReference databaseRefUnfilteredPost;
    FirebaseUser user;
    FragmentPostBinding binding;
    private ArrayList<Uri> imageUris;
    boolean imageSelected=false;
    StorageReference storageReference;
    private static final int REQUEST_LOCATION_SETTINGS = 1;
    TextView location;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPostBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        databaseRef = FirebaseDatabase.getInstance("https://reuse-20200204-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users");
        databaseRefPost = FirebaseDatabase.getInstance("https://reuse-20200204-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Posts");
        databaseRefUnfilteredPost = FirebaseDatabase.getInstance("https://reuse-20200204-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("UnfilteredPosts");
        storageReference = FirebaseStorage.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        imageUris = new ArrayList<>();
        load();
        binding.addPhotosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("LOBAPAD",binding.addPhotosButton.getText().toString());
                if(binding.addPhotosButton.getText().toString().equals("Add photos")){
                    Log.d("LOBAPAD","Button clicked");
                    pickImagesIntent();
                }
            }
        });
        Uri imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + getResources().getResourcePackageName(R.drawable.add_photos)
                + '/' + getResources().getResourceTypeName(R.drawable.add_photos) + '/' + getResources().getResourceEntryName(R.drawable.add_photos) );
        imageUris.add(imageUri);
        SliderAdapter sliderAdapter = new SliderAdapter(imageUris);
        binding.photos.setSliderAdapter(sliderAdapter);
        binding.photos.setIndicatorAnimation(IndicatorAnimationType.WORM);
        binding.photos.setSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION);
        binding.postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imageSelected == false){
                    Toast.makeText(getContext(), "Include atleast 1 Image", Toast.LENGTH_SHORT).show();
                    return;
                }
                String title = binding.titleEditText.getText().toString();
                String description = binding.descriptionEditText.getText().toString();
                String category="football";
                String location=binding.locationTextView.getText().toString();
                if(title.length()<3){
                    Toast.makeText(getContext(), "Title too short", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(description.length()<5){
                    Toast.makeText(getContext(), "Description too short", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.d("HERE!1","Post button pressed");
                uploadToFirebase(imageUris,title,description,location,category);
            }
        });
        binding.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),MapsActivity.class);
                startActivity(intent);
            }
        });
    }

    public void load(){
        databaseRef.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String url = String.valueOf(snapshot.child("profilePhotoUrl").getValue());
                Log.d("Fahad",url);
                String displayName = String.valueOf(snapshot.child("displayName").getValue());
                binding.displayName.setText(displayName);
                if(url.length()>10){
                    Picasso.get().load(url).into(binding.profileImage);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void pickImagesIntent(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Image(s)"),1);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode== RESULT_OK){
            imageUris.clear();
            if(data.getClipData()!=null){
                int count = data.getClipData().getItemCount();
                if(count>3){
                    Toast.makeText(getActivity(), "Too many Images Selected", Toast.LENGTH_SHORT).show();
                    return;
                }
                for(int i=0;i<count;i++){
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    imageUris.add(imageUri);
                }
            }
            else{
                Uri imageUri = data.getData();
                imageUris.add(imageUri);
            }
            SliderAdapter sliderAdapter = new SliderAdapter(imageUris);
            imageSelected = true;
            binding.photos.setSliderAdapter(sliderAdapter);
        }
        if (requestCode == REQUEST_LOCATION_SETTINGS && resultCode == RESULT_OK) {
            String finalPlaceName = data.getStringExtra("place_name");
            location.setText("Location: "+finalPlaceName);
        }
    }
    private void uploadToFirebase(ArrayList<Uri> imageUris,String title,String description,String location,String category) {
        int i=1;
        long postId = System.currentTimeMillis();
        for(Uri imageUri:imageUris){
            StorageReference fileRef = storageReference.child("PostImages/"+FirebaseAuth.getInstance().getCurrentUser().getUid().toString()+"/"+postId+"/image"+i);
            int finalI = i;
            fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Map<String, Object> map = new HashMap<String, Object>();
                            map.put("image"+ finalI,uri.toString());
                            databaseRefPost.child(user.getUid().toString()).child(String.valueOf(postId)).child("images").updateChildren(map);
                            databaseRefUnfilteredPost.child(String.valueOf(postId)).child("images").updateChildren(map);
                        }
                    });

                }
            });
            i++;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("title",title);
        map.put("description",description);
        map.put("imageCount",String.valueOf(imageUris.size()));
        map.put("uid",user.getUid().toString());
        map.put("postId",String.valueOf(postId));
        map.put("time",System.currentTimeMillis());
        map.put("location",String.valueOf(postId));
        databaseRefPost.child(user.getUid().toString()).child(String.valueOf(postId)).updateChildren(map);
        databaseRefUnfilteredPost.child(String.valueOf(postId)).updateChildren(map);

    }



}


