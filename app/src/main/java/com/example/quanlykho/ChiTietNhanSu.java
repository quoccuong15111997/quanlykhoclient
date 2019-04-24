package com.example.quanlykho;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.example.firebase.NhanVienFirebase;
import com.example.model.NhanVien;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ChiTietNhanSu extends AppCompatActivity {
    Intent intent;
    ImageView imgNhanVien;
    DatabaseReference mData;
    ArrayList<NhanVienFirebase> dsNhanVien;
    NhanVien nhanVien;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chi_tiet_nhan_su);
        intent=getIntent();
        nhanVien= (NhanVien) intent.getSerializableExtra("NHANVIEN");
        addControls();
        addEvents();
        initFirebase();
    }

    private void initFirebase() {
        mData= FirebaseDatabase.getInstance().getReference();
        mData.child("NhanVien").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                NhanVienFirebase nhanVienFirebase=dataSnapshot.getValue(NhanVienFirebase.class);
                if(nhanVienFirebase.getUserName().equals(nhanVien.getUserName())==true){
                    Picasso.with(ChiTietNhanSu.this).load(nhanVienFirebase.getUrlImage()).into(imgNhanVien);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void addEvents() {
    }

    private void addControls() {
        imgNhanVien=findViewById(R.id.imgNhanVien);
        dsNhanVien= new ArrayList<NhanVienFirebase>();
    }
}
