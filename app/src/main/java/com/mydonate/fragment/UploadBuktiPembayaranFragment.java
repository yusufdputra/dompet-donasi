package com.mydonate.fragment;

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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.jpvs0101.currencyfy.Currencyfy;
import com.mydonate.R;
import com.mydonate.activity.DonasiActivity;
import com.mydonate.adapter.DaftarKebutuhanAdapter;
import com.mydonate.data.BayarKebutuhanData;
import com.mydonate.data.TransaksiPembayaranData;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

public class UploadBuktiPembayaranFragment extends Fragment implements View.OnClickListener {
    public Double danaLebih;
    public Double biayaKebutuhan = 0.0;
    int Image_Request_Code1 = 1, Image_Request_Code2 = 2;
    private ImageView iv_back, iv_bukti, iv_upStruk, iv_upPenyerahan;
    private Button btn_upload;
    private TextView nama_kebutuhan, biaya_kebutuhan, dana_tersedia, namaStruk, namaPenyerahan;
    private Spinner sp_asal_dana;
    private Uri filepath_struk, filepath_penyerahan;
    private ArrayList<Uri> ImgUriList = new ArrayList<>();
    private String idKebutuhan, namaKebutuhan, idUser, idPengurus;
    private StorageReference mStorageRef, mStorageRef_struk;
    private StorageTask mUploadTask;
    private DatabaseReference mDatabaseRef, mDbTransaksiPembayaran;


    public UploadBuktiPembayaranFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload_bukti_pembayaran, container, false);
        if (getArguments() != null) {
            idKebutuhan = getArguments().getString(DetailMasjidMusholaFragment.KEY_ID);
            idUser = getArguments().getString(DaftarKebutuhanAdapter.KEY_ID_USER);
            idPengurus = getArguments().getString(DaftarKebutuhanAdapter.KEY_ID_PENGURUS);
            namaKebutuhan = getArguments().getString(DaftarKebutuhanAdapter.KEY_NAMA_KEBUTUHAN);
            biayaKebutuhan = Double.parseDouble(getArguments().getString(DaftarKebutuhanAdapter.KEY_BIAYA_KEBUTUHAN));
            danaLebih = getArguments().getDouble(DaftarKebutuhanAdapter.KEY_DANA_LEBIH);
        }


        init(view);
        setClickListener();
        return view;
    }


    private void setClickListener() {
        iv_back.setOnClickListener(this);
        iv_upStruk.setOnClickListener(this);
        iv_upPenyerahan.setOnClickListener(this);
        btn_upload.setOnClickListener(this);

    }


    private void init(View view) {
        iv_back = view.findViewById(R.id.iv_back);

        nama_kebutuhan = view.findViewById(R.id.tv_nama_kebutuhan);
        biaya_kebutuhan = view.findViewById(R.id.tv_nominal);
        sp_asal_dana = view.findViewById(R.id.sp_asalDana);
        dana_tersedia = view.findViewById(R.id.tv_nominalAsalDana);
        iv_upStruk = view.findViewById(R.id.iv_upStrukBarang);
        iv_upPenyerahan = view.findViewById(R.id.iv_upPenyerahanBarang);
        btn_upload = view.findViewById(R.id.btn_upload);
        namaStruk = view.findViewById(R.id.tv_namaUploadStruk);
        namaPenyerahan = view.findViewById(R.id.tv_namaUploadBarang);

        mStorageRef = FirebaseStorage.getInstance().getReference("Bukti Penyerahan");
        mStorageRef_struk = FirebaseStorage.getInstance().getReference("Struk Barang");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Bayar Kebutuhan");
        mDbTransaksiPembayaran = FirebaseDatabase.getInstance().getReference("Transaksi Pembayaran");

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        biaya_kebutuhan.setText("Rp. " + Currencyfy.currencyfy(biayaKebutuhan, false, false));

        nama_kebutuhan.setText(namaKebutuhan);

        sp_asal_dana.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
//                    Toast.makeText(view.getContext(), "Silahkan Pilih Asal Dana", Toast.LENGTH_SHORT).show();
                    dana_tersedia.setText("Rp. -");
                    disableButton();
//                }else if (i == 2){ // dana umum
//                    // get dana donasi umum
//                    Query dbDU = FirebaseDatabase.getInstance().getReference().child("Dana").orderByChild("jenis_dana").equalTo(DonasiUmumFragment.KEY_NAMA_DONASI);
//                    dbDU.addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            if (snapshot.exists()){
//                                for (DataSnapshot npsnapshot : snapshot.getChildren()) {
//                                    Double DanaDonasiUmum = npsnapshot.child("total_dana").getValue(Double.class);
//
//                                    dana_tersedia.setText("Rp. " + Currencyfy.currencyfy(DanaDonasiUmum, false, false));
//
//                                    if (DanaDonasiUmum < biayaKebutuhan) {
//                                        disableButton();
//
//                                    }else {
//                                        enableButton();
//                                    }
//                                }
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//
//                        }
//                    });
                } else if (i == 1) {
                    // get dana donasi lebih
                    dana_tersedia.setText("Rp. " + Currencyfy.currencyfy(danaLebih, false, false));

                    if (danaLebih < biayaKebutuhan) {
                        disableButton();
                    } else {
                        enableButton();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        super.onViewCreated(view, savedInstanceState);
    }

    private void disableButton() {
        btn_upload.setBackground(getResources().getDrawable(R.drawable.background_disable_button));
        btn_upload.setTextColor(getResources().getColor(R.color.black_trans));
        btn_upload.setText("Dana Tidak Cukup");
        btn_upload.setEnabled(false);
    }

    private void enableButton() {
        btn_upload.setBackground(getResources().getDrawable(R.drawable.background_login_button));
        btn_upload.setTextColor(getResources().getColor(R.color.white));
        btn_upload.setText("Upload");
        btn_upload.setEnabled(true);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.iv_back:
                getActivity().onBackPressed();
                break;
            case R.id.iv_upStrukBarang:
                SelectImage(Image_Request_Code1);
                break;
            case R.id.iv_upPenyerahanBarang:
                SelectImageMultiple(Image_Request_Code2);
                break;
            case R.id.btn_upload:
                if (!validasiFotoStruk(view) || !validasiFotoPenyerahan(view)) {
                    return;
                } else {
                    uploadData(view);
                }

                break;
        }
    }

    private void uploadData(View view) {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Loading...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        try {
            HashMap<Integer, String> hashMap = new HashMap<>();

            StorageReference ref_struk = mStorageRef_struk.child(System.currentTimeMillis() + "." + getFileExtension(filepath_struk));
//            StorageReference ref_barang = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(filepath_penyerahan));

            mUploadTask = ref_struk.putFile(filepath_struk);

            // save foto dan set 0
            BayarKebutuhanData upload0 = new BayarKebutuhanData(
                    "0",
                    null,
                    null
            );
            mDatabaseRef.child(idKebutuhan).setValue(upload0);

            // save data  foto_penyerahan;
            for (int i = 0; i < ImgUriList.size(); i++) {
                StorageReference fileReference_bukti = null;
                fileReference_bukti = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(ImgUriList.get(i)));

                int finalI = i;
                StorageTask storageTask = fileReference_bukti.putFile(ImgUriList.get(i))
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // save

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
                                hashMap.put(finalI, task.getResult().getStorage().getName());
                                if (finalI == (ImgUriList.size() - 1)) {
                                    saveAll(idKebutuhan, hashMap, progressDialog, view);
                                }

                            }
                        });
            }


            // kurangi donasi umum / dana lebih
            String asal_dana = sp_asal_dana.getSelectedItem().toString();

            if (asal_dana.equals("Dana Umum")) {
                Query db = FirebaseDatabase.getInstance().getReference().child("Dana").orderByChild("jenis_dana").equalTo(DonasiUmumFragment.KEY_NAMA_DONASI);
                db.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot np : snapshot.getChildren()) {
                                Double DanaDonasiUmum = np.child("total_dana").getValue(Double.class);
                                // kurangi
                                Double sisa = DanaDonasiUmum - biayaKebutuhan;
                                Log.i(TAG, "SISA Keb " + biayaKebutuhan);

                                String key = np.getKey();
                                Log.i(TAG, "KEY " + key);

                                FirebaseDatabase.getInstance().getReference().child("Dana").child(key).child("total_dana").setValue(sisa);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            } else if (asal_dana.equals("Deposit")) {

                Query db = FirebaseDatabase.getInstance().getReference().child("Dana").orderByChild("id_pengurus").equalTo(idPengurus);
                db.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot np : snapshot.getChildren()) {
                                Double DanaDonasiUmum = np.child("total_dana").getValue(Double.class);
                                // kurangi
                                Double sisa = DanaDonasiUmum - biayaKebutuhan;
                                String key = np.getKey();
                                FirebaseDatabase.getInstance().getReference().child("Dana").child(key).child("total_dana").setValue(sisa);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            TransaksiPembayaranData upload = new TransaksiPembayaranData(
                    null,
                    null,
                    DonasiUmumFragment.KEY_NAMA_DONASI,
                    null,
                    Double.toString(biayaKebutuhan),
                    dateFormat.format(date),
                    "200",
                    idUser,
                    idKebutuhan,
                    null
            );

            String key = mDbTransaksiPembayaran.push().getKey();
            mDbTransaksiPembayaran.child(key).setValue(upload);
            progressDialog.dismiss();
//            goToHistoryClass();
            Toast.makeText(getContext(), "Berhasil upload bukti penyerahan.", Toast.LENGTH_SHORT).show();
            getActivity().finish();
//            getActivity().finish();



        } catch (Exception e) {
            progressDialog.cancel();
            Snackbar snackbar = Snackbar
                    .make(view, "Terjadi Kesalahan Saat Upload!", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    private void saveAll(String key, HashMap<Integer, String> hashMap, ProgressDialog progressDialog, View view) {
        for (int i = 0; i < hashMap.size(); i++) {
            mDatabaseRef.child(key).child("foto_bukti_donasi").child(String.valueOf(i)).setValue(hashMap.get(i));
        }
    }

    //method pilih gambar
    public void SelectImage(int request) {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i, request);
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
            filepath_struk = data.getData();
            namaStruk.setText(filepath_struk.getLastPathSegment());
        } else if (requestCode == Image_Request_Code2 && resultCode == RESULT_OK && data != null) {
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
//            filepath_penyerahan = data.getData();
            namaPenyerahan.setText(filepath_penyerahan.getLastPathSegment());
        }
    }


    private String getFileExtension(Uri filepath) {
        ContentResolver cR = getActivity().getApplicationContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(filepath));
    }

    private void goToHistoryClass() {
        getActivity().finish();
        Intent intent = null;
        intent = new Intent(getContext(), DonasiActivity.class);
        intent.putExtra(DonasiActivity.DONATION_TYPE_KEY_EXTRA, HomeDonaturFragment.USER_DONASIKU_KEY_EXTRA);
        startActivity(intent);
    }


    private boolean validasiFotoStruk(View v) {
        if (filepath_struk == null) {
            Snackbar snackbar = Snackbar
                    .make(v, "Silahkan upload foto struk barang terlebih dahulu!", Snackbar.LENGTH_LONG);
            snackbar.show();
            return false;
        } else {
            return true;
        }
    }

    private boolean validasiFotoPenyerahan(View v) {
        if (filepath_penyerahan == null) {
            Snackbar snackbar = Snackbar
                    .make(v, "Silahkan upload foto penyerahan terlebih dahulu!", Snackbar.LENGTH_LONG);
            snackbar.show();
            return false;
        } else {
            return true;
        }
    }
}
