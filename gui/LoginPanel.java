package gui;

import data.DataManager;
import data.Session;
import model.User;
import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel {
    private MainFrame frame;
    private JTextField     usernameField;
    private JPasswordField passwordField;
    private JLabel         errorLabel;

    public LoginPanel(MainFrame frame) {
        this.frame = frame;
        setBackground(Theme.BG_DARK);
        setLayout(new GridBagLayout());
        buildUI();
    }

    private void buildUI() {
        JPanel card = new JPanel();
        card.setBackground(Theme.BG_PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER, 1),
                BorderFactory.createEmptyBorder(40, 50, 40, 50)));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setMaximumSize(new Dimension(420, 500));

        // Logo
        JLabel logo = new JLabel("🎟  TicketHub", SwingConstants.CENTER);
        logo.setFont(Theme.FONT_TITLE);
        logo.setForeground(Theme.ACCENT);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Your gateway to live events", SwingConstants.CENTER);
        sub.setFont(Theme.FONT_SMALL);
        sub.setForeground(Theme.TEXT_MUTED);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        JSeparator sep = new JSeparator();
        sep.setForeground(Theme.BORDER);
        sep.setMaximumSize(new Dimension(320, 2));

        usernameField = Theme.makeField();
        usernameField.setMaximumSize(new Dimension(320, 38));
        passwordField = Theme.makePasswordField();
        passwordField.setMaximumSize(new Dimension(320, 38));

        errorLabel = new JLabel(" ");
        errorLabel.setFont(Theme.FONT_SMALL);
        errorLabel.setForeground(Theme.DANGER);
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton loginBtn = Theme.makeButton("Sign In", true);
        loginBtn.setMaximumSize(new Dimension(320, 40));
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.addActionListener(e -> doLogin());

        JButton regBtn = Theme.makeButton("Create Account", false);
        regBtn.setMaximumSize(new Dimension(320, 36));
        regBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        regBtn.addActionListener(e -> frame.showCard(MainFrame.CARD_REGISTER));

        card.add(logo);
        card.add(Box.createVerticalStrut(6));
        card.add(sub);
        card.add(Box.createVerticalStrut(20));
        card.add(sep);
        card.add(Box.createVerticalStrut(24));
        card.add(makeLabel("Username"));
        card.add(Box.createVerticalStrut(4));
        card.add(usernameField);
        card.add(Box.createVerticalStrut(14));
        card.add(makeLabel("Password"));
        card.add(Box.createVerticalStrut(4));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(6));
        card.add(errorLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(loginBtn);
        card.add(Box.createVerticalStrut(10));
        card.add(regBtn);

        // Enter key
        passwordField.addActionListener(e -> doLogin());

        add(card);
    }

    private JLabel makeLabel(String text) {
        JLabel l = Theme.makeLabel(text, Theme.FONT_SMALL, Theme.TEXT_MUTED);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private void doLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please fill in all fields.");
            return;
        }
        User user = DataManager.findUser(username, password);
        if (user == null) {
            errorLabel.setText("Invalid username or password.");
            return;
        }
        Session.login(user);
        errorLabel.setText(" ");
        frame.showCard(MainFrame.CARD_EVENTS);
    }

    public void reset() {
        usernameField.setText("");
        passwordField.setText("");
        errorLabel.setText(" ");
    }
}
