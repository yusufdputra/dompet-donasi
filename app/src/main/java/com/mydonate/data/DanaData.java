package com.mydonate.data;

public class DanaData {
    private String jenis_dana, id_pengurus;
    int total_dana;

    public DanaData() {
    }

    public DanaData(String jenis_dana, String id_pengurus, int total_dana) {
        this.jenis_dana = jenis_dana;
        this.id_pengurus = id_pengurus;
        this.total_dana = total_dana;
    }

    public String getJenis_dana() {
        return jenis_dana;
    }

    public void setJenis_dana(String jenis_dana) {
        this.jenis_dana = jenis_dana;
    }

    public String getId_pengurus() {
        return id_pengurus;
    }

    public void setId_pengurus(String id_pengurus) {
        this.id_pengurus = id_pengurus;
    }

    public int getTotal_dana() {
        return total_dana;
    }

    public void setTotal_dana(int total_dana) {
        this.total_dana = total_dana;
    }
}
