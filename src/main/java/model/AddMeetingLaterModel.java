package model;

import connect.*;
import structure.*;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Model for the finalisation screen for meetings intended for the batch scheduler
 */
public class AddMeetingLaterModel {
    private ArrayList<Location> locations;
    private ArrayList<Room> rooms;

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

    public void fetchData() {
        findLocations();
        findRooms();
    }

    private void findRooms() {
        rooms = RoomConnection.findAllRooms();
    }

    private void findLocations() {
        locations = LocationConnection.findAllLocations();
    }

    public int finaliseMeeting(ProvisionalMeeting provisionalMeeting, MeetingPreferences preferences) {
        Meeting meeting = provisionalMeeting.toMeeting();
        int meetingID = MeetingConnection.addMeeting(meeting);
        if (meetingID == -1){
            return meetingID;
        }
        HashSet<Attendee> attendees = provisionalMeeting.getAttendees();
        for (Attendee attendee : attendees){
            attendee.setMeetingID(meetingID);
        }
        AttendeeConnection.addAllAttendees(new ArrayList<>(attendees));
        preferences.setMeetingID(meetingID);
        PreferencesConnection.addPreferences(preferences);
        return meetingID;
    }
}
