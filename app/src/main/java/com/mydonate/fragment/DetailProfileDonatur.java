package com.mydonate.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
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
import com.mydonate.R;
import com.mydonate.activity.AboutActivity;
import com.mydonate.activity.AdminActivity;
import com.mydonate.activity.AuthActivity;
import com.mydonate.activity.HomeActivity;
import com.mydonate.activity.ProfilActivity;
import com.mydonate.data.DonatursData;
import com.mydonate.data.PengurusData;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import static android.app.Activity.RESULT_OK;

public class DetailProfileDonatur extends Fragment implements View.OnClickListener {
    private TextInputLayout error_nama, error_alias, error_email, error_phone;
    private EditText nama, alias, email, phone;
    private ImageView iv_profil, iv_back, iv_logout, iv_edit, iv_about;
    private Button simpan, cancel;
    private CardView cv_upload_foto;
    private GridLayout gridLayout;

    private String Uid, Unama, Ualias, Uemail, Uphone, Ufoto_profil;

    int Image_Request_Code1 = 1;
    private Uri filepath_profile;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private DatabaseReference mDatabaseRefKebutuhan;
    private StorageTask mUploadTask;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    Context context;

    public DetailProfileDonatur() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_donatur, container, false);
        init(view);
        GetDataUser();
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


        return view;
    }

    private void setClickListener() {
        simpan.setOnClickListener(this);
        cancel.setOnClickListener(this);
        cv_upload_foto.setOnClickListener(this);
        iv_logout.setOnClickListener(this);
        iv_edit.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        iv_about.setOnClickListener(this);
    }

    private void GetDataUser() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Uid = currentUser.getUid();

        DatabaseReference jLoginDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(Uid);
        jLoginDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Unama = snapshot.child("nama").getValue(String.class);
                Uemail = snapshot.child("email").getValue(String.class);
                Uphone = snapshot.child("phone").getValue(String.class);
                if (snapshot.child("alias").exists()) {
                    Ualias = snapshot.child("alias").getValue(String.class);
                } else {
                    Ualias = null;
                }

                Ufoto_profil = snapshot.child("foto_profile").getValue(String.class);

                //set image to layout
                StorageReference ref = FirebaseStorage.getInstance().getReference();
                StorageReference dateRef = ref.child("Users/" + Ufoto_profil);
                dateRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).fetch(new Callback() {
                            @Override
                            public void onSuccess() {
                                iv_profil.setAlpha(0f);
                                Picasso.get().load(uri).into(iv_profil);
                                iv_profil.animate().setDuration(300).alpha(1f).start();
                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        });
                    }
                });
                nama.setText(Unama);
                email.setText(Uemail);
                phone.setText(Uphone);
                alias.setText(Ualias);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void init(View view) {
        error_nama = view.findViewById(R.id.textInputLayout_nama_asli);
        error_alias = view.findViewById(R.id.textInputLayout_nama_alias);
        error_email = view.findViewById(R.id.textInputLayout_email);
        error_phone = view.findViewById(R.id.textInputLayout_phone);

        nama = view.findViewById(R.id.tv_edit_nama_asli);
        alias = view.findViewById(R.id.tv_edit_nama_alias);
        email = view.findViewById(R.id.tv_edit_email);
        phone = view.findViewById(R.id.tv_edit_phone);

        iv_profil = view.findViewById(R.id.profile_image);
        iv_back = view.findViewById(R.id.iv_back);
        iv_logout = view.findViewById(R.id.iv_logout);
        iv_edit = view.findViewById(R.id.iv_edit);
        cv_upload_foto = view.findViewById(R.id.cv_upload_foto);
        gridLayout = view.findViewById(R.id.gridLayout);
        simpan = view.findViewById(R.id.btn_simpan);
        cancel = view.findViewById(R.id.btn_cancel);
        iv_about = view.findViewById(R.id.iv_about);


        mStorageRef = FirebaseStorage.getInstance().getReference("Users");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users");

        auth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btn_simpan:
                cekDataUser(view);

                setDisableForm();
                break;
            case R.id.btn_cancel:
                setDisableForm();
                break;
            case R.id.cv_upload_foto:
                SelectImage(Image_Request_Code1);
                break;
            case R.id.iv_edit:
                setEnableForm();
                break;
            case R.id.tv_edit_nama_alias:
                Toast.makeText(getActivity(), "Nama ini akan digunakan sebagai nama donatur yang anda lakukan.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.iv_back:
                getActivity().onBackPressed();
                break;
            case R.id.iv_logout:
                logOut();
                break;
            case R.id.iv_about:
                Intent intent = new Intent(getActivity(), AboutActivity.class);
                startActivity(intent);
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


    private void setEnableForm() {
        cv_upload_foto.setVisibility(View.VISIBLE);
        error_nama.setEnabled(true);
        error_alias.setEnabled(true);
        error_phone.setEnabled(true);
        gridLayout.setVisibility(View.VISIBLE);
    }

    private void setDisableForm() {
        cv_upload_foto.setVisibility(View.GONE);
        error_nama.setEnabled(false);
        error_alias.setEnabled(false);
        error_phone.setEnabled(false);
        gridLayout.setVisibility(View.GONE);
    }

    //method pilih gambar
    public void SelectImage(int request) {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i, Image_Request_Code1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Image_Request_Code1 && resultCode == RESULT_OK && data != null) {
            filepath_profile = data.getData();
            iv_profil.setImageURI(filepath_profile);
        }
    }

    private void cekDataUser(View view) {
        if (!validasiNama() | !validasiPhone() | !validasiAlias()) {
            return;
        } else {
            updateData(view);

        }
    }

    private String getFileExtension(Uri filepath) {
        ContentResolver cR = getActivity().getApplicationContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(filepath));
    }

    private void updateData(View view) {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Loading...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        StorageReference ref = FirebaseStorage.getInstance().getReference();
        StorageReference dateRef_lama = ref.child("Users/" + Ufoto_profil);
        StorageReference fileReference_profile = null;

        if (filepath_profile != null) {
            fileReference_profile = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(filepath_profile));
            mUploadTask = fileReference_profile.putFile(filepath_profile)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //simpan data text
                            UpdateData(taskSnapshot.getStorage().getName());

                            dateRef_lama.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    progressDialog.cancel();
                                    Toast.makeText(getContext(), "Berhasil Mengubah Data", Toast.LENGTH_SHORT).show();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.cancel();
                                    Toast.makeText(getContext(), "Gagal Mengubah Data", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.cancel();
                            Toast.makeText(getContext(), "Gagal Mengubah Data", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                            double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                            progressDialog.setProgress((int) progress);
                        }
                    });
        } else {
            UpdateData(Ufoto_profil);

            Toast.makeText(getContext(), "Berhasil Mengubah Data", Toast.LENGTH_SHORT).show();
        }
    }

    private void UpdateData(String foto) {

        DonatursData upload = new DonatursData(
                nama.getText().toString(),
                phone.getText().toString(),
                email.getText().toString(),
                foto,
                LoginFragment.DONATUR_LOGIN,
                alias.getText().toString()
        );


        mDatabaseRef.child(Uid).setValue(upload);


//        DonasikuFragment donasikuFragment = new DonasikuFragment();
//        getActivity().getSupportFragmentManager().beginTransaction()
//                .replace(R.id.donasi_frame_layout, donasikuFragment)
//                .addToBackStack(null)
//                .commit();
//        Intent intent = new Intent(getActivity(), ProfilActivity.class);
//        intent.putExtra(HomeActivity.LOGIN_TYPE_KEY_EXTRA, LoginFragment.DONATUR_LOGIN);
//        startActivity(intent);
//        getActivity().finish();
    }


    private boolean validasiNama() {
        String nama = error_nama.getEditText().getText().toString().trim();
        if (nama.isEmpty()) {
            error_nama.setError("Field tidak boleh kosong");
            return false;
        } else {
            error_nama.setError(null);
            return true;
        }
    }

    private boolean validasiAlias() {
        String alias = error_alias.getEditText().getText().toString().trim();
        if (alias.isEmpty()) {
            error_alias.setError("Field tidak boleh kosong");
            return false;
        } else {
            error_alias.setError(null);
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
