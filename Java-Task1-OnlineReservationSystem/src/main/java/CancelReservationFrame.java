
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;

public class CancelReservationFrame extends JFrame {

    private final String username;
    private final JFrame previousFrame;

    private JTextField pnrField;
    private JTextArea detailsArea;
    private JButton confirmButton;
    private int fetchedPnr = -1;

    public CancelReservationFrame(String username, JFrame previousFrame) {
        this.username = username;
        this.previousFrame = previousFrame;

        setTitle("Little Journeys - Cancel Reservation");
        setSize(650, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel page = new JPanel(new BorderLayout(15, 15));
        page.setBackground(AppStyle.CREAM);
        page.setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel heading = new JLabel("Cancel a Reservation");
        heading.setFont(AppStyle.HEADING);
        heading.setForeground(AppStyle.BROWN);

        JButton backButton = AppStyle.createButton("← Back to Dashboard");
        backButton.addActionListener(e -> goBack());

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(heading, BorderLayout.WEST);
        top.add(backButton, BorderLayout.EAST);

        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setOpaque(false);

        pnrField = new JTextField();
        pnrField.setFont(AppStyle.BODY);
        pnrField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppStyle.BORDER),
                new EmptyBorder(8, 10, 8, 10)));

        JButton fetchButton = AppStyle.createButton("Fetch Booking");
        fetchButton.addActionListener(e -> fetchBooking());

        searchPanel.add(new JLabel("Enter PNR: "), BorderLayout.WEST);
        searchPanel.add(pnrField, BorderLayout.CENTER);
        searchPanel.add(fetchButton, BorderLayout.EAST);

        detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        detailsArea.setFont(AppStyle.BODY);
        detailsArea.setBackground(AppStyle.CARD);
        detailsArea.setForeground(AppStyle.BROWN);
        detailsArea.setBorder(new EmptyBorder(12, 12, 12, 12));

        JScrollPane detailsScroll = new JScrollPane(detailsArea);
        detailsScroll.setBorder(BorderFactory.createLineBorder(AppStyle.BORDER));

        confirmButton = AppStyle.createButton("Confirm Cancellation");
        confirmButton.setEnabled(false);
        confirmButton.addActionListener(e -> cancelBooking());

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);
        bottom.add(confirmButton);

        page.add(top, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(10, 15));
        center.setOpaque(false);
        center.add(searchPanel, BorderLayout.NORTH);
        center.add(detailsScroll, BorderLayout.CENTER);

        page.add(center, BorderLayout.CENTER);
        page.add(bottom, BorderLayout.SOUTH);

        setContentPane(page);
    }

    private void fetchBooking() {
        String input = pnrField.getText().trim();

        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this, "Please enter a PNR number.");
            return;
        }

        int pnr;

        try {
            pnr = Integer.parseInt(input);
            if (pnr <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    this, "PNR must be a positive number.");
            return;
        }

        String sql =
                "SELECT r.pnr, r.passenger_name, r.train_number, " +
                        "r.source_station, r.destination_station, " +
                        "r.journey_date, r.class_type, t.train_name " +
                        "FROM reservations r " +
                        "LEFT JOIN trains t ON r.train_number = t.train_number " +
                        "WHERE r.pnr = ? AND r.username = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, pnr);
            statement.setString(2, username);

            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) {
                    fetchedPnr = -1;
                    confirmButton.setEnabled(false);
                    detailsArea.setText(
                            "No reservation found for this PNR under your account.");
                    return;
                }

                fetchedPnr = pnr;

                String passengerNames = getPassengerNames(connection, pnr);

                detailsArea.setText(
                        "PNR: " + rs.getInt("pnr") + "\n" +
                                "Passenger(s): " + passengerNames + "\n" +
                                "Train Number: " + rs.getInt("train_number") + "\n" +
                                "Train Name: " + rs.getString("train_name") + "\n" +
                                "From: " + rs.getString("source_station") + "\n" +
                                "To: " + rs.getString("destination_station") + "\n" +
                                "Journey Date: " + rs.getString("journey_date") + "\n" +
                                "Class: " + rs.getString("class_type") + "\n\n" +
                                "This is a local demonstration reservation.");

                confirmButton.setEnabled(true);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not fetch the reservation. Please check the database.",
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private String getPassengerNames(Connection connection, int pnr)
            throws SQLException {

        String sql =
                "SELECT passenger_name FROM booking_passengers WHERE pnr = ?";

        StringBuilder names = new StringBuilder();

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, pnr);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    if (names.length() > 0) {
                        names.append(", ");
                    }
                    names.append(rs.getString("passenger_name"));
                }
            }
        }

        if (names.length() == 0) {
            return "No passenger details recorded";
        }

        return names.toString();
    }

    private void cancelBooking() {
        if (fetchedPnr <= 0) {
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to cancel reservation PNR "
                        + fetchedPnr + "?\nAll passengers under this PNR will be cancelled.",
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        String checkSql =
                "SELECT pnr FROM reservations WHERE pnr = ? AND username = ?";

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);

            try {
                try (PreparedStatement check =
                             connection.prepareStatement(checkSql)) {
                    check.setInt(1, fetchedPnr);
                    check.setString(2, username);

                    try (ResultSet rs = check.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException(
                                    "Reservation not found under your account.");
                        }
                    }
                }

                try (PreparedStatement seats = connection.prepareStatement(
                        "DELETE FROM booking_seats WHERE pnr = ?")) {
                    seats.setInt(1, fetchedPnr);
                    seats.executeUpdate();
                }

                try (PreparedStatement passengers = connection.prepareStatement(
                        "DELETE FROM booking_passengers WHERE pnr = ?")) {
                    passengers.setInt(1, fetchedPnr);
                    passengers.executeUpdate();
                }

                try (PreparedStatement reservation = connection.prepareStatement(
                        "DELETE FROM reservations WHERE pnr = ? AND username = ?")) {
                    reservation.setInt(1, fetchedPnr);
                    reservation.setString(2, username);

                    if (reservation.executeUpdate() != 1) {
                        throw new SQLException("Could not cancel reservation.");
                    }
                }

                connection.commit();

                JOptionPane.showMessageDialog(
                        this, "Reservation cancelled successfully!");

                fetchedPnr = -1;
                confirmButton.setEnabled(false);
                pnrField.setText("");
                detailsArea.setText("");

            } catch (SQLException ex) {
                connection.rollback();
                throw ex;
            } finally {
                connection.setAutoCommit(true);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Cancellation failed: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void goBack() {
        dispose();
        previousFrame.setVisible(true);
    }
}
