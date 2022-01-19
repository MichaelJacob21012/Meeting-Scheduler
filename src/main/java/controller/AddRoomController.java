package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.AddEditRoomModel;
import org.controlsfx.control.textfield.TextFields;
import utility.LocationUtility;

import java.util.ArrayList;

import static org.apache.commons.lang3.math.NumberUtils.isParsable;

/**
 * Controller for the add room form
 */
public class AddRoomController extends SceneController {
    @FXML
    private Label nameLabel;
    @FXML
    private Label locationLabel;
    @FXML
    private Label capacityLabel;
    @FXML
    private Label costLabel;
    @FXML
    private Label cleanupLabel;
    @FXML
    private TextField nameField;
    @FXML
    private TextField locationField;
    @FXML
    private TextField descriptionField;
    @FXML
    private TextField capacityField;
    @FXML
    private TextField cleanupField;
    @FXML
    private TextField costField;

    private final AddEditRoomModel model = new AddEditRoomModel();

    public Button submitButton;

    private final LocationUtility locationUtility = new LocationUtility();


    @Override
    public void refresh() {
        displayMenuBar();
        model.findLocations();
        ArrayList<String> locations = locationUtility.getLocationDetails(model.getLocations());
        TextFields.bindAutoCompletion(locationField, locations);
    }

    @FXML
    public void submit() {
        displayErrors();
        if (error()) {
            return;
        }
        model.addRoom(nameField.getText(), locationField.getText(), descriptionField.getText(),
                capacityField.getText(), costField.getText(), cleanupField.getText());
        goToRooms();

    }

    private void displayErrors() {
        resetErrorLabels();
        if (nameField.getText().isEmpty()){
            nameLabel.setText("Name is required");
        }
        if (locationField.getText().isEmpty()){
            locationLabel.setText("Location is required");
        }
        else if (!locationUtility.isLocation(locationField.getText())){
            locationLabel.setText("Location not found");
        }
        if (capacityField.getText().isEmpty()){
            capacityLabel.setText("Capacity is required");
        }
        else if (!isParsable(capacityField.getText())){
            capacityLabel.setText("Capacity should be a number");
        }
        if (!isParsable(costField.getText())){
            costLabel.setText("Cost per hour should be number");
        }
        if (!isParsable(cleanupField.getText())){
            cleanupLabel.setText("Minutes required for cleanup should be a number");
        }
        else if (Integer.parseInt(cleanupField.getText()) > 60){
            cleanupLabel.setText("Minutes required for cleanup should not be greater than 60");
        }
    }

    private boolean error() {
        if (!nameLabel.getText().equals("")) {
            return true;
        }
        if (!locationLabel.getText().equals("")) {
            return true;
        }
        if (!capacityLabel.getText().equals("")) {
            return true;
        }
        if (!costLabel.getText().equals("")) {
            return true;
        }
        return !cleanupLabel.getText().equals("");
    }

    private void resetErrorLabels(){
        nameLabel.setText("");
        locationLabel.setText("");
        capacityLabel.setText("");
        costLabel.setText("");
        cleanupLabel.setText("");
    }

}
