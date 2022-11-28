package com.mydonate.adapter;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mydonate.R;
import com.mydonate.data.PengurusData;
import com.mydonate.data.RiwayatPembayaranData;
import com.mydonate.fragment.DetailVerifikasiAkunPengurusFragment;
import com.mydonate.fragment.DetailVerifikasiPembayaranFragment;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class DaftarRiwayatPembayaranAdapter extends RecyclerView.Adapter<DaftarRiwayatPembayaranAdapter.daftarAkunViewHolder> {
    public final static String KEY_ID_PEMBAYARAN = "IDPemb";
    public final static String KEY_NAMA_DONATUR_PEMBAYARAN = "NamaDonatur";
    public final static String KEY_NAMA_KEBUTUHAN_PEMBAYARAN = "NamaKeb";
    public final static String KEY_NOMINAL_PEMBAYARAN = "Nominal";
    public final static String KEY_FOTO_PEMBAYARAN = "FotoPemb";
    public final static String KEY_ID_PENGURUS = "Tujuan";

    private ArrayList<RiwayatPembayaranData> riwayatPembayaranData = new ArrayList<>();
    private ArrayList<String> keyItem = new ArrayList<>();
    private Context mContext;
    private String foto_donatur, nama_donatur, nama_kebutuhan, id_pengurus;


    public DaftarRiwayatPembayaranAdapter(Context context, ArrayList<RiwayatPembayaranData> riwayatPembayaranData, ArrayList<String> keyItem) {
        this.riwayatPembayaranData = riwayatPembayaranData;
        this.keyItem = keyItem;
        this.mContext = context;
    }

    @NonNull
    @Override
    public daftarAkunViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_verifikasi_pembayaran, parent, false);

        return new daftarAkunViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull daftarAkunViewHolder holder, int position) {

        holder.setIsRecyclable(false);

        RiwayatPembayaranData id = riwayatPembayaranData.get(position);
        holder.tv_nominal.append(id.getNominal_bayar());

        Log.i(TAG, "DATA PEMBAYARAN"+id);

        // get data donatur
        DatabaseReference listDb = FirebaseDatabase.getInstance().getReference().child("Users").child(id.getId_donatur());
        listDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    holder.tv_nama_donatur.append(snapshot.child("nama").getValue(String.class));
                    foto_donatur = snapshot.child("foto_profile").getValue(String.class);

                    //set to layout image
                    StorageReference ref = FirebaseStorage.getInstance().getReference();
                    StorageReference dateRef = ref.child("Users/" + foto_donatur);
                    dateRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get().load(uri).fetch(new Callback() {
                                @Override
                                public void onSuccess() {
                                    holder.iv_foto_donatur.setAlpha(0f);
                                    Picasso.get().load(uri).into(holder.iv_foto_donatur);
                                    holder.iv_foto_donatur.animate().setDuration(300).alpha(1f).start();
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


        if (id.getId_kebutuhan() != null) { // donasi barang
            DatabaseReference listDb4 = FirebaseDatabase.getInstance().getReference().child("Kebutuhan").child(id.getId_kebutuhan());
            listDb4.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        holder.tv_nama_kebutuhan.append(snapshot.child("nama_kebutuhan").getValue(String.class));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else { //donasi umum
            holder.tv_nama_kebutuhan.append("Donasi Umum");
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString(KEY_ID_PEMBAYARAN, keyItem.get(position));


                DetailVerifikasiPembayaranFragment fragment = new DetailVerifikasiPembayaranFragment();
                fragment.setArguments(bundle);
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.admin_frame_layout, fragment).addToBackStack(null).commit();
            }
        });

    }


    @Override
    public int getItemCount() {
        return riwayatPembayaranData.size();
    }


    static class daftarAkunViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_foto_donatur;
        TextView tv_nama_donatur, tv_nama_kebutuhan, tv_nominal;

        public daftarAkunViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_foto_donatur = itemView.findViewById(R.id.iv_foto_donatur);
            tv_nama_donatur = itemView.findViewById(R.id.tv_nama_donatur);
            tv_nama_kebutuhan = itemView.findViewById(R.id.tv_nama_kebutuhan);
            tv_nominal = itemView.findViewById(R.id.tv_nominal);
        }
    }
}
