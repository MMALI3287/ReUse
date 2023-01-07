package com.example.reuse;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.ReUse);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/Poppins-Regular.ttf");
        textView.setTypeface(custom_font);
    }
}