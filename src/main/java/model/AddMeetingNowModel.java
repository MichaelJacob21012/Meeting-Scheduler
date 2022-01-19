package model;

import structure.*;
import connect.AttendeeConnection;
import connect.LocationConnection;
import connect.MeetingConnection;
import connect.RoomConnection;
import utility.LocationUtility;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

/**
 * Model for the finalisation screen for meetings intended to be scheduled immediately
 */
public class AddMeetingNowModel {
    //The greatest number of possible options for the meeting to be given to the user
    private static final int MAX_RETURNED_SOLUTIONS = 5;
    private ArrayList<Location> locations;
    private ArrayList<Room> rooms;
    private final LocationUtility locationUtility;

    public AddMeetingNowModel() {
        locationUtility = new LocationUtility();
    }

    public void fetchData(){
        findLocations();
        findRooms();
    }

    public ArrayList<Location> getLocations() {
        return locations;
    }

    public void setLocations(ArrayList<Location> locations) {
        this.locations = locations;
    }

    public ArrayList<Room> getRooms() {
        return rooms;
    }

    public void setRooms(ArrayList<Room> rooms) {
        this.rooms = rooms;
    }

    private void findLocations(){
        locations = LocationConnection.findAllLocations();
    }

    private void findRooms(){
        rooms = RoomConnection.findAllRooms();
    }



    /**
     * Attempt to schedule the meeting based entirely on the values given
     * @param provisionalMeeting
     * @param locationDetails
     * @param roomName
     * @param date
     * @param time
     * @return
     */
    public ArrayList<ProvisionalMeeting> immediateSchedule(ProvisionalMeeting provisionalMeeting, String locationDetails,
                                                           String roomName, LocalDate date, LocalTime time) {
        ArrayList<ProvisionalMeeting> provisionalMeetings = new ArrayList<>();
        Location location = locationUtility.recoverLocation(locationDetails);
        Room room = locationUtility.recoverRoom(roomName, location);
        Duration duration = Duration.between(LocalTime.MIN,LocalTime.of(provisionalMeeting.getHours(),provisionalMeeting.getMinutes()));
        Solution solution = new Solution(location, room, date, time);

        ArrayList<Meeting> meetings = MeetingConnection.findAllByRoomAndDate(room.getRoomID(), date);
        if (meetingInTime(meetings, time, time.plus(duration).plusMinutes(room.getCleanupTime().getMinute()))){
            return provisionalMeetings;
        }
        for (AccountAttendee accountAttendee : provisionalMeeting.getAccountAttendees()){
            if (!solution.canAttend(accountAttendee, duration)){
                return provisionalMeetings;
            }
        }
        solution.addToMeeting(provisionalMeeting);
        provisionalMeetings.add(provisionalMeeting);
        return provisionalMeetings;
    }

    private boolean meetingInTime(ArrayList<Meeting> meetings, LocalTime start, LocalTime finish) {
        for (Meeting meeting : meetings){
            if (meeting.timeClash(start, finish)){
                return true;
            }
        }
        return false;
    }

    public ArrayList<ProvisionalMeeting> autoSchedule(ProvisionalMeeting provisionalMeeting, String locationDetails,
                                                      boolean autoLocation, String roomName, boolean autoRoom,
                                                      LocalDate date, boolean autoDate, LocalTime time, boolean autoTime) {
        ArrayList<Solution> solutions = new ArrayList<>();

        // Create solutions for each possible location
        if(!autoLocation){
            Location location = locationUtility.recoverLocation(locationDetails);
            Solution solution = new Solution(location);
            solutions.add(solution);
        }
        else{
            ArrayList<Location> locations = autoLocate(provisionalMeeting, date, autoDate, time, autoTime);
            locations.forEach(location -> solutions.add(new Solution(location)));
        }
        ArrayList<Solution> roomSolutions = new ArrayList<>();
        for (Solution solution : solutions) {
            if (!autoRoom) {
                Room room = RoomConnection.roomByNameAndLocationID(roomName, solution.location.getLocationID());
                solution.setRoom(room);
                roomSolutions = solutions;
            } else {
                ArrayList<Room> rooms = autoRoom(provisionalMeeting,solution, date, autoDate, time, autoTime);
                ArrayList<Solution> solutionArrayList = new ArrayList<>();
                rooms.forEach(r -> {
                    Solution solution1 = new Solution(solution.getLocation(), r);
                    solutionArrayList.add(solution1);
                });
                roomSolutions.addAll(solutionArrayList);
            }
        }

        ArrayList<Solution> dateSolutions = new ArrayList<>();
        for (Solution solution : roomSolutions) {
            if (!autoDate) {
                solution.setDate(date);
                dateSolutions = roomSolutions;
            } else {
                HashSet<LocalDate> dates = autoDate(provisionalMeeting,solution, time, autoTime);
                ArrayList<Solution> solutionArrayList = new ArrayList<>();
                dates.forEach(d -> {
                    Solution solution1 = new Solution(solution.getLocation(), solution.getRoom(), d);
                    solutionArrayList.add(solution1);
                });
                dateSolutions.addAll(solutionArrayList);
            }
        }

        ArrayList<Solution> timeSolutions = new ArrayList<>();
        for (Solution solution : dateSolutions) {
            if (!autoTime) {
                solution.setTime(time);
                timeSolutions = dateSolutions;
            } else {
                HashSet<LocalTime> times = autoTime(provisionalMeeting,solution);
                ArrayList<Solution> solutionArrayList = new ArrayList<>();
                times.forEach(t -> {
                    Solution solution1 = new Solution(solution.getLocation(), solution.getRoom(), solution.getDate(), t);
                    solutionArrayList.add(solution1);
                });
                timeSolutions.addAll(solutionArrayList);
            }
        }

        ArrayList<ProvisionalMeeting> provisionalMeetings = new ArrayList<>();
        Random random = new Random();
        int i = 0;
        while (i < MAX_RETURNED_SOLUTIONS){
            if (timeSolutions.isEmpty()){
                break;
            }
            Solution solution = timeSolutions.get(random.nextInt(timeSolutions.size()));
            boolean clash = false;
            for (AccountAttendee accountAttendee : provisionalMeeting.getAccountAttendees()){
                if (!solution.canAttend(accountAttendee, provisionalMeeting.duration())){
                    timeSolutions.remove(solution);
                    clash = true;
                }
            }
            if (clash){
                continue;
            }
            ProvisionalMeeting meeting = new ProvisionalMeeting(provisionalMeeting);
            solution.addToMeeting(meeting);
            provisionalMeetings.add(meeting);
            ++i;
            timeSolutions.remove(solution);
        }
        return provisionalMeetings;
    }

    private ArrayList<Location> autoLocate(ProvisionalMeeting meeting, LocalDate date, boolean autoDate,
                                           LocalTime time, boolean autoTime) {
        ArrayList<Location> availableLocations = new ArrayList<>();
        ArrayList<AccountAttendee> accountAttendees = meeting.getAccountAttendees();
        LocalTime duration = LocalTime.of(meeting.getHours(), meeting.getMinutes());
        if (autoDate && autoTime){
            locations.stream()
                    .filter(location -> location.hasTimeSlot(duration, accountAttendees))
                    .forEach(availableLocations::add);
            return availableLocations;
        }
        else if (autoDate){
            locations.stream()
                    .filter(location -> location.hasTimeSlot(duration, accountAttendees, date))
                    .forEach(availableLocations::add);
            return availableLocations;
        }
        else if (autoTime){
            locations.stream()
                    .filter(location -> location.hasTimeSlot(duration, accountAttendees, time))
                    .forEach(availableLocations::add);
            return availableLocations;
        }
        LocalDateTime dateTime = LocalDateTime.of(date, time);
        locations.stream()
                .filter(location -> location.hasTimeSlot(duration, accountAttendees, dateTime))
                .forEach(availableLocations::add);
        return availableLocations;
    }

    private ArrayList<Room> autoRoom(ProvisionalMeeting meeting, Solution solution, LocalDate date, boolean autoDate, LocalTime time, boolean autoTime) {
        ArrayList<Room> rooms = RoomConnection.findRoomsInLocation(solution.getLocation());
        ArrayList<Room> availableRooms = new ArrayList<>();
        ArrayList<AccountAttendee> accountAttendees = meeting.getAccountAttendees();
        LocalTime duration = LocalTime.of(meeting.getHours(), meeting.getMinutes());
        if (autoDate && autoTime){
            rooms.stream()
                    .filter(room -> room.hasTimeSlot(duration, accountAttendees))
                    .forEach(availableRooms::add);
            return availableRooms;
        }
        else if (autoDate){
            rooms.stream()
                    .filter(room -> room.hasTimeSlot(duration, accountAttendees, date))
                    .forEach(availableRooms::add);
            return availableRooms;
        }
        else if (autoTime){
            rooms.stream()
                    .filter(room -> room.hasTimeSlot(duration, accountAttendees, time))
                    .forEach(availableRooms::add);
            return availableRooms;
        }
        LocalDateTime dateTime = LocalDateTime.of(date, time);
        rooms.stream()
                .filter(room -> room.hasTimeSlot(duration, accountAttendees, dateTime))
                .forEach(availableRooms::add);
        return availableRooms;
    }

    private HashSet<LocalDate> autoDate(ProvisionalMeeting meeting, Solution solution, LocalTime time, boolean autoTime) {
        ArrayList<AccountAttendee> accountAttendees = meeting.getAccountAttendees();
        LocalTime duration = LocalTime.of(meeting.getHours(), meeting.getMinutes());
        Room room = solution.getRoom();
        if(autoTime){
            return room.availableDates(duration, accountAttendees);
        }
        return room.availableDates(duration, accountAttendees, time);

    }

    private HashSet<LocalTime> autoTime(ProvisionalMeeting meeting, Solution solution) {
        LocalTime duration = LocalTime.of(meeting.getHours(), meeting.getMinutes());
        Room room = solution.getRoom();
        Location location = room.getLocation();
        HashSet<LocalTime> availableTimes = new HashSet<>();
        Duration cleanupDuration = Duration.between(LocalTime.MIN, room.getCleanupTime());
        Duration meetingDuration = Duration.between(LocalTime.MIN,duration).plus(cleanupDuration);
        ArrayList<Meeting> meetings = MeetingConnection.findAllByRoomAndDate(room.getRoomID(), solution.getDate());
        if (meetings.isEmpty()){
            if (!location.getOpenTime().plus(meetingDuration).isAfter(location.getCloseTime())){
                if (validTime(solution, location.getOpenTime(), meetingDuration)) {
                    availableTimes.add(location.getOpenTime());
                }
            }
            else {
                return availableTimes;
            }
            for (int i = 0; i < 24; ++i){
                LocalTime startTime = LocalTime.of(i, 0);
                if (validTime(solution, startTime, meetingDuration)){
                    availableTimes.add(startTime);
                }

            }
            return availableTimes;
        }
        if (!location.getOpenTime().plus(meetingDuration).isAfter(meetings.get(0).startTime())){
            if (validTime(solution,location.getOpenTime(), meetingDuration)) {
                availableTimes.add(location.getOpenTime());
            }
        }
        for (int i = 0; i < meetings.size()-1; ++i){
            Meeting current = meetings.get(i);
            Meeting next = meetings.get(i+1);
            if(!current.finishTime().plus(cleanupDuration).plus(meetingDuration).isAfter(next.startTime())){
                LocalTime startTime = current.finishTime().plus(cleanupDuration);
                if (validTime(solution, startTime, meetingDuration)) {
                    availableTimes.add(startTime);
                }
            }
        }
        if (!location.getCloseTime().minus(meetingDuration).isBefore(meetings.get(meetings.size()-1).finishTime().plus(cleanupDuration))){
            LocalTime startTime = meetings.get(meetings.size()-1).finishTime().plus(cleanupDuration);
            if (validTime(solution, startTime, meetingDuration)) {
                availableTimes.add(startTime);
            }
        }
        return availableTimes;
    }

    private boolean validTime(Solution solution, LocalTime startTime, Duration duration){
        if (startTime.isBefore(solution.getLocation().getOpenTime())) {
            return false;
        }
        if (startTime.isAfter(solution.getLocation().getCloseTime())){
            return false;
        }
        LocalTime finishTime = startTime.plus(duration);
        if (finishTime.isBefore(startTime)){
            return false;
        }
        if (finishTime.isBefore(solution.getLocation().getOpenTime())){
            return false;
        }
        return !finishTime.isAfter(solution.getLocation().getCloseTime());

    }


    public int finaliseMeeting(ProvisionalMeeting provisionalMeeting) {
        Meeting meeting = provisionalMeeting.toMeeting();
        HashSet<Attendee> attendees = provisionalMeeting.getAttendees();
        meeting.setBooked(true);
        int meetingID = MeetingConnection.addMeeting(meeting);
        if (meetingID == -1) {
            return meetingID;
        }
        for (Attendee attendee : attendees) {
            attendee.setMeetingID(meetingID);
            if (attendee instanceof AccountAttendee){
                if (((AccountAttendee) attendee).getAccount().equals(meeting.getAccount())){
                    attendee.setAccepted(true);
                }
            }
        }
        AttendeeConnection.addAllAttendees(new ArrayList<>(attendees));
        return meetingID;
    }


    /**
     * Helper class to model potential solutions to the requirements of the meeting
     */
    class Solution {
        private Location location;
        private Room room;
        private LocalDate date;
        private  LocalTime time;

        public Solution() {
        }

        Solution(Location location) {
            this.location = location;
        }

        Solution(Location location, Room room) {
            this.location = location;
            this.room = room;
        }

        Solution(Location location, Room room, LocalDate date) {
            this.location = location;
            this.room = room;
            this.date = date;
        }

        Solution(Location location, Room room, LocalDate date, LocalTime time) {
            this.location = location;
            this.room = room;
            this.date = date;
            this.time = time;
        }

        Location getLocation() {
            return location;
        }

        public void setLocation(Location location) {
            this.location = location;
        }

        Room getRoom() {
            return room;
        }

        void setRoom(Room room) {
            this.room = room;
        }

        LocalDate getDate() {
            return date;
        }

        void setDate(LocalDate date) {
            this.date = date;
        }

        public LocalTime getTime() {
            return time;
        }

        void setTime(LocalTime time) {
            this.time = time;
        }

        void addToMeeting(ProvisionalMeeting meeting) {
            meeting.setLocation(location);
            meeting.setRoom(room);
            meeting.setDate(date);
            meeting.setStartTime(time);
        }

        @Override
        public String toString() {
            return "Solution{" +
                    "location=" + location +
                    ", room=" + room +
                    ", date=" + date +
                    ", time=" + time +
                    '}';
        }

        /**
         * Check if an attendee is able to attend the solution
         * @param accountAttendee
         * @param duration
         * @return
         */
        boolean canAttend(AccountAttendee accountAttendee, Duration duration) {
            ArrayList<Meeting> meetings = MeetingConnection.attendeeMeetingsOnDate(
                    accountAttendee.getAccount().getAccountID(), date);
            if (meetings.isEmpty()){
                return true;
            }
            LocalTime finishTime = time.plus(duration);
            for (Meeting meeting : meetings){
                if (meeting.timeClash(time, finishTime)){
                    return false;
                }
            }
            return true;
        }
    }
}
