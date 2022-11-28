package com.mydonate.data;

import com.midtrans.sdk.corekit.models.CustomerDetails;

public class DonatursData {


    private String nama, phone, email, foto_profile, tipe_user, alias;

    public DonatursData() {
    }

    public DonatursData(String nama, String phone, String email,  String foto_profile, String tipe_user, String alias) {
        this.nama = nama;
        this.phone = phone;
        this.email = email;
        this.foto_profile = foto_profile;
        this.tipe_user = tipe_user;
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getFoto_profile() {
        return foto_profile;
    }

    public void setFoto_profile(String foto_profile) {
        this.foto_profile = foto_profile;
    }

    public String getTipe_user() {
        return tipe_user;
    }

    public void setTipe_user(String tipe_user) {
        this.tipe_user = tipe_user;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


}
