package connect;

import structure.Location;
import org.apache.commons.dbutils.DbUtils;

import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;

/**
 * This class is used for database interaction with the location entity.
 */
public class LocationConnection {

    /**
     * Get an location that matches an ID
     * @param locationID
     * @return
     */
    public static Location findLocation(int locationID) {
        Connect connect = new Connect();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Location location = null;
        try {
            connection = connect.getConnection();
            statement = connection.prepareStatement("SELECT * FROM location WHERE locationID = ?");
            statement.setInt(1, locationID);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                location = (resultSetToLocation(resultSet));
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
        return location;
    }

    /**
     *
     * @return a list of all locations in the database
     */
    public static ArrayList<Location> findAllLocations() {
        Connect connect = new Connect();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        ArrayList<Location> locations = new ArrayList<>();
        try {
            connection = connect.getConnection();
            statement = connection.prepareStatement("SELECT * FROM location");
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                locations.add(resultSetToLocation(resultSet));
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
        return locations;
    }

    /**
     * Find a location given its name and postcode
     * @param name
     * @param postcode
     * @return
     */
    public static Location locationByNameAndPostcode(String name, String postcode) {
        Connect connect = new Connect();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Location location = null;
        try {
            connection = connect.getConnection();
            statement = connection.prepareStatement("SELECT * FROM location WHERE locationName = ?" +
                    "AND postcode = ?");
            statement.setString(1, name);
            statement.setString(2, postcode);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                location = (resultSetToLocation(resultSet));
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
        return location;
    }

    /**
     * Add a location to the database
     * @param location
     * @return
     */
    public static int addLocation(Location location) {
        Connect connect = new Connect();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connect.getConnection();
            statement = connection.prepareStatement("INSERT INTO location VALUES (" +
                    "DEFAULT ,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, location.getName());
            statement.setString(2, location.getAddress());
            statement.setString(3, location.getPostcode());
            statement.setTime(4, Time.valueOf(location.getOpenTime()));
            statement.setTime(5, Time.valueOf(location.getCloseTime()));
            statement.setBoolean(6, location.isOpenSpace());
            statement.setString(7, location.getDescription());
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
     * Delete a location from the database
     * @param locationID
     */
    public static void deleteLocation(int locationID) {
        Connect connect = new Connect();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connect.getConnection();
            statement = connection.prepareStatement("DELETE FROM location WHERE locationID = ?");
            statement.setInt(1, locationID);
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
     * Edit a location in the database
     * @param location
     */
    public static void editLocation(Location location) {
        Connect connect = new Connect();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connect.getConnection();
            statement = connection.prepareStatement("UPDATE location SET " +
                    "locationName = ?, address = ?, postcode = ?, openTime = ?, closeTime = ?, isOpenSpace = ?, " +
                    "locationDescription = ? " +
                    "WHERE locationID = ?");
            statement.setString(1, location.getName());
            statement.setString(2, location.getAddress());
            statement.setString(3, location.getPostcode());
            statement.setTime(4, Time.valueOf(location.getOpenTime()));
            statement.setTime(5, Time.valueOf(location.getCloseTime()));
            statement.setBoolean(6, location.isOpenSpace());
            statement.setString(7, location.getDescription());
            statement.setInt(8, location.getLocationID());
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
     * Convert a result set row to a location
     * @param resultSet
     * @return
     * @throws SQLException
     */
    private static Location resultSetToLocation(ResultSet resultSet) throws SQLException {
        int locationID = resultSet.getInt(1);
        String name = resultSet.getString(2);
        String address = resultSet.getString(3);
        String postcode = resultSet.getString(4);
        LocalTime openTime = resultSet.getTime(5).toLocalTime();
        LocalTime closeTime  = resultSet.getTime(6).toLocalTime();
        boolean isOpenSpace = resultSet.getBoolean(7);
        String description = resultSet.getString(8);
        return new Location(locationID,name,address, postcode, openTime,closeTime, isOpenSpace, description);
    }
}
