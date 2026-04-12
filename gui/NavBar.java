package gui;

import data.Session;
import javax.swing.*;
import java.awt.*;

public class NavBar extends JPanel {

    public NavBar(MainFrame frame, String activeTab) {
        setBackground(Theme.BG_PANEL);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Theme.ACCENT));
        setPreferredSize(new Dimension(0, 56));

        // Left – logo
        JLabel logo = new JLabel("  TicketStore");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        logo.setForeground(Theme.ACCENT);
        add(logo, BorderLayout.WEST);

        // Center – nav links
        JPanel nav = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 10));
        nav.setOpaque(false);
        addNavBtn(nav, "Browse Events", MainFrame.CARD_EVENTS, activeTab, frame);
        addNavBtn(nav, "My Bookings",   MainFrame.CARD_BOOKINGS, activeTab, frame);
        if (Session.isAdmin()) {
            addNavBtn(nav, "Admin Panel", MainFrame.CARD_ADMIN, activeTab, frame);
        }
        add(nav, BorderLayout.CENTER);

        // Right – user info + logout
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        right.setOpaque(false);
        JLabel user = new JLabel(Session.getCurrentUser().getFullName());
        user.setFont(Theme.FONT_SMALL);
        user.setForeground(Theme.TEXT_MUTED);
        right.add(user);

        JButton logout = Theme.makeButton("Logout", false);
        logout.setPreferredSize(new Dimension(80, 30));
        logout.addActionListener(e -> frame.logout());
        right.add(logout);
        add(right, BorderLayout.EAST);
    }

    private void addNavBtn(JPanel nav, String label, String card, String active, MainFrame frame) {
        JButton btn = new JButton(label);
        btn.setFont(Theme.FONT_BODY);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        if (label.equals(active)) {
            btn.setForeground(Theme.ACCENT);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        } else {
            btn.setForeground(Theme.TEXT_MUTED);
            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent e) { btn.setForeground(Theme.TEXT_PRIMARY); }
                public void mouseExited(java.awt.event.MouseEvent e)  { btn.setForeground(Theme.TEXT_MUTED);   }
            });
        }
        btn.addActionListener(e -> frame.showCard(card));
        nav.add(btn);
    }
}
