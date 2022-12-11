package com.mydonate.fragment.item;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.widget.ContentLoadingProgressBar;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jpvs0101.currencyfy.Currencyfy;
import com.mydonate.R;
import com.mydonate.adapter.SpinnerTujuanKebutuhanAdapter;
import com.mydonate.component.NumberTextWatcher;
import com.mydonate.data.KebutuhanData;
import com.mydonate.data.PengajuanPencairanDanaData;

import java.util.ArrayList;

public class ItemPencairanDanaDialogFragment extends BottomSheetDialogFragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static ArrayList<KebutuhanData> kebutuhanDataList;
    private static ArrayList<String> spinner_id_kebutuhan;
    private AppCompatSpinner sp_pilihan_kebutuhan;
    private TextInputLayout error_nominal, error_keterangan;
    private TextInputEditText et_nominal, et_keterangan;
    private Button btn_ajukan;
    private ContentLoadingProgressBar progressBar;
    private TextView tv_nominalAsalDana;
    private ArrayList<String> spinner_nama_kebutuhan = new ArrayList<>();
    private ArrayList<String> spinner_detail_kebutuhan = new ArrayList<>();
    private ArrayList<String> spinner_biaya_kebutuhan = new ArrayList<>();
    private Double terkumpul;
    private DatabaseReference ref_bayar_kebutuhan, ref_pengajuan;

    private static String Getid_pengurus;
    private String Getid_kebutuhan;

    private BottomSheetDialogFragment bottomSheetDialogFragment;

    // TODO: Customize parameters
    public static ItemPencairanDanaDialogFragment newInstance(ArrayList<KebutuhanData> kebutuhanData, ArrayList<String> id_kebutuhanUmum, String uid) {
        final ItemPencairanDanaDialogFragment fragment = new ItemPencairanDanaDialogFragment();
        final Bundle args = new Bundle();
        kebutuhanDataList = kebutuhanData;
        spinner_id_kebutuhan = id_kebutuhanUmum;
        Getid_pengurus = uid;
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_item_pencairan_dana_list_dialog, container, false);
        init(view);
        setClickListener();


        for (int i = 0; i < kebutuhanDataList.size(); i++) {
            KebutuhanData id = kebutuhanDataList.get(i);

            // cek kebutuhan sudah selesai atau belum
            String kebutuhan = id.getBiaya_kebutuhan();
            String biaya_kebutuhan_conv = kebutuhan.replaceAll("[^a-zA-Z0-9]", "");
            Double sisa = Double.parseDouble(biaya_kebutuhan_conv);
            if (sisa > 0) {
                spinner_nama_kebutuhan.add(id.getNama_kebutuhan());
                spinner_detail_kebutuhan.add(id.getJenis_kebutuhan());
                spinner_biaya_kebutuhan.add(id.getBiaya_kebutuhan());

                SpinnerTujuanKebutuhanAdapter spinner = new SpinnerTujuanKebutuhanAdapter(view.getContext(), spinner_nama_kebutuhan, spinner_detail_kebutuhan, spinner_id_kebutuhan);
                sp_pilihan_kebutuhan.setAdapter(spinner);
            }

        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        bottomSheetDialogFragment = new BottomSheetDialogFragment();


        et_nominal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String getDAna = String.valueOf(et_nominal.getText());
                String getDAna_con = getDAna.replaceAll("[^a-zA-Z0-9]", "");
                //bandingkan
                if (!getDAna_con.isEmpty()) {
                    if (Double.parseDouble(getDAna_con) <= (terkumpul)) {
                        enableButton();
                    } else {
                        disableButton();
                    }
                }
            }
        });
    }

    private void setClickListener() {
        btn_ajukan.setOnClickListener(this);
        sp_pilihan_kebutuhan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Getid_kebutuhan = spinner_id_kebutuhan.get(i);

                ref_bayar_kebutuhan.child(Getid_kebutuhan).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String sisa_nominal = snapshot.child("sisa_nominal_kebutuhan").getValue().toString();
                            Double sisa_nominal_conv = Double.valueOf(sisa_nominal.replaceAll("[^a-zA-Z0-9]", ""));

                            Double biaya_kebutuhan = Double.valueOf(spinner_biaya_kebutuhan.get(i).replaceAll("[^a-zA-Z0-9]", ""));

                            terkumpul = biaya_kebutuhan - sisa_nominal_conv;
                            Log.i(TAG, "terkumpul: " + terkumpul);
                            if (terkumpul == 0) {
                                disableButton();
                                tv_nominalAsalDana.setText("Rp. 0");
                            } else {
                                tv_nominalAsalDana.setText("Rp. " + Currencyfy.currencyfy(terkumpul, false, false));
                                enableButton();
                            }


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void disableButton() {
        if (getActivity() != null && isAdded()) {
            btn_ajukan.setBackground(getResources().getDrawable(R.drawable.background_disable_button));
            btn_ajukan.setTextColor(getResources().getColor(R.color.black_trans));
            btn_ajukan.setText("Dana Belum Terkumpul");
            btn_ajukan.setEnabled(false);
        }

    }

    private void enableButton() {
        if (getActivity() != null && isAdded()) {
            btn_ajukan.setBackground(getResources().getDrawable(R.drawable.background_login_button));
            btn_ajukan.setTextColor(getResources().getColor(R.color.darkerOrange));
            btn_ajukan.setText("Ajukan Permohonan");
            btn_ajukan.setEnabled(true);
        }
    }

    private void init(View view) {
        sp_pilihan_kebutuhan = view.findViewById(R.id.sp_pilih_kebutuhan);
        error_nominal = view.findViewById(R.id.textInputLayout_biaya);
        error_keterangan = view.findViewById(R.id.textInputLayout_keterangan);

        et_nominal = view.findViewById(R.id.et_biaya);
        et_keterangan = view.findViewById(R.id.et_keterangan);
        btn_ajukan = view.findViewById(R.id.btn_ajukan);
        tv_nominalAsalDana = view.findViewById(R.id.tv_nominalAsalDana);

        progressBar = view.findViewById(R.id.progress_bar);


        et_nominal.addTextChangedListener(new NumberTextWatcher(et_nominal));

        ref_bayar_kebutuhan = FirebaseDatabase.getInstance().getReference().child("Bayar Kebutuhan");
        ref_pengajuan = FirebaseDatabase.getInstance().getReference().child(getResources().getString(R.string.ref_pengajuan_pencairan));
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btn_ajukan:
                if (!validasiPencairan(error_nominal) || !validasiPencairan(error_keterangan)) {
                    return;
                } else {
                    ajukanPermohonan();
                }
                break;
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    private void ajukanPermohonan() {
        progressBar.setVisibility(View.VISIBLE);
        // cek apakah sudah ada atau belum
        try {
            ref_pengajuan.orderByChild("id_kebutuhan").equalTo(Getid_kebutuhan).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()) {
                        savePengajuan();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } catch (Exception e) {

        }


    }

    private void savePengajuan() {
        PengajuanPencairanDanaData data = new PengajuanPencairanDanaData(
                Getid_kebutuhan,
                Getid_pengurus,
                et_nominal.getText().toString().replaceAll("[^a-zA-Z0-9]", ""),
                et_keterangan.getText().toString(),
                "baru"
        );

        String key = ref_pengajuan.push().getKey();
        ref_pengajuan.child(key).setValue(data);
        progressBar.setVisibility(View.GONE);
        Toast.makeText(getContext(), "Pengajuan Pencairan Dana Berhasil.", Toast.LENGTH_LONG).show();

        this.dismiss();
    }

    private boolean validasiPencairan(TextInputLayout error) {
        String errors = error.getEditText().getText().toString().trim();
        if (errors.isEmpty()) {
            error.setError("Field tidak boleh kosong");
            return false;
        } else {
            error.setError(null);
            return true;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}