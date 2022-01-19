package structure;

import java.util.Arrays;
import java.util.Objects;

/**
 * An account for use of the system
 */
public class Account {
    private int accountID;
    private String firstName;
    private String surname;
    private String email;
    private byte[] hashedPassword;
    private byte[] salt;
    private boolean isAdmin;

    /**
     * Constructor for creation the object given all attributes
     */
    public Account(int accountID, String firstName, String surname, String email, byte[] hashedPassword, byte[] salt, boolean isAdmin) {
        this.accountID = accountID;
        this.firstName = firstName;
        this.surname = surname;
        this.email = email;
        this.hashedPassword = hashedPassword;
        this.salt = salt;
        this.isAdmin = isAdmin;
    }

    /**
     * Copy constructor
     */
    public Account(Account other) {
        this.accountID = other.accountID;
        this.firstName = other.firstName;
        this.surname = other.surname;
        this.email = other.email;
        this.hashedPassword = other.hashedPassword;
        this.salt = other.salt;
        this.isAdmin = other.isAdmin;
    }

    /**
     * Constructor for creating an account not currently in the database
     * ID is initialised to -1
     */
    public Account(String firstName, String surname, String email, byte[] hashedPassword, byte[] salt, boolean isAdmin) {
        this.accountID = -1;
        this.firstName = firstName;
        this.surname = surname;
        this.email = email;
        this.hashedPassword = hashedPassword;
        this.salt = salt;
        this.isAdmin = isAdmin;
    }

    public int getAccountID() {
        return accountID;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public byte[] getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(byte[] hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public byte[] getSalt() {
        return salt;
    }

    public void setSalt(byte[] salt) {
        this.salt = salt;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String getFullName() {
        return getFirstName() + " " + getSurname();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return accountID == account.accountID &&
                isAdmin == account.isAdmin &&
                Objects.equals(firstName, account.firstName) &&
                Objects.equals(surname, account.surname) &&
                Objects.equals(email, account.email) &&
                Arrays.equals(hashedPassword, account.hashedPassword) &&
                Arrays.equals(salt, account.salt);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(accountID, firstName, surname, email, isAdmin);
        result = 31 * result + Arrays.hashCode(hashedPassword);
        result = 31 * result + Arrays.hashCode(salt);
        return result;
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountID=" + accountID +
                ", firstName='" + firstName + '\'' +
                ", surname='" + surname + '\'' +
                ", email='" + email + '\'' +
                ", hashedPassword=" + Arrays.toString(hashedPassword) +
                ", salt=" + Arrays.toString(salt) +
                ", isAdmin=" + isAdmin +
                '}';
    }
}
