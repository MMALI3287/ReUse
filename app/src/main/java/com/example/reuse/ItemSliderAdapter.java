package com.example.reuse;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ItemSliderAdapter extends SliderViewAdapter<ItemSliderAdapter.Holder> {

    ArrayList<String> imageUrls;

    public ItemSliderAdapter(ArrayList<String> imageUrls){
        this.imageUrls = imageUrls;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_item,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder viewHolder, int position) {
        Picasso.get().load(imageUrls.get(position)).into(viewHolder.imageView);
    }

    @Override
    public int getCount() {
        return imageUrls.size();
    }

    public class Holder extends SliderViewAdapter.ViewHolder{
        ImageView imageView;

        public Holder(View itemView){
            super(itemView);

            imageView = itemView.findViewById(R.id.image_view);
        }

    }
}
