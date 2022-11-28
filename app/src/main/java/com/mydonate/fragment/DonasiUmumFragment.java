package com.mydonate.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Html;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jpvs0101.currencyfy.Currencyfy;
import com.midtrans.sdk.corekit.callback.TransactionCallback;
import com.midtrans.sdk.corekit.callback.TransactionFinishedCallback;
import com.midtrans.sdk.corekit.core.BaseSdkBuilder;
import com.midtrans.sdk.corekit.core.MidtransSDK;
import com.midtrans.sdk.corekit.core.PaymentMethod;
import com.midtrans.sdk.corekit.core.SdkCoreFlowBuilder;
import com.midtrans.sdk.corekit.core.TransactionRequest;
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme;
import com.midtrans.sdk.corekit.models.BankType;
import com.midtrans.sdk.corekit.models.CustomerDetails;
import com.midtrans.sdk.corekit.models.ItemDetails;
import com.midtrans.sdk.corekit.models.snap.CreditCard;
import com.midtrans.sdk.corekit.models.snap.TransactionResult;
import com.midtrans.sdk.uikit.SdkUIFlowBuilder;
import com.mydonate.BuildConfig;
import com.mydonate.R;
import com.mydonate.activity.DonasiActivity;
import com.mydonate.activity.HomeActivity;
import com.mydonate.activity.ProfilActivity;
import com.mydonate.adapter.DaftarKebutuhanAdapter;
import com.mydonate.adapter.RiwayatDonasiAdapter;
import com.mydonate.component.NumberTextWatcher;
import com.mydonate.data.KebutuhanData;
import com.mydonate.data.RiwayatPembayaranData;
import com.mydonate.data.TransaksiData;
import com.mydonate.data.TransaksiPembayaranData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import static android.content.ContentValues.TAG;

public class DonasiUmumFragment extends Fragment implements View.OnClickListener {
    public final static String KEY_NAMA_DONASI = "Donasi Umum";

    private TextView tvKeterangan, tvDonasiTerkumpul;
    private ImageView back, iv_informasi;
    private EditText etNominalDonasi;
    private Button btnSubmit;
    private View popUpDialogView;
    private TextInputLayout textInputLayout_donasi, error_nominal;
    private EditText tv_donasi;
    private Button simpan, cancel;
    private DatabaseReference mStorageRef, mDbTransaksiPembayaran;

    private FloatingActionButton floatingActionButton;

    private ArrayList<TransaksiPembayaranData> transaksiPembayaranData;
    private ArrayList<String> keyItem = new ArrayList<>();


    public DonasiUmumFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_donasi_umum, container, false);
        initMidtransSDK();
        init(view);
        setClickListener();

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
                        product_name = KEY_NAMA_DONASI;

                        Log.i(TAG, "TRANSAKSI RESPON " + transactionResult.getStatus());
                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                        String idUser = currentUser.getUid();
                        if (transactionResult.getResponse() != null) {
                            switch (transactionResult.getStatus()) {
                                case TransactionResult.STATUS_SUCCESS:
//                    Toast.makeText(this.getContext(), "Transaksi Selesai. ID " + transactionResult.getResponse().getTransactionId(), Toast.LENGTH_LONG).show();
//                    order_id = transactionResult.getResponse().getOrderId();
//                    payment_type = transactionResult.getResponse().getPaymentType();
//                    status_message = transactionResult.getResponse().getStatusMessage();
//                    transaction_id = transactionResult.getResponse().getTransactionId();
//                    total_bayar = transactionResult.getResponse().getGrossAmount();
//                    transaction_time = transactionResult.getResponse().getTransactionTime();
//                    status_code = transactionResult.getResponse().getStatusCode();
//                    url_pdf = transactionResult.getResponse().getPdfUrl();
//                    SimpantoDatabase(order_id, payment_type, status_message, transaction_id, total_bayar, transaction_time, status_code, idUser, product_name, url_pdf);
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
//
//                order_id = transactionResult.getResponse().getOrderId();
//                payment_type = transactionResult.getResponse().getPaymentType();
//                status_message = transactionResult.getResponse().getStatusMessage();
//                transaction_id = transactionResult.getResponse().getTransactionId();
//                total_bayar = transactionResult.getResponse().getGrossAmount();
//                transaction_time = transactionResult.getResponse().getTransactionTime();
//                status_code = transactionResult.getResponse().getStatusCode();
//                url_pdf = transactionResult.getResponse().getPdfUrl();
//
//                SimpantoDatabase(order_id, payment_type, status_message, transaction_id, total_bayar, transaction_time, status_code, idUser, product_name, url_pdf);
//                goToHistoryClass();

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

    private void setClickListener() {
        back.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        etNominalDonasi.setOnClickListener(this);

    }

    private void init(View view) {
        back = view.findViewById(R.id.iv_back);
        etNominalDonasi = view.findViewById(R.id.et_nominal_donasi);
        tvDonasiTerkumpul = view.findViewById(R.id.donasi_terkumpul);
        error_nominal = view.findViewById(R.id.error_nominal);
        tvKeterangan = view.findViewById(R.id.tv_keterangan);
        btnSubmit = view.findViewById(R.id.btn_submit);

        etNominalDonasi.addTextChangedListener(new NumberTextWatcher(etNominalDonasi));

        floatingActionButton = view.findViewById(R.id.floatingActionButton);

        mStorageRef = FirebaseDatabase.getInstance().getReference("Riwayat Pembayaran");
        mDbTransaksiPembayaran = FirebaseDatabase.getInstance().getReference("Transaksi Pembayaran");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String donasiUmumTeks = " <font color='#EE0000'>" + getString(R.string.keterangan_donasi_umum) + "</font>";
        tvKeterangan.append(Html.fromHtml(donasiUmumTeks));
        tvKeterangan.append(getString(R.string.keterangan_donasi_postfix));

        //set donasi terkumpul
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Dana");
        dbRef.orderByChild("jenis_dana").equalTo(DonasiUmumFragment.KEY_NAMA_DONASI).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot npsnapshot : snapshot.getChildren()) {
                        Double donasi_terkumpul = npsnapshot.child("total_dana").getValue(Double.class);
                        tvDonasiTerkumpul.setText("Rp. " + Currencyfy.currencyfy(donasi_terkumpul, false, false));
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        floatingActionButton.setColorFilter(getResources().getColor(R.color.white));

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RiwayatDonasiUmumFragment fragment = new RiwayatDonasiUmumFragment();
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.home_frame_layout, fragment).addToBackStack(null).commit();
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                getActivity().onBackPressed();
                break;
            case R.id.et_nominal_donasi:
                showPopUp();
                break;

            case R.id.btn_submit:
                if (!validasiNominalDonasi()) {
                    return;
                } else {
                    payMidtrans(v);
                }
//                setPembayaran(v);
                break;
        }
    }

    private void payMidtrans(View v) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String idUser = currentUser.getUid();

        String nominal = etNominalDonasi.getText().toString();
        String nominal_conv = nominal.replaceAll("[^a-zA-Z0-9]", "");
        Double nilai_donasi = (Double.parseDouble(nominal_conv));
        MidtransSDK.getInstance().setTransactionRequest(TransaksiData.transactionRequest(
                idUser,
                nilai_donasi,
                1,
                KEY_NAMA_DONASI,
                nilai_donasi
        ));
        MidtransSDK.getInstance().startPaymentUiFlow(v.getContext());
    }


    //pop up
    private void showPopUp() {
        AlertDialog.Builder alerBuilder = new AlertDialog.Builder(getActivity());
        alerBuilder.setTitle("Donasi berapa hari ini?");
        alerBuilder.setCancelable(false);

        // Init popup dialog view and it's ui controls
        initPopupViewControls();
        // Set the inflated layout view object to the AlertDialog builder
        alerBuilder.setView(popUpDialogView);
        // Create AlertDialog and show.
        final AlertDialog alertDialog = alerBuilder.create();
        alertDialog.show();

        //set on click

        simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!validasiNominal()) {
                    return;
                } else {
                    //get inputan
                    String donasi = tv_donasi.getText().toString();
//                    String donasi_final = donasi.replaceAll("[^a-zA-Z0-9]", "");
//                    Random random = new Random();
//                    int x = random.nextInt(999);
//                    int nilai_donasi = (Integer.parseInt(donasi_final)) + x;
                    etNominalDonasi.setText(String.valueOf(donasi));
                    alertDialog.cancel();
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

    private void setPembayaran(View v) {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.show();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String idUser = currentUser.getUid();
        try {

            Date c = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
            String formattedDate = df.format(c);

            String nominal = etNominalDonasi.getText().toString();

            RiwayatPembayaranData upload = new RiwayatPembayaranData(
                    null,
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


        } catch (Exception e) {
            progressDialog.cancel();
            Snackbar snackbar = Snackbar
                    .make(v, "Terjadi Kesalahan!", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    private void initPopupViewControls() {
        //get layout
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        popUpDialogView = layoutInflater.inflate(R.layout.popup_nominal_donasi, null);
        textInputLayout_donasi = popUpDialogView.findViewById(R.id.textInputLayout_donasi);

        tv_donasi = popUpDialogView.findViewById(R.id.tv_donasi);
        tv_donasi.addTextChangedListener(new NumberTextWatcher(tv_donasi));

        cancel = popUpDialogView.findViewById(R.id.btn_cancel);
        simpan = popUpDialogView.findViewById(R.id.btn_simpan);
        simpan.setOnClickListener(this);

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

    private boolean validasiNominalDonasi() {
        String nominal = error_nominal.getEditText().getText().toString().trim();
        if (nominal.isEmpty()) {
            error_nominal.setError("Field tidak boleh kosong");
            return false;
        } else {
            error_nominal.setError(null);
            return true;
        }
    }
}