package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import structure.*;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import model.*;
import org.controlsfx.control.textfield.TextFields;
import utility.LocationUtility;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

/**
 * Controller for the finalisation screen for meetings intended for the batch scheduler
 */
public class AddMeetingLaterController extends SceneController implements Initializable {
    @FXML
    private TextField locationField;
    @FXML
    private TextField roomField;
    @FXML
    private ComboBox<String> locationCombo;
    @FXML
    private ComboBox<String> roomCombo;
    @FXML
    private ComboBox<String> meetingCombo;
    @FXML
    private ComboBox<String> timePriorityCombo;
    @FXML
    private ComboBox<String> timeCombo;
    @FXML
    private DatePicker datePicker;
    @FXML
    private MenuBar adminMenuBar;
    @FXML
    private MenuBar normalMenuBar;
    @FXML
    private Label timeLabel;
    @FXML
    private Label roomLabel;
    @FXML
    private Label locationLabel;
    @FXML
    private Label meetingLabel;
    @FXML
    private Label dateLabel;

    private final AddMeetingLaterModel model = new AddMeetingLaterModel();
    private final LocationUtility locationUtility = new LocationUtility();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupCombos();
    }

    @Override
    public void refresh() {
        adminMenuBar.setVisible(false);
        normalMenuBar.setVisible(false);
        model.fetchData();
        TextFields.bindAutoCompletion(locationField, locationUtility.getLocationDetails(model.getLocations()));
        TextFields.bindAutoCompletion(roomField, locationUtility.getRoomNames(model.getRooms()));
    }

    private void setupCombos() {
        meetingCombo.getItems().addAll("1 - least important", "2 - average importance", "3 - most important");
        timeCombo.getItems().addAll("AM", "PM");
        setupPriorityCombo(locationCombo);
        setupPriorityCombo(roomCombo);
        setupPriorityCombo(timePriorityCombo);
    }

    private void setupPriorityCombo(ComboBox<String> comboBox) {
        comboBox.getItems().addAll("0 - ignore", "1 - unimportant", "2 - could change","3 - preferable",
                "4 - important","5 - unbreakable");

    }

    public void finish(MouseEvent mouseEvent) {
        displayErrors();
        if (error()){
            return;
        }

        ProvisionalMeeting provisionalMeeting = (ProvisionalMeeting) data.get("Provisional Meeting");
        String locationDetails = locationField.getText();
        String roomName = roomField.getText();
        int locationPriority = Character.getNumericValue(locationCombo.getValue().charAt(0));
        int roomPriority = Character.getNumericValue(roomCombo.getValue().charAt(0));
        boolean AMPreferred = timeCombo.getValue().equals("AM");
        int timePriority = Character.getNumericValue(timePriorityCombo.getValue().charAt(0));
        LocalDate latestDate = datePicker.getValue();
        int meetingPriority = Character.getNumericValue(meetingCombo.getValue().charAt(0));


        Location location = locationUtility.recoverLocation(locationDetails);
        provisionalMeeting.setLocation(location);

        Room room = locationUtility.recoverRoom(roomName, location);
        provisionalMeeting.setRoom(room);

        MeetingPreferences preferences = new MeetingPreferences(-1,meetingPriority,location,locationPriority,
                room,roomPriority,latestDate,AMPreferred,timePriority);
        provisionalMeeting.setMeetingPreferences(preferences);

        int meetingID = model.finaliseMeeting(provisionalMeeting, preferences);
        if (meetingID == -1){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Failed to create meeting");
            alert.show();
            return;
        }

        goToMeetingDetails(meetingID);
    }


    public void back(ActionEvent actionEvent) {
        goToAddMeetingAttendees();
    }

    private boolean error() {
        if (!meetingLabel.getText().equals("")) {
            return true;
        }
        if (!locationLabel.getText().equals("")) {
            return true;
        }
        if (!roomLabel.getText().equals("")) {
            return true;
        }
        if (!timeLabel.getText().equals("")) {
            return true;
        }
        return !dateLabel.getText().equals("");
    }

    private void displayErrors() {
        resetErrorLabels();
        if (meetingCombo.getValue().isEmpty()){
            meetingLabel.setText("Meeting priority is required");
        }
        if (locationField.getText().isEmpty()){
            locationLabel.setText("Location is required");
        }
        else if (!locationUtility.isLocation(locationField.getText())){
            locationLabel.setText("Location not found");
            return;
        }
        else if (locationCombo.getValue().isEmpty()){
            locationLabel.setText("Location priority is required");
        }
        if (roomField.getText().isEmpty()){
            roomLabel.setText("Room is required");
        }
        else if (!locationUtility.isValidRoom(roomField.getText(), locationField.getText())){
            roomLabel.setText("Room not found in location");
            return;
        }
        else if (roomCombo.getValue().isEmpty()){
            roomLabel.setText("Room priority is required");
        }
        else if (!locationCombo.getValue().isEmpty() &&
                Character.getNumericValue(roomCombo.getValue().charAt(0)) >
                Character.getNumericValue(locationCombo.getValue().charAt(0))){
            roomLabel.setText("Room priority exceeds location priority");
        }
        if (timeCombo.getValue().isEmpty()){
            timeLabel.setText("Time preference required");
        }
        if (datePicker.getValue().isBefore(LocalDate.now().plusDays(1))){
           dateLabel.setText("Latest date cannot be before tomorrow");
        }

    }

    private void resetErrorLabels() {
        meetingLabel.setText("");
        locationLabel.setText("");
        roomLabel.setText("");
        timeLabel.setText("");
        dateLabel.setText("");
    }
}
