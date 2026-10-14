
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class BookingFrame extends JFrame {

    private Train train;
    private final String username;
    private final JFrame previousFrame;

    private JTextField trainNumberField;
    private JTextField trainNameField;
    private JTextField sourceField;
    private JTextField destinationField;
    private JTextField journeyDate;
    private JComboBox<String> classType;
    private JComboBox<Integer> passengerCount;
    private JPanel passengerFieldsPanel;
    private JLabel seatAvailabilityLabel;

    private final List<JTextField> passengerNames = new ArrayList<>();

    // Demo capacity per train, date and class.
    private static final int SEAT_CAPACITY = 72;

    public BookingFrame(
            Train train,
            String username,
            JFrame previousFrame) {

        this.train = train;
        this.username = username;
        this.previousFrame = previousFrame;

        setTitle("Little Journeys | Passenger Details");
        setSize(650, 800);
        setMinimumSize(new Dimension(520, 650));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initializeSeatTable();

        JPanel page = new JPanel();
        page.setBackground(AppStyle.CREAM);
        page.setBorder(new EmptyBorder(25, 30, 25, 30));
        page.setLayout(new BoxLayout(page, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Plan Your Journey");
        title.setFont(AppStyle.HEADING);
        title.setForeground(AppStyle.BROWN);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        page.add(title);
        page.add(Box.createVerticalStrut(18));

        trainNumberField = new JTextField(
                String.valueOf(train.getTrainNumber()));
        trainNameField = new JTextField(train.getTrainName());
        sourceField = new JTextField(train.getSourceStation());
        destinationField = new JTextField(train.getDestinationStation());

        styleField(trainNumberField);
        styleField(trainNameField);
        styleField(sourceField);
        styleField(destinationField);

        trainNameField.setEditable(false);
        sourceField.setEditable(false);
        destinationField.setEditable(false);

        addField(page, "Train Number", trainNumberField);
        addField(page, "Train Name (Auto-filled)", trainNameField);
        addField(page, "Source Station (Auto-filled)", sourceField);
        addField(page, "Destination Station (Auto-filled)", destinationField);

        trainNumberField.addActionListener(e -> lookupTrain());

        trainNumberField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                lookupTrain();
            }
        });

        classType = new JComboBox<>(new String[]{
                "Sleeper", "AC 3 Tier", "AC 2 Tier", "AC First Class"
        });
        classType.setFont(AppStyle.BODY);
        classType.addActionListener(e -> updateSeatAvailability());

        journeyDate = new JTextField(
                LocalDate.now().plusDays(1).toString());
        styleField(journeyDate);

        journeyDate.addActionListener(e -> updateSeatAvailability());

        journeyDate.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                updateSeatAvailability();
            }
        });

        addField(page, "Journey Date (YYYY-MM-DD)", journeyDate);
        addField(page, "Travel Class", classType);

        seatAvailabilityLabel = new JLabel();
        seatAvailabilityLabel.setFont(AppStyle.BUTTON_FONT);
        seatAvailabilityLabel.setForeground(AppStyle.DARK_SAGE);
        seatAvailabilityLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        page.add(seatAvailabilityLabel);
        page.add(Box.createVerticalStrut(14));

        passengerCount = new JComboBox<>(
                new Integer[]{1, 2, 3, 4, 5, 6});
        passengerCount.setFont(AppStyle.BODY);
        passengerCount.addActionListener(e -> updatePassengerFields());

        addField(page, "Number of Passengers (Maximum 6)", passengerCount);

        passengerFieldsPanel = new JPanel();
        passengerFieldsPanel.setLayout(
                new BoxLayout(passengerFieldsPanel, BoxLayout.Y_AXIS));
        passengerFieldsPanel.setBackground(AppStyle.CREAM);
        passengerFieldsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        page.add(passengerFieldsPanel);
        page.add(Box.createVerticalStrut(12));

        JLabel note = new JLabel(
                "All passengers share one booking reference.");
        note.setFont(AppStyle.BODY);
        note.setForeground(AppStyle.DARK_SAGE);
        note.setAlignmentX(Component.LEFT_ALIGNMENT);

        page.add(note);
        page.add(Box.createVerticalStrut(20));

        JButton confirm = AppStyle.createButton("Confirm Booking →");
        confirm.addActionListener(e -> confirmBooking());

        JButton back = AppStyle.createButton("← Back");
        back.addActionListener(e -> goBack());

        JPanel buttons = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttons.setOpaque(false);
        buttons.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttons.add(confirm);
        buttons.add(back);

        page.add(buttons);

        JScrollPane scrollPane = new JScrollPane(page);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        setContentPane(scrollPane);

        updatePassengerFields();
        updateSeatAvailability();
    }

    private void styleField(JTextField field) {
        field.setFont(AppStyle.BODY);
        field.setBackground(AppStyle.CARD);
        field.setForeground(AppStyle.BROWN);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppStyle.BORDER),
                new EmptyBorder(8, 10, 8, 10)));
    }

    private void addField(
            JPanel panel, String labelText, JComponent field) {

        JLabel label = new JLabel(labelText);
        label.setFont(AppStyle.BUTTON_FONT);
        label.setForeground(AppStyle.BROWN);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        panel.add(label);
        panel.add(Box.createVerticalStrut(6));
        panel.add(field);
        panel.add(Box.createVerticalStrut(12));
    }

    private void lookupTrain() {
        String numberText = trainNumberField.getText().trim();

        if (numberText.isEmpty()) {
            clearTrainDetails();
            return;
        }

        int number;

        try {
            number = Integer.parseInt(numberText);

            if (number <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            clearTrainDetails();
            seatAvailabilityLabel.setText(
                    "Enter a valid numeric train number.");
            return;
        }

        String sql =
                "SELECT train_number, train_name, source_station, " +
                        "destination_station FROM trains WHERE train_number = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, number);

            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) {
                    clearTrainDetails();
                    seatAvailabilityLabel.setText(
                            "Train number not found. Please try again.");
                    return;
                }

                train = new Train(
                        rs.getInt("train_number"),
                        rs.getString("train_name"),
                        rs.getString("source_station"),
                        rs.getString("destination_station"));

                trainNameField.setText(train.getTrainName());
                sourceField.setText(train.getSourceStation());
                destinationField.setText(train.getDestinationStation());

                updateSeatAvailability();
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not retrieve train details.\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearTrainDetails() {
        train = null;
        trainNameField.setText("");
        sourceField.setText("");
        destinationField.setText("");

        if (seatAvailabilityLabel != null) {
            seatAvailabilityLabel.setText("Train details unavailable.");
        }
    }

    private void updatePassengerFields() {
        if (passengerFieldsPanel == null || passengerCount == null) {
            return;
        }

        passengerFieldsPanel.removeAll();
        passengerNames.clear();

        int count = (Integer) passengerCount.getSelectedItem();

        for (int i = 1; i <= count; i++) {
            JLabel label = new JLabel("Passenger " + i + " Name");
            label.setFont(AppStyle.BUTTON_FONT);
            label.setForeground(AppStyle.BROWN);
            label.setAlignmentX(Component.LEFT_ALIGNMENT);

            JTextField nameField = new JTextField();
            styleField(nameField);
            nameField.setAlignmentX(Component.LEFT_ALIGNMENT);
            nameField.setMaximumSize(
                    new Dimension(Integer.MAX_VALUE, 38));

            passengerNames.add(nameField);

            passengerFieldsPanel.add(label);
            passengerFieldsPanel.add(Box.createVerticalStrut(5));
            passengerFieldsPanel.add(nameField);
            passengerFieldsPanel.add(Box.createVerticalStrut(10));
        }

        passengerFieldsPanel.revalidate();
        passengerFieldsPanel.repaint();

        updateSeatAvailability();
    }

    private void initializeSeatTable() {
        String createSql =
                "CREATE TABLE IF NOT EXISTS booking_seats (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "pnr INTEGER NOT NULL, " +
                        "train_number INTEGER NOT NULL, " +
                        "journey_date TEXT NOT NULL, " +
                        "class_type TEXT NOT NULL, " +
                        "seat_number TEXT NOT NULL, " +
                        "UNIQUE(train_number, journey_date, class_type, seat_number))";

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {

            statement.execute(createSql);

            boolean hasPassengerId = false;

            try (ResultSet columns =
                         statement.executeQuery("PRAGMA table_info(booking_seats)")) {

                while (columns.next()) {
                    if ("passenger_id".equalsIgnoreCase(
                            columns.getString("name"))) {
                        hasPassengerId = true;
                        break;
                    }
                }
            }

            if (!hasPassengerId) {
                statement.executeUpdate(
                        "ALTER TABLE booking_seats " +
                                "ADD COLUMN passenger_id INTEGER");
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not initialize seat tracking.\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private int getAvailableSeats(
            Connection connection,
            int trainNumber,
            String date,
            String selectedClass) throws SQLException {

        String sql =
                "SELECT COUNT(*) FROM booking_seats " +
                        "WHERE train_number = ? AND journey_date = ? " +
                        "AND class_type = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, trainNumber);
            statement.setString(2, date);
            statement.setString(3, selectedClass);

            try (ResultSet rs = statement.executeQuery()) {
                rs.next();
                return SEAT_CAPACITY - rs.getInt(1);
            }
        }
    }

    private void updateSeatAvailability() {
        if (seatAvailabilityLabel == null ||
                train == null ||
                journeyDate == null ||
                classType == null) {
            return;
        }

        String date = journeyDate.getText().trim();

        try {
            LocalDate.parse(date);
        } catch (DateTimeParseException ex) {
            seatAvailabilityLabel.setText(
                    "Enter a valid date to check seat availability.");
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            int available = getAvailableSeats(
                    connection,
                    train.getTrainNumber(),
                    date,
                    (String) classType.getSelectedItem());

            seatAvailabilityLabel.setText(
                    "Seats available (demo): " + available +
                            " / " + SEAT_CAPACITY);

        } catch (SQLException ex) {
            seatAvailabilityLabel.setText(
                    "Seat availability could not be loaded.");
        }
    }

    private void confirmBooking() {
        lookupTrain();

        if (train == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Enter a valid train number first.",
                    "Invalid Train",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<String> names = new ArrayList<>();

        for (int i = 0; i < passengerNames.size(); i++) {
            String name = passengerNames.get(i).getText().trim();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Please enter the name of Passenger " + (i + 1) + ".",
                        "Missing Details",
                        JOptionPane.WARNING_MESSAGE);
                passengerNames.get(i).requestFocus();
                return;
            }

            names.add(name);
        }

        String date = journeyDate.getText().trim();
        String selectedClass = (String) classType.getSelectedItem();

        LocalDate parsedDate;

        try {
            parsedDate = LocalDate.parse(date);

            if (parsedDate.isBefore(LocalDate.now())) {
                JOptionPane.showMessageDialog(
                        this,
                        "Journey date cannot be in the past.",
                        "Invalid Date",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Enter the date in YYYY-MM-DD format.",
                    "Invalid Date",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (username == null || username.trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Your login session is missing. Please log in again.",
                    "Login Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String reservationSql =
                "INSERT INTO reservations " +
                        "(passenger_name, train_number, class_type, journey_date, " +
                        "source_station, destination_station, username) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)";

        String passengerSql =
                "INSERT INTO booking_passengers (pnr, passenger_name) " +
                        "VALUES (?, ?)";

        String seatSql =
                "INSERT INTO booking_seats " +
                        "(pnr, train_number, journey_date, class_type, " +
                        "seat_number, passenger_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);

            try {
                int available = getAvailableSeats(
                        connection,
                        train.getTrainNumber(),
                        date,
                        selectedClass);

                if (available < names.size()) {
                    connection.rollback();

                    JOptionPane.showMessageDialog(
                            this,
                            "Not enough seats available. Seats remaining: " +
                                    available,
                            "Seats Unavailable",
                            JOptionPane.WARNING_MESSAGE);

                    updateSeatAvailability();
                    return;
                }

                int pnr;

                try (PreparedStatement statement = connection.prepareStatement(
                        reservationSql, Statement.RETURN_GENERATED_KEYS)) {

                    statement.setString(1, names.get(0));
                    statement.setInt(2, train.getTrainNumber());
                    statement.setString(3, selectedClass);
                    statement.setString(4, date);
                    statement.setString(5, train.getSourceStation());
                    statement.setString(6, train.getDestinationStation());
                    statement.setString(7, username);

                    statement.executeUpdate();

                    try (ResultSet keys = statement.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new SQLException(
                                    "Could not retrieve the booking reference.");
                        }

                        pnr = keys.getInt(1);
                    }
                }

                // Insert each passenger and collect their database IDs.
                List<Integer> passengerIds = new ArrayList<>();

                try (PreparedStatement statement = connection.prepareStatement(
                        passengerSql, Statement.RETURN_GENERATED_KEYS)) {

                    for (String name : names) {
                        statement.setInt(1, pnr);
                        statement.setString(2, name);
                        statement.executeUpdate();

                        try (ResultSet keys = statement.getGeneratedKeys()) {
                            if (!keys.next()) {
                                throw new SQLException(
                                        "Could not retrieve passenger ID.");
                            }

                            passengerIds.add(keys.getInt(1));
                        }
                    }
                }

                // Find and assign the first available seat numbers.
                // This safely reuses seats released by cancellations.
                List<String> assignedSeats = new ArrayList<>();

                String findSeatSql =
                        "SELECT 1 FROM booking_seats " +
                                "WHERE train_number = ? " +
                                "AND journey_date = ? " +
                                "AND class_type = ? " +
                                "AND seat_number = ?";

                try (PreparedStatement seatStatement =
                             connection.prepareStatement(seatSql);
                     PreparedStatement checkStatement =
                             connection.prepareStatement(findSeatSql)) {

                    int nextSeat = 1;

                    for (int i = 0; i < names.size(); i++) {
                        String seatNumber;

                        while (true) {
                            if (nextSeat > SEAT_CAPACITY) {
                                throw new SQLException(
                                        "No seats are available.");
                            }

                            seatNumber = "S1-" +
                                    String.format("%02d", nextSeat);

                            checkStatement.setInt(
                                    1, train.getTrainNumber());
                            checkStatement.setString(2, date);
                            checkStatement.setString(3, selectedClass);
                            checkStatement.setString(4, seatNumber);

                            try (ResultSet rs =
                                         checkStatement.executeQuery()) {
                                if (!rs.next()) {
                                    break;
                                }
                            }

                            nextSeat++;
                        }

                        seatStatement.setInt(1, pnr);
                        seatStatement.setInt(2, train.getTrainNumber());
                        seatStatement.setString(3, date);
                        seatStatement.setString(4, selectedClass);
                        seatStatement.setString(5, seatNumber);
                        seatStatement.setInt(6, passengerIds.get(i));

                        seatStatement.executeUpdate();
                        assignedSeats.add(seatNumber);

                        nextSeat++;
                    }
                }

                connection.commit();

                JOptionPane.showMessageDialog(
                        this,
                        "Booking saved successfully!\n\n" +
                                "Booking Reference (PNR): " + pnr +
                                "\nUsername: " + username +
                                "\nPassengers: " + String.join(", ", names) +
                                "\nTrain: " + train.getTrainName() +
                                "\nFrom: " + train.getSourceStation() +
                                "\nTo: " + train.getDestinationStation() +
                                "\nDate: " + date +
                                "\nClass: " + selectedClass +
                                "\nAssigned Seats: " +
                                String.join(", ", assignedSeats) +
                                "\n\nDemo booking only — not an official railway ticket.",
                        "Booking Confirmed",
                        JOptionPane.INFORMATION_MESSAGE);

                if (previousFrame != null) {
                    previousFrame.setVisible(true);
                    previousFrame.toFront();
                }

                dispose();

            } catch (SQLException ex) {
                connection.rollback();

                JOptionPane.showMessageDialog(
                        this,
                        "Could not save the booking.\n" + ex.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);

                ex.printStackTrace();

            } finally {
                connection.setAutoCommit(true);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not connect to the database.\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void goBack() {
        if (previousFrame != null) {
            previousFrame.setVisible(true);
            previousFrame.toFront();
        }

        dispose();
    }
}
