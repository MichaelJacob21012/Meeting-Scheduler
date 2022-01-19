package model;

import connect.*;
import structure.*;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class BatchModel {
    private static final int BATCH_ITERATIONS = 3;
    private LinkedHashMap<Meeting, MeetingPreferences> original;
    private LinkedHashMap<Meeting, MeetingPreferences> current;
    private LinkedHashMap<Meeting, MeetingPreferences> next;
    private LinkedHashMap<Meeting, MeetingPreferences> best;
    private ArrayList<Meeting> toExclude;
    private ArrayList<Location> locations;

    public BatchModel(){
        toExclude = new ArrayList<>();
        fetchData();
    }


    public void fetchData() {
        original = findData();
        current = findData();
        next = findData();
        best = makeCopy(current);
        locations = LocationConnection.findAllLocations();
    }

    public LinkedHashMap<Meeting, MeetingPreferences> getOriginal() {
        return original;
    }

    public void setOriginal(LinkedHashMap<Meeting, MeetingPreferences> original) {
        this.original = original;
    }

    public LinkedHashMap<Meeting, MeetingPreferences> getCurrent() {
        return current;
    }

    public void setCurrent(LinkedHashMap<Meeting, MeetingPreferences> current) {
        this.current = current;
    }

    public LinkedHashMap<Meeting, MeetingPreferences> getNext() {
        return next;
    }

    public void setNext(LinkedHashMap<Meeting, MeetingPreferences> next) {
        this.next = next;
    }

    public ArrayList<Meeting> getToExclude() {
        return toExclude;
    }

    public void setToExclude(ArrayList<Meeting> toExclude) {
        this.toExclude = toExclude;
    }

    public LinkedHashMap<Meeting, MeetingPreferences> getBest() {
        return best;
    }

    public void setBest(LinkedHashMap<Meeting, MeetingPreferences> best) {
        this.best = best;
    }

    public ArrayList<Location> getLocations() {
        return locations;
    }

    public void setLocations(ArrayList<Location> locations) {
        this.locations = locations;
    }

    private LinkedHashMap<Meeting, MeetingPreferences> findData() {
        LinkedHashMap<Meeting, MeetingPreferences> data = new LinkedHashMap<>();
        ArrayList<Meeting> meetings = MeetingConnection.unscheduledMeetings();
        for (Meeting meeting : meetings){
            MeetingPreferences preferences = PreferencesConnection.preferencesByMeetingID(meeting.getMeetingID());
            if (preferences != null){
                data.put(meeting, preferences);
            }
        }
        return data;
    }


    public void schedule(){
        for (Meeting meeting : toExclude){
            current.remove(meeting);
            next.remove(meeting);
        }
        for (int i = 0; i < BATCH_ITERATIONS; ++i) {
            for (Map.Entry<Meeting, MeetingPreferences> entry : current.entrySet()) {
                Meeting meeting = entry.getKey();
                MeetingPreferences preferences = entry.getValue();
                if (score(meeting, preferences) == 1){
                    continue;
                }
                Meeting scheduledMeeting = scheduleMeeting(meeting, preferences);
                if (score(meeting, preferences) < score(scheduledMeeting, preferences)) {
                    scheduledMeeting.setBooked(true);
                    scheduledMeeting.setBookingFailed(false);
                    next.remove(meeting);
                    next.put(scheduledMeeting, preferences);
                } else {
                    if (i == 0) {
                        next.remove(meeting);
                        meeting.setBookingFailed(true);
                        next.put(meeting, preferences);
                    }
                }
            }
            Meeting victim = findVictim();
            MeetingPreferences preferences = next.get(victim);
            if (score(next) > score(best) || i == 0) {
                best = makeCopy(next);
            }
            if (i != current.size()-1) {
                next.remove(victim);
                revert(victim);
                next.put(victim, preferences);
                current = makeCopy(next);
            }
        }
    }

    private void revert(Meeting victim) {
        victim.setBookingFailed(true);
        victim.setBooked(false);
    }

    private double score(LinkedHashMap<Meeting, MeetingPreferences> meetingsMap) {
        double score = 0;
        for (Map.Entry<Meeting, MeetingPreferences> entry : meetingsMap.entrySet()) {
            Meeting meeting = entry.getKey();
            MeetingPreferences preferences = entry.getValue();
            score += preferences.getMeetingPriority()*score(meeting, preferences);
        }
        return score;
    }

    private double score(Meeting meeting, MeetingPreferences preferences) {
        if (!meeting.isBooked()){
            return 0;
        }
        double maxScore = 1;
        maxScore += preferences.getRoomPriority();
        maxScore += preferences.getLocationPriority();
        maxScore += preferences.getTimePriority();
        double score = 1;
        if(preferences.getLocationPriority() != 0 && meeting.getLocation() != null){
            if (meeting.getLocation().getLocationID() == preferences.getLocation().getLocationID()){
                score += preferences.getLocationPriority();
            }
        }
        if(preferences.getRoomPriority() != 0 && meeting.getRoom() != null){
            if (meeting.getRoom().getRoomID() == preferences.getRoom().getRoomID()){
                score += preferences.getRoomPriority();
            }
        }
        if(preferences.getTimePriority() != 0){
            LocalTime time = meeting.getTimestamp().toLocalDateTime().toLocalTime();
            if (preferences.isAMPreferred() == time.isBefore(LocalTime.NOON)){
                score += preferences.getTimePriority();
            }
        }

        return score/maxScore;
    }

    LinkedHashMap<Meeting, MeetingPreferences> makeCopy(LinkedHashMap<Meeting, MeetingPreferences> map) {
        LinkedHashMap<Meeting, MeetingPreferences> copy = new LinkedHashMap<>();
        for (Map.Entry<Meeting, MeetingPreferences> entry : map.entrySet()){
            copy.put(new Meeting(entry.getKey()), new MeetingPreferences(entry.getValue()));
        }
        return copy;
    }

    private Meeting findVictim() {
        ArrayList<Meeting> meetings = new ArrayList<>(next.keySet());
        Random random = new Random();
        int number = random.nextInt(meetings.size());
        return meetings.get(number);
    }

    public void updateDatabase() {
        for (Map.Entry<Meeting, MeetingPreferences> entry : best.entrySet()) {
            Meeting meeting = entry.getKey();
            MeetingConnection.editMeeting(meeting);
        }
        DetailsConnection.updateLastRun(LocalDateTime.now());
    }

    private Meeting scheduleMeeting(Meeting meeting, MeetingPreferences preferences) {
        Meeting scheduledMeeting = new Meeting(meeting);

        ArrayList<Room> rooms = RoomConnection.findRoomsInLocation(preferences.getLocation());
        int locationPriority = preferences.getLocationPriority();
        int roomPriority = preferences.getRoomPriority();
        int timePriority = preferences.getTimePriority();
        String strongest = "time";
        String middle = "location";
        String weakest = "room";
        if (timePriority <= locationPriority) {
            strongest = "location";
            if (timePriority > roomPriority){
                middle = "time";
                weakest = "room";
            }
            else {
                middle = "room";
                weakest = "time";
            }
        }

        scheduledMeeting.setLocation(preferences.getLocation());
        scheduledMeeting.setRoom(preferences.getRoom());

        // All preferences
        if (checkPeriod(scheduledMeeting, preferences.getLatestDate(), preferences.isAMPreferred())){
            scheduledMeeting.setBooked(true);
            scheduledMeeting.setBookingFailed(false);
            return scheduledMeeting;
        }

        // Strongest two preferences
        if (weakest.equals("time")){
            if (timePriority == 5){
                return meeting;
            }
            if (checkPeriod(scheduledMeeting, preferences.getLatestDate(), !preferences.isAMPreferred())){
                scheduledMeeting.setBooked(true);
                scheduledMeeting.setBookingFailed(false);
                return scheduledMeeting;
            }
        }
        if (roomPriority == 5){
            return meeting;
        }

        if (checkRooms(scheduledMeeting, rooms, preferences.getLatestDate(), preferences.isAMPreferred())){
            scheduledMeeting.setBooked(true);
            scheduledMeeting.setBookingFailed(false);
            return scheduledMeeting;
        }

        // Strongest and weakest
        if (middle.equals("time")){
            if (timePriority == 5){
                return meeting;
            }
            scheduledMeeting.setRoom(preferences.getRoom());
            if (checkPeriod(scheduledMeeting, preferences.getLatestDate(), !preferences.isAMPreferred())){
                scheduledMeeting.setBooked(true);
                scheduledMeeting.setBookingFailed(false);
                return scheduledMeeting;
            }
        }
        // Strongest or weakest two
        if (strongest.equals("location")){
            if (checkRooms(scheduledMeeting, rooms, preferences.getLatestDate(), !preferences.isAMPreferred())){
                scheduledMeeting.setBooked(true);
                scheduledMeeting.setBookingFailed(false);
                return scheduledMeeting;
            }
        }
        if (strongest.equals("time")&& timePriority > (roomPriority + locationPriority)){
             if (checkLocations(scheduledMeeting, preferences.getLatestDate(), preferences.isAMPreferred())){
                 scheduledMeeting.setBooked(true);
                 scheduledMeeting.setBookingFailed(false);
                 return scheduledMeeting;
            }
        }
        if (strongest.equals("time")&& timePriority < 5 && timePriority <= (roomPriority + locationPriority)){
            scheduledMeeting.setLocation(preferences.getLocation());
            scheduledMeeting.setRoom(preferences.getRoom());
            if (checkPeriod(scheduledMeeting, preferences.getLatestDate(), !preferences.isAMPreferred())){
                scheduledMeeting.setBooked(true);
                scheduledMeeting.setBookingFailed(false);
                return scheduledMeeting;
            }
        }

        if (checkRooms(scheduledMeeting, rooms, preferences.getLatestDate(), !preferences.isAMPreferred())){
            scheduledMeeting.setBooked(true);
            scheduledMeeting.setBookingFailed(false);
            return scheduledMeeting;
        }

        if (checkLocations(scheduledMeeting, preferences.getLatestDate(), preferences.isAMPreferred())){
            scheduledMeeting.setBooked(true);
            scheduledMeeting.setBookingFailed(false);
            return scheduledMeeting;
        }

        if (checkLocations(scheduledMeeting, preferences.getLatestDate(), !preferences.isAMPreferred())){
            scheduledMeeting.setBooked(true);
            scheduledMeeting.setBookingFailed(false);
            return scheduledMeeting;
        }
        return meeting;
    }

    private boolean checkLocations(Meeting scheduledMeeting, LocalDate latestDate, boolean AMPreferred) {
        for (Location location : locations) {
            scheduledMeeting.setLocation(location);
            if (checkRooms(scheduledMeeting, RoomConnection.findRoomsInLocation(location), latestDate, AMPreferred)){
                return true;
            }
        }
        return false;
    }

    private boolean checkRooms(Meeting meeting, ArrayList<Room> rooms, LocalDate latestDate, boolean AMPreferred) {
        for (Room room : rooms){
            meeting.setRoom(room);
            if (checkPeriod(meeting,latestDate,AMPreferred)){
                return true;
            }
        }
        return false;
    }

    private boolean checkPeriod(Meeting meeting, LocalDate latestDate, boolean AMPreferred) {
        for(LocalDate date = LocalDate.now().plusDays(1); date.isBefore(latestDate.plusDays(1)); date = date.plusDays(1)){
            LocalTime startTime = checkDay(meeting, date, AMPreferred);
            if (startTime != null){
                meeting.setTimestamp(Timestamp.valueOf(LocalDateTime.of(date,startTime)));
                return true;
            }
        }
        return false;
    }

    private LocalTime checkDay(Meeting meeting, LocalDate date, boolean AMPreferred) {
        Location location = meeting.getLocation();
        Room room = meeting.getRoom();
        if (room.getCapacity() < meeting.getNumberOfPeople()){
            return null;
        }
        Duration cleanupDuration = Duration.between(LocalTime.MIN, room.getCleanupTime());
        Duration meetingDuration = Duration.between(LocalTime.MIN, meeting.getDurationTime().toLocalTime()).plus(cleanupDuration);
        HashSet<Meeting> meetingSet = new HashSet<>(MeetingConnection.findAllByRoomAndDate(room.getRoomID(), date));
        for(AccountAttendee accountAttendee : AttendeeConnection.accountAttendeesForMeeting(meeting.getMeetingID())){
            meetingSet.addAll(MeetingConnection.attendeeMeetingsOnDate(accountAttendee.getAccount().getAccountID(), date));
        }
        next.keySet().stream()
                .filter(Meeting::isBooked)
                .filter(m -> m.getMeetingID() != meeting.getMeetingID())
                .filter(m -> m.getTimestamp().toLocalDateTime().toLocalDate().equals(date))
                .filter(m -> m.getRoom().equals(room))
                .forEach(meetingSet::add);
        ArrayList<Meeting> meetingList = new ArrayList<>(meetingSet);
        meetingList.sort(Comparator.comparing(Meeting::getTimestamp));
        ArrayList<LocalTime> times = new ArrayList<>();
        if (meetingList.isEmpty()){
            if (AMPreferred && location.getOpenTime().isBefore(LocalTime.NOON)){
                return  location.getOpenTime();
            }
            if (!AMPreferred && location.getOpenTime().isAfter(LocalTime.NOON)){
                return location.getOpenTime();
            }
            if (!AMPreferred && location.getOpenTime().isBefore(LocalTime.NOON) && location.getCloseTime().isAfter(LocalTime.NOON)){
                return LocalTime.NOON;
            }
            return null;
        }
        if (location.getOpenTime().plus(meetingDuration).isBefore(meetingList.get(0).startTime())){
            times.add(location.getOpenTime());
        }
        boolean end = false;
        LocalTime checked = LocalTime.MIN;
        for (int i = 0; i < meetingList.size()-1; ++i){
            Meeting current = meetingList.get(i);
            Meeting next = meetingList.get(i+1);
            if (current.finishTime().plus(meetingDuration).isBefore(current.startTime())){
                end = true;
                break;
            }
            if (current.finishTime().plus(meetingDuration).isAfter(location.getCloseTime())){
                end = true;
                break;
            }
            if(current.finishTime().plus(cleanupDuration).plus(meetingDuration).isBefore(next.startTime())){
                if (!checked.isAfter(current.finishTime().plus(cleanupDuration))){
                    times.add(current.finishTime().plus(cleanupDuration));
                    if (!AMPreferred && current.finishTime().isBefore(LocalTime.NOON) &&
                            LocalTime.NOON.plus(meetingDuration).isBefore(next.startTime())) {
                        times.add(LocalTime.NOON);
                    }
                }
            }
            else{
                if (current.finishTime().isAfter(checked)){
                    checked = current.finishTime();
                }
            }
        }
        LocalTime lastTime = meetingList.get(meetingList.size()-1).finishTime().plus(cleanupDuration);
        if (checked.isAfter(lastTime)){
            lastTime = checked;
        }
        if (!end && location.getCloseTime().minus(meetingDuration).isAfter(lastTime)){
            times.add(lastTime);
        }
        for (LocalTime time : times){
            if (time.isAfter(location.getCloseTime()) || time.isBefore(location.getOpenTime())){
                continue;
            }
            LocalTime finishTime = time.plus(meetingDuration);
            if (finishTime.isAfter(location.getCloseTime()) || finishTime.isBefore(location.getOpenTime())){
                continue;
            }
            if (finishTime.isBefore(time)){
                continue;
            }
            if (AMPreferred && time.isBefore(LocalTime.NOON) || (!AMPreferred && !time.isBefore(LocalTime.NOON))){
                return time;
            }
        }
        return null;
    }

    public String batchDetails() {

        return "Last Run At: " + DetailsConnection.getLastRun();
    }

    public double score(Meeting meeting) {
        MeetingPreferences preferences = PreferencesConnection.preferencesByMeetingID(meeting.getMeetingID());
        return score(meeting,preferences);
    }
}

