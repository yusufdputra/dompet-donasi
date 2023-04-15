package com.mydonate.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mydonate.R;
import com.mydonate.fragment.DetailVerifikasiAkunPengurusFragment;
import com.mydonate.fragment.LoginFragment;

public class cekUserSessionActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener listener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loadingsplash);

        auth = FirebaseAuth.getInstance();

        listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                //cek user sudah login atau belum
                FirebaseUser user = firebaseAuth.getCurrentUser();

                FirebaseMessaging.getInstance().subscribeToTopic("nope");
                if (user != null) {
                    //cek tipe user
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    String idUser = currentUser.getUid();
                    DatabaseReference jLoginDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(idUser);
                    jLoginDatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String tipe = snapshot.child("tipe_user").getValue(String.class);

                            //handle untuk donatur
                            if (tipe.equals(LoginFragment.DONATUR_LOGIN)) {
                                if (!currentUser.isEmailVerified()) {
                                    GoToAuthAct();
                                } else {
                                    FirebaseMessaging.getInstance().subscribeToTopic("all");

                                    Intent intent = new Intent(cekUserSessionActivity.this, HomeActivity.class);
                                    intent.putExtra(HomeActivity.LOGIN_TYPE_KEY_EXTRA, LoginFragment.DONATUR_LOGIN);
                                    startActivity(intent);
                                    cekUserSessionActivity.this.finish();
                                }
                            } else if (tipe.equals(LoginFragment.ADMIN_LOGIN)) { // handle admin
                                Intent intent = new Intent(cekUserSessionActivity.this, HomeActivity.class);
                                intent.putExtra(HomeActivity.LOGIN_TYPE_KEY_EXTRA, LoginFragment.ADMIN_LOGIN);
                                startActivity(intent);
                                cekUserSessionActivity.this.finish();
                            } else if (tipe.equals(LoginFragment.PENGURUS_LOGIN)) { // handle pengurus
                                Boolean status = snapshot.child("verified").getValue(Boolean.class);
                                if (status == DetailVerifikasiAkunPengurusFragment.KEY_APPROVE) {
                                    Intent intent = new Intent(cekUserSessionActivity.this, HomeActivity.class);
                                    intent.putExtra(HomeActivity.LOGIN_TYPE_KEY_EXTRA, LoginFragment.PENGURUS_LOGIN);
                                    startActivity(intent);
                                    cekUserSessionActivity.this.finish();
                                } else {
                                    GoToAuthAct();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                } else {
                    GoToAuthAct();
                }
            }

            private void GoToAuthAct() {
                Intent intent = new Intent(cekUserSessionActivity.this, SlideViewPagerActivity.class);
                startActivity(intent);
                cekUserSessionActivity.this.finish();
            }
        };
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
