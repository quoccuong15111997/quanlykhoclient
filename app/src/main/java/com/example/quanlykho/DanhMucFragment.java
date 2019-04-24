package com.example.quanlykho;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.conts.Constant;
import com.example.model.DanhMuc;
import com.example.model.SanPham;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class DanhMucFragment extends Fragment {
    View view;
    Spinner spinner_DanhMuc;
    ArrayAdapter<DanhMuc> danhMucAdapter;
    EditText et_MadanhMuc, edt_TenDanhMuc, et_SoLuong;
    Button bt_Them, bt_Luu, bt_Sua, bt_Xoa;
    ArrayList<SanPham> dsSanPham= new ArrayList<>();
    int viTri=1;
    int sl=0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = (View) inflater.inflate(R.layout.fragment_danhmuc, container, false);
        addControls();
        addEvents();
        return view;
    }

    private void addEvents() {
        bt_Them.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xuLyThem();
            }
        });
        bt_Xoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xuLyXoa();
            }
        });
        bt_Sua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xuLySua();
            }
        });
        bt_Luu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xuLyLuu();
            }
        });
        spinner_DanhMuc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sl=0;
                DanhMuc danhMuc= (DanhMuc) danhMucAdapter.getItem(position);
                et_MadanhMuc.setText(danhMuc.getMaDanhMuc()+"");
                edt_TenDanhMuc.setText(danhMuc.getTenDanhMuc());
                int sl=0;
                TraCuuSanPhamTheoMaDmTask task1= new TraCuuSanPhamTheoMaDmTask();
                task1.execute(Integer.valueOf(et_MadanhMuc.getText().toString()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void xuLySua() {
        DanhMuc dm= new DanhMuc();
        dm.setMaDanhMuc(Integer.parseInt(et_MadanhMuc.getText().toString()));
        dm.setTenDanhMuc(edt_TenDanhMuc.getText().toString());
        SuaDanhMucTask task= new SuaDanhMucTask();
        task.execute(dm);
    }

    private void xuLyXoa() {
        if(et_MadanhMuc.getText()!=null){
            XoaDanhMucTask xoaDanhMucTask= new XoaDanhMucTask();
            xoaDanhMucTask.execute(Integer.parseInt(et_MadanhMuc.getText().toString()));
        }
    }

    private void xuLyThem() {
        et_MadanhMuc.setText("");
        edt_TenDanhMuc.setText("");
        edt_TenDanhMuc.setEnabled(true);
        et_MadanhMuc.setEnabled(true);
    }

    private void xuLyLuu() {
        DanhMuc dm= new DanhMuc();
        dm.setMaDanhMuc(Integer.parseInt(et_MadanhMuc.getText().toString()));
        dm.setTenDanhMuc(edt_TenDanhMuc.getText().toString());
        LuuMoiDanhMucTask luuMoiDanhMucTask= new LuuMoiDanhMucTask();
        luuMoiDanhMucTask.execute(dm);
    }

    private void addControls() {
        spinner_DanhMuc=view.findViewById(R.id.spinner_DanhMucQuanLy);
        danhMucAdapter=new ArrayAdapter<>(view.getContext(),android.R.layout.simple_spinner_item);
        danhMucAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_DanhMuc.setAdapter(danhMucAdapter);

        et_MadanhMuc=view.findViewById(R.id.edtMaDanhMucQuanLy);
        et_SoLuong=view.findViewById(R.id.edtSoLuongTonQuanLy);
        edt_TenDanhMuc=view.findViewById(R.id.edtTenDanhMucQuanLy);

        bt_Them=view.findViewById(R.id.btnThemDanhMuc);
        bt_Luu=view.findViewById(R.id.btnLuuDanhMuc);
        bt_Sua=view.findViewById(R.id.btnSuaDanhMuc);
        bt_Xoa=view.findViewById(R.id.btnXoaDanhMuc);

        LayDanhSachDanhMuc();

    }

    private void LayDanhSachDanhMuc() {
        LayDanhSachDanhMucTask task= new LayDanhSachDanhMucTask();
        task.execute();
    }

    class LayDanhSachDanhMucTask extends AsyncTask<Void, Void, ArrayList<DanhMuc>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<DanhMuc> danhMucs) {
            super.onPostExecute(danhMucs);
            if (danhMucs != null) {
                danhMucAdapter.clear();
                danhMucAdapter.addAll(danhMucs);
                danhMucAdapter.notifyDataSetChanged();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected ArrayList<DanhMuc> doInBackground(Void... voids) {
            ArrayList<DanhMuc> dsDanhMuc = new ArrayList<>();
            try {
                URL url = new URL(Constant.IP_ADDRESS+"DanhMuc");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");

                InputStreamReader isr = new InputStreamReader(connection.getInputStream());
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
                        Toast.makeText(view.getContext(), "không tìm thấy danh mục", Toast.LENGTH_LONG).show();
                    } else if (object != null) {
                        int ma = object.getInt("MaDanhMuc");
                        String ten = object.getString("TenDanhMuc");
                        DanhMuc dm = new DanhMuc();
                        dm.setTenDanhMuc(ten);
                        dm.setMaDanhMuc(ma);

                        dsDanhMuc.add(dm);
                    }
                }
            } catch (Exception ex) {
                Log.e("LOI", ex.toString());
            }
            return dsDanhMuc;
        }
    }
    class TraCuuSanPhamTheoMaDmTask extends AsyncTask<Integer, Void, ArrayList<SanPham>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<SanPham> sanPhams) {
            super.onPostExecute(sanPhams);
            if (sanPhams != null) {
                int sl=0;
                for (SanPham sanPham : sanPhams){
                    sl+=sanPham.getSoLuong();
                }
                et_SoLuong.setText(sl+"");
            } else
                Toast.makeText(view.getContext(), "không tìm thấy sản phẩm", Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected ArrayList<SanPham> doInBackground(Integer... integers) {
            ArrayList<SanPham> dsSp = new ArrayList<>();
            try {
                URL url = new URL(Constant.IP_ADDRESS+"SanPham/?madm=" + integers[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");

                InputStreamReader isr = new InputStreamReader(connection.getInputStream());
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
                        Toast.makeText(view.getContext(), "không tìm thấy sản phẩm", Toast.LENGTH_LONG).show();
                    } else if (object != null) {
                        int ma = object.getInt("Ma");
                        String ten = object.getString("Ten");
                        int donGia = object.getInt("DonGia");
                        int soLuong = object.getInt("SoLuong");
                        boolean tinhTrang = object.getBoolean("TinhTrang");
                        int size = object.getInt("Size");

                        SanPham sp = new SanPham();
                        sp.setMaSanPham(ma);
                        sp.setTenSanPham(ten);
                        sp.setDonGia(donGia);
                        sp.setSoLuong(soLuong);
                        sp.setTinhTrang(tinhTrang);
                        sp.setSize(size);

                        dsSp.add(sp);
                    }
                }
                return dsSp;
            } catch (Exception ex) {
                Log.e("LOI", ex.toString());
            }
            return null;
        }
    }
    class XoaDanhMucTask extends AsyncTask<Integer,Void,Boolean>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(aBoolean==true){
                AlertDialog.Builder alertDialog= new AlertDialog.Builder(view.getContext());
                alertDialog.setTitle("Xóa thành công");
                alertDialog.setIcon(R.drawable.ic_ok);
                alertDialog.setNegativeButton("OK",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
                LayDanhSachDanhMuc();
            }
            else {
                AlertDialog.Builder alertDialog= new AlertDialog.Builder(view.getContext());
                alertDialog.setTitle("Xóa thất bại");
                alertDialog.setIcon(R.drawable.ic_error);
                alertDialog.setNegativeButton("OK",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Boolean doInBackground(Integer... integers) {
            try{
                URL url= new URL(Constant.IP_ADDRESS+"DanhMuc/?maDmXoa="+integers[0]);
                HttpURLConnection connection= (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type","application/json;charset=UTF-8");

                InputStreamReader isr= new InputStreamReader(connection.getInputStream(),"UTF-8");
                BufferedReader br= new BufferedReader(isr);
                StringBuilder builder= new StringBuilder();
                String line=null;
                while ((line=br.readLine())!=null){
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
    class LuuMoiDanhMucTask extends AsyncTask<DanhMuc, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(aBoolean==true){
                AlertDialog.Builder alertDialog= new AlertDialog.Builder(view.getContext());
                alertDialog.setTitle("Thêm thành công");
                alertDialog.setIcon(R.drawable.ic_ok);
                alertDialog.setNegativeButton("OK",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
                LayDanhSachDanhMuc();
            }
            else {
                AlertDialog.Builder alertDialog= new AlertDialog.Builder(view.getContext());
                alertDialog.setTitle("Thêm thất bại");
                alertDialog.setIcon(R.drawable.ic_error);
                alertDialog.setNegativeButton("OK",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Boolean doInBackground(DanhMuc... danhMucs) {
            try{
                URL url= new URL(Constant.IP_ADDRESS+"DanhMuc/?maDm="+danhMucs[0].getMaDanhMuc()+"&tenDm="+ URLEncoder.encode(danhMucs[0].getTenDanhMuc()));
                HttpURLConnection connection= (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type","application/json;charset=UTF-8");

                InputStreamReader isr= new InputStreamReader(connection.getInputStream(),"UTF-8");
                BufferedReader br= new BufferedReader(isr);
                StringBuilder builder= new StringBuilder();
                String line=null;
                while ((line=br.readLine())!=null){
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
    class SuaDanhMucTask extends AsyncTask<DanhMuc,Void,Boolean>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(aBoolean==true){
                AlertDialog.Builder alertDialog= new AlertDialog.Builder(view.getContext());
                alertDialog.setTitle("Sửa thành công");
                alertDialog.setIcon(R.drawable.ic_ok);
                alertDialog.setNegativeButton("OK",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
                LayDanhSachDanhMuc();
            }
            else {
                AlertDialog.Builder alertDialog= new AlertDialog.Builder(view.getContext());
                alertDialog.setTitle("Sửa thất bại");
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
        protected Boolean doInBackground(DanhMuc... danhMucs) {
            try{
                URL url= new URL(Constant.IP_ADDRESS+"DanhMuc/?maDmSua="+danhMucs[0].getMaDanhMuc()+"&tenDm="+ URLEncoder.encode(danhMucs[0].getTenDanhMuc()));
                HttpURLConnection connection= (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type","application/json;charset=UTF-8");

                InputStreamReader isr= new InputStreamReader(connection.getInputStream(),"UTF-8");
                BufferedReader br= new BufferedReader(isr);
                StringBuilder builder= new StringBuilder();
                String line=null;
                while ((line=br.readLine())!=null){
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
}
