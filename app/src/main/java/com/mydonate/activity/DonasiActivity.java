package com.mydonate.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.mydonate.R;
import com.mydonate.data.ProgramUmumData;
import com.mydonate.fragment.DonasiMasjidFragment;
import com.mydonate.fragment.DonasiMusholaFragment;
import com.mydonate.fragment.DonasiProgramUmumFragment;
import com.mydonate.fragment.DonasiUmumFragment;
import com.mydonate.fragment.DonasikuFragment;
import com.mydonate.fragment.HomeDonaturFragment;
import com.mydonate.fragment.HomeAdminFragment;
import com.mydonate.fragment.KelolaDanaUmumFragment;
import com.mydonate.fragment.KelolaProgramUmumFragment;
import com.mydonate.fragment.RiwayatDonasiFragment;
import com.mydonate.fragment.laporanKeuangan.DetailLaporanKeuanganFragment;

public class DonasiActivity extends AppCompatActivity {
    public static final String DONATION_TYPE_KEY_EXTRA = "DonationType";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Intent intent = getIntent();
        if (intent != null) {
            switch (intent.getStringExtra(DONATION_TYPE_KEY_EXTRA)) {
                case HomeDonaturFragment.DONATION_TYPE_MASJID_KEY_EXTRA:
                    DonasiMasjidFragment donasiMasjidFragment = new DonasiMasjidFragment();
                    fragmentTransaction.add(R.id.home_frame_layout, donasiMasjidFragment, DonasiMasjidFragment.class.getSimpleName()).commit();
                    break;
                case HomeDonaturFragment.DONATION_TYPE_MUSHOLA_KEY_EXTRA:
                    DonasiMusholaFragment donasiMusholaFragment = new DonasiMusholaFragment();
                    fragmentTransaction.add(R.id.home_frame_layout, donasiMusholaFragment, DonasiMusholaFragment.class.getSimpleName()).commit();
                    break;
                case HomeDonaturFragment.DONATION_TYPE_BANNER:
                    Bundle bundle = new Bundle();
                    bundle.putString(HomeDonaturFragment.DONATION_TYPE_BANNER, HomeDonaturFragment.DONATION_TYPE_BANNER);
                    DonasiMasjidFragment donasiBanner = new DonasiMasjidFragment();
                    donasiBanner.setArguments(bundle);
                    fragmentTransaction.add(R.id.home_frame_layout, donasiBanner, DonasiMasjidFragment.class.getSimpleName()).commit();
                    break;
                case HomeDonaturFragment.DONATION_TYPE_UMUM_KEY_EXTRA:
                    DonasiProgramUmumFragment programUmumFragment = new DonasiProgramUmumFragment();
                    fragmentTransaction.add(R.id.home_frame_layout, programUmumFragment, DonasiProgramUmumFragment.class.getSimpleName()).commit();
                    break;
                case HomeDonaturFragment.USER_DONATION_KEY_EXTRA:
                    RiwayatDonasiFragment riwayatDonasiFragment = new RiwayatDonasiFragment();
                    fragmentTransaction.add(R.id.home_frame_layout, riwayatDonasiFragment, RiwayatDonasiFragment.class.getSimpleName()).commit();
                    break;
                case HomeDonaturFragment.USER_DONASIKU_KEY_EXTRA:
                    DonasikuFragment donasikuFragment = new DonasikuFragment();
                    fragmentTransaction.add(R.id.home_frame_layout, donasikuFragment, DonasikuFragment.class.getSimpleName()).commit();
                    break;
                case HomeAdminFragment.KEY_KELOLA_DU:
                    KelolaDanaUmumFragment kelolaDanaUmumFragment = new KelolaDanaUmumFragment();
                    fragmentTransaction.add(R.id.home_frame_layout, kelolaDanaUmumFragment, KelolaDanaUmumFragment.class.getSimpleName()).commit();
                    break;
                case HomeAdminFragment.KEY_KELOLA_PU:
                    DonasiProgramUmumFragment programUmumFragment2 = new DonasiProgramUmumFragment();
                    fragmentTransaction.add(R.id.home_frame_layout, programUmumFragment2, DonasiProgramUmumFragment.class.getSimpleName()).commit();
                    break;
                case HomeAdminFragment.KEY_LAPORAN_KEUANGAN:
                    DetailLaporanKeuanganFragment detailLaporanKeuanganFragment = new DetailLaporanKeuanganFragment();
                    fragmentTransaction.add(R.id.home_frame_layout, detailLaporanKeuanganFragment, DetailLaporanKeuanganFragment.class.getSimpleName()).commit();
                    break;
            }
        }
    }
}