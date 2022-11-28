package com.mydonate.fragment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.mydonate.adapter.DaftarDonasikuAdapter;
import com.mydonate.adapter.DaftarMasjidMusholaAdapter;
import com.mydonate.data.RiwayatPembayaranData;
import com.mydonate.data.TransaksiPembayaranData;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class DonasikuFragment extends Fragment{
    private ImageView ivBack;
    private RecyclerView rvDonasiku;
    private ArrayList<TransaksiPembayaranData> transaksiPembayaranData;
    private ArrayList<String> keyItem = new ArrayList<>();
    private View topLayout;
    private ConstraintLayout bottomSheetLayout;
    private int screenHeight, screenWidth, height;
    private TextView titleLayout;
    private BottomSheetBehavior<ConstraintLayout> sheetBehavior;

    public DonasikuFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_donasiku, container, false);
        init(view);
        setOnClickListener();


        return view;
    }

    private void getDonasiku() {

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String idUser = currentUser.getUid();
        if (transaksiPembayaranData != null){
            transaksiPembayaranData.clear();
        }else{
            transaksiPembayaranData = new ArrayList<>();
        }

        Query DbRef = FirebaseDatabase.getInstance().getReference().child("Transaksi Pembayaran").orderByChild("transaction_time");
        DbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                transaksiPembayaranData.clear();
                if (snapshot.exists()){
                    for (DataSnapshot npsnapshot : snapshot.getChildren()){
                        Object user = npsnapshot.child("id_donatur").getValue();

                        if (user.equals(idUser)){
                            TransaksiPembayaranData list = npsnapshot.getValue(TransaksiPembayaranData.class);
                            transaksiPembayaranData.add(list);
                            keyItem.add(npsnapshot.getKey());
                        }
                    }
                    DaftarDonasikuAdapter daftarDonasikuAdapter = new DaftarDonasikuAdapter(getContext(), transaksiPembayaranData, keyItem);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                    layoutManager.setReverseLayout(true);
                    layoutManager.setStackFromEnd(true);
                    rvDonasiku.setLayoutManager(layoutManager);
                    rvDonasiku.setAdapter(daftarDonasikuAdapter);


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

        topLayout = view.findViewById(R.id.iv_background);
        bottomSheetLayout = view.findViewById(R.id.bottom_sheet_layout);
        sheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
        titleLayout = view.findViewById(R.id.tv_title);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        getDonasiku();

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