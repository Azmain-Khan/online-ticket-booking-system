package gui;

import data.Session;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel     mainPanel;

    public static final String CARD_LOGIN    = "LOGIN";
    public static final String CARD_REGISTER = "REGISTER";
    public static final String CARD_EVENTS   = "EVENTS";
    public static final String CARD_BOOKINGS = "BOOKINGS";
    public static final String CARD_ADMIN    = "ADMIN";

    private LoginPanel    loginPanel;
    private RegisterPanel registerPanel;
    private EventsPanel   eventsPanel;
    private BookingsPanel bookingsPanel;
    private AdminPanel    adminPanel;

    public MainFrame() {
        setTitle("TicketStore – Online Ticket Booking System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 720);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        getContentPane().setBackground(Theme.BG_DARK);

        cardLayout = new CardLayout();
        mainPanel  = new JPanel(cardLayout);
        mainPanel.setBackground(Theme.BG_DARK);

        loginPanel    = new LoginPanel(this);
        registerPanel = new RegisterPanel(this);
        eventsPanel   = new EventsPanel(this);
        bookingsPanel = new BookingsPanel(this);
        adminPanel    = new AdminPanel(this);

        mainPanel.add(loginPanel,    CARD_LOGIN);
        mainPanel.add(registerPanel, CARD_REGISTER);
        mainPanel.add(eventsPanel,   CARD_EVENTS);
        mainPanel.add(bookingsPanel, CARD_BOOKINGS);
        mainPanel.add(adminPanel,    CARD_ADMIN);

        add(mainPanel);
        showCard(CARD_LOGIN);
    }

    public void showCard(String card) {
        if (card.equals(CARD_EVENTS))   eventsPanel.refresh();
        if (card.equals(CARD_BOOKINGS)) bookingsPanel.refresh();
        if (card.equals(CARD_ADMIN))    adminPanel.refresh();
        cardLayout.show(mainPanel, card);
    }

    public void logout() {
        Session.logout();
        loginPanel.reset();
        showCard(CARD_LOGIN);
    }
}
