 package com.example.hotels;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HotelActivity extends AppCompatActivity {

    private ImageView image;
    private TextView tvName, tvCity, tvPrice, tvCheckInDate, tvCheckOutDate;
    private Spinner spinner;
    private Button btnPay, btnBack;
    private DatabaseHelper dbHelper;
    private Integer selectedRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_hotel);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        image = findViewById(R.id.image);
        tvCheckInDate = findViewById(R.id.checkInDate);
        tvCheckOutDate = findViewById(R.id.checkOutDate);
        tvName = findViewById(R.id.name);
        tvCity = findViewById(R.id.city);
        tvPrice = findViewById(R.id.price);
        spinner = findViewById(R.id.spinner);
        btnPay = findViewById(R.id.btn_pay);
        btnBack = findViewById(R.id.btn_back);

        btnPay.setOnClickListener(view -> pay());
        btnBack.setOnClickListener(view -> finish());

        dbHelper = new DatabaseHelper(this);
        getHotelData();
    }

    private void getHotelData() {
        SharedPreferences sharedPreferences = getSharedPreferences("hotel", Context.MODE_PRIVATE);
        String checkInDate = sharedPreferences.getString("checkInDate", "");
        String checkOutDate = sharedPreferences.getString("checkOutDate", "");
        Integer hotelId = sharedPreferences.getInt("id", 0);

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("hotels", null, "id = ?",
                new String[]{hotelId.toString()}, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String address = cursor.getString(cursor.getColumnIndex("city"));
            String image_url = cursor.getString(cursor.getColumnIndex("image_url"));
            int price = cursor.getInt(cursor.getColumnIndex("price"));
            onScreenData(image_url, checkInDate, checkOutDate, name, address, price);
        }
        cursor.close();
        db.close();
    }

    private void onScreenData(String image_url, String checkInDate, String checkOutDate, String name, String address, Integer price) {
        tvCheckInDate.setText("Дата заезда " + checkInDate);
        tvCheckOutDate.setText("Дата выезда " + checkOutDate);
        tvName.setText("Отель: " + name);
        tvCity.setText("Город: " + address);
        tvPrice.setText("Цена за ночь: " + price + "₽");
        configureSpinner();
        Picasso.get().load(image_url).into(image); // исправлено
    }

    private void configureSpinner() {
        ArrayList<ArrayList<Integer>> idAndNumber = getRoomsFromBase();
        if (idAndNumber.get(0).get(0) != null) {
            selectedRoom = idAndNumber.get(0).get(0);
        }
        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this, R.layout.simple_spinner_item, idAndNumber.get(1));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedRoom = idAndNumber.get(0).get(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        spinner.setOnItemSelectedListener(itemSelectedListener);
    }

    private ArrayList<ArrayList<Integer>> getRoomsFromBase() {
        ArrayList<ArrayList<Integer>> idAndNumber = new ArrayList<ArrayList<Integer>>();
        ArrayList<Integer> rooms = new ArrayList<Integer>();
        ArrayList<Integer> reservations_id = new ArrayList<Integer>();
        SharedPreferences sharedPreferences = getSharedPreferences("hotel", Context.MODE_PRIVATE);
        String checkInDate = sharedPreferences.getString("checkInDate", "");
        String checkOutDate = sharedPreferences.getString("checkOutDate", "");

        Integer hotelId = sharedPreferences.getInt("id", 0);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT rooms.id, rooms.number FROM rooms " +
                "WHERE rooms.hotel_id = ? AND rooms.id NOT IN (SELECT room_id FROM reservations " +
                "WHERE NOT (? >= reservations.check_out_date OR ? <= reservations.check_in_date))",
                new String[]{String.valueOf(hotelId), checkInDate, checkOutDate
        });

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                int room = cursor.getInt(cursor.getColumnIndex("number"));
                reservations_id.add(id);
                rooms.add(room);
            }
            cursor.close();
        }
        db.close();
        idAndNumber.add(reservations_id);
        idAndNumber.add(rooms);
        return idAndNumber;
    }

    private void pay() {
        Intent intent = new Intent(this, BookingActivity.class);
        if (selectedRoom != null) {
            SharedPreferences sharedPreferences = getSharedPreferences("hotel", Context.MODE_PRIVATE);
            String checkInDate = sharedPreferences.getString("checkInDate", "");
            String checkOutDate = sharedPreferences.getString("checkOutDate", "");
            Integer userId = getSharedPreferences("user", MODE_PRIVATE).getInt("id", 0);

            insertReservation(checkInDate, checkOutDate, userId);

            startActivity(intent);
            finish();
            Toast.makeText(this, "Оплата (которая не существует) прошла успешно!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Выберите номер ", Toast.LENGTH_SHORT).show();
        }
    }

    private void insertReservation(String checkInDate, String checkOutDate, int userId) {
        ContentValues cv = new ContentValues();
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        cv.put("check_in_date", checkInDate);
        cv.put("check_out_date", checkOutDate);
        cv.put("user_id", userId);
        cv.put("room_id", selectedRoom);
        db.insert("reservations", null, cv);
    }
}