package com.example.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.model.SanPham;
import com.example.quanlykho.R;

import java.util.ArrayList;
import java.util.List;

public class SanPhamAdapter extends ArrayAdapter<SanPham> {
    Activity context = null;
    List<SanPham> objects;
    int resource;

    public SanPhamAdapter(Context context, int resource, ArrayList<SanPham> objects) {
        super(context, resource, objects);
        this.context = (Activity) context;
        this.resource = resource;
        this.objects = objects;
    }
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = this.context.getLayoutInflater();
        View row = inflater.inflate(this.resource, null);
        TextView txtTen=row.findViewById(R.id.txtTenSanPham);
        TextView txtGia=row.findViewById(R.id.txtGia);
        TextView txtSoLuong=row.findViewById(R.id.txtSoLuong);

        SanPham sp=objects.get(position);
        txtGia.setText(sp.getDonGia()+"");
        txtTen.setText(sp.getTenSanPham());
        txtSoLuong.setText(sp.getSoLuong()+"");

        return row;
    }
}
