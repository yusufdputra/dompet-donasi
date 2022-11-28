package com.mydonate.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
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
import com.mydonate.adapter.DaftarRiwayatPembayaranAdapter;
import com.mydonate.data.DanaData;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import static android.content.ContentValues.TAG;

public class DetailVerifikasiPembayaranFragment extends Fragment implements View.OnClickListener {
    public final static String KEY_DANA_LEBIH = "Dana Lebih";
    public final static String KEY_DONASI_UMUM = "Donasi Umum";
    private String id_pembayaran, nama_donatur, nama_kebutuhan, nominal, foto_bukti, id_pengurus, nama_tujuan, jenis_tujuan, id_donatur, id_kebutuhan;
    private ImageView ivfoto_bukti, iv_back, photoView;
    private TextView tvnama_donatur, tvnama_kebutuhan, tvnominal;
    private Button btnTerima, btnPeringatan;
    private DatabaseReference mDatabaseRef, mDatabaseRefKebutuhan, getmDatabaseRefDana, mDatabaseRefBayarKebutuhan;
    private View popUpDialogView;
    private TextInputLayout textInputLayout_pesan;
    private EditText tv_pesan;
    private Button kirim, cancel;

    public DetailVerifikasiPembayaranFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_verif_pembayaran, container, false);
        if (getArguments() != null) {
            id_pembayaran = getArguments().getString(DaftarRiwayatPembayaranAdapter.KEY_ID_PEMBAYARAN);

            getDetail();
        }
        init(view);
        setOnClickListener();
        return view;
    }

    private void getDetail() {

        // get data riwayat pembayaran
        DatabaseReference listDb = FirebaseDatabase.getInstance().getReference().child("Riwayat Pembayaran").child(id_pembayaran);
        listDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    foto_bukti = snapshot.child("foto_bukti_pembayaran").getValue(String.class);
                    id_donatur = snapshot.child("id_donatur").getValue(String.class);
                    id_kebutuhan = snapshot.child("id_kebutuhan").getValue(String.class);
                    nominal = snapshot.child("nominal_bayar").getValue(String.class);

                    tvnominal.append(nominal);

                    //set to layout image bukti pembayaran
                    StorageReference ref = FirebaseStorage.getInstance().getReference();
                    StorageReference dateRef = ref.child("Bukti Pembayaran/" + foto_bukti);
                    dateRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get().load(uri).fetch(new Callback() {
                                @Override
                                public void onSuccess() {
                                    ivfoto_bukti.setAlpha(0f);
                                    Picasso.get()
                                            .load(uri)
                                            .placeholder(R.drawable.background_splash)
                                            .into(ivfoto_bukti);
                                    ivfoto_bukti.animate().setDuration(300).alpha(1f).start();
                                }

                                @Override
                                public void onError(Exception e) {

                                }
                            });
                        }
                    });
                    // get data donatur
                    DatabaseReference listDb3 = FirebaseDatabase.getInstance().getReference().child("Users").child(id_donatur);
                    listDb3.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            nama_donatur = snapshot.child("nama").getValue(String.class);

                            tvnama_donatur.append(nama_donatur);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    // get data kebutuhan
                    if (id_kebutuhan != null) {


                        DatabaseReference listDb2 = FirebaseDatabase.getInstance().getReference().child("Kebutuhan").child(id_kebutuhan);
                        listDb2.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                nama_kebutuhan = snapshot.child("nama_kebutuhan").getValue(String.class);
                                id_pengurus = snapshot.child("id_pengurus").getValue(String.class);

                                // get data pengurus
                                DatabaseReference listDb2 = FirebaseDatabase.getInstance().getReference().child("Users").child(id_pengurus);
                                listDb2.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        nama_tujuan = snapshot.child("nama_masjid").getValue(String.class);
                                        jenis_tujuan = snapshot.child("tipe_tempat").getValue(String.class);

                                        tvnama_kebutuhan.append(nama_kebutuhan + " - " + jenis_tujuan + " " + nama_tujuan);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    } else {
                        tvnama_kebutuhan.append("DONASI UMUM");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void setOnClickListener() {
        ivfoto_bukti.setOnClickListener(this);
        btnTerima.setOnClickListener(this);
        btnPeringatan.setOnClickListener(this);
        iv_back.setOnClickListener(this);
    }

    private void init(View view) {

        ivfoto_bukti = view.findViewById(R.id.imageView);
        tvnama_donatur = view.findViewById(R.id.tv_nama_donatur);
        tvnama_kebutuhan = view.findViewById(R.id.tv_nama_kebutuhan);
        tvnominal = view.findViewById(R.id.tv_nominal);

        btnPeringatan = view.findViewById(R.id.btn_peringatan);
        btnTerima = view.findViewById(R.id.btn_approve);
        iv_back = view.findViewById(R.id.iv_back);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Riwayat Pembayaran");
        mDatabaseRefKebutuhan = FirebaseDatabase.getInstance().getReference("Kebutuhan");
        getmDatabaseRefDana = FirebaseDatabase.getInstance().getReference("Dana");
        mDatabaseRefBayarKebutuhan = FirebaseDatabase.getInstance().getReference("Bayar Kebutuhan");
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.iv_back:
                getActivity().onBackPressed();
                break;
            case R.id.btn_peringatan:
                showPupUpWarning(view);
                break;
            case R.id.btn_approve:
                showPupUpApprove();

                break;
            case R.id.imageView:
                showPupUpImage(foto_bukti);
                break;
        }
    }

    private void showPupUpWarning(View view) {
        AlertDialog.Builder alerBuilder = new AlertDialog.Builder(view.getContext());
        alerBuilder.setTitle("Kirim pesan penolakan bukti pembayaran?");
        alerBuilder.setCancelable(false);
        initPopupViewControls(view);
        alerBuilder.setView(popUpDialogView);
        final AlertDialog alertDialog = alerBuilder.create();
        alertDialog.show();

        kirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pesan = tv_pesan.getText().toString();
                mDatabaseRef.child(id_pembayaran).child("pesan").setValue(pesan);
                Toast.makeText(getContext(), "Berhasil menolak pembayaran.", Toast.LENGTH_SHORT).show();

                alertDialog.cancel();
                getActivity().finish();
                Intent intent = null;
                intent = new Intent(getContext(), AdminActivity.class);
                intent.putExtra(AdminActivity.ACTIVITY_TYPE_KEY_EXTRA, HomeAdminFragment.KEY_KELOLA_DU);
                startActivity(intent);

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });

    }

    private void initPopupViewControls(View view) {
        //get layout
        LayoutInflater layoutInflater = LayoutInflater.from(view.getContext());
        popUpDialogView = layoutInflater.inflate(R.layout.popup_pesan_tolak_pembayaran, null);

        textInputLayout_pesan = popUpDialogView.findViewById(R.id.textInputLayout_pesan);

        tv_pesan = popUpDialogView.findViewById(R.id.tv_pesan);

        cancel = popUpDialogView.findViewById(R.id.btn_cancel);
        kirim = popUpDialogView.findViewById(R.id.btn_kirim);
    }

    private void showPupUpApprove() {

        AlertDialog.Builder alerBuilder = new AlertDialog.Builder(getContext());
        alerBuilder.setMessage("Apakah anda yakin untuk menyetujui pembayaran ini?");
        alerBuilder.setPositiveButton("Yakin", (dialogInterface, i) -> {
            mDatabaseRef.child(id_pembayaran).child("verified").setValue(true);
            // mengurangi biaya di kebutuhan
            mDatabaseRef.child(id_pembayaran).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot1) {

                    Object id_kebutuhan = snapshot1.child("id_kebutuhan").getValue();
                    String nominal_bayar = snapshot1.child("nominal_bayar").getValue(String.class);


                    if (id_kebutuhan != null) { // bukan donasi umum
                        Query DbRef = FirebaseDatabase.getInstance().getReference().child("Bayar Kebutuhan").child(String.valueOf(id_kebutuhan));
                        DbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshots) {
//                                String id_pengurus = snapshots.child("id_pengurus").getValue(String.class);

                                // get biaya kebutuhan
                                String biaya_kebutuhan = snapshots.child("sisa_nominal_kebutuhan").getValue(String.class);
                                String x = biaya_kebutuhan.replaceAll("[^a-zA-Z0-9]", "");
                                int biaya_final = (Integer.parseInt(x));

                                // get biaya donasi
                                String nominal_bayar = snapshot1.child("nominal_bayar").getValue(String.class);
                                String y = nominal_bayar.replaceAll("[^a-zA-Z0-9]", "");
                                int nominal_bayar_int = Integer.parseInt(y);

                                // mengurangi
                                int sisa_kebutuhan = (biaya_final - nominal_bayar_int);


                                // jika berlebih maka simpan ke tabungan masjid
                                if (sisa_kebutuhan < 0) {
                                    int to_positif = sisa_kebutuhan * -1;

                                    mDatabaseRefBayarKebutuhan.child(String.valueOf(id_kebutuhan)).child("sisa_nominal_kebutuhan").setValue("0");
                                    // cek apakah id pengurus sudah ada atau belum
                                    DatabaseReference DbRef = FirebaseDatabase.getInstance().getReference().child("Dana");
                                    DbRef.orderByChild("id_pengurus").equalTo(id_pengurus).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {

                                                for (DataSnapshot npsnapshot : snapshot.getChildren()) {

                                                    String id_keyDana = npsnapshot.getKey();
                                                    // get dana yang sudah ada
                                                    String dana_tersedia = npsnapshot.child("total_dana").getValue(String.class);

                                                    //pre proses
                                                    String x = dana_tersedia.replaceAll("[^a-zA-Z0-9]", "");
                                                    int dana_tersedia_int = Integer.parseInt(x);
                                                    //proses dana sisa + dana tersedia
                                                    int dana_total = to_positif + dana_tersedia_int;
                                                    //pra proses
                                                    NumberFormat formatter = new DecimalFormat("#,###");
                                                    String formattedNumber = formatter.format(dana_total);
                                                    // update dana sisa
                                                    getmDatabaseRefDana.child(id_keyDana).child("total_dana").setValue(formattedNumber);
                                                    break;
                                                }
                                            } else {
                                                NumberFormat formatter = new DecimalFormat("#,###");
                                                String formattedNumber = formatter.format(to_positif);
                                                // sisanya simpan ke tabungan masjid
                                                DanaData upload = new DanaData(
                                                        KEY_DANA_LEBIH,
                                                        id_pengurus,
                                                        Integer.parseInt(formattedNumber)

                                                );
                                                String key = getmDatabaseRefDana.push().getKey();
                                                getmDatabaseRefDana.child(key).setValue(upload);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                        }
                                    });

                                } else {
                                    NumberFormat formatter = new DecimalFormat("#,###");
                                    String formattedNumber = formatter.format(sisa_kebutuhan);
                                    mDatabaseRefBayarKebutuhan.child(String.valueOf(id_kebutuhan)).child("sisa_nominal_kebutuhan").setValue(formattedNumber);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    } else { // jika donasi umum
                        String x = nominal_bayar.replaceAll("[^a-zA-Z0-9]", "");
                        int nominal_donasi = Integer.parseInt(x);

                        Query DbRef = FirebaseDatabase.getInstance().getReference().child("Dana");
                        DbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    for (DataSnapshot npsnapshot : snapshot.getChildren()) {
                                        Object jenis = npsnapshot.child("jenis_dana").getValue();
                                        if (jenis.equals(KEY_DONASI_UMUM)) {
                                            // get dana tersedia
                                            String dana_tersedia = npsnapshot.child("total_dana").getValue(String.class);
                                            String idKey = npsnapshot.getKey();
                                            //pre proses
                                            String x = dana_tersedia.replaceAll("[^a-zA-Z0-9]", "");
                                            int dana_tersedia_int = Integer.parseInt(x);
                                            //proses
                                            int dana_total = nominal_donasi + dana_tersedia_int;
                                            //pra proses
                                            NumberFormat formatter = new DecimalFormat("#,###");
                                            String formattedNumber = formatter.format(dana_total);
                                            //simpan
                                            DanaData upload = new DanaData(
                                                    KEY_DONASI_UMUM,
                                                    "-",
                                                    Integer.parseInt(formattedNumber)
                                            );
                                            getmDatabaseRefDana.child(idKey).setValue(upload);
                                        }
                                    }
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

            getActivity().finish();
            Intent intent = null;
            intent = new Intent(getContext(), AdminActivity.class);
            intent.putExtra(AdminActivity.ACTIVITY_TYPE_KEY_EXTRA, HomeAdminFragment.KEY_KELOLA_DU);
            startActivity(intent);

            Toast.makeText(getContext(), "Berhasil menyetujui pembayaran.", Toast.LENGTH_SHORT).show();

        });

        alerBuilder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        alerBuilder.show();

    }

    private void showPupUpImage(String jenis_foto) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        View view = getLayoutInflater().inflate(R.layout.view_zoom_image, null);
        photoView = view.findViewById(R.id.iv_foto_view);
        setImageView(jenis_foto, photoView);

        mBuilder.setView(view);
        AlertDialog mDialog = mBuilder.create();

        mDialog.show();
        mBuilder.setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

    }


    private void setImageView(String Ufoto, ImageView iv_target) {
        //set image to layout
        StorageReference ref = FirebaseStorage.getInstance().getReference();
        StorageReference dateRef = ref.child("Bukti Pembayaran/" + Ufoto);
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

                    }
                });

            }
        });
    }
}

// btn terima, btn peringatan, view bukti img zoom belum
