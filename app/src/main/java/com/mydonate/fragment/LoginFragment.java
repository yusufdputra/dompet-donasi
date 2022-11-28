package com.mydonate.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mydonate.R;
import com.mydonate.activity.HomeActivity;
import com.mydonate.component.NumberTextWatcher;

import static android.content.ContentValues.TAG;

public class LoginFragment extends Fragment implements View.OnClickListener {
    public static final String ADMIN_LOGIN = "Admin";
    public static final String PENGURUS_LOGIN = "Pengurus";
    public static final String DONATUR_LOGIN = "Donatur";
    public static final String KEY_MSG = "SIGN_MSG";
    private EditText etEmail, etPassword;
    private Button btnLogin, btn_user_pengurus, btn_user_donatur;
    private TextView tvSignup, tv_msg, tvforgot_password;
    private TextInputLayout error_email, error_password;
    //private View tv_msg;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener listener;
    private String TIPE_USER;


    //set untuk pop up TIPE_USER
    private View popUpDialogView;


    public LoginFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_login, container, false);
        init(view);

        //cek msg
        tv_msg.setVisibility(View.VISIBLE);
        if (getArguments() != null) {
            String msg = this.getArguments().getString(KEY_MSG);
            tv_msg.setText(msg);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setClickListener();

        //cek user
        listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                //cek user sudah login atau belum
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    CekTipeUserLogin();
                }
            }
        };
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_login:
                doLogin(v);
                break;
            case R.id.tv_sign_up:
                showPilihUser();
//                PilihTipeUSerFragment pilihTipeUSerFragment = new PilihTipeUSerFragment();
//                if (getActivity() != null) {
//                    getActivity().getSupportFragmentManager().beginTransaction()
//                            .replace(R.id.auth_frame_layout, pilihTipeUSerFragment)
//                            .addToBackStack(null)
//                            .commit();
//                } else {
//                    Snackbar snackbar = Snackbar
//                            .make(v, "Terjadi kesalahan", Snackbar.LENGTH_LONG);
//                    snackbar.show();
//                }
                break;
            case R.id.tv_forgot_password:
                LupaPasswordFragment lupaPasswordFragment = new LupaPasswordFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.auth_frame_layout, lupaPasswordFragment)
                        .addToBackStack(null)
                        .commit();
                break;
        }
    }

    private void showPilihUser() {
        android.app.AlertDialog.Builder alerBuilder = new android.app.AlertDialog.Builder(getActivity());
        alerBuilder.setTitle("Daftar Sebagai?");
        alerBuilder.setIcon(R.drawable.ic_baseline_playlist_add_24);

        // Init popup dialog view and it's ui controls
        initPopupViewControls();
        // Set the inflated layout view object to the AlertDialog builder
        alerBuilder.setView(popUpDialogView);
        // Create AlertDialog and show.
        final android.app.AlertDialog alertDialog = alerBuilder.create();
        alertDialog.show();

        btn_user_pengurus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                SignUpPengurusFragment signUpPengurusFragment = new SignUpPengurusFragment();
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.auth_frame_layout, signUpPengurusFragment)
                            .addToBackStack(null)
                            .commit();
                } else {
                    Snackbar snackbar = Snackbar
                            .make(view, "Terjadi kesalahan", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });

        btn_user_donatur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                SignUpDonaturFragment signUpDonaturFragment = new SignUpDonaturFragment();
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.auth_frame_layout, signUpDonaturFragment)
                            .addToBackStack(null)
                            .commit();
                } else {
                    Snackbar snackbar = Snackbar
                            .make(view, "Terjadi kesalahan", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });
    }

    private void setClickListener() {
        btnLogin.setOnClickListener(this);
        tvSignup.setOnClickListener(this);
        tvforgot_password.setOnClickListener(this);
    }

    private void init(View view) {
        etEmail = view.findViewById(R.id.et_email);
        etPassword = view.findViewById(R.id.et_password);
        btnLogin = view.findViewById(R.id.btn_login);
        tvSignup = view.findViewById(R.id.tv_sign_up);
        tv_msg = view.findViewById(R.id.tv_msg);
        progressBar = view.findViewById(R.id.progressBar);
        tvforgot_password = view.findViewById(R.id.tv_forgot_password);
        tv_msg.setVisibility(View.GONE);

        auth = FirebaseAuth.getInstance();

        error_email = view.findViewById(R.id.error_email);
        error_password = view.findViewById(R.id.error_password);


    }




    private void doLogin(View v) {
        progressBar.setVisibility(View.VISIBLE);

        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();


        if (!validasiEmail() | !validasiPassword()) {
            progressBar.setVisibility(View.GONE);
            return;
        } else {

            auth.signInWithEmailAndPassword(email, password)

                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {

                                CekTipeUserLogin();


                            } else {
                                tv_msg.setVisibility(View.VISIBLE);
                                tv_msg.setText("Email dan Password yang anda masukkan salah. Coba lagi!");

                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });

        }
    }

    private void CekTipeUserLogin() {
        //cek tipe user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String idUser = currentUser.getUid();
        DatabaseReference jLoginDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(idUser);
        jLoginDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String tipe = snapshot.child("tipe_user").getValue(String.class);

                //handle untuk donatur
                if (tipe.equals(DONATUR_LOGIN)) {
                    if (!currentUser.isEmailVerified()) {
                        tv_msg.setVisibility(View.VISIBLE);
                        tv_msg.setText("Akun anda belum dilakukan verifikasi, silahkan cek Email anda.");
                        progressBar.setVisibility(View.GONE);
                    } else {
                        Intent intent = new Intent(getActivity(), HomeActivity.class);
                        intent.putExtra(HomeActivity.LOGIN_TYPE_KEY_EXTRA, DONATUR_LOGIN);
                        startActivity(intent);
                        getActivity().finish();
                    }
                } else if (tipe.equals(ADMIN_LOGIN)) { // handle admin
                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                    intent.putExtra(HomeActivity.LOGIN_TYPE_KEY_EXTRA, ADMIN_LOGIN);
                    startActivity(intent);
                    getActivity().finish();
                } else if (tipe.equals(PENGURUS_LOGIN)) { // handle pengurus
                    Boolean status = snapshot.child("verified").getValue(Boolean.class);
                    if (status == DetailVerifikasiAkunPengurusFragment.KEY_APPROVE) {
                        Intent intent = new Intent(getActivity(), HomeActivity.class);
                        intent.putExtra(HomeActivity.LOGIN_TYPE_KEY_EXTRA, PENGURUS_LOGIN);
                        startActivity(intent);
                        getActivity().finish();
                    } else {
                        tv_msg.setVisibility(View.VISIBLE);
                        tv_msg.setText("Akun anda belum dilakukan verifikasi, silahkan tunggu email dari Admin.");
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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

    private boolean validasiEmail() {
        String email = error_email.getEditText().getText().toString().trim();
        if (email.isEmpty()) {
            error_email.setError("Field tidak boleh kosong");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            error_email.setError("Masukkan alamat email yang benar");
            return false;
        } else {
            error_email.setError(null);
            return true;
        }
    }

    private boolean validasiPassword() {
        String pw = error_password.getEditText().getText().toString().trim();
        if (pw.isEmpty()) {
            error_password.setError("Field tidak boleh kosong");
            return false;
        } else if (!SignUpPengurusFragment.PASSWORD_PATTERN.matcher(pw).matches()) {
            error_password.setError("Password lemah. Harus terdiri dari 1 [0-9] dan [a-zA-Z]. Minimal 4 karakter.");
            return false;
        } else if (pw.length() < 6) {
            error_password.setError("Minimal 4 karakter.");
            return false;
        } else {
            error_password.setError(null);
            return true;
        }
    }

    @SuppressLint("CutPasteId")
    private void initPopupViewControls() {
        //get layout
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        popUpDialogView = layoutInflater.inflate(R.layout.popup_tipe_user, null);

        btn_user_donatur = popUpDialogView.findViewById(R.id.btn_donatur);
        btn_user_pengurus = popUpDialogView.findViewById(R.id.btn_pengurus);

    }

}
