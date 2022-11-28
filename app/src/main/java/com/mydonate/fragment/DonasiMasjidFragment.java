package com.mydonate.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mydonate.R;
import com.mydonate.activity.DonasiActivity;
import com.mydonate.adapter.DaftarMasjidMusholaAdapter;
import com.mydonate.data.KebutuhanData;
import com.mydonate.data.PengurusData;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class DonasiMasjidFragment extends Fragment implements View.OnClickListener {
    private static final String KEY_MASJID = "Masjid";
    private static final String KEY_MUSHOLA = "Mushola";
    private ConstraintLayout bottomSheetLayout;
    private BottomSheetBehavior<ConstraintLayout> sheetBehavior;
    private View topLayout;
    private TextView titleLayout, donasiku;
    private RecyclerView rvDaftarMasjid;
    private ImageView ivBack;
    private Boolean banner = false;

    private ArrayList<PengurusData> pengurusData;
    private ArrayList<KebutuhanData> kebutuhanData;
    private ArrayList<String> keyItem = new ArrayList<>();
    private int screenHeight, screenWidth, height;
    private Query DbRef, ref_dana;
    private DatabaseReference ref_users;

    public DonasiMasjidFragment() {
    }

    public static float convertPixelsToDp(float px, Context context) {
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_donasi_masjid, container, false);
        init(view);
        setClickListener();

        if (getArguments() != null) {
            banner = true;
        }

        //cek tipe user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String idUser = currentUser.getUid();
        DatabaseReference jLoginDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(idUser);
        jLoginDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String tipe = snapshot.child("tipe_user").getValue(String.class);
                if (tipe.equals(LoginFragment.PENGURUS_LOGIN)) {
                    donasiku.setVisibility(view.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }

    private void setClickListener() {
        donasiku.setOnClickListener(this);
        ivBack.setOnClickListener(this);
    }

    private void init(View view) {
        bottomSheetLayout = view.findViewById(R.id.bottom_sheet_layout);
        sheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
        topLayout = view.findViewById(R.id.iv_background);
        titleLayout = view.findViewById(R.id.tv_title);
        donasiku = view.findViewById(R.id.tv_donasiku);
        ivBack = view.findViewById(R.id.iv_back);
        rvDaftarMasjid = view.findViewById(R.id.rv_daftar_masjid);

        ref_users = FirebaseDatabase.getInstance().getReference().child("Users");
        DbRef = FirebaseDatabase.getInstance().getReference().child("Users");
        ref_dana = FirebaseDatabase.getInstance().getReference().child("Dana");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewGroup.LayoutParams topLayoutParams = topLayout.getLayoutParams();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels + getNavigationBarHeight();
        screenWidth = displayMetrics.widthPixels;

        if (screenHeight > 2000) {
            height = (int) (screenHeight - topLayoutParams.height - convertPixelsToDp(200f, getContext()));
        } else {
            height = (int) (screenHeight - topLayoutParams.height);
        }

        ViewGroup.LayoutParams params = bottomSheetLayout.getLayoutParams();
        params.height = (int) ((screenHeight * 0.89) - titleLayout.getMeasuredHeight());
        params.width = screenWidth;

        bottomSheetLayout.setLayoutParams(params);
        sheetBehavior.setPeekHeight(height);
        sheetBehavior.setHideable(false);


        getListMasjid();

    }

    private void getListMasjid() {
        if (pengurusData != null) {
            pengurusData.clear();
        } else {
            pengurusData = new ArrayList<>();
        }

        if (kebutuhanData != null) {
            kebutuhanData.clear();
        } else {
            kebutuhanData = new ArrayList<>();
        }

        //get list


        if (banner) {
            ref_dana = ref_dana.orderByChild("total_dana").limitToFirst(10);
            ref_dana.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot np : snapshot.getChildren()) {
                            // get id pengurus
                            if (np.child("jenis_dana").getValue().toString().toLowerCase().equals("dana lebih")) {
                                String id_pengurus = np.child("id_pengurus").getValue().toString();

                                ref_users.child(id_pengurus).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        Log.i(TAG, "onDataChange: " + snapshot.getValue());
                                        PengurusData list = snapshot.getValue(PengurusData.class);
                                        pengurusData.add(list);
                                        keyItem.add(snapshot.getKey());
                                        DaftarMasjidMusholaAdapter daftarMasjidMusholaAdapter = new DaftarMasjidMusholaAdapter(getContext(), pengurusData, keyItem, "donasi");
                                        rvDaftarMasjid.setLayoutManager(new LinearLayoutManager(getContext()));
                                        rvDaftarMasjid.setAdapter(daftarMasjidMusholaAdapter);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }


                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        } else {
            DbRef = DbRef.orderByChild("tipe_user").equalTo(LoginFragment.PENGURUS_LOGIN);
            DbRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    pengurusData.clear();
                    if (snapshot.exists()) {
                        for (DataSnapshot npsnapshot : snapshot.getChildren()) {

                            Object status_user = npsnapshot.child("tipe_tempat").getValue();
                            if (status_user.equals(KEY_MASJID)) {

                                PengurusData list = npsnapshot.getValue(PengurusData.class);
                                pengurusData.add(list);
                                keyItem.add(npsnapshot.getKey());
                            }

                        }

                        DaftarMasjidMusholaAdapter daftarMasjidMusholaAdapter = new DaftarMasjidMusholaAdapter(getContext(), pengurusData, keyItem, "donasi");
                        rvDaftarMasjid.setLayoutManager(new LinearLayoutManager(getContext()));
                        rvDaftarMasjid.setAdapter(daftarMasjidMusholaAdapter);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.tv_donasiku:
                intent = new Intent(getContext(), DonasiActivity.class);
                intent.putExtra(DonasiActivity.DONATION_TYPE_KEY_EXTRA, HomeDonaturFragment.USER_DONASIKU_KEY_EXTRA);
                startActivity(intent);

                break;
            case R.id.iv_back:
                getActivity().onBackPressed();
                break;
        }
    }

    private int getNavigationBarHeight() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            DisplayMetrics metrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int usableHeight = metrics.heightPixels;
            getActivity().getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
            int realHeight = metrics.heightPixels;
            if (realHeight > usableHeight)
                return realHeight - usableHeight;
            else
                return 0;
        }
        return 0;
    }
}