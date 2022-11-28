package com.mydonate.data;

import java.util.ArrayList;

public class MasjidData {
    private static String[] daftarNamaMasjid = {
            "Masjid Baitul Rahman",
            "Masjid Ar-Rahman",
            "Masjid Ar-Rahim",
            "Masjid Ar-Rahman",
            "Masjid Ar-Rahim",
            "Masjid Ar-Rahman",
            "Masjid Ar-Rahim",
            "Masjid Ar-Rahman",
            "Masjid Ar-Rahim",
            "Masjid Ar-Rahman",
            "Masjid Ar-Rahim",
    };

    private static String[] daftarKebutuhanDonasi = {
            "Karpet Sajadah, Toa, Microphone",
            "Microphone, Karpet Sajadah",
            "Toa, Microphone",
            "Microphone, Karpet Sajadah",
            "Toa, Microphone",
            "Microphone, Karpet Sajadah",
            "Toa, Microphone",
            "Microphone, Karpet Sajadah",
            "Toa, Microphone",
            "Karpet Sajadah, Toa, Microphone",
            "Microphone, Karpet Sajadah"
    };

    public static ArrayList<MasjidMushola> getDaftarMasjid() {

        ArrayList<MasjidMushola> daftarMasjid = new ArrayList<MasjidMushola>();

        for (int position = 0; position < daftarNamaMasjid.length; position++){
            MasjidMushola masjid = new MasjidMushola();
            masjid.setNamaMasjidMushola(daftarNamaMasjid[position]);
            masjid.setKebutuhanDonasi(daftarKebutuhanDonasi[position]);
            daftarMasjid.add(masjid);
        }
        return daftarMasjid;
    };
}
