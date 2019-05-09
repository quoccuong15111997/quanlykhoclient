package com.example.quanlykho;

import android.app.Activity;
import android.app.Dialog;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.conts.Constant;
import com.example.firebase.SanPhamFirebase;
import com.example.model.DanhMuc;
import com.example.model.SanPham;
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
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class SanPhamNangCaoActivity extends AppCompatActivity {
    Intent intent;
    SanPham sanPham;
    ImageView iv_back;
    EditText et_Ma, et_Loai, et_Ten, et_Gia, et_SoLuong, et_Size;
    Button bt_Sua, bt_Xoa, bt_luu;
    private Dialog dialog;
    ImageView imgSanPham;
    TextView txtTenSanPham;
    String urlImage = "";

    //firebase
    DatabaseReference mData = FirebaseDatabase.getInstance().getReference();
    FirebaseStorage storage = FirebaseStorage.getInstance();

    int REQUEST_CODE_IMAGE = 1;
    int REQUEST_CODE_IMAGE_STORAGE = 2;
    StorageReference storageRef = storage.getReferenceFromUrl("gs://quanlykho-c05ef.appspot.com/");
    ProgressDialog progressDialog;
    SanPhamFirebase sanPhamFirebase;

    Bitmap bitmapCamera;
    String KEY = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_san_pham_nang_cao);
        addControls();
        initFirebase();
        addevents();
    }

    private void addevents() {
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result",1);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });
        bt_Xoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xuLyXoa();
            }
        });
        bt_Sua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xuLySua();
            }
        });
        bt_luu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xuLyLuu();
            }
        });
        imgSanPham.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xuLyDoi();
            }
        });
    }

    private void xuLyDoi() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SanPhamNangCaoActivity.this);
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

    private void xuLyLuu() {
        SanPham sanPham= new SanPham();
        sanPham.setMaSanPham(Integer.parseInt(et_Ma.getText().toString()));
        sanPham.setTenSanPham(et_Ten.getText().toString());
        sanPham.setSoLuong(Integer.parseInt(et_SoLuong.getText().toString()));
        sanPham.setDonGia(Integer.parseInt(et_Gia.getText().toString()));
        sanPham.setSize(Integer.parseInt(et_Size.getText().toString()));

        SuaSanPhamTask suaSanPhamTask= new SuaSanPhamTask();
        suaSanPhamTask.execute(sanPham);
    }

    private void xuLySua() {
        et_Ten.setEnabled(true);
        et_Gia.setEnabled(true);
        et_SoLuong.setEnabled(true);
        et_Size.setEnabled(true);
    }

    private void xuLyXoa() {
        dialog = new Dialog(SanPhamNangCaoActivity.this);
        dialog.setTitle("Xác nhận xóa");
        dialog.setContentView(R.layout.dialog_confirm);
        dialog.show();
    }
    public void XacNhanXoa(View view) {
        dialog.dismiss();
        XoaSanPhamTask task= new XoaSanPhamTask();
        task.execute(Integer.parseInt(et_Ma.getText().toString()));
    }

    public void XacNhanHuy(View view) {
        dialog.dismiss();
    }

    private void addControls() {
        intent=getIntent();
        sanPham= (SanPham) intent.getSerializableExtra("SANPHAM");

        iv_back=findViewById(R.id.iv_backChiTiet);

        et_Ma=findViewById(R.id.edtMaSpChiTiet);
        et_Loai=findViewById(R.id.edtLoaiSpChiTiet);
        et_Ten=findViewById(R.id.edtTenSpChiTiet);
        et_Gia=findViewById(R.id.edtDonGiaSpChiTiet);
        et_SoLuong=findViewById(R.id.edtSoLuongSpChiTiet);
        et_Size=findViewById(R.id.edtSizeSpChiTiet);

        bt_luu=findViewById(R.id.btnLuuChiTiet);
        bt_Sua=findViewById(R.id.btnSuaChiTiet);
        bt_Xoa=findViewById(R.id.btnXoaChiTiet);

        et_Ma.setText(sanPham.getMaSanPham()+"");
        et_Ten.setText(sanPham.getTenSanPham());
        et_Gia.setText(sanPham.getDonGia()+"");
        et_SoLuong.setText(sanPham.getSoLuong()+"");
        et_Size.setText(sanPham.getSize()+"");

        et_Ma.setEnabled(false);
        et_Ten.setEnabled(false);
        et_Gia.setEnabled(false);
        et_SoLuong.setEnabled(false);
        et_Size.setEnabled(false);
        et_Loai.setEnabled(false);


        ArrayList<String> dstinhTrang= new ArrayList<>();
        dstinhTrang.add("Còn hàng");
        dstinhTrang.add("Hết hàng");

        TimDanhMucTheoMaTask task= new TimDanhMucTheoMaTask();
        task.execute(sanPham.getMaDanhMuc());

        imgSanPham=findViewById(R.id.imgSanPham);
        txtTenSanPham=findViewById(R.id.txtTen);
        txtTenSanPham.setText(sanPham.getTenSanPham());

    }
    class TimDanhMucTheoMaTask extends AsyncTask<Integer,Void, DanhMuc> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(DanhMuc danhMuc) {
            super.onPostExecute(danhMuc);
            if(danhMuc!=null){
                et_Loai.setText(danhMuc.getTenDanhMuc().toString());
            }
            else
                Toast.makeText(SanPhamNangCaoActivity.this,"Không tìm thấy danh muc",Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected DanhMuc doInBackground(Integer... integers) {
            try{
                URL url= new URL(Constant.IP_ADDRESS+"DanhMuc/"+integers[0]);
                HttpURLConnection connection= (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type","application/json;charset=UTF-8");

                InputStreamReader isr= new InputStreamReader(connection.getInputStream(),"UTF-8");
                BufferedReader br= new BufferedReader(isr);
                StringBuilder builder= new StringBuilder();
                String line=null;
                while ((line=br.readLine())!=null){
                    builder.append(line);
                }
                JSONObject object=new JSONObject(builder.toString());
                int maDm=object.getInt("MaDanhMuc");
                String tenDm=object.getString("TenDanhMuc");
                DanhMuc danhMuc= new DanhMuc();
                danhMuc.setTenDanhMuc(tenDm);
                danhMuc.setMaDanhMuc(maDm);

                return danhMuc;
            }
            catch (Exception ex){
                Log.e("LOI",ex.toString());
            }
            return null;
        }
    }
    class XoaSanPhamTask extends AsyncTask<Integer,Void,Boolean>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(aBoolean==true){
                Toast.makeText(SanPhamNangCaoActivity.this, "Xóa thành công", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(SanPhamNangCaoActivity.this, "Xóa thất bại, vui lòng kiểm tra lại", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Boolean doInBackground(Integer... integers) {
            try{
                URL url= new URL(Constant.IP_ADDRESS+"SanPham/?maSp="+integers[0]);
                HttpURLConnection connection= (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type","application/json;charset=UTF-8");

                InputStreamReader isr= new InputStreamReader(connection.getInputStream(),"UTF-8");
                BufferedReader br= new BufferedReader(isr);
                StringBuilder builder= new StringBuilder();
                String line=null;
                while ((line=br.readLine())!=null){
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
    class SuaSanPhamTask extends AsyncTask<SanPham,Void,Boolean>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(aBoolean==true){
                Toast.makeText(SanPhamNangCaoActivity.this, "Lưu thành công", Toast.LENGTH_LONG).show();
               if(bitmapCamera!=null){
                   xuLyUpload();
               }
            }
            else {
                Toast.makeText(SanPhamNangCaoActivity.this, "Lưu thất bại", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Boolean doInBackground(SanPham... sanPhams) {
            try{
                int masp=sanPhams[0].getMaSanPham();
                String tensp=sanPhams[0].getTenSanPham();
                int gia=sanPhams[0].getDonGia();
                int soluong=sanPhams[0].getSoLuong();
                boolean tinhtrang=sanPhams[0].isTinhTrang();
                int size=sanPhams[0].getSize();
                int madm=sanPhams[0].getMaDanhMuc();

                String params="maSpSua="+masp +
                        "&tenSp="+ URLEncoder.encode(tensp) +
                        "&giaSp="+gia+
                        "&soLuong="+soluong +
                        "&tinhTrang="+tinhtrang +
                        "&size="+size;
                URL url= new URL(Constant.IP_ADDRESS+"SanPham/?"+params);
                HttpURLConnection connection= (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type","application/json;charset=UTF-8");

                InputStreamReader isr= new InputStreamReader(connection.getInputStream(),"UTF-8");
                BufferedReader br= new BufferedReader(isr);
                StringBuilder builder= new StringBuilder();
                String line=null;
                while ((line=br.readLine())!=null){
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
        progressDialog= new ProgressDialog(SanPhamNangCaoActivity.this);
        progressDialog.setTitle("Đang xử lý");
        progressDialog.setMessage("Vui lòng chờ...");
        progressDialog.show();
        String child = sanPham.getMaSanPham()+sanPham.getTenSanPham();
        final StorageReference mountainsRef = storageRef.child(child);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmapCamera.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mountainsRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                //Toast.makeText(SanPhamNangCaoActivity.this, "Thất bại", Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //Toast.makeText(SanPhamNangCaoActivity.this, "Thành công", Toast.LENGTH_LONG).show();
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
                    sanPhamFirebase.setUrlImage(urlImage);
                    String key=KEY;
                    mData.child("SanPham").child(key).child("urlImage").setValue((downloadUri.toString()), new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                //Toast.makeText(SanPhamNangCaoActivity.this, "Lưu databse Thành công", Toast.LENGTH_SHORT).show();
                                Picasso.with(SanPhamNangCaoActivity.this).load(urlImage).into(imgSanPham);
                                progressDialog.dismiss();
                                android.app.AlertDialog.Builder alertDialog= new android.app.AlertDialog.Builder(SanPhamNangCaoActivity.this);
                                alertDialog.setTitle("Lưu thành công");
                                alertDialog.setIcon(R.drawable.ic_ok);
                                alertDialog.setNegativeButton("OK",new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                }).show();
                            } else {
                                 //Toast.makeText(SanPhamNangCaoActivity.this, "Lưu dadabase thất bại", Toast.LENGTH_LONG).show();
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
    private void initFirebase() {
        mData.child("SanPham").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                sanPhamFirebase = dataSnapshot.getValue(SanPhamFirebase.class);
                if (sanPhamFirebase.getTenSanPham().equals(sanPham.getTenSanPham())) {
                    KEY=dataSnapshot.getKey();
                    urlImage = sanPhamFirebase.getUrlImage();
                    Picasso.with(SanPhamNangCaoActivity.this).load(urlImage).into(imgSanPham);
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
}
