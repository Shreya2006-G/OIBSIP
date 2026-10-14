

import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.border.EmptyBorder;

public class AppStyle {

    // Little Journeys - Vintage Moss & Cream Theme

    // Background colours
    public static final Color CREAM =
            new Color(245, 239, 228);       // #F5EFE4

    public static final Color CARD =
            new Color(255, 253, 248);       // #FFFDF8

    // Vintage moss green
    public static final Color SAGE =
            new Color(126, 144, 115);       // #7E9073

    public static final Color DARK_SAGE =
            new Color(91, 108, 79);         // Deeper moss green

    // Cocoa brown palette
    public static final Color BROWN =
            new Color(73, 56, 45);          // #49382D

    public static final Color SOFT_BROWN =
            new Color(181, 153, 124);       // #B5997C

    public static final Color BORDER =
            new Color(222, 209, 190);       // #DED1BE

    // Typography
    public static final Font HEADING =
            new Font("Serif", Font.BOLD, 28);

    public static final Font SUBHEADING =
            new Font("Serif", Font.PLAIN, 18);

    public static final Font BODY =
            new Font("SansSerif", Font.PLAIN, 14);

    public static final Font BUTTON_FONT =
            new Font("SansSerif", Font.BOLD, 14);

    // Prevent unnecessary object creation
    private AppStyle() {
    }

    // Reusable button styling
    public static JButton createButton(String text) {
        JButton button = new JButton(text);

        button.setFont(BUTTON_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(SAGE);
        button.setFocusPainted(false);

        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(SAGE),
                new EmptyBorder(10, 20, 10, 20)
        ));

        button.setOpaque(true);
        button.setCursor(
                new java.awt.Cursor(
                        java.awt.Cursor.HAND_CURSOR
                )
        );

        return button;
    }

    // Reusable text field styling
    public static void styleTextField(JTextField field) {
        field.setFont(BODY);
        field.setForeground(BROWN);
        field.setBackground(CARD);
        field.setCaretColor(DARK_SAGE);

        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(9, 10, 9, 10)
        ));
    }

    // Reusable password field styling
    public static void stylePasswordField(
            JPasswordField field) {

        field.setFont(BODY);
        field.setForeground(BROWN);
        field.setBackground(CARD);
        field.setCaretColor(DARK_SAGE);

        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(9, 10, 9, 10)
        ));
    }
}
