package com.mydonate.activity.Berita;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mydonate.R;
import com.mydonate.adapter.ItemBeritaBigAdapter;
import com.mydonate.data.BeritaData;
import com.mydonate.fragment.LoginFragment;

import java.util.ArrayList;
import java.util.Collections;

public class BeritaActivity extends AppCompatActivity {
  ItemBeritaBigAdapter adapter;
  private ImageView ivBack;
  private LinearLayout ll_tambah;
  private RecyclerView rv_riwayat_berita;
  private ArrayList<BeritaData> beritaDataArrayList;
  private ArrayList<String> keyItem;
  private TextView titleLayout, tv_no_data;
  private ShimmerFrameLayout shimmerlayout;
  private Boolean isDonatur = true;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_berita);

    init();
    Intent intent = getIntent();
    if (intent != null) {
      String idPengurus = intent.getStringExtra(LoginFragment.PENGURUS_LOGIN);
      if (idPengurus != null) {
        getBeritaGrid(idPengurus);
      } else {
        checkUserPengurus();
      }
    }

  }

  @Override
  protected void onResume() {
    super.onResume();

  }

  private void checkUserPengurus() {
    shimmerlayout.startShimmer();

    //cek tipe user
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    String userUid = currentUser.getUid();
    DatabaseReference jLoginDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userUid);
    jLoginDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
        String tipe = snapshot.child("tipe_user").getValue(String.class);
        if (tipe.equals(LoginFragment.PENGURUS_LOGIN)) {
          ll_tambah.setVisibility(View.VISIBLE);
          getBeritaGrid(userUid);
          isDonatur = false;
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {

      }
    });
  }


  private void init() {
    ivBack = findViewById(R.id.iv_back);
    ll_tambah = findViewById(R.id.ll_tambah);

    rv_riwayat_berita = findViewById(R.id.rv_riwayat_berita);
    shimmerlayout = findViewById(R.id.shimmerlayout);

    titleLayout = findViewById(R.id.tv_title);
    tv_no_data = findViewById(R.id.tv_no_data1);

    ll_tambah.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(getApplicationContext(), CreateEditBeritaActivity.class);
        getApplicationContext().startActivity(intent);
      }
    });
    ivBack.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        finish();
      }
    });
  }

  private void getBeritaGrid(String id_pengurus) {
    if (beritaDataArrayList != null) {
      beritaDataArrayList.clear();
      keyItem.clear();
    } else {
      beritaDataArrayList = new ArrayList<>();
      keyItem = new ArrayList<>();
    }

    // get list
    Query db = FirebaseDatabase.getInstance().getReference().child("Berita").orderByChild("id_pengurus").equalTo(id_pengurus);
    db.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
        beritaDataArrayList.clear();
        if (snapshot.exists()) {
          for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
            BeritaData data = dataSnapshot.getValue(BeritaData.class);
            beritaDataArrayList.add(data);
            keyItem.add(dataSnapshot.getKey());

          }
          Collections.reverse(beritaDataArrayList);
          Collections.reverse(keyItem);

          shimmerlayout.stopShimmer();
          shimmerlayout.setVisibility(View.GONE);
          rv_riwayat_berita.setVisibility(View.VISIBLE);
          tv_no_data.setVisibility(View.GONE);

          rv_riwayat_berita.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
          adapter = new ItemBeritaBigAdapter(getApplicationContext(), beritaDataArrayList, keyItem);
          rv_riwayat_berita.setAdapter(adapter);
        } else {
          shimmerlayout.stopShimmer();
          shimmerlayout.setVisibility(View.GONE);
          rv_riwayat_berita.setVisibility(View.GONE);
          tv_no_data.setVisibility(View.VISIBLE);
        }

      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {

      }
    });
  }

}
