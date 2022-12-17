package com.mydonate.activity.Berita;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mydonate.R;
import com.mydonate.adapter.ItemBeritaAdapter;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailBeritaActivity extends AppCompatActivity {
  private ImageView iv_back, iv_berita;
  private LinearLayout ll_edit, ll_content;
  private TextView tv_header, tv_detail, tv_nama_pengurus, tv_tanggal, tv_no_data;
  private CircleImageView iv_profil_pengurus;

  private DatabaseReference ref_berita, ref_user;
  private StorageReference ref_storage = FirebaseStorage.getInstance().getReference();

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_detail_berita);

    inits();
    Intent intent = getIntent();
    if (intent != null) {
      String idBerita = intent.getStringExtra(ItemBeritaAdapter.KEY_ID_BERITA);
      String idPengurus = intent.getStringExtra(ItemBeritaAdapter.KEY_ID_PENGURUS);
      getBerita(idBerita, idPengurus);
    }

  }


  private void inits() {
    iv_back = findViewById(R.id.iv_back);
    iv_profil_pengurus = findViewById(R.id.iv_profil_pengurus);
    iv_berita = findViewById(R.id.iv_berita);
    ll_edit = findViewById(R.id.ll_edit);
    ll_content = findViewById(R.id.ll_content);
    tv_header = findViewById(R.id.tv_header);
    tv_detail = findViewById(R.id.tv_detail);
    tv_nama_pengurus = findViewById(R.id.tv_nama_pengurus);
    tv_tanggal = findViewById(R.id.tv_tanggal);
    tv_no_data = findViewById(R.id.tv_no_data);


    ref_berita = FirebaseDatabase.getInstance().getReference().child("Dana");
    ref_user = FirebaseDatabase.getInstance().getReference().child("Users");


    iv_back.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        finish();
      }
    });

    ll_edit.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {

      }
    });
  }

  private void getBerita(String idBerita, String idPengurus) {
    ref_berita.child(idBerita).addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
        if (snapshot.exists()) {
          tv_header.setText(snapshot.child("title").getValue(String.class));
          tv_tanggal.setText(snapshot.child("created_at").getValue(String.class));
          tv_detail.setText(snapshot.child("detail").getValue(String.class));
          //set image to layout
          StorageReference dateRef = ref_storage.child("Berita/" + snapshot.child("image").getValue(String.class));
          dateRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
              Picasso.get().load(uri).fetch(new Callback() {
                @Override
                public void onSuccess() {
                  iv_berita.setAlpha(0f);
                  Picasso.get().load(uri).into(iv_berita);
                  iv_berita.animate().setDuration(300).alpha(1f).start();
                }

                @Override
                public void onError(Exception e) {

                }
              });
            }
          });


          // get data pengurus
          ref_user.child(idPengurus).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshotUser) {
              if (snapshotUser.exists()) {
                tv_nama_pengurus.setText(snapshotUser.child("alamat_masjid").getValue(String.class));
                //set image to layout
                StorageReference dateRef = ref_storage.child("Users/" + snapshotUser.child("foto_tempat").getValue(String.class));
                dateRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                  @Override
                  public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).fetch(new Callback() {
                      @Override
                      public void onSuccess() {
                        iv_profil_pengurus.setAlpha(0f);
                        Picasso.get().load(uri).into(iv_profil_pengurus);
                        iv_profil_pengurus.animate().setDuration(300).alpha(1f).start();
                      }

                      @Override
                      public void onError(Exception e) {

                      }
                    });
                  }
                });
              }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
          });
        } else {
          tv_no_data.setVisibility(View.VISIBLE);
          ll_content.setVisibility(View.GONE);
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {

      }
    });
  }
}
