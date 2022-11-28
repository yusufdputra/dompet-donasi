package com.mydonate.data;

public class PengurusData {
    private String nama_pengurus;
    private String alamat_pengurus;
    private String nama_masjid;
    private String alamat_masjid;
    private String phone;
    private String email;
    private String foto_tempat;
    private String foto_ktp;
    private String foto_surat_pernyataan;
    private boolean verified;
    private String tipe_user;
    private String tipe_tempat;
    private String foto_sim;
    private String foto_imb;


    public PengurusData() {
    }

    public String getFoto_sim() {
        return foto_sim;
    }

    public void setFoto_sim(String foto_sim) {
        this.foto_sim = foto_sim;
    }

    public String getFoto_imb() {
        return foto_imb;
    }

    public void setFoto_imb(String foto_imb) {
        this.foto_imb = foto_imb;
    }

    public String getTipe_tempat() {
        return tipe_tempat;
    }

    public void setTipe_tempat(String tipe_tempat) {
        this.tipe_tempat = tipe_tempat;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getTipe_user() {
        return tipe_user;
    }

    public void setTipe_user(String tipe_user) {
        this.tipe_user = tipe_user;
    }

    public String getNama_pengurus() {
        return nama_pengurus;
    }

    public void setNama_pengurus(String nama_pengurus) {
        this.nama_pengurus = nama_pengurus;
    }

    public String getAlamat_pengurus() {
        return alamat_pengurus;
    }

    public void setAlamat_pengurus(String alamat_pengurus) {
        this.alamat_pengurus = alamat_pengurus;
    }

    public String getNama_masjid() {
        return nama_masjid;
    }

    public void setNama_masjid(String nama_masjid) {
        this.nama_masjid = nama_masjid;
    }

    public String getAlamat_masjid() {
        return alamat_masjid;
    }

    public void setAlamat_masjid(String alamat_masjid) {
        this.alamat_masjid = alamat_masjid;
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

    public String getFoto_tempat() {
        return foto_tempat;
    }

    public void setFoto_tempat(String foto_tempat) {
        this.foto_tempat = foto_tempat;
    }

    public String getFoto_ktp() {
        return foto_ktp;
    }

    public void setFoto_ktp(String foto_ktp) {
        this.foto_ktp = foto_ktp;
    }

    public String getFoto_surat_pernyataan() {
        return foto_surat_pernyataan;
    }

    public void setFoto_surat_pernyataan(String foto_surat_pernyataan) {
        this.foto_surat_pernyataan = foto_surat_pernyataan;
    }


    public PengurusData(String nama_pengurus, String alamat_pengurus, String nama_masjid, String alamat_masjid, String phone,
                        String email, String foto_tempat, String foto_ktp, String foto_surat_pernyataan, boolean verified, String tipe_user, String tipe_tempat, String foto_sim, String foto_imb) {
        this.nama_pengurus = nama_pengurus;
        this.alamat_pengurus = alamat_pengurus;
        this.nama_masjid = nama_masjid;
        this.alamat_masjid = alamat_masjid;
        this.phone = phone;
        this.email = email;
        this.foto_tempat = foto_tempat;
        this.foto_ktp = foto_ktp;
        this.foto_surat_pernyataan = foto_surat_pernyataan;
        this.verified = verified;
        this.tipe_user = tipe_user;
        this.tipe_tempat = tipe_tempat;
        this.foto_sim = foto_sim;
        this.foto_imb = foto_imb;
    }


}
