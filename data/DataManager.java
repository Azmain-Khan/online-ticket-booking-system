package data;

import model.Booking;
import model.Event;
import model.User;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class DataManager {

    private static final String DATA_DIR = "data_files";
    private static final String USERS_FILE  = DATA_DIR + "/users.txt";
    private static final String EVENTS_FILE = DATA_DIR + "/events.txt";
    private static final String BOOKINGS_FILE = DATA_DIR + "/bookings.txt";

    // ── Bootstrap ────────────────────────────────────────────────────────────

    public static void initialize() {
        new File(DATA_DIR).mkdirs();
        ensureFile(USERS_FILE);
        ensureFile(EVENTS_FILE);
        ensureFile(BOOKINGS_FILE);
        seedAdminIfNeeded();
        seedEventsIfNeeded();
    }

    private static void ensureFile(String path) {
        File f = new File(path);
        if (!f.exists()) {
            try { f.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        }
    }

    private static void seedAdminIfNeeded() {
        List<User> users = loadUsers();
        boolean hasAdmin = users.stream().anyMatch(User::isAdmin);
        if (!hasAdmin) {
            users.add(new User("admin", "admin123", "admin@tickets.com", "Administrator", true));
            saveUsers(users);
        }
    }

    private static void seedEventsIfNeeded() {
        List<Event> events = loadEvents();
        if (events.isEmpty()) {
            events.add(new Event("EV001", "Avengers Finale",      "Movie",   "Cineplex Downtown",    "2026-05-10", "18:00", 12.50,  200, 200, "Epic Marvel conclusion"));
            events.add(new Event("EV002", "Ed Sheeran Live",      "Concert", "National Stadium",     "2026-05-15", "20:00", 85.00,  5000, 5000, "Mathematics World Tour"));
            events.add(new Event("EV003", "Hamilton Musical",     "Theater", "Grand Theatre",        "2026-05-20", "19:30", 65.00,  400, 400, "Award-winning Broadway hit"));
            events.add(new Event("EV004", "NBA Finals Game 1",   "Sports",  "Arena Center",         "2026-05-22", "21:00", 120.00, 1000, 1000, "Championship showdown"));
            events.add(new Event("EV005", "Taylor Swift Eras",   "Concert", "City Amphitheatre",    "2026-06-01", "19:00", 150.00, 8000, 8000, "The Eras Tour – Extended"));
            events.add(new Event("EV006", "Dune: Part Three",    "Movie",   "IMAX Galaxy",          "2026-06-05", "17:30", 15.00,  300, 300, "The saga continues"));
            events.add(new Event("EV007", "World Chess Championship","Sports","Convention Hall",     "2026-06-10", "10:00", 30.00,  500, 500, "Battle of grandmasters"));
            events.add(new Event("EV008", "Comedy Night Gala",   "Theater", "Laugh Factory",        "2026-06-12", "20:30", 40.00,  250, 250, "Top 10 comedians live"));
            saveEvents(events);
        }
    }

    // ── Users ────────────────────────────────────────────────────────────────

    public static List<User> loadUsers() {
        return readLines(USERS_FILE).stream()
                .map(User::fromCSV)
                .filter(u -> u != null)
                .collect(java.util.stream.Collectors.toList());
    }

    public static void saveUsers(List<User> users) {
        writeLines(USERS_FILE, users.stream().map(User::toCSV).collect(java.util.stream.Collectors.toList()));
    }

    public static User findUser(String username, String password) {
        return loadUsers().stream()
                .filter(u -> u.getUsername().equals(username) && u.getPassword().equals(password))
                .findFirst().orElse(null);
    }

    public static boolean usernameExists(String username) {
        return loadUsers().stream().anyMatch(u -> u.getUsername().equals(username));
    }

    public static void addUser(User user) {
        List<User> users = loadUsers();
        users.add(user);
        saveUsers(users);
    }

    // ── Events ───────────────────────────────────────────────────────────────

    public static List<Event> loadEvents() {
        return readLines(EVENTS_FILE).stream()
                .map(Event::fromCSV)
                .filter(e -> e != null)
                .collect(java.util.stream.Collectors.toList());
    }

    public static void saveEvents(List<Event> events) {
        writeLines(EVENTS_FILE, events.stream().map(Event::toCSV).collect(java.util.stream.Collectors.toList()));
    }

    public static Event findEvent(String eventId) {
        return loadEvents().stream()
                .filter(e -> e.getEventId().equals(eventId))
                .findFirst().orElse(null);
    }

    public static void updateEvent(Event updated) {
        List<Event> events = loadEvents();
        for (int i = 0; i < events.size(); i++) {
            if (events.get(i).getEventId().equals(updated.getEventId())) {
                events.set(i, updated);
                break;
            }
        }
        saveEvents(events);
    }

    public static void addEvent(Event event) {
        List<Event> events = loadEvents();
        events.add(event);
        saveEvents(events);
    }

    public static void deleteEvent(String eventId) {
        List<Event> events = loadEvents();
        events.removeIf(e -> e.getEventId().equals(eventId));
        saveEvents(events);
    }

    public static String generateEventId() {
        List<Event> events = loadEvents();
        int max = events.stream()
                .mapToInt(e -> {
                    try { return Integer.parseInt(e.getEventId().replace("EV", "")); }
                    catch (NumberFormatException ex) { return 0; }
                }).max().orElse(0);
        return String.format("EV%03d", max + 1);
    }

    // ── Bookings ─────────────────────────────────────────────────────────────

    public static List<Booking> loadBookings() {
        return readLines(BOOKINGS_FILE).stream()
                .map(Booking::fromCSV)
                .filter(b -> b != null)
                .collect(java.util.stream.Collectors.toList());
    }

    public static void saveBookings(List<Booking> bookings) {
        writeLines(BOOKINGS_FILE, bookings.stream().map(Booking::toCSV).collect(java.util.stream.Collectors.toList()));
    }

    public static void addBooking(Booking booking) {
        List<Booking> bookings = loadBookings();
        bookings.add(booking);
        saveBookings(bookings);
    }

    public static List<Booking> getUserBookings(String username) {
        return loadBookings().stream()
                .filter(b -> b.getUsername().equals(username))
                .collect(java.util.stream.Collectors.toList());
    }

    public static void updateBooking(Booking updated) {
        List<Booking> bookings = loadBookings();
        for (int i = 0; i < bookings.size(); i++) {
            if (bookings.get(i).getBookingId().equals(updated.getBookingId())) {
                bookings.set(i, updated);
                break;
            }
        }
        saveBookings(bookings);
    }

    // ── File helpers ─────────────────────────────────────────────────────────

    private static List<String> readLines(String path) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.isBlank()) lines.add(line.trim());
            }
        } catch (IOException e) { /* file not ready yet */ }
        return lines;
    }

    private static void writeLines(String path, List<String> lines) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path, false))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) { e.printStackTrace(); }
    }
}
