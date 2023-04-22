package com.example.colectivos_interurbanos_aguilar.providers;

import static android.app.PendingIntent.getActivity;

import com.example.colectivos_interurbanos_aguilar.models.Token;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class TokenProvider {

    DatabaseReference mDatabase;

    public TokenProvider() {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Tokens");
    }


    public void create(final String idUser){

        if (idUser == null) return;

        // FirebaseMessaging.getInstance().setAutoInitEnabled(true);

        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {

                Token token = new Token(s);
                mDatabase.child(idUser).setValue(token);

            }
        });

    }

    public DatabaseReference getToken(String idUser){
        return mDatabase.child(idUser);
    }


}
