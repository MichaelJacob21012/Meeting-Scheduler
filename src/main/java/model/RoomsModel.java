package model;

import connect.RoomConnection;
import structure.Room;

import java.util.ArrayList;

public class RoomsModel {
    private ArrayList<Room> rooms;
    private ArrayList<Room> filteredRooms;

    public RoomsModel() {
        this.rooms = new ArrayList<>();
    }

    public ArrayList<Room> getRooms() {
        return rooms;
    }

    public void setRooms(ArrayList<Room> rooms) {
        this.rooms = rooms;
    }

    public ArrayList<Room> getFilteredRooms() {
        return filteredRooms;
    }

    public void setFilteredRooms(ArrayList<Room> filteredRooms) {
        this.filteredRooms = filteredRooms;
    }

    public void fetchData() {
        findRooms();
    }

    private void findRooms() {
        rooms = RoomConnection.findAllRooms();
        filteredRooms = rooms;
    }

    public void search(String text) {
        ArrayList<Room> filtered = new ArrayList<>();
        rooms.stream()
                .filter(room -> (room.getName().toLowerCase().contains(text.toLowerCase()) ||
                        room.getLocation().getName().toLowerCase().contains(text.toLowerCase())))
                .forEach(filtered::add);
        filteredRooms = filtered;
    }
}
