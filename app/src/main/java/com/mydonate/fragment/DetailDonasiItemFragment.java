package com.mydonate.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jpvs0101.currencyfy.Currencyfy;
import com.mydonate.R;
import com.mydonate.adapter.RiwayatDonasiAdapter;

import static android.content.ContentValues.TAG;

public class DetailDonasiItemFragment extends Fragment implements View.OnClickListener {
    private TextView nama_donatur, email_donatur, nominal_donasi, nama_tujuan, tgl_donasi, nama_kebutuhan, tv_status;
    private LinearLayout llNamaTempat;
    private ImageView iv_back, iv_link;
    private String id_tujuan, link_url;
    ShimmerFrameLayout shimmerLayout;
    private ConstraintLayout constraintLayout1, constraintLayout2, llLink;

    public DetailDonasiItemFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_donasi, container, false);
        init(view);
        if (getArguments() != null) {
            String id_pembayaran = getArguments().getString(RiwayatDonasiAdapter.KEY_ID_PEMBAYARAN);
            id_tujuan = getArguments().getString(RiwayatDonasiAdapter.KEY_ID_TUJUAN);
            link_url = getArguments().getString(RiwayatDonasiAdapter.KEY_LINK);
            getDataDonasi(id_pembayaran);

        }


        setClickListener();
        return view;
    }

    private void setClickListener() {
        iv_back.setOnClickListener(this);
//        nama_tujuan.setOnClickListener(this);
        iv_link.setOnClickListener(this);
    }

    private void getDataDonasi(String id_pembayaran) {
        // link pembayaran
        if (link_url == null) {
            llLink.setVisibility(View.GONE);
        }
        DatabaseReference listDb = FirebaseDatabase.getInstance().getReference().child("Transaksi Pembayaran").child(id_pembayaran);
        listDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {



                    String total_bayar = snapshot.child("total_bayar").getValue(String.class);
                    nominal_donasi.append(Currencyfy.currencyfy(Double.parseDouble(total_bayar), false, false) );
                    tgl_donasi.append(snapshot.child("transaction_time").getValue(String.class));

                    // status pembayaran
                    String status = snapshot.child("status_code").getValue(String.class);
                    if (status.equals("200")){ // sukses
                        tv_status.setText("Sukses");
                        tv_status.setTextColor(Color.parseColor("#32ad4a"));
                    }else if (status.equals("201")){ // pending
                        tv_status.setText("Pending");
                        tv_status.setTextColor(Color.parseColor("#0e4e95"));
                    }else{ // cancel
                        tv_status.setText("Dibatalkan");
                        tv_status.setTextColor(Color.parseColor("#FF0000"));
                    }



                    // get data donatur
                    String id_donatur = snapshot.child("id_donatur").getValue(String.class);
                    DatabaseReference listDb2 = FirebaseDatabase.getInstance().getReference().child("Users").child(id_donatur);
                    listDb2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                String alias = snapshot.child("alias").getValue(String.class);
                                if (alias != null) {
                                    nama_donatur.setText(alias);
                                } else {
                                    nama_donatur.setText(snapshot.child("nama").getValue(String.class));
                                }

                                email_donatur.setText(snapshot.child("email").getValue(String.class));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    // get data tujuan
                    String id_kebutuhan = snapshot.child("product_name").getValue(String.class);
                    Query db_pu = FirebaseDatabase.getInstance().getReference().child("Program Umum").child(id_kebutuhan).child("nama");
                    db_pu.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                nama_tujuan.append("Program Umum");
                                nama_kebutuhan.setText((CharSequence) snapshot.getValue());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    if (!id_kebutuhan.equals(DonasiUmumFragment.KEY_NAMA_DONASI)) {
                        DatabaseReference listDb3 = FirebaseDatabase.getInstance().getReference().child("Kebutuhan").child(id_kebutuhan);
                        listDb3.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    nama_kebutuhan.setText(snapshot.child("nama_kebutuhan").getValue(String.class));

                                    // get nama masjid
                                    String id_pengurus = snapshot.child("id_pengurus").getValue(String.class);
                                    DatabaseReference listDb4 = FirebaseDatabase.getInstance().getReference().child("Users").child(id_pengurus);
                                    listDb4.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                String tipe = snapshot.child("tipe_tempat").getValue(String.class);
                                                String nama = snapshot.child("nama_masjid").getValue(String.class);
                                                nama_tujuan.append(tipe + " " + nama);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    } else {
                        llNamaTempat.setVisibility(View.GONE);
                        nama_kebutuhan.setText("DONASI UMUM");
                    }

                }
            }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
        shimmerLayout.stopShimmer();
        shimmerLayout.setVisibility(View.GONE);

        constraintLayout1.setVisibility(View.VISIBLE);
        constraintLayout2.setVisibility(View.VISIBLE);

    }

    private void init(View view) {
        llNamaTempat = view.findViewById(R.id.llNamaTempat);
        nama_donatur = view.findViewById(R.id.tv_nama_donatur);
        email_donatur = view.findViewById(R.id.tv_email_donatur);
        nominal_donasi = view.findViewById(R.id.tv_nominal);
        tgl_donasi = view.findViewById(R.id.tv_tgl_donasi);
        nama_tujuan = view.findViewById(R.id.tv_nama_tempat);
        nama_kebutuhan = view.findViewById(R.id.tv_nama_kebutuhan);
        tv_status = view.findViewById(R.id.tv_status);
        iv_back = view.findViewById(R.id.iv_back);
        llLink = view.findViewById(R.id.llLink);
        iv_link = view.findViewById(R.id.iv_view_link);

        shimmerLayout = view.findViewById(R.id.shimmerlayout);
        shimmerLayout.startShimmer();

        constraintLayout1 = view.findViewById(R.id.constraintLayout2);
        constraintLayout2 = view.findViewById(R.id.constraintLayout3);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.iv_back:
                getActivity().onBackPressed();
                break;
//            case R.id.tv_nama_tempat:
////                linkTempat(id_tujuan, view);
////                break;
            case R.id.iv_view_link:
                showLink(link_url);
                break;
        }
    }

    private void showLink(String link_url) {
        Intent i = new Intent();
        i.setAction(Intent.ACTION_VIEW);
        i.addCategory(Intent.CATEGORY_BROWSABLE);
        i.setData(Uri.parse(link_url));
        startActivity(i);
    }

    private void linkTempat(String id_tujuan, View view) {
        DatabaseReference listDb = FirebaseDatabase.getInstance().getReference().child("Kebutuhan").child(id_tujuan);
        listDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String id_pengurus = snapshot.child("id_pengurus").getValue(String.class);

                    Bundle bundle = new Bundle();
                    bundle.putString(DetailMasjidMusholaFragment.KEY_ID, id_pengurus);

                    DetailMasjidMusholaFragment fragment = new DetailMasjidMusholaFragment();
                    fragment.setArguments(bundle);
                    AppCompatActivity activity = (AppCompatActivity) view.getContext();
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.home_frame_layout,fragment).addToBackStack(null).commit();


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
