package com.example.reuse;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
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
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class EditProfileFragment extends Fragment {

    public static final int PICK_IMAGE = 1;
    ImageView profileImage;
    Button changeProfileImageButton,saveButton;
    FirebaseUser user;
    EditText displayNameEditText;
    StorageReference storageReference;
    boolean imageSelected = false;
    Uri uri;
    DatabaseReference databaseRef;
    String initialName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseRef = FirebaseDatabase.getInstance("https://reuse-20200204-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users");
        storageReference = FirebaseStorage.getInstance().getReference();
        profileImage = view.findViewById(R.id.profileImage);
        changeProfileImageButton = view.findViewById(R.id.changeProfileImageButton);
        saveButton = view.findViewById(R.id.saveButton);
        displayNameEditText = view.findViewById(R.id.displayNameEditText);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imageSelected == true){
                    uploadImageToFirebase(uri);
                }
                updateDisplayName(displayNameEditText.getText().toString());
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        changeProfileImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }

        });
        load();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        try{
            if (requestCode == PICK_IMAGE  &&  resultCode == RESULT_OK) {
                if(data!=null){
                    uri = data.getData();
                    profileImage.setImageURI(uri);
                    imageSelected = true;
                }
            }
        }catch (Exception e){
            Toast.makeText(getContext(), "No Image Picked", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImageToFirebase(Uri uri) {
        StorageReference fileRef = storageReference.child("ProfileImages/"+FirebaseAuth.getInstance().getCurrentUser().getUid().toString());
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("profilePhotoUrl",uri.toString());
                        databaseRef.child(user.getUid().toString()).updateChildren(map);
                    }
                });

            }
        });
    }
    private void updateDisplayName(String displayName){
        databaseRef.child(user.getUid()).child("displayName").setValue(displayName);
    }

    public void load(){
        databaseRef.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String url = String.valueOf(snapshot.child("profilePhotoUrl").getValue());
                String name = String.valueOf(snapshot.child("displayName").getValue());
                Log.d("Fahad",url);
                displayNameEditText.setText(name);
                if(url.length()<10)
                    return;
                Picasso.get().load(url).into(profileImage);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}