package controller;

import connect.LocationConnection;
import connect.RoomConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import model.AddEditRoomModel;
import org.controlsfx.control.textfield.TextFields;
import structure.Location;
import structure.Room;
import utility.LocationUtility;

import java.time.LocalTime;
import java.util.ArrayList;

import static org.apache.commons.lang3.math.NumberUtils.isParsable;

public class EditRoomController extends SceneController {
    @FXML
    private TextField nameField;
    @FXML
    private TextField locationField;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private TextField capacityField;
    @FXML
    private TextField cleanupField;
    @FXML
    private TextField costField;
    @FXML
    private Label nameErrorLabel;
    @FXML
    private Label locationErrorLabel;
    @FXML
    private Label capacityErrorLabel;
    @FXML
    private Label costErrorLabel;
    @FXML
    private Label cleanupErrorLabel;
    @FXML
    private Button submitButton;
    private Room room;
    private final AddEditRoomModel model = new AddEditRoomModel();
    private final LocationUtility locationUtility = new LocationUtility();

    @Override
    public void refresh() {
        displayMenuBar();
        setEditable(isAdminAccount());
        room = (Room)data.get("Edit room");
        model.findLocations();
        ArrayList<String> locations = locationUtility.getLocationDetails(model.getLocations());
        TextFields.bindAutoCompletion(locationField, locations);
        nameField.setText(room.getName());
        locationField.setText(room.getLocation().getName() + "    " + room.getLocation().getPostcode());
        descriptionArea.setText(room.getDescription());
        capacityField.setText("" + room.getCapacity());
        cleanupField.setText("" + room.getCleanupTime().getMinute());
        costField.setText("" + room.getCost());
    }

    private void setEditable(boolean adminAccount) {
        nameField.setEditable(adminAccount);
        locationField.setEditable(adminAccount);
        descriptionArea.setEditable(adminAccount);
        capacityField.setEditable(adminAccount);
        cleanupField.setEditable(adminAccount);
        costField.setEditable(adminAccount);
        submitButton.setVisible(adminAccount);

    }

    public void submit(MouseEvent mouseEvent) {
        displayErrors();
        if (error()) {
            return;
        }
        room.setName(nameField.getText());
        Location location = LocationConnection.locationByNameAndPostcode(
                locationUtility.recoverLocationName(locationField.getText()),
               locationUtility.recoverPostcode(locationField.getText()));
        room.setLocation(location);
        room.setDescription(descriptionArea.getText());
        room.setCapacity(Integer.parseInt(capacityField.getText()));
        room.setCost(Double.parseDouble(costField.getText()));
        room.setCleanupTime(LocalTime.of(0, Integer.parseInt(cleanupField.getText())));
        RoomConnection.editRoom(room);
        goToRooms();
    }

    private void displayErrors() {
        resetErrorLabels();
        if (nameField.getText().isEmpty()){
            nameErrorLabel.setText("Name is required");
        }
        if (locationField.getText().isEmpty()){
            locationErrorLabel.setText("Location is required");
        }
        else if (!locationUtility.isLocation(locationField.getText())){
            locationErrorLabel.setText("Location not found");
        }
        if (capacityField.getText().isEmpty()){
            capacityErrorLabel.setText("Capacity is required");
        }
        else if (!isParsable(capacityField.getText())){
            capacityErrorLabel.setText("Capacity should be a number");
        }
        if (!isParsable(costField.getText())){
            costErrorLabel.setText("Cost per hour should be number");
        }
        if (!isParsable(cleanupField.getText())){
            cleanupErrorLabel.setText("Minutes required for cleanup should be a number");
        }
        else if (Integer.parseInt(cleanupField.getText()) > 60){
            cleanupErrorLabel.setText("Minutes required for cleanup should not be greater than 60");
        }
    }

    private boolean error() {
        if (!nameErrorLabel.getText().equals("")) {
            return true;
        }
        if (!locationErrorLabel.getText().equals("")) {
            return true;
        }
        if (!capacityErrorLabel.getText().equals("")) {
            return true;
        }
        if (!costErrorLabel.getText().equals("")) {
            return true;
        }
        return !cleanupErrorLabel.getText().equals("");
    }

    private void resetErrorLabels(){
        nameErrorLabel.setText("");
        locationErrorLabel.setText("");
        capacityErrorLabel.setText("");
        costErrorLabel.setText("");
        cleanupErrorLabel.setText("");
    }
}
