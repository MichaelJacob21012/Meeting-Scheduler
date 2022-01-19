package model;

import structure.Account;
import connect.AccountConnection;
import utility.PasswordManager;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

/**
 * Model class for adding accounts based on user input
 */
public class AddAccountModel {

    /**
     * Add account given all information barring the ID and the salt
     * @param fName
     * @param sName
     * @param email
     * @param password
     * @param isAdmin
     */
    public void addAccount(String fName, String sName, String email, String password, boolean isAdmin) {
        byte[] salt = PasswordManager.makeSalt();
        byte[] hashedPassword = PasswordManager.hash(password,salt);
        Account account = new Account( fName, sName, email, hashedPassword, salt, isAdmin);
        AccountConnection.addAccount(account);
    }

    /**
     *
     * @return true if no accounts in the database
     */
    public boolean noAccounts(){
        return AccountConnection.findAllAccounts().isEmpty();
    }

    /**
     * Check if an email is a valid format
     * @param email
     * @return
     */
    public boolean isEmail(String email) {
        try {
            InternetAddress emailAddress = new InternetAddress(email);
            emailAddress.validate();
            return true;
        } catch (AddressException e) {
            return false;
        }
    }

    /**
     * Check if an email is already associated with an account
     * @param email
     * @return
     */
    public boolean emailExists(String email){
        return AccountConnection.findAllEmails().contains(email);
    }

}
