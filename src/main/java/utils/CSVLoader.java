/**
 * Utility class for loading CSV data into the Hospital Management System database.
 * Handles the bulk import of data from CSV files into corresponding database tables.
 * Supports error handling and provides feedback on the loading process.
 */
package utils;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

public class CSVLoader {
    /** Database connection for loading data */
    private final Connection connection;
    
    /** Directory containing the CSV files */
    private final String dataDirectory;
    
    private static final SimpleDateFormat CSV_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy", Locale.UK);
    private static final SimpleDateFormat SQL_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
    
    /**
     * Creates a new CSVLoader with the specified database connection.
     * Uses the default "data" directory for CSV files.
     * 
     * @param connection The database connection to use for loading data
     */
    public CSVLoader(Connection connection) {
        this(connection, "data");  // Default to "data" directory
    }
    
    /**
     * Creates a new CSVLoader with the specified connection and data directory.
     * 
     * @param connection The database connection to use for loading data
     * @param dataDirectory The directory containing the CSV files
     */
    public CSVLoader(Connection connection, String dataDirectory) {
        this.connection = connection;
        this.dataDirectory = dataDirectory;
    }
    
    /**
     * Loads all data from CSV files into the database.
     * 
     * @throws SQLException if there's an error loading the data
     */
    public void loadAllData() throws SQLException {
        loadAllData(connection, dataDirectory);
    }
    
    /**
     * Static method to load all data from CSV files into the database.
     * Processes each table's data with appropriate error handling.
     * 
     * @param conn The database connection
     * @param dataDirectory The directory containing the CSV files
     * @throws SQLException if there's an error loading the data
     */
    public static void loadAllData(Connection conn, String dataDirectory) throws SQLException {
        // Configuration for each table's CSV structure
        String[] tableConfigs = {
            "Doctor,doctorID,firstname,surname,address,email,specialization",
            "Insurance,insuranceID,company,address,phone",
            "Patient,patientID,firstname,surname,postcode,address,phone,email,insuranceID",
            "Drug,drugID,name,sideeffects,benefits",
            "Visit,patientID,doctorID,dateofvisit,symptoms,diagnosis",
            "Prescription,prescriptionID,dateprescribed,dosage,duration,comment,drugID,doctorID,patientID"
        };

        // Process each table configuration
        for (String config : tableConfigs) {
            String[] parts = config.split(",", 2);
            String tableName = parts[0];
            String columns = parts[1];
            
            try {
                loadTable(conn, dataDirectory + "/" + tableName + ".csv", tableName,
                    "(" + String.join(",", columns.split(",")) + ")");
                System.out.println("Successfully loaded " + tableName + " data");
            } catch (SQLException e) {
                System.err.println("Error loading " + tableName + " data: " + e.getMessage());
                throw e; // Re-throw to handle in the GUI
            }
        }
    }

    /**
     * Loads data from a single CSV file into the specified table.
     * Performs file existence and permission checks before loading.
     * 
     * @param conn The database connection
     * @param csvFile The path to the CSV file
     * @param tableName The name of the target database table
     * @param columns The column specification for the LOAD DATA command
     * @throws SQLException if there's an error loading the data
     */
    private static void loadTable(Connection conn, String csvFile, String tableName, String columns) 
            throws SQLException {
        // Verify file existence and permissions
        File file = new File(csvFile);
        if (!file.exists()) {
            throw new SQLException("CSV file not found: " + csvFile);
        }
        if (!file.canRead()) {
            throw new SQLException("Cannot read CSV file: " + csvFile + ". Please check file permissions.");
        }

        // Convert file path to proper format for SQL
        String absolutePath = file.getAbsolutePath().replace('\\', '/');

        // Construct and execute LOAD DATA command
        String sql = String.format(
            "LOAD DATA LOCAL INFILE '%s' " +
            "INTO TABLE %s " +
            "FIELDS TERMINATED BY ',' " +
            "ENCLOSED BY '\"' " +
            "LINES TERMINATED BY '\r\n' " +
            "IGNORE 1 LINES %s",
            absolutePath, tableName, columns);

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new SQLException("Error loading " + tableName + ": " + e.getMessage() + 
                "\nMake sure MariaDB is configured to allow local infile loading", e);
        }
    }
} 