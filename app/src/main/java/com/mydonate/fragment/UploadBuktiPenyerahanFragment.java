package com.mydonate.fragment;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.denzcoskun.imageslider.ImageSlider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
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
import com.mydonate.R;
import com.mydonate.activity.AdminActivity;
import com.mydonate.adapter.DaftarDonasikuAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

public class UploadBuktiPenyerahanFragment extends Fragment implements View.OnClickListener {
    int Image_Request_Code1 = 1;
    String idPembayaran, idKebutuhan, nominalKebutuhan, urlBukti;
    private ImageView iv_back, iv_bukti;
    private ImageSlider iv_slider;
    private LinearLayout llPesan;
    private Button btn_upload;
    private CardView cupload_foto;
    private TextView nama_kebutuhan, nominal, nama_tempat, alamat_tempat;
    private Uri filepath_bukti;
    private ArrayList<Uri> ImgUriList = new ArrayList<>();
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    public UploadBuktiPenyerahanFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload_bukti_penyerahan, container, false);
        if (getArguments() != null) {
            idKebutuhan = getArguments().getString(DaftarDonasikuAdapter.KEY_ID_KEBUTUHAN);
        }
        init(view);
        setClickListener();
        //setImageView(urlBukti, iv_bukti);
        return view;
    }

//    private void setImageView(String Ufoto, ImageView iv_target) {
//        //set image to layout
//        StorageReference ref = FirebaseStorage.getInstance().getReference();
//        StorageReference dateRef = ref.child("Bukti Pembayaran/" + Ufoto);
//        dateRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//            @Override
//            public void onSuccess(Uri uri) {
//                Picasso.get().load(uri).fetch(new Callback() {
//                    @Override
//                    public void onSuccess() {
//                        iv_target.setAlpha(0f);
//                        Picasso.get()
//                                .load(uri)
//                                .placeholder(R.drawable.background_splash)
//                                .into(iv_target);
//                        iv_target.animate().setDuration(300).alpha(1f).start();
//                    }
//
//                    @Override
//                    public void onError(Exception e) {
//
//                    }
//                });
//
//            }
//        });
//    }

    private void setClickListener() {
        iv_back.setOnClickListener(this);
        cupload_foto.setOnClickListener(this);
        btn_upload.setOnClickListener(this);
    }


    private void init(View view) {
        iv_back = view.findViewById(R.id.iv_back);
        cupload_foto = view.findViewById(R.id.cv_upload_foto);
        nama_kebutuhan = view.findViewById(R.id.tv_nama_kebutuhan);
        nominal = view.findViewById(R.id.tv_nominal);
        iv_bukti = view.findViewById(R.id.imageView);
        nama_tempat = view.findViewById(R.id.tv_nama_tempat);
        alamat_tempat = view.findViewById(R.id.tv_alamat_tempat);

        btn_upload = view.findViewById(R.id.btn_upload);
        mStorageRef = FirebaseStorage.getInstance().getReference("Bukti Penyerahan");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Bayar Kebutuhan");

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (idKebutuhan != null) {
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Kebutuhan").child(idKebutuhan);
            dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String nama_keb = snapshot.child("nama_kebutuhan").getValue(String.class);
                    nama_kebutuhan.append(nama_keb);
                    String nominal_keb = snapshot.child("biaya_kebutuhan").getValue(String.class);
                    nominal.append(nominal_keb);
                    String id_pengurus = snapshot.child("id_pengurus").getValue(String.class);
                    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(id_pengurus);
                    dbRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                String tipe_tempat_ = snapshot.child("tipe_tempat").getValue(String.class);
                                String nama_tempat_ = snapshot.child("nama_masjid").getValue(String.class);
                                String alamat_tempat_ = snapshot.child("alamat_masjid").getValue(String.class);
                                nama_tempat.setText(tipe_tempat_ + " " + nama_tempat_);
                                alamat_tempat.setText(alamat_tempat_);
                            }
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
        }

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.iv_back:
                getActivity().onBackPressed();
                break;
            case R.id.cv_upload_foto:
                SelectImageMultiple(Image_Request_Code1);
                break;
            case R.id.btn_upload:
                if (!validasiBukti()) {
                    return;
                } else {
                    simpanGambar(view);
                }

                break;
        }
    }

    //method pilih gambar
    public void SelectImage(int request) {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i, Image_Request_Code1);
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
                filepath_bukti = data.getData();
                Uri uri = data.getData();
                ImgUriList.add(uri);
            } else {
                for (int i = 0; i < cd.getItemCount(); i++) {
                    ClipData.Item item = cd.getItemAt(i);
                    Uri uri = item.getUri();
                    filepath_bukti = uri;
                    ImgUriList.add(uri);

                }
            }
            iv_bukti.setImageURI(filepath_bukti);
//            filepath_bukti = data.getData();
//            iv_bukti.setImageURI(filepath_bukti);
        }
    }

    private void simpanGambar(View view) {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Loading...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        StorageReference ref = FirebaseStorage.getInstance().getReference();

//        StorageReference fileReference_bukti = null;

        if (filepath_bukti != null) {

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

        }
        progressDialog.cancel();
//        Intent intent = null;
//        intent = new Intent(getContext(), AdminActivity.class);
//        intent.putExtra(AdminActivity.ACTIVITY_TYPE_KEY_EXTRA, HomeAdminFragment.KEY_UPLOAD);
//        startActivity(intent);
        Toast.makeText(getContext(), "Berhasil upload bukti penyerahan.", Toast.LENGTH_SHORT).show();
        getActivity().finish();
    }

    private void saveAll(String key, HashMap<Integer, String> hashMap, ProgressDialog progressDialog, View view) {
        for (int i = 0; i < hashMap.size(); i++) {
            mDatabaseRef.child(key).child("foto_bukti_donasi").child(String.valueOf(i)).setValue(hashMap.get(i));
        }
    }

    private String getFileExtension(Uri filepath) {
        ContentResolver cR = getActivity().getApplicationContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(filepath));
    }

    private boolean validasiBukti() {
        if (filepath_bukti == null) {
            Toast.makeText(getContext(), "Silahkan pilih gambar.", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }
}
