package model;

import structure.Account;
import connect.AccountConnection;

import java.util.ArrayList;

/**
 * Model class for the accounts list screen
 */
public class AccountsModel {
    private ArrayList<Account> accounts;
    // A filtered list of accounts according to a search string
    private ArrayList<Account> filteredAccounts;

    public AccountsModel() {
        this.accounts = new ArrayList<>();
    }

    public ArrayList<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(ArrayList<Account> accounts) {
        this.accounts = accounts;
    }

    public ArrayList<Account> getFilteredAccounts() {
        return filteredAccounts;
    }

    public void setFilteredAccounts(ArrayList<Account> filteredAccounts) {
        this.filteredAccounts = filteredAccounts;
    }

    public void fetchData(){
        findAccounts();
    }

    private void findAccounts(){
        accounts = AccountConnection.findAllAccounts();
        filteredAccounts = accounts;
    }

    /**
     * Assign filteredAccounts to list of all accounts whose name or email contains the search string
     */
    public void search(String text) {
        ArrayList<Account> filtered = new ArrayList<>();
        accounts.stream()
                .filter(a -> (a.getEmail().toLowerCase().contains(text.toLowerCase()) ||
                        a.getFullName().toLowerCase().contains(text.toLowerCase())))
                .forEach(filtered::add);
        filteredAccounts = filtered;
    }
}
