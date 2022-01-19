package model;

import org.junit.Assert;
import org.junit.Test;
import structure.Account;

import java.util.ArrayList;

public class AddMeetingAttendeesModelTest {

    private final AddMeetingAttendeesModel meetingAttendeesModel = new AddMeetingAttendeesModel();
    private final TestCaseGenerator generator = new TestCaseGenerator();
    private static final int TEST_ACCOUNTS = 5;

    @Test
    public void search() {
        ArrayList<Account> accounts = generator.generateAccounts(TEST_ACCOUNTS);
        meetingAttendeesModel.setAccounts(accounts);
        Assert.assertEquals(1, meetingAttendeesModel.search("Person1").size());
        Assert.assertEquals(1, meetingAttendeesModel.search("3@").size());
        Assert.assertEquals(5, meetingAttendeesModel.search("person").size());
        Assert.assertEquals(0, meetingAttendeesModel.search("banana").size());
    }
}