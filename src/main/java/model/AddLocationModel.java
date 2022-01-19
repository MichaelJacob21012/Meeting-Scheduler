package model;

import structure.Location;
import connect.LocationConnection;

import java.time.LocalTime;

/**
 * Model class for adding locations based on user input
 */
public class AddLocationModel {
    /**
     * Add location given all information barring the ID
     * @param name
     * @param address
     * @param postcode
     * @param openTime
     * @param closeTime
     * @param openSpace
     * @param description
     */
    public void addLocation(String name, String address, String postcode, LocalTime openTime, LocalTime closeTime,
                            Boolean openSpace, String description) {
        Location location = new Location(name,address,postcode,openTime,closeTime,openSpace,description);
        LocationConnection.addLocation(location);
    }
}
