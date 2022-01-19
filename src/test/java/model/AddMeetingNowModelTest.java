package model;

import connect.*;
import org.junit.*;
import structure.*;
import utility.LocationUtility;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;

public class AddMeetingNowModelTest {
    private static final AddMeetingNowModel model = new AddMeetingNowModel();
    private static final TestCaseGenerator generator = new TestCaseGenerator();
    private static ArrayList<Account> accounts;
    private static ArrayList<Location> locations;
    private static ArrayList<Room> rooms;
    private static ArrayList<Meeting> meetings;
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
        meetings = generator.generateMeetings(40, accounts, rooms);
        for (Meeting meeting : meetings){
            meeting.setMeetingID(MeetingConnection.addMeeting(meeting));
        }
        for (Meeting meeting : meetings){
            addRandomAttendees(meeting);
        }
        LocationUtility locationUtility = new LocationUtility();
        model.setLocations(locations);
        model.setRooms(rooms);
    }

    private static void addRandomAttendees(Meeting meeting) {
        ArrayList<Account> accountsCopy = new ArrayList<>();
        for (Account account : accounts){
            accountsCopy.add(new Account(account));
        }
        int bound = random.nextInt(accounts.size());
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
    public void immediateScheduleSuccess() {
        ProvisionalMeeting provisionalMeeting = new ProvisionalMeeting(accounts.get(0), "Test1", 2, 30, "A Test", true);
        Location intendedLocation = locations.get(0);
        Room intendedRoom = rooms.get(0);
        LocalDate intendedDate = LocalDate.now().plusDays(1);
        LocalTime intendedStartTime = LocalTime.of(12,0);

        LocalTime intendedFinishTime = intendedStartTime.plusHours(provisionalMeeting.getHours())
                .plusMinutes(provisionalMeeting.getMinutes()).plusMinutes(intendedRoom.getCleanupTime().getMinute());

        ArrayList<Account> toAttend = accounts.stream().limit(7).collect(Collectors.toCollection(ArrayList::new));
        for (Account account : toAttend){
            provisionalMeeting.addAttendee(new AccountAttendee(account));
        }
        boolean available = true;
        ArrayList<Meeting> roomMeetings = MeetingConnection.findAllByRoomAndDate(intendedRoom.getRoomID(), intendedDate);
        for (Meeting meeting : roomMeetings){
           if(!(meeting.finishTime().isAfter(intendedFinishTime) ||
                    meeting.finishTime().isBefore(intendedStartTime))){
               available = false;
           }
            if(!(meeting.startTime().isAfter(intendedFinishTime) ||
                    meeting.startTime().isBefore(intendedStartTime))){
                available = false;
            }
            if(meeting.startTime().isBefore(intendedStartTime) && meeting.finishTime().isAfter(intendedFinishTime)){
                available = false;
            }
        }
        for (Account account : toAttend) {
            ArrayList<Meeting> attendeeMeetings = MeetingConnection.attendeeMeetingsOnDate(account.getAccountID(), intendedDate);
            for (Meeting meeting : attendeeMeetings) {
                if(!(meeting.finishTime().isAfter(intendedFinishTime) ||
                        meeting.finishTime().isBefore(intendedStartTime))){
                    available = false;
                }
                if(!(meeting.startTime().isAfter(intendedFinishTime) ||
                        meeting.startTime().isBefore(intendedStartTime))){
                    available = false;
                }
                if(meeting.startTime().isBefore(intendedStartTime) && meeting.finishTime().isAfter(intendedFinishTime)){
                    available = false;
                }
            }
        }
        ArrayList<ProvisionalMeeting> scheduled = model.immediateSchedule(provisionalMeeting, intendedLocation.details(),
                intendedRoom.getName(), intendedDate, intendedStartTime);

        if (available) {
            Assert.assertFalse(scheduled.isEmpty());
            ProvisionalMeeting scheduledMeeting = scheduled.get(0);
            Assert.assertEquals(scheduledMeeting.getLocation(), intendedLocation);
            Assert.assertEquals(scheduledMeeting.getRoom(), intendedRoom);
            Assert.assertEquals(scheduledMeeting.getDate(), intendedDate);
            Assert.assertEquals(scheduledMeeting.getStartTime(), intendedStartTime);
        }
        else {
            Assert.assertTrue(scheduled.isEmpty());
        }
    }


    @Test
    public void immediateScheduleMeetingClash() {
        Meeting toClash = meetings.get(0);
        LocalTime time = toClash.getDurationTime().toLocalTime();
        ProvisionalMeeting provisionalMeeting = new ProvisionalMeeting(accounts.get(0), "Test1", time.getHour(),
                time.getMinute(), "A Test", true);
        Location intendedLocation = toClash.getLocation();
        Room intendedRoom = toClash.getRoom();
        LocalDate intendedDate = toClash.getTimestamp().toLocalDateTime().toLocalDate();
        LocalTime intendedStartTime = toClash.startTime();

        ArrayList<Account> toAttend = accounts.stream().limit(5).collect(Collectors.toCollection(ArrayList::new));
        for (Account account : toAttend){
            provisionalMeeting.addAttendee(new AccountAttendee(account));
        }
        ArrayList<ProvisionalMeeting> scheduled = model.immediateSchedule(provisionalMeeting, intendedLocation.details(),
                intendedRoom.getName(), intendedDate, intendedStartTime);
        Assert.assertTrue(scheduled.isEmpty());

    }


    @Test
    public void autoSchedule() {
        ProvisionalMeeting provisionalMeeting = new ProvisionalMeeting(accounts.get(0), "Test1", 2, 30, "A Test", true);
        ArrayList<Account> toAttend = accounts.stream().limit(5).collect(Collectors.toCollection(ArrayList::new));
        for (Account account : toAttend){
            provisionalMeeting.addAttendee(new AccountAttendee(account));
        }
        ArrayList<ProvisionalMeeting> scheduled = model.autoSchedule(provisionalMeeting, locations.get(0).details(),
                true, rooms.get(0).getName(), true, LocalDate.now(), true, LocalTime.now(), true);
        for (ProvisionalMeeting pm : scheduled){
            boolean available = true;
            ArrayList<Meeting> roomMeetings = MeetingConnection.findAllByRoomAndDate(pm.getRoom().getRoomID(), pm.getDate());
            for (Meeting meeting : roomMeetings){
                if(!(meeting.finishTime().isAfter(pm.getFinishTime()) ||
                        meeting.finishTime().isBefore(pm.getStartTime()))){
                    available = false;
                }
                if(!(meeting.startTime().isAfter(pm.getFinishTime()) ||
                        meeting.startTime().isBefore(pm.getStartTime()))){
                    available = false;
                }
                if(meeting.startTime().isBefore(pm.getStartTime()) && meeting.finishTime().isAfter(pm.getFinishTime())){
                    available = false;
                }
            }
            for (Account account : toAttend) {
                ArrayList<Meeting> attendeeMeetings = MeetingConnection.attendeeMeetingsOnDate(account.getAccountID(), pm.getDate());
                for (Meeting meeting : attendeeMeetings) {
                    if(!(meeting.finishTime().isAfter(pm.getFinishTime()) ||
                            !meeting.finishTime().isAfter(pm.getStartTime()))){
                        available = false;
                    }
                    if(!(!meeting.startTime().isBefore(pm.getFinishTime()) ||
                            meeting.startTime().isBefore(pm.getStartTime()))){
                        available = false;
                    }
                    if(meeting.startTime().isBefore(pm.getStartTime()) && meeting.finishTime().isAfter(pm.getFinishTime())){
                        available = false;
                    }
                }
            }
            Assert.assertTrue(available);
        }
    }

}