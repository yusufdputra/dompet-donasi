package com.mydonate.adapter;

import static android.content.ContentValues.TAG;
import static com.mydonate.activity.HelperActivity.validasiNominal;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jpvs0101.currencyfy.Currencyfy;
import com.mydonate.R;
import com.mydonate.activity.EditKebutuhanActivity;
import com.mydonate.component.NumberTextWatcher;
import com.mydonate.data.BayarKebutuhanData;
import com.mydonate.data.KebutuhanData;
import com.mydonate.fragment.DetailDonasiMasjidMusholaFragment;
import com.mydonate.fragment.DetailMasjidMusholaFragment;
import com.mydonate.fragment.LoginFragment;
import com.mydonate.fragment.UploadBuktiPembayaranFragment;

import java.util.ArrayList;
import java.util.List;

public class DaftarKebutuhanAdapter extends RecyclerView.Adapter {
  public final static String KEY_NAMA_KEBUTUHAN = "NamaKeb";
  public final static String KEY_BIAYA_KEBUTUHAN = "BiayaKeb";
  public final static String KEY_ID_PENGURUS = "IdPengurus";
  public final static String KEY_ID_USER = "IdUser";
  public final static String KEY_DANA_LEBIH = "DanaLebih";
  public final static String KEY_DONASI = "NominalDonasi";
  public final static String KEY_SISA_KEBUTUHAN = "KEY_SISA_KEBUTUHAN";
  public final static String KEY_ID_KEBUTUHAN = "KEY_ID_KEBUTUHAN";
  private final Context mContext;
  private final String edit_kebutuhan_key;
  private final DaftarKebutuhanAdapter adapter;
  private ArrayList<KebutuhanData> kebutuhanData = new ArrayList<>();
  private ArrayList<String> keyItem = new ArrayList<>();
  private ImageView iv_remove_item, iv_foto_view;
  private View popUpDialogView, popUpZoomImage;
  private TextInputLayout textInputLayout_donasi;
  private EditText tv_donasi;
  private Button simpan, cancel;


  public DaftarKebutuhanAdapter(Context context, ArrayList<KebutuhanData> kebutuhanDatanew, ArrayList<String> keyItem, String edit_kebutuhan_key) {
    this.kebutuhanData = kebutuhanDatanew;
    this.keyItem = keyItem;
    this.mContext = context;
    this.edit_kebutuhan_key = edit_kebutuhan_key;
    this.adapter = this;
  }

  public void updateData(final KebutuhanData kebutuhanDataUpdate) {
    kebutuhanData = new ArrayList<>();
    kebutuhanData.add(kebutuhanDataUpdate);


  }


  @NonNull
  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_kebutuhan, parent, false);


    return new ViewHolder(view);
  }



  @Override
  public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
    ViewHolder viewHolder = (ViewHolder) holder;
    holder.setIsRecyclable(false);
    final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
    viewHolder.getItemId();
    if (kebutuhanData != null) {
      KebutuhanData id = kebutuhanData.get(position);
      viewHolder.nama_kebutuhan.setText(id.getNama_kebutuhan());
      viewHolder.tanggal.setText("Ditambahkan " + id.getTanggal());

      if (id.getDetail_kebutuhan().length() != 0) {
        viewHolder.detail_kebutuhan.setVisibility(View.VISIBLE);

        viewHolder.detail_kebutuhan.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(view.getContext());
            View popupLayout = layoutInflater.inflate(R.layout.view_zoom_image_slider, null);
            final ImageSlider photoView = popupLayout.findViewById(R.id.imageViewSlider);
            //set image to layout
            List<SlideModel> slideModels = new ArrayList<>();
            // get
            StorageReference sr = FirebaseStorage.getInstance().getReference().child("Kebutuhan").child(id.getDetail_kebutuhan());
            sr.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
              @Override
              public void onSuccess(Uri uri) {
                slideModels.add(new SlideModel(uri.toString(), ScaleTypes.CENTER_INSIDE));
                photoView.setImageList(slideModels, ScaleTypes.CENTER_INSIDE);
              }
            });

            mBuilder.setView(popupLayout);
            AlertDialog mDialog = mBuilder.create();

            mDialog.show();

            mBuilder.setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialogInterface, int i) {

              }
            });
          }
        });
      }

      viewHolder.foto_kebutuhan.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          AlertDialog.Builder mBuilder = new AlertDialog.Builder(view.getContext());
          View popupLayout = layoutInflater.inflate(R.layout.view_zoom_image_slider, null);
          final ImageSlider photoView = popupLayout.findViewById(R.id.imageViewSlider);
          //set image to layout
          List<SlideModel> slideModels = new ArrayList<>();
          // get
          for (int i = 0; i < id.getFoto_kebutuhan().size(); i++) {
            StorageReference sr = FirebaseStorage.getInstance().getReference().child("Kebutuhan").child(id.getFoto_kebutuhan().get(i));
            sr.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
              @Override
              public void onSuccess(Uri uri) {

                slideModels.add(new SlideModel(uri.toString(), ScaleTypes.CENTER_INSIDE));
                photoView.setImageList(slideModels, ScaleTypes.CENTER_INSIDE);
              }
            });
          }


          mBuilder.setView(popupLayout);
          AlertDialog mDialog = mBuilder.create();

          mDialog.show();

          mBuilder.setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
          });
        }
      });


      // get biaya kebutuhan
      final String[] biaya_kebutuhan_conv = {""};
      DatabaseReference dbRefBK2 = FirebaseDatabase.getInstance().getReference().child("Kebutuhan").child(keyItem.get(position));
      dbRefBK2.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
          if (snapshot.exists()) {
            String biaya_kebutuhan = snapshot.child("biaya_kebutuhan").getValue(String.class);
            biaya_kebutuhan_conv[0] = biaya_kebutuhan.replaceAll("[^a-zA-Z0-9]", "");
            viewHolder.terkumpul.setText("Dari Rp. " + Currencyfy.currencyfy(Double.parseDouble((biaya_kebutuhan_conv[0])), false, false));
          }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
      });

      // get sisa kebutuhan

      DatabaseReference dbRefBK3 = FirebaseDatabase.getInstance().getReference().child("Bayar Kebutuhan").child(keyItem.get(position));
      dbRefBK3.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
          if (snapshot.exists()) {
            String sisa_kebutuhan = snapshot.child("sisa_nominal_kebutuhan").getValue(String.class);
            String sisa_kebutuhan_ = sisa_kebutuhan.replaceAll("[^a-zA-Z0-9]", "");

            // if add harga keb
            String add = snapshot.child("edit_kebutuhan").getValue(String.class);

            if (add != null) {
              viewHolder.iv_add_notif.setVisibility(View.VISIBLE);
              viewHolder.iv_add_notif.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  Snackbar snackbar = Snackbar
                          .make(view, "Dana telah ditambahkan karena harga pasaran berubah. Terimakasih.", Snackbar.LENGTH_LONG);
                  snackbar.show();
                }
              });
            }

            if (!sisa_kebutuhan.equals("0")) {
              viewHolder.biaya_kebutuhan.setText("Rp. " + Currencyfy.currencyfy(Double.parseDouble(sisa_kebutuhan_), false, false));
              viewHolder.donasi.setVisibility(View.VISIBLE);
              viewHolder.selesai.setVisibility(View.GONE);
            } else {

              viewHolder.donasi.setVisibility(View.GONE);
              viewHolder.selesai.setVisibility(View.VISIBLE);

              // cek foto bukti penyerahan
              long foto_bukti = snapshot.child("foto_bukti_donasi").getChildrenCount();
              if (foto_bukti <= 0) {
                viewHolder.title_sisa.setVisibility(View.GONE);
                viewHolder.biaya_kebutuhan.setText("Sudah Terpenuhi");
                viewHolder.biaya_kebutuhan.setTextColor(Color.parseColor("#2900bf"));
              } else {
                viewHolder.title_sisa.setVisibility(View.GONE);
                viewHolder.biaya_kebutuhan.setText("Sudah Diserahkan");
                viewHolder.biaya_kebutuhan.setTextColor(Color.parseColor("#00a81c"));
              }
              viewHolder.selesai.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                  if (foto_bukti <= 0) {
                    Toast.makeText(view.getContext(), "Bukti penyerahan belum di upload.", Toast.LENGTH_LONG).show();
                  } else {
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(view.getContext());
                    View popupLayout = layoutInflater.inflate(R.layout.view_zoom_image_slider, null);
                    final ImageSlider photoView = popupLayout.findViewById(R.id.imageViewSlider);
                    BayarKebutuhanData bayarKebutuhanData;
                    Query listBK = FirebaseDatabase.getInstance().getReference().child("Bayar Kebutuhan").child(keyItem.get(position));
                    listBK.addValueEventListener(new ValueEventListener() {
                      @Override
                      public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                          BayarKebutuhanData bayarKebutuhanData = snapshot.getValue(BayarKebutuhanData.class);
                          //set image to layout
                          List<SlideModel> slideModels = new ArrayList<>();
                          // get
                          for (int i = 0; i < bayarKebutuhanData.getFoto_bukti_donasi().size(); i++) {
                            StorageReference sr = FirebaseStorage.getInstance().getReference().child("Bukti Penyerahan").child(bayarKebutuhanData.getFoto_bukti_donasi().get(i));
                            sr.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                              @Override
                              public void onSuccess(Uri uri) {

                                slideModels.add(new SlideModel(uri.toString(), ScaleTypes.CENTER_INSIDE));
                                photoView.setImageList(slideModels, ScaleTypes.CENTER_INSIDE);
                              }
                            });
                          }
                        }
                      }

                      @Override
                      public void onCancelled(@NonNull DatabaseError error) {

                      }
                    });

                    mBuilder.setView(popupLayout);
                    AlertDialog mDialog = mBuilder.create();

                    mDialog.show();

                    mBuilder.setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialogInterface, int i) {

                      }
                    });
                  }
                }
              });


            }

          }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
      });


      //cek user login
      FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
      String idUser = currentUser.getUid();
      DatabaseReference jLoginDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(idUser);
      jLoginDatabase.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
          String tipe = snapshot.child("tipe_user").getValue(String.class);

          if (tipe.equals(LoginFragment.PENGURUS_LOGIN)) {

            DatabaseReference dbRefBK = FirebaseDatabase.getInstance().getReference().child("Bayar Kebutuhan").child(keyItem.get(position));
            dbRefBK.addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                  String sisa_kebutuhan = snapshot.child("sisa_nominal_kebutuhan").getValue(String.class);


                  if (!sisa_kebutuhan.equals("0")) {
                    viewHolder.donasi.setVisibility(View.GONE);

                    if (edit_kebutuhan_key != null) {
                      viewHolder.edit_kebutuhan.setVisibility(View.VISIBLE);


                      viewHolder.edit_kebutuhan.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                          Integer sisa_kebutuhan_ = Integer.parseInt(sisa_kebutuhan.replaceAll("[^a-zA-Z0-9]", ""));




                          Intent intent = new Intent(mContext, EditKebutuhanActivity.class);
                          intent.putExtra(KEY_SISA_KEBUTUHAN, Integer.toString(sisa_kebutuhan_));
                          intent.putExtra(KEY_BIAYA_KEBUTUHAN, biaya_kebutuhan_conv[0]);
                          intent.putExtra(KEY_ID_KEBUTUHAN, keyItem.get(position));
                          intent.putExtra(KEY_NAMA_KEBUTUHAN, id.getNama_kebutuhan());
                          mContext.startActivity(intent);

                        }
                      });
                    } else {
                      viewHolder.edit_kebutuhan.setVisibility(View.GONE);
                    }

                  } else {
                    viewHolder.edit_kebutuhan.setVisibility(View.GONE);
                  }
                }
              }

              @Override
              public void onCancelled(@NonNull DatabaseError error) {

              }
            });


          } else if (tipe.equals(LoginFragment.DONATUR_LOGIN)) {
            viewHolder.edit_kebutuhan.setVisibility(View.GONE);
            viewHolder.donasi.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
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

                    if (!validasiNominal(tv_donasi)) {
                      return;
                    } else {

                      //get inputan
                      String donasi = tv_donasi.getText().toString();
                      String donasi_final = donasi.replaceAll("[^a-zA-Z0-9]", "");

                      Bundle bundle = new Bundle();
                      bundle.putString(DetailMasjidMusholaFragment.KEY_ID, keyItem.get(position));
                      bundle.putString(KEY_NAMA_KEBUTUHAN, id.getNama_kebutuhan());
                      bundle.putString(KEY_BIAYA_KEBUTUHAN, id.getBiaya_kebutuhan());
                      bundle.putString(KEY_ID_PENGURUS, id.getId_pengurus());
                      bundle.putString(KEY_DONASI, String.valueOf(donasi_final));

                      alertDialog.cancel();
                      DetailDonasiMasjidMusholaFragment fragment = new DetailDonasiMasjidMusholaFragment();
                      fragment.setArguments(bundle);
                      AppCompatActivity activity = (AppCompatActivity) view.getContext();
                      activity.getSupportFragmentManager().beginTransaction().replace(R.id.home_frame_layout, fragment).addToBackStack(null).commit();

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
            });
          } else if (tipe.equals(LoginFragment.ADMIN_LOGIN)) {
            viewHolder.edit_kebutuhan.setVisibility(View.GONE);
            viewHolder.donasi.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                // ambil dana lebih yg tersedia
                Query dbDana = FirebaseDatabase.getInstance().getReference().child("Dana").orderByChild("id_pengurus").equalTo(id.getId_pengurus());
                dbDana.addValueEventListener(new ValueEventListener() {
                  @Override
                  public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {

                      Bundle bundle = new Bundle();
                      bundle.putString(DetailMasjidMusholaFragment.KEY_ID, keyItem.get(position));
                      bundle.putString(KEY_NAMA_KEBUTUHAN, id.getNama_kebutuhan());
                      bundle.putString(KEY_ID_USER, idUser);
                      bundle.putString(KEY_ID_PENGURUS, id.getId_pengurus());
                      for (DataSnapshot npsnapshot : snapshot.getChildren()) {
                        bundle.putDouble(KEY_DANA_LEBIH, npsnapshot.child("total_dana").getValue(Double.class));
                      }

                      Query dbSisa = FirebaseDatabase.getInstance().getReference().child("Bayar Kebutuhan").child(keyItem.get(position));
                      dbSisa.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                          if (snapshot.exists()) {
                            for (DataSnapshot np : snapshot.getChildren()) {
                              String sisa_kebutuhan = snapshot.child("sisa_nominal_kebutuhan").getValue(String.class);
                              String sisa = sisa_kebutuhan.replaceAll("[^a-zA-Z0-9]", "");
//                                                            Bundle bundle = new Bundle();
                              bundle.putString(KEY_BIAYA_KEBUTUHAN, sisa);

                              UploadBuktiPembayaranFragment fragment = new UploadBuktiPembayaranFragment();
                              fragment.setArguments(bundle);
                              AppCompatActivity activity = (AppCompatActivity) view.getContext();
                              activity.getSupportFragmentManager().beginTransaction().replace(R.id.home_frame_layout, fragment).addToBackStack(null).commit();
                            }
                          }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                      });


                    }
                  }

                  @Override
                  public void onCancelled(@NonNull DatabaseError error) {

                  }
                });
              }
            });


          }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
      });
    }

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

  @Override
  public int getItemCount() {
    return kebutuhanData.size();
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {
    private final TextView terkumpul;
    private final TextView nama_kebutuhan;
    private final TextView biaya_kebutuhan;
    private final TextView tanggal;
    private final TextView foto_kebutuhan;
    private final TextView title_sisa;
    private final TextView detail_kebutuhan;
    private final ImageView selesai;
    private final ImageView donasi;
    private final ImageView edit_kebutuhan;
    private final ImageView iv_add_notif;

    public ViewHolder(View itemView) {
      super(itemView);
      nama_kebutuhan = itemView.findViewById(R.id.list_nama_kebutuhan);
      biaya_kebutuhan = itemView.findViewById(R.id.list_biaya_kebutuhan);
      selesai = itemView.findViewById(R.id.iv_selesai);
      edit_kebutuhan = itemView.findViewById(R.id.iv_edit_kebutuhan);
      donasi = itemView.findViewById(R.id.iv_donate_kebutuhan);
      iv_add_notif = itemView.findViewById(R.id.iv_add);
      terkumpul = itemView.findViewById(R.id.terkumpul);
      tanggal = itemView.findViewById(R.id.tanggal);
      foto_kebutuhan = itemView.findViewById(R.id.foto_kebutuhan);
      title_sisa = itemView.findViewById(R.id.tv_sisa);
      detail_kebutuhan = itemView.findViewById(R.id.tv_detail_kebutuhan);

    }
  }

}
