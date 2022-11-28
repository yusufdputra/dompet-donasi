package com.mydonate.adapter;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mydonate.R;
import com.mydonate.data.PengurusData;
import com.mydonate.fragment.DetailMasjidMusholaFragment;
import com.mydonate.fragment.laporanKeuangan.DetailLaporanKeuanganFragment;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DaftarMasjidMusholaAdapter extends RecyclerView.Adapter<DaftarMasjidMusholaAdapter.daftarMasjidViewHolder> {
    private final ArrayList<PengurusData> pengurusData;
    private final ArrayList<String> keyItem;

    private final Context mContext;
    private final String jenis_list;

    public DaftarMasjidMusholaAdapter(Context context, ArrayList<PengurusData> pengurusData, ArrayList<String> keyItem, String jenis_list) {
        this.mContext = context;
        this.pengurusData = pengurusData;
        this.keyItem = keyItem;
        this.jenis_list = jenis_list;
    }

    @NonNull
    @Override
    public daftarMasjidViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_daftar_masjid_mushola, parent, false);
        return new daftarMasjidViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final daftarMasjidViewHolder holder, int position) {
        PengurusData id = pengurusData.get(position);
        holder.namaMasjid.setText(id.getNama_masjid());
        holder.alamatMasjid.setText(id.getAlamat_masjid());

        //set to layout image
        StorageReference ref = FirebaseStorage.getInstance().getReference();
        StorageReference dateRef = ref.child("Users/" + id.getFoto_tempat());
        dateRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).fetch(new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.ivFotoMasjid.setAlpha(0f);
                        Picasso.get().load(uri).into(holder.ivFotoMasjid);
                        holder.ivFotoMasjid.animate().setDuration(300).alpha(1f).start();
                    }

                    @Override
                    public void onError(Exception e) {
                    }
                });
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                if (jenis_list.equals("laporan")) {
                    bundle.putString(DetailLaporanKeuanganFragment.KEY_ID, keyItem.get(position));
                    DetailLaporanKeuanganFragment fragment = new DetailLaporanKeuanganFragment();
                    fragment.setArguments(bundle);
                    AppCompatActivity activity = (AppCompatActivity) view.getContext();
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.admin_frame_layout, fragment).addToBackStack(null).commit();

                } else {
                    bundle.putString(DetailMasjidMusholaFragment.KEY_ID, keyItem.get(position));
                    DetailMasjidMusholaFragment fragment = new DetailMasjidMusholaFragment();
                    fragment.setArguments(bundle);
                    AppCompatActivity activity = (AppCompatActivity) view.getContext();
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.home_frame_layout, fragment).addToBackStack(null).commit();
                }


            }
        });
    }

    @Override
    public int getItemCount() {
        return pengurusData.size();
    }

    static class daftarMasjidViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFotoMasjid;
        TextView namaMasjid, alamatMasjid;

        daftarMasjidViewHolder(View itemView) {
            super(itemView);
            ivFotoMasjid = itemView.findViewById(R.id.iv_foto_masjid);
            namaMasjid = itemView.findViewById(R.id.tv_nama_masjid);
            alamatMasjid = itemView.findViewById(R.id.tv_alamat);
        }
    }

}
