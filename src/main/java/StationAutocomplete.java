
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StationAutocomplete {

    public static void attach(JTextField field) {

        JPopupMenu popup = new JPopupMenu();
        JList<String> suggestions = new JList<>();
        JScrollPane scrollPane = new JScrollPane(suggestions);

        scrollPane.setBorder(null);
        scrollPane.setPreferredSize(new Dimension(240, 120));
        popup.add(scrollPane);

        suggestions.setFont(field.getFont());
        suggestions.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);

        // Fill the text field when a suggestion is selected
        Runnable selectSuggestion = () -> {
            String selected = suggestions.getSelectedValue();

            if (selected != null) {
                field.setText(selected);
                popup.setVisible(false);
            }
        };

        suggestions.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    selectSuggestion.run();
                }
            }
        });

        field.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {

                if (!popup.isVisible()) {
                    return;
                }

                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    int next = Math.min(
                            suggestions.getSelectedIndex() + 1,
                            suggestions.getModel().getSize() - 1);

                    suggestions.setSelectedIndex(next);
                    e.consume();

                } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                    int previous = Math.max(
                            suggestions.getSelectedIndex() - 1, 0);

                    suggestions.setSelectedIndex(previous);
                    e.consume();

                } else if (e.getKeyCode() == KeyEvent.VK_ENTER
                        && suggestions.getSelectedIndex() >= 0) {

                    selectSuggestion.run();
                    e.consume();

                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    popup.setVisible(false);
                }
            }
        });

        field.getDocument().addDocumentListener(
                new DocumentListener() {

                    private void updateSuggestions() {
                        String input = field.getText().trim();
                        System.out.println("Autocomplete input: " + input);

                        if (input.isEmpty()) {
                            popup.setVisible(false);
                            return;
                        }

                        List<String> matches =
                                findStations(input);
                        System.out.println("Matching stations: " + matches);

                        if (matches.isEmpty()
                                || !field.isShowing()
                                || !field.hasFocus()) {
                            popup.setVisible(false);
                            return;
                        }

                        suggestions.setListData(
                                matches.toArray(new String[0]));

                        suggestions.setSelectedIndex(0);

                        int height = Math.min(
                                matches.size() * 24, 160);

                        scrollPane.setPreferredSize(
                                new Dimension(
                                        Math.max(field.getWidth(), 240),
                                        height));

                        if (!popup.isVisible()) {
                            popup.show(
                                    field, 0, field.getHeight());
                        }

                        popup.pack();
                        popup.setPopupSize(
                                Math.max(field.getWidth(), 240),
                                height);
                    }

                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        SwingUtilities.invokeLater(
                                this::updateSuggestions);
                    }

                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        SwingUtilities.invokeLater(
                                this::updateSuggestions);
                    }

                    @Override
                    public void changedUpdate(DocumentEvent e) {
                        SwingUtilities.invokeLater(
                                this::updateSuggestions);
                    }
                });

        popup.setFocusable(false);

        suggestions.setFocusable(false);

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                SwingUtilities.invokeLater(() -> {
                    if (!popup.isShowing()) {
                        return;
                    }

                    Component focusOwner =
                            KeyboardFocusManager
                                    .getCurrentKeyboardFocusManager()
                                    .getFocusOwner();

                    if (focusOwner != suggestions
                            && (focusOwner == null
                            || !SwingUtilities.isDescendingFrom(
                            focusOwner, popup))) {
                        popup.setVisible(false);
                    }
                });
            }
        });
    }

    private static List<String> findStations(String input) {

        List<String> stations = new ArrayList<>();

        String sql =
                "SELECT station_name FROM stations " +
                        "WHERE LOWER(station_name) LIKE LOWER(?) " +
                        "ORDER BY station_name LIMIT 10";

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, input.trim() + "%");

            try (ResultSet result = statement.executeQuery()) {

                while (result.next()) {
                    stations.add(
                            result.getString("station_name"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return stations;
    }
}

