package com.example.model;

import java.io.Serializable;

public class NhanVien implements Serializable {
    private int maNhanVien;
    private String tenNhanVien;
    private int gioiTinh;
    private String diaChi;
    private int phone;
    private String email;
    private String userName;
    private String password;
    private int role;

    public int getMaNhanVien() {
        return maNhanVien;
    }

    public String getTenNhanVien() {
        return tenNhanVien;
    }

    public int getGioiTinh() {
        return gioiTinh;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public int getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public int getRole() {
        return role;
    }

    public void setMaNhanVien(int maNhanVien) {
        this.maNhanVien = maNhanVien;
    }

    public void setTenNhanVien(String tenNhanVien) {
        this.tenNhanVien = tenNhanVien;
    }

    public void setGioiTinh(int gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public void setPhone(int phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public NhanVien(int maNhanVien, String tenNhanVien, int gioiTinh, String diaChi, int phone, String email, String userName, String password, int role) {
        this.maNhanVien = maNhanVien;
        this.tenNhanVien = tenNhanVien;
        this.gioiTinh = gioiTinh;
        this.diaChi = diaChi;
        this.phone = phone;
        this.email = email;
        this.userName = userName;
        this.password = password;
        this.role = role;
    }

    public NhanVien() {
    }

    @Override
    public String toString() {
        return tenNhanVien;
    }
}
