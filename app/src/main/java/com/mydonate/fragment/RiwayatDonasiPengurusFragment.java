package com.mydonate.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mydonate.R;
import com.mydonate.adapter.RiwayatDonasiAdapter;
import com.mydonate.data.TransaksiPembayaranData;

import java.util.ArrayList;

public class RiwayatDonasiPengurusFragment extends Fragment implements View.OnClickListener {
  ShimmerFrameLayout shimmerLayout;
  private ArrayList<TransaksiPembayaranData> transaksiPembayaranData;
  private ArrayList<String> keyItem = new ArrayList<>();
  private RecyclerView rvRiwayatDonasi;
  private ImageView iv_back;

  public RiwayatDonasiPengurusFragment() {
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
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    String Uid = currentUser.getUid();
    if (transaksiPembayaranData != null) {
      transaksiPembayaranData.clear();
    } else {
      transaksiPembayaranData = new ArrayList<>();
    }

    Query db = FirebaseDatabase.getInstance().getReference().child("Kebutuhan").orderByChild("id_pengurus").equalTo(Uid);
    db.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
        transaksiPembayaranData.clear();
        if (snapshot.exists()) {
          for (DataSnapshot npsnapshot : snapshot.getChildren()) {
            String id_keb = npsnapshot.getKey();

            Query db2 = FirebaseDatabase.getInstance().getReference().child("Transaksi Pembayaran").orderByChild("product_name").equalTo(id_keb);
            db2.addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot snapshots) {
                if (snapshots.exists()) {

                  for (DataSnapshot npsnapshots : snapshots.getChildren()) {
                    String status_code = npsnapshots.child("status_code").getValue(String.class);
                    if (status_code != null && status_code.equals("200")) {
                      TransaksiPembayaranData list = npsnapshots.getValue(TransaksiPembayaranData.class);
                      transaksiPembayaranData.add(list);
                      keyItem.add(npsnapshots.getKey());
                    }

                  }
                  shimmerLayout.stopShimmer();
                  shimmerLayout.setVisibility(View.GONE);

                  RiwayatDonasiAdapter riwayatDonasiAdapter = new RiwayatDonasiAdapter(getContext(), transaksiPembayaranData, keyItem, "donasi");

                  rvRiwayatDonasi.setVisibility(View.VISIBLE);

                  LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                  layoutManager.setReverseLayout(true);
                  layoutManager.setStackFromEnd(true);
                  rvRiwayatDonasi.setLayoutManager(layoutManager);
                  rvRiwayatDonasi.setAdapter(riwayatDonasiAdapter);
                } else {
                  shimmerLayout.stopShimmer();
                  shimmerLayout.setVisibility(View.GONE);
                  rvRiwayatDonasi.setVisibility(View.VISIBLE);
                }
              }

              @Override
              public void onCancelled(@NonNull DatabaseError error) {

              }
            });

          }


        } else {
          shimmerLayout.stopShimmer();
          shimmerLayout.setVisibility(View.GONE);
          rvRiwayatDonasi.setVisibility(View.VISIBLE);
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {

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
    switch (id) {
      case R.id.iv_back:
        getActivity().onBackPressed();
        break;
    }
  }
}