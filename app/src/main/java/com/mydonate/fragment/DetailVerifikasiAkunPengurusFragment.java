package com.mydonate.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mydonate.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import static android.content.ContentValues.TAG;

public class DetailVerifikasiAkunPengurusFragment extends Fragment implements View.OnClickListener {
    public final static String KEY_VERIF_AKUN = "ID_PENGURUS";
    public final static String KEY_REJECT = "Reject";
    public final static Boolean KEY_APPROVE = true;
    private String id_item, UnamaPengurus, Unama_masjid, Ualamat_masjid, Ualamat_pengurus, Uemail, Uphone, Utipe_tempat, Ufoto_tempat, Ufoto_ktp, Ufoto_surat, Ufoto_sim, Ufoto_imb;
    private TextView nama_pengurus, nama_masjid, alamat_masjid, alamat_pengurus, email, phone, title;
    private ImageView foto_ktp, foto_surat, foto_tempat, iv_back, foto_sim, foto_imb;
    private DatabaseReference databaseReference;
    private String firebaseUser;
    private ImageView photoView;
    private Button btn_reject, btn_approve;

    public DetailVerifikasiAkunPengurusFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_verif_pengurus, container, false);
        if (getArguments() != null) {
            id_item = getArguments().getString(KEY_VERIF_AKUN);
            getDataPengurus();
        }
        init(view);
        setOnClickListener();

        return view;
    }

    private void setOnClickListener() {
        iv_back.setOnClickListener(this);
        foto_ktp.setOnClickListener(this);
        foto_surat.setOnClickListener(this);
        btn_approve.setOnClickListener(this);
        btn_reject.setOnClickListener(this);
        foto_sim.setOnClickListener(this);
        foto_imb.setOnClickListener(this);
    }

    private void getDataPengurus() {
        DatabaseReference listDb = FirebaseDatabase.getInstance().getReference().child("Users").child(id_item);
        listDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.v(TAG, "Data Pengurus " + snapshot);

                Unama_masjid = snapshot.child("nama_masjid").getValue(String.class);
                Ualamat_masjid = snapshot.child("alamat_masjid").getValue(String.class);
                UnamaPengurus = snapshot.child("nama_pengurus").getValue(String.class);
                Ualamat_pengurus = snapshot.child("alamat_pengurus").getValue(String.class);
                Uemail = snapshot.child("email").getValue(String.class);
                Uphone = snapshot.child("phone").getValue(String.class);
                Utipe_tempat = snapshot.child("tipe_tempat").getValue(String.class);
                Ufoto_tempat = snapshot.child("foto_tempat").getValue(String.class);
                Ufoto_ktp = snapshot.child("foto_ktp").getValue(String.class);
                Ufoto_surat = snapshot.child("foto_surat_pernyataan").getValue(String.class);
                Ufoto_sim = snapshot.child("foto_sim").getValue(String.class);
                Ufoto_imb = snapshot.child("foto_imb").getValue(String.class);

                //set to layout
                title.append(Utipe_tempat + " " + Unama_masjid);
                nama_masjid.append(Unama_masjid);
                alamat_masjid.append(Ualamat_masjid);
                nama_pengurus.append(UnamaPengurus);
                alamat_pengurus.append(Ualamat_pengurus);
                email.append(Uemail);
                phone.append(Uphone);

                //set foto tempat
                setImageView(Ufoto_tempat, foto_tempat);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setImageView(String Ufoto, ImageView iv_target) {
        //set image to layout
        StorageReference ref = FirebaseStorage.getInstance().getReference();
        StorageReference dateRef = ref.child("Users/" + Ufoto);
        dateRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).fetch(new Callback() {
                    @Override
                    public void onSuccess() {
                        iv_target.setAlpha(0f);
                        Picasso.get()
                                .load(uri)
                                .placeholder(R.drawable.background_splash)
                                .into(iv_target);
                        iv_target.animate().setDuration(300).alpha(1f).start();
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });

            }
        });
    }

    private void init(View view) {
        nama_masjid = view.findViewById(R.id.tv_nama_masjid);
        alamat_masjid = view.findViewById(R.id.tv_alamat_masjid);
        nama_pengurus = view.findViewById(R.id.tv_nama_pengurus);
        alamat_pengurus = view.findViewById(R.id.tv_alamat_pengurus);
        email = view.findViewById(R.id.tv_email);
        phone = view.findViewById(R.id.tv_phone);
        foto_ktp = view.findViewById(R.id.iv_view_ktp);
        foto_surat = view.findViewById(R.id.iv_view_surat);
        foto_tempat = view.findViewById(R.id.imageView);
        foto_sim = view.findViewById(R.id.iv_view_sim);
        foto_imb = view.findViewById(R.id.iv_view_imb);
        title = view.findViewById(R.id.tv_title);
        iv_back = view.findViewById(R.id.iv_back);
        btn_reject = view.findViewById(R.id.btn_reject);
        btn_approve = view.findViewById(R.id.btn_approve);


        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.iv_back:
                getActivity().onBackPressed();
                break;
            case R.id.iv_view_ktp:
                showPupUpImage(Ufoto_ktp);
                break;
            case R.id.iv_view_surat:
                showPupUpImage(Ufoto_surat);
                break;
            case R.id.btn_reject:
                rejectPengurus(id_item);
                break;
            case R.id.btn_approve:
                approvePengurus(id_item);
                break;
            case R.id.iv_view_sim:
                showPupUpImage(Ufoto_sim);
                break;
            case R.id.iv_view_imb:
                showPupUpImage(Ufoto_imb);
                break;
        }
    }

    private void approvePengurus(String id_item) {


        AlertDialog.Builder alerBuilder = new AlertDialog.Builder(getContext());
        alerBuilder.setMessage("Ingin Menyetujui Permohonan?");
        alerBuilder.setPositiveButton("Yes", (dialogInterface, i) -> {
        Boolean succ = false;

        try {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:"));
            intent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{Uemail});
            intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Persetujuan Permohonan");
            intent.putExtra(android.content.Intent.EXTRA_TEXT, "Selamat, akun anda telah disetujui oleh admin myDonation. Silahkan lakukan login menggunakan Email dan kata sandi saat melakukan pendaftaran.");
            startActivity(intent);
            succ = true;

        }catch (Exception e){
            Log.i(TAG, "Error ", e);
            Toast.makeText(getActivity(), "Aplikasi tidak didukung dalam mengirim email", Toast.LENGTH_SHORT).show();
        }

        if (succ == true){
            Toast.makeText(getActivity(), "Pengajuan pengurus berhasil diterima.", Toast.LENGTH_LONG).show();
            databaseReference.child(id_item).child("verified").setValue(KEY_APPROVE);
            getActivity().finish();
        }
        });
        alerBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alerBuilder.show();


    }

    private void rejectPengurus(String id_item) {
        AlertDialog.Builder alerBuilder = new AlertDialog.Builder(getContext());
        alerBuilder.setMessage("Ingin Menolak Permohonan?");
        alerBuilder.setPositiveButton("Yes", (dialogInterface, i) -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:"));
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{Uemail});
            intent.putExtra(Intent.EXTRA_SUBJECT, "Penolakan Permohonan");
            intent.putExtra(Intent.EXTRA_TEXT, "Sangat disayangkan, akun anda tidak disetujui oleh admin myDonation. Silahkan lakukan permohonan kembali.");

            Boolean succ = false;
            try {
                startActivity(Intent.createChooser(intent, "Kirim Email Melalui..."));
                succ = true;
            }catch (Exception e){
                Toast.makeText(getActivity(), "Aplikasi tidak didukung dalam mengirim email", Toast.LENGTH_SHORT).show();
            }
            if (succ == true){
                Toast.makeText(getActivity(), "Pengajuan pengurus berhasil ditolak.", Toast.LENGTH_LONG).show();
                databaseReference.child(id_item).child("verified").setValue(false);
                getActivity().finish();
            }
        });

        alerBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        alerBuilder.show();
    }

    private void showPupUpImage(String jenis_foto) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        View view = getLayoutInflater().inflate(R.layout.view_zoom_image, null);
        photoView = view.findViewById(R.id.iv_foto_view);
        setImageView(jenis_foto, photoView);

        mBuilder.setView(view);
        AlertDialog mDialog = mBuilder.create();

        mDialog.show();

        mBuilder.setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

    }

}
