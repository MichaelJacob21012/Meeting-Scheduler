package connect;

import structure.Location;
import structure.MeetingPreferences;
import structure.Room;
import org.apache.commons.dbutils.DbUtils;

import java.sql.*;
import java.time.LocalDate;

/**
 * This class is used for database interaction with the meeting preferences entity.
 */
public class PreferencesConnection {

    /**
     * Add meeting preferences to the database
     * @param preferences
     */
    public static void addPreferences(MeetingPreferences preferences) {
        Connect connect = new Connect();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connect.getConnection();
            statement = connection.prepareStatement("INSERT INTO meetingPreferences VALUES (" +
                    " ?,?,?,?,?,?,?,?,?)");
            statement.setInt(1, preferences.getMeetingID());
            statement.setInt(2, preferences.getMeetingPriority());
            statement.setInt(3, preferences.getLocation().getLocationID());
            statement.setInt(4, preferences.getLocationPriority());
            statement.setInt(5, preferences.getRoom().getRoomID());
            statement.setInt(6, preferences.getRoomPriority());
            statement.setDate(7, Date.valueOf(preferences.getLatestDate()));
            statement.setBoolean(8, preferences.isAMPreferred());
            statement.setInt(9, preferences.getTimePriority());

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

    /**
     * Find the meeting preference associated with a particular meeting
     * @param meetingID
     * @return
     */
    public static MeetingPreferences preferencesByMeetingID(int meetingID) {
        Connect connect = new Connect();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        MeetingPreferences preferences = null;
        try {
            connection = connect.getConnection();
            statement = connection.prepareStatement("SELECT * FROM meetingPreferences" +
                    " WHERE meetingID = ?");
            statement.setInt(1, meetingID);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                preferences = (resultSetToPreferences(resultSet));
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
        return preferences;
    }

    /**
     * Convert a result set row to a meeting preferences object
     * @param resultSet
     * @return
     * @throws SQLException
     */
    private static MeetingPreferences resultSetToPreferences(ResultSet resultSet) throws SQLException {
        int meetingID = resultSet.getInt(1);
        int meetingPriority = resultSet.getInt(2);
        int locationID = resultSet.getInt(3);
        Location location = LocationConnection.findLocation(locationID);
        int locationPriority = resultSet.getInt(4);
        int roomID = resultSet.getInt(5);
        Room room = RoomConnection.roomByID(roomID);
        int roomPriority = resultSet.getInt(6);
        LocalDate latestDate = resultSet.getDate(7).toLocalDate();
        boolean AMPreferred = resultSet.getBoolean(8);
        int timePriority = resultSet.getInt(9);
        return new MeetingPreferences(meetingID,meetingPriority, location,locationPriority,room, roomPriority,
                latestDate,AMPreferred, timePriority);
    }
}
