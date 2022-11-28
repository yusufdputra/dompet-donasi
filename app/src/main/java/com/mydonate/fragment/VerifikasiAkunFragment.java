package com.mydonate.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mydonate.R;
import com.mydonate.adapter.DaftarAkunPengurusAdapter;
import com.mydonate.adapter.DaftarKebutuhanAdapter;
import com.mydonate.adapter.DaftarMasjidMusholaAdapter;
import com.mydonate.adapter.RiwayatDonasiAdapter;
import com.mydonate.data.Donatur;
import com.mydonate.data.DonaturData;
import com.mydonate.data.KebutuhanData;
import com.mydonate.data.MasjidMushola;
import com.mydonate.data.PengurusData;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class VerifikasiAkunFragment extends Fragment {
    private RecyclerView rv_VerifikasiAkun;
    ArrayList<String> keyItem = new ArrayList<>();
    private ArrayList<PengurusData> pengurusData;
    private ImageView iv_back;
    private TextView tv_no_data;

    public VerifikasiAkunFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_verifikasi_akun, container, false);

        init(view);
        GetAkunPengurus();

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        return view;
    }

    private void init(View view) {
        rv_VerifikasiAkun = (RecyclerView) view.findViewById(R.id.rv_verifikasi_pengurus);
        iv_back = (ImageView) view.findViewById(R.id.iv_back);
        tv_no_data = (TextView) view.findViewById(R.id.tv_no_data);
    }


    private void GetAkunPengurus() {
        if (pengurusData != null) {
            pengurusData.clear();
        } else {
            pengurusData = new ArrayList<>();
        }
        //get item list pengurus
        DatabaseReference listDb = FirebaseDatabase.getInstance().getReference().child("Users");
        listDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pengurusData.clear();
                keyItem.clear();
                if (snapshot.exists()) {


                    for (DataSnapshot npsnapshot : snapshot.getChildren()) {

                        Object tipe_user = npsnapshot.child("tipe_user").getValue();
                        if (tipe_user.equals(LoginFragment.PENGURUS_LOGIN)) {
                            Object status_user = npsnapshot.child("verified").getValue();
                            if (status_user.equals(false)) { // belum di verifikaasi
                                PengurusData list = npsnapshot.getValue(PengurusData.class);
                                pengurusData.add(list);
                                keyItem.add(npsnapshot.getKey());
                            }
                        }
                    }

                    if (pengurusData.size() > 0) {
                        DaftarAkunPengurusAdapter daftarAkunPengurusAdapter = new DaftarAkunPengurusAdapter(getContext(), pengurusData, keyItem);
                        rv_VerifikasiAkun.setLayoutManager(new LinearLayoutManager(getContext()));
                        rv_VerifikasiAkun.setAdapter(daftarAkunPengurusAdapter);
                    } else {
                        tv_no_data.setVisibility(View.VISIBLE);
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}