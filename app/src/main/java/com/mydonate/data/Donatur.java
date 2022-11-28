package com.mydonate.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Donatur implements Parcelable {
    private String namaDonatur;
    private String jumlahDonasi;
    private String kebutuhanDonasi;
    private String alamat;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.namaDonatur);
        dest.writeString(this.jumlahDonasi);
        dest.writeString(this.kebutuhanDonasi);
        dest.writeString(this.alamat);
    }

    public Donatur() {
    }

    protected Donatur(Parcel in) {
        this.namaDonatur = in.readString();
        this.jumlahDonasi = in.readString();
        this.kebutuhanDonasi = in.readString();
        this.alamat = in.readString();
    }

    public static final Parcelable.Creator<Donatur> CREATOR = new Parcelable.Creator<Donatur>() {
        @Override
        public Donatur createFromParcel(Parcel source) {
            return new Donatur(source);
        }

        @Override
        public Donatur[] newArray(int size) {
            return new Donatur[size];
        }
    };

    public String getNamaDonatur() {
        return namaDonatur;
    }

    public void setNamaDonatur(String namaDonatur) {
        this.namaDonatur = namaDonatur;
    }

    public String getJumlahDonasi() {
        return jumlahDonasi;
    }

    public void setJumlahDonasi(String jumlahDonasi) {
        this.jumlahDonasi = jumlahDonasi;
    }

    public String getKebutuhanDonasi() {
        return kebutuhanDonasi;
    }

    public void setKebutuhanDonasi(String kebutuhanDonasi) {
        this.kebutuhanDonasi = kebutuhanDonasi;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }
}
