package com.example.reuse;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ListedItemsAdapter extends RecyclerView.Adapter<ListedItemsAdapter.MyViewHolder>{
    Context context;
    ArrayList<Posts> posts;
    StorageReference storageRef;
    DatabaseReference databaseRefPosts;
    DatabaseReference databaseRefUnfilteredPosts;

    public ListedItemsAdapter(Context context, ArrayList<Posts> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public ListedItemsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.profile_listed_item,parent,false);
        return new ListedItemsAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ListedItemsAdapter.MyViewHolder holder, int position) {
        Posts post = posts.get(position);
        Picasso.get().load(post.getImages().getImage1()).into(holder.cardImage);
        holder.cardTitleText.setText(post.getTitle());
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseRefPosts = FirebaseDatabase.getInstance("https://reuse-20200204-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Posts");
                databaseRefUnfilteredPosts = FirebaseDatabase.getInstance("https://reuse-20200204-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("UnfilteredPosts");
                storageRef = FirebaseStorage.getInstance().getReference();
                databaseRefPosts.child(post.getUid()).child(post.getPostId()).removeValue();
                databaseRefUnfilteredPosts.child(post.getPostId()).removeValue();
                //storageRef.child("PostImages").child(post.getUid()).child(post.getPostId()).delete();
                notifyDataSetChanged();

            }
        });

    }

    @Override
    public int getItemCount() {
        return  posts.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        CardView parent;
        ImageView cardImage;
        TextView cardTitleText;
        TextView cardTimeText;
        TextView cardLocationText;
        ImageView deleteButton;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            parent = itemView.findViewById(R.id.parent);
            cardImage = itemView.findViewById(R.id.cardImage);
            cardTitleText = itemView.findViewById(R.id.cardTitleText);
            cardTimeText = itemView.findViewById(R.id.cardTimeText);
            cardLocationText = itemView.findViewById(R.id.cardLocationText);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
