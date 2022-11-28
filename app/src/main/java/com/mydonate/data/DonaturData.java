package com.mydonate.data;

import java.util.ArrayList;

public class DonaturData {
    private static String[] daftarNamaDonatur = {
            "Adi Hidayat",
            "Hamba Allah",
            "Hamba Allah",
            "Hamba Allah",
            "Hamba Allah",
            "Hamba Allah",
            "Hamba Allah",
            "Hamba Allah",
            "Hamba Allah",
            "Hamba Allah",
            "Hamba Allah"
    };

    private static String[] daftarJumlahDonasi = {
            "200.000",
            "200.000",
            "200.000",
            "200.000",
            "200.000",
            "200.000",
            "200.000",
            "200.000",
            "200.000",
            "200.000",
            "200.000"
    };

    private static String[] daftarKebutuhanDonasi = {
            "Kebutuhan Pembelian Karpet Sajadah - MasjidMushola Al-Ikhlas",
            "Kebutuhan Pembelian Karpet Sajadah - MasjidMushola Al-Ikhlas",
            "Kebutuhan Pembelian Karpet Sajadah - MasjidMushola Al-Ikhlas",
            "Kebutuhan Pembelian Karpet Sajadah - MasjidMushola Al-Ikhlas",
            "Kebutuhan Pembelian Karpet Sajadah - MasjidMushola Al-Ikhlas",
            "Kebutuhan Pembelian Karpet Sajadah - MasjidMushola Al-Ikhlas",
            "Kebutuhan Pembelian Karpet Sajadah - MasjidMushola Al-Ikhlas",
            "Kebutuhan Pembelian Karpet Sajadah - MasjidMushola Al-Ikhlas",
            "Kebutuhan Pembelian Karpet Sajadah - MasjidMushola Al-Ikhlas",
            "Kebutuhan Pembelian Karpet Sajadah - MasjidMushola Al-Ikhlas",
            "Kebutuhan Pembelian Karpet Sajadah - MasjidMushola Al-Ikhlas"
    };

    private static String[] daftarAlamat = {
            "Jl. Merpati Sakti, Panam Pekanbaru",
            "Jl. Merpati Sakti, Panam Pekanbaru",
            "Jl. Merpati Sakti, Panam Pekanbaru",
            "Jl. Merpati Sakti, Panam Pekanbaru",
            "Jl. Merpati Sakti, Panam Pekanbaru",
            "Jl. Merpati Sakti, Panam Pekanbaru",
            "Jl. Merpati Sakti, Panam Pekanbaru",
            "Jl. Merpati Sakti, Panam Pekanbaru",
            "Jl. Merpati Sakti, Panam Pekanbaru",
            "Jl. Merpati Sakti, Panam Pekanbaru",
            "Jl. Merpati Sakti, Panam Pekanbaru"
    };

    public static ArrayList<Donatur> getRiwayatDonasi() {

        ArrayList<Donatur> riwayatDonasi = new ArrayList<Donatur>();

        for (int position = 0; position < daftarNamaDonatur.length; position++){
            Donatur donatur = new Donatur();
            donatur.setNamaDonatur(daftarNamaDonatur[position]);
            donatur.setJumlahDonasi(daftarJumlahDonasi[position]);
            donatur.setKebutuhanDonasi(daftarKebutuhanDonasi[position]);
            donatur.setAlamat(daftarAlamat[position]);
            riwayatDonasi.add(donatur);
        }
        return riwayatDonasi;
    };

    public static ArrayList<Donatur> getLastThreeRiwayatDonasi() {

        ArrayList<Donatur> riwayatDonasi = new ArrayList<Donatur>();

        for (int position = 0; position < 3; position++){
            Donatur donatur = new Donatur();
            donatur.setNamaDonatur(daftarNamaDonatur[position]);
            donatur.setJumlahDonasi(daftarJumlahDonasi[position]);
            donatur.setKebutuhanDonasi(daftarKebutuhanDonasi[position]);
            donatur.setAlamat(daftarAlamat[position]);
            riwayatDonasi.add(donatur);
        }
        return riwayatDonasi;
    };
}
