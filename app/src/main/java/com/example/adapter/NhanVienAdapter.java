package com.example.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.model.NhanVien;
import com.example.quanlykho.R;

import java.util.ArrayList;
import java.util.List;

public class NhanVienAdapter extends ArrayAdapter<NhanVien> {
    Activity context=null;
    List<NhanVien> objects;
    int resource;
    public NhanVienAdapter(Context context, int resource, ArrayList<NhanVien> objects){
        super(context,resource,objects);
        this.context= (Activity) context;
        this.resource=resource;
        this.objects=objects;
    }
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater=this.context.getLayoutInflater();
        View row=inflater.inflate(this.resource,null);
        TextView txtTen=row.findViewById(R.id.txtTenNhanVien);
        TextView txtChucVu=row.findViewById(R.id.txtChucVu);

        NhanVien nhanVien=objects.get(position);
        txtTen.setText(nhanVien.getTenNhanVien());
        if(nhanVien.getRole()==0){
            txtChucVu.setText("Quản lý");
        }
        else if(nhanVien.getRole()==1){
            txtChucVu.setText("Nhân viên");
        }

        return row;
    }
}
