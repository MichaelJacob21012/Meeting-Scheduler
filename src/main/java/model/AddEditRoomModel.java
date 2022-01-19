package model;

import connect.LocationConnection;
import connect.RoomConnection;
import structure.Location;
import structure.Room;
import utility.LocationUtility;

import java.time.LocalTime;
import java.util.ArrayList;

/**
 * Model class for adding or editing accounts based on user input
 */
public class AddEditRoomModel {
    private ArrayList<Location> locations;
    private final LocationUtility locationUtility = new LocationUtility();
    public void findLocations(){
        locations = LocationConnection.findAllLocations();
    }

    public ArrayList<Location> getLocations() {
        return locations;
    }

    public void setLocations(ArrayList<Location> locations) {
        this.locations = locations;
    }

    public void addRoom(String name, String locationDetails, String description, String capacity, String cost, String cleanupTime) {
        Room room = new Room();
        room.setName(name);
        Location location = LocationConnection.locationByNameAndPostcode(
                locationUtility.recoverLocationName(locationDetails), locationUtility.recoverPostcode(locationDetails));
        room.setLocation(location);
        room.setDescription(description);
        room.setCapacity(Integer.parseInt(capacity));
        room.setCost(Double.parseDouble(cost));
        room.setCleanupTime(LocalTime.of(0, Integer.parseInt(cleanupTime)));
        RoomConnection.addRoom(room);
    }

}
