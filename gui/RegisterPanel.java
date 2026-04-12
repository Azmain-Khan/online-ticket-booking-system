package gui;

import data.DataManager;
import model.User;
import javax.swing.*;
import java.awt.*;

public class RegisterPanel extends JPanel {
    private MainFrame      frame;
    private JTextField     fullNameField, usernameField, emailField;
    private JPasswordField passwordField, confirmField;
    private JLabel         errorLabel;

    public RegisterPanel(MainFrame frame) {
        this.frame = frame;
        setBackground(Theme.BG_DARK);
        setLayout(new GridBagLayout());
        buildUI();
    }

    private void buildUI() {
        JPanel card = new JPanel();
        card.setBackground(Theme.BG_PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER),
                BorderFactory.createEmptyBorder(36, 50, 36, 50)));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel logo = new JLabel("Create Your Account  ", SwingConstants.CENTER);
        logo.setFont(Theme.FONT_HEADER);
        logo.setForeground(Theme.ACCENT);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        fullNameField = Theme.makeField(); fullNameField.setMaximumSize(new Dimension(320, 38));
        usernameField = Theme.makeField(); usernameField.setMaximumSize(new Dimension(320, 38));
        emailField    = Theme.makeField(); emailField.setMaximumSize(new Dimension(320, 38));
        passwordField = Theme.makePasswordField(); passwordField.setMaximumSize(new Dimension(320, 38));
        confirmField  = Theme.makePasswordField(); confirmField.setMaximumSize(new Dimension(320, 38));

        errorLabel = new JLabel(" ");
        errorLabel.setFont(Theme.FONT_SMALL);
        errorLabel.setForeground(Theme.DANGER);
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton registerBtn = Theme.makeButton("Register", true);
        registerBtn.setMaximumSize(new Dimension(320, 40));
        registerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerBtn.addActionListener(e -> doRegister());

        JButton backBtn = Theme.makeButton("Back to Login", false);
        backBtn.setMaximumSize(new Dimension(320, 36));
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        backBtn.addActionListener(e -> frame.showCard(MainFrame.CARD_LOGIN));

        card.add(logo);
        card.add(Box.createVerticalStrut(20));
        addRow(card, "Full Name              ",        fullNameField);
        addRow(card, "Username              ",         usernameField);
        addRow(card, "Email                      ",            emailField);
        addRow(card, "Password              ",         passwordField);
        addRow(card, "Confirm Password", confirmField);
        card.add(Box.createVerticalStrut(4));
        card.add(errorLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(registerBtn);
        card.add(Box.createVerticalStrut(8));
        card.add(backBtn);

        add(card);
    }

    private void addRow(JPanel p, String label, JComponent field) {
        JLabel l = Theme.makeLabel(label, Theme.FONT_SMALL, Theme.TEXT_MUTED);
        l.setAlignmentX(Component.RIGHT_ALIGNMENT);
        p.add(l);
        p.add(Box.createVerticalStrut(4));
        p.add(field);
        p.add(Box.createVerticalStrut(12));
    }

    private void doRegister() {
        String fullName = fullNameField.getText().trim();
        String username = usernameField.getText().trim();
        String email    = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirm  = new String(confirmField.getPassword());

        if (fullName.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("All fields are required."); return;
        }
        if (!password.equals(confirm)) {
            errorLabel.setText("Passwords do not match."); return;
        }
        if (password.length() < 6) {
            errorLabel.setText("Password must be at least 6 characters."); return;
        }
        if (DataManager.usernameExists(username)) {
            errorLabel.setText("Username already taken."); return;
        }
        DataManager.addUser(new User(username, password, email, fullName, false));
        JOptionPane.showMessageDialog(this, "Account created! Please log in.", "Success",
                JOptionPane.INFORMATION_MESSAGE);
        clearFields();
        frame.showCard(MainFrame.CARD_LOGIN);
    }

    private void clearFields() {
        fullNameField.setText(""); usernameField.setText(""); emailField.setText("");
        passwordField.setText(""); confirmField.setText(""); errorLabel.setText(" ");
    }
}
