package com.example.quanlykho;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.ArrayList;

public class SuaNhanVienActivity extends AppCompatActivity {
    EditText edtTen, edtGioiTinh, edtSoDienThoai, edtEmail, edtDiaChi, edtUserName;
    Button btnLuu;
    ImageView iv_back;
    Intent intent;
    NhanVien nhanVienSua;
    RadioGroup groupChucVu;
    RadioButton radQuanLy, radNhanVien;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sua_nhan_vien);
        addControls();
        addEvent();
    }

    private void addEvent() {
        btnLuu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtTen.getText()!=null && edtDiaChi.getText()!=null && edtEmail.getText()!=null && edtGioiTinh.getText()!=null
                        && edtUserName.getText()!=null && edtSoDienThoai.getText()!=null){
                    String username= String.valueOf(edtUserName.getText());
                    if(username.equals(nhanVienSua.getUserName())==true){
                        xuLyLuu();
                    }
                    else if(username.equals(nhanVienSua.getUserName())==false){
                        LayChiTietNhanVienTheoUserNameTask task= new LayChiTietNhanVienTheoUserNameTask();
                        task.execute(username);
                    }
                }
                else
                    Toast.makeText(SuaNhanVienActivity.this,"Vui lòng nhập đầy đủ thông tin",Toast.LENGTH_LONG).show();

            }
        });
        radNhanVien.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                radQuanLy.setChecked(false);
                nhanVienSua.setRole(0);
            }
        });
        radQuanLy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                radNhanVien.setChecked(false);
                nhanVienSua.setRole(1);
            }
        });
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SuaNhanVienActivity.this,NhanSuActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void addControls() {
        intent=getIntent();
        nhanVienSua= (NhanVien) intent.getSerializableExtra("NHANVIEN");
        edtTen=findViewById(R.id.edtTenNVThem);
        edtGioiTinh=findViewById(R.id.edtGioiTinhThem);
        edtSoDienThoai=findViewById(R.id.edtSoDienThoaiThem);
        edtEmail=findViewById(R.id.edtEmailThem);
        edtDiaChi=findViewById(R.id.edtDiaChiThem);
        edtUserName=findViewById(R.id.edtUserNameThem);

        groupChucVu=findViewById(R.id.groupChucVu);
        radNhanVien=findViewById(R.id.radNhanVien);
        radQuanLy=findViewById(R.id.radQuanLy);

        edtTen.setText(nhanVienSua.getTenNhanVien());
        edtSoDienThoai.setText("0"+nhanVienSua.getPhone());
        edtEmail.setText(nhanVienSua.getEmail());
        edtDiaChi.setText(nhanVienSua.getDiaChi());
        edtUserName.setText(nhanVienSua.getUserName());
        if(nhanVienSua.getGioiTinh()==0){
            edtGioiTinh.setText("Nam");
        }
        else if(nhanVienSua.getGioiTinh()==1){
            edtGioiTinh.setText("Nữ");
        }

        if(nhanVienSua.getRole()==0){
            groupChucVu.check(R.id.radQuanLy);
        }
        else if(nhanVienSua.getRole()==1){
            groupChucVu.check(R.id.radNhanVien);

        }

        btnLuu=findViewById(R.id.btnLuuNhanVienMoi);
        iv_back=findViewById(R.id.iv_backThemNhanVien);
    }
    private void xuLyLuu() {
        String tenNV= String.valueOf(edtTen.getText());
        String email= String.valueOf(edtEmail.getText());
        int phone= Integer.parseInt(edtSoDienThoai.getText().toString());
        String username= String.valueOf(edtUserName.getText());
        String diachi= String.valueOf(edtDiaChi.getText());

        int gt=0;
        String gtNhapVao=edtGioiTinh.getText().toString().toUpperCase();
        if(gtNhapVao.equals("NAM")==false) {
            gt=1;
        }
        if(radNhanVien.isChecked()){
            nhanVienSua.setRole(1);
        }
        else if(radQuanLy.isChecked()){
            nhanVienSua.setRole(0);
        }
        nhanVienSua.setTenNhanVien(tenNV);
        nhanVienSua.setEmail(email);
        nhanVienSua.setPhone(phone);
        nhanVienSua.setUserName(username);
        nhanVienSua.setDiaChi(diachi);
        nhanVienSua.setGioiTinh(gt);
        SuaNhanVienTask task= new SuaNhanVienTask();
        task.execute(nhanVienSua);
    }
    class LayChiTietNhanVienTheoUserNameTask extends AsyncTask<String,Void,ArrayList<NhanVien>>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<NhanVien> nhanViens) {
            super.onPostExecute(nhanViens);
            if(nhanViens.size()==1){
                xuLyLuu();
            }
            else if(nhanViens.size()!=1){
                Toast.makeText(SuaNhanVienActivity.this,"Username đã tồn tại",Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected ArrayList<NhanVien> doInBackground(String... strings) {
            ArrayList<NhanVien> dsNhanVien= new ArrayList<>();
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
                    dsNhanVien.add(nv);
                }
            }
            catch (Exception ex){
                Log.e("LOI",ex.toString());
            }
            return dsNhanVien;
        }
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
                Toast.makeText(SuaNhanVienActivity.this, "Sửa thành công", Toast.LENGTH_LONG).show();
            }
            else if (aBoolean==false){
                Toast.makeText(SuaNhanVienActivity.this, "Sửa thất bại", Toast.LENGTH_LONG).show();
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
                String params="?maNVSua="+nv.getMaNhanVien()+"&tenNV="+ URLEncoder.encode(nv.getTenNhanVien())+"&gioiTinh="+gt+"&diaChi="+URLEncoder.encode(nv.getDiaChi())+"&phone="+nv.getPhone()
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
