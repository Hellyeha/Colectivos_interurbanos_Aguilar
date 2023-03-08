package com.example.colectivos_interurbanos_aguilar.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.colectivos_interurbanos_aguilar.R;
import com.example.colectivos_interurbanos_aguilar.activities.client.RegisterActivity;
import com.example.colectivos_interurbanos_aguilar.activities.driver.RegisterDriverActivity;

public class SelectOptionAuthActivity extends AppCompatActivity {

    Toolbar mToolbar;
    Button mButtonGoToLogin;
    Button mButtonGoToRegister;
    SharedPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_option_auth);

        mPref = getApplicationContext().getSharedPreferences("typeUser", MODE_PRIVATE);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Seleccione una Opcion");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mButtonGoToLogin = findViewById(R.id.btnGoToLogin);
        mButtonGoToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLogin();
            }
        });

        mButtonGoToRegister = findViewById(R.id.btnGoToRegister);
        mButtonGoToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRegister();
            }
        });

    }

    private void goToLogin() {
        Intent intent = new Intent(SelectOptionAuthActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    private void goToRegister() {
        String typeUser = mPref.getString("user", "");

        if (typeUser.equals("client")) {
            Intent intent = new Intent(SelectOptionAuthActivity.this, RegisterActivity.class);
            startActivity(intent);
        }
        else {
            Intent intent = new Intent(SelectOptionAuthActivity.this, RegisterDriverActivity.class);
            startActivity(intent);
        }
    }
}