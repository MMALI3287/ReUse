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
        View v = LayoutInflater.from(context).inflate(R.layout.feed_list_item_grid,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        UnfilteredPosts unfilteredPost = unfilteredPosts.get(position);
        try {
            Picasso.get().load(unfilteredPost.getImages().getImage1()).into(holder.cardImage);
        }catch (Exception e){

        }
        holder.cardTitleText.setText(unfilteredPost.getTitle());
        holder.cardTimeText.setText(timeCalc(unfilteredPost.getTime()));
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

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            parent = itemView.findViewById(R.id.parent);
            cardImage = itemView.findViewById(R.id.cardImage);
            cardTitleText = itemView.findViewById(R.id.cardTitleText);
            cardTimeText = itemView.findViewById(R.id.cardTimeText);
        }
    }

    public String timeCalc(String time){
        long current= System.currentTimeMillis();
        long given = Long.parseLong(time);
        long diff = current - given;
        long diffSeconds = diff / 1000;
        long diffMinutes = diffSeconds / 60;
        long diffHours = diffMinutes / 60;
        long diffDays = diffHours / 24;
        long diffWeeks = diffDays / 7;
        long diffMonths = diffDays / 30;
        long diffYears = diffDays / 365;
        if (diffYears > 0)
            return diffYears + " years ago";
        else if (diffMonths > 0)
            return diffMonths + " months ago";
        else if (diffWeeks > 0)
            return diffWeeks + " weeks ago";
        else if (diffDays > 0)
            return diffDays + " days ago";
        else if (diffHours > 0)
            return diffHours + " hours ago";
        else if (diffMinutes > 0)
            return diffMinutes + " minutes ago";
        else
            return "Just now";
    }
}
