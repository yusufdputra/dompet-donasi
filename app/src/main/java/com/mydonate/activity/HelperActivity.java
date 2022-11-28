package com.mydonate.activity;

import android.net.Uri;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mydonate.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class HelperActivity extends AppCompatActivity {

  public static void setImageView(String Ufoto, ImageView iv_target, ShimmerFrameLayout shimmerImageView) {
    //set image to layout
    StorageReference ref = FirebaseStorage.getInstance().getReference();
    StorageReference dateRef = ref.child("Program Umum/" + Ufoto);
    dateRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
      @Override
      public void onSuccess(Uri uri) {
        Picasso.get().load(uri).fetch(new Callback() {
          @Override
          public void onSuccess() {
            if (shimmerImageView != null) {
              shimmerImageView.stopShimmer();
              shimmerImageView.setVisibility(View.GONE);
            }
            iv_target.setVisibility(View.VISIBLE);
            iv_target.setAlpha(0f);
            Picasso.get()
                    .load(uri)
                    .placeholder(R.drawable.background_splash)
                    .into(iv_target);
            iv_target.animate().setDuration(300).alpha(1f).start();

          }

          @Override
          public void onError(Exception e) {
            if (shimmerImageView != null) {
              shimmerImageView.stopShimmer();
              shimmerImageView.setVisibility(View.GONE);
            }
            Toast.makeText(iv_target.getContext(), "Gagal mengambil gambar", Toast.LENGTH_SHORT).show();
            iv_target.setVisibility(View.VISIBLE);
          }
        });

      }
    });
  }


  public static boolean validasiNominal(EditText tv_donasi) {
    String nominal_kebutuhan = tv_donasi.getText().toString().trim();
    if (nominal_kebutuhan.isEmpty()) {
      tv_donasi.setError("Field tidak boleh kosong");
      return false;
    } else {
      tv_donasi.setError(null);
      return true;
    }
  }

  public static boolean validasiInput(TextInputLayout textInputLayout) {
    String input = textInputLayout.getEditText().getText().toString().trim();
    if (input.isEmpty()) {
      textInputLayout.setError("Field tidak boleh kosong");
      return false;
    } else {
      textInputLayout.setError(null);
      return true;
    }
  }

}
