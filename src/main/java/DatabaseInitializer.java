
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void main(String[] args) {

        System.out.println("DATABASE INITIALIZER STARTED");

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {

            // 1. Users table
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS users (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "username TEXT NOT NULL UNIQUE, " +
                            "password TEXT NOT NULL)"
            );

            // 2. Trains table
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS trains (" +
                            "train_number INTEGER PRIMARY KEY, " +
                            "train_name TEXT NOT NULL, " +
                            "source_station TEXT NOT NULL, " +
                            "destination_station TEXT NOT NULL)"
            );

            // 3. Reservations table
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS reservations (" +
                            "pnr INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "passenger_name TEXT NOT NULL, " +
                            "train_number INTEGER NOT NULL, " +
                            "class_type TEXT NOT NULL, " +
                            "journey_date TEXT NOT NULL, " +
                            "source_station TEXT NOT NULL, " +
                            "destination_station TEXT NOT NULL, " +
                            "FOREIGN KEY (train_number) " +
                            "REFERENCES trains(train_number))"
            );

            // 4. Add username column if it is missing
            boolean usernameColumnExists = false;

            try (ResultSet columns =
                         statement.executeQuery(
                                 "PRAGMA table_info(reservations)")) {

                while (columns.next()) {
                    if ("username".equalsIgnoreCase(
                            columns.getString("name"))) {
                        usernameColumnExists = true;
                        break;
                    }
                }
            }

            if (!usernameColumnExists) {
                statement.execute(
                        "ALTER TABLE reservations ADD COLUMN username TEXT"
                );
                System.out.println(
                        "Added username column to reservations.");
            }

            // 5. Booking passengers table
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS booking_passengers (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "pnr INTEGER NOT NULL, " +
                            "passenger_name TEXT NOT NULL, " +
                            "FOREIGN KEY (pnr) REFERENCES reservations(pnr))"
            );

            // 6. Stations table
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS stations (" +
                            "station_code TEXT PRIMARY KEY, " +
                            "station_name TEXT NOT NULL UNIQUE)"
            );

            // 7. Station data
            // Pune is displayed as "Pune", not "Pune Junction".
            String[][] stations = {
                    {"CSMT", "Mumbai CSMT"},
                    {"MMCT", "Mumbai Central"},
                    {"PUNE", "Pune"},
                    {"NGP", "Nagpur Junction"},
                    {"NK", "Nashik Road"},
                    {"ADI", "Ahmedabad Junction"},
                    {"NDLS", "New Delhi"},
                    {"BDTS", "Bandra Terminus"},
                    {"DR", "Dadar"},
                    {"LTT", "Lokmanya Tilak Terminus"},
                    {"SBC", "KSR Bengaluru"},
                    {"MAS", "Chennai Central"},
                    {"HYB", "Hyderabad Deccan"},
                    {"HWH", "Howrah Junction"},
                    {"JP", "Jaipur Junction"},
                    {"BPL", "Bhopal Junction"},
                    {"LKO", "Lucknow Charbagh"},
                    {"MAO", "Madgaon"},
                    {"SUR", "Solapur"},
                    {"TVC", "Thiruvananthapuram Central"},
                    {"SURAT", "Surat"},
                    {"VAD", "Vadodara Junction"},
                    {"RJT", "Rajkot"},
                    {"AGC", "Agra Cantt"},
                    {"ASR", "Amritsar Junction"},
                    {"PNBE", "Patna Junction"},
                    {"BBS", "Bhubaneswar"},
                    {"ERS", "Ernakulam Junction"},
                    {"MYS", "Mysuru Junction"},
                    {"KOTA", "Kota Junction"},
                    {"JBP", "Jabalpur"},
                    {"GWL", "Gwalior Junction"},
                    {"BSB", "Varanasi Junction"},
                    {"ALD", "Prayagraj Junction"}
            };

            String stationSql =
                    "INSERT OR IGNORE INTO stations " +
                            "(station_code, station_name) VALUES (?, ?)";

            int stationsInserted = 0;

            try (PreparedStatement ps =
                         connection.prepareStatement(stationSql)) {

                for (String[] station : stations) {
                    ps.setString(1, station[0]);
                    ps.setString(2, station[1]);
                    stationsInserted += ps.executeUpdate();
                }
            }

            // Standardize an existing Pune station record.
            // Update the station table before inserting new train data.
            try (PreparedStatement ps =
                         connection.prepareStatement(
                                 "UPDATE stations SET station_name = ? " +
                                         "WHERE station_code = ?")) {
                ps.setString(1, "Pune");
                ps.setString(2, "PUNE");
                ps.executeUpdate();
            }

            // 8. Demo train data
            // The routes are demonstration data, not live schedules.
            // Existing train numbers are retained.

            String[][] trains = {

                    // Mumbai <-> Pune
                    {"11007", "Deccan Express",
                            "Mumbai CSMT", "Pune"},
                    {"12123", "Deccan Queen",
                            "Mumbai CSMT", "Pune"},
                    {"12125", "Pragati Express",
                            "Mumbai CSMT", "Pune"},
                    {"11008", "Deccan Express",
                            "Pune", "Mumbai CSMT"},
                    {"12124", "Deccan Queen",
                            "Pune", "Mumbai CSMT"},
                    {"12126", "Pragati Express",
                            "Pune", "Mumbai CSMT"},

                    // Mumbai <-> Delhi
                    {"12951", "Mumbai Rajdhani Express",
                            "Mumbai Central", "New Delhi"},
                    {"12952", "Mumbai Rajdhani Express",
                            "New Delhi", "Mumbai Central"},
                    {"12903", "Golden Temple Mail",
                            "Mumbai Central", "New Delhi"},
                    {"12904", "Golden Temple Mail",
                            "New Delhi", "Mumbai Central"},
                    {"12137", "Punjab Mail",
                            "Mumbai CSMT", "New Delhi"},
                    {"12138", "Punjab Mail",
                            "New Delhi", "Mumbai CSMT"},

                    // Mumbai <-> Ahmedabad
                    {"12009", "Shatabdi Express",
                            "Mumbai Central", "Ahmedabad"},
                    {"12010", "Shatabdi Express",
                            "Ahmedabad", "Mumbai Central"},

                    // Mumbai <-> Nagpur
                    {"11039", "Maharashtra Express",
                            "Mumbai CSMT", "Nagpur Junction"},
                    {"11040", "Maharashtra Express",
                            "Nagpur Junction", "Mumbai CSMT"},

                    // Mumbai <-> Jaipur
                    {"12977", "Marusagar Express",
                            "Mumbai Central", "Jaipur Junction"},
                    {"12978", "Marusagar Express",
                            "Jaipur Junction", "Mumbai Central"},

                    // Mumbai <-> Chennai
                    {"11005", "Puducherry Express",
                            "Mumbai CSMT", "Chennai Central"},
                    {"11006", "Puducherry Express",
                            "Chennai Central", "Mumbai CSMT"},

                    // Mumbai <-> Goa
                    {"10103", "Mandovi Express",
                            "Mumbai CSMT", "Madgaon"},
                    {"10104", "Mandovi Express",
                            "Madgaon", "Mumbai CSMT"},

                    // Mumbai <-> Bhopal
                    {"12155", "Bhopal Express",
                            "Mumbai CSMT", "Bhopal Junction"},
                    {"12156", "Bhopal Express",
                            "Bhopal Junction", "Mumbai CSMT"},

                    // Mumbai <-> Howrah
                    {"12809", "Howrah Mail",
                            "Mumbai CSMT", "Howrah Junction"},
                    {"12810", "Howrah Mail",
                            "Howrah Junction", "Mumbai CSMT"},

                    // Mumbai <-> Lucknow
                    {"22109", "LTT Ballia Express",
                            "Lokmanya Tilak Terminus",
                            "Lucknow Charbagh"},
                    {"22110", "LTT Ballia Express",
                            "Lucknow Charbagh",
                            "Lokmanya Tilak Terminus"},

                    // Mumbai <-> Thiruvananthapuram
                    {"16331", "Mumbai Express",
                            "Mumbai CSMT",
                            "Thiruvananthapuram Central"},
                    {"16332", "Mumbai Express",
                            "Thiruvananthapuram Central",
                            "Mumbai CSMT"},

                    // Mumbai <-> Solapur
                    {"12115", "Siddheshwar Express",
                            "Mumbai CSMT", "Solapur"},
                    {"12116", "Siddheshwar Express",
                            "Solapur", "Mumbai CSMT"},

                    // Existing demo train numbers retained
                    {"12627", "Karnataka Express",
                            "New Delhi", "KSR Bengaluru"},
                    {"12628", "Karnataka Express",
                            "KSR Bengaluru", "New Delhi"},
                    {"12621", "Tamil Nadu Express",
                            "New Delhi", "Chennai Central"},
                    {"12622", "Tamil Nadu Express",
                            "Chennai Central", "New Delhi"},
                    {"12723", "Telangana Express",
                            "New Delhi", "Hyderabad Deccan"},
                    {"12724", "Telangana Express",
                            "Hyderabad Deccan", "New Delhi"},

                    // Additional demo routes
                    {"70001", "Little Journeys Western Express",
                            "Mumbai CSMT", "Surat"},
                    {"70002", "Little Journeys Western Express",
                            "Surat", "Mumbai CSMT"},

                    {"70003", "Little Journeys Gujarat Express",
                            "Mumbai Central", "Vadodara Junction"},
                    {"70004", "Little Journeys Gujarat Express",
                            "Vadodara Junction", "Mumbai Central"},

                    {"70005", "Little Journeys Goa Express",
                            "Mumbai CSMT", "Madgaon"},
                    {"70006", "Little Journeys Goa Express",
                            "Madgaon", "Mumbai CSMT"},

                    {"70007", "Little Journeys Desert Express",
                            "Jaipur Junction", "Agra Cantt"},
                    {"70008", "Little Journeys Desert Express",
                            "Agra Cantt", "Jaipur Junction"},

                    {"70009", "Little Journeys Capital Express",
                            "New Delhi", "Jaipur Junction"},
                    {"70010", "Little Journeys Capital Express",
                            "Jaipur Junction", "New Delhi"},

                    {"70011", "Little Journeys North Express",
                            "New Delhi", "Amritsar Junction"},
                    {"70012", "Little Journeys North Express",
                            "Amritsar Junction", "New Delhi"},

                    {"70013", "Little Journeys East Express",
                            "Howrah Junction", "Bhubaneswar"},
                    {"70014", "Little Journeys East Express",
                            "Bhubaneswar", "Howrah Junction"},

                    {"70015", "Little Journeys Bihar Express",
                            "Howrah Junction", "Patna Junction"},
                    {"70016", "Little Journeys Bihar Express",
                            "Patna Junction", "Howrah Junction"},

                    {"70017", "Little Journeys Temple Express",
                            "Varanasi Junction", "Prayagraj Junction"},
                    {"70018", "Little Journeys Temple Express",
                            "Prayagraj Junction", "Varanasi Junction"},

                    {"70019", "Little Journeys Central Express",
                            "Bhopal Junction", "Jabalpur"},
                    {"70020", "Little Journeys Central Express",
                            "Jabalpur", "Bhopal Junction"},

                    {"70021", "Little Journeys South Express",
                            "KSR Bengaluru", "Mysuru Junction"},
                    {"70022", "Little Journeys South Express",
                            "Mysuru Junction", "KSR Bengaluru"},

                    {"70023", "Little Journeys Coastal Express",
                            "Chennai Central", "Ernakulam Junction"},
                    {"70024", "Little Journeys Coastal Express",
                            "Ernakulam Junction", "Chennai Central"},

                    {"70025", "Little Journeys Deccan Express",
                            "Nagpur Junction", "Bhopal Junction"},
                    {"70026", "Little Journeys Deccan Express",
                            "Bhopal Junction", "Nagpur Junction"},

                    {"70027", "Little Journeys Heritage Express",
                            "Agra Cantt", "Gwalior Junction"},
                    {"70028", "Little Journeys Heritage Express",
                            "Gwalior Junction", "Agra Cantt"},

                    {"70029", "Little Journeys City Express",
                            "Surat", "Vadodara Junction"},
                    {"70030", "Little Journeys City Express",
                            "Vadodara Junction", "Surat"},

                    // Routes designed to provide connecting journeys
                    // Pune -> Surat -> Mysuru
                    {"70031", "Little Journeys Pune Connector",
                            "Pune", "Surat"},
                    {"70032", "Little Journeys Mysuru Connector",
                            "Surat", "Mysuru Junction"},

                    // Pune -> Mumbai Central -> Ahmedabad
                    {"70033", "Little Journeys Mumbai Connector",
                            "Pune", "Mumbai Central"},
                    {"70034", "Little Journeys Ahmedabad Connector",
                            "Mumbai Central", "Ahmedabad"},

                    // Pune -> Nagpur -> Bhopal
                    {"70035", "Little Journeys Nagpur Connector",
                            "Pune", "Nagpur Junction"},
                    {"70036", "Little Journeys Bhopal Connector",
                            "Nagpur Junction", "Bhopal Junction"},

                    // Pune -> Solapur -> Hyderabad
                    {"70037", "Little Journeys Solapur Connector",
                            "Pune", "Solapur"},
                    {"70038", "Little Journeys Hyderabad Connector",
                            "Solapur", "Hyderabad Deccan"},

                    // Ahmedabad -> Surat -> Mumbai Central
                    {"70039", "Little Journeys Surat Connector",
                            "Ahmedabad", "Surat"},
                    {"70040", "Little Journeys Mumbai Express",
                            "Surat", "Mumbai Central"},

                    // Chennai -> Mysuru -> Bengaluru
                    {"70041", "Little Journeys Mysuru Express",
                            "Chennai Central", "Mysuru Junction"},
                    {"70042", "Little Journeys Bengaluru Connector",
                            "Mysuru Junction", "KSR Bengaluru"},

                    // Delhi -> Jaipur -> Kota
                    {"70043", "Little Journeys Jaipur Connector",
                            "New Delhi", "Jaipur Junction"},
                    {"70044", "Little Journeys Kota Connector",
                            "Jaipur Junction", "Kota Junction"},

                    // Delhi -> Agra -> Gwalior
                    {"70045", "Little Journeys Agra Connector",
                            "New Delhi", "Agra Cantt"},
                    {"70046", "Little Journeys Gwalior Connector",
                            "Agra Cantt", "Gwalior Junction"},

                    // Howrah -> Bhubaneswar -> Chennai
                    {"70047", "Little Journeys Bhubaneswar Connector",
                            "Howrah Junction", "Bhubaneswar"},
                    {"70048", "Little Journeys Chennai Connector",
                            "Bhubaneswar", "Chennai Central"},

                    // Mumbai -> Vadodara -> Rajkot
                    {"70049", "Little Journeys Vadodara Connector",
                            "Mumbai Central", "Vadodara Junction"},
                    {"70050", "Little Journeys Rajkot Connector",
                            "Vadodara Junction", "Rajkot"}
            };

            // 9. Insert trains without duplicating existing numbers
            String trainSql =
                    "INSERT OR IGNORE INTO trains " +
                            "(train_number, train_name, source_station, " +
                            "destination_station) VALUES (?, ?, ?, ?)";

            int trainsInserted = 0;

            try (PreparedStatement ps =
                         connection.prepareStatement(trainSql)) {

                for (String[] train : trains) {
                    ps.setInt(1, Integer.parseInt(train[0]));
                    ps.setString(2, train[1]);
                    ps.setString(3, train[2]);
                    ps.setString(4, train[3]);

                    trainsInserted += ps.executeUpdate();
                }
            }

            // 10. Normalize old Pune Junction route names.
            // This also handles trains already present in the database.
            try (Statement updateStatement =
                         connection.createStatement()) {

                updateStatement.executeUpdate(
                        "UPDATE trains SET source_station = 'Pune' " +
                                "WHERE LOWER(TRIM(source_station)) = " +
                                "'pune junction'"
                );

                updateStatement.executeUpdate(
                        "UPDATE trains SET destination_station = 'Pune' " +
                                "WHERE LOWER(TRIM(destination_station)) = " +
                                "'pune junction'"
                );
            }

            // 11. Summary
            try (ResultSet rs = statement.executeQuery(
                    "SELECT COUNT(*) FROM stations")) {
                if (rs.next()) {
                    System.out.println(
                            "Total stations: " + rs.getInt(1));
                }
            }

            try (ResultSet rs = statement.executeQuery(
                    "SELECT COUNT(*) FROM trains")) {
                if (rs.next()) {
                    System.out.println(
                            "Total trains in database: " + rs.getInt(1));
                }
            }

            System.out.println(
                    "New stations inserted: " + stationsInserted);
            System.out.println(
                    "New trains inserted: " + trainsInserted);
            System.out.println(
                    "Database initialization completed successfully!");

        } catch (SQLException e) {
            System.out.println("Database initialization failed!");
            e.printStackTrace();
        }
    }
}
