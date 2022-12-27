package com.mydonate.adapter;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jpvs0101.currencyfy.Currencyfy;
import com.mydonate.R;
import com.mydonate.data.PengajuanPencairanDanaData;
import com.mydonate.fragment.pencairan.DetailPengajuanPencairanFragment;

import java.util.ArrayList;

public class DaftarPengajuanPencairanDanaAdapter extends RecyclerView.Adapter<DaftarPengajuanPencairanDanaAdapter.daftarPembayaranViewholder> {
    public final static String KEY_ID_KEBUTUHAN = "IDKebutuhan";
    public final static String KEY_ID_PENGURUS = "IDPengurus";
    public final static String KEY_NOMINAL = "NOMINAL";
    public final static String KEY_ID_PENCAIRAN = "ID_PENCAIRAN";
    public final static String KEY_ID_STATUS = "ID_STATUS";
    public static final String KEY_KETERANGAN = "KETERANGAN";
    private ArrayList<PengajuanPencairanDanaData> pengajuanPencairanDanaData;
    private ArrayList<String> keyItem;
    private Context mContext;
    private String nama_kebutuhan, nominal_kebutuhan;

    private StorageReference mStorageRef;
    private DatabaseReference ref_pengurus, ref_kebutuhan;



    public DaftarPengajuanPencairanDanaAdapter(Context context, ArrayList<PengajuanPencairanDanaData> pengajuanPencairanDanaData, ArrayList<String> keyItem) {
        this.mContext = context;
        this.pengajuanPencairanDanaData = pengajuanPencairanDanaData;
        this.keyItem = keyItem;
    }



    @NonNull
    @Override
    public daftarPembayaranViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pengajuan_pencairan, parent, false);
        mStorageRef = FirebaseStorage.getInstance().getReference("Bukti Pembayaran");
        return new daftarPembayaranViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull daftarPembayaranViewholder holder, @SuppressLint("RecyclerView") int position) {

        PengajuanPencairanDanaData id = pengajuanPencairanDanaData.get(position);

        if (id.getStatus().equals("baru")) {
            holder.status.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_status_blue));
        } else if (id.getStatus().equals("proses")) {
            holder.status.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_status_green));
        }
        Log.i(TAG, "onBindViewHolder: " + id.getStatus());


        ref_pengurus = FirebaseDatabase.getInstance().getReference().child("Users").child(id.getId_pengurus());
        ref_pengurus.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String nama_masjid = snapshot.child("nama_masjid").getValue(String.class);
                    holder.namaMasjid.setText(nama_masjid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ref_kebutuhan = FirebaseDatabase.getInstance().getReference().child("Kebutuhan").child(id.getId_kebutuhan());
        ref_kebutuhan.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String nama_kebutuhan = snapshot.child("nama_kebutuhan").getValue(String.class);
                    holder.namaKebutuhan.setText(nama_kebutuhan);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.nominalBayar.setText("Rp. "+ Currencyfy.currencyfy(Double.parseDouble(id.getNominal_pencairan()), false, false));


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString(KEY_ID_KEBUTUHAN, id.getId_kebutuhan());
                bundle.putString(KEY_ID_PENGURUS, id.getId_pengurus());
                bundle.putString(KEY_NOMINAL, id.getNominal_pencairan());
                bundle.putString(KEY_ID_PENCAIRAN, keyItem.get(position));
                bundle.putString(KEY_ID_STATUS, id.getStatus());
                bundle.putString(KEY_KETERANGAN, id.getKeterangan());
                DetailPengajuanPencairanFragment fragment = new DetailPengajuanPencairanFragment();
                fragment.setArguments(bundle);
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.admin_frame_layout, fragment).addToBackStack(null).commit();
            }
        });

    }




    @Override
    public int getItemCount() {
        return pengajuanPencairanDanaData.size();
    }



    static class daftarPembayaranViewholder extends RecyclerView.ViewHolder {
        TextView namaMasjid, namaKebutuhan, nominalBayar;
        View status;

        public daftarPembayaranViewholder(@NonNull View itemView) {
            super(itemView);
            namaMasjid = itemView.findViewById(R.id.tv_nama_masjid);
            namaKebutuhan = itemView.findViewById(R.id.tv_kebutuhan_donasi);
            nominalBayar = itemView.findViewById(R.id.tv_total_donasi);
            status = itemView.findViewById(R.id.view_status);
            status.setVisibility(View.VISIBLE);


        }
    }

}
