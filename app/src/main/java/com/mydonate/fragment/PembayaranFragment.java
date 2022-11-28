package com.mydonate.fragment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
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

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mydonate.R;
import com.mydonate.adapter.DaftarDonasikuAdapter;
import com.mydonate.adapter.DaftarPembayaranAdapter;
import com.mydonate.data.BayarKebutuhanData;
import com.mydonate.data.DanaData;
import com.mydonate.data.KebutuhanData;
import com.mydonate.data.RiwayatPembayaranData;

import java.util.ArrayList;

public class PembayaranFragment extends Fragment {
    private ImageView ivBack;
    private RecyclerView rvDonasiku;
    private ArrayList<BayarKebutuhanData> bayarKebutuhanData;
    private ArrayList<DanaData> danaData;
    private ArrayList<String> keyItem = new ArrayList<>();
    private ArrayList<String> keyItemDana = new ArrayList<>();
    private View topLayout;
    private ConstraintLayout bottomSheetLayout;
    private int screenHeight, screenWidth, height;
    private TextView titleLayout;
    private BottomSheetBehavior<ConstraintLayout> sheetBehavior;
    private TextView tv_no_data;

    ShimmerFrameLayout shimmerLayout;

    public PembayaranFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rekap_pembayaran, container, false);
        init(view);
        setOnClickListener();
        getDonasi();
//        getBiayaLebih();

        return view;
    }

    private void getBiayaLebih() {
        if (danaData != null) {
            danaData.clear();
        } else {
            danaData = new ArrayList<>();
        }

        Query Dbref = FirebaseDatabase.getInstance().getReference().child("Dana");
        Dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot npSnapshot : snapshot.getChildren()){
                        String jenis_dana = npSnapshot.child("jenis_dana").getValue(String.class);
                        String total_dana = npSnapshot.child("total_dana").getValue(String.class);
                        if (jenis_dana.equals("Dana Lebih") && total_dana != "0"){
                            DanaData list = npSnapshot.getValue(DanaData.class);
                            danaData.add(list);
                            keyItemDana.add(npSnapshot.getKey());
                        }

//                        DaftarPembayaranAdapter daftarPembayaranAdapter = new DaftarPembayaranAdapter(getContext(), bayarKebutuhanData, keyItem);
//                        rvDonasiku.setLayoutManager(new LinearLayoutManager(getContext()));
//                        rvDonasiku.setAdapter(daftarPembayaranAdapter);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getDonasi() {

        if (bayarKebutuhanData != null) {
            bayarKebutuhanData.clear();
        } else {
            bayarKebutuhanData = new ArrayList<>();
        }

        Query DbRef = FirebaseDatabase.getInstance().getReference().child("Bayar Kebutuhan").orderByChild("sisa_nominal_kebutuhan").equalTo("0");
        DbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot npsnapshot : snapshot.getChildren()) {
                        Object foto_bukti = npsnapshot.child("foto_bukti_donasi").getValue();
                        if (foto_bukti == null) { // bukan donasi umum dan sudah di verifikasi
                            BayarKebutuhanData list = npsnapshot.getValue(BayarKebutuhanData.class);
                            bayarKebutuhanData.add(list);
                            keyItem.add(npsnapshot.getKey());
                        }
                    }
                    if (bayarKebutuhanData.size() > 0) {
                        shimmerLayout.stopShimmer();
                        shimmerLayout.setVisibility(View.GONE);

                        rvDonasiku.setVisibility(View.VISIBLE);
                        DaftarPembayaranAdapter daftarPembayaranAdapter = new DaftarPembayaranAdapter(getContext(), bayarKebutuhanData, keyItem);

                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                        linearLayoutManager.setReverseLayout(true);
                        linearLayoutManager.setStackFromEnd(true);
                        rvDonasiku.setLayoutManager(linearLayoutManager);
                        rvDonasiku.setAdapter(daftarPembayaranAdapter);
                    } else {
                        shimmerLayout.stopShimmer();
                        shimmerLayout.setVisibility(View.GONE);
                        rvDonasiku.setVisibility(View.GONE);
                        tv_no_data.setVisibility(View.VISIBLE);
                    }

                }else{
                    shimmerLayout.stopShimmer();
                    shimmerLayout.setVisibility(View.GONE);
                    rvDonasiku.setVisibility(View.GONE);
                    tv_no_data.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setOnClickListener() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }

    private void init(View view) {
        ivBack = view.findViewById(R.id.iv_back);
        rvDonasiku = view.findViewById(R.id.rv_riwayat_donasi);
        tv_no_data = (TextView) view.findViewById(R.id.tv_no_data);
        topLayout = view.findViewById(R.id.iv_background);
        bottomSheetLayout = view.findViewById(R.id.bottom_sheet_layout);
        sheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
        titleLayout = view.findViewById(R.id.tv_title);
        shimmerLayout = view.findViewById(R.id.shimmerlayout);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        shimmerLayout.startShimmer();
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
        super.onViewCreated(view, savedInstanceState);
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

    public static float convertPixelsToDp(float px, Context context) {
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
}