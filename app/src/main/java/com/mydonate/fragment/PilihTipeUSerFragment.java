package com.mydonate.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.mydonate.R;

public class PilihTipeUSerFragment extends Fragment implements View.OnClickListener {

    private ImageView donatur, pengurus, iv_back;
    Animation TopAnim, BottomAnim;
    private LinearLayout LLDonatur, LLPengurus;

    public PilihTipeUSerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pilih_tipe_user, container, false);
        init(view);
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setClickListener();

    }
    private void setClickListener(){
        donatur.setOnClickListener(this);
        pengurus.setOnClickListener(this);
        iv_back.setOnClickListener(this);
    }

    private void init(View view) {
        donatur = view.findViewById(R.id.img_donatur);
        pengurus = view.findViewById(R.id.img_pengurus);
        iv_back = view.findViewById(R.id.iv_backTipeUser);
        LLDonatur = view.findViewById(R.id.llDonatur);
        LLPengurus = view.findViewById(R.id.llPengurus);

        //animasi
        TopAnim = AnimationUtils.loadAnimation(getContext(),R.anim.top_animation);
        BottomAnim = AnimationUtils.loadAnimation(getContext(),R.anim.bottom_animation);

        LLDonatur.setAnimation(TopAnim);
        LLPengurus.setAnimation(BottomAnim);
    }



    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.img_donatur:
                SignUpDonaturFragment signUpDonaturFragment = new SignUpDonaturFragment();
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.auth_frame_layout, signUpDonaturFragment)
                            .addToBackStack(null)
                            .commit();
                } else {
                    Snackbar snackbar = Snackbar
                            .make(v, "Terjadi kesalahan", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
                break;
            case R.id.img_pengurus:
                SignUpPengurusFragment signUpPengurusFragment = new SignUpPengurusFragment();
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.auth_frame_layout, signUpPengurusFragment)
                            .addToBackStack(null)
                            .commit();
                } else {
                    Snackbar snackbar = Snackbar
                            .make(v, "Terjadi kesalahan", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
                break;
            case R.id.iv_backTipeUser:
                getActivity().onBackPressed();
                break;
        }
    }
}
