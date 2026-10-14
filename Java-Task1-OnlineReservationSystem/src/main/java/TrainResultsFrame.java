
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class TrainResultsFrame extends JFrame {

    public TrainResultsFrame(
            String from,
            String to,
            List<Train> trains,
            String username,
            DashboardFrame dashboard) {

        setTitle("Little Journeys | Train Results");
        setSize(900, 650);
        setMinimumSize(new Dimension(750, 500));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel page = new JPanel(new BorderLayout(0, 15));
        page.setBackground(AppStyle.CREAM);
        page.setBorder(new EmptyBorder(20, 24, 24, 24));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Your Journey Options");
        title.setFont(AppStyle.HEADING);
        title.setForeground(AppStyle.BROWN);

        JButton back = AppStyle.createButton("← Back to Search");
        back.addActionListener(e -> {
            dashboard.setVisible(true);
            dashboard.toFront();
            dispose();
        });

        header.add(title, BorderLayout.WEST);
        header.add(back, BorderLayout.EAST);

        // Route details
        String directMessage = trains.size() == 1
                ? "1 direct train found"
                : trains.size() + " direct trains found";

        JLabel route = new JLabel(
                "Journeys from " + from + " to " + to
                        + "  •  " + directMessage);

        route.setFont(AppStyle.SUBHEADING);
        route.setForeground(AppStyle.DARK_SAGE);

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.add(header);
        top.add(Box.createVerticalStrut(12));
        top.add(route);

        // Results list
        JPanel results = new JPanel();
        results.setLayout(new BoxLayout(results, BoxLayout.Y_AXIS));
        results.setBackground(AppStyle.CREAM);

        // Direct trains
        if (!trains.isEmpty()) {
            JLabel directHeading = new JLabel("Direct Journeys");
            directHeading.setFont(AppStyle.SUBHEADING);
            directHeading.setForeground(AppStyle.BROWN);
            directHeading.setAlignmentX(Component.LEFT_ALIGNMENT);

            results.add(directHeading);
            results.add(Box.createVerticalStrut(12));

            for (Train train : trains) {
                results.add(createTrainCard(train, username));
                results.add(Box.createVerticalStrut(12));
            }
        } else {
            JLabel noDirect = new JLabel(
                    "No direct trains found. Here are other routes to try!");

            noDirect.setFont(AppStyle.BODY);
            noDirect.setForeground(AppStyle.BROWN);
            noDirect.setAlignmentX(Component.LEFT_ALIGNMENT);

            results.add(noDirect);
            results.add(Box.createVerticalStrut(18));
        }

        // Always search for connecting journeys
        try {
            TrainDAO trainDAO = new TrainDAO();

            List<TrainConnection> connections =
                    trainDAO.findConnections(from, to);

            if (!connections.isEmpty()) {
                JLabel connectionHeading =
                        new JLabel("Journeys with One Change");

                connectionHeading.setFont(AppStyle.SUBHEADING);
                connectionHeading.setForeground(AppStyle.BROWN);
                connectionHeading.setAlignmentX(Component.LEFT_ALIGNMENT);

                results.add(connectionHeading);
                results.add(Box.createVerticalStrut(12));

                for (TrainConnection connection : connections) {
                    results.add(createConnectionCard(connection, username));
                    results.add(Box.createVerticalStrut(12));
                }
            } else {
                JLabel none = new JLabel(
                        trains.isEmpty()
                                ? "No connecting demo journeys were found either."
                                : "No connecting demo journeys found for this route.");

                none.setFont(AppStyle.BODY);
                none.setForeground(AppStyle.DARK_SAGE);
                none.setAlignmentX(Component.LEFT_ALIGNMENT);

                results.add(none);
            }

        } catch (SQLException ex) {
            JLabel error = new JLabel(
                    "Could not load connecting journeys. Please try again.");

            error.setFont(AppStyle.BODY);
            error.setForeground(AppStyle.BROWN);
            error.setAlignmentX(Component.LEFT_ALIGNMENT);

            results.add(error);
            ex.printStackTrace();
        }

        JScrollPane scrollPane = new JScrollPane(results);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(AppStyle.CREAM);

        page.add(top, BorderLayout.NORTH);
        page.add(scrollPane, BorderLayout.CENTER);

        setContentPane(page);
    }

    // Card for one direct train
    private JPanel createTrainCard(Train train, String username) {

        JPanel card = new JPanel(new BorderLayout(15, 8));
        card.setBackground(AppStyle.CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppStyle.BORDER),
                new EmptyBorder(18, 18, 18, 18)));

        JPanel details = new JPanel();
        details.setOpaque(false);
        details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));

        JLabel name = new JLabel(train.getTrainName());
        name.setFont(new Font("Serif", Font.BOLD, 21));
        name.setForeground(AppStyle.BROWN);

        JLabel number = new JLabel(
                "Train No. " + train.getTrainNumber());
        number.setFont(AppStyle.BODY);
        number.setForeground(AppStyle.DARK_SAGE);

        JLabel stations = new JLabel(
                train.getSourceStation() + "  →  "
                        + train.getDestinationStation());

        stations.setFont(AppStyle.BUTTON_FONT);
        stations.setForeground(AppStyle.BROWN);

        details.add(name);
        details.add(Box.createVerticalStrut(5));
        details.add(number);
        details.add(Box.createVerticalStrut(12));
        details.add(stations);

        JButton book = AppStyle.createButton("Book Now →");
        book.addActionListener(e -> {
            BookingFrame bookingFrame =
                    new BookingFrame(train, username, this);

            bookingFrame.setVisible(true);
            setVisible(false);
        });

        card.add(details, BorderLayout.CENTER);
        card.add(book, BorderLayout.EAST);

        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 125));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        return card;
    }

    // Card for a journey using two trains
    private JPanel createConnectionCard(
            TrainConnection connection, String username) {

        Train first = connection.getFirstTrain();
        Train second = connection.getSecondTrain();

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(AppStyle.CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppStyle.BORDER),
                new EmptyBorder(18, 18, 18, 18)));

        JLabel firstLabel = new JLabel(
                "1. " + first.getTrainName()
                        + " (Train No. " + first.getTrainNumber() + ")");

        firstLabel.setFont(AppStyle.BUTTON_FONT);
        firstLabel.setForeground(AppStyle.BROWN);

        JLabel firstRoute = new JLabel(
                first.getSourceStation() + " → "
                        + first.getDestinationStation());

        firstRoute.setFont(AppStyle.BODY);
        firstRoute.setForeground(AppStyle.DARK_SAGE);

        JLabel change = new JLabel(
                "Change trains at: "
                        + connection.getConnectingStation());

        change.setFont(AppStyle.BUTTON_FONT);
        change.setForeground(AppStyle.DARK_SAGE);

        JLabel secondLabel = new JLabel(
                "2. " + second.getTrainName()
                        + " (Train No. " + second.getTrainNumber() + ")");

        secondLabel.setFont(AppStyle.BUTTON_FONT);
        secondLabel.setForeground(AppStyle.BROWN);

        JLabel secondRoute = new JLabel(
                second.getSourceStation() + " → "
                        + second.getDestinationStation());

        secondRoute.setFont(AppStyle.BODY);
        secondRoute.setForeground(AppStyle.DARK_SAGE);

        JLabel note = new JLabel(
                "Demo connection only — check schedules and transfer feasibility.");

        note.setFont(new Font("SansSerif", Font.ITALIC, 11));
        note.setForeground(AppStyle.SOFT_BROWN);

        JButton bookFirst = AppStyle.createButton("Book First Train");
        bookFirst.addActionListener(e -> {
            BookingFrame bookingFrame =
                    new BookingFrame(first, username, this);

            bookingFrame.setVisible(true);
            setVisible(false);
        });

        JButton bookSecond = AppStyle.createButton("Book Second Train");
        bookSecond.addActionListener(e -> {
            BookingFrame bookingFrame =
                    new BookingFrame(second, username, this);

            bookingFrame.setVisible(true);
            setVisible(false);
        });

        JPanel buttons = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttons.setOpaque(false);
        buttons.add(bookFirst);
        buttons.add(bookSecond);

        card.add(firstLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(firstRoute);
        card.add(Box.createVerticalStrut(12));
        card.add(change);
        card.add(Box.createVerticalStrut(12));
        card.add(secondLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(secondRoute);
        card.add(Box.createVerticalStrut(12));
        card.add(note);
        card.add(Box.createVerticalStrut(12));
        card.add(buttons);

        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        return card;
    }
}