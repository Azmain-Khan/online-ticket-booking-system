package gui;

import data.DataManager;
import model.Booking;
import model.Event;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class AdminPanel extends JPanel {
    private MainFrame frame;
    private JTabbedPane tabs;

    // Events tab
    private JTable            eventTable;
    private DefaultTableModel eventModel;

    // Bookings tab
    private JTable            bookingTable;
    private DefaultTableModel bookingModel;
    private JLabel            revenueLabel;

    private static final String[] EVENT_COLS   = {"ID", "Name", "Type", "Location", "Date", "Time", "Price", "Total", "Available"};
    private static final String[] BOOKING_COLS = {"Booking ID", "User", "Event", "Tickets", "Total", "Date", "Status"};

    public AdminPanel(MainFrame frame) {
        this.frame = frame;
        setBackground(Theme.BG_DARK);
        setLayout(new BorderLayout());
    }

    private void buildUI() {
        removeAll();
        add(new NavBar(frame, "Admin Panel"), BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(0, 10));
        content.setBackground(Theme.BG_DARK);
        content.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        JLabel title = Theme.makeLabel("Admin Panel", Theme.FONT_HEADER, Theme.TEXT_PRIMARY);

        tabs = new JTabbedPane();
        tabs.setBackground(Theme.BG_DARK);
        tabs.setForeground(Theme.TEXT_PRIMARY);
        tabs.setFont(Theme.FONT_BODY);

        tabs.addTab("Manage Events",   buildEventsTab());
        tabs.addTab("All Bookings",    buildBookingsTab());
        tabs.addTab("Sales Report",    buildReportTab());
        tabs.setBackground(Theme.BG_DARK);
        tabs.setForeground(Theme.TEXT_MUTED);
        tabs.setFont(Theme.FONT_BODY);
       
        content.add(title, BorderLayout.NORTH);
        content.add(tabs,  BorderLayout.CENTER);
        add(content, BorderLayout.CENTER);

        loadEvents();
        loadBookings();
        revalidate(); repaint();
    }

    // ── Events Tab ────────────────────────────────────────────────────────────

    private JPanel buildEventsTab() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBackground(Theme.BG_DARK);
        p.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        eventModel = new DefaultTableModel(EVENT_COLS, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        eventTable = new JTable(eventModel);
        styleTable(eventTable);
        JScrollPane scroll = new JScrollPane(eventTable);
        scroll.getViewport().setBackground(Theme.BG_DARK);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);

        JButton addBtn    = Theme.makeButton("Add Event",    true);
        JButton editBtn   = Theme.makeButton("Edit Event",   true);
        JButton deleteBtn = Theme.makeButton("Delete Event", true);
        deleteBtn.setForeground(Theme.DANGER);

        addBtn.addActionListener(e    -> showEventDialog(null));
        editBtn.addActionListener(e   -> editSelectedEvent());
        deleteBtn.addActionListener(e -> deleteSelectedEvent());

        actions.add(addBtn); actions.add(editBtn); actions.add(deleteBtn);

        p.add(scroll,  BorderLayout.CENTER);
        p.add(actions, BorderLayout.SOUTH);
        return p;
    }

    private void loadEvents() {
        if (eventModel == null) return;
        eventModel.setRowCount(0);
        for (Event e : DataManager.loadEvents()) {
            eventModel.addRow(new Object[]{
                e.getEventId(), e.getName(), e.getType(), e.getLocation(),
                e.getDate(), e.getTime(),
                String.format("$%.2f", e.getPrice()),
                e.getTotalSeats(), e.getAvailableSeats()
            });
        }
    }

    private void editSelectedEvent() {
        int row = eventTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select an event first."); return; }
        String id = eventModel.getValueAt(row, 0).toString();
        Event event = DataManager.findEvent(id);
        if (event != null) showEventDialog(event);
    }

    private void deleteSelectedEvent() {
        int row = eventTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select an event first."); return; }
        String id   = eventModel.getValueAt(row, 0).toString();
        String name = eventModel.getValueAt(row, 1).toString();
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete event \"" + name + "\"?", "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;
        DataManager.deleteEvent(id);
        loadEvents();
    }

    private void showEventDialog(Event existing) {
        boolean isNew = (existing == null);
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                isNew ? "Add New Event" : "Edit Event", true);
        dlg.setSize(480, 480);
        dlg.setLocationRelativeTo(this);
        dlg.getContentPane().setBackground(Theme.BG_PANEL);

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 8));
        form.setBackground(Theme.BG_PANEL);
        form.setBorder(BorderFactory.createEmptyBorder(20, 24, 12, 24));

        JTextField nameF     = Theme.makeField();
        JComboBox<String> typeC = new JComboBox<>(new String[]{"Movie","Concert","Theater","Sports"});
        typeC.setBackground(Theme.BG_DARK); typeC.setForeground(Theme.TEXT_PRIMARY); typeC.setFont(Theme.FONT_BODY);
        JTextField locationF = Theme.makeField();
        JTextField dateF     = Theme.makeField();
        JTextField timeF     = Theme.makeField();
        JTextField priceF    = Theme.makeField();
        JTextField seatsF    = Theme.makeField();
        JTextField descF     = Theme.makeField();

        if (!isNew) {
            nameF.setText(existing.getName());
            typeC.setSelectedItem(existing.getType());
            locationF.setText(existing.getLocation());
            dateF.setText(existing.getDate());
            timeF.setText(existing.getTime());
            priceF.setText(String.valueOf(existing.getPrice()));
            seatsF.setText(String.valueOf(existing.getTotalSeats()));
            descF.setText(existing.getDescription());
        } else {
            dateF.setText("2026-MM-DD"); timeF.setText("HH:MM");
        }

        addFormRow(form, "Event Name",   nameF);
        addFormRow(form, "Type",         typeC);
        addFormRow(form, "Location",     locationF);
        addFormRow(form, "Date (yyyy-MM-dd)", dateF);
        addFormRow(form, "Time (HH:MM)", timeF);
        addFormRow(form, "Price ($)",    priceF);
        addFormRow(form, "Total Seats",  seatsF);
        addFormRow(form, "Description",  descF);

        JButton save = Theme.makeButton("Save", true);
        JButton cancel = Theme.makeButton("Cancel", false);
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);
        btnPanel.add(cancel); btnPanel.add(save);

        save.addActionListener(e -> {
            try {
                String name     = nameF.getText().trim();
                String type     = (String) typeC.getSelectedItem();
                String location = locationF.getText().trim();
                String date     = dateF.getText().trim();
                String time     = timeF.getText().trim();
                double price    = Double.parseDouble(priceF.getText().trim());
                int seats       = Integer.parseInt(seatsF.getText().trim());
                String desc     = descF.getText().trim();

                if (name.isEmpty() || location.isEmpty()) throw new IllegalArgumentException("Name & location required.");

                if (isNew) {
                    String id = DataManager.generateEventId();
                    DataManager.addEvent(new Event(id, name, type, location, date, time, price, seats, seats, desc));
                } else {
                    existing.setName(name); existing.setType(type); existing.setLocation(location);
                    existing.setDate(date); existing.setTime(time); existing.setPrice(price);
                    existing.setDescription(desc);
                    DataManager.updateEvent(existing);
                }
                loadEvents();
                dlg.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dlg, "Invalid price or seat count.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dlg, ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        cancel.addActionListener(e -> dlg.dispose());

        dlg.setLayout(new BorderLayout());
        dlg.add(form, BorderLayout.CENTER);
        dlg.add(btnPanel, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void addFormRow(JPanel form, String label, JComponent field) {
        form.add(Theme.makeLabel(label, Theme.FONT_SMALL, Theme.TEXT_MUTED));
        form.add(field);
    }

    // ── Bookings Tab ─────────────────────────────────────────────────────────

    private JPanel buildBookingsTab() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBackground(Theme.BG_DARK);
        p.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        bookingModel = new DefaultTableModel(BOOKING_COLS, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        bookingTable = new JTable(bookingModel);
        styleTable(bookingTable);
        JScrollPane scroll = new JScrollPane(bookingTable);
        scroll.getViewport().setBackground(Theme.BG_DARK);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));

        revenueLabel = Theme.makeLabel("", Theme.FONT_SMALL, Theme.TEXT_MUTED);
        p.add(scroll,        BorderLayout.CENTER);
        p.add(revenueLabel,  BorderLayout.SOUTH);
        return p;
    }

    private void loadBookings() {
        if (bookingModel == null) return;
        bookingModel.setRowCount(0);
        List<Booking> list = DataManager.loadBookings();
        double revenue = 0;
        for (Booking b : list) {
            bookingModel.addRow(new Object[]{
                b.getBookingId(), b.getUsername(), b.getEventName(),
                b.getNumTickets(), String.format("$%.2f", b.getTotalAmount()),
                b.getBookingDate(), b.getStatus()
            });
            if ("CONFIRMED".equals(b.getStatus())) revenue += b.getTotalAmount();
        }
        if (revenueLabel != null)
            revenueLabel.setText(String.format("  Total bookings: %d  |  Confirmed revenue: $%.2f", list.size(), revenue));
    }

    // ── Report Tab ────────────────────────────────────────────────────────────

    private JPanel buildReportTab() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Theme.BG_DARK);
        p.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        JTextArea area = new JTextArea();
        area.setFont(Theme.FONT_MONO);
        area.setBackground(Theme.BG_CARD);
        area.setForeground(Theme.SUCCESS);
        area.setEditable(false);
        area.setLineWrap(false);
        area.setText(generateReport());

        JScrollPane scroll = new JScrollPane(area);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));

        JButton refresh = Theme.makeButton("Refresh Report", true);
        refresh.addActionListener(e -> area.setText(generateReport()));

        p.add(scroll,  BorderLayout.CENTER);
        p.add(refresh, BorderLayout.SOUTH);
        return p;
    }

    private String generateReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("════════════════════════════════════════════════════════════════\n");
        sb.append("          TICKETHUB – SALES REPORT\n");
        sb.append("════════════════════════════════════════════════════════════════\n\n");

        List<Event> events    = DataManager.loadEvents();
        List<Booking> bookings = DataManager.loadBookings();

        long totalConfirmed = bookings.stream().filter(b -> "CONFIRMED".equals(b.getStatus())).count();
        long totalCancelled = bookings.stream().filter(b -> "CANCELLED".equals(b.getStatus())).count();
        double totalRevenue = bookings.stream()
                .filter(b -> "CONFIRMED".equals(b.getStatus()))
                .mapToDouble(Booking::getTotalAmount).sum();

        sb.append(String.format("  Total Events     : %d%n", events.size()));
        sb.append(String.format("  Total Bookings   : %d%n", bookings.size()));
        sb.append(String.format("  Confirmed        : %d%n", totalConfirmed));
        sb.append(String.format("  Cancelled        : %d%n", totalCancelled));
        sb.append(String.format("  Total Revenue    : $%.2f%n%n", totalRevenue));

        sb.append("───────────────────────────────────────────────────────────────\n");
        sb.append("  EVENT BREAKDOWN\n");
        sb.append("───────────────────────────────────────────────────────────────\n");
        sb.append(String.format("  %-8s %-24s %6s %8s %10s%n",
                "ID", "Name", "Sold", "Left", "Revenue"));
        sb.append("───────────────────────────────────────────────────────────────\n");

        for (Event e : events) {
            int sold = e.getTotalSeats() - e.getAvailableSeats();
            double rev = bookings.stream()
                    .filter(b -> b.getEventId().equals(e.getEventId()) && "CONFIRMED".equals(b.getStatus()))
                    .mapToDouble(Booking::getTotalAmount).sum();
            sb.append(String.format("  %-8s %-24s %6d %8d %10s%n",
                    e.getEventId(), truncate(e.getName(), 24), sold, e.getAvailableSeats(),
                    String.format("$%.2f", rev)));
        }
        sb.append("\n════════════════════════════════════════════════════════════════\n");
        return sb.toString();
    }

    private String truncate(String s, int max) {
        return s.length() > max ? s.substring(0, max - 2) + ".." : s;
    }

    private void styleTable(JTable t) {
        t.setBackground(Theme.BG_CARD);
        t.setForeground(Theme.TEXT_PRIMARY);
        t.setFont(Theme.FONT_BODY);
        t.setRowHeight(30);
        t.setGridColor(Theme.BORDER);
        t.setSelectionBackground(Theme.ACCENT);
        t.setSelectionForeground(Theme.BG_DARK);
        t.setShowHorizontalLines(true);
        t.setShowVerticalLines(false);
        t.getTableHeader().setBackground(Theme.BG_PANEL);
        t.getTableHeader().setForeground(Theme.ACCENT);
        t.getTableHeader().setFont(Theme.FONT_BODY);
    }

    public void refresh() { buildUI(); }
}
