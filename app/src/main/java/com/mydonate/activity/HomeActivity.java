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
import com.mydonate.fragment.HomeAdminFragment;
import com.mydonate.fragment.HomeDonaturFragment;
import com.mydonate.fragment.HomePengurusFragment;
import com.mydonate.fragment.LoginFragment;

public class HomeActivity extends AppCompatActivity {
    public static final String LOGIN_TYPE_KEY_EXTRA = "LoginType";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        adjustFontScale(getResources().getConfiguration());
        setContentView(R.layout.activity_home);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Intent intent = getIntent();
        if (intent != null) {

            switch (intent.getStringExtra(LOGIN_TYPE_KEY_EXTRA)) {
                case LoginFragment.ADMIN_LOGIN:
                    HomeAdminFragment homeAdminFragment = new HomeAdminFragment();
                    fragmentTransaction.add(R.id.home_frame_layout, homeAdminFragment, HomeAdminFragment.class.getSimpleName()).commit();
                    break;
                case LoginFragment.DONATUR_LOGIN:
                    HomeDonaturFragment homeDonaturFragment = new HomeDonaturFragment();
                    fragmentTransaction.add(R.id.home_frame_layout, homeDonaturFragment, HomeDonaturFragment.class.getSimpleName()).commit();
                    break;
                case LoginFragment.PENGURUS_LOGIN:
                    HomePengurusFragment homePengurusFragment = new HomePengurusFragment();
                    fragmentTransaction.add(R.id.home_frame_layout, homePengurusFragment, HomePengurusFragment.class.getSimpleName()).commit();
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