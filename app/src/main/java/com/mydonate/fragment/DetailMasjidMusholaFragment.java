package com.mydonate.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jpvs0101.currencyfy.Currencyfy;
import com.mydonate.R;
import com.mydonate.adapter.DaftarKebutuhanAdapter;
import com.mydonate.adapter.ItemBeritaSmallAdapter;
import com.mydonate.adapter.RiwayatDonasiAdapter;
import com.mydonate.data.BeritaData;
import com.mydonate.data.Donatur;
import com.mydonate.data.KebutuhanData;
import com.mydonate.data.TransaksiPembayaranData;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;

public class DetailMasjidMusholaFragment extends Fragment implements View.OnClickListener {
    public final static String KEY_ID = "KEY_ID";

    private String id_item, UnamaPengurus, Unama_masjid, Ualamat_masjid, Ualamat_pengurus, Uemail, Uphone, Utipe_tempat, Ufoto_tempat, edit_kebutuhan_key;
    private TextView deskripsi, title, tv_no_data_kebutuhan, tv_no_data_riwayatDonasi, tv_no_data_buktiPenyerahan, NamaMasjid, AlamatMasjid, NamaPengurus, AlamatPengurus, EmailPengurus, PhonePengurus, Deposit;
    private ImageView foto_tempat, iv_back;
    private RecyclerView rvRiwayatDonasi, rvkebutuhan, rv_slide_berita;
    private ArrayList<Donatur> dataDonatur;
    private ArrayList<KebutuhanData> kebutuhanData;
    private ArrayList<BeritaData> beritaData;
    private String Uid;
    private TextView tv_more_berita;
    private LinearLayout ll_berita;
    private ItemBeritaSmallAdapter adapter;

    private ArrayList<TransaksiPembayaranData> transaksiPembayaranData;
    private ArrayList<String> keyItem;
    private ArrayList<String> keyBerita;


    ShimmerFrameLayout shimmerLayout, shimmer_kebutuhan;

    public DetailMasjidMusholaFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_masjid, container, false);
        init(view);
        setOnClickListener();

        if (getArguments() != null) {
            Uid = getArguments().getString(KEY_ID);
            GetDetail();
            GetKebutuhan();
            getListDonasi();
            getListBerita();
        }

        return view;
    }

    private void getListBerita() {
        if (beritaData != null) {
            beritaData.clear();
            keyBerita.clear();
        } else {
            beritaData = new ArrayList<>();
            keyBerita = new ArrayList<>();
        }

        Query db = FirebaseDatabase.getInstance().getReference().child("Berita").orderByChild("id_pengurus").equalTo(Uid).limitToLast(5);
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        BeritaData data = dataSnapshot.getValue(BeritaData.class);
                        beritaData.add(data);
                        keyBerita.add(dataSnapshot.getKey());
                    }
                    Collections.reverse(beritaData);
                    Collections.reverse(keyBerita);

                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                    linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                    rv_slide_berita.setLayoutManager(new GridLayoutManager(getContext(), 1, GridLayoutManager.HORIZONTAL, false));
                    adapter = new ItemBeritaSmallAdapter(getContext(), beritaData, keyBerita);
                    rv_slide_berita.setAdapter(adapter);
                    ll_berita.setVisibility(View.VISIBLE);
                } else {
                    ll_berita.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void getListDonasi() {

        if (transaksiPembayaranData != null) {
            transaksiPembayaranData.clear();
            keyItem.clear();
        } else {
            transaksiPembayaranData = new ArrayList<>();
            keyItem = new ArrayList<>();
        }

        Query db = FirebaseDatabase.getInstance().getReference().child("Kebutuhan").orderByChild("id_pengurus").equalTo(Uid);
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
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
                                    if (transaksiPembayaranData.size() > 0) {


                                        RiwayatDonasiAdapter riwayatDonasiAdapter = new RiwayatDonasiAdapter(getContext(), transaksiPembayaranData, keyItem, "donasi");
                                        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                                        layoutManager.setReverseLayout(true);
                                        layoutManager.setStackFromEnd(true);
                                        rvRiwayatDonasi.setLayoutManager(layoutManager);
                                        rvRiwayatDonasi.setAdapter(riwayatDonasiAdapter);

                                        rvRiwayatDonasi.setVisibility(View.VISIBLE);
                                        rvRiwayatDonasi.clearOnChildAttachStateChangeListeners();
                                        rvRiwayatDonasi.clearOnScrollListeners();
                                        tv_no_data_riwayatDonasi.setVisibility(View.GONE);

                                        shimmerLayout.stopShimmer();
                                        shimmerLayout.setVisibility(View.GONE);
                                    } else {
                                        shimmerLayout.stopShimmer();
                                        shimmerLayout.setVisibility(View.GONE);

                                        rvRiwayatDonasi.setVisibility(View.GONE);
                                        tv_no_data_riwayatDonasi.setVisibility(View.VISIBLE);
                                    }
                                }else{
                                    shimmerLayout.stopShimmer();
                                    shimmerLayout.setVisibility(View.GONE);

                                    rvRiwayatDonasi.setVisibility(View.GONE);
                                    tv_no_data_riwayatDonasi.setVisibility(View.VISIBLE);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }


                }else{
                    shimmerLayout.stopShimmer();
                    shimmerLayout.setVisibility(View.GONE);

                    rvRiwayatDonasi.setVisibility(View.GONE);
                    tv_no_data_riwayatDonasi.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void GetKebutuhan() {
        if (kebutuhanData != null) {
            kebutuhanData.clear();
            keyItem.clear();
        } else {
            kebutuhanData = new ArrayList<>();
            keyItem = new ArrayList<>();
        }

        //get item list kebutuhan
        Query listDb = FirebaseDatabase.getInstance().getReference().child("Kebutuhan").orderByChild("id_pengurus").equalTo(Uid);
        listDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    for (DataSnapshot npsnapshot : snapshot.getChildren()) {

//                        Object data = npsnapshot.child("id_pengurus").getValue();
//                        if (data.equals(Uid)) {
                        String id_kebutuhan = npsnapshot.getKey();

                        Query listBK = FirebaseDatabase.getInstance().getReference().child("Bayar Kebutuhan").child(id_kebutuhan).orderByChild("sisa_nominal_kebutuhan");
                        listBK.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    String sisa_kebutuhan = snapshot.child("sisa_nominal_kebutuhan").getValue(String.class);

//                                        if (!sisa_kebutuhan.equals("0")) {
                                    KebutuhanData list = npsnapshot.getValue(KebutuhanData.class);
                                    kebutuhanData.add(list);
                                    keyItem.add(snapshot.getKey());
//                                        }
//                                    Log.i(TAG, "PANJANG KEBUTUHAN" + snapshot);
                                    if (kebutuhanData.size() > 0) {
                                        DaftarKebutuhanAdapter daftarKebutuhanAdapter = new DaftarKebutuhanAdapter(getContext(), kebutuhanData, keyItem, edit_kebutuhan_key);

                                        rvkebutuhan.setVisibility(View.VISIBLE);
                                        tv_no_data_kebutuhan.setVisibility(View.GONE);

                                        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                                        layoutManager.setReverseLayout(true);
                                        layoutManager.setStackFromEnd(true);
                                        rvkebutuhan.setLayoutManager(layoutManager);
                                        rvkebutuhan.setAdapter(daftarKebutuhanAdapter);

                                        shimmer_kebutuhan.stopShimmer();
                                        shimmer_kebutuhan.setVisibility(View.GONE);
                                    }
                                } else {
                                    shimmer_kebutuhan.stopShimmer();
                                    shimmer_kebutuhan.setVisibility(View.GONE);
                                    rvkebutuhan.setVisibility(View.GONE);
                                    tv_no_data_kebutuhan.setVisibility(View.VISIBLE);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                shimmer_kebutuhan.stopShimmer();
                                shimmer_kebutuhan.setVisibility(View.GONE);
                                rvkebutuhan.setVisibility(View.GONE);
                                tv_no_data_kebutuhan.setVisibility(View.VISIBLE);
                            }
                        });
                    }
//                    }


                } else {
                    shimmer_kebutuhan.stopShimmer();
                    shimmer_kebutuhan.setVisibility(View.GONE);
                    rvkebutuhan.setVisibility(View.GONE);
                    tv_no_data_kebutuhan.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void GetDetail() {
        DatabaseReference listDb = FirebaseDatabase.getInstance().getReference().child("Users").child(Uid);
        listDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Unama_masjid = snapshot.child("nama_masjid").getValue(String.class);
                Ualamat_masjid = snapshot.child("alamat_masjid").getValue(String.class);
                UnamaPengurus = snapshot.child("nama_pengurus").getValue(String.class);
                Ualamat_pengurus = snapshot.child("alamat_pengurus").getValue(String.class);
                Uemail = snapshot.child("email").getValue(String.class);
                Uphone = snapshot.child("phone").getValue(String.class);
                Utipe_tempat = snapshot.child("tipe_tempat").getValue(String.class);
                Ufoto_tempat = snapshot.child("foto_tempat").getValue(String.class);


                //set to layout
                title.append(Utipe_tempat + " " + Unama_masjid);
                NamaMasjid.setText(Utipe_tempat + " " + Unama_masjid);
                AlamatMasjid.setText(Ualamat_masjid);
                NamaPengurus.setText(UnamaPengurus);
                AlamatPengurus.setText(Ualamat_pengurus);
                EmailPengurus.setText(Uemail);

                PhonePengurus.setText(Uphone);
//
//                deskripsi.setText(Utipe_tempat + " " + Unama_masjid + " berada di " + Ualamat_masjid + ". " + Utipe_tempat + " ini memiliki pengurus " + UnamaPengurus + ". Kontak yang dapat dihubungi ialah " +
//                        Uemail + " atau " + Uphone);
//                //set foto tempat

                setImageView(Ufoto_tempat, foto_tempat);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // get deposit
        Query dbDana = FirebaseDatabase.getInstance().getReference().child("Dana").orderByChild("id_pengurus").equalTo(Uid);
        dbDana.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot npsnapshot : snapshot.getChildren()) {
                        Double donasi_terkumpul = npsnapshot.child("total_dana").getValue(Double.class);
                        Deposit.setText("Rp. " + Currencyfy.currencyfy(donasi_terkumpul, false, false));
                        break;
                    }
                } else {
                    Deposit.setText("Rp 0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setImageView(String Ufoto, ImageView iv_target) {
        //set image to layout
        StorageReference ref = FirebaseStorage.getInstance().getReference();
        StorageReference dateRef = ref.child("Users/" + Ufoto);
        dateRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).fetch(new Callback() {
                    @Override
                    public void onSuccess() {
                        iv_target.setAlpha(0f);
                        Picasso.get()
                                .load(uri)
                                .placeholder(R.drawable.background_splash)
                                .into(iv_target);
                        iv_target.animate().setDuration(300).alpha(1f).start();

                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(getContext(), "Terjadi kesalahan saat mengunduh gambar", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }

    private void init(View view) {
        tv_no_data_kebutuhan = (TextView) view.findViewById(R.id.tv_no_data1);
        tv_no_data_riwayatDonasi = (TextView) view.findViewById(R.id.tv_no_data2);

        title = view.findViewById(R.id.tv_title);
        deskripsi = view.findViewById(R.id.tv_deskripsi);
        rvRiwayatDonasi = view.findViewById(R.id.rv_riwayat_donasi);
        rvkebutuhan = view.findViewById(R.id.rv_kebutuhan);
        foto_tempat = view.findViewById(R.id.imageView);
        iv_back = view.findViewById(R.id.iv_back);

        NamaMasjid = view.findViewById(R.id.tv_nama_masjid);
        AlamatMasjid = view.findViewById(R.id.tv_alamat_masjid);
        NamaPengurus = view.findViewById(R.id.tv_nama_pengurus);
        AlamatPengurus = view.findViewById(R.id.tv_alamat_pengurus);
        EmailPengurus = view.findViewById(R.id.tv_email_pengurus);
        PhonePengurus = view.findViewById(R.id.tv_phone_pengurus);
        Deposit = view.findViewById(R.id.tv_deposit);

        rv_slide_berita = view.findViewById(R.id.rv_slide_berita);
        tv_more_berita = view.findViewById(R.id.tv_more_berita);
        ll_berita = view.findViewById(R.id.ll_list_berita);

        shimmerLayout = view.findViewById(R.id.shimmerlayout);
        shimmer_kebutuhan = view.findViewById(R.id.shimmerlayout_kebutuhan);
    }

    private void setOnClickListener() {
        iv_back.setOnClickListener(this);
        tv_more_berita.setOnClickListener(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        shimmerLayout.startShimmer();
        shimmer_kebutuhan.startShimmer();
    }

    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        bundle.putString(DetailDonasiMasjidMusholaFragment.NAMA_MASJID_BUNDLE_KEY, Uid);
        switch (v.getId()) {
            case R.id.iv_back:
                getActivity().onBackPressed();
                break;

        }
    }


    private void detailDonasi(Bundle bundle) {
        DetailDonasiMasjidMusholaFragment detailDonasiMasjidMusholaFragment = new DetailDonasiMasjidMusholaFragment();
        detailDonasiMasjidMusholaFragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.donasi_frame_layout, detailDonasiMasjidMusholaFragment)
                .addToBackStack(null)
                .commit();
    }
}