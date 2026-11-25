package com.example.rentlanka;

public class Vehicle {

    public String id;
    public String name;
    public String type;
    public String district;
    public long pricePerDay;
    public boolean withDriver;
    public String description;
    public String ownerId;
    public boolean available;
    public long createdAt;

    // Firestore ට මේ හිස් constructor එක අනිවාර්යයි
    public Vehicle() {
    }
}