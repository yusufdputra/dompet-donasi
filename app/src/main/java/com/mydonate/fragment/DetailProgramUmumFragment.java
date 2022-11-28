package com.mydonate.fragment;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jpvs0101.currencyfy.Currencyfy;
import com.midtrans.sdk.corekit.callback.TransactionFinishedCallback;
import com.midtrans.sdk.corekit.core.MidtransSDK;
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme;
import com.midtrans.sdk.corekit.models.snap.TransactionResult;
import com.midtrans.sdk.uikit.SdkUIFlowBuilder;
import com.mydonate.BuildConfig;
import com.mydonate.R;
import com.mydonate.activity.DonasiActivity;
import com.mydonate.activity.HelperActivity;
import com.mydonate.activity.TambahProgramUmumActivity;
import com.mydonate.adapter.DaftarKebutuhanAdapter;
import com.mydonate.adapter.RiwayatDonasiAdapter;
import com.mydonate.component.NumberTextWatcher;
import com.mydonate.data.BayarKebutuhanData;
import com.mydonate.data.Donatur;
import com.mydonate.data.TransaksiData;
import com.mydonate.data.TransaksiPembayaranData;
import com.rd.PageIndicatorView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DetailProgramUmumFragment extends Fragment implements View.OnClickListener {
  public final static String KEY_NAMA_DONASI = "Program Umum";
  public final static String KEY_ID = "KEY_ID";
  private final ArrayList<String> keyItem = new ArrayList<>();
  ShimmerFrameLayout shimmerLayout;
  private TextView keterangan, nama, tv_no_data_riwayatDonasi, dana, title, penyaluran;
  private ImageView foto_tempat, iv_back, img_donasi;
  private RecyclerView rvRiwayatDonasi;
  private String Uid;
  private View popUpDialogView;
  private TextInputLayout textInputLayout_donasi;
  private LinearLayout ll_edit;
  private EditText tv_donasi;
  private Button simpan, cancel;
  private ArrayList<TransaksiPembayaranData> transaksiPembayaranData;
  private ShimmerFrameLayout shimmerImageView;
  private DatabaseReference mDbTransaksiPembayaran;

  public DetailProgramUmumFragment() {
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_detail_program_umum, container, false);


    initMidtransSDK();

    init(view);
    setOnClickListener();


    return view;
  }

  private void initMidtransSDK() {
    SdkUIFlowBuilder.init()
            .setContext(getContext())
            .setMerchantBaseUrl(BuildConfig.MERCHANT_BASE_URL)
            .setClientKey(BuildConfig.MERCHANT_CLIENT_KEY)

            .setTransactionFinishedCallback(new TransactionFinishedCallback() {
              @Override
              public void onTransactionFinished(TransactionResult transactionResult) {
                String order_id, payment_type, status_message, transaction_id, total_bayar, transaction_time, status_code, product_name, url_pdf;
                product_name = Uid;

                Log.i(TAG, "TRANSAKSI RESPON " + transactionResult.getStatus());
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                String idUser = currentUser.getUid();
                if (transactionResult.getResponse() != null) {
                  switch (transactionResult.getStatus()) {
                    case TransactionResult.STATUS_SUCCESS:
                      break;
                    case TransactionResult.STATUS_PENDING:
                      Toast.makeText(getContext(), "Transaksi Pending. ID " + transactionResult.getResponse().getTransactionId(), Toast.LENGTH_LONG).show();
                      order_id = transactionResult.getResponse().getOrderId();
                      payment_type = transactionResult.getResponse().getPaymentType();
                      status_message = transactionResult.getResponse().getStatusMessage();
                      transaction_id = transactionResult.getResponse().getTransactionId();
                      total_bayar = transactionResult.getResponse().getGrossAmount();
                      transaction_time = transactionResult.getResponse().getTransactionTime();
                      status_code = transactionResult.getResponse().getStatusCode();
                      url_pdf = transactionResult.getResponse().getPdfUrl();
                      SimpantoDatabase(order_id, payment_type, status_message, transaction_id, total_bayar, transaction_time, status_code, idUser, product_name, url_pdf);
                      break;
                    case TransactionResult.STATUS_FAILED:
                      Toast.makeText(getContext(), "Transaksi Gagal. ID " + transactionResult.getResponse().getTransactionId(), Toast.LENGTH_LONG).show();
                      break;
                  }
                  transactionResult.getResponse().getValidationMessages();
                } else if (transactionResult.isTransactionCanceled()) {
                  order_id = transactionResult.getResponse().getOrderId();
                  mDbTransaksiPembayaran.child(order_id).removeValue();
                  Toast.makeText(getContext(), "Transaksi dibatalkan.", Toast.LENGTH_LONG).show();
                } else {
                  if (transactionResult.getStatus().equalsIgnoreCase(TransactionResult.STATUS_INVALID)) {
                    Toast.makeText(getContext(), "Transaksi Invalid.", Toast.LENGTH_LONG).show();
                  } else {
                    Toast.makeText(getContext(), "Transaksi Selesai.", Toast.LENGTH_LONG).show();
                  }
                }
                goToHistoryClass();
              }
            })
            .enableLog(true)
            .setColorTheme(new CustomColorTheme("#F57F08", "#F5AC08", "#72FDAF00"))
            .buildSDK()
    ;
  }


  private void getListDonasi() {

    if (transaksiPembayaranData != null) {
      transaksiPembayaranData.clear();
    } else {
      transaksiPembayaranData = new ArrayList<>();
    }

    Query db = FirebaseDatabase.getInstance().getReference().child("Transaksi Pembayaran").orderByChild("product_name").equalTo(Uid);
    db.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
        if (snapshot.exists()) {
          for (DataSnapshot npsnapshot : snapshot.getChildren()) {
            String status_code = npsnapshot.child("status_code").getValue(String.class);
            if (status_code != null) {
              String id_kebutuhan = npsnapshot.child("product_name").getValue(String.class);

              if (status_code.equals("200") && !id_kebutuhan.equals(DonasiUmumFragment.KEY_NAMA_DONASI)) {


                TransaksiPembayaranData list = npsnapshot.getValue(TransaksiPembayaranData.class);
                transaksiPembayaranData.add(list);
                keyItem.add(npsnapshot.getKey());

                Log.i("AS", "span " + transaksiPembayaranData.size());

                if (transaksiPembayaranData.size() > 0) {
                  shimmerLayout.stopShimmer();
                  shimmerLayout.setVisibility(View.GONE);

                  rvRiwayatDonasi.setVisibility(View.VISIBLE);
                  tv_no_data_riwayatDonasi.setVisibility(View.GONE);
                  RiwayatDonasiAdapter riwayatDonasiAdapter = new RiwayatDonasiAdapter(getContext(), transaksiPembayaranData, keyItem, "donasi");
                  LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                  layoutManager.setReverseLayout(true);
                  layoutManager.setStackFromEnd(true);
                  rvRiwayatDonasi.setLayoutManager(layoutManager);
                  rvRiwayatDonasi.setAdapter(riwayatDonasiAdapter);
                } else {
                  shimmerLayout.stopShimmer();
                  shimmerLayout.setVisibility(View.GONE);

                  rvRiwayatDonasi.setVisibility(View.GONE);
                  tv_no_data_riwayatDonasi.setVisibility(View.VISIBLE);
                }


              }
//                            else {
//                                shimmerLayout.stopShimmer();
//                                shimmerLayout.setVisibility(View.GONE);
//                                rvRiwayatDonasi.setVisibility(View.GONE);
//                                tv_no_data_riwayatDonasi.setVisibility(View.VISIBLE);
//                            }
            }

          }


        } else {
          shimmerLayout.stopShimmer();
          shimmerLayout.setVisibility(View.GONE);
          rvRiwayatDonasi.setVisibility(View.GONE);
          tv_no_data_riwayatDonasi.setVisibility(View.VISIBLE);
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {

      }
    });

  }


  private void GetDetail() {
    // cek user login
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    String idUser = currentUser.getUid();
    Query jLoginDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(idUser);
    jLoginDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
        if (snapshot.exists()) {
          String tipe = snapshot.child("tipe_user").getValue(String.class);
          if (tipe.equals(LoginFragment.ADMIN_LOGIN)) {
            img_donasi.setVisibility(View.GONE);
          }
          if (tipe.equals(LoginFragment.ADMIN_LOGIN)){
            ll_edit.setVisibility(View.VISIBLE);
          }
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {

      }
    });

    DatabaseReference listDb = FirebaseDatabase.getInstance().getReference().child("Program Umum").child(Uid);
    listDb.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {

        if (snapshot.exists()) {
          String UNama = snapshot.child("nama").getValue(String.class);
          String UKeterangan = snapshot.child("keterangan").getValue(String.class);
          String UFoto = snapshot.child("foto").getValue(String.class);
          String UDana = snapshot.child("dana").getValue(String.class);

          String donasi = UDana.replaceAll("[^a-zA-Z0-9]", "");

          title.setText("Program " + UNama);

          nama.setText(UNama);
          dana.setText("Rp. " + Currencyfy.currencyfy(Double.parseDouble(donasi), false, false));
          keterangan.setText(UKeterangan);

          HelperActivity.setImageView(UFoto, foto_tempat, shimmerImageView);

        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {

      }
    });

  }


  private void init(View view) {
    tv_no_data_riwayatDonasi = view.findViewById(R.id.tv_no_data2);
    ll_edit = view.findViewById(R.id.ll_edit);

    title = view.findViewById(R.id.tv_title);
    rvRiwayatDonasi = view.findViewById(R.id.rv_riwayat_donasi);

    foto_tempat = view.findViewById(R.id.imageView);
    img_donasi = view.findViewById(R.id.iv_donate_kebutuhan);
    iv_back = view.findViewById(R.id.iv_back);
    penyaluran = view.findViewById(R.id.tv_penyaluran);


    nama = view.findViewById(R.id.tv_nama_program);
    keterangan = view.findViewById(R.id.tv_keterangan);
    dana = view.findViewById(R.id.tv_deposit);

    shimmerImageView = view.findViewById(R.id.shimmerImageView);
    shimmerLayout = view.findViewById(R.id.shimmerlayout);


    mDbTransaksiPembayaran = FirebaseDatabase.getInstance().getReference("Transaksi Pembayaran");
  }

  private void setOnClickListener() {
    iv_back.setOnClickListener(this);
    img_donasi.setOnClickListener(this);
    penyaluran.setOnClickListener(this);
    ll_edit.setOnClickListener(this);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    shimmerImageView.startShimmer();
    shimmerLayout.startShimmer();
  }

  @SuppressLint("NonConstantResourceId")
  @Override
  public void onClick(View v) {
    Bundle bundle = new Bundle();
//        bundle.putString(DetailDonasiMasjidMusholaFragment.NAMA_MASJID_BUNDLE_KEY, Uid);
    switch (v.getId()) {
      case R.id.iv_back:
        getActivity().onBackPressed();
        break;
      case R.id.iv_donate_kebutuhan:
        do_donasi(v);
        break;
      case R.id.ll_edit:
        Intent intent = new Intent(getContext(), TambahProgramUmumActivity.class);
        intent.putExtra(KEY_ID, Uid);
        startActivity(intent);

        break;
      case R.id.tv_penyaluran:

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String idUser = currentUser.getUid();

        bundle.putString(KEY_ID, Uid);
        bundle.putString(DaftarKebutuhanAdapter.KEY_ID_USER, idUser);

        PenyerahanPUGridFragment fragment = new PenyerahanPUGridFragment();
        fragment.setArguments(bundle);
        AppCompatActivity activity = (AppCompatActivity) v.getContext();
        activity.getSupportFragmentManager().beginTransaction().replace(R.id.home_frame_layout, fragment).addToBackStack(null).commit();
        break;
    }
  }


  private void do_donasi(View view) {

    AlertDialog.Builder alerBuilder = new AlertDialog.Builder(view.getContext());
    alerBuilder.setTitle("Donasi berapa hari ini?");
    alerBuilder.setCancelable(false);
    initPopupViewControls(view);

    // Set the inflated layout view object to the AlertDialog builder
    alerBuilder.setView(popUpDialogView);
    // Create AlertDialog and show.
    final AlertDialog alertDialog = alerBuilder.create();
    alertDialog.show();

    simpan.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {

        if (!validasiNominal()) {
          return;
        } else {

          //get inputan
          String donasi = tv_donasi.getText().toString();
          String donasi_final = donasi.replaceAll("[^a-zA-Z0-9]", "");

          FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
          String idUser = currentUser.getUid();

          Double nilai_donasi = (Double.parseDouble(donasi_final));
          MidtransSDK.getInstance().setTransactionRequest(TransaksiData.transactionRequest(
                  idUser,
                  nilai_donasi,
                  1,
                  Uid,
                  nilai_donasi
          ));
          MidtransSDK.getInstance().startPaymentUiFlow(view.getContext());


        }

      }
    });

    cancel.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        alertDialog.cancel();
      }
    });

  }

  private void initPopupViewControls(View view) {
    //get layout
    LayoutInflater layoutInflater = LayoutInflater.from(view.getContext());
    popUpDialogView = layoutInflater.inflate(R.layout.popup_nominal_donasi, null);
    textInputLayout_donasi = popUpDialogView.findViewById(R.id.textInputLayout_donasi);

    tv_donasi = popUpDialogView.findViewById(R.id.tv_donasi);
    tv_donasi.addTextChangedListener(new NumberTextWatcher(tv_donasi));

    cancel = popUpDialogView.findViewById(R.id.btn_cancel);
    simpan = popUpDialogView.findViewById(R.id.btn_simpan);

  }

  private boolean validasiNominal() {
    String nominal_kebutuhan = tv_donasi.getText().toString().trim();
    if (nominal_kebutuhan.isEmpty()) {
      tv_donasi.setError("Field tidak boleh kosong");
      return false;
    } else {
      tv_donasi.setError(null);
      return true;
    }
  }

  private void goToHistoryClass() {

    Intent intent = null;
    intent = new Intent(getContext(), DonasiActivity.class);
    intent.putExtra(DonasiActivity.DONATION_TYPE_KEY_EXTRA, HomeDonaturFragment.USER_DONASIKU_KEY_EXTRA);
    startActivity(intent);
    getActivity().finish();
  }

  private void SimpantoDatabase(String order_id, String payment_type, String status_message, String transaction_id, String total_bayar, String transaction_time, String status_code, String id_donatur, String product_name, String url_pdf) {

    TransaksiPembayaranData upload = new TransaksiPembayaranData(
            order_id,
            payment_type,
            status_message,
            transaction_id,
            total_bayar,
            transaction_time,
            status_code,
            id_donatur,
            product_name,
            url_pdf
    );
    mDbTransaksiPembayaran.child(order_id).setValue(upload);
  }


  @Override
  public void onStart() {
    if (getArguments() != null) {
      Uid = getArguments().getString(KEY_ID);
      GetDetail();
      getListDonasi();
    }
    super.onStart();
  }
}