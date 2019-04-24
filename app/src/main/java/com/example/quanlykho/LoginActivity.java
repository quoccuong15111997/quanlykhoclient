package com.example.quanlykho;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class LoginActivity extends AppCompatActivity {
    EditText edtUserName, edtPassword;
    Button btnLogin;
    ProgressDialog progressDialog;
    AlertDialog.Builder alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        addControl();
        addEvents();
    }
    private void addEvents() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayChiTietNhanVienTheoUserNameTask task= new LayChiTietNhanVienTheoUserNameTask();
                task.execute(URLEncoder.encode(edtUserName.getText().toString()));
            }
        });
    }

    private void addControl() {
        edtUserName=findViewById(R.id.edtUserName);
        edtPassword=findViewById(R.id.edtPassword);
        btnLogin=findViewById(R.id.btnLogin);
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
                    Intent intent= new Intent(LoginActivity.this,MainActivity.class);
                    Toast.makeText(LoginActivity.this,"Đăng nhập thành công",Toast.LENGTH_LONG).show();
                    intent.putExtra("NHANVIEN",nhanVien);
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
}
