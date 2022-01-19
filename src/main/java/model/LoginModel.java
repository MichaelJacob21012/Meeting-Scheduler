package model;

import connect.AccountConnection;
import structure.Account;
import utility.PasswordManager;

import java.util.Arrays;

/**
 * Model class for logging in to the system
 */
public class LoginModel {
    /**
     * Attempt to match an email and password
     * @param email
     * @param password
     * @return The account if successful, null otherwise
     */
    public  Account login(String email, String password) {
        Account account = AccountConnection.findAccount(email);
        if (account == null){
            return null;
        }
        byte[] salt = account.getSalt();
        byte[] hashedPassword = PasswordManager.hash(password, salt);
        if (Arrays.equals(hashedPassword, account.getHashedPassword())){
            return account;
        }
        return null;
    }


}
