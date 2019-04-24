package com.example.model;

import java.io.Serializable;

public class DanhMuc implements Serializable {
    private int maDanhMuc;
    private String tenDanhMuc;

    public int getMaDanhMuc() {
        return maDanhMuc;
    }

    public void setMaDanhMuc(int maDanhMuc) {
        this.maDanhMuc = maDanhMuc;
    }

    public String getTenDanhMuc() {
        return tenDanhMuc;
    }

    public void setTenDanhMuc(String tenDanhMuc) {
        this.tenDanhMuc = tenDanhMuc;
    }

    public DanhMuc(int maDanhMuc, String tenDanhMuc) {
        this.maDanhMuc = maDanhMuc;
        this.tenDanhMuc = tenDanhMuc;
    }

    public DanhMuc() {
    }

    @Override
    public String toString() {
        return tenDanhMuc;
    }
}
