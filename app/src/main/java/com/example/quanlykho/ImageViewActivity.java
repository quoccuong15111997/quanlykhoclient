package com.example.quanlykho;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import agency.tango.android.avatarview.IImageLoader;

public class ImageViewActivity extends AppCompatActivity {
    ImageView imgNhanVien;
    Intent intent;
    String url = "";
    ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        intent = getIntent();
        url = intent.getStringExtra("IMAGEURL");
        addControls();
        initData();
        addEvents();
    }

    private void addEvents() {
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initData() {
        Picasso.with(ImageViewActivity.this).load(url).into(imgNhanVien);
    }

    private void addControls() {
        imgNhanVien=findViewById(R.id.imgNhanVien);
        imgBack=findViewById(R.id.iv_back);
    }
}
