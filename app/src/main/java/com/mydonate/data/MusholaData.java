package com.mydonate.data;

import java.util.ArrayList;

public class MusholaData {
    private static String[] daftarNamaMushola = {
            "Mushola Baitul Rahman",
            "Mushola Ar-Rahman",
            "Mushola Ar-Rahim",
            "Mushola Ar-Rahman",
            "Mushola Ar-Rahim",
            "Mushola Ar-Rahman",
            "Mushola Ar-Rahim",
            "Mushola Ar-Rahman",
            "Mushola Ar-Rahim",
            "Mushola Ar-Rahman",
            "Mushola Ar-Rahim",
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

    public static ArrayList<MasjidMushola> getDaftarMushola() {

        ArrayList<MasjidMushola> daftarMushola = new ArrayList<MasjidMushola>();

        for (int position = 0; position < daftarNamaMushola.length; position++){
            MasjidMushola mushola = new MasjidMushola();
            mushola.setNamaMasjidMushola(daftarNamaMushola[position]);
            mushola.setKebutuhanDonasi(daftarKebutuhanDonasi[position]);
            daftarMushola.add(mushola);
        }
        return daftarMushola;
    };
}
