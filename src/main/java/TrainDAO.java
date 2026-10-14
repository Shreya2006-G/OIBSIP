
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TrainDAO {

    // Search for direct trains between two stations
    public List<Train> searchTrains(String source, String destination)
            throws SQLException {

        List<Train> trains = new ArrayList<>();

        String sql = "SELECT train_number, train_name, source_station, destination_station " +
                "FROM trains " +
                "WHERE LOWER(TRIM(source_station)) = LOWER(TRIM(?)) " +
                "AND LOWER(TRIM(destination_station)) = LOWER(TRIM(?)) " +
                "ORDER BY train_number";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, source.trim());
            statement.setString(2, destination.trim());

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    Train train = new Train(
                            result.getInt("train_number"),
                            result.getString("train_name"),
                            result.getString("source_station"),
                            result.getString("destination_station")
                    );

                    trains.add(train);
                }
            }
        }

        return trains;
    }


    // Find journeys that require one change of train
    public List<TrainConnection> findConnections(
            String source, String destination) throws SQLException {

        List<TrainConnection> connections = new ArrayList<>();

        String sql =
                "SELECT " +
                        "t1.train_number AS first_number, " +
                        "t1.train_name AS first_name, " +
                        "t1.source_station AS first_source, " +
                        "t1.destination_station AS first_destination, " +
                        "t2.train_number AS second_number, " +
                        "t2.train_name AS second_name, " +
                        "t2.source_station AS second_source, " +
                        "t2.destination_station AS second_destination " +
                        "FROM trains t1 " +
                        "JOIN trains t2 ON " +
                        "LOWER(TRIM(t1.destination_station)) = " +
                        "LOWER(TRIM(t2.source_station)) " +
                        "WHERE LOWER(TRIM(t1.source_station)) = LOWER(TRIM(?)) " +
                        "AND LOWER(TRIM(t2.destination_station)) = LOWER(TRIM(?)) " +
                        "AND t1.train_number <> t2.train_number " +
                        "AND LOWER(TRIM(t1.destination_station)) <> LOWER(TRIM(?)) " +
                        "ORDER BY t1.train_number, t2.train_number";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, source.trim());
            statement.setString(2, destination.trim());
            statement.setString(3, source.trim());

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {

                    Train firstTrain = new Train(
                            result.getInt("first_number"),
                            result.getString("first_name"),
                            result.getString("first_source"),
                            result.getString("first_destination")
                    );

                    Train secondTrain = new Train(
                            result.getInt("second_number"),
                            result.getString("second_name"),
                            result.getString("second_source"),
                            result.getString("second_destination")
                    );

                    TrainConnection connectionOption =
                            new TrainConnection(firstTrain, secondTrain);

                    connections.add(connectionOption);
                }
            }
        }

        return connections;
    }
}