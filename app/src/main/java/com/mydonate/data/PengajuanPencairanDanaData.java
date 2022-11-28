package com.mydonate.data;

public class PengajuanPencairanDanaData {
    String id_kebutuhan, id_pengurus, nominal_pencairan, keterangan, status;

    public PengajuanPencairanDanaData() {
    }

    public PengajuanPencairanDanaData(String id_kebutuhan, String id_pengurus, String nominal_pencairan, String keterangan, String status) {
        this.id_kebutuhan = id_kebutuhan;
        this.id_pengurus = id_pengurus;
        this.nominal_pencairan = nominal_pencairan;
        this.keterangan = keterangan;
        this.status = status;
    }

    public String getId_kebutuhan() {
        return id_kebutuhan;
    }

    public void setId_kebutuhan(String id_kebutuhan) {
        this.id_kebutuhan = id_kebutuhan;
    }

    public String getId_pengurus() {
        return id_pengurus;
    }

    public void setId_pengurus(String id_pengurus) {
        this.id_pengurus = id_pengurus;
    }

    public String getNominal_pencairan() {
        return nominal_pencairan;
    }

    public void setNominal_pencairan(String nominal_pencairan) {
        this.nominal_pencairan = nominal_pencairan;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
