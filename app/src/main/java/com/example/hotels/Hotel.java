package com.example.hotels;

public class Hotel {
    private Integer id;
    private String name;
    private String address;
    private Integer price;
    private String imageUrl;

    public Hotel(Integer id,String name, String address, Integer price, String imageUrl) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public Integer getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
