package com.mydonate.fragment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mydonate.R;
import com.mydonate.adapter.DaftarDonasikuAdapter;
import com.mydonate.adapter.DaftarKebutuhanAdapter;
import com.mydonate.adapter.ItemPenyerahanAdapter;
import com.mydonate.data.PenyaluranProgramUmumData;
import com.mydonate.data.TransaksiPembayaranData;

import java.util.ArrayList;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;
import static com.mydonate.fragment.DetailProgramUmumFragment.KEY_ID;

public class PenyerahanPUGridFragment extends Fragment {
    private ImageView ivBack, ivAdd;
    private RecyclerView rv_riwayat_penyerahan;
    private ArrayList<PenyaluranProgramUmumData> penyaluranProgramUmumData;
    private ArrayList<String> keyItem ;
    private TextView titleLayout, tv_no_data1;
    private ShimmerFrameLayout shimmerlayout;
    private String idKebutuhan, idUser;
    ItemPenyerahanAdapter adapter;

    public PenyerahanPUGridFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_penyerahan_pu, container, false);

        if (getArguments() != null) {
            idKebutuhan = getArguments().getString(DetailMasjidMusholaFragment.KEY_ID);
            idUser = getArguments().getString(DaftarKebutuhanAdapter.KEY_ID_USER);
            init(view);
            setOnClickListener();
            getPenyerahanGrid(view);
        }


        return view;
    }

    private void setOnClickListener() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                penyaluran(view);
            }
        });
    }

    private void penyaluran(View v) {

        //cek user login
        Query jLoginDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(idUser);
        jLoginDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String tipe = snapshot.child("tipe_user").getValue(String.class);
                    if (tipe.equals(LoginFragment.ADMIN_LOGIN)) { //  jika admin
                        Bundle bundle = new Bundle();
                        bundle.putString(KEY_ID, idKebutuhan);
                        bundle.putString(DaftarKebutuhanAdapter.KEY_ID_USER, idUser);

                        UploadBuktiProgramUmumFragment fragment = new UploadBuktiProgramUmumFragment();
                        fragment.setArguments(bundle);
                        AppCompatActivity activity = (AppCompatActivity) v.getContext();
                        activity.getSupportFragmentManager().beginTransaction().replace(R.id.home_frame_layout, fragment).addToBackStack(null).commit();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void init(View view) {
        ivBack = view.findViewById(R.id.iv_back);
        ivAdd = view.findViewById(R.id.iv_add);

        rv_riwayat_penyerahan = view.findViewById(R.id.rv_riwayat_penyerahan);
        shimmerlayout = view.findViewById(R.id.shimmerlayout);

        titleLayout = view.findViewById(R.id.tv_title);
        tv_no_data1 = view.findViewById(R.id.tv_no_data1);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        shimmerlayout.startShimmer();

        //cek tipe user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String idUser = currentUser.getUid();
        DatabaseReference jLoginDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(idUser);
        jLoginDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String tipe = snapshot.child("tipe_user").getValue(String.class);
                if (!tipe.equals(LoginFragment.ADMIN_LOGIN)) {
                    ivAdd.setVisibility(view.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        super.onViewCreated(view, savedInstanceState);
    }

    private void getPenyerahanGrid(View view) {
        if (penyaluranProgramUmumData != null){
            penyaluranProgramUmumData.clear();
            keyItem.clear();
        }else{
            penyaluranProgramUmumData = new ArrayList<>();
            keyItem = new ArrayList<>();
        }




        // get list
        Query db = FirebaseDatabase.getInstance().getReference().child("Penyaluran Program Umum").orderByChild("id_program").equalTo(idKebutuhan);
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        PenyaluranProgramUmumData data = dataSnapshot.getValue(PenyaluranProgramUmumData.class);
                        penyaluranProgramUmumData.add(data);
                        keyItem.add(dataSnapshot.getKey());

                    }

                    shimmerlayout.stopShimmer();
                    shimmerlayout.setVisibility(View.GONE);
                    rv_riwayat_penyerahan.setVisibility(View.VISIBLE);

                    rv_riwayat_penyerahan.setLayoutManager(new GridLayoutManager(getContext(), 2));
                    adapter = new ItemPenyerahanAdapter(getContext(), penyaluranProgramUmumData, keyItem);
                    rv_riwayat_penyerahan.setAdapter(adapter);
                }else{
                    shimmerlayout.stopShimmer();
                    shimmerlayout.setVisibility(View.GONE);
                    rv_riwayat_penyerahan.setVisibility(View.GONE);
                    tv_no_data1.setVisibility(View.VISIBLE);
                }





            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}