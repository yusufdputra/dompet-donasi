package com.mydonate.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mydonate.R;
import com.mydonate.adapter.RiwayatDonasiAdapter;
import com.mydonate.data.TransaksiPembayaranData;

import java.util.ArrayList;

public class RiwayatDonasiFragment extends Fragment implements View.OnClickListener {
    private ArrayList<TransaksiPembayaranData> transaksiPembayaranData;
    private ArrayList<String> keyItem = new ArrayList<>();
    private RecyclerView rvRiwayatDonasi;
    private ImageView iv_back;
    ShimmerFrameLayout shimmerLayout;

    public RiwayatDonasiFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_riwayat_donasi, container, false);

        init(view);
        return view;
    }

    private void init(View view) {
        iv_back = view.findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
        rvRiwayatDonasi = view.findViewById(R.id.rv_riwayat_donasi);
        shimmerLayout = view.findViewById(R.id.shimmerlayout);
    }

    private void getListDonasi() {
        if (transaksiPembayaranData != null) {
            transaksiPembayaranData.clear();
        } else {
            transaksiPembayaranData = new ArrayList<>();
        }

        // get riwayat donasi
        Query dbRef =  FirebaseDatabase.getInstance().getReference().child("Transaksi Pembayaran").orderByChild("transaction_time").limitToFirst(100);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    for (DataSnapshot npsnapshot : snapshot.getChildren()) {
                        String status = String.valueOf(npsnapshot.child("status_code").getValue());
                        if (status != null) {
                            if (status.equals("200")) {
                                TransaksiPembayaranData list = npsnapshot.getValue(TransaksiPembayaranData.class);
                                transaksiPembayaranData.add(list);
                                keyItem.add(npsnapshot.getKey());
                            }
                        }
                    }
                }
                RiwayatDonasiAdapter riwayatDonasiAdapter = new RiwayatDonasiAdapter(getContext(), transaksiPembayaranData, keyItem, "donasi");

                rvRiwayatDonasi.setVisibility(View.VISIBLE);
                rvRiwayatDonasi.clearOnChildAttachStateChangeListeners();
                rvRiwayatDonasi.clearOnScrollListeners();

                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                layoutManager.setReverseLayout(true);
                layoutManager.setStackFromEnd(true);
                rvRiwayatDonasi.setLayoutManager(layoutManager);
//
//
//                rvRiwayatDonasi.setLayoutManager(new LinearLayoutManager(getContext()));
                rvRiwayatDonasi.setAdapter(riwayatDonasiAdapter);
                riwayatDonasiAdapter.notifyDataSetChanged();

                shimmerLayout.stopShimmer();
                shimmerLayout.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                shimmerLayout.stopShimmer();
                shimmerLayout.setVisibility(View.GONE);

                rvRiwayatDonasi.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListDonasi();
        shimmerLayout.startShimmer();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.iv_back:
                getActivity().onBackPressed();
                break;
        }
    }
}