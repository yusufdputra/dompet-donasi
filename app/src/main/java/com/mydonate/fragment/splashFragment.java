package com.mydonate.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mydonate.activity.AuthActivity;
import com.mydonate.activity.HomeActivity;

public class splashFragment extends Fragment {
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener listener;

    public splashFragment() {
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //cek user
        listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                //cek user sudah login atau belum
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    Intent intent = new Intent(getActivity(), AuthActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        };
        super.onViewCreated(view, savedInstanceState);
    }


    //menerapkan listener
    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(listener);
    }

    //lepas listener
    @Override
    public void onStop() {
        super.onStop();
        if (listener != null) {
            auth.removeAuthStateListener(listener);
        }
    }
}
