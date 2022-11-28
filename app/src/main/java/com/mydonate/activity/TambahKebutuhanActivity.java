package com.mydonate.activity;

import static com.mydonate.activity.HelperActivity.validasiInput;

import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.mydonate.R;
import com.mydonate.component.NumberTextWatcher;
import com.mydonate.data.BayarKebutuhanData;
import com.mydonate.data.KebutuhanData;
import com.mydonate.fragment.DetailProfilePengurus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class TambahKebutuhanActivity extends AppCompatActivity implements View.OnClickListener {

  private final ArrayList<Uri> ImgUriList = new ArrayList<>();
  int Image_Request_Code2 = 2, Image_Request_Code3 = 3, Image_Request_Code4 = 4;
  private ImageView iv_back;
  private EditText nama_kebutuhan, biaya_kebutuhan, foto_kebutuhan, foto_detail_kebutuhan;
  private ImageView iv_namaUploadBarang;
  private TextInputLayout error_nama_kebutuhan, error_jumlah_kebutuhan, error_foto_kebutuhan, error_detail_kebutuhan;
  private Button popUp_simpan;
  private ProgressBar progressBar;
  private AppCompatSpinner sp_jenis_kebutuhan;
  private Uri filepath_kebutuhan, filepath_detail_kebutuhan;
  private String Uid;


  private DatabaseReference mDatabaseRefKebutuhan, mDatabaseRefBayarKeb;
  private StorageReference mStorageKeb;


  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_tambah_kebutuhan);

    Intent intent = getIntent();
    if (intent != null) {
      Uid = intent.getStringExtra(DetailProfilePengurus.UID_BUNDLE_KEY);
//      edit_harga_keb = intent.getStringExtra(DaftarKebutuhanAdapter.KEY_BIAYA_KEBUTUHAN);
//      id_kebutuhan = intent.getStringExtra(DaftarKebutuhanAdapter.KEY_ID_KEBUTUHAN);
//      nama_kebutuhan = intent.getStringExtra(DaftarKebutuhanAdapter.KEY_NAMA_KEBUTUHAN);
    }
    init();
    setClickListener();
  }

  private void init() {
    error_nama_kebutuhan = findViewById(R.id.textInputLayout_kebutuhan);
    error_jumlah_kebutuhan = findViewById(R.id.textInputLayout_biaya_kebutuhan);
    error_foto_kebutuhan = findViewById(R.id.textInputLayout_foto_kebutuhan);
    error_detail_kebutuhan = findViewById(R.id.textInputLayout_detail_kebutuhan);

    sp_jenis_kebutuhan = findViewById(R.id.sp_jenis_kebutuhan);
    nama_kebutuhan = findViewById(R.id.tv_add_kebutuhan);
    biaya_kebutuhan = findViewById(R.id.tv_add_biaya_kebutuhan);
    foto_detail_kebutuhan = findViewById(R.id.tv_detail_kebutuhan);
    biaya_kebutuhan.addTextChangedListener(new NumberTextWatcher(biaya_kebutuhan));
    foto_kebutuhan = findViewById(R.id.tv_add_foto_kebutuhan);
    iv_namaUploadBarang = findViewById(R.id.iv_namaUploadBarang);

    progressBar = findViewById(R.id.progressBar);
    iv_back = findViewById(R.id.iv_back);
    popUp_simpan = findViewById(R.id.btn_simpan);


    mDatabaseRefKebutuhan = FirebaseDatabase.getInstance().getReference("Kebutuhan");
    mDatabaseRefBayarKeb = FirebaseDatabase.getInstance().getReference("Bayar Kebutuhan");
    mStorageKeb = FirebaseStorage.getInstance().getReference("Kebutuhan");
  }

  private void setClickListener() {
    // jenis kebutuhan listener
    sp_jenis_kebutuhan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (i == 0) {
          error_detail_kebutuhan.setVisibility(View.VISIBLE);

        } else {
          error_detail_kebutuhan.setVisibility(View.GONE);

        }
      }

      @Override
      public void onNothingSelected(AdapterView<?> adapterView) {

      }
    });


    popUp_simpan.setOnClickListener(this);
    foto_kebutuhan.setOnClickListener(this);
    foto_detail_kebutuhan.setOnClickListener(this);

  }

  @Override
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.iv_back:
        finish();
        break;
      case R.id.btn_simpan:
        simpanData(view);
        break;
      case R.id.tv_add_foto_kebutuhan:
        SelectImageMultiple(Image_Request_Code3);
        break;
      case R.id.tv_detail_kebutuhan:
        SelectImage(Image_Request_Code4);
        break;
    }
  }

  private void simpanData(View view) {
    String jenis_kebutuhan = sp_jenis_kebutuhan.getSelectedItem().toString();
//                final ProgressDialog progressDialog = new ProgressDialog(getActivity());
    if (!validasiInput(error_nama_kebutuhan) || !validasiInput(error_jumlah_kebutuhan) || !validasiFotoKebutuhan()) {
      if (jenis_kebutuhan.equalsIgnoreCase("Kebutuhan Umum")) {
        if (!validasiFotoDetailKebutuhan()) {
          return;
        }
      } else {
        return;
      }

    } else {
      try {

        progressBar.setVisibility(View.VISIBLE);
        popUp_simpan.setVisibility(View.GONE);

        //get inputan
        String kebutuhan = nama_kebutuhan.getText().toString();
        String biaya = biaya_kebutuhan.getText().toString();
        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        //simpan
        KebutuhanData upload = new KebutuhanData(
                Uid,
                kebutuhan,
                biaya,
                null,
                date,
                jenis_kebutuhan,
                ""
        );

        String key = mDatabaseRefKebutuhan.push().getKey();
        mDatabaseRefKebutuhan.child(key).setValue(upload);

        // save data detail kebutuhan
        if (jenis_kebutuhan.equalsIgnoreCase("Kebutuhan Umum")) {
          StorageReference fileRef_detailKeb = mStorageKeb.child(System.currentTimeMillis() + "." + getFileExtension(filepath_detail_kebutuhan));
          StorageTask storageTaskDet = fileRef_detailKeb.putFile(filepath_detail_kebutuhan)
                  .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                      mDatabaseRefKebutuhan.child(key).child("detail_kebutuhan").setValue(taskSnapshot.getStorage().getName());
                    }
                  }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                      Toast.makeText(view.getContext(), "Terjadi Kesalahan Saat Upload Detail Kebutuhan! ", Toast.LENGTH_LONG).show();

                    }
                  });
        }

        // save data  foto kebutuhan;
        HashMap<Integer, String> hashMap = new HashMap<>();
        for (int i = 0; i < ImgUriList.size(); i++) {
          StorageReference fileReference_bukti = null;
          fileReference_bukti = mStorageKeb.child(System.currentTimeMillis() + "." + getFileExtension(ImgUriList.get(i)));

          int finalI = i;
          StorageTask storageTask = fileReference_bukti.putFile(ImgUriList.get(i))
                  .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                      // save
                      hashMap.put(finalI, taskSnapshot.getStorage().getName());
                      if (finalI == (ImgUriList.size() - 1)) {
                        saveAll(key, hashMap, view);
                      }

                    }
                  }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                      Snackbar snackbar = Snackbar
                              .make(view, "Terjadi Kesalahan Saat Upload! ", Snackbar.LENGTH_LONG);
                      snackbar.show();
                    }
                  }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                      double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());

                    }
                  }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {


                    }
                  });

        }
        //simpan bayar kebutuhan
        BayarKebutuhanData data = new BayarKebutuhanData(
                biaya,
                null,
                null
        );
        mDatabaseRefBayarKeb.child(key).setValue(data);

        Toast.makeText(getApplicationContext(), "Berhasil Menambah Kebutuhan", Toast.LENGTH_SHORT).show();


        finish();

      } catch (Exception e) {
        Snackbar snackbar = Snackbar
                .make(view, "Terjadi kesalahan saat menyimpan data! ", Snackbar.LENGTH_LONG);
        snackbar.show();
        progressBar.setVisibility(View.GONE);
        popUp_simpan.setVisibility(View.VISIBLE);
      }

    }

  }

  private void saveAll(String key, HashMap<Integer, String> hashMap, View view) {
    for (int i = 0; i < hashMap.size(); i++) {
      mDatabaseRefKebutuhan.child(key).child("foto_kebutuhan").child(String.valueOf(i)).setValue(hashMap.get(i));
    }
  }


  private boolean validasiFotoKebutuhan() {
    if (filepath_kebutuhan == null) {
      error_foto_kebutuhan.setError("Field tidak boleh kosong");
      return false;
    } else {
      error_foto_kebutuhan.setError(null);
      return true;
    }
  }

  private boolean validasiFotoDetailKebutuhan() {
    if (filepath_detail_kebutuhan == null) {
      error_detail_kebutuhan.setError("Field tidak boleh kosong");
      return false;
    } else {
      error_detail_kebutuhan.setError(null);
      return true;
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
    if (requestCode == Image_Request_Code2 && resultCode == RESULT_OK && data != null) {
      filepath_kebutuhan = data.getData();
      foto_kebutuhan.setText(filepath_kebutuhan.getLastPathSegment());
    } else if (requestCode == Image_Request_Code3 && resultCode == RESULT_OK && data != null) {
      ClipData cd = data.getClipData();
      if (cd == null) {
        filepath_kebutuhan = data.getData();
        Uri uri = data.getData();
        ImgUriList.add(uri);
        iv_namaUploadBarang.setVisibility(View.VISIBLE);
      } else {
        for (int i = 0; i < cd.getItemCount(); i++) {
          ClipData.Item item = cd.getItemAt(i);
          Uri uri = item.getUri();
          filepath_kebutuhan = uri;
          ImgUriList.add(uri);

          iv_namaUploadBarang.setVisibility(View.VISIBLE);
        }
      }
      foto_kebutuhan.setText("Data Dipilih");
    } else if (requestCode == Image_Request_Code4 && resultCode == RESULT_OK && data != null) {
      filepath_detail_kebutuhan = data.getData();
      foto_detail_kebutuhan.setText(filepath_detail_kebutuhan.getLastPathSegment());
    }
  }

  private String getFileExtension(Uri filepath) {
    ContentResolver cR = getApplicationContext().getContentResolver();
    MimeTypeMap mime = MimeTypeMap.getSingleton();
    return mime.getExtensionFromMimeType(cR.getType(filepath));
  }
}
