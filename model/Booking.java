package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Booking {
    private String bookingId;
    private String username;
    private String eventId;
    private String eventName;
    private int numTickets;
    private double totalAmount;
    private String bookingDate;
    private String status; // CONFIRMED, CANCELLED

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public Booking(String bookingId, String username, String eventId,
                   String eventName, int numTickets, double totalAmount,
                   String bookingDate, String status) {
        this.bookingId   = bookingId;
        this.username    = username;
        this.eventId     = eventId;
        this.eventName   = eventName;
        this.numTickets  = numTickets;
        this.totalAmount = totalAmount;
        this.bookingDate = bookingDate;
        this.status      = status;
    }

    public static Booking create(String username, Event event, int numTickets) {
        String id   = "BK" + System.currentTimeMillis();
        String date = LocalDateTime.now().format(FMT);
        double amt  = event.getPrice() * numTickets;
        return new Booking(id, username, event.getEventId(), event.getName(),
                           numTickets, amt, date, "CONFIRMED");
    }

    // CSV: bookingId,username,eventId,eventName,numTickets,totalAmount,bookingDate,status
    public String toCSV() {
        return bookingId + "," + username + "," + eventId + "," +
               eventName + "," + numTickets + "," + totalAmount + "," +
               bookingDate + "," + status;
    }

    public static Booking fromCSV(String csv) {
        String[] p = csv.split(",", 8);
        if (p.length < 8) return null;
        try {
            return new Booking(p[0], p[1], p[2], p[3],
                               Integer.parseInt(p[4]),
                               Double.parseDouble(p[5]), p[6], p[7]);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public void cancel() { this.status = "CANCELLED"; }

    public String getBookingId()   { return bookingId; }
    public String getUsername()    { return username; }
    public String getEventId()     { return eventId; }
    public String getEventName()   { return eventName; }
    public int getNumTickets()     { return numTickets; }
    public double getTotalAmount() { return totalAmount; }
    public String getBookingDate() { return bookingDate; }
    public String getStatus()      { return status; }

    @Override
    public String toString() {
        return String.format("Booking %s | %s | %d ticket(s) | $%.2f | %s | %s",
                bookingId, eventName, numTickets, totalAmount, bookingDate, status);
    }
}
