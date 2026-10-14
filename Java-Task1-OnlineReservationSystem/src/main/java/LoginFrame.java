
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginFrame extends JFrame {

    public LoginFrame() {
        setTitle("Little Journeys | Online Reservation System");
        setSize(900, 580);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        mainPanel.setBackground(AppStyle.CREAM);

        // Left side: travel-inspired welcome panel
        JPanel welcomePanel = new JPanel();
        welcomePanel.setBackground(AppStyle.SAGE);
        welcomePanel.setLayout(new BoxLayout(
                welcomePanel, BoxLayout.Y_AXIS
        ));
        welcomePanel.setBorder(new EmptyBorder(55, 35, 40, 35));

        TrainIllustration trainIllustration = new TrainIllustration();
        trainIllustration.setAlignmentX(Component.CENTER_ALIGNMENT);
        trainIllustration.setMaximumSize(new Dimension(340, 200));

        JLabel brand = new JLabel("Little Journeys");
        brand.setFont(new Font("Serif", Font.BOLD, 30));
        brand.setForeground(Color.WHITE);
        brand.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel tagline = new JLabel(
                "Every journey has a story."
        );
        tagline.setFont(AppStyle.SUBHEADING);
        tagline.setForeground(Color.WHITE);
        tagline.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel scenery = new JLabel(
                "<html><center>Rolling hills, golden skies,<br>" +
                        "and somewhere new to discover.</center></html>"
        );
        scenery.setFont(AppStyle.BODY);
        scenery.setForeground(Color.WHITE);
        scenery.setAlignmentX(Component.CENTER_ALIGNMENT);

        welcomePanel.add(Box.createVerticalGlue());
        welcomePanel.add(trainIllustration);
        welcomePanel.add(Box.createVerticalStrut(18));
        welcomePanel.add(brand);
        welcomePanel.add(Box.createVerticalStrut(10));
        welcomePanel.add(tagline);
        welcomePanel.add(Box.createVerticalStrut(35));
        welcomePanel.add(scenery);
        welcomePanel.add(Box.createVerticalGlue());

        // Right side: login form
        JPanel formPanel = new JPanel();
        formPanel.setBackground(AppStyle.CREAM);
        formPanel.setLayout(new BoxLayout(
                formPanel, BoxLayout.Y_AXIS
        ));
        formPanel.setBorder(new EmptyBorder(65, 55, 55, 55));

        JLabel heading = new JLabel("Welcome aboard!");
        heading.setFont(AppStyle.HEADING);
        heading.setForeground(AppStyle.BROWN);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel(
                "Sign in to plan your next journey."
        );
        subtitle.setFont(AppStyle.BODY);
        subtitle.setForeground(AppStyle.BROWN);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(AppStyle.BODY);
        usernameLabel.setForeground(AppStyle.BROWN);
        usernameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField usernameField = new JTextField();
        AppStyle.styleTextField(usernameField);
        usernameField.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 42)
        );
        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(AppStyle.BODY);
        passwordLabel.setForeground(AppStyle.BROWN);
        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPasswordField passwordField = new JPasswordField();
        AppStyle.stylePasswordField(passwordField);
        passwordField.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 42)
        );
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton loginButton =
                AppStyle.createButton("Login");

        JButton registerButton =
                AppStyle.createButton("Create an account");

        registerButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        registerButton.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 44)
        );
        registerButton.addActionListener(e -> {
            new RegisterFrame().setVisible(true);
        });
        loginButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginButton.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 44)
        );

        JLabel footer = new JLabel(
                "A little adventure awaits you."
        );
        footer.setFont(new Font("Serif", Font.ITALIC, 14));
        footer.setForeground(AppStyle.DARK_SAGE);
        footer.setAlignmentX(Component.CENTER_ALIGNMENT);

        loginButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password =
                    new String(passwordField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Please enter both username and password.",
                        "Missing details",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }


            try {
                String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

                try (java.sql.Connection connection =
                             DatabaseConnection.getConnection();
                     java.sql.PreparedStatement statement =
                             connection.prepareStatement(sql)) {

                    statement.setString(1, username);
                    statement.setString(2, password);

                    try (java.sql.ResultSet result = statement.executeQuery()) {

                        if (result.next()) {
                            new DashboardFrame(username).setVisible(true);
                            dispose();

                        } else {
                            JOptionPane.showMessageDialog(
                                    this,
                                    "Invalid username or password.",
                                    "Login failed",
                                    JOptionPane.ERROR_MESSAGE
                            );
                        }
                    }
                }

            } catch (java.sql.SQLException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Database error. Please try again.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                ex.printStackTrace();
            }


        });

        formPanel.add(Box.createVerticalGlue());
        formPanel.add(heading);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(subtitle);
        formPanel.add(Box.createVerticalStrut(35));
        formPanel.add(usernameLabel);
        formPanel.add(Box.createVerticalStrut(7));
        formPanel.add(usernameField);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(passwordLabel);
        formPanel.add(Box.createVerticalStrut(7));
        formPanel.add(passwordField);
        formPanel.add(Box.createVerticalStrut(28));
        formPanel.add(loginButton);
        formPanel.add(Box.createVerticalStrut(12));
        formPanel.add(registerButton);
        formPanel.add(Box.createVerticalStrut(22));
        formPanel.add(footer);
        formPanel.add(Box.createVerticalGlue());

        mainPanel.add(welcomePanel);
        mainPanel.add(formPanel);

        add(mainPanel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginFrame frame = new LoginFrame();
            frame.setVisible(true);
        });
    }
}
