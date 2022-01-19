package connect;

import org.apache.commons.dbutils.DbUtils;

import java.sql.*;
import java.time.LocalDateTime;

public class DetailsConnection {
    /**
     * Update the last run time of the batch scheduler
     * @param dateTime
     */
    public static void updateLastRun(LocalDateTime dateTime) {
        Connect connect = new Connect();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connect.getConnection();
            statement = connection.prepareStatement("UPDATE details SET " +
                    "batchLastRun = ?");
            statement.setTimestamp(1, Timestamp.valueOf(dateTime));
            statement.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            DbUtils.closeQuietly(connection);
            DbUtils.closeQuietly(statement);
            DbUtils.closeQuietly(resultSet);
        }
    }

    public static LocalDateTime getLastRun() {
        Connect connect = new Connect();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        LocalDateTime batchLastRun = null;
        try {
            connection = connect.getConnection();
            statement = connection.prepareStatement("SELECT batchLastRun FROM details " +
                    "LIMIT 1");
            resultSet = statement.executeQuery();
            if (resultSet.next()){
                batchLastRun =resultSet.getTimestamp(1).toLocalDateTime();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            DbUtils.closeQuietly(connection);
            DbUtils.closeQuietly(statement);
            DbUtils.closeQuietly(resultSet);
        }
        return batchLastRun;
    }
}
