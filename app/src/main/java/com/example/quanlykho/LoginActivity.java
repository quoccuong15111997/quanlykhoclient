package com.example.quanlykho;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.conts.Constant;
import com.example.firebase.NhanVienFirebase;
import com.example.model.NhanVien;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class LoginActivity extends AppCompatActivity {
    EditText edtUserName, edtPassword;
    Button btnLogin;
    ProgressDialog progressDialog;
    AlertDialog.Builder alertDialog;
    CheckBox chkRemember;
    SharedPreferences sharedPreferences;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String USERNAME = "userNameKey";
    public static final String PASS = "passKey";
    public static final String REMEMBER = "remember";
    public static String KEY_NHAN_VIEN="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        addControl();
        addEvents();
        loadData();
    }
    private void addEvents() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkRemember.isChecked())
                    //lưu lại thông tin đăng nhập
                    saveData(edtUserName.getText().toString(),edtPassword.getText().toString());
                else
                    clearData();
                LayChiTietNhanVienTheoUserNameTask task= new LayChiTietNhanVienTheoUserNameTask();
                task.execute(URLEncoder.encode(edtUserName.getText().toString()));
            }
        });
    }

    private void addControl() {
        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        edtUserName=findViewById(R.id.edtUserName);
        edtPassword=findViewById(R.id.edtPassword);
        btnLogin=findViewById(R.id.btnLogin);
        chkRemember=findViewById(R.id.chkRemember);
    }
    private void clearData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }

    private void saveData(String username, String Pass) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USERNAME, username);
        editor.putString(PASS, Pass);
        editor.putBoolean(REMEMBER,chkRemember.isChecked());
        editor.commit();
    }
    private void loadData() {
        if(sharedPreferences.getBoolean(REMEMBER,false)) {
            edtUserName.setText(sharedPreferences.getString(USERNAME, ""));
            edtPassword.setText(sharedPreferences.getString(PASS, ""));
            chkRemember.setChecked(true);
        }
        else
            chkRemember.setChecked(false);

    }

    class LayChiTietNhanVienTheoUserNameTask extends AsyncTask<String,Void, NhanVien> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog= new ProgressDialog(LoginActivity.this);
            progressDialog.setTitle("Đang đăng nhập");
            progressDialog.setMessage("Vui lòng chờ...");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(NhanVien nhanVien) {
            super.onPostExecute(nhanVien);
            progressDialog.dismiss();
            if(nhanVien!=null){
                String pass=nhanVien.getPassword();
                if (pass.equals(edtPassword.getText().toString())){
                    initFirebase(nhanVien);
                    Intent intent= new Intent(LoginActivity.this,MainActivity.class);
                    Toast.makeText(LoginActivity.this,"Đăng nhập thành công",Toast.LENGTH_LONG).show();
                    intent.putExtra("NHANVIEN",nhanVien);
                    intent.putExtra("KEYNHANVIEN",KEY_NHAN_VIEN);
                    startActivity(intent);
                }
                else {
                    //Toast.makeText(LoginActivity.this,"Sai UserName hoặc Password",Toast.LENGTH_LONG).show();
                    alertDialog= new AlertDialog.Builder(LoginActivity.this);
                    alertDialog.setTitle("Lỗi");
                    alertDialog.setMessage("Sai UserName hoặc Password"+"\n"+"vui lòng thử lại");
                    alertDialog.setIcon(R.drawable.ic_error);
                    alertDialog.setNegativeButton("OK",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).show();
                }
            }
            else{
                alertDialog= new AlertDialog.Builder(LoginActivity.this);
                alertDialog.setTitle("Lỗi");
                alertDialog.setMessage("Sai UserName hoặc Password"+"\n"+"Vui lòng thử lại");
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
                        Toast.makeText(LoginActivity.this,"Sai UserName hoặc Password",Toast.LENGTH_LONG).show();
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
    public void initFirebase(final NhanVien nhanVien) {
        FirebaseDatabase.getInstance().getReference().child("NhanVien").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                NhanVienFirebase nhanVienFirebase=dataSnapshot.getValue(NhanVienFirebase.class);
                if(nhanVienFirebase.getMaNhanVien()==nhanVien.getMaNhanVien()){
                    KEY_NHAN_VIEN=dataSnapshot.getKey();
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
