package gui;

import java.awt.*;

public class Theme {
    // Palette – deep navy + electric amber + off-white
    public static final Color BG_DARK      = new Color(12,  18,  40);   // deep navy
    public static final Color BG_PANEL     = new Color(20,  30,  60);
    public static final Color BG_CARD      = new Color(28,  42,  80);
    public static final Color ACCENT       = new Color(255, 176, 0);    // amber
    public static final Color ACCENT_HOVER = new Color(255, 200, 60);
    public static final Color TEXT_PRIMARY = new Color(240, 240, 250);
    public static final Color TEXT_MUTED   = new Color(140, 150, 180);
    public static final Color SUCCESS      = new Color(60,  210, 130);
    public static final Color DANGER       = new Color(255,  80,  80);
    public static final Color BORDER       = new Color(40,  60, 110);

    // Fonts
    public static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD,  26);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD,  16);
    public static final Font FONT_BODY   = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL  = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_MONO   = new Font("Consolas",  Font.PLAIN, 12);

    // ── Helpers ───────────────────────────────────────────────────────────────

    public static javax.swing.JButton makeButton(String text, boolean primary) {
        javax.swing.JButton btn = new javax.swing.JButton(text);
        btn.setFont(FONT_BODY);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        if (primary) {
            btn.setBackground(ACCENT);
            btn.setForeground(BG_DARK);
        } else {
            btn.setBackground(BG_CARD);
            btn.setForeground(TEXT_PRIMARY);
            btn.setBorder(javax.swing.BorderFactory.createLineBorder(BORDER));
        }
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            Color orig = btn.getBackground();
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(primary ? ACCENT_HOVER : BORDER);
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(orig);
            }
        });
        btn.setOpaque(true);
        return btn;
    }

    public static javax.swing.JTextField makeField() {
        javax.swing.JTextField tf = new javax.swing.JTextField();
        tf.setFont(FONT_BODY);
        tf.setBackground(BG_DARK);
        tf.setForeground(TEXT_PRIMARY);
        tf.setCaretColor(ACCENT);
        tf.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createLineBorder(BORDER),
                javax.swing.BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        return tf;
    }

    public static javax.swing.JPasswordField makePasswordField() {
        javax.swing.JPasswordField pf = new javax.swing.JPasswordField();
        pf.setFont(FONT_BODY);
        pf.setBackground(BG_DARK);
        pf.setForeground(TEXT_PRIMARY);
        pf.setCaretColor(ACCENT);
        pf.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createLineBorder(BORDER),
                javax.swing.BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        return pf;
    }

    public static javax.swing.JLabel makeLabel(String text, Font font, Color color) {
        javax.swing.JLabel lbl = new javax.swing.JLabel(text);
        lbl.setFont(font);
        lbl.setForeground(color);
        return lbl;
    }
}
