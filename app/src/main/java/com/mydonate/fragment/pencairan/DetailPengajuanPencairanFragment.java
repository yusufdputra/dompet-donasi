package com.mydonate.fragment.pencairan;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.jpvs0101.currencyfy.Currencyfy;
import com.mydonate.R;
import com.mydonate.adapter.DaftarPengajuanPencairanDanaAdapter;
import com.mydonate.data.TransaksiPembayaranData;
import com.mydonate.fragment.DonasiUmumFragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class DetailPengajuanPencairanFragment extends Fragment implements View.OnClickListener {
    int Image_Request_Code1 = 1;
    private String id_pengurus, idKebutuhan, nominal_pencairan, id_pencairan, status, nominal_kebutuhan, idUser, keterangan;
    private ImageView iv_back, iv_upStrukBarang;
    private ImageSlider imageViewSlider;
    private Button btn_terima;
    private TextView nama_kebutuhan, tv_keterangan, nominal, nama_tempat, alamat_tempat, namaStruk, tv_nominal_kebutuhan;
    private ConstraintLayout llUpStruk;
    private Uri filepath_penyerahan;
    private ArrayList<Uri> ImgUriList = new ArrayList<>();

    private Integer nominal_kebutuhan_conv, nominal_pencairan_conv;
    private int countFotoPenyerahan;

    private StorageReference strRef_kebutuhan, strRef_bukti_penyerahan;

    private StorageTask mUploadTask;
    private DatabaseReference ref_kebutuhan, ref_pengurus, ref_pencairan_dana, ref_bayar_kebutuhan, mDbTransaksiPembayaran;

    public DetailPengajuanPencairanFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_pengajuan_pencairan, container, false);
        if (getArguments() != null) {
            id_pengurus = getArguments().getString(DaftarPengajuanPencairanDanaAdapter.KEY_ID_PENGURUS);
            idKebutuhan = getArguments().getString(DaftarPengajuanPencairanDanaAdapter.KEY_ID_KEBUTUHAN);
            nominal_pencairan = getArguments().getString(DaftarPengajuanPencairanDanaAdapter.KEY_NOMINAL);
            id_pencairan = getArguments().getString(DaftarPengajuanPencairanDanaAdapter.KEY_ID_PENCAIRAN);
            status = getArguments().getString(DaftarPengajuanPencairanDanaAdapter.KEY_ID_STATUS);
            keterangan = getArguments().getString(DaftarPengajuanPencairanDanaAdapter.KEY_KETERANGAN);
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            ;
            idUser = currentUser.getUid();
        }
        init(view);
        setClickListener();

        return view;
    }


    private void setClickListener() {
        iv_back.setOnClickListener(this);
        btn_terima.setOnClickListener(this);
        nama_tempat.setOnClickListener(this);
        iv_upStrukBarang.setOnClickListener(this);
    }


    private void init(View view) {
        iv_back = view.findViewById(R.id.iv_back);
        nama_kebutuhan = view.findViewById(R.id.tv_nama_kebutuhan);
        tv_keterangan = view.findViewById(R.id.tv_keterangan);
        nominal = view.findViewById(R.id.tv_nominal);
        imageViewSlider = view.findViewById(R.id.imageViewSlider);
        nama_tempat = view.findViewById(R.id.tv_nama_tempat);
        alamat_tempat = view.findViewById(R.id.tv_alamat_tempat);
        tv_nominal_kebutuhan = view.findViewById(R.id.tv_nominal_kebutuhan);

        namaStruk = view.findViewById(R.id.tv_namaUploadStruk);
        llUpStruk = view.findViewById(R.id.llUpStruk);
        iv_upStrukBarang = view.findViewById(R.id.iv_upStrukBarang);

        btn_terima = view.findViewById(R.id.btn_terima);
        strRef_kebutuhan = FirebaseStorage.getInstance().getReference("Kebutuhan");
        strRef_bukti_penyerahan = FirebaseStorage.getInstance().getReference("Bukti Penyerahan");



        ref_bayar_kebutuhan = FirebaseDatabase.getInstance().getReference("Bayar Kebutuhan");
        ref_kebutuhan = FirebaseDatabase.getInstance().getReference("Kebutuhan");
        ref_pengurus = FirebaseDatabase.getInstance().getReference("Users");
        ref_pencairan_dana = FirebaseDatabase.getInstance().getReference(getResources().getString(R.string.ref_pengajuan_pencairan));
        mDbTransaksiPembayaran = FirebaseDatabase.getInstance().getReference("Transaksi Pembayaran");

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (idKebutuhan != null) {

            if (status.equals("proses")) {
                llUpStruk.setVisibility(View.VISIBLE);
                btn_terima.setText("Upload Pembayaran");
            }

            tv_keterangan.setText(keterangan);

            nominal_pencairan_conv = Integer.valueOf(nominal_pencairan.replaceAll("[^a-zA-Z0-9]", ""));
            nominal.setText("Rp. " + Currencyfy.currencyfy(nominal_pencairan_conv, false, false));

            ref_kebutuhan.child(idKebutuhan).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String nama_keb = snapshot.child("nama_kebutuhan").getValue(String.class);
                    nama_kebutuhan.setText(nama_keb);
                    nominal_kebutuhan = snapshot.child("biaya_kebutuhan").getValue(String.class);

                    List<SlideModel> slideModels = new ArrayList<>();
                    // get

                    for (int i = 0; i < snapshot.child("foto_kebutuhan").getChildrenCount(); i++) {
                        strRef_kebutuhan = strRef_kebutuhan.child(snapshot.child("foto_kebutuhan").child(String.valueOf(i)).getValue(String.class));
                        strRef_kebutuhan.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                slideModels.add(new SlideModel(uri.toString(), ScaleTypes.CENTER_CROP));
                                imageViewSlider.setImageList(slideModels, ScaleTypes.CENTER_CROP);
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });

            ref_pengurus.child(id_pengurus).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        nama_tempat.setText(snapshot.child("nama_masjid").getValue().toString());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            ref_bayar_kebutuhan.child(idKebutuhan).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String sisa_nominal = snapshot.child("sisa_nominal_kebutuhan").getValue().toString();
                        Double sisa_nominal_conv = Double.valueOf(sisa_nominal.replaceAll("[^a-zA-Z0-9]", ""));

                        nominal_kebutuhan_conv = Integer.valueOf(nominal_kebutuhan.replaceAll("[^a-zA-Z0-9]", ""));

                        Log.i(TAG, "nominal_kebutuhan_conv: "+nominal_kebutuhan_conv);
                        Double terkumpul = nominal_kebutuhan_conv - sisa_nominal_conv;
                        countFotoPenyerahan = (int) snapshot.child("foto_bukti_donasi").getChildrenCount();

                        tv_nominal_kebutuhan.setText("Rp. " + Currencyfy.currencyfy(terkumpul, false, false));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        super.onViewCreated(view, savedInstanceState);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.iv_back:
                requireActivity().onBackPressed();
                break;

            case R.id.iv_upStrukBarang:
                SelectImageMultiple(Image_Request_Code1);
                break;

            case R.id.btn_terima:
                if (status.equals("baru")) {
                    ref_pencairan_dana.child(id_pencairan).child("status").setValue("proses").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getContext(), "Silahkan Lakukan Penyerahan dan Upload Bukti Penyerahan", Toast.LENGTH_LONG).show();
                            requireActivity().onBackPressed();
                        }
                    });
                } else {
                    if (!validasiFotoStruk(view)) {
                        return;
                    } else {
                        uploadData(view);
                    }
                }
                break;
        }
    }

    public void SelectImageMultiple(int request) {
        Intent i = new Intent();
        i.setType("image/*");
        i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i, request);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Image_Request_Code1 && resultCode == RESULT_OK && data != null) {
            ClipData cd = data.getClipData();
            if (cd == null) {
                filepath_penyerahan = data.getData();
                Uri uri = data.getData();
                ImgUriList.add(uri);
            } else {
                for (int i = 0; i < cd.getItemCount(); i++) {
                    ClipData.Item item = cd.getItemAt(i);
                    Uri uri = item.getUri();
                    filepath_penyerahan = uri;
                    ImgUriList.add(uri);

                }
            }
            namaStruk.setText(filepath_penyerahan.getLastPathSegment());
        }
    }

    private boolean validasiFotoStruk(View v) {
        if (filepath_penyerahan == null) {
            Snackbar snackbar = Snackbar
                    .make(v, "Silahkan upload foto penyerahan terlebih dahulu!", Snackbar.LENGTH_LONG);
            snackbar.show();
            return false;
        } else {
            return true;
        }
    }

    private String getFileExtension(Uri filepath) {
        ContentResolver cR = getActivity().getApplicationContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(filepath));
    }


    private void uploadData(View view) {
        final ProgressDialog progressDialog = new ProgressDialog(view.getContext());
        progressDialog.setTitle("Loading...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        try {
            HashMap<Integer, String> hashMap = new HashMap<>();
            // save foto dan set 0
            Integer sisa = nominal_kebutuhan_conv - nominal_pencairan_conv;

            ref_kebutuhan.child(idKebutuhan).child("biaya_kebutuhan").setValue(String.valueOf(sisa));

            // save data  foto_penyerahan;
            for (int i = 0; i < ImgUriList.size(); i++) {
                StorageReference fileReference_bukti = null;
                fileReference_bukti = strRef_bukti_penyerahan.child(System.currentTimeMillis() + "." + getFileExtension(ImgUriList.get(i)));

                int finalI = i;
                StorageTask storageTask = fileReference_bukti.putFile(ImgUriList.get(i))
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                hashMap.put(finalI, taskSnapshot.getStorage().getName());
                                if (finalI == (ImgUriList.size() - 1)) {
                                    saveAll(idKebutuhan, hashMap, progressDialog, view);
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.cancel();
                                Snackbar snackbar = Snackbar
                                        .make(view, "Terjadi Kesalahan Saat Upload! ", Snackbar.LENGTH_LONG);
                                snackbar.show();
                            }
                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                                progressDialog.setProgress((int) progress);
                            }
                        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {

                                } else {
                                    progressDialog.cancel();
                                    Snackbar snackbar = Snackbar
                                            .make(view, "Terjadi Kesalahan Saat Upload! ", Snackbar.LENGTH_LONG);
                                    snackbar.show();
                                }
                            }
                        });
            }

            // save
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            TransaksiPembayaranData upload = new TransaksiPembayaranData(
                    null,
                    null,
                    DonasiUmumFragment.KEY_NAMA_DONASI,
                    null,
                    Integer.toString(nominal_pencairan_conv),
                    dateFormat.format(date),
                    "200",
                    idUser,
                    idKebutuhan,
                    null
            );

            String key = mDbTransaksiPembayaran.push().getKey();
            mDbTransaksiPembayaran.child(key).setValue(upload);


            ref_pencairan_dana.child(id_pencairan).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Berhasil upload bukti penyerahan.", Toast.LENGTH_SHORT).show();
                    requireActivity().onBackPressed();
                }
            });


        } catch (Exception e) {
            progressDialog.cancel();
            Snackbar snackbar = Snackbar
                    .make(view, "Terjadi Kesalahan Saat Upload!", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    private void saveAll(String key, HashMap<Integer, String> hashMap, ProgressDialog progressDialog, View view) {
        int j = 0;
        Log.i(TAG, "saveAll: "+countFotoPenyerahan);
        for (int i = countFotoPenyerahan; i < (hashMap.size()+countFotoPenyerahan); i++) {
            ref_bayar_kebutuhan.child(key).child("foto_bukti_donasi").child(String.valueOf(i)).setValue(hashMap.get(j));
            j++;
        }
    }
}
