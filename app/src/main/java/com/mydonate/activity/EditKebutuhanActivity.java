package com.mydonate.activity;

import static com.mydonate.activity.HelperActivity.validasiNominal;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mydonate.R;
import com.mydonate.adapter.DaftarKebutuhanAdapter;
import com.mydonate.component.NumberTextWatcher;
import com.mydonate.data.BayarKebutuhanData;

public class EditKebutuhanActivity extends AppCompatActivity implements View.OnClickListener {

  private ImageView iv_back;
  private Button btn_simpan;
  private TextInputLayout textInputLayout_donasi;
  private EditText tv_donasi;
  private TextView tv_subtitle;
  private String id_kebutuhan, biaya_kebutuhan, nama_kebutuhan, sisa_kebutuhan_;
  private Integer donasi;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_edit_kebutuhan);

    Intent intent = getIntent();
    if (intent != null) {
      sisa_kebutuhan_ = intent.getStringExtra(DaftarKebutuhanAdapter.KEY_SISA_KEBUTUHAN);
      biaya_kebutuhan = intent.getStringExtra(DaftarKebutuhanAdapter.KEY_BIAYA_KEBUTUHAN);
      id_kebutuhan = intent.getStringExtra(DaftarKebutuhanAdapter.KEY_ID_KEBUTUHAN);
      nama_kebutuhan = intent.getStringExtra(DaftarKebutuhanAdapter.KEY_NAMA_KEBUTUHAN);


    }
    init();
    setClickListener();


  }

  private void setClickListener() {
    iv_back.setOnClickListener(this);
    btn_simpan.setOnClickListener(this);
  }

  private void init() {
    iv_back = findViewById(R.id.iv_back);
    tv_subtitle = findViewById(R.id.tv_subtitle);
    btn_simpan = findViewById(R.id.btn_simpan);
    textInputLayout_donasi = findViewById(R.id.textInputLayout_donasi);
    tv_donasi = findViewById(R.id.tv_donasi);
    tv_donasi.addTextChangedListener(new NumberTextWatcher(tv_donasi));

    tv_subtitle.setText("Tambah Kebutuhan " + nama_kebutuhan + " Karena Harga Pasar Naik?");
  }

  @SuppressLint("NonConstantResourceId")
  @Override
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.iv_back:
        finish();
        break;
      case R.id.btn_simpan:
        if (!validasiNominal(tv_donasi)) {
          return;
        } else {
          doAddKebutuhan(view);
        }
        break;
    }
  }

  private void doAddKebutuhan(View view) {

    //get inputan
    donasi = Integer.parseInt(tv_donasi.getText().toString().replaceAll("[^a-zA-Z0-9]", ""));

    Integer edit_harga = Integer.parseInt(sisa_kebutuhan_) + donasi;


    BayarKebutuhanData edit = new BayarKebutuhanData(
            Integer.toString(edit_harga),
            null,
            "benar"
    );


    Integer edit_harga_keb = Integer.parseInt(biaya_kebutuhan) + donasi;

    FirebaseDatabase.getInstance().getReference()
            .child("Kebutuhan")
            .child(id_kebutuhan)
            .child("biaya_kebutuhan")
            .setValue(Integer.toString(edit_harga_keb));

    DatabaseReference dbRefBK = FirebaseDatabase.getInstance().getReference()
            .child("Bayar Kebutuhan")
            .child(id_kebutuhan);
    dbRefBK.setValue(edit);
    Toast.makeText(view.getContext(), " Berhasil Tambah Biaya Kebutuhan", Toast.LENGTH_SHORT).show();


    finish();

  }
}
