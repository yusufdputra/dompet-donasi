package com.mydonate.data;

import java.util.ArrayList;

public class KebutuhanData {
    private String id_pengurus, nama_kebutuhan;
    private ArrayList<String> foto_kebutuhan;
    private String biaya_kebutuhan, tanggal, jenis_kebutuhan, detail_kebutuhan;

    public KebutuhanData() {
    }

    public KebutuhanData(String id_pengurus, String nama_kebutuhan, String biaya_kebutuhan,  ArrayList<String> foto_kebutuhan, String tanggal, String jenis_kebutuhan, String detail_kebutuhan) {
        this.id_pengurus = id_pengurus;
        this.nama_kebutuhan = nama_kebutuhan;
        this.biaya_kebutuhan = biaya_kebutuhan;
        this.foto_kebutuhan = foto_kebutuhan;
        this.tanggal = tanggal;
        this.jenis_kebutuhan = jenis_kebutuhan;
        this.detail_kebutuhan = detail_kebutuhan;
    }

    public String getDetail_kebutuhan() {
        return detail_kebutuhan;
    }

    public void setDetail_kebutuhan(String detail_kebutuhan) {
        this.detail_kebutuhan = detail_kebutuhan;
    }

    public String getJenis_kebutuhan() {
        return jenis_kebutuhan;
    }

    public void setJenis_kebutuhan(String jenis_kebutuhan) {
        this.jenis_kebutuhan = jenis_kebutuhan;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public ArrayList<String> getFoto_kebutuhan() {
        return foto_kebutuhan;
    }

    public void setFoto_kebutuhan(ArrayList<String> foto_kebutuhan) {
        this.foto_kebutuhan = foto_kebutuhan;
    }

    public String getId_pengurus() {
        return id_pengurus;
    }

    public void setId_pengurus(String id_pengurus) {
        this.id_pengurus = id_pengurus;
    }

    public String getNama_kebutuhan() {
        return nama_kebutuhan;
    }

    public void setNama_kebutuhan(String nama_kebutuhan) {
        this.nama_kebutuhan = nama_kebutuhan;
    }

    public String getBiaya_kebutuhan() {
        return biaya_kebutuhan;
    }

    public void setBiaya_kebutuhan(String biaya_kebutuhan) {
        this.biaya_kebutuhan = biaya_kebutuhan;
    }
}
