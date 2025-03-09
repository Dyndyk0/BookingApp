package com.example.hotels;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import android.view.View;
import android.widget.*;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvLogin, tvEmail;
    private EditText etEmail, etPassword;
    private Button btnLogin, btnRegister, btnExit;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView.setSelectedItemId(R.id.navigation_profile);
        bottomNavigationView.setOnApplyWindowInsetsListener(null);
        bottomNavigationView.setItemIconTintList(null);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_booked:
                    startActivity(new Intent(getApplicationContext(), BookingActivity.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                case R.id.navigation_hotels:
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                case R.id.navigation_profile:
                    return true;
            }
            return false;
        });

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);
        tvLogin = findViewById(R.id.tv_login);
        tvEmail = findViewById(R.id.tv_email);
        btnExit = findViewById(R.id.btn_exit);

        btnLogin.setOnClickListener(view -> login());
        btnRegister.setOnClickListener(view -> register());
        btnExit.setOnClickListener(view -> exit());

        dbHelper = new DatabaseHelper(this);

        if (isUserAuthorized()) {
            tvLogin.setText(getUserLogin());
            tvEmail.setText(getUserEmail());
            setVisibilityLoginForm(false);
            setVisibilityAccountInfo(true);
        } else {
            setVisibilityLoginForm(true);
            setVisibilityAccountInfo(false);
        }
    }

    private void login() {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query("users", null, "email = ?",
                new String[]{email}, null, null, null);

        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            String passwordFromDb = cursor.getString(cursor.getColumnIndex("password"));
            if (password.equals(passwordFromDb)) {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String login = cursor.getString(cursor.getColumnIndex("login"));

                saveUserDate(id, login, email);
                tvLogin.setText(login);
                tvEmail.setText(email);
                etEmail.setText("");
                etPassword.setText("");
                setVisibilityLoginForm(false);
                setVisibilityAccountInfo(true);
            } else {
                Toast.makeText(this, "Неверный логин или пароль", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Неверный логин или пароль", Toast.LENGTH_SHORT).show();
        }
        cursor.close();
        db.close();
    }

    private void register() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean isUserAuthorized() {
        if (getUserId() != 0) {
            return true;
        } else {
            return false;
        }
    }

    private void saveUserDate(int id, String login, String email) {
        SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("id", id);
        editor.putString("login", login);
        editor.putString("email", email);
        editor.apply();
    }

    private int getUserId() {
        return getSharedPreferences("user", MODE_PRIVATE).getInt("id", 0);
    }
    private String getUserLogin() {
        return getSharedPreferences("user", MODE_PRIVATE).getString("login", "");
    }
    private String getUserEmail() {
        return getSharedPreferences("user", MODE_PRIVATE).getString("email", "");
    }

    private void setVisibilityLoginForm(boolean isVisibility) {
        if (isVisibility) {
            etEmail.setVisibility(View.VISIBLE);
            etPassword.setVisibility(View.VISIBLE);
            btnLogin.setVisibility(View.VISIBLE);
            btnRegister.setVisibility(View.VISIBLE);
        } else  {
            etEmail.setVisibility(View.GONE);
            etPassword.setVisibility(View.GONE);
            btnLogin.setVisibility(View.GONE);
            btnRegister.setVisibility(View.GONE);
        }
    }

    private void setVisibilityAccountInfo(boolean isVisibility) {
        if (isVisibility) {
            tvEmail.setVisibility(View.VISIBLE);
            tvLogin.setVisibility(View.VISIBLE);
            btnExit.setVisibility(View.VISIBLE);
        } else  {
            tvEmail.setVisibility(View.GONE);
            tvLogin.setVisibility(View.GONE);
            btnExit.setVisibility(View.GONE);
        }
    }

    private void exit() {
        saveUserDate(0, "", "");
        setVisibilityLoginForm(true);
        setVisibilityAccountInfo(false);
    }
}