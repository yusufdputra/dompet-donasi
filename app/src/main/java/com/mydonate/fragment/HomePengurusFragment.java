package com.mydonate.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mydonate.R;
import com.mydonate.activity.AuthActivity;
import com.mydonate.activity.Berita.BeritaActivity;
import com.mydonate.activity.DonasiActivity;
import com.mydonate.activity.HomeActivity;
import com.mydonate.activity.ProfilActivity;
import com.mydonate.adapter.RiwayatDonasiAdapter;
import com.mydonate.data.Donatur;
import com.mydonate.data.DonaturData;
import com.mydonate.data.TransaksiPembayaranData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class HomePengurusFragment extends Fragment implements View.OnClickListener {
    public static final String DONATION_TYPE_MASJID_KEY_EXTRA = "MasjidMushola";
    public static final String DONATION_TYPE_MUSHOLA_KEY_EXTRA = "Mushola";
    public static final String DONATION_TYPE_UMUM_KEY_EXTRA = "Umum";
    public static final String USER_DONATION_KEY_EXTRA = "RiwayatDonasi";
    public static final String KEY_LAPORAN_KEUANGAN = "LAPORAN_KEUANGAN";
    private RecyclerView rvRiwayatDonasi;
    private ArrayList<Donatur> dataDonatur;
    private ImageView ivUserPic, iv_logout;
    private ConstraintLayout CLLaporanKeuangan, CLProfile, CLBerita;
    private TextView tvMore, tv_user_name, tv_no_data;

    private ArrayList<TransaksiPembayaranData> transaksiPembayaranData;
    private ArrayList<String> keyItem = new ArrayList<>();


    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;

    private String Uid, Unama, Uimg_profile;
    ShimmerFrameLayout shimmerLayout;

    public HomePengurusFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_pengurus, container, false);
        init(view);
        setOnClickListener();

        GetDataUser();
        getListDonasi();

        return view;
    }

    private void setOnClickListener() {
        tvMore.setOnClickListener(this);
        ivUserPic.setOnClickListener(this);
        CLBerita.setOnClickListener(this);
//        ivMenuDonasiUmum.setOnClickListener(this);

        iv_logout.setOnClickListener(this);
        CLProfile.setOnClickListener(this);
        CLLaporanKeuangan.setOnClickListener(this);
    }

    private void getListDonasi() {
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

                        Query db2 = FirebaseDatabase.getInstance().getReference().child("Transaksi Pembayaran").orderByChild("product_name").equalTo(id_keb).limitToFirst(5);
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
                                    tv_no_data.setVisibility(View.VISIBLE);
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
                    tv_no_data.setVisibility(View.VISIBLE);
                    rvRiwayatDonasi.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void GetDataUser() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Uid = currentUser.getUid();
        DatabaseReference jLoginDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(Uid);
        jLoginDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Unama = snapshot.child("nama_pengurus").getValue(String.class);
                Uimg_profile = snapshot.child("foto_tempat").getValue(String.class);

                StorageReference ref = FirebaseStorage.getInstance().getReference();
                StorageReference dateRef = ref.child("Users/"+Uimg_profile);
                dateRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get()
                                .load(uri)
                                .placeholder(R.drawable.profile_icon)
                                .into(ivUserPic);
                    }
                });

                //set to layout
                tv_user_name.setText("Hi, "+Unama);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        shimmerLayout.startShimmer();
        //tv_logout.setOnClickListener(this);
        //cek user
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null){
                    Toast.makeText(getContext(), "Berhasil Logout!", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getActivity(), AuthActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(i);
                    getActivity().finishAffinity();
                }
            }
        };


    }

    private void init(View view) {
        rvRiwayatDonasi = view.findViewById(R.id.rv_user_donation);
        tvMore = view.findViewById(R.id.tv_more);
        ivUserPic = view.findViewById(R.id.iv_user_profile_pic);
        CLBerita = view.findViewById(R.id.cl_berita);
        CLProfile = view.findViewById(R.id.cl_profile);
        dataDonatur = new ArrayList<>();
        dataDonatur.addAll(DonaturData.getLastThreeRiwayatDonasi());
        tv_user_name = view.findViewById(R.id.tv_user_name);
        shimmerLayout = view.findViewById(R.id.shimmerlayout);
        tv_no_data = view.findViewById(R.id.tv_no_data1);
        CLLaporanKeuangan = view.findViewById(R.id.CLLaporanKeuangan);
        iv_logout = view.findViewById(R.id.iv_logout);

        auth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        Bundle bundle = new Bundle();
        switch (v.getId()) {
            case R.id.tv_more:
                RiwayatDonasiPengurusFragment fragment = new RiwayatDonasiPengurusFragment();
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.home_frame_layout, fragment).addToBackStack(null).commit();
                break;
            case R.id.iv_user_profile_pic:
            case R.id.cl_profile:
                intent = new Intent(getContext(), ProfilActivity.class);
                intent.putExtra(HomeActivity.LOGIN_TYPE_KEY_EXTRA, LoginFragment.PENGURUS_LOGIN);
                startActivity(intent);

                break;
            case R.id.iv_menu_donasi_umum:
                intent = new Intent(getContext(), DonasiActivity.class);
                intent.putExtra(DonasiActivity.DONATION_TYPE_KEY_EXTRA, DONATION_TYPE_UMUM_KEY_EXTRA);
                startActivity(intent);
                break;
            case R.id.cl_berita:
                intent = new Intent(getContext(), BeritaActivity.class);
                startActivity(intent);
                break;
            case R.id.CLLaporanKeuangan:
                intent = new Intent(getContext(), DonasiActivity.class);
                intent.putExtra(DonasiActivity.DONATION_TYPE_KEY_EXTRA, KEY_LAPORAN_KEUANGAN);
                startActivity(intent);
                break;
            case R.id.iv_logout:
                logOut();
                break;

        }
    }

    private void logOut() {
        AlertDialog.Builder alerBuilder = new AlertDialog.Builder(getContext());
        alerBuilder.setMessage("Ingin keluar?");

        alerBuilder.setPositiveButton("Yes", (dialogInterface, i) -> {

            auth.signOut();
        });

        alerBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alerBuilder.show();
    }

    //menerapkan listener
    @Override
    public void onStart(){
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    //lepas listener
    @Override
    public void onStop(){
        super.onStop();
        if (authListener != null){
            auth.removeAuthStateListener(authListener);
        }
    }
}