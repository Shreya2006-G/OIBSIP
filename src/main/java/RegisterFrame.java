
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegisterFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmField;

    public RegisterFrame() {
        setTitle("Little Journeys | Create Account");
        setSize(850, 580);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        mainPanel.setBackground(AppStyle.CREAM);

        // LEFT SIDE: Welcome panel
        JPanel welcomePanel = new JPanel();
        welcomePanel.setBackground(AppStyle.SAGE);
        welcomePanel.setLayout(
                new BoxLayout(welcomePanel, BoxLayout.Y_AXIS)
        );
        welcomePanel.setBorder(
                new EmptyBorder(35, 30, 35, 30)
        );

        JLabel brand = new JLabel("Little Journeys");
        brand.setFont(new Font("Serif", Font.BOLD, 29));
        brand.setForeground(Color.WHITE);
        brand.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel tagline = new JLabel(
                "A new adventure begins here."
        );
        tagline.setFont(AppStyle.SUBHEADING);
        tagline.setForeground(Color.WHITE);
        tagline.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sceneryText = new JLabel(
                "<html><center>" +
                        "Rolling hills, golden skies,<br>" +
                        "and somewhere new to discover." +
                        "</center></html>"
        );
        sceneryText.setFont(AppStyle.BODY);
        sceneryText.setForeground(Color.WHITE);
        sceneryText.setAlignmentX(Component.CENTER_ALIGNMENT);

        TrainIllustration illustration =
                new TrainIllustration();

        illustration.setPreferredSize(new Dimension(340, 200));
        illustration.setMaximumSize(new Dimension(340, 200));
        illustration.setAlignmentX(Component.CENTER_ALIGNMENT);

        welcomePanel.add(Box.createVerticalGlue());
        welcomePanel.add(brand);
        welcomePanel.add(Box.createVerticalStrut(12));
        welcomePanel.add(tagline);
        welcomePanel.add(Box.createVerticalStrut(25));
        welcomePanel.add(illustration);
        welcomePanel.add(Box.createVerticalStrut(20));
        welcomePanel.add(sceneryText);
        welcomePanel.add(Box.createVerticalGlue());

        // RIGHT SIDE: Registration form
        JPanel formPanel = new JPanel();
        formPanel.setBackground(AppStyle.CREAM);
        formPanel.setLayout(
                new BoxLayout(formPanel, BoxLayout.Y_AXIS)
        );
        formPanel.setBorder(
                new EmptyBorder(35, 45, 30, 45)
        );

        JLabel heading = new JLabel("Join the journey");
        heading.setFont(AppStyle.HEADING);
        heading.setForeground(AppStyle.BROWN);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel(
                "Create your account to get started."
        );
        subtitle.setFont(AppStyle.BODY);
        subtitle.setForeground(AppStyle.DARK_SAGE);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Username
        JLabel usernameLabel = createLabel("Username");

        usernameField = new JTextField();
        AppStyle.styleTextField(usernameField);
        styleField(usernameField);

        // Password
        JLabel passwordLabel = createLabel("Password");

        passwordField = new JPasswordField();
        AppStyle.stylePasswordField(passwordField);
        styleField(passwordField);

        // Confirm password
        JLabel confirmLabel = createLabel("Confirm password");

        confirmField = new JPasswordField();
        AppStyle.stylePasswordField(confirmField);
        styleField(confirmField);

        // Create account button
        JButton createButton =
                AppStyle.createButton("Create my account  →");

        createButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        createButton.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 44)
        );

        createButton.addActionListener(e -> registerUser());

        // Back to login
        JButton backButton = new JButton(
                "Already travelling with us? Sign in"
        );

        backButton.setFont(
                new Font("SansSerif", Font.PLAIN, 12)
        );
        backButton.setForeground(AppStyle.DARK_SAGE);
        backButton.setBackground(AppStyle.CREAM);
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setFocusPainted(false);
        backButton.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        backButton.addActionListener(e -> dispose());

        JLabel note = new JLabel(
                "Your next adventure starts with a little step."
        );
        note.setFont(new Font("Serif", Font.ITALIC, 12));
        note.setForeground(AppStyle.SOFT_BROWN);
        note.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Assemble form
        formPanel.add(Box.createVerticalGlue());

        formPanel.add(heading);
        formPanel.add(Box.createVerticalStrut(7));
        formPanel.add(subtitle);

        formPanel.add(Box.createVerticalStrut(23));
        formPanel.add(usernameLabel);
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(usernameField);

        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(passwordLabel);
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(passwordField);

        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(confirmLabel);
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(confirmField);

        formPanel.add(Box.createVerticalStrut(23));
        formPanel.add(createButton);

        formPanel.add(Box.createVerticalStrut(13));
        formPanel.add(backButton);

        formPanel.add(Box.createVerticalStrut(12));
        formPanel.add(note);

        formPanel.add(Box.createVerticalGlue());

        mainPanel.add(welcomePanel);
        mainPanel.add(formPanel);

        add(mainPanel);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(AppStyle.BODY);
        label.setForeground(AppStyle.BROWN);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private void styleField(JTextField field) {
        field.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 42)
        );
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private void registerUser() {

        String username = usernameField.getText().trim();
        String password =
                new String(passwordField.getPassword());
        String confirm =
                new String(confirmField.getPassword());

        if (username.isEmpty()
                || password.isEmpty()
                || confirm.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please fill in all fields.",
                    "Missing details",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (username.length() < 3) {
            JOptionPane.showMessageDialog(
                    this,
                    "Username must be at least 3 characters long.",
                    "Invalid username",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (password.length() < 6) {
            JOptionPane.showMessageDialog(
                    this,
                    "Password must be at least 6 characters long.",
                    "Weak password",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (!password.equals(confirm)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Passwords do not match. Please try again.",
                    "Password mismatch",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String sql =
                "INSERT INTO users (username, password) VALUES (?, ?)";

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, username);
            statement.setString(2, password);

            statement.executeUpdate();

            JOptionPane.showMessageDialog(
                    this,
                    "Your account is ready, " + username +
                            "!\nYou can now sign in and plan your journey.",
                    "Welcome aboard!",
                    JOptionPane.INFORMATION_MESSAGE
            );

            dispose();

        } catch (SQLException ex) {

            String message = ex.getMessage();

            if (message != null &&
                    (message.toLowerCase().contains("unique")
                            || message.toLowerCase().contains("constraint"))) {

                JOptionPane.showMessageDialog(
                        this,
                        "This username is already taken.\n" +
                                "Please choose another one.",
                        "Username unavailable",
                        JOptionPane.WARNING_MESSAGE
                );

            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "We couldn't create your account.\n" +
                                "Please check the database connection.",
                        "Registration error",
                        JOptionPane.ERROR_MESSAGE
                );

                ex.printStackTrace();
            }
        }
    }
}
