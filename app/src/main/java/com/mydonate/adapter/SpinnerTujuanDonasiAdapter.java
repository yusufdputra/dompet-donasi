package com.mydonate.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mydonate.R;

import java.util.ArrayList;

public class SpinnerTujuanDonasiAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> nama_arr;
    private ArrayList<String> alamat_arr;
    private ArrayList<String> tipe_spinner;
    private LayoutInflater layoutInflater;



    public SpinnerTujuanDonasiAdapter(Context context, ArrayList<String> nama_spinner, ArrayList<String> alamat_spinner, ArrayList<String> tipe_spinner) {
        this.context = context;
        this.nama_arr = nama_spinner;
        this.alamat_arr = alamat_spinner;
        this.tipe_spinner = tipe_spinner;
        this.layoutInflater = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return nama_arr.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = layoutInflater.inflate(R.layout.item_custom_spinner, null);
        TextView nama = (TextView) view.findViewById(R.id.list_nama_masjid);
        TextView alamat = (TextView) view.findViewById(R.id.list_alamat);

        nama.setText(tipe_spinner.get(i)+" "+nama_arr.get(i));
        alamat.setText(alamat_arr.get(i));

        return view;
    }
}
