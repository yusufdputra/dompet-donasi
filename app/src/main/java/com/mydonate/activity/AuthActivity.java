package com.mydonate.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.mydonate.R;
import com.mydonate.fragment.LoginFragment;

public class AuthActivity extends AppCompatActivity {

    private CoordinatorLayout coordinatorLayout;
    private TextInputEditText etEmail, etPassword;
    private String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        Intent intent = getIntent();
        String msg = intent.getStringExtra(LoginFragment.KEY_MSG);

        Bundle bundle = new Bundle();
        bundle.putString(LoginFragment.KEY_MSG, msg);
        LoginFragment loginFragment = new LoginFragment();
        loginFragment.setArguments(bundle);


        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        LoginFragment authFragment = new LoginFragment();
        fragmentTransaction.add(R.id.auth_frame_layout, loginFragment, LoginFragment.class.getSimpleName()).commit();
    }
}