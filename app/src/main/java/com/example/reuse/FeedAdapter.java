package com.example.reuse;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.MyViewHolder> {

    Context context;
    ArrayList<UnfilteredPosts> unfilteredPosts;

    public FeedAdapter(Context context, ArrayList<UnfilteredPosts> unfilteredPosts) {
        this.context = context;
        this.unfilteredPosts = unfilteredPosts;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.feed_list_item,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        UnfilteredPosts unfilteredPost = unfilteredPosts.get(position);
        Picasso.get().load(unfilteredPost.getImages().getImage1()).into(holder.cardImage);
        holder.cardTitleText.setText(unfilteredPost.getTitle());
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = ((AppCompatActivity)context).getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame, new ItemFragment(unfilteredPosts.get(position)), null)
                        .setReorderingAllowed(true)
                        .addToBackStack("theFragment")
                        .commit();
            }
        });

    }

    @Override
    public int getItemCount() {
        return  unfilteredPosts.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        CardView parent;
        ImageView cardImage;
        TextView cardTitleText;
        TextView cardTimeText;
        TextView cardLocationText;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            parent = itemView.findViewById(R.id.parent);
            cardImage = itemView.findViewById(R.id.cardImage);
            cardTitleText = itemView.findViewById(R.id.cardTitleText);
            cardTimeText = itemView.findViewById(R.id.cardTimeText);
            cardLocationText = itemView.findViewById(R.id.cardLocationText);
        }
    }
}
