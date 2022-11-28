package com.mydonate.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mydonate.R;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class SpinnerTujuanKebutuhanAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> nama_arr;
    private ArrayList<String> detail_arr;
    private ArrayList<String> id_arr;
    private LayoutInflater layoutInflater;



    public SpinnerTujuanKebutuhanAdapter(Context context, ArrayList<String> nama_spinner, ArrayList<String> detail_arr, ArrayList<String> id_arr) {
        this.context = context;
        this.nama_arr = nama_spinner;
        this.detail_arr = detail_arr;
        this.id_arr = id_arr;
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
        view = layoutInflater.inflate(R.layout.item_custom_spinner_kebutuhan, null);
        TextView kebutuhan = (TextView) view.findViewById(R.id.tv_nama_kebutuhan);
        TextView detail = (TextView) view.findViewById(R.id.tv_detail_kebutuhan);


        kebutuhan.setText(nama_arr.get(i));
        detail.setText(detail_arr.get(i));

        return view;
    }
}
