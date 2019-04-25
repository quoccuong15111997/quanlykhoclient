package com.example.quanlykho;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.firebase.NhanVienFirebase;
import com.example.model.NhanVien;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import agency.tango.android.avatarview.IImageLoader;
import agency.tango.android.avatarview.loader.PicassoLoader;
import agency.tango.android.avatarview.views.AvatarView;

public class ChiTietNhanSu extends AppCompatActivity {
    Intent intent;
    DatabaseReference mData;
    ArrayList<NhanVienFirebase> dsNhanVienFirebase = new ArrayList<>();
    NhanVien nhanVien;

    AvatarView avatarView;
    IImageLoader imageLoader;
    TextView txtName, txtChucVu;
    ImageView iv_back;


    NhanVienFirebase nhanVienFirebase;
    String urlImage = "";
    String KEY = "";
    FirebaseStorage storage = FirebaseStorage.getInstance();
    int REQUEST_CODE_IMAGE = 1;
    int REQUEST_CODE_IMAGE_STORAGE = 2;
    StorageReference storageRef = storage.getReferenceFromUrl("gs://quanlykho-c05ef.appspot.com/");

    Bitmap bitmapCamera;

    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chi_tiet_nhan_su);
        intent = getIntent();
        nhanVien = (NhanVien) intent.getSerializableExtra("NHANVIEN");
        addControls();
        addEvents();
        initFirebase();
    }


    private void initFirebase() {
        mData = FirebaseDatabase.getInstance().getReference();
        mData.child("NhanVien").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                nhanVienFirebase = dataSnapshot.getValue(NhanVienFirebase.class);
                if (nhanVienFirebase.getUserName().equals(nhanVien.getUserName()) == true) {
                    imageLoader = new PicassoLoader();
                    urlImage = nhanVienFirebase.getUrlImage();
                    KEY = dataSnapshot.getKey();
                    imageLoader.loadImage(avatarView, urlImage, nhanVienFirebase.getTenNhanVien());
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
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        avatarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerForContextMenu(avatarView);
            }
        });
    }

    private void addControls() {
        avatarView = findViewById(R.id.avatar_view_example);
        txtName = findViewById(R.id.txtName);
        txtName.setText(nhanVien.getTenNhanVien());
        iv_back = findViewById(R.id.iv_backNhanVien);
        txtChucVu = findViewById(R.id.txtChucVu);
        if (nhanVien.getRole() == 0) {
            txtChucVu.setText("Quản lý");
        } else if (nhanVien.getRole() == 1) {
            txtChucVu.setText("Nhân viên");
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater()
                .inflate(R.menu.context_menu_image, menu);
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemXem:
                xuLyXem();
                break;
            case R.id.itemDoi:
                xuLyDoi();
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void xuLyDoi() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ChiTietNhanSu.this);
        builder.setTitle("Ảnh từ");
        builder.setNegativeButton("Mở máy ảnh", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_CODE_IMAGE);
            }
        }).setPositiveButton("Bộ sưu tập", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent,REQUEST_CODE_IMAGE_STORAGE);
            }
        }).show();
    }

    private void xuLyXem() {
        Intent intent = new Intent(ChiTietNhanSu.this, ImageViewActivity.class);
        intent.putExtra("IMAGEURL", urlImage);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE_IMAGE && resultCode == RESULT_OK && data != null) {
            bitmapCamera = (Bitmap) data.getExtras().get("data");
            xuLyUpload();
        }
        else if (requestCode == REQUEST_CODE_IMAGE_STORAGE && resultCode == RESULT_OK && data != null) {
            Uri uri=data.getData();
            String path=getRealPathFromURI(uri);
            xuLyUploadStroge(path);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void xuLyUploadStroge(String path) {
        try{
            bitmapCamera=getThumbnail(path);
            xuLyUpload();
        }
        catch (Exception ex){
            Log.e("LOI",ex.toString());
        }
    }

    private void xuLyUpload() {
        progressDialog= new ProgressDialog(ChiTietNhanSu.this);
        progressDialog.setTitle("Đang xử lý");
        progressDialog.setMessage("Vui lòng chờ...");
        progressDialog.show();
        String child = nhanVien.getUserName();
        final StorageReference mountainsRef = storageRef.child(child);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmapCamera.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mountainsRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                //Toast.makeText(ChiTietNhanSu.this, "Thất bại", Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //Toast.makeText(ChiTietNhanSu.this, "Thành công", Toast.LENGTH_LONG).show();
            }
        });
        final StorageReference ref = storageRef.child(child);
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    final Uri downloadUri = task.getResult();
                    urlImage=downloadUri.toString();
                    mData.child("NhanVien").child(KEY).child("urlImage").setValue((downloadUri.toString()), new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                //Toast.makeText(ChiTietNhanSu.this, "Lưu databse Thành công", Toast.LENGTH_SHORT).show();
                                imageLoader.loadImage(avatarView, downloadUri.toString(), nhanVien.getTenNhanVien());
                                progressDialog.dismiss();
                                android.app.AlertDialog.Builder alertDialog= new android.app.AlertDialog.Builder(ChiTietNhanSu.this);
                                alertDialog.setTitle("Lưu thành công");
                                alertDialog.setIcon(R.drawable.ic_ok);
                                alertDialog.setNegativeButton("OK",new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                }).show();
                            } else {
                               // Toast.makeText(ChiTietNhanSu.this, "Lưu dadabase thất bại", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    // Handle failures
                    // ...
                }
            }
        });
    }
    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }
    public Bitmap getThumbnail(String pathHinh)
    {
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathHinh, bounds);
        if ((bounds.outWidth == -1) || (bounds.outHeight == -1))
            return null;
        int originalSize = (bounds.outHeight > bounds.outWidth) ?
                bounds.outHeight
                : bounds.outWidth;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = originalSize / 500;
        return BitmapFactory.decodeFile(pathHinh, opts);
    }
}
