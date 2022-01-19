package connect;

import org.apache.commons.dbcp.BasicDataSource;

import java.sql.Connection;
import java.sql.SQLException;


/**
 * This class is for connecting to the database
 */
class Connect {
    private static final String DB_NAME = "individual_project";
    private static final String DB_USER = "root";
    private static final String PASSWORD = "";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/";
    private static final String TIMEZONE = "Europe/London";
    private final static BasicDataSource dataSource = new BasicDataSource();

    static {
        dataSource.setPassword(PASSWORD);
        dataSource.setUsername(DB_USER);
        dataSource.setUrl(DB_URL + DB_NAME + "?serverTimezone=" + TIMEZONE);
    }

    /**
     * Get the connection to the database
     * @return
     * @throws SQLException
     */
    public Connection getConnection () throws SQLException {
        return dataSource.getConnection();
    }
}