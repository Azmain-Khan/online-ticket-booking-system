package model;

public class Event {
    private String eventId;
    private String name;
    private String type;       // Movie, Concert, Sports, Theater
    private String location;
    private String date;       // yyyy-MM-dd
    private String time;
    private double price;
    private int totalSeats;
    private int availableSeats;
    private String description;

    public Event(String eventId, String name, String type, String location,
                 String date, String time, double price, int totalSeats,
                 int availableSeats, String description) {
        this.eventId = eventId;
        this.name = name;
        this.type = type;
        this.location = location;
        this.date = date;
        this.time = time;
        this.price = price;
        this.totalSeats = totalSeats;
        this.availableSeats = availableSeats;
        this.description = description;
    }

    // CSV: eventId,name,type,location,date,time,price,totalSeats,availableSeats,description
    public String toCSV() {
        return eventId + "," + name + "," + type + "," + location + "," +
               date + "," + time + "," + price + "," + totalSeats + "," +
               availableSeats + "," + description;
    }

    public static Event fromCSV(String csv) {
        String[] parts = csv.split(",", 10);
        if (parts.length < 10) return null;
        try {
            return new Event(parts[0], parts[1], parts[2], parts[3], parts[4],
                             parts[5], Double.parseDouble(parts[6]),
                             Integer.parseInt(parts[7]), Integer.parseInt(parts[8]), parts[9]);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public boolean bookSeat() {
        if (availableSeats > 0) { availableSeats--; return true; }
        return false;
    }

    public void cancelSeat() { if (availableSeats < totalSeats) availableSeats++; }

    public String getEventId()       { return eventId; }
    public String getName()          { return name; }
    public String getType()          { return type; }
    public String getLocation()      { return location; }
    public String getDate()          { return date; }
    public String getTime()          { return time; }
    public double getPrice()         { return price; }
    public int getTotalSeats()       { return totalSeats; }
    public int getAvailableSeats()   { return availableSeats; }
    public String getDescription()   { return description; }

    public void setName(String name)               { this.name = name; }
    public void setType(String type)               { this.type = type; }
    public void setLocation(String location)       { this.location = location; }
    public void setDate(String date)               { this.date = date; }
    public void setTime(String time)               { this.time = time; }
    public void setPrice(double price)             { this.price = price; }
    public void setTotalSeats(int totalSeats)      { this.totalSeats = totalSeats; }
    public void setAvailableSeats(int s)           { this.availableSeats = s; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return String.format("[%s] %s | %s | %s %s | $%.2f | %d seats left",
                eventId, name, type, date, time, price, availableSeats);
    }
}
