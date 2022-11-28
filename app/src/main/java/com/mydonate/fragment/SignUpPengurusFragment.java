package com.mydonate.fragment;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.mydonate.R;
import com.mydonate.activity.AuthActivity;
import com.mydonate.activity.DonasiActivity;
import com.mydonate.data.PengurusData;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

public class SignUpPengurusFragment extends Fragment implements View.OnClickListener {
    public static final String MSG = "Akun berhasil didaftarkan. Silahkan tunggu admin melakukan verifikasi data anda. Terimakasih";
    private Button btnSignup;
    private TextView tvLogin;
    private EditText tv_nama_pengurus, tv_phone, tv_email, tv_password, tv_nama_masjid, tv_alamat_masjid, tv_alamat_pengurus;
    private Spinner sp_tipe_tempat;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private ImageView iv_foto_tempat, iv_foto_ktp, iv_foto_surat_pernyataan, iv_foto_sim, iv_foto_imb, iv_back;

    // Image request code for onActivityResult() .
    int Image_Request_Code1 = 1, Image_Request_Code2 = 2, Image_Request_Code3 = 3, Image_Request_Code4 = 4, Image_Request_Code5 = 5;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;

    private Uri filepath_ktp, filepath_masjid, filepath_surat, filepath_sim, filepath_imb;

    // validasi
    private TextInputLayout error_nama_masjid, error_alamat_masjid, error_nama_pengurus, error_alamat_pengurus, error_email, error_phone, error_password, error_tipe_tempat;
    public static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +         //at least 1 digit
                    //"(?=.*[a-z])" +         //at least 1 lower case letter
                    //"(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[a-zA-Z])" +      //any letter
                    // "(?=.*[@#$%^&+=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{6,}" +               //at least 4 characters
                    "$");


    public SignUpPengurusFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up_pengurus, container, false);
        init(view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setClickListener();

        iv_foto_tempat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectImage(Image_Request_Code1);
            }
        });

        iv_foto_ktp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectImage(Image_Request_Code2);
            }
        });

        iv_foto_surat_pernyataan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectImage(Image_Request_Code3);
            }
        });

        iv_foto_sim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectImage(Image_Request_Code4);
            }
        });
        iv_foto_imb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectImage(Image_Request_Code5);
            }
        });
    }

    //method pilih gambar
    public void SelectImage(int request) {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Silahkan pilih gambar"), request);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Image_Request_Code1 && resultCode == RESULT_OK && data != null) {
            filepath_masjid = data.getData();
            iv_foto_tempat.setImageURI(filepath_masjid);
        } else if (requestCode == Image_Request_Code2 && resultCode == RESULT_OK && data != null) {
            filepath_ktp = data.getData();
            iv_foto_ktp.setImageURI(filepath_ktp);
        } else if (requestCode == Image_Request_Code3 && resultCode == RESULT_OK && data != null) {
            filepath_surat = data.getData();
            iv_foto_surat_pernyataan.setImageURI(filepath_surat);
        } else if (requestCode == Image_Request_Code4 && resultCode == RESULT_OK && data != null) {
            filepath_sim = data.getData();
            iv_foto_sim.setImageURI(filepath_sim);
        }else if (requestCode == Image_Request_Code5 && resultCode == RESULT_OK && data != null) {
            filepath_imb = data.getData();
            iv_foto_imb.setImageURI(filepath_imb);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_sign_up:
                cekDataUser(v);
                break;
            case R.id.iv_back:
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                } else {
                    Snackbar snackbar = Snackbar
                            .make(v, "Terjadi kesalahan", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
        }
    }

    private void cekDataUser(View v) {

        String email = tv_email.getText().toString();
        String password = tv_password.getText().toString();

        if (!validasiEmail() || !validasiNamaMasjid() || !validasiAlamatMasjid() || !validasiNamaPengurus() || !validasiAlamatPengurus() || !validasiPhone()
                || !validasiPassword() || !validasiFotoTempat(v) || !validasiFotoKtp(v) || !validasiFotoSurat(v) || !validasiFotoSIM(v) || !validasiFotoIMB(v)) {
            return;
        } else {
            buatAkunBaru(v, email, password);
        }
    }

    private void uploadData(View v, String email, String password, String id_user) {

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCanceledOnTouchOutside(false);
        String nama_masjid = tv_nama_masjid.getText().toString();
        String alamat_masjid = tv_alamat_masjid.getText().toString();
        String nama_pengurus = tv_nama_pengurus.getText().toString();
        String alamat_pengurus = tv_alamat_pengurus.getText().toString();
        String phone = tv_phone.getText().toString();
        String sp_tempat = (sp_tipe_tempat.getSelectedItem().toString());

        try {
            HashMap<Integer, String> hashMap = new HashMap<>();

            StorageReference fileReference_masjid = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(filepath_masjid));
            StorageReference fileReference_ktp = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(filepath_ktp));
            StorageReference fileReference_surat = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(filepath_surat));
            StorageReference fileReference_sim = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(filepath_sim));
            StorageReference fileReference_imb = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(filepath_imb));


            mUploadTask = fileReference_ktp.putFile(filepath_ktp).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    hashMap.put(0, taskSnapshot.getStorage().getName());
                }
            });


            mUploadTask = fileReference_surat.putFile(filepath_surat).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    hashMap.put(1, taskSnapshot.getStorage().getName());
                }
            });

            mUploadTask = fileReference_sim.putFile(filepath_sim).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    hashMap.put(2, taskSnapshot.getStorage().getName());
                }
            });
            mUploadTask = fileReference_imb.putFile(filepath_imb).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    hashMap.put(4, taskSnapshot.getStorage().getName());
                }
            });

            mUploadTask = fileReference_masjid.putFile(filepath_masjid).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    hashMap.put(3, taskSnapshot.getStorage().getName());
                    PengurusData upload = new PengurusData(
                            nama_pengurus,
                            alamat_pengurus,
                            nama_masjid,
                            alamat_masjid,
                            phone,
                            email,
                            hashMap.get(3),
                            hashMap.get(0),
                            hashMap.get(1),
                            false,
                            LoginFragment.PENGURUS_LOGIN,
                            sp_tempat,
                            hashMap.get(2),
                            hashMap.get(4)
                            );

                    mDatabaseRef.child(id_user).setValue(upload);



                    auth.signOut();
                    progressDialog.dismiss();

                    Intent intent = new Intent(getContext(), AuthActivity.class);
                    intent.putExtra(LoginFragment.KEY_MSG, MSG);
                    startActivity(intent);
                    getActivity().finish();

//                    Bundle bundle = new Bundle();
//                    bundle.putString(LoginFragment.KEY_MSG, MSG);
//                    LoginFragment loginFragment = new LoginFragment();
//                    loginFragment.setArguments(bundle);
//                    getActivity().getSupportFragmentManager().beginTransaction()
//                            .replace(R.id.auth_frame_layout, loginFragment)
//                            .addToBackStack(null)
//                            .commit();
//
//                Intent intent = new Intent(getActivity(), AuthActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                startActivity(intent);

                    //getActivity().finish();


                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Snackbar snackbar = Snackbar
                                    .make(v, "Terjadi Kesalahan Saat Mendaftar!", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                            double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                            progressDialog.setProgress((int) progress);
                        }
                    });

        } catch (Exception e) {
            progressDialog.dismiss();
            Snackbar snackbar = Snackbar
                    .make(v, "Terjadi Kesalahan Saat Mendaftar!", Snackbar.LENGTH_LONG);
            snackbar.show();
        }


    }

    private void EmailVerifikasi() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                        }
                    }
                });
    }


    private String getFileExtension(Uri filepath) {
        ContentResolver cR = getActivity().getApplicationContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(filepath));
    }

    private void buatAkunBaru(View v, String email, String password) {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Loading...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    String idUser = currentUser.getUid();

                    uploadData(v, email, password, idUser);

                } else {
                    error_email.setError("Email yang digunakan sudah terdaftar");
                    progressDialog.dismiss();

                }
            }
        });
    }

    private void setClickListener() {
        btnSignup.setOnClickListener(this);
        tvLogin.setOnClickListener(this);
        iv_back.setOnClickListener(this);
//        iv_foto_ktp.setOnClickListener(this);
//        iv_foto_surat_pernyataan.setOnClickListener(this);
//        iv_foto_sim.setOnClickListener(this);
//        iv_foto_imb.setOnClickListener(this);
    }

    private void init(View view) {
        btnSignup = view.findViewById(R.id.btn_sign_up);
        tvLogin = view.findViewById(R.id.tv_login);

        sp_tipe_tempat = view.findViewById(R.id.sp_tempat_type);
        tv_nama_masjid = view.findViewById(R.id.tv_nama_masjidReg);
        tv_alamat_masjid = view.findViewById(R.id.tv_alamat_masjidReg);
        tv_alamat_pengurus = view.findViewById(R.id.tv_alamatReg);
        tv_nama_pengurus = view.findViewById(R.id.tv_nameReg);
        tv_phone = view.findViewById(R.id.tv_phoneReg);
        tv_email = view.findViewById(R.id.tv_emailReg);
        tv_password = view.findViewById(R.id.tv_passwordReg);
        iv_foto_tempat = view.findViewById(R.id.iv_foto_masjidReg);
        iv_foto_ktp = view.findViewById(R.id.iv_foto_ktpReg);
        iv_foto_surat_pernyataan = view.findViewById(R.id.iv_foto_pernyataanReg);
        iv_foto_sim = view.findViewById(R.id.iv_foto_SIMReg);
        iv_foto_imb = view.findViewById(R.id.iv_foto_IMBReg);
        iv_back = view.findViewById(R.id.iv_back);


        progressBar = view.findViewById(R.id.progressBar);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //new
        mStorageRef = FirebaseStorage.getInstance().getReference("Users");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users");

        //validasi

        error_nama_masjid = view.findViewById(R.id.textInputLayout_nama_masjid);
        error_alamat_masjid = view.findViewById(R.id.textInputLayout_alamat_masjd);
        error_nama_pengurus = view.findViewById(R.id.textInputLayout_nama);
        error_alamat_pengurus = view.findViewById(R.id.textInputLayout_alamat);
        error_email = view.findViewById(R.id.textInputLayout_email);
        error_phone = view.findViewById(R.id.textInputLayout_phone);
        error_password = view.findViewById(R.id.textInputLayout_password);

    }


    private boolean validasiNamaMasjid() {
        String nama_masjid = error_nama_masjid.getEditText().getText().toString().trim();
        if (nama_masjid.isEmpty()) {
            error_nama_masjid.setError("Field tidak boleh kosong");
            return false;
        } else {
            error_nama_masjid.setError(null);
            return true;
        }
    }

    private boolean validasiAlamatMasjid() {
        String alamat_masjid = error_alamat_masjid.getEditText().getText().toString().trim();
        if (alamat_masjid.isEmpty()) {
            error_alamat_masjid.setError("Field tidak boleh kosong");
            return false;
        } else {
            error_alamat_masjid.setError(null);
            return true;
        }
    }

    private boolean validasiNamaPengurus() {
        String nama_peng = error_nama_pengurus.getEditText().getText().toString().trim();
        if (nama_peng.isEmpty()) {
            error_nama_pengurus.setError("Field tidak boleh kosong");
            return false;
        } else {
            error_nama_pengurus.setError(null);
            return true;
        }
    }

    private boolean validasiAlamatPengurus() {
        String alamat_peng = error_alamat_pengurus.getEditText().getText().toString().trim();
        if (alamat_peng.isEmpty()) {
            error_alamat_pengurus.setError("Field tidak boleh kosong");
            return false;
        } else {
            error_alamat_pengurus.setError(null);
            return true;
        }
    }

    private boolean validasiPhone() {
        String phone = error_phone.getEditText().getText().toString().trim();
        if (phone.isEmpty()) {
            error_phone.setError("Field tidak boleh kosong");
            return false;
        } else if (phone.length() < 10) {
            error_phone.setError("Masukkan Nomor Hp dengan benar");
            return false;
        } else {
            error_phone.setError(null);
            return true;
        }
    }

    private boolean validasiEmail() {
        String email = error_email.getEditText().getText().toString().trim();
        if (email.isEmpty()) {
            error_email.setError("Field tidak boleh kosong");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            error_email.setError("Masukkan alamat email yang benar");
            return false;
        } else {
            error_email.setError(null);
            return true;
        }
    }

    private boolean validasiPassword() {
        String pw = error_password.getEditText().getText().toString().trim();
        if (pw.isEmpty()) {
            error_password.setError("Field tidak boleh kosong");
            return false;
        } else if (!PASSWORD_PATTERN.matcher(pw).matches()) {
            error_password.setError("Password lemah. Harus terdiri dari 1 [0-9] dan [a-zA-Z]. Minimal 4 karakter.");
            return false;
        } else if (pw.length() < 6) {
            error_password.setError("Minimal 4 karakter.");
            return false;
        } else {
            error_password.setError(null);
            return true;
        }
    }

    private boolean validasiFotoTempat(View v) {
        if (filepath_masjid == null) {
            Snackbar snackbar = Snackbar
                    .make(v, "Silahkan masukkan gambar masjid terlebih dahulu!", Snackbar.LENGTH_LONG);
            snackbar.show();
            return false;
        } else {
            return true;
        }
    }

    private boolean validasiFotoKtp(View v) {
        if (filepath_ktp == null) {
            Snackbar snackbar = Snackbar
                    .make(v, "Silahkan masukkan gambar KTP terlebih dahulu!", Snackbar.LENGTH_LONG);
            snackbar.show();
            return false;
        } else {
            return true;
        }
    }

    private boolean validasiFotoSurat(View v) {
        if (filepath_surat == null) {
            Snackbar snackbar = Snackbar
                    .make(v, "Silahkan masukkan gambar Surat Pernyataan terlebih dahulu!", Snackbar.LENGTH_LONG);
            snackbar.show();
            return false;
        } else {
            return true;
        }
    }

    private boolean validasiFotoSIM(View v) {
        if (filepath_sim == null) {
            Snackbar snackbar = Snackbar
                    .make(v, "Silahkan masukkan gambar Surat Izin Masjid terlebih dahulu!", Snackbar.LENGTH_LONG);
            snackbar.show();
            return false;
        } else {
            return true;
        }
    }

    private boolean validasiFotoIMB(View v) {
        if (filepath_imb == null) {
            Snackbar snackbar = Snackbar
                    .make(v, "Silahkan masukkan gambar Surat IMB terlebih dahulu!", Snackbar.LENGTH_LONG);
            snackbar.show();
            return false;
        } else {
            return true;
        }
    }


}