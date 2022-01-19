package controller;

import com.calendarfx.view.TimeField;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.AddLocationModel;

/**
 * Controller for the add location form
 */
public class AddLocationController extends SceneController {
    @FXML
    private TimeField openTime;
    @FXML
    private TimeField closeTime;
    @FXML
    private Label nameLabel;
    @FXML
    private Label timeLabel;
    @FXML
    private Label addressLabel;
    @FXML
    private Label postcodeLabel;
    @FXML
    private TextField nameField;
    @FXML
    private TextField addressField;
    @FXML
    private TextField postcodeField;
    @FXML
    private TextArea descriptionArea;

    private final AddLocationModel model = new AddLocationModel();

    public Button submitButton;

    @Override
    public void refresh() {
        displayMenuBar();
    }

    @FXML
    public void submit() {
        displayErrors();
        if (error()){
            return;
        }
        model.addLocation(nameField.getText(), addressField.getText(), postcodeField.getText(),
                openTime.getValue(), closeTime.getValue(), false, descriptionArea.getText());
        goToLocations();
    }

    private void displayErrors() {
        resetErrorLabels();
        if (nameField.getText().isEmpty()){
            nameLabel.setText("Name required");
        }
        if (addressField.getText().isEmpty()){
            addressLabel.setText("Address required");
        }
        if (postcodeField.getText().isEmpty()){
            postcodeLabel.setText("Postcode required");
        }
        if (!openTime.getValue().isBefore(closeTime.getValue())){
            timeLabel.setText("Opening time should be before closing time");
        }
    }

    private boolean error() {
        if (!nameLabel.getText().equals("")) {
            return true;
        }
        if (!addressLabel.getText().equals("")) {
            return true;
        }
        if (!postcodeLabel.getText().equals("")) {
            return true;
        }
        return !timeLabel.getText().equals("");
    }

    private void resetErrorLabels(){
        nameLabel.setText("");
        addressLabel.setText("");
        postcodeLabel.setText("");
        timeLabel.setText("");
    }

}
