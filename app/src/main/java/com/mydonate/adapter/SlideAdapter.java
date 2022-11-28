package com.mydonate.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.mydonate.R;

import java.util.ArrayList;

public class SlideAdapter extends PagerAdapter {
    private Context mcontext;
    private LayoutInflater inflater;

    public SlideAdapter(Context context) {
        this.mcontext = context;
    }

    public int [] slide_images = {
            R.drawable.donation_fromphone,
            R.drawable.secure,
            R.drawable.donasi
    };

    public String [] slide_header = {
            "Donasi Lebih Mudah",
            "Amanah dan Transparansi",
            "Semakin Banyak Memberi\nSemakin Banyak Menerima"
    };

    public String [] slide_deskripsi = {
            "Saat ini donasi dapat dilakukan dengan mudah melalui gadget mu. Ada banyak platform yang menyediakan hal tersebut, salah satunya aplikasi donatur masjid. Aplikasi Donatur masjid ini merupakan sebuah aplikasi berbasis android yang bergerak dalam penggalangan dana masyarakat secara online.",
            "Kebutuhan yang telah terpenuhi akan segera kami berikan kepada masjid / musholla yang telah mengajukan permintaan kebutuhan kepada aplikasi. kemudian bukti serah terima kebutuhan akan di publikasikan kedalam aplikasi.",
            "Sesungguhnya orang-orang yang bersedekah baik laki-laki maupun perempuan dan meminjamkan kepada Allah dengan pinjaman yang baik, niscaya akan dilipat-gandakan (pahalanya) kepada mereka dan bagi mereka pahala yang banyak“. (QS. Al-Hadid: 18)"

    };

    public SlideAdapter(ArrayList<String> slideModels) {
    }

    @Override
    public int getCount() {
        return slide_header.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        inflater = (LayoutInflater) mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.slide_layout, null);

        ImageView slideImageIcon = (ImageView) view.findViewById(R.id.iv_icon);
        TextView slideHeader = (TextView) view.findViewById(R.id.tv_header);
        TextView slideDesk = (TextView) view.findViewById(R.id.tv_deskripsi);

        slideImageIcon.setImageResource(slide_images[position]);
        slideHeader.setText(slide_header[position]);
        slideDesk.setText(slide_deskripsi[position]);


        ViewPager vp = (ViewPager) container;
        vp.addView(view, 0);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);
    }
}
