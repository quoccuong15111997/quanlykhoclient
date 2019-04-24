package com.example.firebase;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.conts.Constant;
import com.example.model.NhanVien;
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
        LayDanhSachNhanVienTask layDanhSachNhanVienTask= new LayDanhSachNhanVienTask();
        layDanhSachNhanVienTask.execute();
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
                    nhanVienFirebase.setUrlImage("abc");

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
}
