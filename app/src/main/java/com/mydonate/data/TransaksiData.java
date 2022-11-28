package com.mydonate.data;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.midtrans.sdk.corekit.core.TransactionRequest;
import com.midtrans.sdk.corekit.models.BankType;
import com.midtrans.sdk.corekit.models.CustomerDetails;
import com.midtrans.sdk.corekit.models.ItemDetails;
import com.midtrans.sdk.corekit.models.snap.CreditCard;

import java.util.ArrayList;

public class TransaksiData {
    private static String Unama, Uphone, Uemail;
    public static CustomerDetails customerDetails(){

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String idUser = currentUser.getUid();
        DatabaseReference drf = FirebaseDatabase.getInstance().getReference().child("Users").child(idUser);
        drf.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Unama = snapshot.child("nama").getValue(String.class);
                Uemail = snapshot.child("email").getValue(String.class);
                Uphone = snapshot.child("phone").getValue(String.class);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        CustomerDetails cd = new CustomerDetails();

        cd.setFirstName(Unama);
        cd.setPhone(Uphone);
        cd.setEmail(Uemail);

        return cd;
    }


    public static TransactionRequest transactionRequest(String id, Double dana, int qty, String nama, double donasi){

        TransactionRequest tr = new TransactionRequest(System.currentTimeMillis()+"",donasi);
        tr.setCustomerDetails(customerDetails());
        ItemDetails details = new ItemDetails(id, dana, qty, nama);

        ArrayList<ItemDetails> itemDetails = new ArrayList<>();
        itemDetails.add(details);
        tr.setItemDetails(itemDetails);

//        CreditCard creditCardOptions = new CreditCard();

//        creditCardOptions.setBank(BankType.BRI);
//        creditCardOptions.setBank(BankType.MANDIRI);
//        creditCardOptions.setBank(BankType.PERMATA);
//        creditCardOptions.setBank(BankType.MAYBANK);
//        creditCardOptions.setBank(BankType.MEGA);
//        creditCardOptions.setBank(BankType.CIMB);
//// Set MIGS channel (ONLY for BCA, BRI and Maybank Acquiring bank)
//        creditCardOptions.setChannel(CreditCard.MIGS);

        // opsi kartu kredit
//        CreditCard creditCard = new CreditCard();
//        creditCard.setSaveCard(false);
//        creditCard.setAuthentication(CreditCard.RBA);
//        creditCard.setBank(BankType.BRI);
//        tr.setCreditCard(creditCard);

        return tr;
    }
}
