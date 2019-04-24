package com.example.quanlykho;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.conts.Constant;
import com.example.model.NhanVien;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class TaiKhoanActivity extends AppCompatActivity {
    NhanVien nhanVienLogin;
    EditText et_Ma, et_Ten, et_DiaChi, et_Sdt, et_Email, et_ChucVu, et_User, et_GioiTinh;
    Button bt_Sua, bt_Luu, bt_DoiMK;
    ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tai_khoan);
        addControls();
        addEvents();
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
