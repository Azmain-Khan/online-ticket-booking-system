package gui;

import data.DataManager;
import data.Session;
import model.Booking;
import model.Event;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class BookingsPanel extends JPanel {
    private MainFrame         frame;
    private JTable            table;
    private DefaultTableModel tableModel;
    private JLabel            summaryLabel;

    private static final String[] COLS = {"Booking ID", "Event", "Tickets", "Total ($)", "Date", "Status"};

    public BookingsPanel(MainFrame frame) {
        this.frame = frame;
        setBackground(Theme.BG_DARK);
        setLayout(new BorderLayout());
    }

    private void buildUI() {
        removeAll();
        add(new NavBar(frame, "My Bookings"), BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(0, 16));
        content.setBackground(Theme.BG_DARK);
        content.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JLabel title = Theme.makeLabel("My Bookings", Theme.FONT_HEADER, Theme.TEXT_PRIMARY);
        content.add(title, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(COLS, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        styleTable(table);
        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(Theme.BG_DARK);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));

        // Summary + actions
        summaryLabel = Theme.makeLabel("", Theme.FONT_SMALL, Theme.TEXT_MUTED);
        JButton cancelBtn = Theme.makeButton("❌  Cancel Booking", false);
        cancelBtn.setForeground(Theme.DANGER);
        cancelBtn.addActionListener(e -> cancelSelected());

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.add(summaryLabel, BorderLayout.WEST);
        bottom.add(cancelBtn,   BorderLayout.EAST);

        content.add(scroll,  BorderLayout.CENTER);
        content.add(bottom,  BorderLayout.SOUTH);
        add(content, BorderLayout.CENTER);

        loadBookings();
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
        t.getTableHeader().setBackground(Theme.BG_PANEL);
        t.getTableHeader().setForeground(Theme.ACCENT);
        t.getTableHeader().setFont(Theme.FONT_BODY);

        int[] widths = {140, 220, 70, 90, 160, 100};
        for (int i = 0; i < widths.length; i++) {
            t.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        // Status column colour
        t.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable tbl, Object val,
                    boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(tbl, val, sel, foc, r, c);
                setHorizontalAlignment(CENTER);
                if (!sel) {
                    setForeground("CONFIRMED".equals(val) ? Theme.SUCCESS : Theme.DANGER);
                }
                return this;
            }
        });
    }

    private void loadBookings() {
        tableModel.setRowCount(0);
        List<Booking> bookings = DataManager.getUserBookings(Session.getCurrentUser().getUsername());
        double total = 0;
        int confirmed = 0;
        for (Booking b : bookings) {
            tableModel.addRow(new Object[]{
                b.getBookingId(), b.getEventName(), b.getNumTickets(),
                String.format("%.2f", b.getTotalAmount()), b.getBookingDate(), b.getStatus()
            });
            if ("CONFIRMED".equals(b.getStatus())) { total += b.getTotalAmount(); confirmed++; }
        }
        summaryLabel.setText(String.format("  %d confirmed booking(s) · Total spent: $%.2f", confirmed, total));
    }

    private void cancelSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a booking to cancel.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String status    = tableModel.getValueAt(row, 5).toString();
        String bookingId = tableModel.getValueAt(row, 0).toString();
        if ("CANCELLED".equals(status)) {
            JOptionPane.showMessageDialog(this, "This booking is already cancelled.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Cancel booking " + bookingId + "?", "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        List<Booking> bookings = DataManager.loadBookings();
        for (Booking b : bookings) {
            if (b.getBookingId().equals(bookingId)) {
                b.cancel();
                // Restore seat
                Event event = DataManager.findEvent(b.getEventId());
                if (event != null) { event.cancelSeat(); DataManager.updateEvent(event); }
                DataManager.saveBookings(bookings);
                break;
            }
        }
        JOptionPane.showMessageDialog(this, "Booking cancelled successfully.");
        loadBookings();
    }

    public void refresh() { buildUI(); }
}
