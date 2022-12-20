package com.mydonate.fragment.laporanKeuangan;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jpvs0101.currencyfy.Currencyfy;
import com.mydonate.R;
import com.mydonate.adapter.RiwayatDonasiAdapter;
import com.mydonate.data.TransaksiPembayaranData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class UangMasukFragment extends Fragment {
    private static String id_user;
    private static int getNewBulan, getNewTahun;
    private final ArrayList<String> keyItem = new ArrayList<>();
    private TextView tv_not_found, tv_jumlah;
    private RecyclerView rv_riwayat;
    private ShimmerFrameLayout shimmerLayout;
    private ArrayList<TransaksiPembayaranData> transaksiPembayaranData = new ArrayList<>();
    private static Double JumlahDanaMasuk = 0.0;

    public static UangMasukFragment newInstance(String uid, int getBulan, int getTahun) {
        id_user = uid;
        getNewBulan = getBulan;
        getNewTahun = getTahun;
        JumlahDanaMasuk = 0.0;
        return new UangMasukFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_riwayat_uang_masuk_keluar, container, false);
        init(view);
        getRiwayat();
        return view;
    }

    private void init(View view) {
        tv_not_found = view.findViewById(R.id.tv_not_found);
        rv_riwayat = view.findViewById(R.id.rv_riwayat);
        rv_riwayat.setHasFixedSize(true);
        rv_riwayat.setNestedScrollingEnabled(false);
        tv_jumlah = view.findViewById(R.id.tv_jumlah);
        shimmerLayout = view.findViewById(R.id.shimmerlayout);
    }

    private void getRiwayat() {


        Query db = FirebaseDatabase.getInstance().getReference().child("Kebutuhan").orderByChild("id_pengurus").equalTo(id_user);
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
                                        String order_id = npsnapshots.child("order_id").getValue(String.class);
                                        if (order_id != null && status_code != null && status_code.equals("200")) {
                                            // sorting by bulan
                                            String str_date = npsnapshots.child("transaction_time").getValue(String.class);

                                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                            Date date = null;
                                            try {
                                                date = formatter.parse(str_date);
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                            String getBulanDb = (String) DateFormat.format("MM", date);
                                            String getTahunDb = (String) DateFormat.format("yyyy", date);

                                            if ((Integer.parseInt(getBulanDb) == getNewBulan) && (Integer.parseInt(getTahunDb)) == getNewTahun) {
                                                TransaksiPembayaranData list = npsnapshots.getValue(TransaksiPembayaranData.class);
                                                transaksiPembayaranData.add(list);
                                                keyItem.add(npsnapshots.getKey());

                                                // akumulasikan jumlah dana masuk
                                                String bayar = npsnapshots.child("total_bayar").getValue(String.class);
                                                JumlahDanaMasuk = JumlahDanaMasuk + Double.parseDouble(bayar);

                                            }

                                            Log.i(TAG, "onDataChange: size " + transaksiPembayaranData.size());
                                            if (transaksiPembayaranData.size() > 0) {
                                                RiwayatDonasiAdapter riwayatDonasiAdapter = new RiwayatDonasiAdapter(getContext(), transaksiPembayaranData, keyItem, "laporan");
                                                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                                                layoutManager.setReverseLayout(true);
                                                layoutManager.setOrientation(RecyclerView.VERTICAL);
                                                rv_riwayat.setLayoutManager(layoutManager);
                                                rv_riwayat.setAdapter(riwayatDonasiAdapter);

                                                rv_riwayat.setVisibility(View.VISIBLE);
                                                tv_not_found.setVisibility(View.GONE);
                                            } else {
                                                rv_riwayat.setVisibility(View.GONE);
                                                tv_not_found.setVisibility(View.VISIBLE);
                                            }

                                            Log.i(TAG, "onDataChange: 3" + JumlahDanaMasuk.toString());

                                            shimmerLayout.stopShimmer();
                                            shimmerLayout.setVisibility(View.GONE);
                                        }
                                    }
                                    tv_jumlah.setText(Currencyfy.currencyfy(JumlahDanaMasuk, false, false));
                                } else {
//                                    tv_jumlah.setText(Currencyfy.currencyfy(0, false, false));
//                                    shimmerLayout.stopShimmer();
//                                    shimmerLayout.setVisibility(View.GONE);
//
//                                    rv_riwayat.setVisibility(View.GONE);
//                                    tv_not_found.setVisibility(View.VISIBLE);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    @Override
    public void onStart() {
        super.onStart();
        // hitung semua dana

    }

    @Override
    public void onStop() {
        super.onStop();

    }


}
