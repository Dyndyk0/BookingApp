package com.example.hotels;

import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HotelAdapter extends RecyclerView.Adapter<HotelAdapter.ViewHolder> {
    private Context context;
    private String checkInDate, checkOutDate;
    private final LayoutInflater inflater;
    private final List<Hotel> hotels;

    public HotelAdapter(Context context, List<Hotel> hotels,String checkInDate, String checkOutDate) {
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.hotels = hotels;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public HotelAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = inflater.inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HotelAdapter.ViewHolder holder, @SuppressLint("RecyclerView")
    int position) {
        Hotel hotel = hotels.get(position);
        holder.name.setText(hotel.getName());
        //holder.city.setText(hotel.getAddress());
        Picasso.get().load(hotel.getImageUrl()).into(holder.image); // исправлено
        holder.prise.setText(hotel.getPrice().toString() + "₽");
        holder.book.setOnClickListener(v -> book(hotel.getId()));
    }

    @Override
    public int getItemCount() {
        return hotels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView name, prise;
        final ImageView image;
        final Button book;
        ViewHolder(View view){
            super(view);
            name = view.findViewById(R.id.name);
            //city = view.findViewById(R.id.city);
            prise = view.findViewById(R.id.prise);
            image = view.findViewById(R.id.image);
            book = view.findViewById(R.id.book);
        }
    }

    interface OnHotelClickListener {
        void onHotelClick(Hotel hotel, int position);
    }

    public void book(int hotelId) {
        SharedPreferences preferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        int userId = preferences.getInt("id", 0);
        if (userId != 0) {
            SharedPreferences sharedPreferences = context.getSharedPreferences("hotel", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("id", hotelId);
            editor.putString("checkInDate", checkInDate);
            editor.putString("checkOutDate", checkOutDate);
            editor.apply();
            context.startActivity(new Intent(context, HotelActivity.class));
            //insertReservation(checkInDate, userId, hotelId);
        } else {
            Toast.makeText(context, "Для бронированиня необходима регистрация", Toast.LENGTH_SHORT).show();
        }
    }


}