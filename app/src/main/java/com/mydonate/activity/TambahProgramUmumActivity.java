package com.mydonate.activity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
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
import com.mydonate.component.FcmNotificationsSender;
import com.mydonate.data.ProgramUmumData;
import com.mydonate.fragment.DetailProgramUmumFragment;

public class TambahProgramUmumActivity extends AppCompatActivity implements View.OnClickListener {

  private ImageView iv_back, iv_foto;
  private TextView tv_title;
  private LinearLayout ll_upload;
  private Button btn_simpan;
  private EditText et_nama, et_detail;
  private TextInputLayout ti_nama, ti_detail;
  // Image request code for onActivityResult() .
  private int Image_Request_Code1 = 1;
  private Uri filepath_brosur;
  private StorageTask mUploadTask;
  private String Uid, UFoto;

  private StorageReference mStorageRef;
  private DatabaseReference dbReference;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_program_umum);

    init();
    setClickListener();

  }

  private void init() {
    iv_back = findViewById(R.id.iv_back);
    ll_upload = findViewById(R.id.ll_upload);
    btn_simpan = findViewById(R.id.btn_simpan);
    et_nama = findViewById(R.id.et_nama);
    et_detail = findViewById(R.id.et_detail);
    ti_nama = findViewById(R.id.TI_nama);
    ti_detail = findViewById(R.id.TI_detail);
    iv_foto = findViewById(R.id.iv_foto);
    tv_title = findViewById(R.id.tv_title);


    mStorageRef = FirebaseStorage.getInstance().getReference("Program Umum");
    dbReference = FirebaseDatabase.getInstance().getReference("Program Umum");

    // jika edit
    // cek uid
    Intent intent = getIntent();
    if (intent != null) {
      Uid = intent.getStringExtra(DetailProgramUmumFragment.KEY_ID);
      if (Uid != null) {
        getData();
      }

    }
  }

  private void getData() {
    DatabaseReference listDb = FirebaseDatabase.getInstance().getReference().child("Program Umum").child(Uid);
    listDb.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {

        if (snapshot.exists()) {
          String UNama = snapshot.child("nama").getValue(String.class);
          String UKeterangan = snapshot.child("keterangan").getValue(String.class);
          UFoto = snapshot.child("foto").getValue(String.class);


          tv_title.setText("Ubah " + UNama);

          et_nama.setText(UNama);
          et_detail.setText(UKeterangan);

          HelperActivity.setImageView(UFoto, iv_foto, null);

        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {

      }
    });
  }

  private void setClickListener() {
    iv_back.setOnClickListener(this);
    ll_upload.setOnClickListener(this);
    btn_simpan.setOnClickListener(this);
  }

  @Override
  public void onClick(View view) {
    Intent intent = null;
    switch (view.getId()) {
      case R.id.iv_back:
        this.finish();
        break;
      case R.id.ll_upload:
        SelectImage(Image_Request_Code1);
        break;
      case R.id.btn_simpan:
        if (!validasiText(et_nama, ti_nama) || !validasiText(et_detail, ti_detail)) {
          return;
        }
        if (Uid == null) {
          tambahProgram(view);
        } else {
          ubahProgram(view);
        }
        break;
    }
  }


  private String getFileExtension(Uri filepath) {
    ContentResolver cR = this.getApplicationContext().getContentResolver();
    MimeTypeMap mime = MimeTypeMap.getSingleton();
    return mime.getExtensionFromMimeType(cR.getType(filepath));
  }

  private void ubahProgram(View view) {
    final ProgressDialog progressDialog = new ProgressDialog(this);
    progressDialog.setTitle("Loading...");
    progressDialog.setCanceledOnTouchOutside(false);
    progressDialog.show();

    dbReference.child(Uid).child("nama").setValue(et_nama.getText().toString());
    dbReference.child(Uid).child("keterangan").setValue(et_detail.getText().toString());

    // send notif
    FcmNotificationsSender notificationsSender = new FcmNotificationsSender(
            "/topics/all",
            "Yuk donasi sekarang!!! Satu Program Umum telah ditambahkan.",
            "Program :" + et_nama.getText().toString(),
            getApplicationContext(),
            TambahProgramUmumActivity.this
    );
    notificationsSender.SendNotifications();

    // jika mengubah foto
    if (filepath_brosur != null) {
      try {
        StorageReference fileReference_profile = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(filepath_brosur));
        mUploadTask = fileReference_profile.putFile(filepath_brosur)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                  @Override
                  public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    // hapus file lama
                    StorageReference dateRef_lama = mStorageRef.child(UFoto);
                    dateRef_lama.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                      @Override
                      public void onSuccess(Void aVoid) {
                        dbReference.child(Uid).child("foto").setValue(taskSnapshot.getStorage().getName());


                        progressDialog.cancel();
                        Toast.makeText(view.getContext(), "Berhasil Mengubah Data", Toast.LENGTH_SHORT).show();
                        finish();
                      }
                    }).addOnFailureListener(new OnFailureListener() {
                      @Override
                      public void onFailure(@NonNull Exception e) {
                        progressDialog.cancel();
                        Toast.makeText(view.getContext(), "Terjadi kesalahan saat menyimpan gambar. " + e.getMessage(), Toast.LENGTH_SHORT).show();
                      }
                    });

                  }
                })
                .addOnFailureListener(new OnFailureListener() {
                  @Override
                  public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Snackbar snackbar = Snackbar
                            .make(view, "Terjadi kesalahan saat menyimpan gambar.", Snackbar.LENGTH_LONG);
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
        progressDialog.cancel();
        Snackbar snackbar = Snackbar
                .make(view, "Terjadi kesalahan saat menyimpan gambar.", Snackbar.LENGTH_LONG);
        snackbar.show();
      }
    } else {
      progressDialog.cancel();
      Toast.makeText(view.getContext(), "Berhasil Mengubah Data", Toast.LENGTH_SHORT).show();
      finish();
    }
  }


  private void tambahProgram(View view) {
    if (!validasiFoto(view)) {
      return;
    }
    final ProgressDialog progressDialog = new ProgressDialog(this);
    progressDialog.setTitle("Loading...");
    progressDialog.setCanceledOnTouchOutside(false);
    progressDialog.show();

    try {
      StorageReference fileReference_profile = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(filepath_brosur));
      mUploadTask = fileReference_profile.putFile(filepath_brosur)
              .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                  ProgramUmumData value = new ProgramUmumData(
                          et_nama.getText().toString(),
                          et_detail.getText().toString(),
                          taskSnapshot.getStorage().getName(),
                          "0"
                  );

                  String key = dbReference.push().getKey();
                  dbReference.child(key).setValue(value);

                  // send notif
                  FcmNotificationsSender notificationsSender = new FcmNotificationsSender(
                          "/topics/all",
                          "Yuk donasi sekarang!!! Satu Program Umum telah ditambahkan.",
                          "Program :" + et_nama.getText().toString(),
                          getApplicationContext(),
                          TambahProgramUmumActivity.this
                  );
                  notificationsSender.SendNotifications();

                  progressDialog.dismiss();
                  Snackbar snackbar = Snackbar
                          .make(view, "Berhasil menyimpan data.", Snackbar.LENGTH_LONG);
                  snackbar.show();

                  // send notif
                  finish();


                }
              })
              .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                  progressDialog.dismiss();
                  Snackbar snackbar = Snackbar
                          .make(view, "Terjadi kesalahan.", Snackbar.LENGTH_LONG);
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
      progressDialog.cancel();
      Snackbar snackbar = Snackbar
              .make(view, "Terjadi kesalahan", Snackbar.LENGTH_LONG);
      snackbar.show();
    }


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
      filepath_brosur = data.getData();
      iv_foto.setImageURI(filepath_brosur);
    } else {
      Toast.makeText(getApplicationContext(), "Terjadi kesalahan saat mengambil gambar", Toast.LENGTH_LONG).show();
    }
  }

  private boolean validasiText(EditText getText, TextInputLayout inputLayout) {
    String text = getText.getText().toString().trim();
    if (text.isEmpty()) {
      inputLayout.setError("Field tidak boleh kosong");
      return false;
    } else {
      inputLayout.setError(null);
      return true;
    }
  }

  private boolean validasiFoto(View v) {
    if (filepath_brosur == null) {
      Snackbar snackbar = Snackbar
              .make(v, "Silahkan masukkan gambar masjid terlebih dahulu!", Snackbar.LENGTH_LONG);
      snackbar.show();
      return false;
    } else {
      return true;
    }
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
  }
}
