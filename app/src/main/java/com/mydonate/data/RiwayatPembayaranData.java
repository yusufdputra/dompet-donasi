package com.mydonate.data;

import java.util.Date;

public class RiwayatPembayaranData {
    private String id_kebutuhan, id_donatur, nominal_bayar, foto_bukti_pembayaran, tanggal_donasi, pesan;
    Boolean verified;


    public RiwayatPembayaranData() {
    }

    public RiwayatPembayaranData(String id_kebutuhan, String id_donatur, String nominal_bayar, String foto_bukti_pembayaran, Boolean verified, String tanggal_donasi, String pesan) {
        this.id_kebutuhan = id_kebutuhan;
        this.id_donatur = id_donatur;
        this.nominal_bayar = nominal_bayar;
        this.foto_bukti_pembayaran = foto_bukti_pembayaran;
        this.verified = verified;
        this.tanggal_donasi = tanggal_donasi;
        this.pesan = pesan;
    }

    public String getPesan() {
        return pesan;
    }

    public void setPesan(String pesan) {
        this.pesan = pesan;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public String getTanggal_donasi() {
        return tanggal_donasi;
    }

    public void setTanggal_donasi(String tanggal_donasi) {
        this.tanggal_donasi = tanggal_donasi;
    }

    public String getFoto_bukti_pembayaran() {
        return foto_bukti_pembayaran;
    }

    public void setFoto_bukti_pembayaran(String foto_bukti_pembayaran) {
        this.foto_bukti_pembayaran = foto_bukti_pembayaran;
    }

    public String getId_kebutuhan() {
        return id_kebutuhan;
    }

    public void setId_kebutuhan(String id_kebutuhan) {
        this.id_kebutuhan = id_kebutuhan;
    }

    public String getId_donatur() {
        return id_donatur;
    }

    public void setId_donatur(String id_donatur) {
        this.id_donatur = id_donatur;
    }

    public String getNominal_bayar() {
        return nominal_bayar;
    }

    public void setNominal_bayar(String nominal_bayar) {
        this.nominal_bayar = nominal_bayar;
    }
}
