package com.example.hotels;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.ViewHolder> {
    private Context context;
    private final LayoutInflater inflater;
    private final List<Reservation> reservations;

    public BookingAdapter(Context context, List<Reservation> reservations) {
        this.reservations = reservations;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public BookingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = inflater.inflate(R.layout.booking_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BookingAdapter.ViewHolder holder, @SuppressLint("RecyclerView")
    int position) {
        Reservation reservation = reservations.get(position);

        holder.name.setText(reservation.getName());
        holder.city.setText(reservation.getAddress());
        Picasso.get().load(reservation.getImageUrl()).into(holder.image); // исправлено
        holder.dateOut.setText("Выезд: " + reservation.getCheckOutDate());
        holder.date.setText("Въезд: " + reservation.getCheckInDate());
        holder.delete.setOnClickListener(v -> deleteReservation(reservation.getId()));
        holder.room.setText("Номер: " + reservation.getRoomNumber().toString());
    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView name, date, city, room, dateOut;
        final ImageView image;
        final Button delete;
        ViewHolder(View view){
            super(view);
            name = view.findViewById(R.id.name);
            city = view.findViewById(R.id.city);
            date = view.findViewById(R.id.date);
            dateOut = view.findViewById(R.id.dateOut);
            room = view.findViewById(R.id.room);
            image = view.findViewById(R.id.image);
            delete = view.findViewById(R.id.delete);
        }
    }

    interface OnHotelClickListener {
        void onHotelClick(Hotel hotel, int position);
    }

    private void deleteReservation(Integer id) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("reservations", "id = ?", new String[]{id.toString()});
        db.close();
        ((BookingActivity)context).onResume();
    }
}