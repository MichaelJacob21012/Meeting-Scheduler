package connect;

import structure.Account;
import structure.Location;
import structure.Meeting;
import org.apache.commons.dbutils.DbUtils;
import structure.Room;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * This class is used for database interaction with the meeting entity.
 */
public class MeetingConnection {

    /**
     * Find all meetings organised by a particular account
     * @param accountID
     * @return
     */
    public static ArrayList<Meeting> meetingsByAccountID(int accountID){
        ArrayList<Meeting> meetings = new ArrayList<>();
        Connect connect = new Connect();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connect.getConnection();
            statement = connection.prepareStatement("SELECT * FROM meeting WHERE accountID = ?");
            statement.setInt(1, accountID);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                meetings.add(resultSetToMeeting(resultSet));
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
        return meetings;
    }

    /**
     * Get a meeting that matches an ID
     * @param meetingID
     * @return
     */
    public static Meeting findMeeting(int meetingID) {
        Connect connect = new Connect();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Meeting meeting = null;
        try {
            connection = connect.getConnection();
            statement = connection.prepareStatement("SELECT * FROM meeting WHERE meetingID = ?");
            statement.setInt(1, meetingID);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                meeting = resultSetToMeeting(resultSet);
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
        return meeting;
    }


    /**
     * Find all meetings scheduled to take place in a particular room
     * @param roomID
     * @return
     */
    public static ArrayList<Meeting> meetingsByRoomID(int roomID) {
        Connect connect = new Connect();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        ArrayList<Meeting> meetings = new ArrayList<>();
        try {
            connection = connect.getConnection();
            statement = connection.prepareStatement("SELECT * FROM meeting WHERE " +
                    "roomID = ? AND booked = TRUE");
            statement.setInt(1, roomID);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                meetings.add((resultSetToMeeting(resultSet)));
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
        return meetings;
    }

    /**
     * Add a meeting to the database
     * @param meeting
     * @return the ID of the inserted meeting
     */
    public static int addMeeting(Meeting meeting) {
        Connect connect = new Connect();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connect.getConnection();
            statement = connection.prepareStatement("INSERT INTO " +
                    "meeting VALUES (" + "DEFAULT ,?,?,?,?,?,?,?,? ,? ,?)", Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, meeting.getAccount().getAccountID());
            statement.setString(2, meeting.getTitle());
            statement.setInt(3, meeting.getNumberOfPeople());
            statement.setTimestamp(4, meeting.getTimestamp());
            statement.setTime(5, meeting.getDurationTime());
            statement.setInt(6, meeting.getLocation().getLocationID());
            statement.setInt(7, meeting.getRoom().getRoomID());
            statement.setBoolean(8, meeting.isBooked());
            statement.setBoolean(9, meeting.isBookingFailed());
            statement.setString(10, meeting.getDescription());
            statement.executeUpdate();
            resultSet = statement.getGeneratedKeys();
            if(resultSet.next()){
                return resultSet.getInt(1);
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
        return -1;
    }

    /**
     * Delete a meeting from the database
     * @param meetingID
     */
    public static void deleteMeeting(int meetingID) {
        Connect connect = new Connect();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connect.getConnection();
            statement = connection.prepareStatement("DELETE FROM meeting WHERE meetingID = ?");
            statement.setInt(1, meetingID);
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
     * Edit a meeting in the database
     * @param meeting
     */
    public static void editMeeting(Meeting meeting) {
        Connect connect = new Connect();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connect.getConnection();
            statement = connection.prepareStatement("UPDATE meeting " +
                    "SET title = ?, numberOfPeople = ?, meetingTime = ?, meetingDuration = ?, " +
                    "locationID = ?, roomID = ?, booked = ?, bookingFailed = ?, meetingDescription = ? " +
                    "WHERE meetingID = ?");
            statement.setString(1, meeting.getTitle());
            statement.setInt(2, meeting.getNumberOfPeople());
            statement.setTimestamp(3, meeting.getTimestamp());
            statement.setTime(4, meeting.getDurationTime());
            statement.setInt(5, meeting.getLocation().getLocationID());
            statement.setInt(6, meeting.getRoom().getRoomID());
            statement.setBoolean(7, meeting.isBooked());
            statement.setBoolean(8, meeting.isBookingFailed());
            statement.setString(9, meeting.getDescription());
            statement.setInt(10, meeting.getMeetingID());
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
     * Find all meetings taking place at a given location on a given date
     * @param locationID
     * @param date
     * @return
     */
    public static ArrayList<Meeting> findAllByLocationAndDate(int locationID, LocalDate date) {
        Connect connect = new Connect();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        ArrayList<Meeting> meetings = new ArrayList<>();
        try {
            connection = connect.getConnection();
            statement = connection.prepareStatement("SELECT * FROM meeting WHERE CAST(meetingTime AS DATE) = ?" +
                    "AND locationID = ? AND booked = TRUE ORDER BY meetingTime ASC ");
            statement.setDate(1, Date.valueOf(date));
            statement.setInt(2, locationID);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                meetings.add((resultSetToMeeting(resultSet)));
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
        return meetings;
    }

    /**
     * Find all meetings taking place in a given room on a given date
     * @param roomID
     * @param date
     * @return
     */
    public static ArrayList<Meeting> findAllByRoomAndDate(int roomID, LocalDate date) {
        Connect connect = new Connect();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        ArrayList<Meeting> meetings = new ArrayList<>();
        try {
            connection = connect.getConnection();
            statement = connection.prepareStatement("SELECT * FROM meeting WHERE CAST(meetingTime AS DATE) = ?" +
                    "AND roomID = ? AND booked = TRUE ORDER BY meetingTime ASC");
            statement.setDate(1, Date.valueOf(date));
            statement.setInt(2, roomID);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                meetings.add((resultSetToMeeting(resultSet)));
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
        return meetings;
    }

    /**
     * Find all meetings a particular account is an attendee for
     * @param accountID
     * @return
     */
    public static ArrayList<Meeting> attendeeMeetings(int accountID) {
        ArrayList<Meeting> meetings = new ArrayList<>();
        Connect connect = new Connect();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connect.getConnection();
            statement = connection.prepareStatement("SELECT * FROM meeting" +
                    " NATURAL JOIN attendee" +
                    " JOIN accountAttendee ON attendee.attendeeID = accountAttendee.attendeeID " +
                    "WHERE accountAttendee.accountID = ?");
            statement.setInt(1, accountID);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                meetings.add(resultSetToMeeting(resultSet));
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
        return meetings;
    }

    /**
     * Find all meetings a particular account is an attendee for on a given date
     * @param accountID
     * @param date
     * @return
     */
    public static ArrayList<Meeting> attendeeMeetingsOnDate(int accountID, LocalDate date) {
        ArrayList<Meeting> meetings = new ArrayList<>();
        Connect connect = new Connect();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connect.getConnection();
            statement = connection.prepareStatement("SELECT * FROM meeting" +
                    " NATURAL JOIN attendee" +
                    " JOIN accountAttendee ON attendee.attendeeID = accountAttendee.attendeeID " +
                    "WHERE accountAttendee.accountID = ? AND " +
                    "CAST(meeting.meetingTime AS DATE) = ? AND meeting.booked = TRUE");
            statement.setInt(1, accountID);
            statement.setDate(2, Date.valueOf(date));
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                meetings.add(resultSetToMeeting(resultSet));
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
        return meetings;
    }

    /**
     * Find all currently unscheduled meetings in the database
     * @return
     */
    public static ArrayList<Meeting> unscheduledMeetings() {
        ArrayList<Meeting> meetings = new ArrayList<>();
        Connect connect = new Connect();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connect.getConnection();
            statement = connection.prepareStatement("SELECT * FROM meeting " +
                    "JOIN meetingPreferences " +
                    "ON meeting.meetingID = meetingPreferences.meetingID " +
                    "WHERE booked = FALSE AND bookingFailed = FALSE");
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                meetings.add(resultSetToMeeting(resultSet));
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
        return meetings;
    }

    /**
     * Convert a result set row to a meeting
     * @param resultSet
     * @return
     * @throws SQLException
     */
    private static Meeting resultSetToMeeting(ResultSet resultSet) throws SQLException {
        int meetingID = resultSet.getInt(1);
        int accountID = resultSet.getInt(2);
        Account account = AccountConnection.findAccount(accountID);
        String title = resultSet.getString(3);
        int numberOfPeople = resultSet.getInt(4);
        Timestamp timestamp = resultSet.getTimestamp(5);
        Time duration = resultSet.getTime(6);
        int locationID = resultSet.getInt(7);
        Location location = LocationConnection.findLocation(locationID);
        int roomID = resultSet.getInt(8);
        Room room = RoomConnection.roomByID(roomID);
        boolean booked = resultSet.getBoolean(9);
        boolean bookingFailed = resultSet.getBoolean(10);
        String description = resultSet.getString(11);
        return new Meeting(meetingID,account, title,numberOfPeople,timestamp, duration, location,room,
                booked, bookingFailed, description);
    }

}
