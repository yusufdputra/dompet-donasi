package com.mydonate.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.mydonate.R;
import com.mydonate.adapter.SlideAdapter;

public class SlideViewPagerActivity extends AppCompatActivity {
    private ViewPager vp_slide_info;
    private LinearLayout dotLayout, temp;
    private TextView[] dot;
    private SlideAdapter slideAdapter;
    private Integer CurrentPage;
    private Button btn_mulai;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen_info);

        vp_slide_info = (ViewPager) findViewById(R.id.vp_splash);
        dotLayout = (LinearLayout) findViewById(R.id.dotLayout);
        temp = (LinearLayout) findViewById(R.id.temp);
        btn_mulai = findViewById(R.id.btn_mulai);

        slideAdapter = new SlideAdapter(this);
        vp_slide_info.setAdapter(slideAdapter);

        addDotsIndicator(0);
        vp_slide_info.addOnPageChangeListener(viewListener);

        btn_mulai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SlideViewPagerActivity.this, AuthActivity.class);
                startActivity(intent);
                SlideViewPagerActivity.this.finish();
            }
        });
    }

    private void addDotsIndicator(int position) {
        dot = new TextView[3];
        dotLayout.removeAllViews();
        for (int i = 0; i < dot.length; i++) {
            dot[i] = new TextView(this);
            dot[i].setText(Html.fromHtml("&#8226;"));
            dot[i].setTextSize(35);
            dot[i].setTextColor(getResources().getColor(R.color.lighterOrangeTrans2));

            dotLayout.addView(dot[i]);
        }
        if (dot.length > 0) {
            dot[position].setTextColor(getResources().getColor(R.color.darkerOrange));
        }
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDotsIndicator(position);

            CurrentPage = position;
            if (CurrentPage == dot.length - 1) {
                temp.setVisibility(View.GONE);
                btn_mulai.setVisibility(View.VISIBLE);

            } else {
                temp.setVisibility(View.VISIBLE);
                btn_mulai.setVisibility(View.GONE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };


}
