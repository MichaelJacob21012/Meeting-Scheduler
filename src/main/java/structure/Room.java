package structure;

import connect.MeetingConnection;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

public class Room {
    private int roomID;
    private String name;
    private Location location;
    private int capacity;
    private String description;
    private double cost;
    private LocalTime cleanupTime;

    public Room(int roomID, String name, Location location, int capacity, String description, double cost, LocalTime cleanupTime) {
        this.roomID = roomID;
        this.name = name;
        this.location = location;
        this.capacity = capacity;
        this.description = description;
        this.cost = cost;
        this.cleanupTime = cleanupTime;
    }

    public Room(String name, Location location, int capacity, String description, double cost, LocalTime cleanupTime) {
        this.roomID = -1;
        this.name = name;
        this.location = location;
        this.capacity = capacity;
        this.description = description;
        this.cost = cost;
        this.cleanupTime = cleanupTime;
    }

    public Room() {
    }

    public int getRoomID() {
        return roomID;
    }

    public void setRoomID(int roomID) {
        this.roomID = roomID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public LocalTime getCleanupTime() {
        return cleanupTime;
    }

    public void setCleanupTime(LocalTime cleanupTime) {
        this.cleanupTime = cleanupTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return roomID == room.roomID &&
                capacity == room.capacity &&
                Double.compare(room.cost, cost) == 0 &&
                Objects.equals(name, room.name) &&
                Objects.equals(location, room.location) &&
                Objects.equals(description, room.description) &&
                Objects.equals(cleanupTime, room.cleanupTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomID, name, location, capacity, description, cost, cleanupTime);
    }

    @Override
    public String toString() {
        return "Room{" +
                "roomID=" + roomID +
                ", name='" + name + '\'' +
                ", location=" + location +
                ", capacity=" + capacity +
                ", description='" + description + '\'' +
                ", cost=" + cost +
                ", cleanupTime=" + cleanupTime +
                '}';
    }

    public boolean hasTimeSlot(LocalTime duration, ArrayList<AccountAttendee> accountAttendees) {
        if (capacity < accountAttendees.size()){
            return false;
        }
        Duration cleanupDuration = Duration.between(LocalTime.MIN,cleanupTime);
        Duration meetingDuration = Duration.between(LocalTime.MIN,duration).plus(cleanupDuration);
        for (LocalDate date = LocalDate.now().plusDays(1); date.isBefore(LocalDate.now().plusDays(8)); date = date.plusDays(1)){
            ArrayList<Meeting> meetings = MeetingConnection.findAllByRoomAndDate(roomID, date);
            if (meetings.isEmpty()){
                return true;
            }
            if (location.getOpenTime().plus(meetingDuration).isBefore(meetings.get(0).startTime())){
                return true;
            }
            for (int i = 0; i < meetings.size()-1; ++i){
                Meeting current = meetings.get(i);
                Meeting next = meetings.get(i+1);
                if(current.finishTime().plus(cleanupDuration).plus(meetingDuration).isBefore(next.startTime())){
                    return true;
                }
            }
            if (location.getCloseTime().minus(meetingDuration).isAfter(meetings.get(meetings.size()-1).finishTime().plus(cleanupDuration))){
                return true;
            }
        }
        return false;

    }

    public boolean hasTimeSlot(LocalTime duration, ArrayList<AccountAttendee> accountAttendees, LocalDateTime dateTime) {
        if (capacity < accountAttendees.size()){
            return false;
        }
        Duration cleanupDuration = Duration.between(LocalTime.MIN,cleanupTime);
        Duration meetingDuration = Duration.between(LocalTime.MIN,duration).plus(cleanupDuration);
        ArrayList<Meeting> meetings = MeetingConnection.findAllByRoomAndDate(roomID, dateTime.toLocalDate());
        LocalTime startTime = dateTime.toLocalTime();
        LocalTime finishTime = startTime.plus(meetingDuration);
        if (meetings.isEmpty()){
            return true;
        }
        for (Meeting meeting : meetings){
            LocalTime start = meeting.startTime();
            LocalTime finish = meeting.finishTime().plus(cleanupDuration);
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
        if (capacity < accountAttendees.size()){
            return false;
        }
        Duration cleanupDuration = Duration.between(LocalTime.MIN,cleanupTime);
        Duration meetingDuration = Duration.between(LocalTime.MIN,duration).plus(cleanupDuration);
        ArrayList<Meeting> meetings = MeetingConnection.findAllByRoomAndDate(roomID, date);
        if (meetings.isEmpty()){
            return true;
        }
        if (location.getOpenTime().plus(meetingDuration).isBefore(meetings.get(0).startTime())){
            return true;
        }
        for (int i = 0; i < meetings.size()-1; ++i){
            Meeting current = meetings.get(i);
            Meeting next = meetings.get(i+1);
            if(current.finishTime().plus(cleanupDuration).plus(meetingDuration).isBefore(next.startTime())){
                return true;
            }
        }
        return location.getCloseTime().minus(meetingDuration).isAfter(meetings.get(meetings.size() - 1).finishTime().plus(cleanupDuration));
    }

    public boolean hasTimeSlot(LocalTime duration, ArrayList<AccountAttendee> accountAttendees, LocalTime startTime) {
        if (capacity < accountAttendees.size()){
            return false;
        }
        Duration cleanupDuration = Duration.between(LocalTime.MIN,cleanupTime);
        Duration meetingDuration = Duration.between(LocalTime.MIN,duration).plus(cleanupDuration);
        LocalTime finishTime = startTime.plus(meetingDuration);
        for (LocalDate date = LocalDate.now().plusDays(1); date.isBefore(LocalDate.now().plusDays(8)); date = date.plusDays(1)){
            ArrayList<Meeting> meetings = MeetingConnection.findAllByRoomAndDate(roomID, date);
            if (meetings.isEmpty()){
                return true;
            }
            boolean next = false;
            for (Meeting meeting : meetings){
                LocalTime start = meeting.startTime();
                LocalTime finish = meeting.finishTime().plus(cleanupDuration);
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

    public HashSet<LocalDate> availableDates(LocalTime duration, ArrayList<AccountAttendee> accountAttendees) {
        HashSet<LocalDate> availableDates = new HashSet<>();
        Duration cleanupDuration = Duration.between(LocalTime.MIN,cleanupTime);
        Duration meetingDuration = Duration.between(LocalTime.MIN,duration).plus(cleanupDuration);
        for (LocalDate date = LocalDate.now().plusDays(1); date.isBefore(LocalDate.now().plusDays(8)); date = date.plusDays(1)){
            ArrayList<Meeting> meetings = MeetingConnection.findAllByRoomAndDate(roomID, date);
            if (meetings.isEmpty()){
               availableDates.add(date);
               continue;
            }
            if (location.getOpenTime().plus(meetingDuration).isBefore(meetings.get(0).startTime())){
                availableDates.add(date);
            }
            for (int i = 0; i < meetings.size()-1; ++i){
                Meeting current = meetings.get(i);
                Meeting next = meetings.get(i+1);
                if(current.finishTime().plus(cleanupDuration).plus(meetingDuration).isBefore(next.startTime())){
                    availableDates.add(date);
                }
            }
            if (location.getCloseTime().minus(meetingDuration).isAfter(meetings.get(meetings.size()-1).finishTime().plus(cleanupDuration))){
                availableDates.add(date);
            }
        }
        return availableDates;
    }

    public HashSet<LocalDate> availableDates(LocalTime duration, ArrayList<AccountAttendee> accountAttendees, LocalTime startTime) {
        HashSet<LocalDate> availableDates = new HashSet<>();
        Duration cleanupDuration = Duration.between(LocalTime.MIN,cleanupTime);
        Duration meetingDuration = Duration.between(LocalTime.MIN,duration).plus(cleanupDuration);
        LocalTime finishTime = startTime.plus(meetingDuration);
        for (LocalDate date = LocalDate.now().plusDays(1); date.isBefore(LocalDate.now().plusDays(8)); date = date.plusDays(1)) {
            ArrayList<Meeting> meetings = MeetingConnection.findAllByRoomAndDate(roomID, date);
            if (meetings.isEmpty()) {
                availableDates.add(date);
                continue;
            }
            boolean next = false;
            for (Meeting meeting : meetings) {
                LocalTime start = meeting.startTime();
                LocalTime finish = meeting.finishTime().plus(cleanupDuration);
                if (start.isAfter(startTime) && start.isBefore(finishTime)) {
                    next = true;
                    break;
                }
                if (finish.isAfter(startTime) && finish.isBefore(finishTime)) {
                    next = true;
                    break;
                }
            }
            if (next) {
                continue;
            }
            availableDates.add(date);
        }
        return availableDates;
    }

}
