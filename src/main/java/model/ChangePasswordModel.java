package model;

import structure.Account;
import connect.AccountConnection;
import utility.PasswordManager;

public class ChangePasswordModel {
    public void changePassword(int accountID, String password) {
        Account account = AccountConnection.findAccount(accountID);
        account.setSalt(PasswordManager.makeSalt());
        account.setHashedPassword(PasswordManager.hash(password,account.getSalt()));
        AccountConnection.editAccount(account);
    }
}
