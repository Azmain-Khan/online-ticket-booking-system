package gui;

import data.DataManager;
import data.Session;
import model.Booking;
import model.Event;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class EventsPanel extends JPanel {
    private MainFrame  frame;
    private JTextField searchField;
    private JComboBox<String> typeFilter;
    private JTable     table;
    private DefaultTableModel tableModel;

    private static final String[] COLS = {"ID", "Event Name", "Type", "Location", "Date", "Time", "Price", "Seats"};

    public EventsPanel(MainFrame frame) {
        this.frame = frame;
        setBackground(Theme.BG_DARK);
        setLayout(new BorderLayout());
    }

    private void buildUI() {
        removeAll();

        // ── Nav ──
        add(new NavBar(frame, "Browse Events"), BorderLayout.NORTH);

        // ── Content ──
        JPanel content = new JPanel(new BorderLayout(0, 12));
        content.setBackground(Theme.BG_DARK);
        content.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        // Title
        JLabel title = Theme.makeLabel("Browse Events", Theme.FONT_HEADER, Theme.TEXT_PRIMARY);
        content.add(title, BorderLayout.NORTH);

        // Search bar
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchBar.setOpaque(false);

        searchField = Theme.makeField();
        searchField.setPreferredSize(new Dimension(260, 34));
        searchField.setToolTipText("Search by name or location...");

        typeFilter = new JComboBox<>(new String[]{"All Types", "Movie", "Concert", "Theater", "Sports"});
        typeFilter.setBackground(Theme.BG_CARD);
        typeFilter.setForeground(Theme.TEXT_PRIMARY);
        typeFilter.setFont(Theme.FONT_BODY);
        typeFilter.setPreferredSize(new Dimension(140, 34));

        JButton searchBtn = Theme.makeButton("🔍  Search", true);
        searchBtn.setPreferredSize(new Dimension(110, 34));
        searchBtn.addActionListener(e -> filterTable());

        JButton clearBtn = Theme.makeButton("Clear", false);
        clearBtn.setPreferredSize(new Dimension(70, 34));
        clearBtn.addActionListener(e -> { searchField.setText(""); typeFilter.setSelectedIndex(0); filterTable(); });

        searchBar.add(new JLabel("  "));
        searchBar.add(Theme.makeLabel("Search:", Theme.FONT_SMALL, Theme.TEXT_MUTED));
        searchBar.add(searchField);
        searchBar.add(Theme.makeLabel("Type:", Theme.FONT_SMALL, Theme.TEXT_MUTED));
        searchBar.add(typeFilter);
        searchBar.add(searchBtn);
        searchBar.add(clearBtn);

        // Table
        tableModel = new DefaultTableModel(COLS, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        styleTable(table);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBackground(Theme.BG_DARK);
        scroll.getViewport().setBackground(Theme.BG_DARK);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));

        // Bottom action bar
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);

        JButton bookBtn = Theme.makeButton("🎫  Book Selected Event", true);
        bookBtn.setPreferredSize(new Dimension(200, 38));
        bookBtn.addActionListener(e -> bookSelected());

        JLabel hint = Theme.makeLabel("Select a row, then click Book.", Theme.FONT_SMALL, Theme.TEXT_MUTED);
        actions.add(hint);
        actions.add(bookBtn);

        JPanel center = new JPanel(new BorderLayout(0, 8));
        center.setOpaque(false);
        center.add(searchBar, BorderLayout.NORTH);
        center.add(scroll,    BorderLayout.CENTER);
        center.add(actions,   BorderLayout.SOUTH);

        content.add(center, BorderLayout.CENTER);
        add(content, BorderLayout.CENTER);

        populateTable(DataManager.loadEvents());
        revalidate(); repaint();
    }

    private void styleTable(JTable t) {
        t.setBackground(Theme.BG_CARD);
        t.setForeground(Theme.TEXT_PRIMARY);
        t.setFont(Theme.FONT_BODY);
        t.setRowHeight(32);
        t.setGridColor(Theme.BORDER);
        t.setSelectionBackground(Theme.ACCENT);
        t.setSelectionForeground(Theme.BG_DARK);
        t.setShowHorizontalLines(true);
        t.setShowVerticalLines(false);
        t.setIntercellSpacing(new Dimension(0, 1));
        t.getTableHeader().setBackground(Theme.BG_PANEL);
        t.getTableHeader().setForeground(Theme.ACCENT);
        t.getTableHeader().setFont(Theme.FONT_BODY);
        t.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Theme.BORDER));

        // Column widths
        int[] widths = {55, 200, 90, 170, 90, 65, 70, 60};
        for (int i = 0; i < widths.length; i++) {
            t.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        // Colour "Seats" column
        t.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable tbl, Object val,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(tbl, val, sel, foc, row, col);
                setHorizontalAlignment(CENTER);
                int seats = Integer.parseInt(val.toString());
                if (!sel) setForeground(seats == 0 ? Theme.DANGER : seats < 20 ? Theme.ACCENT : Theme.SUCCESS);
                return this;
            }
        });
    }

    private void populateTable(List<Event> events) {
        tableModel.setRowCount(0);
        for (Event e : events) {
            tableModel.addRow(new Object[]{
                e.getEventId(), e.getName(), e.getType(), e.getLocation(),
                e.getDate(), e.getTime(), String.format("$%.2f", e.getPrice()),
                e.getAvailableSeats()
            });
        }
    }

    private void filterTable() {
        String keyword = searchField.getText().trim().toLowerCase();
        String type    = (String) typeFilter.getSelectedItem();
        List<Event> events = DataManager.loadEvents().stream()
                .filter(e -> (keyword.isEmpty() || e.getName().toLowerCase().contains(keyword)
                                                  || e.getLocation().toLowerCase().contains(keyword)))
                .filter(e -> type.equals("All Types") || e.getType().equals(type))
                .collect(Collectors.toList());
        populateTable(events);
    }

    private void bookSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an event first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String eventId = tableModel.getValueAt(row, 0).toString();
        Event event = DataManager.findEvent(eventId);
        if (event == null) return;

        if (event.getAvailableSeats() == 0) {
            JOptionPane.showMessageDialog(this, "Sorry, this event is fully booked.", "Sold Out", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Booking dialog
        JPanel dlg = new JPanel(new GridLayout(0, 2, 10, 8));
        dlg.setBackground(Theme.BG_PANEL);
        dlg.add(makeInfoLabel("Event:")); dlg.add(makeInfoLabel(event.getName()));
        dlg.add(makeInfoLabel("Date:"));  dlg.add(makeInfoLabel(event.getDate() + " " + event.getTime()));
        dlg.add(makeInfoLabel("Price per ticket:")); dlg.add(makeInfoLabel(String.format("$%.2f", event.getPrice())));
        dlg.add(makeInfoLabel("Available seats:")); dlg.add(makeInfoLabel(String.valueOf(event.getAvailableSeats())));

        SpinnerNumberModel spinModel = new SpinnerNumberModel(1, 1, Math.min(10, event.getAvailableSeats()), 1);
        JSpinner spinner = new JSpinner(spinModel);
        spinner.setFont(Theme.FONT_BODY);
        dlg.add(makeInfoLabel("Number of tickets:"));
        dlg.add(spinner);

        int result = JOptionPane.showConfirmDialog(this, dlg, "Confirm Booking",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;

        int numTickets = (int) spinner.getValue();
        double total   = event.getPrice() * numTickets;

        // Payment simulation
        int pay = JOptionPane.showConfirmDialog(this,
                String.format("<html><b>Total Amount: $%.2f</b><br>Proceed to payment?</html>", total),
                "Payment Confirmation", JOptionPane.YES_NO_OPTION);
        if (pay != JOptionPane.YES_OPTION) return;

        // Commit
        for (int i = 0; i < numTickets; i++) event.bookSeat();
        DataManager.updateEvent(event);
        Booking booking = Booking.create(Session.getCurrentUser().getUsername(), event, numTickets);
        DataManager.addBooking(booking);

        JOptionPane.showMessageDialog(this,
                String.format("<html>✅ Booking confirmed!<br>Booking ID: <b>%s</b><br>Amount paid: <b>$%.2f</b></html>",
                        booking.getBookingId(), total),
                "Booking Successful", JOptionPane.INFORMATION_MESSAGE);
        populateTable(DataManager.loadEvents());
    }

    private JLabel makeInfoLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.FONT_BODY);
        l.setForeground(Theme.TEXT_PRIMARY);
        return l;
    }

    public void refresh() { buildUI(); }
}
