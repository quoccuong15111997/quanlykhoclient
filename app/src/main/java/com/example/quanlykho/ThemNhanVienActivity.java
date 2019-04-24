package com.example.quanlykho;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
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
import com.example.model.NhanVien;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class ThemNhanVienActivity extends AppCompatActivity {
    EditText edtTen, edtGioiTinh, edtSoDienThoai, edtEmail, edtDiaChi, edtUserName, edtPassword;
    Spinner spinner_ChucVu;
    ArrayAdapter<String> chucVuAdapter;
    Button btnLuu;
    ImageView iv_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_them_nhan_vien);
        addControls();
        addEvents();
    }

    private void addEvents() {
        btnLuu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtTen.getText()!=null && edtDiaChi.getText()!=null && edtEmail.getText()!=null && edtGioiTinh.getText()!=null
                && edtPassword.getText()!=null && edtUserName.getText()!=null && edtSoDienThoai.getText()!=null){
                    String username= String.valueOf(edtUserName.getText());
                    LayChiTietNhanVienTheoUserNameTask task= new LayChiTietNhanVienTheoUserNameTask();
                    task.execute(username);
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
}
