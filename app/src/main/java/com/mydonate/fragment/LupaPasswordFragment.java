package com.mydonate.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.mydonate.R;

public class LupaPasswordFragment extends Fragment implements View.OnClickListener {
    private ImageView iv_back;
    private TextInputLayout error_email;
    private EditText email;
    private Button btn_reset;
    private ProgressBar progressBar;
    private FirebaseAuth auth;

    public LupaPasswordFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lupa_password, container, false);
        init(view);
        setOnClickListener();
        return view;
    }

    private void setOnClickListener() {
        iv_back.setOnClickListener(this);
        btn_reset.setOnClickListener(this);
    }

    private void init(View view) {
        iv_back = view.findViewById(R.id.iv_back);
        error_email = view.findViewById(R.id.error_email);
        email = view.findViewById(R.id.et_email);
        btn_reset = view.findViewById(R.id.btn_reset);
        progressBar = view.findViewById(R.id.progressBar);

        auth =  FirebaseAuth.getInstance();
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

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.iv_back:
                getActivity().onBackPressed();
                break;
            case R.id.btn_reset:

                if (!validasiEmail()){
                    return;
                }else{
                    progressBar.setVisibility(View.VISIBLE);
                    String get_email = email.getText().toString();
                    auth.sendPasswordResetEmail(get_email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(getContext(), "Berhasil mengirim reset password. Silahkan cek email anda.", Toast.LENGTH_LONG).show();
                                        getActivity().onBackPressed();
                                    }else{
                                        Toast.makeText(getContext(), "Gagal mengirim reset password.", Toast.LENGTH_SHORT).show();
                                    }
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                }
                break;
        }
    }
}
