package model;

import structure.Account;
import connect.AccountConnection;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class AddAccountModelTest {

    private final AddAccountModel model = new AddAccountModel();
    private final TestCaseGenerator generator = new TestCaseGenerator();
    private static final int TEST_ACCOUNTS = 5;

    @Test
    public void addAccount() {
        int beforeAccounts = AccountConnection.findAllAccounts().size();
        ArrayList<Account> accounts = generator.generateAccounts(TEST_ACCOUNTS);
        for (Account account : accounts){
            model.addAccount(account.getFirstName(), account.getSurname(), account.getEmail(),
                    "iamaperson", account.isAdmin());
        }
        int afterAccounts = AccountConnection.findAllAccounts().size();
        Assert.assertEquals(beforeAccounts, afterAccounts - TEST_ACCOUNTS);
        ArrayList<Account> addedAccounts = new ArrayList<>();
        for (Account account : accounts){
            addedAccounts.add(AccountConnection.findAccount(account.getEmail()));
        }
        for (Account account : addedAccounts){
            AccountConnection.deleteAccount(account.getAccountID());
        }
    }
}