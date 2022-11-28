package com.mydonate.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.midtrans.sdk.corekit.callback.TransactionFinishedCallback;
import com.midtrans.sdk.corekit.core.MidtransSDK;
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme;
import com.midtrans.sdk.corekit.models.snap.TransactionResult;
import com.midtrans.sdk.uikit.SdkUIFlowBuilder;
import com.mydonate.BuildConfig;
import com.mydonate.R;
import com.mydonate.activity.DonasiActivity;
import com.mydonate.adapter.DaftarKebutuhanAdapter;
import com.mydonate.component.NumberTextWatcher;
import com.mydonate.data.RiwayatPembayaranData;
import com.mydonate.data.TransaksiData;
import com.mydonate.data.TransaksiPembayaranData;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class DetailDonasiMasjidMusholaFragment extends Fragment implements View.OnClickListener, TransactionFinishedCallback {

    public final static String NAMA_MASJID_BUNDLE_KEY = "NamaMasjid";
    public final static String KEBUTUHAN_DONASI_BUNDLE_KEY = "KebutuhanDonasi";
    private TextView tvTitle, biaya_kebutuhan, kebutuhanDonasi;
    private TextView tvKebutuhanDonasi;
    private TextView tvKeterangan;
    private String UidKebutuhan, UIdPengurus, UnamaKebutuhan, UbiayaKebutuhan, UtotalBayar;
    private ImageView back, iv_informasi;
    private EditText nominal_donasi;
    private Button btnSubmit;
    private String namaMasjid;
    private DatabaseReference mStorageRef, mDbTransaksiPembayaran;

    public DetailDonasiMasjidMusholaFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_donasi_masjid_mushola, container, false);
        if (getArguments() != null) {
            UidKebutuhan = getArguments().getString(DetailMasjidMusholaFragment.KEY_ID);
            UnamaKebutuhan = getArguments().getString(DaftarKebutuhanAdapter.KEY_NAMA_KEBUTUHAN);
            UbiayaKebutuhan = getArguments().getString(DaftarKebutuhanAdapter.KEY_BIAYA_KEBUTUHAN);
            UIdPengurus = getArguments().getString(DaftarKebutuhanAdapter.KEY_ID_PENGURUS);
            UtotalBayar = getArguments().getString(DaftarKebutuhanAdapter.KEY_DONASI);
        }
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
                .setTransactionFinishedCallback(this)
                .enableLog(true)
                .setColorTheme(new CustomColorTheme("#F57F08", "#F5AC08", "#72FDAF00"))
                .buildSDK()
        ;
    }

    private void getDetailDonasi() {
        DatabaseReference listDb = FirebaseDatabase.getInstance().getReference().child("Kebutuhan");
        listDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setOnClickListener() {
        btnSubmit.setOnClickListener(this);
        back.setOnClickListener(this);

    }

    private void init(View view) {
        back = view.findViewById(R.id.iv_back);
        kebutuhanDonasi = view.findViewById(R.id.tv_kebutuhan);

        tvTitle = view.findViewById(R.id.tv_title);
        tvKeterangan = view.findViewById(R.id.tv_keterangan);
        btnSubmit = view.findViewById(R.id.btn_submit);
        nominal_donasi = view.findViewById(R.id.et_nominal_donasi);
        nominal_donasi.addTextChangedListener(new NumberTextWatcher(nominal_donasi));

        mStorageRef = FirebaseDatabase.getInstance().getReference("Riwayat Pembayaran");
        mDbTransaksiPembayaran = FirebaseDatabase.getInstance().getReference("Transaksi Pembayaran");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvTitle.append(UnamaKebutuhan);
        tvKeterangan.append(getString(R.string.keterangan_donasi_postfix));
        kebutuhanDonasi.setText(UnamaKebutuhan);
        nominal_donasi.setText(UtotalBayar);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                getActivity().onBackPressed();
                break;
            case R.id.btn_submit:
                payMidtrans(v);
                break;

        }
    }

    private void payMidtrans(View v) {
        try {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            String idUser = currentUser.getUid();

            String nominal = nominal_donasi.getText().toString();
            String nominal_conv = nominal.replaceAll("[^a-zA-Z0-9]", "");
            Double nilai_donasi = (Double.parseDouble(nominal_conv));
            MidtransSDK.getInstance().setTransactionRequest(TransaksiData.transactionRequest(
                    idUser,
                    nilai_donasi,
                    1,
                    UidKebutuhan,
                    nilai_donasi
            ));
            MidtransSDK.getInstance().startPaymentUiFlow(v.getContext());
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Gagal dalam pembayaran, Hubungi Admin.", Toast.LENGTH_SHORT).show();
        }

    }


    private void setPembayaran(View v) {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.show();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String idUser = currentUser.getUid();
        try {

            Date c = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
            String formattedDate = df.format(c);

            String nominal = nominal_donasi.getText().toString();
            RiwayatPembayaranData upload = new RiwayatPembayaranData(
                    UidKebutuhan,
                    idUser,
                    nominal,
                    null,
                    false,
                    formattedDate,
                    null
            );


            String key = mStorageRef.push().getKey();
            mStorageRef.child(key).setValue(upload);
            Toast.makeText(getContext(), "Berhasil melakukan donasi. Silahkan upload bukti pembayaran untuk menindaklanjuti. Terimakasih", Toast.LENGTH_SHORT).show();

            progressDialog.dismiss();

            getActivity().finish();
            Intent intent = null;
            intent = new Intent(getContext(), DonasiActivity.class);
            intent.putExtra(DonasiActivity.DONATION_TYPE_KEY_EXTRA, HomeDonaturFragment.USER_DONASIKU_KEY_EXTRA);
            startActivity(intent);
//            DonasikuFragment donasikuFragment = new DonasikuFragment();
//            getActivity().getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.home_frame_layout, donasikuFragment)
//                    .addToBackStack(null)
//                    .commit();


        } catch (Exception e) {
            progressDialog.cancel();
            Snackbar snackbar = Snackbar
                    .make(v, "Terjadi Kesalahan!", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    @Override
    public void onTransactionFinished(TransactionResult transactionResult) {
        String order_id, payment_type, status_message, transaction_id, total_bayar, transaction_time, status_code, product_name, url_pdf;
        product_name = UidKebutuhan;
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String idUser = currentUser.getUid();

        if (transactionResult.getResponse() != null) {
            switch (transactionResult.getStatus()) {
                case TransactionResult.STATUS_SUCCESS:
                    Toast.makeText(this.getContext(), "Transaksi Selesai. ID " + transactionResult.getResponse().getTransactionId(), Toast.LENGTH_LONG).show();
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
                case TransactionResult.STATUS_PENDING:
                    Toast.makeText(this.getContext(), "Transaksi Pending. ID " + transactionResult.getResponse().getTransactionId(), Toast.LENGTH_LONG).show();
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
                    Toast.makeText(this.getContext(), "Transaksi Gagal. ID " + transactionResult.getResponse().getTransactionId(), Toast.LENGTH_LONG).show();
                    break;
            }

            transactionResult.getResponse().getValidationMessages();


        } else if (transactionResult.isTransactionCanceled()) {
            order_id = transactionResult.getResponse().getOrderId();
            mDbTransaksiPembayaran.child(order_id).removeValue();
            Toast.makeText(this.getContext(), "Transaksi dibatalkan.", Toast.LENGTH_LONG).show();
        } else {
            if (transactionResult.getStatus().equalsIgnoreCase(TransactionResult.STATUS_INVALID)) {
                order_id = transactionResult.getResponse().getOrderId();
                mDbTransaksiPembayaran.child(order_id).removeValue();
                Toast.makeText(this.getContext(), "Transaksi Invalid.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this.getContext(), "Transaksi Selesai.", Toast.LENGTH_LONG).show();

//                order_id = transactionResult.getResponse().getOrderId();
//                payment_type = transactionResult.getResponse().getPaymentType();
//                status_message = transactionResult.getResponse().getStatusMessage();
//                transaction_id = transactionResult.getResponse().getTransactionId();
//                total_bayar = transactionResult.getResponse().getGrossAmount();
//                transaction_time = transactionResult.getResponse().getTransactionTime();
//                status_code = transactionResult.getResponse().getStatusCode();
//                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
//                String idUser = currentUser.getUid();
//                url_pdf = transactionResult.getResponse().getPdfUrl();
//
//                SimpantoDatabase(order_id, payment_type, status_message, transaction_id, total_bayar, transaction_time, status_code, idUser, product_name, url_pdf);
//                goToHistoryClass();

            }

        }
        goToHistoryClass();
    }
    private void goToHistoryClass() {
        Intent intent = new Intent(getContext(), DonasiActivity.class);
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
    public void onResume() {
        super.onResume();

    }
}