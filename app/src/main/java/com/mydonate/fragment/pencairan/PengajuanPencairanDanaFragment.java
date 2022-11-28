package com.mydonate.fragment.pencairan;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mydonate.R;
import com.mydonate.adapter.DaftarPengajuanPencairanDanaAdapter;
import com.mydonate.data.PengajuanPencairanDanaData;

import java.util.ArrayList;

public class PengajuanPencairanDanaFragment extends Fragment {
    ShimmerFrameLayout shimmerLayout;
    private ImageView ivBack;
    private RecyclerView rvDonasiku;
    private ArrayList<PengajuanPencairanDanaData> pencairanDanaData;
    private ArrayList<String> keyItem = new ArrayList<>();
    private View topLayout;
    private ConstraintLayout bottomSheetLayout;
    private int screenHeight, screenWidth, height;
    private TextView titleLayout;
    private BottomSheetBehavior<ConstraintLayout> sheetBehavior;
    private TextView tv_no_data;
    private Query Dbref;

    public PengajuanPencairanDanaFragment() {
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
        View view = inflater.inflate(R.layout.fragment_pengajuan_pencairan_dana, container, false);
        init(view);
        setOnClickListener();
        getPengajuan();

        return view;
    }

    private void getPengajuan() {
        if (pencairanDanaData != null) {
            pencairanDanaData.clear();
            keyItem.clear();
        } else {
            pencairanDanaData = new ArrayList<>();
        }

        Dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot npSnapshot : snapshot.getChildren()) {
                        PengajuanPencairanDanaData list = npSnapshot.getValue(PengajuanPencairanDanaData.class);
                        pencairanDanaData.add(list);
                        keyItem.add(npSnapshot.getKey());
                    }

                    if (pencairanDanaData.size() > 0) {
                        getRecycleView();

                    } else {
                        shimmerLayout.stopShimmer();
                        shimmerLayout.setVisibility(View.GONE);
                        rvDonasiku.setVisibility(View.GONE);
                        tv_no_data.setVisibility(View.VISIBLE);
                    }
                }else {
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

    private void getRecycleView() {
        shimmerLayout.stopShimmer();
        shimmerLayout.setVisibility(View.GONE);

        rvDonasiku.setVisibility(View.VISIBLE);
        DaftarPengajuanPencairanDanaAdapter pengajuanAdapter = new DaftarPengajuanPencairanDanaAdapter(getContext(), pencairanDanaData, keyItem);


        pengajuanAdapter.notifyDataSetChanged();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        rvDonasiku.setLayoutManager(linearLayoutManager);
        rvDonasiku.setAdapter(pengajuanAdapter);
    }

    private void setOnClickListener() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

//        tv_filter.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (pencairanDanaData != null) {
//                    pencairanDanaData.clear();
//                    keyItem.clear();
//                } else {
//                    pencairanDanaData = new ArrayList<>();
//                }
//
//                String filter = tv_filter.getText().toString().toLowerCase();
//                if (filter.equals("baru")) {
//                    tv_filter.setText("Proses");
//                    filterGetData(filter);
//                }else if (filter.equals("proses")){
//                    tv_filter.setText("Baru");
//                    filterGetData(filter);
//                }
//            }
//        });
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

        Dbref = FirebaseDatabase.getInstance().getReference().child(getResources().getString(R.string.ref_pengajuan_pencairan));
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
}