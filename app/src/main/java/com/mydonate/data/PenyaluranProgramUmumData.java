package com.mydonate.data;

import java.util.ArrayList;

public class PenyaluranProgramUmumData {
    private String id_program,  nama_masjid, alamat_masjid, dana_donasi, kuantitas;
    private ArrayList<String>  foto_penyerahan;

    public PenyaluranProgramUmumData() {
    }

    public PenyaluranProgramUmumData(String id_program, String nama_masjid, String dana_donasi, String kuantitas, ArrayList<String> foto_penyerahan, String alamat_masjid) {
        this.id_program = id_program;
        this.nama_masjid = nama_masjid;
        this.dana_donasi = dana_donasi;
        this.kuantitas = kuantitas;
        this.foto_penyerahan = foto_penyerahan;
        this.alamat_masjid = alamat_masjid;
    }

    public String getAlamat_masjid() {
        return alamat_masjid;
    }

    public void setAlamat_masjid(String alamat_masjid) {
        this.alamat_masjid = alamat_masjid;
    }

    public String getId_program() {
        return id_program;
    }

    public void setId_program(String id_program) {
        this.id_program = id_program;
    }

    public String getNama_masjid() {
        return nama_masjid;
    }

    public void setNama_masjid(String nama_masjid) {
        this.nama_masjid = nama_masjid;
    }

    public String getDana_donasi() {
        return dana_donasi;
    }

    public void setDana_donasi(String dana_donasi) {
        this.dana_donasi = dana_donasi;
    }

    public String getKuantitas() {
        return kuantitas;
    }

    public void setKuantitas(String kuantitas) {
        this.kuantitas = kuantitas;
    }

    public ArrayList<String> getFoto_penyerahan() {
        return foto_penyerahan;
    }

    public void setFoto_penyerahan(ArrayList<String> foto_penyerahan) {
        this.foto_penyerahan = foto_penyerahan;
    }
}
