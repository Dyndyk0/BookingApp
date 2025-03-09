package com.example.hotels;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RegisterActivity extends AppCompatActivity {

    private EditText etLogin, etEmail, etPassword;
    private Button btnBack, btnRegister;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etLogin = findViewById(R.id.et_login);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnBack = findViewById(R.id.btn_back);
        btnRegister = findViewById(R.id.btn_register);

        btnBack.setOnClickListener(view -> back());
        btnRegister.setOnClickListener(view -> register());

        dbHelper = new DatabaseHelper(this);
    }

    private void back() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
        finish();
    }

    private void register() {
        String login = etLogin.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query("users", null, "email=?", new String[]{email},
                null, null, null);

        if (cursor.getCount() == 0) {
            insertUser(dbHelper, login, email, password);
            back();
        }
        else {
            Toast.makeText(this, "Данный email уже зарегистрирован", Toast.LENGTH_SHORT).show();
        }
    }

    private void insertUser(DatabaseHelper dbHelper, String Login, String Email, String Password) {
        ContentValues cv = new ContentValues();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        cv.put("login", Login);
        cv.put("email", Email);
        cv.put("password", Password);
        db.insert("users", null, cv);
    }
}