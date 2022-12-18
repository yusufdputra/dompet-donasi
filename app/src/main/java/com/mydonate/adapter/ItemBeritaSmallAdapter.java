package com.mydonate.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.mydonate.R;
import com.mydonate.activity.Berita.DetailBeritaActivity;
import com.mydonate.data.BeritaData;

import java.util.ArrayList;

public class ItemBeritaSmallAdapter extends RecyclerView.Adapter<ItemBeritaSmallAdapter.GridViewHolder> implements ViewPager.OnAdapterChangeListener {

  public static final String KEY_ID_BERITA = "KEY_ID_BERITA";
  public static final String KEY_ID_PENGURUS = "KEY_ID_PENGURUS";
  private Context mContext;
  private ArrayList<BeritaData> beritaData = new ArrayList<>();
  private ArrayList<String> keyItem = new ArrayList<>();

  public ItemBeritaSmallAdapter(Context context, ArrayList<BeritaData> beritaDataArrayList, ArrayList<String> keyItem) {
    this.mContext = context;
    this.beritaData = beritaDataArrayList;
    this.keyItem = keyItem;

  }


  @NonNull
  @Override
  public ItemBeritaSmallAdapter.GridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_slide_news_small, parent, false);
    return new GridViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ItemBeritaSmallAdapter.GridViewHolder holder, @SuppressLint("RecyclerView") int position) {
//        notifyDataSetChanged();
    BeritaData data = beritaData.get(position);
    holder.tv_header.setText(data.getTitle());
    holder.tv_tanggal.setText(data.getCreated_at());
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
    TextView tv_header, tv_tanggal;

    public GridViewHolder(@NonNull View itemView) {
      super(itemView);
      tv_tanggal = itemView.findViewById(R.id.tv_tanggal);
      tv_header = itemView.findViewById(R.id.tv_header);

    }
  }

}
