package com.mydonate.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerDrawable;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mydonate.R;
import com.mydonate.data.KebutuhanData;
import com.mydonate.data.MasjidMushola;
import com.mydonate.data.PengurusData;
import com.mydonate.fragment.DetailDonasiMasjidMusholaFragment;
import com.mydonate.fragment.DetailVerifikasiAkunPengurusFragment;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DaftarAkunPengurusAdapter extends RecyclerView.Adapter<DaftarAkunPengurusAdapter.daftarAkunViewHolder> {

    private ArrayList<PengurusData> pengurusData = new ArrayList<>();
    private ArrayList<String> keyItem = new ArrayList<>();
    private Context mContext;


    public DaftarAkunPengurusAdapter(Context context, ArrayList<PengurusData> kebutuhanDatanew, ArrayList<String> keyItem) {
        this.pengurusData = kebutuhanDatanew;
        this.keyItem = keyItem;
        this.mContext = context;
    }

    @NonNull
    @Override
    public daftarAkunViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_verifikasi_akun, parent, false);

        return new daftarAkunViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull daftarAkunViewHolder holder, int position) {

        PengurusData id = pengurusData.get(position);
        holder.tv_nama_pengurus.setText(id.getNama_pengurus());
        holder.tv_alamat_masjid.setText(id.getAlamat_masjid());
        holder.tv_nama_masjid.setText(id.getNama_masjid());

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


        //set to layout image
        StorageReference ref = FirebaseStorage.getInstance().getReference();
        StorageReference dateRef = ref.child("Users/" + id.getFoto_tempat());
        dateRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).fetch(new Callback() {
                    @Override
                    public void onSuccess() {
                        Picasso.get()
                                .load(uri)
                                .placeholder(shimmerDrawable)
                                .into(holder.iv_foto_masjid);
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
                bundle.putString(DetailVerifikasiAkunPengurusFragment.KEY_VERIF_AKUN, keyItem.get(position));
                DetailVerifikasiAkunPengurusFragment fragment = new DetailVerifikasiAkunPengurusFragment();
                fragment.setArguments(bundle);
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.admin_frame_layout, fragment).addToBackStack(null).commit();
            }
        });

    }


    @Override
    public int getItemCount() {
        return pengurusData.size();
    }


    static class daftarAkunViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_foto_masjid;
        TextView tv_nama_pengurus, tv_nama_masjid, tv_alamat_masjid;

        public daftarAkunViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_foto_masjid = itemView.findViewById(R.id.iv_foto_masjid);
            tv_nama_pengurus = itemView.findViewById(R.id.tv_nama_pengurus);
            tv_nama_masjid = itemView.findViewById(R.id.tv_nama_masjid);
            tv_alamat_masjid = itemView.findViewById(R.id.tv_alamat_masjid);
        }
    }
}
