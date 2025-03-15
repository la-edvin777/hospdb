/**
 * Configuration class for database connection settings in the Hospital Management System.
 * Provides centralized management of database connection parameters and connection creation.
 * This class follows the Singleton pattern to ensure consistent database configuration.
 */
package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConfig {
    /** Database host address */
    private static final String HOST = "localhost";
    
    /** Database port number */
    private static final String PORT = "3306";
    
    /** Database name */
    private static final String DATABASE = "hospital";
    
    /** Database username */
    private static final String USERNAME = "root";
    
    /** Database password */
    private static final String PASSWORD = "root";

    /**
     * Private constructor to prevent instantiation.
     * This class should only be used through its static methods.
     */
    private DatabaseConfig() {}

    /**
     * Gets the complete database URL including all necessary parameters.
     * @return The formatted database URL string
     */
    public static String getUrl() {
        return String.format("jdbc:mariadb://%s:%s/%s?allowLoadLocalInfile=true&allowPublicKeyRetrieval=true&useSSL=false&local_infile=1", 
            HOST, PORT, DATABASE);
    }

    /**
     * Gets the database URL without specifying a database name.
     * Useful for initial database creation and management.
     * @return The formatted database URL string without database name
     */
    public static String getUrlWithoutDatabase() {
        return String.format("jdbc:mariadb://%s:%s?allowLoadLocalInfile=true&allowPublicKeyRetrieval=true&useSSL=false&local_infile=1", 
            HOST, PORT);
    }

    /**
     * Gets the configured database username.
     * @return The database username
     */
    public static String getUsername() {
        return USERNAME;
    }

    /**
     * Gets the configured database password.
     * @return The database password
     */
    public static String getPassword() {
        return PASSWORD;
    }

    /**
     * Gets the configured database name.
     * @return The database name
     */
    public static String getDatabaseName() {
        return DATABASE;
    }

    /**
     * Creates and configures database connection properties.
     * Includes all necessary settings for secure and efficient database operations.
     * @return Properties object containing all database connection settings
     */
    public static Properties getProperties() {
        Properties props = new Properties();
        props.setProperty("user", USERNAME);
        props.setProperty("password", PASSWORD);
        props.setProperty("allowLoadLocalInfile", "true");
        props.setProperty("allowPublicKeyRetrieval", "true");
        props.setProperty("useSSL", "false");
        props.setProperty("local_infile", "1");
        props.setProperty("allowLocalInfile", "true");
        return props;
    }
    
    /**
     * Creates and returns a new database connection using the configured settings.
     * This method should be used for all database operations in the application.
     * 
     * @return A new Connection object to the database
     * @throws SQLException if a database access error occurs or the URL is null
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(getUrl(), getProperties());
    }
} 