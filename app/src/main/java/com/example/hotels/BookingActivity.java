package com.example.hotels;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import android.widget.*;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class BookingActivity extends AppCompatActivity {

    private ArrayList<Reservation> reservations;
    private DatabaseHelper dbHelper;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_booking);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView.setSelectedItemId(R.id.navigation_booked);
        bottomNavigationView.setOnApplyWindowInsetsListener(null);
        bottomNavigationView.setItemIconTintList(null);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_booked:
                    return true;
                case R.id.navigation_hotels:
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                case R.id.navigation_profile:
                    startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
            }
            return false;
        });
        reservations = new ArrayList<Reservation>();
        dbHelper = new DatabaseHelper(this);

        recyclerView = findViewById(R.id.lv_hotels);
        loadReservation();
    }

    private void loadReservation() {
        reservations.clear();

        Integer userId = getSharedPreferences("user", MODE_PRIVATE).getInt("id", 0);

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT reservations.id, reservations.check_in_date," +
                "reservations.check_out_date, " +
                "hotels.name, hotels.city, hotels.image_url, rooms.number FROM reservations " +
                "JOIN rooms ON reservations.room_id = rooms.id JOIN hotels ON " +
                "rooms.hotel_id = hotels.id WHERE user_id = ?", new String[]{userId.toString()});

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex("hotels.id"));
                String checkInDate = cursor.getString(cursor.getColumnIndex("check_in_date"));
                String checkOutDate = cursor.getString(cursor.getColumnIndex("check_out_date"));
                String address = cursor.getString(cursor.getColumnIndex("city"));
                String image_url = cursor.getString(cursor.getColumnIndex("image_url"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                int number = cursor.getInt(cursor.getColumnIndex("number"));

                reservations.add(new Reservation(id, checkInDate, checkOutDate, name, address, image_url, number));
            }
            cursor.close();
        }
        db.close();
        BookingAdapter adapter = new BookingAdapter(this, reservations);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadReservation();
    }
}