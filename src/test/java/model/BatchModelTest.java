package model;

import connect.*;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import structure.*;
import utility.LocationUtility;

import java.util.*;

public class BatchModelTest {
    private static final BatchModel model = new BatchModel();
    private static final TestCaseGenerator generator = new TestCaseGenerator();
    private static ArrayList<Account> accounts;
    private static ArrayList<Location> locations;
    private static ArrayList<Room> rooms;
    private static ArrayList<Meeting> meetings;
    private static LinkedHashMap<Meeting, MeetingPreferences> unscheduledMeetings;
    private static final Random random = new Random();

    @BeforeClass
    public static void setUp() {
        accounts = generator.generateAccounts(30);
        for (Account account : accounts) {
            account.setAccountID(AccountConnection.addAccount(account));
        }
        locations = generator.generateLocations(2);
        for (Location location : locations){
            location.setLocationID(LocationConnection.addLocation(location));
        }
        rooms = generator.generateRooms(10, locations);
        for (Room room : rooms){
            room.setRoomID(RoomConnection.addRoom(room));
        }
        meetings = generator.generateMeetings(10, accounts, rooms);
        for (Meeting meeting : meetings){
            meeting.setMeetingID(MeetingConnection.addMeeting(meeting));
        }
        for (Meeting meeting : meetings){
            addRandomAttendees(meeting);
        }
        unscheduledMeetings = generator.generateUnscheduledMeetings(5, accounts, rooms);
        for (Map.Entry<Meeting, MeetingPreferences> entry : unscheduledMeetings.entrySet()){
            Meeting meeting = entry.getKey();
            MeetingPreferences preferences = entry.getValue();
            int meetingID = MeetingConnection.addMeeting(meeting);
            meeting.setMeetingID(meetingID);
            preferences.setMeetingID(meetingID);
            PreferencesConnection.addPreferences(preferences);
        }
        for (Meeting meeting : unscheduledMeetings.keySet()){
            addRandomAttendees(meeting);
        }
        model.setOriginal(model.makeCopy(unscheduledMeetings));
        model.setCurrent(model.makeCopy(unscheduledMeetings));
        model.setNext(model.makeCopy(unscheduledMeetings));
        model.setBest(model.makeCopy(unscheduledMeetings));
        model.setLocations(locations);
    }

    private static void addRandomAttendees(Meeting meeting) {
        ArrayList<Account> accountsCopy = new ArrayList<>();
        for (Account account : accounts){
            accountsCopy.add(new Account(account));
        }
        int bound = meeting.getNumberOfPeople();
        for (int i = 0; i < bound; ++i){
            Account account = accountsCopy.remove(random.nextInt(accountsCopy.size()));
            AccountAttendee accountAttendee = new AccountAttendee(account);
            accountAttendee.setMeetingID(meeting.getMeetingID());
            AttendeeConnection.addAttendee(accountAttendee);
        }
    }


    @AfterClass
    public static void tearDown() {
        for (Account account : accounts){
            AccountConnection.deleteAccount(account.getAccountID());
        }
        for (Location location : locations){
            LocationConnection.deleteLocation(location.getLocationID());
        }
    }

    @Test
    public void schedule() {
        model.schedule();
        LinkedHashMap<Meeting, MeetingPreferences> result = model.getBest();
        ArrayList<Meeting> meetings = BatchModelTest.meetings;
        meetings.addAll(result.keySet());
        meetings.removeIf(meeting -> !meeting.isBooked());
        for (Map.Entry<Meeting, MeetingPreferences> entry : result.entrySet()){
            Meeting meeting = entry.getKey();
            MeetingPreferences preferences = entry.getValue();
            if (!meeting.isBooked()){
                continue;
            }
            Assert.assertFalse(meeting.getTimestamp().toLocalDateTime().toLocalDate().isAfter(preferences.getLatestDate()));
            ArrayList<Meeting> allMeetings = new ArrayList<>(meetings);
            allMeetings.removeIf(meeting1 -> !meeting1.getRoom().equals(meeting.getRoom()));
            for (AccountAttendee accountAttendee : AttendeeConnection.accountAttendeesForMeeting(meeting.getMeetingID())){
                allMeetings.addAll(MeetingConnection.attendeeMeetingsOnDate(accountAttendee.getAccount().getAccountID(),meeting.getTimestamp().toLocalDateTime().toLocalDate()));
            }
            allMeetings.removeIf(meeting1 -> !meeting1.getTimestamp().toLocalDateTime().toLocalDate()
                    .equals(meeting.getTimestamp().toLocalDateTime().toLocalDate()));

            for (Meeting meeting1 : allMeetings) {
                if (meeting.equals(meeting1)){
                    continue;
                }
                Assert.assertFalse(meeting.timeClash(meeting1));
            }
        }
    }
}