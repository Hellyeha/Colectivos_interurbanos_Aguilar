package com.example.colectivos_interurbanos_aguilar.activities.driver;

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
import com.example.colectivos_interurbanos_aguilar.activities.client.RegisterActivity;
import com.example.colectivos_interurbanos_aguilar.includes.MyToolbar;
import com.example.colectivos_interurbanos_aguilar.models.Client;
import com.example.colectivos_interurbanos_aguilar.models.Driver;
import com.example.colectivos_interurbanos_aguilar.providers.AuthProvider;
import com.example.colectivos_interurbanos_aguilar.providers.ClientProvider;
import com.example.colectivos_interurbanos_aguilar.providers.DriverProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterDriverActivity extends AppCompatActivity {

    AuthProvider mAuthProvider;
    DriverProvider mDriverProvider;

    Button mButtonRegister;
    TextInputEditText mTextInputEmail;
    TextInputEditText mTextInputName;
    TextInputEditText mTextInputPassword;
    TextInputEditText mTextInputVehicleBrand;
    TextInputEditText mTextInputVehiclePlate;
    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_driver);

        MyToolbar.show(this, "Registro de Chofer", true);
        loadingDialog = new LoadingDialog(RegisterDriverActivity.this);

        mAuthProvider = new AuthProvider();
        mDriverProvider = new DriverProvider();

        mButtonRegister = findViewById(R.id.btnRegister);
        mTextInputName = findViewById(R.id.textInputName);
        mTextInputEmail = findViewById(R.id.textInputEmail);
        mTextInputPassword = findViewById(R.id.textInputPassword);
        mTextInputVehicleBrand = findViewById(R.id.textInputVehicleBrand);
        mTextInputVehiclePlate = findViewById(R.id.textInputVehiclePlate);

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
        String vehicleBrand = mTextInputVehicleBrand.getText().toString();
        String vehiclePlate = mTextInputVehiclePlate.getText().toString();

        if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty() && !vehicleBrand.isEmpty() && !vehiclePlate.isEmpty()) {
            if (password.length() >= 6) {
                loadingDialog.startLoadingDialog();
                register(name, email, password, vehicleBrand, vehiclePlate);
            } else {
                Toast.makeText(this, "La Contraseña debe contener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Complete Todos los Campos", Toast.LENGTH_SHORT).show();
        }

    }

    private void register(String name, String email, String password, String vehicleBrand, String vehiclePlate) {
        mAuthProvider.register(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                loadingDialog.dismissDialog();
                if (task.isSuccessful()) {
                    String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    Driver driver = new Driver(id, name, email, vehicleBrand, vehiclePlate);
                    create(driver);
                } else {
                    Toast.makeText(RegisterDriverActivity.this, "No se Pudo Completar el Registro", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    void create(Driver driver) {
        mDriverProvider.create(driver).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    //Toast.makeText(RegisterDriverActivity.this, "Registro Realizado con Exito", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterDriverActivity.this, MapDriverActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                } else {
                    Toast.makeText(RegisterDriverActivity.this, "Hubo un Error en el Registro", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}