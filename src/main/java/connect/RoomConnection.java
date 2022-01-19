package connect;

import structure.Location;
import structure.Room;
import org.apache.commons.dbutils.DbUtils;

import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;

/**
 * This class is used for database interaction with the room entity.
 */
public class RoomConnection {

    /**
     * Get a room that matches an ID
     * @param roomID
     * @return
     */
    public static Room roomByID(int roomID) {
        Connect connect = new Connect();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Room room = null;
        try {
            connection = connect.getConnection();
            statement = connection.prepareStatement("SELECT * FROM room WHERE roomID = ?");
            statement.setInt(1, roomID);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                room = (resultSetToRoom(resultSet));
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
        return room;
    }

    /**
     *
     * @return a list of all the rooms in the database
     */
    public static ArrayList<Room> findAllRooms() {
        Connect connect = new Connect();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        ArrayList<Room> rooms = new ArrayList<>();
        try {
            connection = connect.getConnection();
            statement = connection.prepareStatement("SELECT * FROM room");
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                rooms.add(resultSetToRoom(resultSet));
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
        return rooms;
    }

    /**
     * Find all rooms in a location
     * @param location
     * @return
     */
    public static ArrayList<Room> findRoomsInLocation(Location location) {
        Connect connect = new Connect();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        ArrayList<Room> rooms = new ArrayList<>();
        try {
            connection = connect.getConnection();
            statement = connection.prepareStatement("SELECT * FROM room WHERE " +
                    "locationID = ?");
            statement.setInt(1, location.getLocationID());
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                rooms.add(resultSetToRoom(resultSet));
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
        return rooms;
    }

    /**
     * Find a room given its name and location
     * @param name the name of the room
     * @param locationID the ID of the location of the room
     * @return
     */
    public static Room roomByNameAndLocationID(String name, int locationID) {
        Connect connect = new Connect();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Room room = null;
        try {
            connection = connect.getConnection();
            statement = connection.prepareStatement("SELECT * FROM room WHERE roomName = ?" +
                    "AND locationID = ?");
            statement.setString(1, name);
            statement.setInt(2, locationID);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                room = (resultSetToRoom(resultSet));
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
        return room;
    }

    /**
     * Convert a result set row to a room
     * @param resultSet
     * @return
     * @throws SQLException
     */
    private static Room resultSetToRoom(ResultSet resultSet) throws SQLException {
        int roomID = resultSet.getInt(1);
        String name = resultSet.getString(2);
        int locationID = resultSet.getInt(3);
        int capacity = resultSet.getInt(4);
        String description = resultSet.getString(5);
        double cost = resultSet.getDouble(6);
        LocalTime cleanupTime  = resultSet.getTime(7).toLocalTime();
        return new Room(roomID,name,LocationConnection.findLocation(locationID),
                capacity, description , cost,cleanupTime);
    }

    public static void deleteRoom(int roomID) {
        Connect connect = new Connect();
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = connect.getConnection();
            statement = connection.prepareStatement("DELETE FROM room WHERE locationID = ?");
            statement.setInt(1, roomID);
            statement.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            DbUtils.closeQuietly(connection);
            DbUtils.closeQuietly(statement);
        }
    }

    public static int addRoom(Room room) {
        Connect connect = new Connect();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connect.getConnection();
            statement = connection.prepareStatement("INSERT INTO room VALUES (" +
                    "DEFAULT ,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, room.getName());
            statement.setInt(2, room.getLocation().getLocationID());
            statement.setInt(3, room.getCapacity());
            statement.setString(4, room.getDescription());
            statement.setDouble(5, room.getCost());
            statement.setTime(6, Time.valueOf(room.getCleanupTime()));
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

    public static void editRoom(Room room) {
        Connect connect = new Connect();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connect.getConnection();
            statement = connection.prepareStatement("UPDATE room SET " +
                    "roomName = ?, locationID = ?, capacity = ?, roomDescription = ?, cost = ?, cleanupTime = ? " +
                    "WHERE roomID = ?");
            statement.setString(1, room.getName());
            statement.setInt(2, room.getLocation().getLocationID());
            statement.setInt(3, room.getCapacity());
            statement.setString(4, room.getDescription());
            statement.setDouble(5, room.getCost());
            statement.setTime(6, Time.valueOf(room.getCleanupTime()));
            statement.setInt(7, room.getRoomID());
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
}
