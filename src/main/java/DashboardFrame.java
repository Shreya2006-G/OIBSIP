
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class DashboardFrame extends JFrame {

    private final String username;
    private JTextField source;
    private JTextField destination;

    public DashboardFrame(String username) {
        this.username = username;

        setTitle("Little Journeys | Explore");
        setSize(1100, 760);
        setMinimumSize(new Dimension(850, 620));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel page = new JPanel(new BorderLayout());
        page.setBackground(AppStyle.CREAM);
        page.add(createHeader(), BorderLayout.NORTH);

        JScrollPane scrollPane =
                new JScrollPane(createMainContent());
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(AppStyle.CREAM);

        page.add(scrollPane, BorderLayout.CENTER);
        setContentPane(page);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(AppStyle.SAGE);
        header.setBorder(new EmptyBorder(12, 24, 12, 24));

        JLabel brand = new JLabel("Little Journeys");
        brand.setFont(new Font("Serif", Font.BOLD, 25));
        brand.setForeground(Color.WHITE);

        JPanel navigation = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 15, 2));
        navigation.setOpaque(false);

        JButton explore = navButton("Explore");
        JButton bookings = navButton("My Bookings");
        JButton cancel = navButton("Cancel Reservation");
        JButton profile = navButton("Hi, " + username + "  ▾");

        explore.addActionListener(e -> {
            source.requestFocusInWindow();
            source.selectAll();
        });

        // Open the logged-in user's bookings.
        bookings.addActionListener(e -> {
            MyBookingsFrame bookingsFrame =
                    new MyBookingsFrame(username, this);

            bookingsFrame.setVisible(true);
            setVisible(false);
        });

        cancel.addActionListener(e -> {
            CancelReservationFrame cancelFrame =
                    new CancelReservationFrame(username, this);

            cancelFrame.setVisible(true);
            setVisible(false);
        });

        profile.addActionListener(e -> {
            String[] options = {"Stay here", "Log out"};

            int choice = JOptionPane.showOptionDialog(
                    this,
                    "Ready to end your journey for now?",
                    "Your account",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    options,
                    options[0]);

            if (choice == 1) {
                dispose();
                new LoginFrame().setVisible(true);
            }
        });

        navigation.add(explore);
        navigation.add(bookings);
        navigation.add(cancel);
        navigation.add(profile);

        header.add(brand, BorderLayout.WEST);
        header.add(navigation, BorderLayout.EAST);

        return header;
    }

    private JButton navButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JPanel createMainContent() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(AppStyle.CREAM);
        content.setBorder(new EmptyBorder(20, 24, 24, 24));

        JPanel topSection = new JPanel(new GridBagLayout());
        topSection.setBackground(AppStyle.CREAM);
        topSection.setAlignmentX(Component.LEFT_ALIGNMENT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 1;

        gbc.gridx = 0;
        gbc.weightx = 0.67;
        gbc.insets = new Insets(0, 0, 0, 18);
        topSection.add(createLeftColumn(), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.33;
        gbc.insets = new Insets(0, 0, 0, 0);
        topSection.add(createRecommendations(), gbc);

        content.add(topSection);

        return content;
    }

    private JPanel createLeftColumn() {
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBackground(AppStyle.CREAM);

        JLabel eyebrow = new JLabel("YOUR NEXT ADVENTURE AWAITS");
        eyebrow.setFont(new Font("SansSerif", Font.BOLD, 10));
        eyebrow.setForeground(AppStyle.DARK_SAGE);
        eyebrow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel heading = new JLabel(
                "<html>The world is<br>waiting.</html>");
        heading.setFont(new Font("Serif", Font.BOLD, 35));
        heading.setForeground(AppStyle.BROWN);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel(
                "<html>Find your next escape,<br>" +
                        "one little journey at a time.</html>");
        subtitle.setFont(AppStyle.BODY);
        subtitle.setForeground(AppStyle.DARK_SAGE);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        ImagePanel illustration =
                new ImagePanel("/images/hero-train.jpg");
        illustration.setPreferredSize(new Dimension(600, 185));
        illustration.setMinimumSize(new Dimension(100, 185));
        illustration.setMaximumSize(new Dimension(600, 185));
        illustration.setAlignmentX(Component.LEFT_ALIGNMENT);

        left.add(eyebrow);
        left.add(Box.createVerticalStrut(8));
        left.add(heading);
        left.add(Box.createVerticalStrut(6));
        left.add(subtitle);
        left.add(Box.createVerticalStrut(16));
        left.add(illustration);
        left.add(Box.createVerticalStrut(18));
        left.add(createSearchCard());

        return left;
    }

    private JPanel createSearchCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(AppStyle.CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppStyle.BORDER),
                new EmptyBorder(18, 18, 18, 18)));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 210));

        JLabel title = new JLabel("Plan your next journey");
        title.setFont(new Font("Serif", Font.BOLD, 23));
        title.setForeground(AppStyle.BROWN);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel description =
                new JLabel("Where would you like to go?");
        description.setFont(AppStyle.BODY);
        description.setForeground(AppStyle.DARK_SAGE);
        description.setAlignmentX(Component.LEFT_ALIGNMENT);

        source = new JTextField();
        destination = new JTextField();

        styleField(source, "Example: Mumbai or Mumbai Central");
        styleField(destination, "Example: Delhi or New Delhi");

        StationAutocomplete.attach(source);
        StationAutocomplete.attach(destination);

        JPanel fields = new JPanel(new GridLayout(1, 2, 10, 0));
        fields.setOpaque(false);
        fields.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        fields.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel fromPanel = new JPanel(new BorderLayout(0, 5));
        fromPanel.setOpaque(false);

        JLabel fromLabel = new JLabel("FROM");
        fromLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
        fromLabel.setForeground(AppStyle.DARK_SAGE);

        fromPanel.add(fromLabel, BorderLayout.NORTH);
        fromPanel.add(source, BorderLayout.CENTER);

        JPanel toPanel = new JPanel(new BorderLayout(0, 5));
        toPanel.setOpaque(false);

        JLabel toLabel = new JLabel("TO");
        toLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
        toLabel.setForeground(AppStyle.DARK_SAGE);

        toPanel.add(toLabel, BorderLayout.NORTH);
        toPanel.add(destination, BorderLayout.CENTER);

        fields.add(fromPanel);
        fields.add(toPanel);

        JButton search = AppStyle.createButton("Search trains  →");
        search.setAlignmentX(Component.LEFT_ALIGNMENT);
        search.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        search.addActionListener(e -> searchTrains());

        card.add(title);
        card.add(Box.createVerticalStrut(6));
        card.add(description);
        card.add(Box.createVerticalStrut(14));
        card.add(fields);
        card.add(Box.createVerticalStrut(12));
        card.add(search);

        return card;
    }

    private void styleField(JTextField field, String tooltip) {
        field.setFont(AppStyle.BODY);
        field.setForeground(AppStyle.BROWN);
        field.setBackground(AppStyle.CARD);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppStyle.BORDER),
                new EmptyBorder(8, 10, 8, 10)));
        field.setToolTipText(tooltip);
    }



    private void searchTrains() {
        String from = source.getText().trim();
        String to = destination.getText().trim();

        if (from.isEmpty() || to.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter both stations.",
                    "Plan your journey",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {
            TrainDAO trainDAO = new TrainDAO();

            // Search for direct trains first.
            List<Train> trains = trainDAO.searchTrains(from, to);

            // Open results even if there are no direct trains.
            // TrainResultsFrame will display connecting journeys.
            TrainResultsFrame resultsFrame = new TrainResultsFrame(
                    from,
                    to,
                    trains,
                    username,
                    this
            );

            resultsFrame.setVisible(true);
            setVisible(false);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not search trains. Please check the database.",
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );

            ex.printStackTrace();
        }
    }



    private JPanel createRecommendations() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(AppStyle.CARD);
        sidebar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppStyle.BORDER),
                new EmptyBorder(14, 12, 14, 12)));

        JLabel eyebrow = new JLabel("A LITTLE INSPIRATION");
        eyebrow.setFont(new Font("SansSerif", Font.BOLD, 10));
        eyebrow.setForeground(AppStyle.DARK_SAGE);
        eyebrow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel heading = new JLabel(
                "<html>Journeys worth<br>taking</html>");
        heading.setFont(new Font("Serif", Font.BOLD, 25));
        heading.setForeground(AppStyle.BROWN);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel intro = new JLabel(
                "<html>Find a place to dream<br>about next.</html>");
        intro.setFont(AppStyle.BODY);
        intro.setForeground(AppStyle.DARK_SAGE);
        intro.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(eyebrow);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(heading);
        sidebar.add(Box.createVerticalStrut(6));
        sidebar.add(intro);
        sidebar.add(Box.createVerticalStrut(12));

        sidebar.add(createDestinationCard(
                "Mountain escapes",
                "Green hills & winding rails",
                "/images/mountain.jpg"));

        sidebar.add(Box.createVerticalStrut(10));

        sidebar.add(createDestinationCard(
                "Coastal adventures",
                "Sea breezes & window seats",
                "/images/coastal.jpg"));

        sidebar.add(Box.createVerticalGlue());

        JLabel footer = new JLabel(
                "<html><center>Take the scenic route.<br>" +
                        "Make beautiful memories.</center></html>");
        footer.setFont(new Font("Serif", Font.ITALIC, 13));
        footer.setForeground(AppStyle.SOFT_BROWN);
        footer.setAlignmentX(Component.CENTER_ALIGNMENT);

        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(footer);

        return sidebar;
    }

    private JPanel createDestinationCard(
            String title, String description, String imagePath) {

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(AppStyle.CREAM);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppStyle.BORDER),
                new EmptyBorder(8, 8, 8, 8)));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        ImagePanel photo = new ImagePanel(imagePath);
        photo.setPreferredSize(new Dimension(280, 105));
        photo.setMinimumSize(new Dimension(100, 105));
        photo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 105));
        photo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 17));
        titleLabel.setForeground(AppStyle.BROWN);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel descriptionLabel =
                new JLabel("<html>" + description + "</html>");
        descriptionLabel.setFont(
                new Font("SansSerif", Font.PLAIN, 11));
        descriptionLabel.setForeground(AppStyle.DARK_SAGE);
        descriptionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton exploreButton = new JButton("Discover  →");
        exploreButton.setFont(new Font("SansSerif", Font.BOLD, 11));
        exploreButton.setForeground(AppStyle.DARK_SAGE);
        exploreButton.setBorderPainted(false);
        exploreButton.setContentAreaFilled(false);
        exploreButton.setFocusPainted(false);
        exploreButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        exploreButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        exploreButton.addActionListener(e -> {
            source.setText("Mumbai");

            if (title.equals("Mountain escapes")) {
                destination.setText("Pune");
            } else if (title.equals("Coastal adventures")) {
                destination.setText("Goa");
            }

            source.requestFocusInWindow();
            destination.selectAll();
        });
        card.add(photo);
        card.add(Box.createVerticalStrut(8));
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(descriptionLabel);
        card.add(exploreButton);

        return card;
    }
}
