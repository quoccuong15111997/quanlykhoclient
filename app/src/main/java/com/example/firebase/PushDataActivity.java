package com.example.firebase;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.conts.Constant;
import com.example.model.NhanVien;
import com.example.model.SanPham;
import com.example.quanlykho.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class PushDataActivity extends AppCompatActivity {
    DatabaseReference mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_data);
        firebaseData();
        /*LayDanhSachNhanVienTask layDanhSachNhanVienTask= new LayDanhSachNhanVienTask();
        layDanhSachNhanVienTask.execute();*/

        LayDanhSachSanPhamTask layDanhSachSanPhamTask= new LayDanhSachSanPhamTask();
        layDanhSachSanPhamTask.execute();
    }

    private void firebaseData() {
        mData = FirebaseDatabase.getInstance().getReference();
    }
    public class LayDanhSachNhanVienTask extends AsyncTask<Void, Void, ArrayList<NhanVien>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<NhanVien> nhanViens) {
            super.onPostExecute(nhanViens);
            if (nhanViens != null) {
                for(NhanVien nhanVien : nhanViens){
                    NhanVienFirebase nhanVienFirebase= new NhanVienFirebase();
                    nhanVienFirebase.setMaNhanVien(nhanVien.getMaNhanVien());
                    nhanVienFirebase.setTenNhanVien(nhanVien.getTenNhanVien());
                    nhanVienFirebase.setEmail(nhanVien.getEmail());

                    nhanVienFirebase.setUserName(nhanVien.getUserName());
                    nhanVienFirebase.setPassword(nhanVien.getPassword());
                    nhanVienFirebase.setUrlImage("https://firebasestorage.googleapis.com/v0/b/quanlykho-c05ef.appspot.com/o/thuyhuynhchodien232.png?alt=media&token=918ebae4-8e53-4622-a911-d4ea459a7106");

                    mData.child("NhanVien").push().setValue(nhanVienFirebase);
                }
            } else
                Toast.makeText(PushDataActivity.this, "Không tìm thấy nhân viên", Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected ArrayList<NhanVien> doInBackground(Void... voids) {
            ArrayList<NhanVien> dsNhanVien = new ArrayList<>();
            try {
                URL url = new URL(Constant.IP_ADDRESS+"NhanVien");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");

                InputStreamReader isr = new InputStreamReader(connection.getInputStream(), "UTF-8");
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
                        Toast.makeText(PushDataActivity.this, "Không tìm thấy nhân viên", Toast.LENGTH_LONG).show();
                    }
                    String ten = object.getString("TenNhanVien");
                    int ma = object.getInt("MaNhanVien");
                    int phone = object.getInt("Phone");
                    String email = object.getString("Email");
                    int role = object.getInt("Role");
                    boolean gt=object.getBoolean("GioiTinh");
                    String diaChi=object.getString("DiaChi");
                    String username=object.getString("UserName");
                    String password=object.getString("Password");

                    NhanVien nv = new NhanVien();
                    nv.setMaNhanVien(ma);
                    nv.setTenNhanVien(ten);
                    nv.setPhone(phone);
                    nv.setEmail(email);
                    nv.setRole(role);
                    nv.setDiaChi(diaChi);
                    nv.setUserName(username);
                    nv.setPassword(password);
                    if (gt == true) {
                        nv.setGioiTinh(0);
                    }
                    else nv.setGioiTinh(1);

                    dsNhanVien.add(nv);
                }
                return dsNhanVien;
            } catch (Exception ex) {
                Log.e("LOI", ex.toString());
            }
            return null;
        }
    }
    //san pham
    class LayDanhSachSanPhamTask extends AsyncTask<Void, Void, ArrayList<SanPham>>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<SanPham> sanPhams) {
            super.onPostExecute(sanPhams);
            for(SanPham sanPham : sanPhams){
                SanPhamFirebase sanPhamFirebase= new SanPhamFirebase();
                sanPhamFirebase.setMaSanPham(sanPham.getMaSanPham());
                sanPhamFirebase.setTenSanPham(sanPham.getTenSanPham());
                sanPhamFirebase.setDonGia(sanPham.getDonGia());
                sanPhamFirebase.setMaDanhMuc(sanPham.getMaDanhMuc());
                sanPhamFirebase.setSoLuong(sanPham.getSoLuong());
                sanPhamFirebase.setSize(sanPham.getSize());
                sanPhamFirebase.setUrlImage("https://firebasestorage.googleapis.com/v0/b/quanlykho-c05ef.appspot.com/o/adidas-pod-s3.1-den-full.jpg?alt=media&token=b5bdee6f-d33a-4ff8-bb6e-00b45e62e3d1");
                mData.child("SanPham").push().setValue(sanPhamFirebase);
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected ArrayList<SanPham> doInBackground(Void... voids) {
            ArrayList<SanPham> dsSp= new ArrayList<>();
            try{
                URL url= new URL(Constant.IP_ADDRESS+"SanPham");
                HttpURLConnection connection= (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type","application/json;charset=UTF-8");

                InputStreamReader isr = new InputStreamReader(connection.getInputStream(), "UTF-8");
                BufferedReader br = new BufferedReader(isr);
                StringBuilder builder = new StringBuilder();
                String line = null;
                while ((line = br.readLine()) != null) {
                    builder.append(line);
                }
                JSONArray jsonArray=new JSONArray(builder.toString());
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject object= jsonArray.getJSONObject(i);
                    if(object==null){
                        Toast.makeText(PushDataActivity.this,"không tìm thấy sản phẩm",Toast.LENGTH_LONG).show();
                    }
                    else if(object!=null){
                        int ma=object.getInt("Ma");
                        String ten=object.getString("Ten");
                        int donGia=object.getInt("DonGia");
                        int soLuong=object.getInt("SoLuong");
                        boolean tinhTrang=object.getBoolean("TinhTrang");
                        int size=object.getInt("Size");

                        SanPham sp= new SanPham();
                        sp.setMaSanPham(ma);
                        sp.setTenSanPham(ten);
                        sp.setDonGia(donGia);
                        sp.setSoLuong(soLuong);
                        sp.setTinhTrang(tinhTrang);
                        sp.setSize(size);

                        dsSp.add(sp);
                    }
                }
                return dsSp;
            }
            catch (Exception ex){
                Log.e("LOI",ex.toString());
            }
            return null;
        }
    }
}
