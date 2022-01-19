package structure;

import java.time.LocalDate;
import java.util.Objects;

public class MeetingPreferences {
    private int meetingID;
    private int meetingPriority;
    private Location location;
    private int locationPriority;
    private Room room;
    private int roomPriority;
    private LocalDate latestDate;
    private boolean AMPreferred;
    private int timePriority;

    public MeetingPreferences(int meetingID, int meetingPriority, Location location, int locationPriority, Room room, int roomPriority, LocalDate latestDate, boolean AMPreferred, int timePriority) {
        this.meetingID = meetingID;
        this.meetingPriority = meetingPriority;
        this.location = location;
        this.locationPriority = locationPriority;
        this.room = room;
        this.roomPriority = roomPriority;
        this.latestDate = latestDate;
        this.AMPreferred = AMPreferred;
        this.timePriority = timePriority;
    }

    public MeetingPreferences(MeetingPreferences other) {
        this.meetingID = other.meetingID;
        this.meetingPriority = other.meetingPriority;
        this.location = other.location;
        this.locationPriority = other.locationPriority;
        this.room = other.room;
        this.roomPriority = other.roomPriority;
        this.latestDate = other.latestDate;
        this.AMPreferred = other.AMPreferred;
        this.timePriority = other.timePriority;
    }

    public int getMeetingID() {
        return meetingID;
    }

    public void setMeetingID(int meetingID) {
        this.meetingID = meetingID;
    }

    public int getMeetingPriority() {
        return meetingPriority;
    }

    public void setMeetingPriority(int meetingPriority) {
        this.meetingPriority = meetingPriority;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getLocationPriority() {
        return locationPriority;
    }

    public void setLocationPriority(int locationPriority) {
        this.locationPriority = locationPriority;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public int getRoomPriority() {
        return roomPriority;
    }

    public void setRoomPriority(int roomPriority) {
        this.roomPriority = roomPriority;
    }

    public LocalDate getLatestDate() {
        return latestDate;
    }

    public void setLatestDate(LocalDate latestDate) {
        this.latestDate = latestDate;
    }

    public boolean isAMPreferred() {
        return AMPreferred;
    }

    public void setAMPreferred(boolean AMPreferred) {
        this.AMPreferred = AMPreferred;
    }

    public int getTimePriority() {
        return timePriority;
    }

    public void setTimePriority(int timePriority) {
        this.timePriority = timePriority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MeetingPreferences that = (MeetingPreferences) o;
        return meetingID == that.meetingID &&
                meetingPriority == that.meetingPriority &&
                locationPriority == that.locationPriority &&
                roomPriority == that.roomPriority &&
                AMPreferred == that.AMPreferred &&
                timePriority == that.timePriority &&
                Objects.equals(location, that.location) &&
                Objects.equals(room, that.room) &&
                Objects.equals(latestDate, that.latestDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(meetingID, meetingPriority, location, locationPriority, room, roomPriority, latestDate, AMPreferred, timePriority);
    }

    @Override
    public String toString() {
        return "MeetingPreferences{" +
                "meetingID=" + meetingID +
                ", meetingPriority=" + meetingPriority +
                ", location=" + location +
                ", locationPriority=" + locationPriority +
                ", room=" + room +
                ", roomPriority=" + roomPriority +
                ", latestDate=" + latestDate +
                ", AMPreferred=" + AMPreferred +
                ", timePriority=" + timePriority +
                '}';
    }
}
