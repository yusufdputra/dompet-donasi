package com.mydonate.fragment.laporanKeuangan;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jpvs0101.currencyfy.Currencyfy;
import com.kal.rackmonthpicker.RackMonthPicker;
import com.kal.rackmonthpicker.listener.DateMonthDialogListener;
import com.kal.rackmonthpicker.listener.OnCancelMonthDialogListener;
import com.mydonate.R;
import com.mydonate.adapter.TabFragmentAdapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class DetailLaporanKeuanganFragment extends Fragment implements View.OnClickListener {
  public final static String KEY_ID = "KEY_ID";
  private String Uid;
  private ImageView iv_back, iv_get_date;
  private TextView tv_namaMasjid, tv_deposit, tv_bulan;
  private int indicatorWidth;
  private TabLayout tabLayout;
  private View indicator;
  private ViewPager viewPager;
  private TabFragmentAdapter adapter;
  private FirebaseAuth auth;
  private FirebaseUser user;

  private int getBulan = 0, getTahun = 0;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_detail_laporan_keuangan, container, false);
    init(view);
    setOnClickListener();

//    dateChangeListener();

    return view;

  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    if (getArguments() != null) {
      Uid = getArguments().getString(KEY_ID);
    } else {
      // auth
      auth = FirebaseAuth.getInstance();
      user = auth.getCurrentUser();

      Uid = user.getUid();
    }
    GetDetail();
    getMonthNow();
    initTabLayout();
  }

  private void getMonthNow() {
    SimpleDateFormat formater = new SimpleDateFormat("MMM, yyyy");
    Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
    getTahun = calendar.get(Calendar.YEAR);
    getBulan = calendar.get(Calendar.MONTH) + 1;
    tv_bulan.setText(formater.format(calendar.getTime()));
  }

  private void dateChangeListener() {
    tv_bulan.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

      }

      @Override
      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        initTabLayout();
      }

      @Override
      public void afterTextChanged(Editable editable) {

      }
    });
  }

  private void GetDetail() {
    DatabaseReference listDb = FirebaseDatabase.getInstance().getReference().child("Users").child(Uid);
    listDb.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
        String nama_masjid = snapshot.child("nama_masjid").getValue(String.class);
        String tipe_tempat = snapshot.child("tipe_tempat").getValue(String.class);
        //set to layout
        tv_namaMasjid.setText(tipe_tempat + " " + nama_masjid);
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {

      }
    });

    // get deposit
    Query dbDana = FirebaseDatabase.getInstance().getReference().child("Dana").orderByChild("id_pengurus").equalTo(Uid);
    dbDana.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
        if (snapshot.exists()) {
          for (DataSnapshot npsnapshot : snapshot.getChildren()) {
            Double donasi_terkumpul = npsnapshot.child("total_dana").getValue(Double.class);
            tv_deposit.setText("Rp. " + Currencyfy.currencyfy(donasi_terkumpul, false, false));
            break;
          }
        } else {
          tv_deposit.setText("Rp 0");
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {

      }
    });
  }


  private void initTabLayout() {

    viewPager.clearOnPageChangeListeners();
    tabLayout.clearOnTabSelectedListeners();

    //Set up the view pager and fragments

    adapter = new TabFragmentAdapter(getChildFragmentManager());
    adapter.notifyDataSetChanged();
    adapter.addFragment(UangMasukFragment.newInstance(Uid, getBulan, getTahun), "Masuk");
    adapter.addFragment(UangKeluarFragment.newInstance(Uid, getBulan, getTahun), "Keluar");
    viewPager.setAdapter(adapter);
    tabLayout.setupWithViewPager(viewPager);
    //Determine indicator width at runtime
    tabLayout.post(new Runnable() {
      @Override
      public void run() {
        indicatorWidth = tabLayout.getWidth() / tabLayout.getTabCount();
        //Assign new width
        FrameLayout.LayoutParams indicatorParams = (FrameLayout.LayoutParams) indicator.getLayoutParams();
        indicatorParams.width = indicatorWidth;
        indicator.setLayoutParams(indicatorParams);
      }
    });

    viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) indicator.getLayoutParams();

        //Multiply positionOffset with indicatorWidth to get translation
        float translationOffset = (positionOffset + position) * indicatorWidth;
        params.leftMargin = (int) translationOffset;
        indicator.setLayoutParams(params);
      }

      @Override
      public void onPageSelected(int position) {

      }

      @Override
      public void onPageScrollStateChanged(int state) {

      }
    });
  }

  private void init(View view) {
    iv_back = view.findViewById(R.id.iv_back);
    iv_get_date = view.findViewById(R.id.iv_get_date);
    tv_bulan = view.findViewById(R.id.tv_bulan);
    tv_namaMasjid = view.findViewById(R.id.tv_nama_masjid);
    tv_deposit = view.findViewById(R.id.tv_deposit);
    // tabbed

    tabLayout = view.findViewById(R.id.tab);
    indicator = view.findViewById(R.id.indicator);
    viewPager = view.findViewById(R.id.viewPager);


  }

  private void setOnClickListener() {
    iv_back.setOnClickListener(this);
    iv_get_date.setOnClickListener(this);
  }


  @Override
  public void onClick(View view) {
    int id = view.getId();
    switch (id) {
      case R.id.iv_back:
        getActivity().onBackPressed();
        break;
      case R.id.iv_get_date:
        getDate();
        break;
    }
  }

  private void getDate() {
    new RackMonthPicker(getContext())
            .setLocale(Locale.US)
            .setPositiveButton(new DateMonthDialogListener() {
              @Override
              public void onDateMonth(int month, int startDate, int endDate, int year, String monthLabel) {
                getBulan = month;
                getTahun = year;
                tv_bulan.setText(monthLabel);
                initTabLayout();
              }
            })
            .setNegativeButton(new OnCancelMonthDialogListener() {
              @Override
              public void onCancel(AlertDialog dialog) {
                dialog.dismiss();
              }
            }).show();
  }

  @Override
  public void onResume() {
    super.onResume();
  }
}
