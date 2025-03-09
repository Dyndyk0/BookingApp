package com.example.hotels;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Hotel> hotels;
    private Spinner spinner;
    private DatabaseHelper dbHelper;
    private TextView textViewCheckInDate, textViewCheckOutDate;
    private Button btnSearch;
    private RecyclerView recyclerView;
    private String selectedCity = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView.setSelectedItemId(R.id.navigation_hotels);
        bottomNavigationView.setItemIconTintList(null);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_booked:
                    startActivity(new Intent(getApplicationContext(), BookingActivity.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                case R.id.navigation_hotels:
                    return true;
                case R.id.navigation_profile:
                    startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
            }
            return false;
        });

        textViewCheckInDate = findViewById(R.id.textViewCheckInDate);
        textViewCheckOutDate = findViewById(R.id.textViewCheckOutDate);
        btnSearch = findViewById(R.id.btn_search);
        recyclerView = findViewById(R.id.lv_hotels);
        spinner = findViewById(R.id.spinner);

        textViewCheckInDate.setOnClickListener(v -> showCheckInDatePicker());
        textViewCheckOutDate.setOnClickListener(v -> showCheckOutDatePicker());
        btnSearch.setOnClickListener(view -> searchHotels());

        hotels = new ArrayList<Hotel>();
        dbHelper = new DatabaseHelper(this);

        //SQLiteDatabase db = dbHelper.getWritableDatabase();
        //db.delete("hotels","id = ?", new String[]{"12"});
        //insertHotel();
        configureSpinner();
    }

    private void searchHotels() {
        if (!textViewCheckInDate.getText().toString().isEmpty()) {
            hotels.clear();
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.query("hotels", null, "city = ?",
                    new String[]{selectedCity}, null, null, null);

            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor.getColumnIndex("id"));
                    String name = cursor.getString(cursor.getColumnIndex("name"));
                    String address = cursor.getString(cursor.getColumnIndex("city"));
                    String image_url = cursor.getString(cursor.getColumnIndex("image_url"));
                    int price = cursor.getInt(cursor.getColumnIndex("price"));

                    hotels.add(new Hotel(id, name, address, price, image_url));
                    insertRoom(id);
                }
                cursor.close();
            }
            db.close();
            HotelAdapter adapter = new HotelAdapter(this, hotels,
                    textViewCheckInDate.getText().toString() , textViewCheckOutDate.getText().toString());
            recyclerView.setAdapter(adapter);
        } else {
            Toast.makeText(this, "Введите искомую дату", Toast.LENGTH_SHORT).show();
        }
    }

    private void showCheckInDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, month1, dayOfMonth) -> {
                    String selectedDate = year1 + "-" + (month1 + 1) + "-" + dayOfMonth;
                    textViewCheckInDate.setText(selectedDate);
                },
                year, month, day);
        datePickerDialog.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());
        datePickerDialog.show();
    }

    private void showCheckOutDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        if (textViewCheckInDate.getText().toString().isEmpty()) {
            Toast.makeText(this, "Выберите дату заезда", Toast.LENGTH_SHORT).show();
            return;
        }
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, month1, dayOfMonth) -> {
                    String selectedDate = year1 + "-" + (month1 + 1) + "-" + dayOfMonth;
                    textViewCheckOutDate.setText(selectedDate);
                },
                year, month, day);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        try {
            calendar.setTime(sdf.parse(textViewCheckInDate.getText().toString()));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis() + 100000000);
        datePickerDialog.show();
    }

    private ArrayList<String> getCityFromBase() {
        ArrayList<String> cities = new ArrayList<String>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT DISTINCT city FROM hotels", null);

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String city = cursor.getString(cursor.getColumnIndex("city"));
                cities.add(city);
            }
            cursor.close();
        }
        db.close();
        return cities;
    }

    private void configureSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_item, getCityFromBase());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCity = (String)parent.getItemAtPosition(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        spinner.setOnItemSelectedListener(itemSelectedListener);
    }

    private void insertHotel() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", "Парижский отель");
        cv.put("city", "Париж");
        cv.put("price", 21000);
        cv.put("image_url", "https://avatars.mds.yandex.net/get-altay/1547687/2a0000016b38ce1730291bff516a3bd158c8/XXXL");
        db.insert("hotels", null, cv);
        db.close();
    }

    private void insertRoom(int hotelId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("hotel_id", hotelId);
        cv.put("number", 3);
        //db.insert("rooms", null, cv);
        db.close();
    }
}