package connect;

import structure.Account;
import structure.AccountAttendee;
import structure.Attendee;
import structure.NoAccountAttendee;
import org.apache.commons.dbutils.DbUtils;

import java.sql.*;
import java.util.ArrayList;

/**
 * This class is used for database interaction with the attendee entity as well as its sub entities,
 * accountAttendee and noAccountAttendee.
 */
public class AttendeeConnection {

    /**
     * Get an attendee that matches an ID
     * @param attendeeID
     * @return
     */
    public static Attendee findAttendee(int attendeeID) {
        if(hasAccount(attendeeID)){
            return findAccountAttendee(attendeeID);
        }
        return findNoAccountAttendee(attendeeID);
    }

    /**
     * Get an account attendee from the attendee ID
     * @param attendeeID
     * @return
     */
    private static AccountAttendee findAccountAttendee(int attendeeID){
        Connect connect = new Connect();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        AccountAttendee accountAttendee = new AccountAttendee();
        try {
            connection = connect.getConnection();
            statement = connection.prepareStatement("SELECT * FROM attendee " +
                    "NATURAL JOIN accountAttendee WHERE attendeeID = ?");
            statement.setInt(1, attendeeID);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                accountAttendee = (resultSetToAccountAttendee(resultSet));
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
        return accountAttendee;
    }

    /**
     * Get a no account attendee from the attendee ID
     * @param attendeeID
     * @return
     */
    private static NoAccountAttendee findNoAccountAttendee(int attendeeID){
        Connect connect = new Connect();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        NoAccountAttendee noAccountAttendee = new NoAccountAttendee();
        try {
            connection = connect.getConnection();
            statement = connection.prepareStatement("SELECT * FROM attendee " +
                    "NATURAL JOIN noAccountAttendee WHERE attendeeID = ?");
            statement.setInt(1, attendeeID);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                noAccountAttendee = (resultSetToNoAccountAttendee(resultSet));
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
        return noAccountAttendee;
    }


    /**
     * Get all attendees with accounts associated with a meeting
     * @param meetingID the ID of the meeting
     * @return The attendees as a list of AccountAttendee
     */
    public static ArrayList<AccountAttendee> accountAttendeesForMeeting(int meetingID) {
        Connect connect = new Connect();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        ArrayList<AccountAttendee> accountAttendees = new ArrayList<>();
        try {
            connection = connect.getConnection();
            statement = connection.prepareStatement("SELECT * FROM attendee " +
                    "NATURAL JOIN accountAttendee WHERE meetingID = ?");
            statement.setInt(1, meetingID);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                accountAttendees.add(resultSetToAccountAttendee(resultSet));
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
        return accountAttendees;
    }

    /**
     * Get all attendees associated with a meeting without accounts
     * @param meetingID the ID of the meeting
     * @return The attendees as a list of NoAccountAttendee
     */
    public static ArrayList<NoAccountAttendee> noAccountAttendeesForMeeting(int meetingID) {
        Connect connect = new Connect();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        ArrayList<NoAccountAttendee> noAccountAttendees = new ArrayList<>();
        try {
            connection = connect.getConnection();
            statement = connection.prepareStatement("SELECT * FROM attendee " +
                    "NATURAL JOIN noAccountAttendee WHERE meetingID = ?");
            statement.setInt(1, meetingID);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                noAccountAttendees.add(resultSetToNoAccountAttendee(resultSet));
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
        return noAccountAttendees;
    }

    /**
     * Find all instances of account attendee associated with an account
     * @param accountID the ID of the account
     * @return
     */
    public static ArrayList<AccountAttendee> accountAttendeesByAccountID(int accountID) {
        Connect connect = new Connect();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        ArrayList<AccountAttendee> accountAttendees = new ArrayList<>();
        try {
            connection = connect.getConnection();
            statement = connection.prepareStatement("SELECT * FROM attendee " +
                    "NATURAL JOIN accountAttendee WHERE accountID = ?");
            statement.setInt(1, accountID);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                accountAttendees.add(resultSetToAccountAttendee(resultSet));
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
        return accountAttendees;
    }

    /**
     * Add every member of a list of attendees to the database
     * @param attendees
     */
    public static void addAllAttendees(ArrayList<Attendee> attendees) {
        for (Attendee attendee: attendees) {
            addAttendee(attendee);
        }
    }

    /**
     * Add a single attendee to the database
     * @param attendee
     */
    public static void addAttendee(Attendee attendee) {
        Connect connect = new Connect();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        int attendeeID = -1;
        try {
            connection = connect.getConnection();
            statement = connection.prepareStatement("INSERT INTO attendee VALUES (" +
                    "DEFAULT ,?,?,0,0,0)", Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, attendee.getMeetingID());
            statement.setBoolean(2, attendee.hasAccount());
            statement.executeUpdate();
            resultSet = statement.getGeneratedKeys();
            if(resultSet.next()){
                attendeeID =  resultSet.getInt(1);
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
        if (attendee.hasAccount()){
            addAccountAttendee((AccountAttendee) attendee, attendeeID);
        }
        else {
            addNoAccountAttendee((NoAccountAttendee)attendee, attendeeID);
        }
    }

    /**
     * Add an attendee without an account to the database
     * @param attendee
     * @param attendeeID
     */
    private static void addNoAccountAttendee(NoAccountAttendee attendee, int attendeeID) {
        Connect connect = new Connect();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connect.getConnection();
            statement = connection.prepareStatement("INSERT INTO noAccountAttendee VALUES (" +
                    "?,?,?)");
            statement.setInt(1,attendeeID);
            statement.setString(2, attendee.getName());
            statement.setString(3, attendee.getEmail());
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
     * Add an attendee with an account to the database
     * @param attendee
     */
    private static void addAccountAttendee(AccountAttendee attendee, int attendeeID) {
        Connect connect = new Connect();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connect.getConnection();
            statement = connection.prepareStatement("INSERT INTO accountAttendee VALUES (" +
                    "?,?)");
            statement.setInt(1,attendeeID);
            statement.setInt(2, attendee.getAccount().getAccountID());
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
     * Set an attendee as intending to attend
     * @param attendeeID
     */
    public static void setAccepted(int attendeeID) {
        Connect connect = new Connect();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connect.getConnection();
            statement = connection.prepareStatement("UPDATE attendee " +
                    "SET accepted = TRUE, rejected = FALSE  WHERE attendeeID = ?");
            statement.setInt(1,attendeeID);
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
     * Set an attendee as not intending to attend
     * @param attendeeID
     */
    public static void setRejected(int attendeeID) {
        Connect connect = new Connect();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connect.getConnection();
            statement = connection.prepareStatement("UPDATE attendee " +
                    "SET accepted = FALSE, rejected = TRUE WHERE attendeeID = ?");
            statement.setInt(1,attendeeID);
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
     * Set whether an attendee attended the meeting
     * @param attendeeID
     */
    public static void setAttended(int attendeeID, boolean attended) {
        Connect connect = new Connect();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connect.getConnection();
            statement = connection.prepareStatement("UPDATE attendee " +
                    "SET attended = ? WHERE attendeeID = ?");
            statement.setBoolean(1, attended);
            statement.setInt(2,attendeeID);
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
     * Check if an attendee has an account
     * @param attendeeID
     * @return
     */
    private static boolean hasAccount(int attendeeID){
        boolean hasAccount = false;
        Connect connect = new Connect();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connect.getConnection();
            statement = connection.prepareStatement("SELECT hasAccount FROM attendee " +
                    "WHERE attendeeID = ?");
            statement.setInt(1, attendeeID);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                hasAccount = (resultSet.getBoolean(1));
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
        return hasAccount;
    }

    /**
     * Convert a result set row to an accountAttendee
     * @param resultSet
     * @return
     * @throws SQLException
     */
    private static AccountAttendee resultSetToAccountAttendee(ResultSet resultSet) throws SQLException {
        int attendeeID = resultSet.getInt(1);
        int meetingID = resultSet.getInt(2);
        boolean hasAccount = resultSet.getBoolean(3);
        boolean accepted = resultSet.getBoolean(4);
        boolean rejected = resultSet.getBoolean(5);
        boolean attended = resultSet.getBoolean(6);
        int accountID = resultSet.getInt(7);
        Account account = AccountConnection.findAccount(accountID);
        return new AccountAttendee(attendeeID,meetingID, accepted, rejected, attended, account);
    }

    /**
     * Convert a result set row to a noAccountAttendee
     * @param resultSet
     * @return
     * @throws SQLException
     */
    private static NoAccountAttendee resultSetToNoAccountAttendee(ResultSet resultSet) throws SQLException {
        int attendeeID = resultSet.getInt(1);
        int meetingID = resultSet.getInt(2);
        boolean hasAccount = resultSet.getBoolean(3);
        boolean accepted = resultSet.getBoolean(4);
        boolean rejected = resultSet.getBoolean(5);
        boolean attended = resultSet.getBoolean(6);
        String name = resultSet.getString(7);
        String email = resultSet.getString(8);
        return new NoAccountAttendee(attendeeID, meetingID, accepted, rejected, attended, name, email);
    }

}
