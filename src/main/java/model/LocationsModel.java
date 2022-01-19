package model;

import connect.LocationConnection;
import structure.Location;

import java.util.ArrayList;

public class LocationsModel {
    private ArrayList<Location> locations;
    private ArrayList<Location> filteredLocations;

    public LocationsModel() {
        this.locations = new ArrayList<>();
    }

    public ArrayList<Location> getLocations() {
        return locations;
    }

    public void setLocations(ArrayList<Location> locations) {
        this.locations = locations;
    }

    public ArrayList<Location> getFilteredLocations() {
        return filteredLocations;
    }

    public void setFilteredLocations(ArrayList<Location> filteredLocations) {
        this.filteredLocations = filteredLocations;
    }

    public void fetchData(){
        findLocations();
    }
    private void findLocations(){
        locations = LocationConnection.findAllLocations();
        filteredLocations = locations;
    }

    public void search(String text) {
        ArrayList<Location> filtered = new ArrayList<>();
        locations.stream()
                .filter(location -> (location.getName().toLowerCase().contains(text.toLowerCase()) ||
                        location.getAddress().toLowerCase().contains(text.toLowerCase())))
                .forEach(filtered::add);
        filteredLocations = filtered;
    }
}
