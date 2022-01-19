package utility;

import connect.LocationConnection;
import connect.RoomConnection;
import structure.Location;
import structure.ProvisionalMeeting;
import structure.Room;

import java.time.LocalTime;
import java.util.ArrayList;

public class LocationUtility {
    public LocationUtility() {
    }

    public boolean isLocation(String locationDetails) {
        Location location = recoverLocation(locationDetails);
        return location != null;
    }

    public boolean isValidRoom(String roomName, String locationDetails) {
        Location location = recoverLocation(locationDetails);
        if (location == null){
            return false;
        }
        Room room = RoomConnection.roomByNameAndLocationID(roomName, location.getLocationID());
        return room != null;
    }

    public Location recoverLocation(String locationDetails){
        String locationName = recoverLocationName(locationDetails);
        String postcode = recoverPostcode(locationDetails);
        return LocationConnection.locationByNameAndPostcode(locationName,postcode);
    }

    public boolean locationOpen(String locationDetails, LocalTime time, ProvisionalMeeting meeting) {
        Location location = recoverLocation(locationDetails);
        return !time.isBefore(location.getOpenTime()) && !time.isAfter(location.getCloseTime()) &&
                !time.plusHours(meeting.getHours()).plusMinutes(meeting.getMinutes()).isAfter(location.getCloseTime());
    }

    public boolean fitInRoom(String roomName, String locationDetails, int numberOfPeople) {
        Location location = recoverLocation(locationDetails);
        Room room = RoomConnection.roomByNameAndLocationID(roomName, location.getLocationID());
        if (room == null){
            return false;
        }
        return room.getCapacity() > numberOfPeople;
    }

    public ArrayList<String> getLocationDetails(ArrayList<Location> locations) {
        ArrayList<String> details = new ArrayList<>();
        for (Location location : locations){
            details.add(location.details());
        }
        return details;
    }

    public String recoverPostcode(String locationDetails) {
        String[] words = locationDetails.split(" {4}");
        return words[words.length - 1];
    }

    public String recoverLocationName(String locationDetails) {
        String[] words = locationDetails.split(" {4}");
        return words[0];
    }

    public ArrayList<String> getRoomNames(ArrayList<Room> rooms) {
        ArrayList<String> names = new ArrayList<>();
        rooms.stream()
                .map(Room::getName)
                .forEach(names::add);
        return names;
    }

    public Room recoverRoom(String roomName, Location location) {
        return RoomConnection.roomByNameAndLocationID(roomName, location.getLocationID());
    }
}
