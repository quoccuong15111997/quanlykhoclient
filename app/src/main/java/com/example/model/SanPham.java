package com.example.model;

import java.io.Serializable;

public class SanPham implements Serializable {
    private int maSanPham;
    private String tenSanPham;
    private int donGia;
    private int maDanhMuc;
    private int soLuong;
    private boolean tinhTrang;
    private int size;

    public int getMaSanPham() {
        return maSanPham;
    }

    public void setMaSanPham(int maSanPham) {
        this.maSanPham = maSanPham;
    }

    public String getTenSanPham() {
        return tenSanPham;
    }

    public void setTenSanPham(String tenSanPham) {
        this.tenSanPham = tenSanPham;
    }

    public int getDonGia() {
        return donGia;
    }

    public void setDonGia(int donGia) {
        this.donGia = donGia;
    }

    public int getMaDanhMuc() {
        return maDanhMuc;
    }

    public void setMaDanhMuc(int maDanhMuc) {
        this.maDanhMuc = maDanhMuc;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public boolean isTinhTrang() {
        return tinhTrang;
    }

    public void setTinhTrang(boolean tinhTrang) {
        this.tinhTrang = tinhTrang;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public SanPham(int maSanPham, String tenSanPham, int donGia, int maDanhMuc, int soLuong, boolean tinhTrang, int size) {
        this.maSanPham = maSanPham;
        this.tenSanPham = tenSanPham;
        this.donGia = donGia;
        this.maDanhMuc = maDanhMuc;
        this.soLuong = soLuong;
        this.tinhTrang = tinhTrang;
        this.size = size;
    }

    public SanPham() {
    }

    @Override
    public String toString() {
        String tt="";
        if(tinhTrang==true){
            tt="Còn hàng";
        }
        else
            tt="Hết hàng";
        return "Mã sản phẩm: "+maSanPham+"\n"+"Tên sản phẩm: "+tenSanPham+"\n"+"Đơn giá: "+donGia+"\n"+"Size: "+size+"\n"+"Số lượng: "+soLuong
                +"\n"+"Tình trạng: "+tt;
    }
}