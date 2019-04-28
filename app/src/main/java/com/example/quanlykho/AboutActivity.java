package com.example.quanlykho;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
                intent.putExtra("VIDEO", Constant.NAME_VIDEO);
                startActivity(intent);
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
}
