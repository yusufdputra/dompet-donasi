package com.mydonate.data;

import java.util.ArrayList;

public class BayarKebutuhanData {
    private String sisa_nominal_kebutuhan, edit_kebutuhan;
    private ArrayList<String> foto_bukti_donasi;

    public BayarKebutuhanData() {
    }

    public BayarKebutuhanData(String sisa_nominal_kebutuhan, ArrayList<String> foto_bukti_donasi, String edit_kebutuhan) {
        this.sisa_nominal_kebutuhan = sisa_nominal_kebutuhan;
        this.foto_bukti_donasi = foto_bukti_donasi;
        this.edit_kebutuhan = edit_kebutuhan;
    }

    public String getEdit_kebutuhan() {
        return edit_kebutuhan;
    }

    public void setEdit_kebutuhan(String edit_kebutuhan) {
        this.edit_kebutuhan = edit_kebutuhan;
    }

    public String getSisa_nominal_kebutuhan() {
        return sisa_nominal_kebutuhan;
    }

    public void setSisa_nominal_kebutuhan(String sisa_nominal_kebutuhan) {
        this.sisa_nominal_kebutuhan = sisa_nominal_kebutuhan;
    }

    public ArrayList<String> getFoto_bukti_donasi() {
        return foto_bukti_donasi;
    }

    public void setFoto_bukti_donasi(ArrayList<String> foto_bukti_donasi) {
        this.foto_bukti_donasi = foto_bukti_donasi;
    }
}
