package com.mydonate.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
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

import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerDrawable;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jpvs0101.currencyfy.Currencyfy;
import com.mydonate.R;
import com.mydonate.data.TransaksiPembayaranData;
import com.mydonate.fragment.DetailDonasiItemFragment;
import com.mydonate.fragment.DonasiUmumFragment;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class RiwayatDonasiAdapter extends RecyclerView.Adapter<RiwayatDonasiAdapter.riwayatDonasiViewHolder> {
    public final static String KEY_ID_PEMBAYARAN = "IDPembayaran";
    public final static String KEY_ID_TUJUAN = "IDTujuan";
    public final static String KEY_LINK = "LINK";

    private ArrayList<TransaksiPembayaranData> transaksiPembayaranData;
    private ArrayList<String> keyItem = new ArrayList<>();
    private String jenis_list;

    private Context mContext;


    public RiwayatDonasiAdapter(Context context, ArrayList<TransaksiPembayaranData> transaksiPembayaranData, ArrayList<String> keyItem, String jenis_list) {
        this.mContext = context;
        this.transaksiPembayaranData = transaksiPembayaranData;
        this.keyItem = keyItem;
        this.jenis_list = jenis_list;
    }


    @NonNull
    @Override
    public riwayatDonasiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_riwayat_donasi, parent, false);
        return new riwayatDonasiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull riwayatDonasiViewHolder holder, @SuppressLint("RecyclerView") int position) {


        holder.setIsRecyclable(false);
        TransaksiPembayaranData id = transaksiPembayaranData.get(position);

        Log.i(TAG, "onBindViewHolder: "+id);


        //init shimmer
        Shimmer shimmer = new Shimmer.ColorHighlightBuilder()
                .setBaseColor(Color.parseColor("#F3F3F3"))
                .setBaseAlpha(1)
                .setHighlightColor(Color.parseColor("#E7E7E7"))
                .setHighlightAlpha(1)
                .setDropoff(50)
                .build();
        ShimmerDrawable shimmerDrawable = new ShimmerDrawable();
        shimmerDrawable.setShimmer(shimmer);
        // get donatur
        String id_donatur = id.getId_donatur();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(id_donatur);

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                holder.namaDonatur.setText("");
                String alias = snapshot.child("alias").getValue(String.class);
                if (alias == null) {
                    holder.namaDonatur.setText("Donatur : " + snapshot.child("nama").getValue(String.class));
                } else {
                    holder.namaDonatur.setText("Donatur : " + alias);
                }
                String foto_donatur = snapshot.child("foto_profile").getValue(String.class);

                //set to layout image

                StorageReference ref = FirebaseStorage.getInstance().getReference();
                StorageReference dateRef = ref.child("Users/" + foto_donatur);
                dateRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).fetch(new Callback() {
                            @Override
                            public void onSuccess() {
                                Picasso.get()
                                        .load(uri)
                                        .placeholder(shimmerDrawable)
                                        .into(holder.iv_foto_donatur);

                            }

                            @Override
                            public void onError(Exception e) {
                            }
                        });
                    }
                });
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // get kebutuhan
        String id_kebutuhan = id.getProduct_name();
        DatabaseReference dbRef1 = FirebaseDatabase.getInstance().getReference().child("Program Umum").child(id_kebutuhan);
        dbRef1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    holder.kebutuhanDonasi.setText("");
                    holder.kebutuhanDonasi.setText("Program Umum " + snapshot.child("nama").getValue(String.class));
                } else if (!id_kebutuhan.equals(DonasiUmumFragment.KEY_NAMA_DONASI)) {
                    DatabaseReference dbRef2 = FirebaseDatabase.getInstance().getReference().child("Kebutuhan").child(id_kebutuhan);
                    dbRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            holder.kebutuhanDonasi.setText("");
                            holder.kebutuhanDonasi.setText("Kebutuhan : " + snapshot.child("nama_kebutuhan").getValue(String.class));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else {
                    holder.kebutuhanDonasi.setText("Kebutuhan : Donasi Umum");
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        // get nominal

        holder.jumlahDonasi.setText("Jumlah Donasi : Rp. " + Currencyfy.currencyfy(Double.parseDouble(id.getTotal_bayar()), false, false));


        if (jenis_list.equals("donasi")){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putString(KEY_ID_PEMBAYARAN, keyItem.get(position));
                    bundle.putString(KEY_ID_TUJUAN, id.getProduct_name());
                    bundle.putString(KEY_LINK, null);

                    DetailDonasiItemFragment fragment = new DetailDonasiItemFragment();
                    fragment.setArguments(bundle);
                    AppCompatActivity activity = (AppCompatActivity) view.getContext();
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.home_frame_layout, fragment).addToBackStack(null).commit();
                }
            });
        }



    }

    @Override
    public int getItemCount() {
        return transaksiPembayaranData.size();
    }

    static class riwayatDonasiViewHolder extends RecyclerView.ViewHolder {
        TextView namaDonatur, jumlahDonasi, kebutuhanDonasi;
        ImageView iv_foto_donatur;

        riwayatDonasiViewHolder(View itemView) {
            super(itemView);

            iv_foto_donatur = itemView.findViewById(R.id.iv_profil_donatur);
            namaDonatur = itemView.findViewById(R.id.tv_nama_donatur);
            jumlahDonasi = itemView.findViewById(R.id.tv_jumlah_donasi);
            kebutuhanDonasi = itemView.findViewById(R.id.tv_kebutuhan_donasi);

        }
    }

}
