package com.oceanview.dto;

import java.time.LocalDate;

public class BillDTO {

    private final String reservationNumber;
    private final String guestName;
    private final String roomNumber;
    private final String roomType;
    private final LocalDate checkInDate;
    private final LocalDate checkOutDate;
    private final long numberOfNights;
    private final double pricePerNight;
    private final double totalCost;

    private BillDTO(BillBuilder builder) {
        this.reservationNumber = builder.reservationNumber;
        this.guestName = builder.guestName;
        this.roomNumber = builder.roomNumber;
        this.roomType = builder.roomType;
        this.checkInDate = builder.checkInDate;
        this.checkOutDate = builder.checkOutDate;
        this.numberOfNights = builder.numberOfNights;
        this.pricePerNight = builder.pricePerNight;
        this.totalCost = builder.totalCost;
    }

    public String getReservationNumber() { return reservationNumber; }
    public String getGuestName() { return guestName; }
    public String getRoomNumber() { return roomNumber; }
    public String getRoomType() { return roomType; }
    public LocalDate getCheckInDate() { return checkInDate; }
    public LocalDate getCheckOutDate() { return checkOutDate; }
    public long getNumberOfNights() { return numberOfNights; }
    public double getPricePerNight() { return pricePerNight; }
    public double getTotalCost() { return totalCost; }

    public static class BillBuilder {
        private String reservationNumber;
        private String guestName;
        private String roomNumber;
        private String roomType;
        private LocalDate checkInDate;
        private LocalDate checkOutDate;
        private long numberOfNights;
        private double pricePerNight;
        private double totalCost;

        public BillBuilder setReservationNumber(String reservationNumber) {
            this.reservationNumber = reservationNumber;
            return this;
        }

        public BillBuilder setGuestName(String guestName) {
            this.guestName = guestName;
            return this;
        }

        public BillBuilder setRoomNumber(String roomNumber) {
            this.roomNumber = roomNumber;
            return this;
        }

        public BillBuilder setRoomType(String roomType) {
            this.roomType = roomType;
            return this;
        }

        public BillBuilder setCheckInDate(LocalDate checkInDate) {
            this.checkInDate = checkInDate;
            return this;
        }

        public BillBuilder setCheckOutDate(LocalDate checkOutDate) {
            this.checkOutDate = checkOutDate;
            return this;
        }

        public BillBuilder setNumberOfNights(long numberOfNights) {
            this.numberOfNights = numberOfNights;
            return this;
        }

        public BillBuilder setPricePerNight(double pricePerNight) {
            this.pricePerNight = pricePerNight;
            return this;
        }

        public BillBuilder setTotalCost(double totalCost) {
            this.totalCost = totalCost;
            return this;
        }

        public BillDTO build() {
            return new BillDTO(this);
        }
    }
}