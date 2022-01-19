package connect;

import structure.Account;
import org.apache.commons.dbutils.DbUtils;

import java.sql.*;
import java.util.ArrayList;

/**
 * This class is used for database interaction with the account entity.
 */
public class AccountConnection {
    /**
     * Get an account that matches an ID
     * @param accountID
     * @return
     */
    public static Account findAccount(int accountID) {
        Connect connect = new Connect();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Account account = null;
        try {
            connection = connect.getConnection();
            statement = connection.prepareStatement("SELECT * FROM account WHERE accountID = ?");
            statement.setInt(1, accountID);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                account = (resultSetToAccount(resultSet));
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
        return account;
    }

    /**
     * Get an account that matches an email
     */
    public static Account findAccount(String email) {
        Connect connect = new Connect();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Account account = null;
        try {
            connection = connect.getConnection();
            statement = connection.prepareStatement("SELECT * FROM account WHERE email = ?");
            statement.setString(1, email);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                account = (resultSetToAccount(resultSet));
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
        return account;
    }

    /**
     *
     * @return a list of all the accounts in the database
     */
    public static ArrayList<Account> findAllAccounts() {
        Connect connect = new Connect();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        ArrayList<Account> accounts = new ArrayList<>();
        try {
            connection = connect.getConnection();
            statement = connection.prepareStatement("SELECT * FROM account");
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                accounts.add(resultSetToAccount(resultSet));
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
        return accounts;
    }

    /**
     *
     * @return a list of all the emails associated with accounts in the database
     */
    public static ArrayList<String> findAllEmails() {
        Connect connect = new Connect();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        ArrayList<String> emails = new ArrayList<>();
        try {
            connection = connect.getConnection();
            statement = connection.prepareStatement("SELECT email FROM account");
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                emails.add(resultSet.getString(1));
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
        return emails;
    }

    /**
     * Add an account to the database
     * @param account
     */
    public static int addAccount(Account account) {
        Connect connect = new Connect();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connect.getConnection();
            statement = connection.prepareStatement("INSERT INTO account VALUES (" +
                    "DEFAULT ,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, account.getFirstName());
            statement.setString(2, account.getSurname());
            statement.setString(3, account.getEmail());
            statement.setBytes(4, account.getHashedPassword());
            statement.setBytes(5, account.getSalt());
            statement.setBoolean(6, account.isAdmin());
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
     * Delete an account from the database
     * @param accountID
     */
    public static void deleteAccount(int accountID) {
        Connect connect = new Connect();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connect.getConnection();
            statement = connection.prepareStatement("DELETE FROM account WHERE accountID = ?");
            statement.setInt(1, accountID);
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
     * Edit an account in the database
     * @param account
     */
    public static void editAccount(Account account) {
        Connect connect = new Connect();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connect.getConnection();
            statement = connection.prepareStatement("UPDATE account " +
                    "SET firstName = ?, surname = ?, email = ?, password = ?, salt = ?, isAdmin = ? " +
                    "WHERE accountID = ?");
            statement.setString(1, account.getFirstName());
            statement.setString(2, account.getSurname());
            statement.setString(3, account.getEmail());
            statement.setBytes(4, account.getHashedPassword());
            statement.setBytes(5, account.getSalt());
            statement.setBoolean(6, account.isAdmin());
            statement.setInt(7, account.getAccountID());
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
     * Convert a result set row to an account
     * @param resultSet
     * @return
     * @throws SQLException
     */
    private static Account resultSetToAccount(ResultSet resultSet) throws SQLException {
        int accountID = resultSet.getInt(1);
        String firstName = resultSet.getString(2);
        String surname = resultSet.getString(3);
        String email = resultSet.getString(4);
        byte[] password = resultSet.getBytes(5);
        byte[] salt = resultSet.getBytes(6);
        boolean isAdmin = resultSet.getBoolean(7);
        return new Account(accountID,firstName,surname, email, password,salt, isAdmin);
    }

}
