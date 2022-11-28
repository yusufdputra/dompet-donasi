package com.mydonate.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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

import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerDrawable;
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
import com.mydonate.activity.AdminActivity;
import com.mydonate.activity.AuthActivity;
import com.mydonate.activity.DonasiActivity;
import com.mydonate.adapter.RiwayatDonasiAdapter;
import com.mydonate.data.TransaksiPembayaranData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class HomeAdminFragment extends Fragment implements View.OnClickListener {
    public static final String KEY_VERIFIKASI_AKUN = "Verifikasi_akun";
    public static final String KEY_KELOLA_DU = "Kelola_DU";
    public static final String KEY_KELOLA_PU = "Kelola_PU";
    public static final String KEY_UPLOAD = "Upload_pembayaran";
    public static final String KEY_PENCAIRAN = "Kelola_pencairan";
    public static final String KEY_LAPORAN_KEUANGAN = "LAPORAN_KEUANGAN";
    ShimmerFrameLayout shimmerLayout;
    private ImageView ivUserPic, ivVerifikasi, ivUploadPembayaran, ivVerifikasiPembayaran;
    private TextView tvMore, tv_usernama, tv_no_data;
    private RecyclerView rvRiwayatDonasi;
    private ConstraintLayout CLDonasiUmum, CLVerifikasiAkun, CLProgramUmum, CLUploadPembayaran, CLPengajuanPencairanDana, CLLaporanKeuangan;
    private ArrayList<TransaksiPembayaranData> transaksiPembayaranData;
    private final ArrayList<String> keyItem = new ArrayList<>();
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private String Uid;
    private String Unama;
    private String Uimg_profile;

    public HomeAdminFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_admin, container, false);
        GetDataUser();
        init(view);

        getListDonasi();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        shimmerLayout.startShimmer();
        setClickListener();
        //cek user
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    Toast.makeText(getContext(), "Berhasil Logout!", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getActivity(), AuthActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(i);
                    getActivity().finish();
                }
            }
        };
    }

    private void getListDonasi() {


        if (transaksiPembayaranData != null) {
            transaksiPembayaranData.clear();
        } else {
            transaksiPembayaranData = new ArrayList<>();
        }

        // get riwayat donasi
        Query dbRef = FirebaseDatabase.getInstance().getReference().child("Transaksi Pembayaran").orderByKey().limitToFirst(5);
        dbRef.addValueEventListener(new ValueEventListener() {
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

                    RiwayatDonasiAdapter riwayatDonasiAdapter = new RiwayatDonasiAdapter(getContext(), transaksiPembayaranData, keyItem, "donasi");

                    shimmerLayout.stopShimmer();
                    shimmerLayout.setVisibility(View.GONE);

                    rvRiwayatDonasi.setVisibility(View.VISIBLE);

                    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                    layoutManager.setReverseLayout(true);
                    layoutManager.setStackFromEnd(true);
                    rvRiwayatDonasi.setLayoutManager(layoutManager);
                    rvRiwayatDonasi.setAdapter(riwayatDonasiAdapter);


                } else {
                    tv_no_data.setVisibility(View.VISIBLE);
                    shimmerLayout.stopShimmer();
                    shimmerLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                shimmerLayout.stopShimmer();
                shimmerLayout.setVisibility(View.GONE);

                rvRiwayatDonasi.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setClickListener() {
        ivUserPic.setOnClickListener(this);
        CLVerifikasiAkun.setOnClickListener(this);
        CLUploadPembayaran.setOnClickListener(this);
        CLDonasiUmum.setOnClickListener(this);
        CLProgramUmum.setOnClickListener(this);
        CLPengajuanPencairanDana.setOnClickListener(this);
        CLLaporanKeuangan.setOnClickListener(this);
        tvMore.setOnClickListener(this);
    }

    private void init(View view) {
        tv_usernama = view.findViewById(R.id.tv_user_name);
        rvRiwayatDonasi = view.findViewById(R.id.rv_user_donation);
        tvMore = view.findViewById(R.id.tv_more);
        ivUserPic = view.findViewById(R.id.iv_user_profile_pic);
        CLUploadPembayaran = view.findViewById(R.id.CLUploadPembayaran);
        CLVerifikasiAkun = view.findViewById(R.id.CLVerifikasiAkun);
        CLDonasiUmum = view.findViewById(R.id.CLKelolaDanaUmum);
        CLProgramUmum = view.findViewById(R.id.CLKelolaProgramUmum);
        shimmerLayout = view.findViewById(R.id.shimmerlayout);
        tv_no_data = view.findViewById(R.id.tv_no_data1);
        CLPengajuanPencairanDana = view.findViewById(R.id.CLPengajuanPencairanDana);
        CLLaporanKeuangan = view.findViewById(R.id.CLLaporanKeuangan);
        auth = FirebaseAuth.getInstance();
    }

    private void GetDataUser() {
        //init shimmer
        Shimmer shimmer = new Shimmer.ColorHighlightBuilder()
                .setBaseColor(Color.parseColor("#F3F3F3"))
                .setBaseAlpha(1)
                .setHighlightColor(Color.parseColor("#E7E7E7"))
                .setHighlightAlpha(1)
                .setDropoff(50)
                .build();
        ShimmerDrawable shimmerDrawable = new ShimmerDrawable();
        shimmerDrawable.setShimmer(shimmer);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Uid = currentUser.getUid();
        DatabaseReference jLoginDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(Uid);
        jLoginDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Unama = snapshot.child("nama").getValue(String.class);
                Uimg_profile = snapshot.child("foto_profile").getValue(String.class);

                StorageReference ref = FirebaseStorage.getInstance().getReference();
                StorageReference dateRef = ref.child("Users/" + Uimg_profile);
                dateRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get()
                                .load(uri)
                                .placeholder(shimmerDrawable)
                                .into(ivUserPic);

                    }
                });
                tv_usernama.setText("Hi, " + Unama);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        Intent intent = null;
        switch (id) {
            case R.id.iv_user_profile_pic:
                logOut();
                break;
            case R.id.CLVerifikasiAkun:
                intent = new Intent(getContext(), AdminActivity.class);
                intent.putExtra(AdminActivity.ACTIVITY_TYPE_KEY_EXTRA, KEY_VERIFIKASI_AKUN);
                startActivity(intent);
                break;
            case R.id.CLKelolaDanaUmum:
                intent = new Intent(getContext(), DonasiActivity.class);
                intent.putExtra(DonasiActivity.DONATION_TYPE_KEY_EXTRA, KEY_KELOLA_DU);
                startActivity(intent);
                break;
            case R.id.CLKelolaProgramUmum:
                intent = new Intent(getContext(), DonasiActivity.class);
                intent.putExtra(DonasiActivity.DONATION_TYPE_KEY_EXTRA, KEY_KELOLA_PU);
                startActivity(intent);
                break;
            case R.id.CLUploadPembayaran:
                intent = new Intent(getContext(), AdminActivity.class);
                intent.putExtra(AdminActivity.ACTIVITY_TYPE_KEY_EXTRA, KEY_UPLOAD);
                startActivity(intent);
                break;
            case R.id.CLPengajuanPencairanDana:
                intent = new Intent(getContext(), AdminActivity.class);
                intent.putExtra(AdminActivity.ACTIVITY_TYPE_KEY_EXTRA, KEY_PENCAIRAN);
                startActivity(intent);
                break;
            case R.id.CLLaporanKeuangan:
                intent = new Intent(getContext(), AdminActivity.class);
                intent.putExtra(AdminActivity.ACTIVITY_TYPE_KEY_EXTRA, KEY_LAPORAN_KEUANGAN);
                startActivity(intent);
                break;
            case R.id.tv_more:
                RiwayatDonasiFragment fragment = new RiwayatDonasiFragment();
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.home_frame_layout, fragment).addToBackStack(null).commit();
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
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    //lepas listener
    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }


}