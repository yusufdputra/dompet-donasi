package com.mydonate.fragment;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.makeramen.roundedimageview.RoundedImageView;
import com.mydonate.R;
import com.mydonate.activity.AboutActivity;
import com.mydonate.activity.AuthActivity;
import com.mydonate.activity.TambahKebutuhanActivity;
import com.mydonate.adapter.DaftarKebutuhanAdapter;
import com.mydonate.data.KebutuhanData;
import com.mydonate.data.PengurusData;
import com.mydonate.fragment.item.ItemPencairanDanaDialogFragment;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DetailProfilePengurus extends Fragment implements View.OnClickListener {
  public final static String UID_BUNDLE_KEY = "UID";
  public final static String NAMA_TEMPAT_KEY = "NAMA_TEMPAT_KEY";
  public String errorMsg = "Opsss.. Terjadi Kesalahan.";
  public String SuccessMsg = "Berhasil";
  int Image_Request_Code1 = 1;
  private TextInputLayout error_nama_masjid, error_tempat_masjid, error_nama_pengurus, error_phone;
  private EditText nama_masj, alamat_masj, nama_peng, phone_peng;
  private ImageView iv_edit, iv_add_kebutuhan, iv_back, iv_logout, iv_about;
  private RoundedImageView iv_tempat;
  private TextView tv_title, tv_pencairan_dana;
  private String Uid, Unama_tempat, Ualamat_tempat, Ufoto_tempat, Unama, Uphone, Uemail, Ufoto_ktp, Ufoto_surat, Ualamat_pengurus, Utipe_tempat, Ufoto_sim, Ufoto_imb, edit_kebutuhan_key;
  private Button iv_batal, iv_simpan;
  private GridLayout gridLayout;
  private Uri filepath_profile;
  private StorageReference mStorageRef;
  private DatabaseReference mDatabaseRef;
  private StorageTask mUploadTask;


  //recycle view list kebutuhan
  private RecyclerView rv_list_kebutuhan;
  private static ArrayList<KebutuhanData> kebutuhanData = new ArrayList<>();
  private static ArrayList<KebutuhanData> kebutuhanDataUmum = new ArrayList<>();
  private static ArrayList<String> id_kebutuhanUmum = new ArrayList<>();
  private static ArrayList<String> keyItem = new ArrayList<>();

  private FirebaseAuth auth;
  private FirebaseAuth.AuthStateListener authListener;


  public DetailProfilePengurus() {

  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_detail_pengurus, container, false);
    init(view);
    GetDataUser();
    setClickListener();

    if (getArguments() != null) {
      edit_kebutuhan_key = getArguments().getString("KEY_EDIT_KEB");
    }

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

  private void GetKebutuhanUser() {
    kebutuhanData.clear();
    kebutuhanDataUmum.clear();
    id_kebutuhanUmum.clear();
    keyItem.clear();

    //get item list kebutuhan
    DatabaseReference listDb = FirebaseDatabase.getInstance().getReference().child("Kebutuhan");
    listDb.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {

        if (snapshot.exists()) {
          for (DataSnapshot npsnapshot : snapshot.getChildren()) {

            Object data = npsnapshot.child("id_pengurus").getValue();
            if (data.equals(Uid)) {

              String id_kebutuhan = npsnapshot.getKey();

              DatabaseReference listBK = FirebaseDatabase.getInstance().getReference().child("Bayar Kebutuhan").child(id_kebutuhan);
              listBK.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                  if (snapshot.exists()) {

                    // get kebutuhan kategori umum
                    if (npsnapshot.child("jenis_kebutuhan").getValue().equals("Kebutuhan Umum")) {
                      KebutuhanData keb = npsnapshot.getValue(KebutuhanData.class);
                      kebutuhanDataUmum.add(keb);
                      id_kebutuhanUmum.add(npsnapshot.getKey());
                    }


                    KebutuhanData list = npsnapshot.getValue(KebutuhanData.class);
                    kebutuhanData.add(list);
                    keyItem.add(snapshot.getKey());

                    DaftarKebutuhanAdapter daftarKebutuhanAdapter = new DaftarKebutuhanAdapter(getContext(), kebutuhanData, keyItem, edit_kebutuhan_key);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                    layoutManager.setReverseLayout(true);
                    layoutManager.setStackFromEnd(true);
                    rv_list_kebutuhan.setLayoutManager(layoutManager);
                    rv_list_kebutuhan.setAdapter(daftarKebutuhanAdapter);
                  }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
              });

            }
          }


        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {

      }
    });
  }

  public void GetDataUser() {
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    Uid = currentUser.getUid();

    DatabaseReference jLoginDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(Uid);
    jLoginDatabase.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
        Unama_tempat = snapshot.child("nama_masjid").getValue(String.class);
        Ualamat_tempat = snapshot.child("alamat_masjid").getValue(String.class);
        Unama = snapshot.child("nama_pengurus").getValue(String.class);
        Uphone = snapshot.child("phone").getValue(String.class);
        Ufoto_tempat = snapshot.child("foto_tempat").getValue(String.class);
        Uemail = snapshot.child("email").getValue(String.class);
        Ufoto_ktp = snapshot.child("foto_ktp").getValue(String.class);
        Ufoto_surat = snapshot.child("foto_surat_pernyataan").getValue(String.class);
        Ualamat_pengurus = snapshot.child("alamat_pengurus").getValue(String.class);
        Utipe_tempat = snapshot.child("tipe_tempat").getValue(String.class);
        Ufoto_sim = snapshot.child("foto_sim").getValue(String.class);
        Ufoto_imb = snapshot.child("foto_imb").getValue(String.class);

        //set image to layout
        StorageReference ref = FirebaseStorage.getInstance().getReference();
        StorageReference dateRef = ref.child("Users/" + Ufoto_tempat);
        dateRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
          @Override
          public void onSuccess(Uri uri) {
            Picasso.get().load(uri).fetch(new Callback() {
              @Override
              public void onSuccess() {
                iv_tempat.setAlpha(0f);
                Picasso.get().load(uri).into(iv_tempat);
                iv_tempat.animate().setDuration(300).alpha(1f).start();
              }

              @Override
              public void onError(Exception e) {

              }
            });
          }
        });
        tv_title.append(Unama_tempat);
        nama_masj.setText(Unama_tempat);
        alamat_masj.setText(Ualamat_tempat);
        nama_peng.setText(Unama);
        phone_peng.setText(Uphone);

      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {

      }
    });


  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);


  }

  private void setDisableForm() {
    error_nama_masjid.setEnabled(false);
    error_tempat_masjid.setEnabled(false);
    error_nama_pengurus.setEnabled(false);
    error_phone.setEnabled(false);
    gridLayout.setVisibility(View.GONE);
  }

  private void setEnableForm() {
    error_nama_masjid.setEnabled(true);
    error_tempat_masjid.setEnabled(true);
    error_nama_pengurus.setEnabled(true);
    error_phone.setEnabled(true);
    gridLayout.setVisibility(View.VISIBLE);
  }

  private void setClickListener() {
    iv_simpan.setOnClickListener(this);
    iv_batal.setOnClickListener(this);
    iv_edit.setOnClickListener(this);
    iv_logout.setOnClickListener(this);
    iv_add_kebutuhan.setOnClickListener(this);
    iv_back.setOnClickListener(this);
    iv_about.setOnClickListener(this);
    tv_pencairan_dana.setOnClickListener(this);
    // foto_kebutuhan.setOnClickListener(this);
  }


  @Override
  public void onClick(View v) {
    int id = v.getId();
    Intent intent;
    switch (id) {
      case R.id.btn_simpan:
        cekDataUser(v);
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
      case R.id.iv_add_kebutuhan:
        intent = new Intent(getContext(), TambahKebutuhanActivity.class);
        intent.putExtra(UID_BUNDLE_KEY, Uid);
        intent.putExtra(NAMA_TEMPAT_KEY, Unama_tempat);
        startActivity(intent);
        break;
      case R.id.iv_back:
        getActivity().onBackPressed();
        break;
      case R.id.iv_logout:
        logOut();
        break;
      case R.id.iv_about:
        intent = new Intent(getActivity(), AboutActivity.class);
        startActivity(intent);
        break;
      case R.id.tv_pencarian_dana:
        // get kebutuhan umum
        if (kebutuhanDataUmum == null || kebutuhanDataUmum.size() == 0) {
          Toast.makeText(getContext(), "Tidak ada kebutuhan umum.", Toast.LENGTH_LONG).show();
        } else {
          ItemPencairanDanaDialogFragment.newInstance(kebutuhanDataUmum, id_kebutuhanUmum, Uid).show(getActivity().getSupportFragmentManager(), "dialog");
        }
        break;
//            case R.id.tv_add_foto_kebutuhan:
//                SelectImage(Image_Request_Code2);
//                break;
    }
  }


  //method pilih gambar
  public void SelectImage(int request) {
    Intent i = new Intent();
    i.setType("image/*");
    i.setAction(Intent.ACTION_GET_CONTENT);
    startActivityForResult(i, request);
  }


  @Override
  public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == Image_Request_Code1 && resultCode == RESULT_OK && data != null) {
      filepath_profile = data.getData();
      iv_tempat.setImageURI(filepath_profile);
    }
  }

  private String getFileExtension(Uri filepath) {
    ContentResolver cR = getActivity().getApplicationContext().getContentResolver();
    MimeTypeMap mime = MimeTypeMap.getSingleton();
    return mime.getExtensionFromMimeType(cR.getType(filepath));
  }

  private void cekDataUser(View v) {

    if (!validasiNamaMasjid() | !validasiAlamatMasjid() | !validasiNamaPengurus() | !validasiPhone()) {
      return;
    } else {
      updateDataPengurus(v);
    }
  }


  private void updateDataPengurus(View v) {
    final ProgressDialog progressDialog = new ProgressDialog(getActivity());
    progressDialog.setTitle("Loading...");
    progressDialog.setCanceledOnTouchOutside(false);
    progressDialog.show();


    StorageReference ref = FirebaseStorage.getInstance().getReference();
    StorageReference dateRef_lama = ref.child("Users/" + Ufoto_tempat);
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
                      Toast.makeText(getContext(), SuccessMsg + " Mengubah Data", Toast.LENGTH_SHORT).show();

                    }
                  }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                      progressDialog.cancel();
                      Toast.makeText(getContext(), errorMsg + " Mengubah Data", Toast.LENGTH_SHORT).show();
                    }
                  });

                }
              }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                  progressDialog.cancel();
                  Toast.makeText(getContext(), errorMsg + " Mengubah Data", Toast.LENGTH_SHORT).show();
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
      UpdateData(Ufoto_tempat);

      Toast.makeText(getContext(), SuccessMsg + " Mengubah Data", Toast.LENGTH_SHORT).show();
    }
  }

  private void UpdateData(String foto) {
    PengurusData upload = new PengurusData(
            nama_peng.getText().toString(),
            Ualamat_pengurus,
            nama_masj.getText().toString(),
            alamat_masj.getText().toString(),
            phone_peng.getText().toString(),
            Uemail,
            foto,
            Ufoto_ktp,
            Ufoto_surat,
            true,
            LoginFragment.PENGURUS_LOGIN,
            Utipe_tempat,
            Ufoto_sim,
            Ufoto_imb);

    mDatabaseRef.child(Uid).setValue(upload);
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


  private void init(View view) {
    error_nama_masjid = view.findViewById(R.id.textInputLayout_nama_masjid);
    error_tempat_masjid = view.findViewById(R.id.textInputLayout_alamat_masjid);
    error_nama_pengurus = view.findViewById(R.id.textInputLayout_nama);
    error_phone = view.findViewById(R.id.textInputLayout_phone);
    iv_edit = view.findViewById(R.id.iv_edit);
    iv_logout = view.findViewById(R.id.iv_logout);
    iv_back = view.findViewById(R.id.iv_back);
    tv_title = view.findViewById(R.id.tv_title);
    iv_tempat = view.findViewById(R.id.iv_background);
    nama_masj = view.findViewById(R.id.tv_edit_nama_tempat);
    alamat_masj = view.findViewById(R.id.tv_edit_alamat_tempat);
    nama_peng = view.findViewById(R.id.tv_edit_nama_pengurus);
    phone_peng = view.findViewById(R.id.tv_edit_phone);
    iv_simpan = view.findViewById(R.id.btn_simpan);
    iv_batal = view.findViewById(R.id.btn_cancel);
    iv_add_kebutuhan = view.findViewById(R.id.iv_add_kebutuhan);
    iv_about = view.findViewById(R.id.iv_about);
    tv_pencairan_dana = view.findViewById(R.id.tv_pencarian_dana);

    gridLayout = view.findViewById(R.id.gridLayout);

    mStorageRef = FirebaseStorage.getInstance().getReference("Users");
    mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users");

    rv_list_kebutuhan = view.findViewById(R.id.rv_kebutuhan);
    auth = FirebaseAuth.getInstance();

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
    String alamat_masjid = error_tempat_masjid.getEditText().getText().toString().trim();
    if (alamat_masjid.isEmpty()) {
      error_tempat_masjid.setError("Field tidak boleh kosong");
      return false;
    } else {
      error_tempat_masjid.setError(null);
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

    GetKebutuhanUser();
    auth.addAuthStateListener(authListener);
  }

  @Override
  public void onResume() {
    super.onResume();
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
