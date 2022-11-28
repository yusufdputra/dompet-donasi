package com.mydonate.adapter;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mydonate.R;
import com.mydonate.data.BayarKebutuhanData;
import com.mydonate.data.KebutuhanData;
import com.mydonate.data.RiwayatPembayaranData;
import com.mydonate.fragment.UploadBuktiPembayaranFragment;
import com.mydonate.fragment.UploadBuktiPenyerahanFragment;

import java.util.ArrayList;

public class DaftarPembayaranAdapter extends RecyclerView.Adapter<DaftarPembayaranAdapter.daftarPembayaranViewholder> {
    public final static String KEY_ID_KEBUTUHAN = "IDKebutuhan";
    private ArrayList<BayarKebutuhanData> bayarKebutuhanData;
    private ArrayList<String> keyItem;
    private Context mContext;
    private String nama_kebutuhan, nominal_kebutuhan;

    private StorageReference mStorageRef;


    public DaftarPembayaranAdapter(Context context, ArrayList<BayarKebutuhanData> bayarKebutuhanData, ArrayList<String> keyItem) {
        this.mContext = context;
        this.bayarKebutuhanData = bayarKebutuhanData;
        this.keyItem = keyItem;
    }



    @NonNull
    @Override
    public daftarPembayaranViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pembayaran_selesai, parent, false);
        mStorageRef = FirebaseStorage.getInstance().getReference("Bukti Pembayaran");
        return new daftarPembayaranViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull daftarPembayaranViewholder holder, int position) {

        BayarKebutuhanData id = bayarKebutuhanData.get(position);

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Kebutuhan").child(keyItem.get(position));
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    String id_pengurus = snapshot.child("id_pengurus").getValue(String.class);
                    nama_kebutuhan = snapshot.child("nama_kebutuhan").getValue(String.class);
                    nominal_kebutuhan = snapshot.child("biaya_kebutuhan").getValue(String.class);

                    holder.namaKebutuhan.setText(nama_kebutuhan);
                    holder.nominalBayar.append(nominal_kebutuhan);

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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString(KEY_ID_KEBUTUHAN, keyItem.get(position));

                UploadBuktiPenyerahanFragment fragment = new UploadBuktiPenyerahanFragment();
                fragment.setArguments(bundle);
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.admin_frame_layout, fragment).addToBackStack(null).commit();
            }
        });

    }


    @Override
    public int getItemCount() {
        return bayarKebutuhanData.size();
    }

    static class daftarPembayaranViewholder extends RecyclerView.ViewHolder {
        TextView namaMasjid, namaKebutuhan, nominalBayar;

        public daftarPembayaranViewholder(@NonNull View itemView) {
            super(itemView);
            namaMasjid = itemView.findViewById(R.id.tv_nama_masjid);
            namaKebutuhan = itemView.findViewById(R.id.tv_kebutuhan_donasi);
            nominalBayar = itemView.findViewById(R.id.tv_total_donasi);

        }
    }
}
