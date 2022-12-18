package com.mydonate.activity.Berita;

import static com.mydonate.activity.HelperActivity.validasiInput;
import static com.mydonate.adapter.ItemBeritaBigAdapter.KEY_ID_BERITA;

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
import com.mydonate.data.BeritaData;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CreateEditBeritaActivity extends AppCompatActivity implements View.OnClickListener {
  private int Image_Request_Code = 1;
  private String Uid, idBerita;
  private Uri filepath_berita;
  private ImageView iv_back, iv_image;
  private Button btn_save;
  private CardView cv_image;
  private TextInputLayout textInputLayout_title, textInputLayout_detail;
  private TextInputEditText tv_title, tv_detail;
  private StorageTask mUploadTask;
  private StorageReference mStorageRef;
  private DatabaseReference mDatabaseRef;
  private DatabaseReference ref_berita;
  private StorageReference ref_storage = FirebaseStorage.getInstance().getReference();

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_create_edit_berita);
    inits();
    setOnClickListener();
    Intent intent = getIntent();
    if (intent != null) {
      idBerita = intent.getStringExtra(KEY_ID_BERITA);
      if (idBerita != null) {
        getBerita();
      }
    }
  }

  private void setOnClickListener() {
    iv_back.setOnClickListener(this);
    btn_save.setOnClickListener(this);
    cv_image.setOnClickListener(this);
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

    ref_berita = FirebaseDatabase.getInstance().getReference().child("Berita");

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
    final ProgressDialog progressDialog = new ProgressDialog(this);
    progressDialog.setTitle("Loading...");
    progressDialog.setCanceledOnTouchOutside(false);
    progressDialog.show();

    if (filepath_berita != null) {
      saveImage(view);
    } else {
      saveDataBerita("");
    }

    progressDialog.dismiss();

    Snackbar snackbar = Snackbar
            .make(view, "Berhasil menambahkan data!", Snackbar.LENGTH_LONG);
    snackbar.show();
    finish();


  }


  private void saveImage(View view) {
    StorageReference fileReference_berita = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(filepath_berita));

    mUploadTask = fileReference_berita.putFile(filepath_berita)
            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
              @Override
              public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                saveDataBerita(taskSnapshot.getStorage().getName());
              }
            })
            .addOnFailureListener(new OnFailureListener() {
              @Override
              public void onFailure(@NonNull Exception e) {
                Snackbar snackbar = Snackbar
                        .make(view, "Terjadi Kesalahan Saat menyimpan!", Snackbar.LENGTH_LONG);
                snackbar.show();
              }
            })
            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
              @Override
              public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

              }
            });
  }

  private void saveDataBerita(String pathUpload) {
    Date c = Calendar.getInstance().getTime();
    SimpleDateFormat df = new SimpleDateFormat("dd-MMMM-yyyy hh:mm", Locale.getDefault());
    String formattedDate = df.format(c);
    BeritaData beritaData = new BeritaData(
            Uid,
            formattedDate,
            tv_title.getText().toString(),
            tv_detail.getText().toString(),
            pathUpload
    );

    if (idBerita != null) {
      mDatabaseRef.child(idBerita).setValue(beritaData);
    } else {
      String key = mDatabaseRef.push().getKey();
      mDatabaseRef.child(key).setValue(beritaData);
    }
  }

  private void getBerita() {
    ref_berita.child(idBerita).addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
        if (snapshot.exists()) {
          tv_title.setText(snapshot.child("title").getValue(String.class));
          tv_detail.setText(snapshot.child("detail").getValue(String.class));
          String getImage = snapshot.child("image").getValue(String.class);
          if (getImage != "") {
            //set image to layout
            StorageReference dateRef = ref_storage.child("Berita/" + getImage);
            dateRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
              @Override
              public void onSuccess(Uri uri) {
                Picasso.get().load(uri).fetch(new Callback() {
                  @Override
                  public void onSuccess() {
                    iv_image.setAlpha(0f);
                    Picasso.get().load(uri).into(iv_image);
                    iv_image.animate().setDuration(300).alpha(1f).start();
                  }

                  @Override
                  public void onError(Exception e) {

                  }
                });
              }
            });
          }

        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {

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
