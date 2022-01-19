package structure;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;

public class ProvisionalMeeting {
    private final Account organiser;
    private String title;
    private int hours;
    private int minutes;
    private String description;
    private boolean scheduleNow;
    private HashSet<Attendee> attendees;
    private Location location;
    private Room room;
    private LocalDate date;
    private LocalTime startTime;
    private MeetingPreferences meetingPreferences;

    public ProvisionalMeeting(Account account, String title, int hours, int minutes, String description, boolean scheduleNow) {
        organiser = account;
        this.title = title;
        this.hours = hours;
        this.minutes = minutes;
        this.description = description;
        this.scheduleNow = scheduleNow;
        attendees = new HashSet<>();
        location = new Location();
        room = new Room();
        meetingPreferences = null;
    }

    public ProvisionalMeeting(ProvisionalMeeting other) {
        this.organiser = other.organiser;
        this.title = other.title;
        this.hours = other.hours;
        this.minutes = other.minutes;
        this.description = other.description;
        this.scheduleNow = other.scheduleNow;
        this.attendees = other.attendees;
        this.location = other.location;
        this.room = other.room;
        this.date = other.date;
        this.startTime = other.startTime;
        this.meetingPreferences = other.meetingPreferences;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isScheduleNow() {
        return scheduleNow;
    }

    public void setScheduleNow(boolean scheduleNow) {
        this.scheduleNow = scheduleNow;
    }

    public HashSet<Attendee> getAttendees() {
        return attendees;
    }

    public void setAttendees(HashSet<Attendee> attendees) {
        this.attendees = attendees;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public MeetingPreferences getMeetingPreferences() {
        return meetingPreferences;
    }

    public void setMeetingPreferences(MeetingPreferences meetingPreferences) {
        this.meetingPreferences = meetingPreferences;
    }

    @Override
    public String toString() {
        return "ProvisionalMeeting{" +
                "organiser=" + organiser +
                ", title='" + title + '\'' +
                ", hours=" + hours +
                ", minutes=" + minutes +
                ", description='" + description + '\'' +
                ", scheduleNow=" + scheduleNow +
                ", attendees=" + attendees +
                ", location=" + location +
                ", room=" + room +
                ", date=" + date +
                ", startTime=" + startTime +
                ", meetingPreferences=" + meetingPreferences +
                '}';
    }

    public LocalTime getFinishTime(){
        return startTime.plusHours(hours).plusMinutes(minutes);
    }

    public Meeting toMeeting() {
        int numberOfPeople = attendees.size();
        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
        if (!(date == null)){
            timestamp = Timestamp.valueOf(LocalDateTime.of(date, startTime));
        }
        Time duration = Time.valueOf(LocalTime.of(hours,minutes));
        boolean booked = false;
        if (meetingPreferences == null){
            booked = true;
        }
        return new Meeting(-1,organiser,title,numberOfPeople,timestamp,duration,location,
                room,booked,false,description);
    }

    public void addAttendee(Attendee attendee){
        attendees.add(attendee);
    }

    public void addAttendees(ArrayList<Attendee> attendees) {
        this.attendees.addAll(attendees);
    }

    public ArrayList<AccountAttendee> getAccountAttendees() {
        ArrayList<AccountAttendee> accountAttendees = new ArrayList<>();
        attendees.stream()
                .filter(attendee -> attendee instanceof AccountAttendee)
                .forEach(attendee -> accountAttendees.add((AccountAttendee)attendee));
        return accountAttendees;
    }

    public ArrayList<NoAccountAttendee> getNoAccountAttendees() {
        ArrayList<NoAccountAttendee> noAccountAttendees = new ArrayList<>();
        attendees.stream()
                .filter(attendee -> attendee instanceof NoAccountAttendee)
                .forEach(attendee -> noAccountAttendees.add((NoAccountAttendee)attendee));
        return noAccountAttendees;
    }

    public Duration duration() {
        return Duration.between(LocalTime.MIN, LocalTime.of(hours, minutes));
    }
}
