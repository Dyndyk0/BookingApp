package com.example.hotels;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "hotel_booking.db";
    private static final int DATABASE_VERSION = 6;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE hotels (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, city TEXT, " +
                "price INTEGER, image_url TEXT)");
        db.execSQL("CREATE TABLE rooms (id INTEGER PRIMARY KEY AUTOINCREMENT, hotel_id INTEGER, " +
                "number integer, FOREIGN KEY(hotel_id) REFERENCES hotels(id))");
        db.execSQL("CREATE TABLE users (id INTEGER PRIMARY KEY AUTOINCREMENT, login TEXT, email TEXT UNIQUE, " +
                "password TEXT)");
        db.execSQL("CREATE TABLE reservations (id INTEGER PRIMARY KEY AUTOINCREMENT, check_in_date TEXT, " +
                "check_out_date TEXT, user_id INTEGER, room_id INTEGER, " +
                "FOREIGN KEY(user_id) REFERENCES users(id), " +
                "FOREIGN KEY(room_id) REFERENCES rooms(id))");

        db.execSQL("CREATE INDEX idx_reservations_room_id ON reservations(room_id)");
        db.execSQL("CREATE INDEX idx_reservations_dates ON reservations(check_in_date, check_out_date)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS hotels");
        db.execSQL("DROP TABLE IF EXISTS rooms");
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS reservations");
        onCreate(db);
    }
}