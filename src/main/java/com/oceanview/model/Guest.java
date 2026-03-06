package com.oceanview.model;


public class Guest {
    private int guestId;
    private String name;
    private String address;
    private String contactNumber;
    private String email;

    public Guest() {}

    public Guest(int guestId, String name, String address, String contactNumber) {
        this.guestId = guestId;
        this.name = name;
        this.address = address;
        this.contactNumber = contactNumber;
    }

    public int getGuestId() { return guestId; }
    public void setGuestId(int guestId) { this.guestId = guestId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}