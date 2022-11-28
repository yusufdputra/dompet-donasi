package com.mydonate.adapter;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jpvs0101.currencyfy.Currencyfy;
import com.mydonate.R;
import com.mydonate.data.DanaData;
import com.mydonate.data.KebutuhanData;
import com.mydonate.fragment.DetailMasjidMusholaFragment;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class DaftarKebutuhanKelolaDUAdapter extends RecyclerView.Adapter<DaftarKebutuhanKelolaDUAdapter.daftarMasjidViewHolder> {
    private ArrayList<KebutuhanData> kebutuhanData;

    private ArrayList<String> keyItemKebutuhan;
    private static final String KEY_MASJID = "Masjid";
    private static final String KEY_MUSHOLA = "Mushola";

    private Context mContext;

    public DaftarKebutuhanKelolaDUAdapter(Context context, ArrayList<KebutuhanData> kebutuhanData, ArrayList<String> keyItem) {
        this.mContext = context;
        this.kebutuhanData = kebutuhanData;
        this.keyItemKebutuhan = keyItem;
    }

    @NonNull
    @Override
    public daftarMasjidViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_daftar_kebutuhan, parent, false);

        return new daftarMasjidViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final daftarMasjidViewHolder holder, int position) {
        KebutuhanData id = kebutuhanData.get(position);

        holder.namaKebutuhan.setText(id.getNama_kebutuhan());
        Query db = FirebaseDatabase.getInstance().getReference().child("Bayar Kebutuhan").child(String.valueOf(keyItemKebutuhan.get(position)));
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String  sisa = snapshot.child("sisa_nominal_kebutuhan").getValue(String.class);
                String sisa_ = sisa.replaceAll("[^a-zA-Z0-9]", "");
                holder.danaKurang.setText("Rp. "+ Currencyfy.currencyfy(Double.parseDouble(sisa_),false,false));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    @Override
    public int getItemCount() {
        return keyItemKebutuhan.size();
    }

    static class daftarMasjidViewHolder extends RecyclerView.ViewHolder {
        TextView namaKebutuhan, status, danaKurang;

        daftarMasjidViewHolder(View itemView) {
            super(itemView);
            namaKebutuhan = itemView.findViewById(R.id.list_nama_kebutuhan);
            status = itemView.findViewById(R.id.list_status);
            danaKurang = itemView.findViewById(R.id.list_dana_kurang);
        }
    }

}
