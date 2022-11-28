package com.mydonate.data;

public class ProgramUmumData {
    private String nama;
    private String keterangan;
    private String foto;
    private String dana;

    public ProgramUmumData() {
    }

    public ProgramUmumData(String nama, String keterangan, String foto, String dana) {
        this.nama = nama;
        this.keterangan = keterangan;
        this.foto = foto;
        this.dana = dana;

    }

    public String getDana() {
        return dana;
    }

    public void setDana(String dana) {
        this.dana = dana;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }
}
