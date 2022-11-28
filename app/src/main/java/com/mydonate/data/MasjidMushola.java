package com.mydonate.data;

import android.os.Parcel;
import android.os.Parcelable;

public class MasjidMushola implements Parcelable {
    private String namaMasjidMushola;
    private String kebutuhanDonasi;

    public String getNamaMasjidMushola() {
        return namaMasjidMushola;
    }

    public void setNamaMasjidMushola(String namaMasjidMushola) {
        this.namaMasjidMushola = namaMasjidMushola;
    }

    public String getKebutuhanDonasi() {
        return kebutuhanDonasi;
    }

    public void setKebutuhanDonasi(String kebutuhanDonasi) {
        this.kebutuhanDonasi = kebutuhanDonasi;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.namaMasjidMushola);
        dest.writeString(this.kebutuhanDonasi);
    }

    public MasjidMushola() {
    }

    protected MasjidMushola(Parcel in) {
        this.namaMasjidMushola = in.readString();
        this.kebutuhanDonasi = in.readString();
    }

    public static final Parcelable.Creator<MasjidMushola> CREATOR = new Parcelable.Creator<MasjidMushola>() {
        @Override
        public MasjidMushola createFromParcel(Parcel source) {
            return new MasjidMushola(source);
        }

        @Override
        public MasjidMushola[] newArray(int size) {
            return new MasjidMushola[size];
        }
    };
}
