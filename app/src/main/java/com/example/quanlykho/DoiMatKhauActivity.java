package com.example.quanlykho;

import android.app.AlertDialog;
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
import android.widget.ImageView;
import android.widget.Toast;

import com.example.conts.Constant;
import com.example.model.NhanVien;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DoiMatKhauActivity extends AppCompatActivity {
    EditText edtOldPassword, edtNewPassword, getEdtNewPasswordRepeat;
    Button btnChangePass;
    ImageView iv_back;
    NhanVien nhanVienLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doi_mat_khau);
        addControls();
        addEvents();
    }

    private void addEvents() {
        btnChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s1 = edtOldPassword.getText().toString();
                String s2 = nhanVienLogin.getPassword().toString();
                if (s1.equals(s2) == true) {
                    String s3 = edtNewPassword.getText().toString();
                    String s4 = getEdtNewPasswordRepeat.getText().toString();
                    if (s3.equals(s4) == true) {
                        xuLyDoiMatKhau();
                    } else {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(DoiMatKhauActivity.this);
                        alertDialog.setTitle("Lỗi");
                        alertDialog.setMessage("Mật khẩu mới không trùng khớp");
                        alertDialog.setIcon(R.drawable.ic_error);
                        alertDialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();
                    }
                } else {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(DoiMatKhauActivity.this);
                    alertDialog.setTitle("Lỗi");
                    alertDialog.setMessage("Sai mật khẩu");
                    alertDialog.setIcon(R.drawable.ic_error);
                    alertDialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).show();
                }
            }
        });
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void xuLyDoiMatKhau() {
        nhanVienLogin.setPassword(edtNewPassword.getText().toString());
        DoiMatKhauNhanVienTask task = new DoiMatKhauNhanVienTask();
        task.execute(nhanVienLogin);
    }

    private void addControls() {
        edtOldPassword = findViewById(R.id.et_old_password);
        edtNewPassword = findViewById(R.id.et_new_password);
        getEdtNewPasswordRepeat = findViewById(R.id.et_new_password_repeat);
        btnChangePass = findViewById(R.id.bt_change_pass);
        iv_back = findViewById(R.id.iv_back);

        Intent intent = getIntent();
        nhanVienLogin = (NhanVien) intent.getSerializableExtra("NHANVIEN");
    }

    class SuaNhanVien extends AsyncTask<NhanVien, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Boolean doInBackground(NhanVien... nhanViens) {
            return null;
        }
    }

    class ThemNhanVienTask extends AsyncTask<NhanVien, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean == true) {
                Toast.makeText(DoiMatKhauActivity.this, "Xóa thành công", Toast.LENGTH_LONG).show();
            } else if (aBoolean == false) {
                Toast.makeText(DoiMatKhauActivity.this, "Xóa thất bại", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Boolean doInBackground(NhanVien... nhanViens) {
            try {
                NhanVien nv = nhanViens[0];
                String params = "maNVSua=" + nv.getMaNhanVien() +
                        "&tenNV=" + nv.getTenNhanVien() + "&gioiTinh=true" + "&diaChi=" + nv.getDiaChi() +
                        "&phone=" + nv.getPhone() + "&email=" + nv.getEmail() + "&userName=" + nv.getUserName() +
                        "&password=" + nv.getPassword();
                URL url = new URL(Constant.IP_ADDRESS + "NhanVien/?" + params);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");

                connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");

                InputStreamReader isr = new InputStreamReader(connection.getInputStream(), "UTF-8");
                BufferedReader br = new BufferedReader(isr);
                StringBuilder builder = new StringBuilder();
                String line = null;
                while ((line = br.readLine()) != null) {
                    builder.append(line);
                }
                boolean kq = builder.toString().contains("true");
                return kq;
            } catch (Exception ex) {
                Log.e("LOI", ex.toString());
            }
            return false;
        }
    }

    class DoiMatKhauNhanVienTask extends AsyncTask<NhanVien, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean == true) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(DoiMatKhauActivity.this);
                alertDialog.setTitle("Thành công");
                alertDialog.setMessage("Bạn có muốn đăng nhập lại?");
                alertDialog.setIcon(R.drawable.ic_ok);
                alertDialog.setNegativeButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(DoiMatKhauActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                }).setPositiveButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
            } else if (aBoolean == false) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(DoiMatKhauActivity.this);
                alertDialog.setTitle("Thất bại");
                alertDialog.setMessage("Đổi mật khẩu thất bại, vui lòng thử lại");
                alertDialog.setIcon(R.drawable.ic_error);
                alertDialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
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
            try {
                NhanVien nv = nhanViens[0];
                URL url = new URL(Constant.IP_ADDRESS + "NhanVien/?maNVDoiMK=" + nv.getMaNhanVien() + "&password=" + nv.getPassword());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");

                connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");

                InputStreamReader isr = new InputStreamReader(connection.getInputStream(), "UTF-8");
                BufferedReader br = new BufferedReader(isr);
                StringBuilder builder = new StringBuilder();
                String line = null;
                while ((line = br.readLine()) != null) {
                    builder.append(line);
                }
                boolean kq = builder.toString().contains("true");
                return kq;
            } catch (Exception ex) {
                Log.e("LOI", ex.toString());
            }
            return false;
        }
    }
}
