package structure;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

public class Meeting {
    private int meetingID;
    private Account account;
    private String title;
    private int numberOfPeople;
    private Timestamp timestamp;
    private Time durationTime;
    private Location location;
    private Room room;
    private boolean booked;
    private boolean bookingFailed;
    private String description;

    public Meeting(int meetingID, Account account, String title, int numberOfPeople, Timestamp timestamp, Time duration,
                   Location location, Room room, boolean booked, boolean bookingFailed, String description) {
        this.meetingID = meetingID;
        this.account = account;
        this.title = title;
        this.numberOfPeople = numberOfPeople;
        this.timestamp = timestamp;
        this.durationTime = duration;
        this.location = location;
        this.room = room;
        this.booked = booked;
        this.bookingFailed = bookingFailed;
        this.description = description;
    }

    public Meeting(Meeting other) {
        this.meetingID = other.meetingID;
        this.account = other.account;
        this.title = other.title;
        this.numberOfPeople = other.numberOfPeople;
        this.timestamp = other.timestamp;
        this.durationTime = other.durationTime;
        this.location = other.location;
        this.room = other.room;
        this.booked = other.booked;
        this.bookingFailed = other.bookingFailed;
        this.description = other.description;
    }

    public Meeting() {

    }

    public int getMeetingID() {
        return meetingID;
    }

    public void setMeetingID(int meetingID) {
        this.meetingID = meetingID;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Time getDurationTime() {
        return durationTime;
    }

    public void setDurationTime(Time durationTime) {
        this.durationTime = durationTime;
    }

    public int getNumberOfPeople() {
        return numberOfPeople;
    }

    public void setNumberOfPeople(int numberOfPeople) {
        this.numberOfPeople = numberOfPeople;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
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

    public boolean isBooked() {
        return booked;
    }

    public void setBooked(boolean booked) {
        this.booked = booked;
    }

    public boolean isBookingFailed() {
        return bookingFailed;
    }

    public void setBookingFailed(boolean bookingFailed) {
        this.bookingFailed = bookingFailed;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public LocalTime startTime(){
        return timestamp.toLocalDateTime().toLocalTime();
    }

    public LocalTime finishTime(){
        int meetingHours = durationTime.toLocalTime().getHour();
        int meetingMinutes = durationTime.toLocalTime().getMinute();
        return startTime().plusHours(meetingHours).plusMinutes(meetingMinutes);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Meeting meeting = (Meeting) o;
        return meetingID == meeting.meetingID &&
                numberOfPeople == meeting.numberOfPeople &&
                booked == meeting.booked &&
                bookingFailed == meeting.bookingFailed &&
                Objects.equals(account, meeting.account) &&
                Objects.equals(title, meeting.title) &&
                Objects.equals(timestamp, meeting.timestamp) &&
                Objects.equals(durationTime, meeting.durationTime) &&
                Objects.equals(location, meeting.location) &&
                Objects.equals(room, meeting.room) &&
                Objects.equals(description, meeting.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(meetingID, account, title, numberOfPeople, timestamp, durationTime, location, room, booked, bookingFailed, description);
    }



    @Override
    public String toString() {
        return "Meeting{" +
                "meetingID=" + meetingID +
                ", account=" + account +
                ", title='" + title + '\'' +
                ", numberOfPeople=" + numberOfPeople +
                ", timestamp=" + timestamp +
                ", durationTime=" + durationTime +
                ", location=" + location +
                ", room=" + room +
                ", booked=" + booked +
                ", bookingFailed=" + bookingFailed +
                ", description='" + description + '\'' +
                '}';
    }
    public Duration duration(){
        return Duration.between(LocalTime.MIN, durationTime.toLocalTime());
    }

    public boolean inPast() {
        return LocalDateTime.now().isAfter(timestamp.toLocalDateTime());
    }
    public boolean timeClash(Meeting other){
        if (!startTime().isBefore(other.startTime()) && startTime().isBefore(other.finishTime())){
            return true;
        }
        if (finishTime().isAfter(other.startTime()) && !finishTime().isAfter(other.finishTime())){
            return true;
        }
        return !startTime().isAfter(other.startTime()) && !finishTime().isBefore(other.finishTime());

    }

    public boolean timeClash(LocalTime startTime, LocalTime finishTime){
        if (!startTime().isBefore(startTime) && startTime().isBefore(finishTime)){
            return true;
        }
        if (finishTime().isAfter(startTime) && !finishTime().isAfter(finishTime)){
            return true;
        }
        if (!startTime().isAfter(startTime) && !finishTime().isBefore(finishTime)){
            return finishTime.isAfter(startTime);
        }
        return false;

    }
}
