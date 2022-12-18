package com.mydonate.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mydonate.R;
import com.mydonate.activity.Berita.DetailBeritaActivity;
import com.mydonate.data.BeritaData;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ItemBeritaAdapter extends RecyclerView.Adapter<ItemBeritaAdapter.GridViewHolder> implements ViewPager.OnAdapterChangeListener {

  public static final String KEY_ID_BERITA = "KEY_ID_BERITA";
  public static final String KEY_ID_PENGURUS = "KEY_ID_PENGURUS";
  private Context mContext;
  private ArrayList<BeritaData> beritaData = new ArrayList<>();
  private ArrayList<String> keyItem = new ArrayList<>();

  public ItemBeritaAdapter(Context context, ArrayList<BeritaData> beritaDataArrayList, ArrayList<String> keyItem) {
    this.mContext = context;
    this.beritaData = beritaDataArrayList;
    this.keyItem = keyItem;

  }


  @NonNull
  @Override
  public ItemBeritaAdapter.GridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_slide_news, parent, false);
    return new GridViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ItemBeritaAdapter.GridViewHolder holder, @SuppressLint("RecyclerView") int position) {
//        notifyDataSetChanged();
    BeritaData data = beritaData.get(position);
    holder.tv_header.setText(data.getTitle());
    holder.tv_tanggal.setText(data.getCreated_at());
    holder.tv_detail.setText(data.getDetail());
    if (!data.getImage().equals("")) {
      //set image to layout
      StorageReference ref = FirebaseStorage.getInstance().getReference();
      StorageReference dateRef = ref.child("Berita/" + data.getImage());
      dateRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
        @Override
        public void onSuccess(Uri uri) {
          Picasso.get().load(uri).fetch(new Callback() {
            @Override
            public void onSuccess() {
              holder.iv_background.setAlpha(0f);
              Picasso.get().load(uri).into(holder.iv_background);
              holder.iv_background.animate().setDuration(200).alpha(1f).start();
            }

            @Override
            public void onError(Exception e) {

            }
          });
        }
      });
    }
    holder.itemView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(mContext, DetailBeritaActivity.class);
        intent.putExtra(KEY_ID_BERITA, keyItem.get(position));
        intent.putExtra(KEY_ID_PENGURUS, data.getId_pengurus());
        mContext.startActivity(intent);
      }
    });
  }

  @Override
  public int getItemCount() {
    return beritaData.size();
  }


  @Override
  public void onAdapterChanged(@NonNull ViewPager viewPager, @Nullable PagerAdapter oldAdapter, @Nullable PagerAdapter newAdapter) {

  }

  public class GridViewHolder extends RecyclerView.ViewHolder {
    ImageView iv_background;
    TextView tv_header, tv_tanggal, tv_detail;

    public GridViewHolder(@NonNull View itemView) {
      super(itemView);
      iv_background = itemView.findViewById(R.id.iv_background);
      tv_tanggal = itemView.findViewById(R.id.tv_tanggal);
      tv_header = itemView.findViewById(R.id.tv_header);
      tv_detail = itemView.findViewById(R.id.tv_detail);

    }
  }

}
