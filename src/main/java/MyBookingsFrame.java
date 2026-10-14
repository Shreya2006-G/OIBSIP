
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class MyBookingsFrame extends JFrame {

    private final String username;
    private final JFrame previousFrame;

    private JTable bookingsTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;

    public MyBookingsFrame(String username, JFrame previousFrame) {
        this.username = username;
        this.previousFrame = previousFrame;

        setTitle("Little Journeys - My Bookings");
        setSize(1100, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        getContentPane().setBackground(AppStyle.CREAM);
        setLayout(new BorderLayout(15, 15));

        createHeader();
        createBookingsTable();
        loadBookings();
    }

    private void createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(AppStyle.CREAM);
        header.setBorder(
                BorderFactory.createEmptyBorder(20, 25, 10, 25)
        );

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(AppStyle.CREAM);

        JLabel title = new JLabel("My Journeys");
        title.setFont(AppStyle.HEADING);
        title.setForeground(AppStyle.BROWN);

        JLabel subtitle = new JLabel(
                "View your bookings and manage your passengers."
        );
        subtitle.setFont(AppStyle.BODY);
        subtitle.setForeground(AppStyle.DARK_SAGE);

        titlePanel.add(title);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subtitle);

        JButton backButton = new JButton("Back to Dashboard");
        styleButton(backButton);

        backButton.addActionListener(e -> goBack());

        header.add(titlePanel, BorderLayout.WEST);
        header.add(backButton, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);
    }

    private void createBookingsTable() {
        String[] columns = {
                "PNR",
                "Train No.",
                "Train Name",
                "From",
                "To",
                "Journey Date",
                "Class",
                "Passengers"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        bookingsTable = new JTable(tableModel);
        bookingsTable.setFont(AppStyle.BODY);
        bookingsTable.setRowHeight(30);
        bookingsTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );
        bookingsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        int[] widths = {70, 90, 190, 150, 150, 120, 100, 220};

        for (int i = 0; i < widths.length; i++) {
            bookingsTable.getColumnModel()
                    .getColumn(i)
                    .setPreferredWidth(widths[i]);
        }

        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        scrollPane.setBorder(
                BorderFactory.createLineBorder(AppStyle.BORDER)
        );

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(AppStyle.CREAM);
        centerPanel.setBorder(
                BorderFactory.createEmptyBorder(0, 25, 0, 25)
        );

        statusLabel = new JLabel("Loading bookings...");
        statusLabel.setFont(AppStyle.BODY);
        statusLabel.setForeground(AppStyle.DARK_SAGE);

        centerPanel.add(statusLabel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        JButton managePassengersButton =
                new JButton("Manage Passengers");
        styleButton(managePassengersButton);

        managePassengersButton.addActionListener(
                e -> managePassengers()
        );

        JButton cancelButton =
                new JButton("Cancel Selected Booking");
        styleButton(cancelButton);

        cancelButton.addActionListener(
                e -> cancelSelectedBooking()
        );

        JPanel bottomPanel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 12, 15)
        );
        bottomPanel.setBackground(AppStyle.CREAM);
        bottomPanel.setBorder(
                BorderFactory.createEmptyBorder(0, 25, 10, 25)
        );

        bottomPanel.add(managePassengersButton);
        bottomPanel.add(cancelButton);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void styleButton(JButton button) {
        button.setFont(AppStyle.BUTTON_FONT);
        button.setBackground(AppStyle.SAGE);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );
    }

    private void loadBookings() {
        tableModel.setRowCount(0);

        String sql =
                "SELECT r.pnr, r.train_number, " +
                        "COALESCE(t.train_name, 'Unknown train'), " +
                        "r.source_station, r.destination_station, " +
                        "r.journey_date, r.class_type, " +
                        "COALESCE((" +
                        " SELECT GROUP_CONCAT(bp.passenger_name, ', ') " +
                        " FROM booking_passengers bp " +
                        " WHERE bp.pnr = r.pnr" +
                        "), r.passenger_name) AS passengers " +
                        "FROM reservations r " +
                        "LEFT JOIN trains t " +
                        "ON r.train_number = t.train_number " +
                        "WHERE r.username = ? " +
                        "ORDER BY r.pnr DESC";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tableModel.addRow(new Object[]{
                            rs.getInt("pnr"),
                            rs.getInt("train_number"),
                            rs.getString(3),
                            rs.getString("source_station"),
                            rs.getString("destination_station"),
                            rs.getString("journey_date"),
                            rs.getString("class_type"),
                            rs.getString("passengers")
                    });
                }
            }

            if (tableModel.getRowCount() == 0) {
                statusLabel.setText(
                        "No bookings yet. Your next journey awaits!"
                );
            } else {
                statusLabel.setText(
                        "You have " + tableModel.getRowCount()
                                + " booking(s)."
                );
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load bookings:\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );

            statusLabel.setText("Could not load bookings.");
        }
    }

    private Integer getSelectedPnr() {
        int selectedRow = bookingsTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a booking first."
            );
            return null;
        }

        int modelRow =
                bookingsTable.convertRowIndexToModel(selectedRow);

        return Integer.parseInt(
                tableModel.getValueAt(modelRow, 0).toString()
        );
    }

    private LocalDate getSelectedJourneyDate() {
        int selectedRow = bookingsTable.getSelectedRow();

        if (selectedRow == -1) {
            return null;
        }

        int modelRow =
                bookingsTable.convertRowIndexToModel(selectedRow);

        String date =
                tableModel.getValueAt(modelRow, 5).toString();

        try {
            return LocalDate.parse(date);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "The journey date is invalid. Please check the booking."
            );
            return null;
        }
    }

    private boolean isFutureJourney(LocalDate journeyDate) {
        if (journeyDate == null) {
            return false;
        }

        if (!journeyDate.isAfter(LocalDate.now())) {
            JOptionPane.showMessageDialog(
                    this,
                    "Only bookings for future dates can be cancelled.",
                    "Cancellation Not Allowed",
                    JOptionPane.WARNING_MESSAGE
            );
            return false;
        }

        return true;
    }

    // Manage passengers separately within a group booking.
    private void managePassengers() {
        Integer pnr = getSelectedPnr();

        if (pnr == null) {
            return;
        }

        LocalDate journeyDate = getSelectedJourneyDate();

        if (!isFutureJourney(journeyDate)) {
            return;
        }

        if (!bookingBelongsToUser(pnr)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Booking not found for your account."
            );
            loadBookings();
            return;
        }

        JDialog dialog = new JDialog(
                this,
                "Manage Passengers - PNR " + pnr,
                true
        );

        dialog.setSize(650, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JLabel heading = new JLabel(
                "Passengers in booking PNR " + pnr
        );
        heading.setFont(AppStyle.SUBHEADING);
        heading.setForeground(AppStyle.BROWN);
        heading.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 5, 15)
        );

        DefaultTableModel passengerModel =
                new DefaultTableModel(
                        new String[]{
                                "Passenger ID",
                                "Passenger Name",
                                "Seat Number"
                        },
                        0
                ) {
                    @Override
                    public boolean isCellEditable(
                            int row, int column
                    ) {
                        return false;
                    }
                };

        JTable passengerTable = new JTable(passengerModel);
        passengerTable.setRowHeight(28);
        passengerTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        // Keep the passenger ID in the model but hide its column.
        passengerTable.removeColumn(
                passengerTable.getColumnModel().getColumn(0)
        );

        JScrollPane scrollPane = new JScrollPane(passengerTable);

        JButton cancelPassengerButton =
                new JButton("Cancel Selected Passenger");
        styleButton(cancelPassengerButton);

        JButton closeButton = new JButton("Close");
        styleButton(closeButton);

        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 10, 10)
        );
        buttonPanel.setBackground(AppStyle.CREAM);
        buttonPanel.add(cancelPassengerButton);
        buttonPanel.add(closeButton);

        dialog.getContentPane().setBackground(AppStyle.CREAM);
        dialog.add(heading, BorderLayout.NORTH);
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        Runnable refreshPassengers = () -> {
            passengerModel.setRowCount(0);

            String sql =
                    "SELECT bp.id, bp.passenger_name, " +
                            "COALESCE((" +
                            " SELECT bs.seat_number " +
                            " FROM booking_seats bs " +
                            " WHERE bs.pnr = bp.pnr " +
                            " AND bs.passenger_id = bp.id " +
                            " LIMIT 1" +
                            "), 'Not linked (older booking)') " +
                            "FROM booking_passengers bp " +
                            "WHERE bp.pnr = ? " +
                            "ORDER BY bp.id";

            try (Connection con =
                         DatabaseConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setInt(1, pnr);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        passengerModel.addRow(new Object[]{
                                rs.getInt(1),
                                rs.getString(2),
                                rs.getString(3)
                        });
                    }
                }

                // Support older single-passenger reservations
                // that do not have a booking_passengers row.
                if (passengerModel.getRowCount() == 0) {
                    String legacySql =
                            "SELECT passenger_name " +
                                    "FROM reservations " +
                                    "WHERE pnr = ? AND username = ?";

                    try (PreparedStatement legacyPs =
                                 con.prepareStatement(legacySql)) {

                        legacyPs.setInt(1, pnr);
                        legacyPs.setString(2, username);

                        try (ResultSet rs =
                                     legacyPs.executeQuery()) {
                            if (rs.next()) {
                                passengerModel.addRow(new Object[]{
                                        -1,
                                        rs.getString("passenger_name"),
                                        "Not linked (older booking)"
                                });
                            }
                        }
                    }
                }

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(
                        dialog,
                        "Unable to load passengers:\n"
                                + ex.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        };

        refreshPassengers.run();

        cancelPassengerButton.addActionListener(e -> {
            int selectedRow =
                    passengerTable.getSelectedRow();

            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(
                        dialog,
                        "Please select a passenger first."
                );
                return;
            }

            int modelRow =
                    passengerTable.convertRowIndexToModel(
                            selectedRow
                    );

            int passengerId = Integer.parseInt(
                    passengerModel.getValueAt(
                            modelRow, 0
                    ).toString()
            );

            String passengerName =
                    passengerModel.getValueAt(
                            modelRow, 1
                    ).toString();

            int choice = JOptionPane.showConfirmDialog(
                    dialog,
                    "Cancel the booking for " + passengerName
                            + " only?\nOther passengers will remain booked.",
                    "Confirm Passenger Cancellation",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (choice != JOptionPane.YES_OPTION) {
                return;
            }

            if (cancelIndividualPassenger(
                    pnr, passengerId
            )) {
                loadBookings();
                refreshPassengers.run();

                if (passengerModel.getRowCount() == 0) {
                    dialog.dispose();
                }
            }
        });

        closeButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private boolean bookingBelongsToUser(int pnr) {
        String sql =
                "SELECT journey_date FROM reservations " +
                        "WHERE pnr = ? AND username = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, pnr);
            ps.setString(2, username);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Unable to verify booking:\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return false;
        }
    }

    private boolean cancelIndividualPassenger(
            int pnr, int passengerId
    ) {
        String selectSql =
                "SELECT journey_date FROM reservations " +
                        "WHERE pnr = ? AND username = ?";

        try (Connection con = DatabaseConnection.getConnection()) {
            con.setAutoCommit(false);

            try {
                LocalDate journeyDate;

                try (PreparedStatement ps =
                             con.prepareStatement(selectSql)) {
                    ps.setInt(1, pnr);
                    ps.setString(2, username);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException(
                                    "Booking not found."
                            );
                        }

                        journeyDate = LocalDate.parse(
                                rs.getString("journey_date")
                        );
                    }
                }

                if (!journeyDate.isAfter(LocalDate.now())) {
                    con.rollback();

                    JOptionPane.showMessageDialog(
                            this,
                            "Only future bookings can be cancelled."
                    );
                    return false;
                }

                if (passengerId == -1) {
                    // Legacy booking with no passenger-detail rows.
                    // This is the only passenger, so remove its booking.
                    try (PreparedStatement ps =
                                 con.prepareStatement(
                                         "DELETE FROM booking_seats "
                                                 + "WHERE pnr = ?"
                                 )) {
                        ps.setInt(1, pnr);
                        ps.executeUpdate();
                    }

                    try (PreparedStatement ps =
                                 con.prepareStatement(
                                         "DELETE FROM reservations "
                                                 + "WHERE pnr = ? "
                                                 + "AND username = ?"
                                 )) {
                        ps.setInt(1, pnr);
                        ps.setString(2, username);

                        if (ps.executeUpdate() != 1) {
                            throw new SQLException(
                                    "Booking could not be cancelled."
                            );
                        }
                    }

                } else {
                    // Confirm the passenger belongs to this PNR.
                    String verifySql =
                            "SELECT id FROM booking_passengers " +
                                    "WHERE id = ? AND pnr = ?";

                    try (PreparedStatement ps =
                                 con.prepareStatement(verifySql)) {
                        ps.setInt(1, passengerId);
                        ps.setInt(2, pnr);

                        try (ResultSet rs = ps.executeQuery()) {
                            if (!rs.next()) {
                                throw new SQLException(
                                        "Passenger not found in this booking."
                                );
                            }
                        }
                    }

                    // Delete only seats explicitly linked to this
                    // passenger. Never guess an old unlinked seat.
                    try (PreparedStatement ps =
                                 con.prepareStatement(
                                         "DELETE FROM booking_seats "
                                                 + "WHERE pnr = ? "
                                                 + "AND passenger_id = ?"
                                 )) {
                        ps.setInt(1, pnr);
                        ps.setInt(2, passengerId);
                        ps.executeUpdate();
                    }

                    try (PreparedStatement ps =
                                 con.prepareStatement(
                                         "DELETE FROM booking_passengers "
                                                 + "WHERE id = ? AND pnr = ?"
                                 )) {
                        ps.setInt(1, passengerId);
                        ps.setInt(2, pnr);

                        if (ps.executeUpdate() != 1) {
                            throw new SQLException(
                                    "Passenger could not be cancelled."
                            );
                        }
                    }

                    int remainingPassengers;

                    try (PreparedStatement ps =
                                 con.prepareStatement(
                                         "SELECT COUNT(*) " +
                                                 "FROM booking_passengers " +
                                                 "WHERE pnr = ?"
                                 )) {
                        ps.setInt(1, pnr);

                        try (ResultSet rs = ps.executeQuery()) {
                            rs.next();
                            remainingPassengers = rs.getInt(1);
                        }
                    }

                    if (remainingPassengers == 0) {
                        // No passengers remain, so remove the booking.
                        try (PreparedStatement ps =
                                     con.prepareStatement(
                                             "DELETE FROM booking_seats "
                                                     + "WHERE pnr = ?"
                                     )) {
                            ps.setInt(1, pnr);
                            ps.executeUpdate();
                        }

                        try (PreparedStatement ps =
                                     con.prepareStatement(
                                             "DELETE FROM reservations "
                                                     + "WHERE pnr = ? "
                                                     + "AND username = ?"
                                     )) {
                            ps.setInt(1, pnr);
                            ps.setString(2, username);

                            if (ps.executeUpdate() != 1) {
                                throw new SQLException(
                                        "Booking could not be removed."
                                );
                            }
                        }

                    } else {
                        // Keep the reservation's primary passenger
                        // name aligned with the remaining passengers.
                        try (PreparedStatement ps =
                                     con.prepareStatement(
                                             "UPDATE reservations " +
                                                     "SET passenger_name = (" +
                                                     " SELECT passenger_name " +
                                                     " FROM booking_passengers " +
                                                     " WHERE pnr = ? " +
                                                     " ORDER BY id LIMIT 1" +
                                                     ") WHERE pnr = ? " +
                                                     "AND username = ?"
                                     )) {
                            ps.setInt(1, pnr);
                            ps.setInt(2, pnr);
                            ps.setString(3, username);
                            ps.executeUpdate();
                        }
                    }
                }

                con.commit();

                JOptionPane.showMessageDialog(
                        this,
                        "Passenger cancellation successful!",
                        "Little Journeys",
                        JOptionPane.INFORMATION_MESSAGE
                );

                return true;

            } catch (Exception ex) {
                con.rollback();

                JOptionPane.showMessageDialog(
                        this,
                        "Unable to cancel passenger:\n"
                                + ex.getMessage(),
                        "Cancellation Error",
                        JOptionPane.ERROR_MESSAGE
                );

                return false;

            } finally {
                con.setAutoCommit(true);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Database error:\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return false;
        }
    }

    // Cancel the entire group booking.
    private void cancelSelectedBooking() {
        Integer pnr = getSelectedPnr();

        if (pnr == null) {
            return;
        }

        LocalDate journeyDate = getSelectedJourneyDate();

        if (!isFutureJourney(journeyDate)) {
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to cancel booking PNR "
                        + pnr + "?\nThis cancels the entire group booking.",
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        cancelBookingInDatabase(pnr, journeyDate);
    }

    private void cancelBookingInDatabase(
            int pnr, LocalDate journeyDate
    ) {
        try (Connection con = DatabaseConnection.getConnection()) {
            con.setAutoCommit(false);

            try {
                String verifySql =
                        "SELECT journey_date FROM reservations " +
                                "WHERE pnr = ? AND username = ?";

                try (PreparedStatement ps =
                             con.prepareStatement(verifySql)) {
                    ps.setInt(1, pnr);
                    ps.setString(2, username);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException(
                                    "Booking not found."
                            );
                        }

                        LocalDate actualDate = LocalDate.parse(
                                rs.getString("journey_date")
                        );

                        if (!actualDate.isAfter(LocalDate.now())) {
                            throw new SQLException(
                                    "Only future bookings can be cancelled."
                            );
                        }
                    }
                }

                String[] deleteQueries = {
                        "DELETE FROM booking_seats WHERE pnr = ?",
                        "DELETE FROM booking_passengers WHERE pnr = ?",
                        "DELETE FROM reservations " +
                                "WHERE pnr = ? AND username = ?"
                };

                try (PreparedStatement ps1 =
                             con.prepareStatement(deleteQueries[0]);
                     PreparedStatement ps2 =
                             con.prepareStatement(deleteQueries[1]);
                     PreparedStatement ps3 =
                             con.prepareStatement(deleteQueries[2])) {

                    ps1.setInt(1, pnr);
                    ps1.executeUpdate();

                    ps2.setInt(1, pnr);
                    ps2.executeUpdate();

                    ps3.setInt(1, pnr);
                    ps3.setString(2, username);

                    if (ps3.executeUpdate() != 1) {
                        throw new SQLException(
                                "Booking could not be cancelled."
                        );
                    }
                }

                con.commit();

                JOptionPane.showMessageDialog(
                        this,
                        "Entire booking cancelled successfully!",
                        "Little Journeys",
                        JOptionPane.INFORMATION_MESSAGE
                );

                loadBookings();

            } catch (Exception ex) {
                con.rollback();

                JOptionPane.showMessageDialog(
                        this,
                        "Unable to cancel booking:\n"
                                + ex.getMessage(),
                        "Cancellation Error",
                        JOptionPane.ERROR_MESSAGE
                );

            } finally {
                con.setAutoCommit(true);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Database error:\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void goBack() {
        if (previousFrame != null) {
            previousFrame.setVisible(true);
        }

        dispose();
    }
}
