package structure;

import connect.MeetingConnection;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Objects;

public class Location {
    private int locationID;
    private String name;
    private String address;
    private String postcode;
    private LocalTime openTime;
    private LocalTime closeTime;
    private boolean isOpenSpace;
    private String description;

    public Location(int locationID, String name, String address, String postcode,
                    LocalTime openTime, LocalTime closeTime, boolean isOpenSpace, String description) {
        this.locationID = locationID;
        this.name = name;
        this.address = address;
        this.postcode = postcode;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.isOpenSpace = isOpenSpace;
        this.description = description;
    }

    public Location(String name, String address, String postcode,
                    LocalTime openTime, LocalTime closeTime, boolean isOpenSpace, String description) {
        this.locationID = -1;
        this.name = name;
        this.address = address;
        this.postcode = postcode;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.isOpenSpace = isOpenSpace;
        this.description = description;
    }

    public Location() {

    }

    public int getLocationID() {
        return locationID;
    }

    public void setLocationID(int locationID) {
        this.locationID = locationID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public LocalTime getOpenTime() {
        return openTime;
    }

    public void setOpenTime(LocalTime openTime) {
        this.openTime = openTime;
    }

    public LocalTime getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(LocalTime closeTime) {
        this.closeTime = closeTime;
    }

    public boolean isOpenSpace() {
        return isOpenSpace;
    }

    public void setOpenSpace(boolean openSpace) {
        isOpenSpace = openSpace;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return locationID == location.locationID &&
                isOpenSpace == location.isOpenSpace &&
                Objects.equals(name, location.name) &&
                Objects.equals(address, location.address) &&
                Objects.equals(postcode, location.postcode) &&
                Objects.equals(openTime, location.openTime) &&
                Objects.equals(closeTime, location.closeTime) &&
                Objects.equals(description, location.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(locationID, name, address, postcode, openTime, closeTime, isOpenSpace, description);
    }

    @Override
    public String toString() {
        return "Location{" +
                "locationID=" + locationID +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", postcode='" + postcode + '\'' +
                ", openTime=" + openTime +
                ", closeTime=" + closeTime +
                ", isOpenSpace=" + isOpenSpace +
                ", description='" + description + '\'' +
                '}';
    }

    public boolean hasTimeSlot(LocalTime duration, ArrayList<AccountAttendee> accountAttendees) {
        Duration meetingDuration = Duration.between(LocalTime.MIN,duration);
        for (LocalDate date = LocalDate.now().plusDays(1); date.isBefore(LocalDate.now().plusDays(8)); date = date.plusDays(1)){
            ArrayList<Meeting> meetings = MeetingConnection.findAllByLocationAndDate(locationID, date);
            if (meetings.isEmpty()){
                return openTime.plus(meetingDuration).isBefore(closeTime);
            }
            if (openTime.plus(meetingDuration).isBefore(meetings.get(0).startTime())){
                return true;
            }
            for (int i = 0; i < meetings.size()-1; ++i){
                Meeting current = meetings.get(i);
                Meeting next = meetings.get(i+1);
                if(current.finishTime().plus(meetingDuration).isBefore(next.startTime())){
                    return true;
                }
            }
            if (closeTime.minus(meetingDuration).isAfter(meetings.get(meetings.size()-1).finishTime())){
                return true;
            }
        }
        return false;
    }

    public boolean hasTimeSlot(LocalTime duration, ArrayList<AccountAttendee> accountAttendees, LocalDateTime dateTime) {
        Duration meetingDuration = Duration.between(LocalTime.MIN,duration);
        ArrayList<Meeting> meetings = MeetingConnection.findAllByLocationAndDate(locationID, dateTime.toLocalDate());
        LocalTime startTime = dateTime.toLocalTime();
        LocalTime finishTime = startTime.plus(meetingDuration);
        if (startTime.isBefore(openTime)){
            return false;
        }
        if (finishTime.isAfter(closeTime)){
            return false;
        }
        if (meetings.isEmpty()){
            return openTime.plus(meetingDuration).isBefore(closeTime);
        }
        for (Meeting meeting : meetings){
            LocalTime start = meeting.startTime();
            LocalTime finish = meeting.finishTime();
            if (start.isAfter(startTime) && start.isBefore(finishTime)){
                return false;
            }
            if (finish.isAfter(startTime) && finish.isBefore(finishTime)){
                return false;
            }
        }
        return true;
    }
    public boolean hasTimeSlot(LocalTime duration, ArrayList<AccountAttendee> accountAttendees, LocalDate date) {
        Duration meetingDuration = Duration.between(LocalTime.MIN,duration);
        ArrayList<Meeting> meetings = MeetingConnection.findAllByLocationAndDate(locationID, date);
        if (meetings.isEmpty()){
            return openTime.plus(meetingDuration).isBefore(closeTime);
        }
        if (openTime.plus(meetingDuration).isBefore(meetings.get(0).startTime())){
            return true;
        }
        for (int i = 0; i < meetings.size()-1; ++i){
            Meeting current = meetings.get(i);
            Meeting next = meetings.get(i+1);
            if(current.finishTime().plus(meetingDuration).isBefore(next.startTime())){
                return true;
            }
        }
        return closeTime.minus(meetingDuration).isAfter(meetings.get(meetings.size() - 1).finishTime());
    }
    public boolean hasTimeSlot(LocalTime duration, ArrayList<AccountAttendee> accountAttendees, LocalTime startTime) {
        Duration meetingDuration = Duration.between(LocalTime.MIN,duration);
        LocalTime finishTime = startTime.plus(meetingDuration);
        for (LocalDate date = LocalDate.now().plusDays(1); date.isBefore(LocalDate.now().plusDays(8)); date = date.plusDays(1)){
            if (startTime.isBefore(openTime)){
                continue;
            }
            if (finishTime.isAfter(closeTime)){
                continue;
            }
            ArrayList<Meeting> meetings = MeetingConnection.findAllByLocationAndDate(locationID, date);
            if (meetings.isEmpty()){
                if (openTime.plus(meetingDuration).isBefore(closeTime)){
                    return true;
                }
                continue;
            }
            boolean next = false;
            for (Meeting meeting : meetings){
                LocalTime start = meeting.startTime();
                LocalTime finish = meeting.finishTime();
                if (start.isAfter(startTime) && start.isBefore(finishTime)){
                    next = true;
                    break;
                }
                if (finish.isAfter(startTime) && finish.isBefore(finishTime)){
                    next = true;
                    break;
                }
            }
            if (next){
                continue;
            }
            return true;
        }
        return false;
    }

    public String details() {
        return name + "    " + postcode;
    }
}
