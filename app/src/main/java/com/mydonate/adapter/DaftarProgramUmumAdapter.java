package com.mydonate.adapter;

import android.annotation.SuppressLint;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jpvs0101.currencyfy.Currencyfy;
import com.mydonate.R;
import com.mydonate.data.PengurusData;
import com.mydonate.data.ProgramUmumData;
import com.mydonate.fragment.DetailMasjidMusholaFragment;
import com.mydonate.fragment.DetailProgramUmumFragment;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class DaftarProgramUmumAdapter extends RecyclerView.Adapter<DaftarProgramUmumAdapter.daftarPUViewHolder> {
    private ArrayList<ProgramUmumData> programUmumData;
    private ArrayList<String> keyItem;

    private Context mContext;

    public DaftarProgramUmumAdapter(Context context, ArrayList<ProgramUmumData> programUmumData, ArrayList<String> keyItem) {
        this.mContext = context;
        this.programUmumData = programUmumData;
        this.keyItem = keyItem;
    }

    @NonNull
    @Override
    public daftarPUViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_daftar_program_umum, parent, false);
        return new daftarPUViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final daftarPUViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.setIsRecyclable(false);
        ProgramUmumData id = programUmumData.get(position);
        holder.nama.setText(id.getNama());
//        holder.keterangan.setText(id.getKeterangan());
        String dana = id.getDana();
        String dana_ = dana.replaceAll("[^a-zA-Z0-9]", "");
        holder.terkumpul.setText("Terkumpul : Rp. "+ Currencyfy.currencyfy(Double.parseDouble(dana_), false, false));


        //set to layout image
        StorageReference ref = FirebaseStorage.getInstance().getReference();
        StorageReference dateRef = ref.child("Program Umum/" + id.getFoto());
        dateRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).fetch(new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.ivFoto.setAlpha(0f);
                        Picasso.get().load(uri).into(holder.ivFoto);
                        holder.ivFoto.animate().setDuration(300).alpha(1f).start();
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
                bundle.putString(DetailMasjidMusholaFragment.KEY_ID, keyItem.get(position));

                DetailProgramUmumFragment fragment = new DetailProgramUmumFragment();
                fragment.setArguments(bundle);
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.home_frame_layout,fragment).addToBackStack(null).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return programUmumData.size();
    }

    static class daftarPUViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFoto;
        TextView nama, keterangan, terkumpul;

        daftarPUViewHolder(View itemView) {
            super(itemView);
            ivFoto = itemView.findViewById(R.id.iv_foto_program);
            nama = itemView.findViewById(R.id.tv_nama_program);
//            keterangan = itemView.findViewById(R.id.tv_keterangan);
            terkumpul = itemView.findViewById(R.id.tv_terkumpul);
        }
    }

}
