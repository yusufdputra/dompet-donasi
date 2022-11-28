package com.mydonate.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jpvs0101.currencyfy.Currencyfy;
import com.mydonate.R;
import com.mydonate.data.PenyaluranProgramUmumData;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

public class ItemPenyerahanAdapter extends RecyclerView.Adapter<ItemPenyerahanAdapter.GridViewHolder> implements ViewPager.OnAdapterChangeListener {
    private Context mContext;
    private ArrayList<PenyaluranProgramUmumData> penyaluranProgramUmumData = new ArrayList<>();
    private ArrayList<String> keyItem = new ArrayList<>();

    public ItemPenyerahanAdapter(Context context, ArrayList<PenyaluranProgramUmumData> penyaluranProgramUmumData, ArrayList<String> keyItem) {
        this.mContext = context;
        this.penyaluranProgramUmumData = penyaluranProgramUmumData;
        this.keyItem = keyItem;

    }



    @NonNull
    @Override
    public ItemPenyerahanAdapter.GridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_penyerahan_pu_grid, parent, false);
        return new GridViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemPenyerahanAdapter.GridViewHolder holder, int position) {
//        notifyDataSetChanged();
        PenyaluranProgramUmumData id = penyaluranProgramUmumData.get(position);
        holder.tv_nama_masjid.setText(id.getNama_masjid());
        holder.tv_alamat_masjid.setText(id.getAlamat_masjid());

        String dana= id.getDana_donasi().replaceAll("[^a-zA-Z0-9]", "");
        holder.tv_dana.setText("Rp. "+ Currencyfy.currencyfy(Double.parseDouble(dana),false,false));

        List<SlideModel> slideModels = new ArrayList<>();
        // get
        for (int i = 0; i < id.getFoto_penyerahan().size(); i++){
            StorageReference sr = FirebaseStorage.getInstance().getReference().child("Bukti Penyerahan").child("Program Umum").child(id.getFoto_penyerahan().get(i));
            sr.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {

                    slideModels.add(new SlideModel(uri.toString(), ScaleTypes.CENTER_CROP));
                    holder.imageViewSlider.setImageList(slideModels, ScaleTypes.CENTER_CROP);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return penyaluranProgramUmumData.size();
    }


    @Override
    public void onAdapterChanged(@NonNull ViewPager viewPager, @Nullable PagerAdapter oldAdapter, @Nullable PagerAdapter newAdapter) {

    }

    public class GridViewHolder extends RecyclerView.ViewHolder {
        ImageSlider imageViewSlider;
        TextView tv_nama_masjid, tv_alamat_masjid,  tv_dana;
        public GridViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewSlider = itemView.findViewById(R.id.imageViewSlider);
            tv_nama_masjid = itemView.findViewById(R.id.tv_nama_masjid);
            tv_alamat_masjid = itemView.findViewById(R.id.tv_alamat_masjid);
            tv_dana = itemView.findViewById(R.id.iv_dana);

        }
    }

}
