package com.example.quanlykho;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.conts.Constant;
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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import agency.tango.android.avatarview.IImageLoader;
import agency.tango.android.avatarview.loader.PicassoLoader;
import agency.tango.android.avatarview.views.AvatarView;

public class TaiKhoanActivity extends AppCompatActivity {
    NhanVien nhanVienLogin;
    EditText et_Ma, et_Ten, et_DiaChi, et_Sdt, et_Email, et_ChucVu, et_User, et_GioiTinh;
    Button bt_Sua, bt_Luu, bt_DoiMK;
    ImageView ivBack;
    AvatarView avatarView;
    IImageLoader imageLoader;
    String KEY="";
    String urlImage="";
    TextView txtTen;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    int REQUEST_CODE_IMAGE = 1;
    int REQUEST_CODE_IMAGE_STORAGE = 2;
    StorageReference storageRef = storage.getReferenceFromUrl("gs://quanlykho-c05ef.appspot.com/");

    Bitmap bitmapCamera;

    ProgressDialog progressDialog;

    DatabaseReference mData= FirebaseDatabase.getInstance().getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tai_khoan);
        addControls();
        addEvents();
        initFirebase();
    }

    private void initFirebase() {
        mData.child("NhanVien").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                NhanVienFirebase nhanVienFirebase=dataSnapshot.getValue(NhanVienFirebase.class);
                if(nhanVienFirebase.getMaNhanVien()==nhanVienLogin.getMaNhanVien()){
                    KEY=dataSnapshot.getKey();
                    urlImage=nhanVienFirebase.getUrlImage();
                    imageLoader.loadImage(avatarView,urlImage,nhanVienFirebase.getTenNhanVien());
                    txtTen.setText(nhanVienFirebase.getTenNhanVien());
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
        bt_Sua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xuLySua();
            }
        });
        bt_DoiMK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xuLyDoiMatKhau();
            }
        });
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        bt_Luu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xuLyLuu();
            }
        });
        avatarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerForContextMenu(avatarView);
            }
        });
    }

    private void xuLyLuu() {
        nhanVienLogin.setUserName(et_User.getText().toString());
        nhanVienLogin.setTenNhanVien(et_Ten.getText().toString());
        nhanVienLogin.setEmail(et_Email.getText().toString());
        nhanVienLogin.setPhone(Integer.parseInt(et_Sdt.getText().toString()));
        nhanVienLogin.setDiaChi(et_DiaChi.getText().toString());
        String gioiTinh=et_GioiTinh.getText().toString().toUpperCase();
        if(gioiTinh.equals("NAM")){
            nhanVienLogin.setGioiTinh(0);
        }
        else
            nhanVienLogin.setGioiTinh(1);
        SuaNhanVienTask task= new SuaNhanVienTask();
        task.execute(nhanVienLogin);
    }

    private void xuLyDoiMatKhau() {
        Intent intent= new Intent(TaiKhoanActivity.this,DoiMatKhauActivity.class);
        intent.putExtra("NHANVIEN",nhanVienLogin);
        startActivity(intent);
    }

    private void xuLySua() {
        et_Ma.setEnabled(false);
        et_Ten.setEnabled(true);
        et_ChucVu.setEnabled(false);
        et_User.setEnabled(true);
        et_Email.setEnabled(true);
        et_Sdt.setEnabled(true);
        et_DiaChi.setEnabled(true);
        et_GioiTinh.setEnabled(true);
    }

    private void addControls() {
        et_Ma = findViewById(R.id.edtMaNV);
        et_Ten = findViewById(R.id.edtTenNV);
        et_DiaChi = findViewById(R.id.edtDiaChi);
        et_Sdt = findViewById(R.id.edtSDT);
        et_Email = findViewById(R.id.edtEmail);
        et_ChucVu = findViewById(R.id.edtChucVu);
        et_User = findViewById(R.id.edtUserName);
        et_GioiTinh=findViewById(R.id.edtGioiTinh);

        Intent intent = getIntent();
        nhanVienLogin = (NhanVien) intent.getSerializableExtra("NHANVIEN");

        et_Ma.setText(nhanVienLogin.getMaNhanVien()+"");
        et_Ten.setText(nhanVienLogin.getTenNhanVien());
        et_DiaChi.setText(nhanVienLogin.getDiaChi());
        et_Sdt.setText("0"+nhanVienLogin.getPhone()+"");
        et_Email.setText(nhanVienLogin.getEmail());
        et_User.setText(nhanVienLogin.getUserName());
        if (nhanVienLogin.getRole() == 0) {
            et_ChucVu.setText("Quản lý");
        }
        else if(nhanVienLogin.getRole()==1){
            et_ChucVu.setText("Nhân viên");
        }
        if (nhanVienLogin.getGioiTinh()==0){
             et_GioiTinh.setText("Nam");
        }
        else if(nhanVienLogin.getGioiTinh()==1){
            et_GioiTinh.setText("Nữ");
        }
        et_Ma.setEnabled(false);
        et_Ten.setEnabled(false);
        et_ChucVu.setEnabled(false);
        et_User.setEnabled(false);
        et_Email.setEnabled(false);
        et_Sdt.setEnabled(false);
        et_DiaChi.setEnabled(false);
        et_GioiTinh.setEnabled(false);
        bt_DoiMK=findViewById(R.id.btnDoiMK);
        bt_Luu=findViewById(R.id.btnLuu);
        bt_Sua=findViewById(R.id.btnSua);

        ivBack=findViewById(R.id.iv_backTaiKhoan);
        txtTen=findViewById(R.id.txtTen);
        avatarView=findViewById(R.id.avatar_view);
        imageLoader= new PicassoLoader();
        imageLoader.loadImage(avatarView,"https://raw.githubusercontent.com/quoccuong151197/FirebaseStorage/master/app/src/main/res/drawable/ic.png","Image");

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
    private void xuLyXem() {
        Intent intent = new Intent(TaiKhoanActivity.this, ImageViewActivity.class);
        intent.putExtra("IMAGEURL", urlImage);
        startActivity(intent);
    }
    private void xuLyDoi() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(TaiKhoanActivity.this);
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
        progressDialog= new ProgressDialog(TaiKhoanActivity.this);
        progressDialog.setTitle("Đang xử lý");
        progressDialog.setMessage("Vui lòng chờ...");
        progressDialog.show();
        String child = nhanVienLogin.getUserName();
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
                                imageLoader.loadImage(avatarView, downloadUri.toString(), nhanVienLogin.getTenNhanVien());
                                progressDialog.dismiss();
                                android.app.AlertDialog.Builder alertDialog= new android.app.AlertDialog.Builder(TaiKhoanActivity.this);
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

    class SuaNhanVienTask extends AsyncTask<NhanVien,Void,Boolean>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean == true) {
                AlertDialog.Builder alertDialog= new AlertDialog.Builder(TaiKhoanActivity.this);
                alertDialog.setTitle("Sửa thành công");
                alertDialog.setIcon(R.drawable.ic_ok);
                alertDialog.setNegativeButton("OK",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
            }
            else if (aBoolean==false){
                AlertDialog.Builder alertDialog= new AlertDialog.Builder(TaiKhoanActivity.this);
                alertDialog.setTitle("Sửa thất bại");
                alertDialog.setIcon(R.drawable.ic_error);
                alertDialog.setNegativeButton("OK",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Boolean doInBackground(NhanVien... nhanViens) {
            try{
                NhanVien nv=nhanViens[0];
                boolean gt=false;
                if(nv.getGioiTinh()==0){
                    gt=true;
                }
                else if(nv.getGioiTinh()==1){
                    gt=false;
                }
                String params="?maNVSua="+nv.getMaNhanVien()+"&tenNV="+URLEncoder.encode(nv.getTenNhanVien())+"&gioiTinh="+gt+"&diaChi="+URLEncoder.encode(nv.getDiaChi())+"&phone="+nv.getPhone()
                        +"&email="+URLEncoder.encode(nv.getEmail())+"&userName="+URLEncoder.encode(nv.getUserName())+"&password="+URLEncoder.encode(nv.getPassword())+"&role="+nv.getRole();
                URL url= new URL(Constant.IP_ADDRESS+"NhanVien/"+params);
                HttpURLConnection connection= (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");

                connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");

                InputStreamReader isr = new InputStreamReader(connection.getInputStream(), "UTF-8");
                BufferedReader br = new BufferedReader(isr);
                StringBuilder builder = new StringBuilder();
                String line = null;
                while ((line = br.readLine()) != null) {
                    builder.append(line);
                }
                boolean kq=builder.toString().contains("true");
                return kq;
            }
            catch (Exception ex){
                Log.e("LOI",ex.toString());
            }
            return false;
        }
    }
}
