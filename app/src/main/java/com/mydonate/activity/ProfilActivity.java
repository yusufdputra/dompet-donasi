package com.mydonate.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.mydonate.R;
import com.mydonate.fragment.DetailMasjidMusholaFragment;
import com.mydonate.fragment.DetailProfileDonatur;
import com.mydonate.fragment.DetailProfilePengurus;
import com.mydonate.fragment.HomeAdminFragment;
import com.mydonate.fragment.HomeDonaturFragment;
import com.mydonate.fragment.HomePengurusFragment;
import com.mydonate.fragment.LoginFragment;

public class ProfilActivity extends AppCompatActivity {
  public static final String LOGIN_TYPE_KEY_EXTRA = "LoginType";
  private String edit_kebutuhan_key = "edit";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
//        adjustFontScale(getResources().getConfiguration());
    setContentView(R.layout.activity_profil);

    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

    Intent intent = getIntent();
    if (intent != null) {

      switch (intent.getStringExtra(LOGIN_TYPE_KEY_EXTRA)) {
        case LoginFragment.DONATUR_LOGIN:
          DetailProfileDonatur detailProfileDonatur = new DetailProfileDonatur();
          fragmentTransaction.add(R.id.profil_frame_layout, detailProfileDonatur, DetailProfileDonatur.class.getSimpleName()).commit();
          break;
        case LoginFragment.PENGURUS_LOGIN:
          Bundle bundle = new Bundle();
          bundle.putString("KEY_EDIT_KEB", edit_kebutuhan_key);
          DetailProfilePengurus detailProfilePengurus = new DetailProfilePengurus();
          detailProfilePengurus.setArguments(bundle);
          fragmentTransaction.add(R.id.profil_frame_layout, detailProfilePengurus, DetailProfilePengurus.class.getSimpleName()).commit();
          break;

      }
    }
  }

  public void adjustFontScale(Configuration configuration) {
    configuration.fontScale = (float) 1.0;
    DisplayMetrics metrics = getResources().getDisplayMetrics();
    WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
    wm.getDefaultDisplay().getMetrics(metrics);
    metrics.scaledDensity = configuration.fontScale * metrics.density;
    getBaseContext().getResources().updateConfiguration(configuration, metrics);
  }
}