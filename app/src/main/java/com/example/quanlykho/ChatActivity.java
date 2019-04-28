package com.example.quanlykho;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.example.adapter.MessageAdapter;
import com.example.firebase.ChatMessage;
import com.example.firebase.NhanVienFirebase;
import com.example.model.NhanVien;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {
    FloatingActionButton fabSend;
    EditText edtInput;
    ListView lvMessege;
    Intent intent;
    NhanVien nhanVien;
    MessageAdapter adapter;
    ArrayList<ChatMessage> dsMessages;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DatabaseReference mData = FirebaseDatabase.getInstance().getReference();
    String urlImage="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        addControls();
        addEvents();
    }

    private void initFirebase() {
        mData.child("NhanVien").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                NhanVienFirebase nhanVienFirebase=dataSnapshot.getValue(NhanVienFirebase.class);
                if(nhanVienFirebase.getUserName().equals(nhanVien.getUserName())==true){
                    urlImage=nhanVienFirebase.getUrlImage();
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
        fabSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String aaaaa=urlImage;
                mData.child("chats")
                        .push()
                        .setValue(new ChatMessage(edtInput.getText().toString(),nhanVien.getUserName(),urlImage));

                // Clear the input
                edtInput.setText("");
            }
        });
    }

    private void addControls() {
        intent = getIntent();
        nhanVien = (NhanVien) intent.getSerializableExtra("NHANVIEN");
        initFirebase();
        dsMessages = new ArrayList<>();
        fabSend = findViewById(R.id.fab);
        edtInput = findViewById(R.id.edtInput);
        lvMessege = findViewById(R.id.lvMessages);
        adapter = new MessageAdapter(ChatActivity.this,R.layout.item_chat_guest,dsMessages);
        lvMessege.setAdapter(adapter);
        Login(nhanVien.getEmail().toString(), nhanVien.getPassword().toString());
    }
    private void Login(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //Toast.makeText(ChatActivity.this, "Đăng nhập firebase thành công", Toast.LENGTH_LONG).show();
                            displayChatMessages();
                        } else {
                            //Toast.makeText(ChatActivity.this, "Đăng nhập firebase thất bại", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void displayChatMessages() {
        mData.child("chats").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ChatMessage chatMessage=dataSnapshot.getValue(ChatMessage.class);
                chatMessage.setMessageUser(nhanVien.getUserName());
                dsMessages.add(chatMessage);
                adapter.notifyDataSetChanged();
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
}