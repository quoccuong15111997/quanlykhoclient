package com.example.quanlykho;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Random;

public class ThemSanPhamActivity extends AppCompatActivity {
    ImageView iv_back;
    EditText et_Ma, et_Ten, et_Gia, et_SoLuong, et_Size;
    Button bt_luu;
    private Dialog dialog;
    ImageView imgSanPham;
    TextView txtTenSanPham;
    String urlImage = "";
    Spinner spinner_danhMuc;
    ArrayAdapter<DanhMuc> danhMucAdapter;
    SanPham sp;

    //firebase
    DatabaseReference mData;
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
        setContentView(R.layout.activity_them_san_pham);
        addControls();
        addEvents();
    }
    private void addEvents() {
        bt_luu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xuLyThemSanPham();
            }
        });
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result",100);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });
        imgSanPham.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xuLyDoi();
            }
        });
    }
    private void initFirebase() {
        mData.child("SanPham").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                sanPhamFirebase = dataSnapshot.getValue(SanPhamFirebase.class);
                if (sanPhamFirebase.getTenSanPham().equals(sp.getTenSanPham())) {
                    urlImage = sanPhamFirebase.getUrlImage();
                    KEY = dataSnapshot.getKey();
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
    private void xuLyDoi() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(ThemSanPhamActivity.this);
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

    private void addControls() {
        iv_back=findViewById(R.id.iv_backChiTiet);

        et_Ma=findViewById(R.id.edtMaSpChiTiet);
        et_Ma.setEnabled(false);
        et_Ten=findViewById(R.id.edtTenSpChiTiet);
        et_Gia=findViewById(R.id.edtDonGiaSpChiTiet);
        et_SoLuong=findViewById(R.id.edtSoLuongSpChiTiet);
        et_Size=findViewById(R.id.edtSizeSpChiTiet);

        bt_luu=findViewById(R.id.btnLuuChiTiet);

        imgSanPham=findViewById(R.id.imgSanPham);
        txtTenSanPham=findViewById(R.id.txtTen);

        Picasso.with(ThemSanPhamActivity.this).load("https://raw.githubusercontent.com/quoccuong151197/FirebaseStorage/master/app/src/main/res/drawable/ic.png").into(imgSanPham);

        spinner_danhMuc=findViewById(R.id.spinner_ChungLoai);
        danhMucAdapter= new ArrayAdapter<>(ThemSanPhamActivity.this,android.R.layout.simple_spinner_item);
        danhMucAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_danhMuc.setAdapter(danhMucAdapter);

        LayDanhSachDanhMucTask task= new LayDanhSachDanhMucTask();
        task.execute();

        mData = FirebaseDatabase.getInstance().getReference();
    }

    private void xuLyThemSanPham() {
        Random rd= new Random();
        int ma=rd.nextInt(1000000);

        sp= new SanPham();
        sp.setMaSanPham(ma);
        sp.setTenSanPham(et_Ten.getText().toString());
        sp.setSize(Integer.parseInt(et_Size.getText().toString()));
        sp.setDonGia(Integer.parseInt(et_Gia.getText().toString()));
        sp.setSoLuong(Integer.parseInt(et_SoLuong.getText().toString()));
        sp.setTinhTrang(true);
        DanhMuc danhMuc=danhMucAdapter.getItem(spinner_danhMuc.getSelectedItemPosition());
        sp.setMaDanhMuc(danhMuc.getMaDanhMuc());
        LuuMoiSanPhamTask task= new LuuMoiSanPhamTask();
        task.execute(sp);
    }
    class LuuMoiSanPhamTask extends AsyncTask<SanPham,Void,Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(aBoolean==true){
                SanPhamFirebase sanPhamFirebase= new SanPhamFirebase();
                sanPhamFirebase.setMaSanPham(sp.getMaSanPham());
                sanPhamFirebase.setTenSanPham(sp.getTenSanPham());
                sanPhamFirebase.setDonGia(sp.getDonGia());
                sanPhamFirebase.setMaDanhMuc(sp.getMaDanhMuc());
                sanPhamFirebase.setSoLuong(sp.getSoLuong());
                sanPhamFirebase.setSize(sp.getSize());
                sanPhamFirebase.setUrlImage("https://raw.githubusercontent.com/quoccuong151197/FirebaseStorage/master/app/src/main/res/drawable/ic.png");
                mData.child("SanPham").push().setValue(sanPhamFirebase);

                initFirebase();

                if(bitmapCamera!=null){
                    xuLyUpload();
                }
                else {
                    AlertDialog.Builder alertDialog= new AlertDialog.Builder(ThemSanPhamActivity.this);
                    alertDialog.setTitle("Vui lòng chọn ảnh");
                    alertDialog.setIcon(R.drawable.ic_error);
                    alertDialog.setNegativeButton("OK",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).show();
                }


            }
            else{
                AlertDialog.Builder alertDialog= new AlertDialog.Builder(ThemSanPhamActivity.this);
                alertDialog.setTitle("Lưu thất bại, sản phẩm đã tồn tại");
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
        protected Boolean doInBackground(SanPham... sanPhams) {
            try{
                int masp=sanPhams[0].getMaSanPham();
                String tensp=sanPhams[0].getTenSanPham();
                int gia=sanPhams[0].getDonGia();
                int soluong=sanPhams[0].getSoLuong();
                boolean tinhtrang=sanPhams[0].isTinhTrang();
                int size=sanPhams[0].getSize();
                int madm=sanPhams[0].getMaDanhMuc();

                String params="maSp="+masp +
                        "&tenSp="+ URLEncoder.encode(tensp) +
                        "&giaSp="+gia +
                        "&maDm="+madm+
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
    class LayDanhSachDanhMucTask extends AsyncTask<Void, Void, ArrayList<DanhMuc>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<DanhMuc> danhMucs) {
            super.onPostExecute(danhMucs);
            if (danhMucs != null) {
                danhMucAdapter.clear();
                danhMucAdapter.addAll(danhMucs);
                danhMucAdapter.notifyDataSetChanged();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected ArrayList<DanhMuc> doInBackground(Void... voids) {
            ArrayList<DanhMuc> dsDanhMuc = new ArrayList<>();
            try {
                URL url = new URL(Constant.IP_ADDRESS+"DanhMuc");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");

                InputStreamReader isr = new InputStreamReader(connection.getInputStream());
                BufferedReader br = new BufferedReader(isr);
                StringBuilder builder = new StringBuilder();
                String line = null;
                while ((line = br.readLine()) != null) {
                    builder.append(line);
                }
                JSONArray jsonArray = new JSONArray(builder.toString());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    if (object == null) {
                        Toast.makeText(ThemSanPhamActivity.this, "không tìm thấy danh mục", Toast.LENGTH_LONG).show();
                    } else if (object != null) {
                        int ma = object.getInt("MaDanhMuc");
                        String ten = object.getString("TenDanhMuc");
                        DanhMuc dm = new DanhMuc();
                        dm.setTenDanhMuc(ten);
                        dm.setMaDanhMuc(ma);

                        dsDanhMuc.add(dm);
                    }
                }
            } catch (Exception ex) {
                Log.e("LOI", ex.toString());
            }
            return dsDanhMuc;
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
    private void xuLyUpload() {
        progressDialog= new ProgressDialog(ThemSanPhamActivity.this);
        progressDialog.setTitle("Đang xử lý");
        progressDialog.setMessage("Vui lòng chờ...");
        progressDialog.show();
        String child = sp.getMaSanPham()+sp.getTenSanPham();
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
                    mData.child("SanPham").child(KEY).child("urlImage").setValue((downloadUri.toString()), new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                //Toast.makeText(SanPhamNangCaoActivity.this, "Lưu databse Thành công", Toast.LENGTH_SHORT).show();
                                Picasso.with(ThemSanPhamActivity.this).load(urlImage).into(imgSanPham);
                                progressDialog.dismiss();
                                android.app.AlertDialog.Builder alertDialog= new android.app.AlertDialog.Builder(ThemSanPhamActivity.this);
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
}
