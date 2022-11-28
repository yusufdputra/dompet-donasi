package com.mydonate.adapter;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jpvs0101.currencyfy.Currencyfy;
import com.mydonate.R;
import com.mydonate.data.DanaData;
import com.mydonate.data.KebutuhanData;
import com.mydonate.data.PengurusData;
import com.mydonate.fragment.DetailMasjidMusholaFragment;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class DaftarKelolaDanaUmumAdapter extends RecyclerView.Adapter<DaftarKelolaDanaUmumAdapter.daftarMasjidViewHolder> {
    private ArrayList<DanaData> danaData;
    private ArrayList<KebutuhanData> kebutuhanData;
    private ArrayList<String> keyItem;
    private ArrayList<String> keyIdPengurus;
    private ArrayList<String> keyItemKebutuhan;
    private static final String KEY_MASJID = "Masjid";
    private static final String KEY_MUSHOLA = "Mushola";

    private Context mContext;


    public DaftarKelolaDanaUmumAdapter(Context context, ArrayList<DanaData> danaData, ArrayList<String> keyItem, ArrayList<String> keyIdPengurus) {
        this.mContext = context;
        this.danaData = danaData;
        this.keyItem = keyItem;
        this.keyIdPengurus = keyIdPengurus;
    }

    @NonNull
    @Override
    public daftarMasjidViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_kelola_dana_umum, parent, false);
        DaftarKebutuhanAdapter.ViewHolder viewHolder = new DaftarKebutuhanAdapter.ViewHolder(view);
        mContext = view.getContext();
        return new daftarMasjidViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final daftarMasjidViewHolder holder, int position) {
        if (kebutuhanData != null) {
            kebutuhanData.clear();
            keyItemKebutuhan.clear();
        } else {
            kebutuhanData = new ArrayList<>();
            keyItemKebutuhan = new ArrayList<>();
        }

        if (keyItemKebutuhan != null) {

            keyItemKebutuhan.clear();
        } else {

            keyItemKebutuhan = new ArrayList<>();
        }
        DanaData id = danaData.get(position);


        holder.depositMasjid.setText("Rp. " + Currencyfy.currencyfy(id.getTotal_dana(), false, false));
        Query dbref = FirebaseDatabase.getInstance().getReference().child("Users").child(id.getId_pengurus());
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String tipe_tempat = snapshot.child("tipe_tempat").getValue(String.class);
                String nama_masjid = snapshot.child("nama_masjid").getValue(String.class);
                holder.namaMasjid.setText(tipe_tempat + " " + nama_masjid);

                // get kebutuhan yg belum terbayarkan
//                Query dbK = FirebaseDatabase.getInstance().getReference().child("Kebutuhan").orderByChild("id_pengurus").equalTo(id.getId_pengurus());
//                dbK.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        keyItemKebutuhan.clear();
//                        kebutuhanData.clear();
//                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                            String id_kebutuhan = dataSnapshot.getKey();
//
//                            DatabaseReference dbBK = FirebaseDatabase.getInstance().getReference().child("Bayar Kebutuhan").child(id_kebutuhan);
//                            dbBK.addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                    if (snapshot.exists()) {
//
//                                        String sisa = snapshot.child("sisa_nominal_kebutuhan").getValue(String.class);
//                                        if (!sisa.equals("0")) {
//
//                                            KebutuhanData list = dataSnapshot.getValue(KebutuhanData.class);
//                                            kebutuhanData.add(list);
//                                            keyItemKebutuhan.add(dataSnapshot.getKey());
//
//                                            holder.rv_daftar_kebutuhan.setVisibility(View.VISIBLE);
//                                            holder.empty.setVisibility(View.GONE);
//
//
//                                            Log.v(TAG, "LIST DATA "+ kebutuhanData +" DAN "+ keyItemKebutuhan);
//
//                                            DaftarKebutuhanKelolaDUAdapter daftarKebutuhanKelolaDUAdapter = new DaftarKebutuhanKelolaDUAdapter(mContext, kebutuhanData, keyItemKebutuhan);
//
//                                            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
//                                            layoutManager.setReverseLayout(true);
//                                            layoutManager.setStackFromEnd(true);
//                                            holder.rv_daftar_kebutuhan.setLayoutManager(layoutManager);
//                                            holder.rv_daftar_kebutuhan.setAdapter(daftarKebutuhanKelolaDUAdapter);
//
//                                        }
//
//                                    }
//                                }
//
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError error) {
//
//                                }
//                            });
//                        }
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString(DetailMasjidMusholaFragment.KEY_ID, keyIdPengurus.get(position));

                DetailMasjidMusholaFragment fragment = new DetailMasjidMusholaFragment();
                fragment.setArguments(bundle);
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.home_frame_layout, fragment).addToBackStack(null).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return danaData.size();
    }

    static class daftarMasjidViewHolder extends RecyclerView.ViewHolder {
        TextView namaMasjid, depositMasjid, namaKebutuhan, status, danaKurang, empty;
        RecyclerView rv_daftar_kebutuhan;

        daftarMasjidViewHolder(View itemView) {
            super(itemView);
            namaMasjid = itemView.findViewById(R.id.list_nama_masjid);
            depositMasjid = itemView.findViewById(R.id.list_deposit);
            namaKebutuhan = itemView.findViewById(R.id.list_nama_kebutuhan);
            status = itemView.findViewById(R.id.list_status);
            danaKurang = itemView.findViewById(R.id.list_dana_kurang);
            rv_daftar_kebutuhan = itemView.findViewById(R.id.rv_daftar_kebutuhan_adap);
            empty = itemView.findViewById(R.id.tv_empty);
        }
    }

}
