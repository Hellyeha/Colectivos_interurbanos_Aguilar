package com.example.colectivos_interurbanos_aguilar.activities.client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.colectivos_interurbanos_aguilar.R;
import com.example.colectivos_interurbanos_aguilar.activities.LoadingDialog;
import com.example.colectivos_interurbanos_aguilar.activities.driver.MapDriverActivity;
import com.example.colectivos_interurbanos_aguilar.activities.driver.RegisterDriverActivity;
import com.example.colectivos_interurbanos_aguilar.includes.MyToolbar;
import com.example.colectivos_interurbanos_aguilar.models.Client;
import com.example.colectivos_interurbanos_aguilar.providers.AuthProvider;
import com.example.colectivos_interurbanos_aguilar.providers.ClientProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    AuthProvider mAuthProvider;
    ClientProvider mClientProvider;

    Button mButtonRegister;
    TextInputEditText mTextInputEmail;
    TextInputEditText mTextInputName;
    TextInputEditText mTextInputPassword;
    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        MyToolbar.show(this, "Registro de Usuario", true);
        loadingDialog = new LoadingDialog(RegisterActivity.this);

        mAuthProvider = new AuthProvider();
        mClientProvider = new ClientProvider();

        mButtonRegister = findViewById(R.id.btnRegister);
        mTextInputName = findViewById(R.id.textInputName);
        mTextInputEmail = findViewById(R.id.textInputEmail);
        mTextInputPassword = findViewById(R.id.textInputPassword);

        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickRegister();
            }
        });

    }

    private void clickRegister() {
        String name = mTextInputName.getText().toString();
        String email = mTextInputEmail.getText().toString();
        String password = mTextInputPassword.getText().toString();

        if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty()){
            if (password.length() >= 6){
                loadingDialog.startLoadingDialog();
                register(name, email, password);
            }
            else {
                Toast.makeText(this, "La Contraseña debe contener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(this, "Complete Todos los Campos", Toast.LENGTH_SHORT).show();
        }

    }

    private void register(String name, String email, String password) {
        mAuthProvider.register(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                loadingDialog.dismissDialog();
                if (task.isSuccessful()){
                    String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    Client client = new Client(id, name, email);
                    create(client);
                }
                else {
                    Toast.makeText(RegisterActivity.this, "No se Pudo Completar el Registro", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    void create(Client client){
        mClientProvider.create(client).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                   // Toast.makeText(RegisterActivity.this, "Registro Realizado con Exito", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(RegisterActivity.this, MapClientActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                }
                else {
                    Toast.makeText(RegisterActivity.this, "Hubo un Error en el Registro", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    /*

    private void saveUser(String id, String name, String email) {
        String selectedUser = mPref.getString("user", "");
        User user = new User();
        user.setEmail(email);
        user.setName(name);

        if (selectedUser.equals("driver")){

            mDatabase.child("Users").child("Drivers").child(id).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(RegisterActivity.this, "El Registro se Realizo con Exito", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(RegisterActivity.this, "Hubo un Error en el Registro", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } else if (selectedUser.equals("client")) {
            mDatabase.child("Users").child("Clients").child(id).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(RegisterActivity.this, "El Registro se Realizo con Exito", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(RegisterActivity.this, "Hubo un Error en el Registro", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

     */
}