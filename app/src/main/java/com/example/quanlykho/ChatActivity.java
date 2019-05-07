package com.example.quanlykho;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.adapter.ChatAdapter;
import com.example.adapterimpl.ItemClick;
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
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONObject;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.HttpHeaders;
import cz.msebera.android.httpclient.entity.StringEntity;

public class ChatActivity extends AppCompatActivity implements ItemClick {
    FloatingActionButton fabSend;
    EditText edtInput;
    RecyclerView rcyMessege;
    ImageView imgBack;
    Intent intent;
    NhanVien nhanVien;
    ChatAdapter adapter;
    ArrayList<ChatMessage> dsMessages;
    ArrayList<String> dsKEY = new ArrayList<>();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DatabaseReference mData = FirebaseDatabase.getInstance().getReference();
    String urlImage = "";
    private static final String TAG = "ChatActivity";

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
                NhanVienFirebase nhanVienFirebase = dataSnapshot.getValue(NhanVienFirebase.class);
                if (nhanVienFirebase.getUserName().equals(nhanVien.getUserName()) == true) {
                    urlImage = nhanVienFirebase.getUrlImage();
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
                String aaaaa = urlImage;
                mData.child("chats")
                        .push()
                        .setValue(new ChatMessage(edtInput.getText().toString(), nhanVien.getUserName(), urlImage));
                sendNotification();
                edtInput.setText("");
            }
        });
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
        imgBack = findViewById(R.id.iv_back);
        rcyMessege = findViewById(R.id.lvMessages);
        rcyMessege.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
        adapter = new ChatAdapter(ChatActivity.this, dsMessages, nhanVien);
        adapter.isClicked(this);
        rcyMessege.setAdapter(adapter);
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
                ChatMessage chatMessage = dataSnapshot.getValue(ChatMessage.class);
                dsMessages.add(chatMessage);
                dsKEY.add(dataSnapshot.getKey());
                adapter.notifyDataSetChanged();
                rcyMessege.smoothScrollToPosition(rcyMessege.getAdapter().getItemCount() - 1);
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

    @Override
    public void isItemClick(final int position) {
        final ChatMessage chatMessage = dsMessages.get(position);
        //Toast.makeText(ChatActivity.this,"chọn: "+chatMessage.getMessageUser(),Toast.LENGTH_LONG).show();
        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
        builder.setTitle("Chọn chức năng");
        builder.setNegativeButton("Xóa", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                xuLyXoaTinNhan(position);
            }
        }).show();
    }

    private void xuLyXoaTinNhan(int position) {
        dsMessages.remove(dsMessages.get(position));
        adapter.notifyDataSetChanged();
        mData.child("chats").child(dsKEY.get(position)).removeValue().addOnCompleteListener(ChatActivity.this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    AlertDialog.Builder mess = new AlertDialog.Builder(ChatActivity.this);
                    mess.setTitle("Xóa thành công").setIcon(R.drawable.ic_ok).setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).show();
                } else {
                    AlertDialog.Builder mess = new AlertDialog.Builder(ChatActivity.this);
                    mess.setTitle("Xóa thất bại").setIcon(R.drawable.ic_error).show();
                }
            }
        });
    }

    private void sendNotification() {
        try {
            String url = "https://fcm.googleapis.com/fcm/send";
            AsyncHttpClient client = new AsyncHttpClient();

            client.addHeader(HttpHeaders.AUTHORIZATION, "key=AIzaSyDGDYP06sfHOwY8JCX8AFmQ6K1D1sTpZ7g");
            client.addHeader(HttpHeaders.CONTENT_TYPE, RequestParams.APPLICATION_JSON);
            JSONObject params = new JSONObject();

            params.put("to", "/topics/ThongBao");

            JSONObject notificationObject = new JSONObject();
            notificationObject.put("body", "Ban co tin nhan moi");
            notificationObject.put("title", "Gui tu " + removeAccent(nhanVien.getTenNhanVien()));

            params.put("notification", notificationObject);

            StringEntity entity = new StringEntity(params.toString());

            client.post(getApplicationContext(), url, entity, RequestParams.APPLICATION_JSON, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString, Throwable throwable) {
                    Log.i(TAG, responseString);
                }

                @Override
                public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString) {
                    Log.i(TAG, responseString);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static String removeAccent(String s) {
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("");
    }
}