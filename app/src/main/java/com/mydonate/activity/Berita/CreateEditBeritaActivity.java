package com.mydonate.activity.Berita;

import static com.mydonate.activity.HelperActivity.validasiInput;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.mydonate.R;
import com.mydonate.data.BeritaData;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CreateEditBeritaActivity extends AppCompatActivity implements View.OnClickListener {
  private int Image_Request_Code = 1;
  private String Uid;
  private Uri filepath_berita;
  private ImageView iv_back, iv_image;
  private Button btn_save;
  private CardView cv_image;
  private TextInputLayout textInputLayout_title, textInputLayout_detail;
  private TextInputEditText tv_title, tv_detail;
  private StorageTask mUploadTask;
  private StorageReference mStorageRef;
  private DatabaseReference mDatabaseRef;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_berita);
    inits();
  }

  private void inits() {
    iv_back = findViewById(R.id.iv_back);
    iv_image = findViewById(R.id.iv_image);
    btn_save = findViewById(R.id.btn_simpan);
    cv_image = findViewById(R.id.cv_image);
    textInputLayout_title = findViewById(R.id.textInputLayout_title);
    textInputLayout_detail = findViewById(R.id.textInputLayout_detail);
    tv_title = findViewById(R.id.tv_title);
    tv_detail = findViewById(R.id.tv_detail);

    mDatabaseRef = FirebaseDatabase.getInstance().getReference("Berita");
    mStorageRef = FirebaseStorage.getInstance().getReference("Berita");
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    Uid = currentUser.getUid();
  }

  @Override
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.iv_back:
        finish();
        break;
      case R.id.cv_image:
        SelectImage(Image_Request_Code);
        break;
      case R.id.btn_simpan:
        if (!validasiInput(textInputLayout_title) || !validasiInput(textInputLayout_detail)) {
          return;
        } else {
          simpanBerita(view);
        }
        break;
    }
  }

  private void simpanBerita(View view) {
    final ProgressDialog progressDialog = new ProgressDialog(getApplicationContext());
    progressDialog.setTitle("Loading...");
    progressDialog.setCanceledOnTouchOutside(false);
    progressDialog.show();

    Date c = Calendar.getInstance().getTime();
    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
    String formattedDate = df.format(c);

    StorageReference fileReference_berita = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(filepath_berita));

    mUploadTask = fileReference_berita.putFile(filepath_berita)
            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
              @Override
              public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                BeritaData beritaData = new BeritaData(
                        Uid,
                        formattedDate,
                        tv_title.getText().toString(),
                        tv_detail.getText().toString(),
                        taskSnapshot.getStorage().getName()
                );

                String key = mDatabaseRef.push().getKey();
                mDatabaseRef.child(key).setValue(beritaData);
                progressDialog.dismiss();

                Snackbar snackbar = Snackbar
                        .make(view, "Berhasil menambahkan data!", Snackbar.LENGTH_LONG);
                snackbar.show();
                finish();
              }
            })
            .addOnFailureListener(new OnFailureListener() {
              @Override
              public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Snackbar snackbar = Snackbar
                        .make(view, "Terjadi Kesalahan Saat menyimpan!", Snackbar.LENGTH_LONG);
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
    if (requestCode == Image_Request_Code && resultCode == RESULT_OK && data != null) {
      filepath_berita = data.getData();
      iv_image.setImageURI(filepath_berita);
    }
  }

  private String getFileExtension(Uri filepath) {
    ContentResolver cR = getApplicationContext().getContentResolver();
    MimeTypeMap mime = MimeTypeMap.getSingleton();
    return mime.getExtensionFromMimeType(cR.getType(filepath));
  }

}
