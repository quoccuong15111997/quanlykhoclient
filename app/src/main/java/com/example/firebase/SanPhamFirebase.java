package com.example.firebase;

public class SanPhamFirebase {
    private int maSanPham;
    private String tenSanPham;
    private int donGia;
    private int maDanhMuc;
    private int soLuong;
    private int size;
    private String urlImage;

    public SanPhamFirebase(int maSanPham, String tenSanPham, int donGia, int maDanhMuc, int soLuong, int size, String urlImage) {
        this.maSanPham = maSanPham;
        this.tenSanPham = tenSanPham;
        this.donGia = donGia;
        this.maDanhMuc = maDanhMuc;
        this.soLuong = soLuong;
        this.size = size;
        this.urlImage = urlImage;
    }

    public SanPhamFirebase() {
    }

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

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }
}
