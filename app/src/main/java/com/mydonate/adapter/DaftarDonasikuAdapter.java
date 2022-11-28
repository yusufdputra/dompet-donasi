package com.mydonate.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

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
import com.mydonate.data.RiwayatPembayaranData;
import com.mydonate.data.TransaksiPembayaranData;
import com.mydonate.fragment.DetailDonasiItemFragment;
import com.mydonate.fragment.DetailDonasiMasjidMusholaFragment;
import com.mydonate.fragment.DetailProgramUmumFragment;
import com.mydonate.fragment.DetailVerifikasiAkunPengurusFragment;
import com.mydonate.fragment.DonasiUmumFragment;
import com.mydonate.fragment.UploadBuktiPembayaranFragment;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

public class DaftarDonasikuAdapter extends RecyclerView.Adapter<DaftarDonasikuAdapter.daftarDonasikuViewholder> {
    public final static String KEY_ID_RIWAYAT_PEMBAYARAN = "IDPembayaran";
    public final static String KEY_ID_KEBUTUHAN = "IDKebutuhan";
    public final static String KEY_NOMINAL_RIWAYAT_PEMBAYARAN = "Nominal";
    public final static String KEY_URL_RIWAYAT_PEMBAYARAN = "URLPembayaran";
    private ArrayList<TransaksiPembayaranData> transaksiPembayaranData;
    private ArrayList<String> keyItem;
    private Context mContext;
    int Image_Request_Code1 = 1;
    private Uri filepath_pembayaran;
    private String nama_kebutuhan;

    private StorageReference mStorageRef;


    public DaftarDonasikuAdapter(Context context, ArrayList<TransaksiPembayaranData> transaksiPembayaranData, ArrayList<String> keyItem) {
        this.mContext = context;
        this.transaksiPembayaranData = transaksiPembayaranData;
        this.keyItem = keyItem;
    }

    @NonNull
    @Override
    public daftarDonasikuViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_riwayat_donasiku, parent, false);
        mStorageRef = FirebaseStorage.getInstance().getReference("Bukti Pembayaran");
        return new daftarDonasikuViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull daftarDonasikuViewholder holder, @SuppressLint("RecyclerView") int position) {
        if (transaksiPembayaranData != null) {


            TransaksiPembayaranData id = transaksiPembayaranData.get(position);

            if (id.getStatus_code().equals("200")) { // sukses
                holder.status_bukti_warning.setVisibility(View.GONE);
                holder.status_bukti_nunggu.setVisibility(View.GONE);
                holder.status_bukti_error.setVisibility(View.GONE);
                holder.status_bukti_check.setVisibility(View.VISIBLE);
            } else if (id.getStatus_code().equals("201")){ // pending
                holder.status_bukti_warning.setVisibility(View.GONE);
                holder.status_bukti_nunggu.setVisibility(View.VISIBLE);
                holder.status_bukti_error.setVisibility(View.GONE);
                holder.status_bukti_check.setVisibility(View.GONE);
            }else if (id.getStatus_code().equals("202")) { // cancel
                holder.status_bukti_warning.setVisibility(View.GONE);
                holder.status_bukti_nunggu.setVisibility(View.GONE);
                holder.status_bukti_error.setVisibility(View.VISIBLE);
                holder.status_bukti_check.setVisibility(View.GONE);
            }
//            }else if (id.getFoto_bukti_pembayaran() != null & id.getPesan() != null){ // pesan
//
//                holder.status_bukti_warning.setVisibility(View.VISIBLE);
//                holder.status_bukti_nunggu.setVisibility(View.GONE);
//                holder.status_bukti_error.setVisibility(View.GONE);
//                holder.status_bukti_check.setVisibility(View.GONE);
//            }

            holder.nominalBayar.setText("Rp. "+ Currencyfy.currencyfy(Double.parseDouble(id.getTotal_bayar()),false, false));
            holder.tgl_donasi.setText(id.getTransaction_time());

            Query db_pu = FirebaseDatabase.getInstance().getReference().child("Program Umum").child(id.getProduct_name()).child("nama");
            db_pu.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        Log.i(TAG, "nama "+snapshot);
                        holder.namaKebutuhan.setText((CharSequence) snapshot.getValue());
                        holder.namaMasjid.setText("Program Umum");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            if (id.getProduct_name().equals(DonasiUmumFragment.KEY_NAMA_DONASI)) {
                holder.namaKebutuhan.setText("-");
                holder.namaMasjid.setText("Donasi Umum");

            } else {
                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Kebutuhan").child(id.getProduct_name());
                dbRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {

                            String id_pengurus = snapshot.child("id_pengurus").getValue(String.class);
                            nama_kebutuhan = snapshot.child("nama_kebutuhan").getValue(String.class);

                            holder.namaKebutuhan.setText(nama_kebutuhan);

                            DatabaseReference dbRef2 = FirebaseDatabase.getInstance().getReference().child("Users").child(id_pengurus);
                            dbRef2.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String nama_masjid = snapshot.child("nama_masjid").getValue(String.class);
                                    String tipe = snapshot.child("tipe_tempat").getValue(String.class);
                                    //set to layout
                                    holder.namaMasjid.setText(tipe + " " + nama_masjid);

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putString(RiwayatDonasiAdapter.KEY_ID_PEMBAYARAN, keyItem.get(position));
                    bundle.putString(RiwayatDonasiAdapter.KEY_ID_TUJUAN, id.getProduct_name());
                    bundle.putString(RiwayatDonasiAdapter.KEY_LINK, id.getUrl_pdf());

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

    static class daftarDonasikuViewholder extends RecyclerView.ViewHolder {
        TextView namaMasjid, namaKebutuhan, nominalBayar, tgl_donasi, status_bukti_error, status_bukti_check,status_bukti_nunggu;
        ImageView  status_bukti_warning;

        public daftarDonasikuViewholder(@NonNull View itemView) {
            super(itemView);
            namaMasjid = itemView.findViewById(R.id.tv_nama_masjid);
            namaKebutuhan = itemView.findViewById(R.id.tv_kebutuhan_donasi);
            nominalBayar = itemView.findViewById(R.id.tv_total_donasi);

            tgl_donasi = itemView.findViewById(R.id.tv_tgl_donasi);
            status_bukti_error = itemView.findViewById(R.id.status_bukti_error);
            status_bukti_check = itemView.findViewById(R.id.status_bukti_check);
            status_bukti_warning = itemView.findViewById(R.id.status_bukti_warning);
            status_bukti_nunggu = itemView.findViewById(R.id.status_bukti_nunggu);
        }
    }
}
