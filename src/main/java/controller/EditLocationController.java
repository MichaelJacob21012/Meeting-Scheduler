package controller;

import com.calendarfx.view.TimeField;
import connect.LocationConnection;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import structure.Location;

public class EditLocationController extends SceneController {
    @FXML
    private TextField nameField;
    @FXML
    private TextField addressField;
    @FXML
    private TextField postcodeField;
    @FXML
    private CheckBox openSpaceBox;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private TimeField openTime;
    @FXML
    private TimeField closeTime;
    @FXML
    private Label nameErrorLabel;
    @FXML
    private Label addressErrorLabel;
    @FXML
    private Label postcodeErrorLabel;
    @FXML
    private Label timeErrorLabel;
    private Location location;

    @Override
    public void refresh() {
        displayMenuBar();
        location = LocationConnection.findLocation((Integer)data.get("Edit locationID"));
        nameField.setText(location.getName());
        addressField.setText(location.getAddress());
        postcodeField.setText(location.getPostcode());
        openSpaceBox.setSelected(location.isOpenSpace());
        descriptionArea.setText(location.getDescription());
        openTime.setValue(location.getOpenTime());
        closeTime.setValue(location.getCloseTime());
    }

    public void submit(MouseEvent mouseEvent) {
        displayErrors();
        if (error()){
            return;
        }
        location.setName(nameField.getText());
        location.setAddress(addressField.getText());
        location.setPostcode(postcodeField.getText());
        location.setOpenSpace(openSpaceBox.isSelected());
        location.setDescription(descriptionArea.getText());
        location.setOpenTime(openTime.getValue());
        location.setCloseTime(closeTime.getValue());
        LocationConnection.editLocation(location);
        goToLocations();
    }

    private void displayErrors() {
        resetErrorLabels();
        if (nameField.getText().isEmpty()){
            nameErrorLabel.setText("Name required");
        }
        if (addressField.getText().isEmpty()){
            addressErrorLabel.setText("Address required");
        }
        if (postcodeField.getText().isEmpty()){
            postcodeErrorLabel.setText("Postcode required");
        }
        if (!openTime.getValue().isBefore(closeTime.getValue())){
            timeErrorLabel.setText("Opening time should be before closing time");
        }
    }

    private boolean error() {
        if (!nameErrorLabel.getText().equals("")) {
            return true;
        }
        if (!addressErrorLabel.getText().equals("")) {
            return true;
        }
        if (!postcodeErrorLabel.getText().equals("")) {
            return true;
        }
        return !timeErrorLabel.getText().equals("");
    }

    private void resetErrorLabels(){
        nameErrorLabel.setText("");
        addressErrorLabel.setText("");
        postcodeErrorLabel.setText("");
        timeErrorLabel.setText("");
    }
}
