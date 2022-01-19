package model;

import structure.Location;
import connect.LocationConnection;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class AddLocationModelTest {

    private final AddLocationModel model = new AddLocationModel();
    private final TestCaseGenerator generator = new TestCaseGenerator();
    private static final int TEST_LOCATIONS = 5;

    @Test
    public void addLocation() {
        int beforeLocations = LocationConnection.findAllLocations().size();
        ArrayList<Location> locations = generator.generateLocations(TEST_LOCATIONS);
        for (Location location : locations){
            model.addLocation(location.getName(), location.getAddress(), location.getPostcode(),
                    location.getOpenTime(), location.getCloseTime(), location.isOpenSpace(), location.getDescription());
        }
        int afterLocations = LocationConnection.findAllLocations().size();
        Assert.assertEquals(beforeLocations, afterLocations - 5);
        ArrayList<Location> addedLocations = new ArrayList<>();
        for (Location location : locations){
            addedLocations.add(LocationConnection.locationByNameAndPostcode(location.getName(), location.getPostcode()));
        }
        for (Location location : addedLocations){
            LocationConnection.deleteLocation(location.getLocationID());
        }
    }
}