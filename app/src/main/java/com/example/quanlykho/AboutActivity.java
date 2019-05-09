package com.example.quanlykho;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.conts.Constant;

public class AboutActivity extends Fragment {
    View view;
    TextView txtHoTro, txtHuongDan;
    TextView txtTraCuu, txtThemSuaXoa, txtNhanSu, txtTaiKhoan;
    boolean trangThai=true;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = (View) inflater.inflate(R.layout.activity_about, container, false);
        addControls();
        addEvents();
        return view;
    }

    private void addEvents() {
        txtHuongDan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (trangThai == true) {
                    txtTraCuu.setVisibility(View.VISIBLE);
                    txtThemSuaXoa.setVisibility(View.VISIBLE);
                    txtNhanSu.setVisibility(View.VISIBLE);
                    txtTaiKhoan.setVisibility(View.VISIBLE);
                    trangThai=false;
                }
                else if(trangThai==false){
                    txtTraCuu.setVisibility(View.GONE);
                    txtThemSuaXoa.setVisibility(View.GONE);
                    txtNhanSu.setVisibility(View.GONE);
                    txtTaiKhoan.setVisibility(View.GONE);
                    trangThai=true;
                }
            }
        });
        txtTraCuu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(view.getContext(),VideoViewerActivity.class);
                intent.putExtra("VIDEO", Constant.NAME_VIDEO_TRACUU);
                startActivity(intent);
            }
        });
        txtHoTro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder= new AlertDialog.Builder(view.getContext());
                builder.setTitle("Liên hệ hỗ trợ");
                builder.setNegativeButton("Gọi", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        xuLyGoiDienThoai();
                    }
                }).setPositiveButton("Gửi tin nhắn", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        xuLyGuiSMS();
                    }
                }).show();
            }
        });

    }

    private void addControls() {
        txtHoTro=view.findViewById(R.id.txtHoTro);
        txtHuongDan=view.findViewById(R.id.txtHuongDan);

        txtTraCuu=view.findViewById(R.id.txtTraCuu);
        txtThemSuaXoa=view.findViewById(R.id.txtThemSanPham);
        txtNhanSu=view.findViewById(R.id.txtQuanLyNhanSu);
        txtTaiKhoan=view.findViewById(R.id.txtQuanLyTaiKhoan);

        txtTraCuu.setVisibility(View.GONE);
        txtThemSuaXoa.setVisibility(View.GONE);
        txtNhanSu.setVisibility(View.GONE);
        txtTaiKhoan.setVisibility(View.GONE);
    }
    private void xuLyGuiSMS() {
        String phone="0977929100";
        Uri uri = Uri.parse("smsto:"+phone);
        Intent it = new Intent(Intent.ACTION_SENDTO, uri);
        it.putExtra("sms_body", "Tin nhắn được gửi từ: "+MainActivity.nhanVien.getTenNhanVien());
        startActivity(it);
    }

    private void xuLyGoiDienThoai() {
        String phone="0977929100";
        Uri uri=Uri.parse("tel:"+phone);
        Intent intent= new Intent(Intent.ACTION_DIAL,uri);
        intent.setData(uri);
        startActivity(intent);
    }
}
