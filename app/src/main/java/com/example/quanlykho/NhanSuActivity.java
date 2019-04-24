package com.example.quanlykho;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adapter.NhanVienAdapter;
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

public class NhanSuActivity extends AppCompatActivity {
    ListView lv_NhanVien;
    ImageView iv_Back, iv_add;
    NhanVienAdapter nhanVienAdapter;
    ArrayList<NhanVien> dsNhanVien;
    LinearLayout linearLayout_tim;
    TextView txtTen, txtChucVu;
    Button btnDong,btnTim;
    EditText edtNhapVao;
    Spinner spinner_ChucVu;
    ArrayAdapter<String> chucVuAdapter;
    private Dialog dialog;
    boolean chucNangTim=true;
    int viTri=0;
    ProgressDialog progressDialog;
    AlertDialog.Builder alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nhan_su);
        addControls();
        addEvents();
    }
    private void addEvents() {
        iv_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        lv_NhanVien.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                viTri=position;
                registerForContextMenu(lv_NhanVien);
                return false;
            }
        });
        lv_NhanVien.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent= new Intent(NhanSuActivity.this,ChiTietNhanSu.class);
                NhanVien nhanVien=nhanVienAdapter.getItem(position);
                intent.putExtra("NHANVIEN",nhanVien);
                startActivity(intent);
            }
        });
        iv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xuLyThemNhanVien();
            }
        });
        txtTen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayout_tim.setVisibility(View.VISIBLE);
                edtNhapVao.setVisibility(View.VISIBLE);
                spinner_ChucVu.setVisibility(View.GONE);
                txtTen.setBackgroundResource(R.drawable.border_txt_select);
                txtChucVu.setBackgroundResource(R.drawable.border_edittext);
                txtTen.setTextColor(Color.WHITE);
                txtChucVu.setTextColor(Color.BLACK);
                chucNangTim=true;
            }
        });
        txtChucVu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                linearLayout_tim.setVisibility(View.VISIBLE);
                edtNhapVao.setVisibility(View.GONE);

                spinner_ChucVu.setVisibility(View.VISIBLE);
                txtChucVu.setBackgroundResource(R.drawable.border_txt_select);
                txtTen.setBackgroundResource(R.drawable.border_edittext);
                txtChucVu.setTextColor(Color.WHITE);
                txtTen.setTextColor(Color.BLACK);
                chucNangTim=false;
            }
        });
        btnTim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chucNangTim==true){
                    xuLyTimTheoTen();
                }
                else if(chucNangTim==false){
                    xuLyTimTheoChucVu();
                }
            }
        });
        btnDong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayout_tim.setVisibility(View.GONE);
                txtChucVu.setBackgroundResource(R.drawable.border_edittext);
                txtTen.setBackgroundResource(R.drawable.border_edittext);
                layDanhSachNhanVien();
            }
        });
    }

    private void xuLyTimTheoChucVu() {
        if(spinner_ChucVu.getSelectedItemPosition()==0){
            LayDanhSachNhanVienQuanLyTask layDanhSachNhanVienQuanLyTask= new LayDanhSachNhanVienQuanLyTask();
            layDanhSachNhanVienQuanLyTask.execute();
        }
        else  if(spinner_ChucVu.getSelectedItemPosition()==1){
            LayDanhSachNhanVienNhanVienTask vienTask= new LayDanhSachNhanVienNhanVienTask();
            vienTask.execute();
        }
    }

    private void xuLyTimTheoTen() {
        String ten= String.valueOf(edtNhapVao.getText());
        if(ten!=null){
            LayChiTietNhanVienTheoTenTask task= new LayChiTietNhanVienTheoTenTask();
            task.execute(URLEncoder.encode(ten));
        }
        else
            Toast.makeText(NhanSuActivity.this, "Vui lòng nhập thông tin cần tìm", Toast.LENGTH_LONG).show();
    }

    private void xuLyThemNhanVien() {
        Intent intent= new Intent(NhanSuActivity.this,ThemNhanVienActivity.class);
        startActivity(intent);
    }

    private void addControls() {
        dsNhanVien = new ArrayList<>();
        lv_NhanVien = findViewById(R.id.lv_NhanVien);
        nhanVienAdapter = new NhanVienAdapter(NhanSuActivity.this, R.layout.item_row_nhanvien, dsNhanVien);
        layDanhSachNhanVien();
        lv_NhanVien.setAdapter(nhanVienAdapter);
        iv_Back=findViewById(R.id.iv_backNhanSu);
        iv_add=findViewById(R.id.iv_addNhanSu);
        linearLayout_tim=findViewById(R.id.ll_seach);
        linearLayout_tim.setVisibility(View.GONE);
        txtChucVu=findViewById(R.id.txtChucVuNS);
        txtTen=findViewById(R.id.txtTenNS);
        btnDong=findViewById(R.id.btnDongNhanSu);
        btnTim=findViewById(R.id.btnTimNhanSu);
        edtNhapVao=findViewById(R.id.edtNhapVaoNhanSu);

        spinner_ChucVu= findViewById(R.id.spiner_ChucVuNhanSu);
        chucVuAdapter=new ArrayAdapter<>(NhanSuActivity.this,android.R.layout.simple_spinner_item);
        chucVuAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayList<String> dsChucVu= new ArrayList<>();
        dsChucVu.add("Quản lý");
        dsChucVu.add("Nhân viên");
        chucVuAdapter.addAll(dsChucVu);
        spinner_ChucVu.setAdapter(chucVuAdapter); }

    private void layDanhSachNhanVien() {
        LayDanhSachNhanVienTask task=new LayDanhSachNhanVienTask();
        task.execute();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater()
                .inflate(R.menu.context_menu, menu);
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.itemXoa:
                xuLyXoaNhanVien();
                break;
            case R.id.itemSua:
                xuLySuaNhanVien();
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void xuLySuaNhanVien() {
        NhanVien nv;
        nv=nhanVienAdapter.getItem(viTri);
        Intent intent= new Intent(NhanSuActivity.this,SuaNhanVienActivity.class);
        intent.putExtra("NHANVIEN",nv);
        startActivity(intent);
        finish();
    }

    private void xuLyXoaNhanVien() {
        dialog = new Dialog(NhanSuActivity.this);
        dialog.setTitle("Xác nhận xóa");
        dialog.setContentView(R.layout.dialog_confirm);
        dialog.show();
    }
    public void XacNhanXoa(View view) {
        dialog.dismiss();
        XoaNhanVienTask task= new XoaNhanVienTask();
        NhanVien nv;
        nv=nhanVienAdapter.getItem(viTri);

        task.execute(nv.getMaNhanVien());
    }

    public void XacNhanHuy(View view) {
        dialog.dismiss();
    }

    public class LayDanhSachNhanVienTask extends AsyncTask<Void, Void, ArrayList<NhanVien>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog= new ProgressDialog(NhanSuActivity.this);
            progressDialog.setTitle("Đang lấy danh sách nhân viên");
            progressDialog.setMessage("Vui lòng chờ");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(ArrayList<NhanVien> nhanViens) {
            super.onPostExecute(nhanViens);
            progressDialog.dismiss();
            if (nhanViens != null) {
                dsNhanVien.clear();
                dsNhanVien.addAll(nhanViens);
                nhanVienAdapter.notifyDataSetChanged();
            } else
                Toast.makeText(NhanSuActivity.this, "Không tìm thấy nhân viên", Toast.LENGTH_LONG).show();
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
                        Toast.makeText(NhanSuActivity.this, "Không tìm thấy nhân viên", Toast.LENGTH_LONG).show();
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
    class XoaNhanVienTask extends AsyncTask<Integer, Void, Boolean>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog= new ProgressDialog(NhanSuActivity.this);
            progressDialog.setTitle("Đang xóa nhân viên");
            progressDialog.setMessage("Vui lòng chờ");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            progressDialog.dismiss();
            if (aBoolean == true) {
                alertDialog= new AlertDialog.Builder(NhanSuActivity.this);
                alertDialog.setTitle("Xóa thành công");
                alertDialog.setIcon(R.drawable.ic_ok);
                alertDialog.setNegativeButton("OK",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
                nhanVienAdapter.clear();
                layDanhSachNhanVien();
            }
            else if (aBoolean==false){
                Toast.makeText(NhanSuActivity.this, "Xóa thất bại", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Boolean doInBackground(Integer... integers) {
            try{
                URL url= new URL(Constant.IP_ADDRESS+"NhanVien/?maNVXoa="+integers[0]);
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
    class LayChiTietNhanVienTheoTenTask extends AsyncTask<String,Void, NhanVien> {
        @Override
        protected void onPreExecute() {
            progressDialog= new ProgressDialog(NhanSuActivity.this);
            progressDialog.setTitle("Đang tìm nhân viên");
            progressDialog.setMessage("Vui lòng chờ");
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(NhanVien nhanVien) {
            super.onPostExecute(nhanVien);
            progressDialog.dismiss();
            if(nhanVien!=null){
                dsNhanVien.clear();
                dsNhanVien.add(nhanVien);
                nhanVienAdapter.notifyDataSetChanged();
            }
            else{
                Toast.makeText(NhanSuActivity.this,"Không tìm thấy nhân viên",Toast.LENGTH_LONG).show();
            }
        }
        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected NhanVien doInBackground(String... strings) {
            try{
                URL url= new URL(Constant.IP_ADDRESS+"NhanVien/?ten="+strings[0]);
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
                    JSONObject object= new JSONObject(builder.toString());
                    if(object==null){
                        Toast.makeText(NhanSuActivity.this,"Không tìm thấy nhân viên",Toast.LENGTH_LONG).show();
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
            catch (Exception ex){
                Log.e("LOI",ex.toString());
            }
            return null;
        }
    }
    public class LayDanhSachNhanVienQuanLyTask extends AsyncTask<Void, Void, ArrayList<NhanVien>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog= new ProgressDialog(NhanSuActivity.this);
            progressDialog.setTitle("Đang tìm nhân viên");
            progressDialog.setMessage("Vui lòng chờ");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(ArrayList<NhanVien> nhanViens) {
            super.onPostExecute(nhanViens);
            progressDialog.dismiss();
            ArrayList<NhanVien>dsNhanVienTheoChucVu= new ArrayList<>();
            if(nhanViens!=null){
                for(NhanVien nv : nhanViens){
                    if(nv.getRole()==0) {
                        dsNhanVienTheoChucVu.add(nv);
                    }
                }
                dsNhanVien.clear();
                dsNhanVien.addAll(dsNhanVienTheoChucVu);
                nhanVienAdapter.notifyDataSetChanged();
            }
            else
                Toast.makeText(NhanSuActivity.this, "Không tìm thấy nhân viên", Toast.LENGTH_LONG).show();
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
                        Toast.makeText(NhanSuActivity.this, "Không tìm thấy nhân viên", Toast.LENGTH_LONG).show();
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
    public class LayDanhSachNhanVienNhanVienTask extends AsyncTask<Void, Void, ArrayList<NhanVien>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog= new ProgressDialog(NhanSuActivity.this);
            progressDialog.setTitle("Đang tìm nhân viên");
            progressDialog.setMessage("Vui lòng chờ");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(ArrayList<NhanVien> nhanViens) {
            super.onPostExecute(nhanViens);
            progressDialog.dismiss();
            ArrayList<NhanVien>dsNhanVienTheoChucVu= new ArrayList<>();
            if(nhanViens!=null){
                for(NhanVien nv : nhanViens){
                    if(nv.getRole()==1) {
                        dsNhanVienTheoChucVu.add(nv);
                    }
                }
                dsNhanVien.clear();
                dsNhanVien.addAll(dsNhanVienTheoChucVu);
                nhanVienAdapter.notifyDataSetChanged();
            }
            else
                Toast.makeText(NhanSuActivity.this, "Không tìm thấy nhân viên", Toast.LENGTH_LONG).show();
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
                        Toast.makeText(NhanSuActivity.this, "Không tìm thấy nhân viên", Toast.LENGTH_LONG).show();
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