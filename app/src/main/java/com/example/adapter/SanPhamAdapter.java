package com.example.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.adapterimpl.DeleteButtonOnclick;
import com.example.model.SanPham;
import com.example.quanlykho.R;

import java.util.ArrayList;
import java.util.List;

public class SanPhamAdapter extends ArrayAdapter<SanPham> {
    Activity context = null;
    List<SanPham> objects;
    int resource;
    private DeleteButtonOnclick deleteButtonOnclick;
    public void isClicked(DeleteButtonOnclick deleteButtonOnclick){
        this.deleteButtonOnclick=deleteButtonOnclick;
    }

    public SanPhamAdapter(Context context, int resource, ArrayList<SanPham> objects) {
        super(context, resource, objects);
        this.context = (Activity) context;
        this.resource = resource;
        this.objects = objects;
    }
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        LayoutInflater layoutInflater = this.context.getLayoutInflater();
        if(convertView==null){
            convertView=layoutInflater.inflate(this.resource,null);
            viewHolder=new ViewHolder();
            viewHolder.txtTen=convertView.findViewById(R.id.txtTenSanPham);
            viewHolder.txtGia=convertView.findViewById(R.id.txtGia);
            viewHolder.txtSoLuong=convertView.findViewById(R.id.txtSoLuong);
            viewHolder.imgDelete=convertView.findViewById(R.id.imgDelete);
            viewHolder.position=position;

            convertView.setTag(viewHolder);
        }
        else {
            viewHolder= (ViewHolder) convertView.getTag();
        }

        SanPham sp=objects.get(position);
        viewHolder.txtGia.setText(sp.getDonGia()+"");
        viewHolder.txtTen.setText(sp.getTenSanPham());
        viewHolder.txtSoLuong.setText(sp.getSoLuong()+"");

        viewHolder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteButtonOnclick.deleteOnclick(position);
            }
        });

        return convertView;
    }
    public static class ViewHolder{
        TextView txtTen;
        TextView txtGia;
        TextView txtSoLuong;
        ImageView imgDelete;
        int position;
    }
}
