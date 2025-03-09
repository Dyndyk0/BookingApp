package com.example.hotels;

public class Reservation {
    private int id;
    private int number;
    private String checkInDate;
    private String checkOutDate;
    private String imageUrl;
    private String name;
    private String address;

    public Reservation(Integer id, String checkInDate, String checkOutDate, String name, String address, String imageUrl, int number) {
        this.id = id;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.name = name;
        this.address = address;
        this.imageUrl = imageUrl;
        this.number = number;
    }

    public Integer getId() {
        return id;
    }

    public String getCheckInDate() {
        return checkInDate;
    }

    public String getCheckOutDate() {
        return checkOutDate;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Integer getRoomNumber() {
        return number;
    }
}
