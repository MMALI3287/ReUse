package com.example.reuse;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;

public class SliderAdapter extends SliderViewAdapter<SliderAdapter.Holder> {

    ArrayList<Uri> imageUris;

    public SliderAdapter(ArrayList<Uri> imageUris){
        this.imageUris = imageUris;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_item,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder viewHolder, int position) {
        viewHolder.imageView.setImageURI(imageUris.get(position));
    }

    @Override
    public int getCount() {
        return imageUris.size();
    }

    public class Holder extends SliderViewAdapter.ViewHolder{
        ImageView imageView;

        public Holder(View itemView){
            super(itemView);

            imageView = itemView.findViewById(R.id.image_view);
        }

    }
}
