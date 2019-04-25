package com.example.quanlykho;

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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.conts.Constant;
import com.example.firebase.NhanVienFirebase;
import com.example.model.NhanVien;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import agency.tango.android.avatarview.IImageLoader;
import agency.tango.android.avatarview.loader.PicassoLoader;
import agency.tango.android.avatarview.views.AvatarView;

public class ThemNhanVienActivity extends AppCompatActivity {
    EditText edtTen, edtGioiTinh, edtSoDienThoai, edtEmail, edtDiaChi, edtUserName, edtPassword;
    Spinner spinner_ChucVu;
    ArrayAdapter<String> chucVuAdapter;
    Button btnLuu;
    ImageView iv_back;

    AvatarView avatarView;
    IImageLoader imageLoader;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    int REQUEST_CODE_IMAGE = 1;
    int REQUEST_CODE_IMAGE_STORAGE = 2;
    StorageReference storageRef = storage.getReferenceFromUrl("gs://quanlykho-c05ef.appspot.com/");
    DatabaseReference mData;
    Bitmap bitmapCamera;
    NhanVienFirebase nhanVienFirebase;
    String urlImage = "";

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_them_nhan_vien);
        addControls();
        initFirebase();
        addEvents();

    }

    private void initFirebase() {
        mData= FirebaseDatabase.getInstance().getReference();
    }

    private void addEvents() {
        btnLuu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtTen.getText()!=null && edtDiaChi.getText()!=null && edtEmail.getText()!=null && edtGioiTinh.getText()!=null
                && edtPassword.getText()!=null && edtUserName.getText()!=null && edtSoDienThoai.getText()!=null){
                    if(bitmapCamera!=null){
                        String username= String.valueOf(edtUserName.getText());
                        LayChiTietNhanVienTheoUserNameTask task= new LayChiTietNhanVienTheoUserNameTask();
                        task.execute(username);
                    }
                    else
                        Toast.makeText(ThemNhanVienActivity.this,"Vui lòng thêm ảnh",Toast.LENGTH_LONG).show();
                }
                else
                    Toast.makeText(ThemNhanVienActivity.this,"Vui lòng nhập đầy đủ thông tin",Toast.LENGTH_LONG).show();

            }
        });
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(ThemNhanVienActivity.this,NhanSuActivity.class);
                startActivity(intent);
                finish();
            }
        });
        avatarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xuLyDoi();
            }
        });
    }

    private void xuLyLuu() {
        String tenNV= String.valueOf(edtTen.getText());
        String email= String.valueOf(edtEmail.getText());
        int phone= Integer.parseInt(edtSoDienThoai.getText().toString());
        String username= String.valueOf(edtUserName.getText());
        String password= String.valueOf(edtPassword.getText());
        String diachi= String.valueOf(edtDiaChi.getText());

        boolean gt=true;
        int role=0;
        String gtNhapVao=edtGioiTinh.getText().toString().toUpperCase();
        if(gtNhapVao.equals("NAM")==false) {
            gt=false;
        }
        if(spinner_ChucVu.getSelectedItemPosition()==1){
            role=1;
        }

        nhanVienFirebase= new NhanVienFirebase(0,tenNV,email,username,password,null);

        String params="?tenNV="+ URLEncoder.encode(tenNV)+"&gioiTinh="+gt+"&diaChi="+URLEncoder.encode(diachi)+"&phone="+phone
                +"&email="+URLEncoder.encode(email)+"&userName="+URLEncoder.encode(username)+"&password="+URLEncoder.encode(password)+"&role="+role;
        ThemNhanVienTask themNhanVienTask= new ThemNhanVienTask();
        themNhanVienTask.execute(params);
    }

    private void addControls() {

        edtTen=findViewById(R.id.edtTenNVThem);
        edtGioiTinh=findViewById(R.id.edtGioiTinhThem);
        edtSoDienThoai=findViewById(R.id.edtSoDienThoaiThem);
        edtEmail=findViewById(R.id.edtEmailThem);
        edtDiaChi=findViewById(R.id.edtDiaChiThem);
        edtUserName=findViewById(R.id.edtUserNameThem);
        edtPassword=findViewById(R.id.edtPasswordThem);

        spinner_ChucVu= findViewById(R.id.spinner_ChucVuThem);
        chucVuAdapter= new ArrayAdapter<>(ThemNhanVienActivity.this,android.R.layout.simple_spinner_item);
        chucVuAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayList<String> dsChucVu= new ArrayList<>();
        dsChucVu.add("Quản lý");
        dsChucVu.add("Nhân viên");
        chucVuAdapter.addAll(dsChucVu);
        spinner_ChucVu.setAdapter(chucVuAdapter);

        btnLuu=findViewById(R.id.btnLuuNhanVienMoi);
        iv_back=findViewById(R.id.iv_backThemNhanVien);

        avatarView=findViewById(R.id.avatar);
        imageLoader= new PicassoLoader();
        imageLoader.loadImage(avatarView,"https://raw.githubusercontent.com/quoccuong151197/FirebaseStorage/master/app/src/main/res/drawable/ic.png","Image");
    }
    class LayChiTietNhanVienTheoUserNameTask extends AsyncTask<String,Void, NhanVien> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(NhanVien nhanVien) {
            super.onPostExecute(nhanVien);
            if(nhanVien!=null){
               Toast.makeText(ThemNhanVienActivity.this,"Username đã tồn tại, vui lòng thay đổi UserName",Toast.LENGTH_LONG).show();
            }
            else {
                xuLyLuu();
            }
        }
        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected NhanVien doInBackground(String... strings) {
            try{
                URL url= new URL(Constant.IP_ADDRESS+"NhanVien/?userName="+strings[0]);
                HttpURLConnection connection= (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type","application/json;charset=UTF-8");

                InputStreamReader isr= new InputStreamReader(connection.getInputStream(),"UTF-8");
                BufferedReader br=new BufferedReader(isr);
                StringBuilder builder= new StringBuilder();
                String line=null;
                while ((line=br.readLine())!=null){
                    builder.append(line);
                }
                JSONArray jsonArray=new JSONArray(builder.toString());
                for(int i=0;i<=jsonArray.length();i++){
                    JSONObject object= jsonArray.getJSONObject(i);
                    if(object==null){
                    }
                    String password=object.getString("Password");
                    int ma=object.getInt("MaNhanVien");
                    String ten=object.getString("TenNhanVien");
                    boolean gioiTinh=object.getBoolean("GioiTinh");
                    String diaChi=object.getString("DiaChi");
                    int phone=object.getInt("Phone");
                    String email=object.getString("Email");
                    int role=object.getInt("Role");
                    String username=object.getString("UserName");
                    NhanVien nv= new NhanVien();
                    nv.setMaNhanVien(ma);
                    nv.setTenNhanVien(ten);
                    if(gioiTinh==true){
                        nv.setGioiTinh(0);
                    }
                    else if(gioiTinh==false){
                        nv.setGioiTinh(1);
                    }
                    nv.setDiaChi(diaChi);
                    nv.setPhone(phone);
                    nv.setEmail(email);
                    nv.setUserName(username);
                    nv.setPassword(password);
                    nv.setRole(role);
                    return nv;
                }
            }
            catch (Exception ex){
                Log.e("LOI",ex.toString());
            }
            return null;
        }
    }
    class ThemNhanVienTask extends AsyncTask<String,Void, Boolean>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean == true) {
                Toast.makeText(ThemNhanVienActivity.this, "Lưu thành công", Toast.LENGTH_LONG).show();
                xuLyLuuAvata();
            }
            else if (aBoolean==false){
                Toast.makeText(ThemNhanVienActivity.this, "Lưu thất bại", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try{
                String params=strings[0];
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

    private void xuLyLuuAvata() {
        xuLyUpload();
    }

    private void xuLyDoi() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ThemNhanVienActivity.this);
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
        }
        else if (requestCode == REQUEST_CODE_IMAGE_STORAGE && resultCode == RESULT_OK && data != null) {
            Uri uri=data.getData();
            String path=getRealPathFromURI(uri);
            bitmapCamera=getThumbnail(path);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void xuLyUpload() {
        progressDialog= new ProgressDialog(ThemNhanVienActivity.this);
        progressDialog.setTitle("Đang xử lý");
        progressDialog.setMessage("Vui lòng chờ...");
        progressDialog.show();
        String child = nhanVienFirebase.getUserName();
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
                    nhanVienFirebase.setUrlImage(urlImage);
                    mData.child("NhanVien").push().setValue(nhanVienFirebase, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                //Toast.makeText(ChiTietNhanSu.this, "Lưu databse Thành công", Toast.LENGTH_SHORT).show();
                                imageLoader.loadImage(avatarView, downloadUri.toString(), nhanVienFirebase.getTenNhanVien());
                                progressDialog.dismiss();
                                android.app.AlertDialog.Builder alertDialog= new android.app.AlertDialog.Builder(ThemNhanVienActivity.this);
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
