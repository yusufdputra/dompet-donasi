package com.mydonate.fragment;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
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
import com.mydonate.adapter.DaftarKebutuhanAdapter;
import com.mydonate.adapter.SpinnerTujuanDonasiAdapter;
import com.mydonate.component.NumberTextWatcher;
import com.mydonate.data.PenyaluranProgramUmumData;

import java.util.ArrayList;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

public class UploadBuktiProgramUmumFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    public Double danaLebih;
    public Double biayaKebutuhan = 0.0;
    ArrayList<String> imgUrl = new ArrayList<>();
    private ImageView iv_back, iv_namaUploadBarang, iv_upStruk, iv_upPenyerahan;
    private Button btn_upload;
    private TextView nama_kebutuhan, dana_tersedia, namaStruk, namaPenyerahan;
    private Spinner sp_tujuan_dana;
    private LinearLayout llNamaMasjid, llJumlahKeb, llOperasional;
    private String getNamaMasjid, getAlamatMasjid;
    private TextInputEditText biaya, kuantitas, tujuan_operasional;
    private TextInputLayout textInputLayout_kuantitas;
    private int Image_Request_Code1 = 1, Image_Request_Code2 = 2, upload_count = 0;
    private ArrayList<Uri> ImgUriList = new ArrayList<>();
    private ArrayList<String> UrlString;
    private String idKebutuhan, namaKebutuhan, idUser, idPengurus;
    private StorageReference mStorageRef, mStorageRef_struk;
    private StorageTask mUploadTask;
    private DatabaseReference mDatabaseRef, mDbTransaksiPembayaran;

    private ArrayList<String> nama_spinner = new ArrayList<>();
    private ArrayList<String> alamat_spinner = new ArrayList<>();
    private ArrayList<String> tipe_spinner = new ArrayList<>();


    public UploadBuktiProgramUmumFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload_program_umum, container, false);
        if (getArguments() != null) {
            idKebutuhan = getArguments().getString(DetailMasjidMusholaFragment.KEY_ID);
            idUser = getArguments().getString(DaftarKebutuhanAdapter.KEY_ID_USER);
        }


        init(view);
        setClickListener();
        return view;
    }


    private void setClickListener() {
        iv_back.setOnClickListener(this);
        iv_upPenyerahan.setOnClickListener(this);
        btn_upload.setOnClickListener(this);

        sp_tujuan_dana.setOnItemSelectedListener(this);
    }


    private void init(View view) {
        iv_back = view.findViewById(R.id.iv_back);

        nama_kebutuhan = view.findViewById(R.id.tv_nama_kebutuhan);
        kuantitas = view.findViewById(R.id.tv_kuantitas);
        textInputLayout_kuantitas = view.findViewById(R.id.textInputLayout_kuantitas);
        biaya = view.findViewById(R.id.tv_biaya);
        dana_tersedia = view.findViewById(R.id.tv_nominalAsalDana);
        iv_upPenyerahan = view.findViewById(R.id.iv_upPenyerahanBarang);
        btn_upload = view.findViewById(R.id.btn_upload);
        namaPenyerahan = view.findViewById(R.id.tv_namaUploadBarang);
        biaya.addTextChangedListener(new NumberTextWatcher(biaya));

        llNamaMasjid = view.findViewById(R.id.llNamaMasjid);
        llJumlahKeb = view.findViewById(R.id.llJumlahKeb);
        llOperasional = view.findViewById(R.id.llOperasional);
        sp_tujuan_dana = view.findViewById(R.id.sp_tujuan_donasi);
        tujuan_operasional = view.findViewById(R.id.tv_tujuan_operasional);
        iv_namaUploadBarang = view.findViewById(R.id.iv_namaUploadBarang);


        mStorageRef = FirebaseStorage.getInstance().getReference("Bukti Penyerahan").child("Program Umum");
//        mStorageRef_struk = FirebaseStorage.getInstance().getReference("Struk Barang");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Penyaluran Program Umum");
//        mDbTransaksiPembayaran = FirebaseDatabase.getInstance().getReference("Transaksi Pembayaran");

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        Query db = FirebaseDatabase.getInstance().getReference().child("Program Umum").child(idKebutuhan);
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String nama = snapshot.child("nama").getValue(String.class);
                    String dana_sedia = snapshot.child("dana").getValue(String.class);
                    String dana_sedia_conv = dana_sedia.replaceAll("[^a-zA-Z0-9]", "");

                    nama_kebutuhan.setText(nama);
                    dana_tersedia.setText("Rp. " + Currencyfy.currencyfy(Double.parseDouble(dana_sedia_conv), false, false));

                    disableButton();
                    Query db = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("tipe_user").equalTo("Pengurus");
                    db.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot np : snapshot.getChildren()) {
                                    String tipe = np.child("tipe_tempat").getValue(String.class);
                                    String nama = np.child("nama_masjid").getValue(String.class);
                                    String alamat = np.child("alamat_masjid").getValue(String.class);

                                    nama_spinner.add(nama);
                                    alamat_spinner.add(alamat);
                                    tipe_spinner.add(tipe);

                                    SpinnerTujuanDonasiAdapter adapterSpinner = new SpinnerTujuanDonasiAdapter(view.getContext(), nama_spinner, alamat_spinner, tipe_spinner);
                                    sp_tujuan_dana.setAdapter(adapterSpinner);

                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    // hide and show kuan / nama masjid
                    if (nama.equalsIgnoreCase("Renovasi Tempat Wudhu")) {
                        llJumlahKeb.setVisibility(View.GONE);
                        llNamaMasjid.setVisibility(View.VISIBLE);
                        llOperasional.setVisibility(View.GONE);
                    } else if (nama.equalsIgnoreCase("Operasional Aplikasi")) {
                        llJumlahKeb.setVisibility(View.GONE);
                        llNamaMasjid.setVisibility(View.GONE);
                        llOperasional.setVisibility(View.VISIBLE);
                    } else {
                        llOperasional.setVisibility(View.GONE);
                        llJumlahKeb.setVisibility(View.VISIBLE);
                    }

                    biaya.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void afterTextChanged(Editable editable) {
                            String getDAna = String.valueOf(biaya.getText());
                            String getDAna_con = getDAna.replaceAll("[^a-zA-Z0-9]", "");

                            //bandingkan
                            if (!getDAna_con.isEmpty()) {
                                if (Double.parseDouble(getDAna_con) <= Double.parseDouble(dana_sedia_conv)) {
                                    enableButton();
                                } else {
                                    disableButton();
                                }
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        super.onViewCreated(view, savedInstanceState);
    }

    private void disableButton() {
        if (getActivity() != null && isAdded()) {
            btn_upload.setBackground(getResources().getDrawable(R.drawable.background_disable_button));
            btn_upload.setTextColor(getResources().getColor(R.color.black_trans));
            btn_upload.setText("Dana Tidak Cukup");
            btn_upload.setEnabled(false);
        }

    }

    private void enableButton() {
        if (getActivity() != null && isAdded()) {
            btn_upload.setBackground(getResources().getDrawable(R.drawable.background_login_button));
            btn_upload.setTextColor(getResources().getColor(R.color.white));
            btn_upload.setText("Upload");
            btn_upload.setEnabled(true);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.iv_back:
                getActivity().onBackPressed();
                break;
            case R.id.iv_upPenyerahanBarang:
                ImgUriList.clear();
                SelectImage(Image_Request_Code2);
                break;
            case R.id.btn_upload:
                if (!validasiFotoPenyerahan(view)) {
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
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        try {

            String get_nama_masjid = getNamaMasjid;
            String get_alamat_masjid = getAlamatMasjid;
            String get_dana_donasi = String.valueOf(biaya.getText());
            String get_kuantitas = String.valueOf(kuantitas.getText());
            String get_operasional = String.valueOf(tujuan_operasional.getText());

            // update data dana program umum
            Query db = FirebaseDatabase.getInstance().getReference().child("Program Umum").child(idKebutuhan);
            db.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String dana_sedia = snapshot.child("dana").getValue(String.class);
                        String dana_sedia_conv = dana_sedia.replaceAll("[^a-zA-Z0-9]", "");

                        String dana_conv = get_dana_donasi.replaceAll("[^a-zA-Z0-9]", "");

                        // kurangi
                        Integer sisa = Integer.parseInt(dana_sedia_conv) - Integer.parseInt(dana_conv);

                        // simpan
                        FirebaseDatabase.getInstance().getReference().child("Program Umum").child(idKebutuhan).child("dana").setValue(Integer.toString(sisa));

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            // save data
            if (get_nama_masjid == null) {
                get_nama_masjid = get_operasional;
            }


            // id_program,  nama_masjid, dana_donasi, kuantitas, foto_penyerahan, alamat_masjid;
            PenyaluranProgramUmumData value = new PenyaluranProgramUmumData(
                    idKebutuhan,
                    get_nama_masjid,
                    get_dana_donasi,
                    get_kuantitas,
                    null,
                    get_alamat_masjid
            );
            String key = mDatabaseRef.push().getKey();
            mDatabaseRef.child(key).setValue(value);

            HashMap<Integer, String> hashMap = new HashMap<>();
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
                                hashMap.put(finalI, taskSnapshot.getStorage().getName());
                                if (finalI == (ImgUriList.size() - 1)) {
                                    saveAll(key, hashMap, progressDialog, view);
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
                        });

            }


        } catch (Exception e) {
            Log.i(TAG, "error " + e);
            progressDialog.cancel();
            Snackbar snackbar = Snackbar
                    .make(view, "Terjadi Kesalahan Saat Upload! ", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    private void saveAll(String key, HashMap<Integer, String> hashMap, ProgressDialog progressDialog, View view) {
        for (int i = 0; i < hashMap.size(); i++) {
            mDatabaseRef.child(key).child("foto_penyerahan").child(String.valueOf(i)).setValue(hashMap.get(i));
        }


        Snackbar snackbar = Snackbar
                .make(view, "Berhasil Menambah Upload Program Umum! ", Snackbar.LENGTH_LONG);
        snackbar.show();
        progressDialog.cancel();
    }

    //method pilih gambar
    public void SelectImage(int request) {
        Intent i = new Intent();
        i.setType("image/*");
        i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i, request);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            ClipData cd = data.getClipData();
            if (requestCode == Image_Request_Code2 && resultCode == RESULT_OK) {
                Log.i(TAG, "CD : " + cd);

                if (cd == null) {
                    Uri uri = data.getData();
                    ImgUriList.add(uri);
                    iv_namaUploadBarang.setVisibility(View.VISIBLE);
                } else {
                    for (int i = 0; i < cd.getItemCount(); i++) {
                        ClipData.Item item = cd.getItemAt(i);
                        Uri uri = item.getUri();

                        ImgUriList.add(uri);
                        iv_namaUploadBarang.setVisibility(View.VISIBLE);
                    }

                }
            }
        }


    }


    private String getFileExtension(Uri filepath) {
        ContentResolver cR = getActivity().getApplicationContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType((Uri) filepath));
    }


    private boolean validasiFotoPenyerahan(View v) {
        if (ImgUriList.size() <= 0) {
            Snackbar snackbar = Snackbar
                    .make(v, "Silahkan upload foto penyerahan terlebih dahulu!", Snackbar.LENGTH_LONG);
            snackbar.show();
            return false;
        } else {
            return true;
        }
    }

    private boolean validasiKuantitas(View v) {

        String kuantitas_ = textInputLayout_kuantitas.getEditText().getText().toString().trim();
        if (kuantitas_.isEmpty()) {
            Snackbar snackbar = Snackbar
                    .make(v, "Silahkan isi kuantitas terlebih dahulu!", Snackbar.LENGTH_LONG);
            snackbar.show();
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        getNamaMasjid = tipe_spinner.get(i) + " " + nama_spinner.get(i);
        getAlamatMasjid = alamat_spinner.get(i);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
