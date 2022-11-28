package com.mydonate.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.mydonate.R;
import com.mydonate.fragment.HomeAdminFragment;
import com.mydonate.fragment.KelolaDanaUmumFragment;
import com.mydonate.fragment.PembayaranFragment;
import com.mydonate.fragment.VerifikasiAkunFragment;
import com.mydonate.fragment.laporanKeuangan.ListLaporanKeuanganFragment;
import com.mydonate.fragment.pencairan.PengajuanPencairanDanaFragment;

public class AdminActivity extends AppCompatActivity {

    public static final String ACTIVITY_TYPE_KEY_EXTRA = "Aktivity" ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Intent intent = getIntent();
        if (intent != null) {

            switch (intent.getStringExtra(ACTIVITY_TYPE_KEY_EXTRA)) {
                case HomeAdminFragment.KEY_VERIFIKASI_AKUN:
                    VerifikasiAkunFragment verifikasiAkunFragment = new VerifikasiAkunFragment();
                    fragmentTransaction.add(R.id.admin_frame_layout, verifikasiAkunFragment, VerifikasiAkunFragment.class.getSimpleName()).commit();
                    break;
                case HomeAdminFragment.KEY_KELOLA_DU:
                    KelolaDanaUmumFragment kelolaDanaUmumFragment = new KelolaDanaUmumFragment();
                    fragmentTransaction.add(R.id.admin_frame_layout, kelolaDanaUmumFragment, KelolaDanaUmumFragment.class.getSimpleName()).commit();
                    break;
                case HomeAdminFragment.KEY_UPLOAD:
                    PembayaranFragment pembayaranFragment = new PembayaranFragment();
                    fragmentTransaction.add(R.id.admin_frame_layout, pembayaranFragment, PembayaranFragment.class.getSimpleName()).commit();
                    break;
                case HomeAdminFragment.KEY_PENCAIRAN:
                    PengajuanPencairanDanaFragment pengajuanPencairanDana = new PengajuanPencairanDanaFragment();
                    fragmentTransaction.add(R.id.admin_frame_layout, pengajuanPencairanDana, PengajuanPencairanDanaFragment.class.getSimpleName()).commit();
                    break;
                case HomeAdminFragment.KEY_LAPORAN_KEUANGAN:
                    ListLaporanKeuanganFragment listLaporanKeuanganFragment = new ListLaporanKeuanganFragment();
                    fragmentTransaction.add(R.id.admin_frame_layout, listLaporanKeuanganFragment, ListLaporanKeuanganFragment.class.getSimpleName()).commit();
                    break;
            }
        }
    }
}
