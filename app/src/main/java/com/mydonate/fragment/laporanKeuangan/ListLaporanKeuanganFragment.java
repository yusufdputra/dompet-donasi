package com.mydonate.fragment.laporanKeuangan;

import android.content.Context;
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

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mydonate.R;
import com.mydonate.adapter.DaftarMasjidMusholaAdapter;
import com.mydonate.data.KebutuhanData;
import com.mydonate.data.PengurusData;
import com.mydonate.fragment.LoginFragment;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class ListLaporanKeuanganFragment extends Fragment {
    private static final String KEY_MASJID = "Masjid";
    private static final String KEY_MUSHOLA = "Mushola";
    ShimmerFrameLayout shimmerLayout;
    private ImageView ivBack;
    private RecyclerView rvDaftarMasjid;
    private View topLayout;
    private ConstraintLayout bottomSheetLayout;
    private int screenHeight, screenWidth, height;
    private TextView titleLayout;
    private BottomSheetBehavior<ConstraintLayout> sheetBehavior;
    private TextView tv_no_data;
    private Query DbRef;

    private ArrayList<PengurusData> pengurusData;
    private ArrayList<KebutuhanData> kebutuhanData;
    private ArrayList<String> keyItem;

    public ListLaporanKeuanganFragment() {
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
        View view = inflater.inflate(R.layout.fragment_list_laporan_keuangan, container, false);
        init(view);
        setOnClickListener();
        getListMasjid();
        return view;
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
        rvDaftarMasjid = view.findViewById(R.id.rv_list_masjid);
        tv_no_data = (TextView) view.findViewById(R.id.tv_no_data);
        topLayout = view.findViewById(R.id.iv_background);
        bottomSheetLayout = view.findViewById(R.id.bottom_sheet_layout);
        sheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
        titleLayout = view.findViewById(R.id.tv_title);
        shimmerLayout = view.findViewById(R.id.shimmerlayout);
        DbRef = FirebaseDatabase.getInstance().getReference().child("Users");
    }


    private void getListMasjid() {
        if (pengurusData != null) {
            pengurusData.clear();
            keyItem.clear();
        } else {
            pengurusData = new ArrayList<>();
            keyItem = new ArrayList<>();
        }


        //get list

        DbRef = DbRef.orderByChild("tipe_user").equalTo(LoginFragment.PENGURUS_LOGIN);
        DbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot npsnapshot : snapshot.getChildren()) {

                        PengurusData list = npsnapshot.getValue(PengurusData.class);
                        pengurusData.add(list);
                        keyItem.add(npsnapshot.getKey());

                    }

                    DaftarMasjidMusholaAdapter daftarMasjidMusholaAdapter = new DaftarMasjidMusholaAdapter(getContext(), pengurusData, keyItem, "laporan");
                    rvDaftarMasjid.setLayoutManager(new LinearLayoutManager(getContext()));
                    rvDaftarMasjid.setAdapter(daftarMasjidMusholaAdapter);

                    rvDaftarMasjid.setVisibility(View.VISIBLE);
                    shimmerLayout.stopShimmer();
                    shimmerLayout.setVisibility(View.GONE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


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