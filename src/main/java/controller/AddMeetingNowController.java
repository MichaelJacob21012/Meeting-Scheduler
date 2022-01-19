package controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import structure.ProvisionalMeeting;
import com.calendarfx.view.TimeField;
import javafx.scene.input.MouseEvent;
import model.*;
import org.controlsfx.control.textfield.TextFields;
import utility.LocationUtility;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Optional;


/**
 * Controller for the finalisation screen for meetings intended to be scheduled immediately
 */
public class AddMeetingNowController extends SceneController {
    @FXML
    private TextField locationField;
    @FXML
    private TextField roomField;
    @FXML
    private TimeField timeField;
    @FXML
    private CheckBox locationBox;
    @FXML
    private CheckBox roomBox;
    @FXML
    private CheckBox dateBox;
    @FXML
    private CheckBox timeBox;
    @FXML
    private DatePicker datePicker;
    @FXML
    private MenuBar normalMenuBar;
    @FXML
    private MenuBar adminMenuBar;
    @FXML
    private Label locationLabel;
    @FXML
    private Label roomLabel;
    @FXML
    private Label dateLabel;
    @FXML
    private Label timeLabel;
    private final AddMeetingNowModel model = new AddMeetingNowModel();
    private ProvisionalMeeting provisionalMeeting;
    private final LocationUtility locationUtility = new LocationUtility();

    @Override
    public void refresh() {
        adminMenuBar.setVisible(false);
        normalMenuBar.setVisible(false);
        model.fetchData();
        provisionalMeeting = (ProvisionalMeeting) data.get("Provisional Meeting");
        TextFields.bindAutoCompletion(locationField, locationUtility.getLocationDetails(model.getLocations()));
        TextFields.bindAutoCompletion(roomField, locationUtility.getRoomNames(model.getRooms()));
    }

    public void finish(MouseEvent mouseEvent) {
        displayErrors();
        if (error()){
            return;
        }
        ArrayList<ProvisionalMeeting> provisionalMeetings;

        if (automate()){
            provisionalMeetings = model.autoSchedule(provisionalMeeting, locationField.getText(), locationBox.isSelected(),
                    roomField.getText(), roomBox.isSelected(),
                    datePicker.getValue(), dateBox.isSelected(), timeField.getValue(), timeBox.isSelected());
        }
        else {
            provisionalMeetings = model.immediateSchedule(provisionalMeeting, locationField.getText(), roomField.getText(),
                    datePicker.getValue(), timeField.getValue());
        }

        if (provisionalMeetings.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Failed to create meeting.\nNo available slots were found that all attendees can attend");
            alert.show();
            return;
        }

        ProvisionalMeeting meeting = meetingChoice(provisionalMeetings);

        //user selected cancel
        if (meeting == null){
            return;
        }

        int meetingID = model.finaliseMeeting(meeting);
        if (meetingID == - 1){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Failed to create meeting");
            return;
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText("Your meeting has been scheduled");
        goToMeetingDetails(meetingID);
    }

    private boolean automate() {
        return locationBox.isSelected() || roomBox.isSelected() || dateBox.isSelected() || timeBox.isSelected();
    }

    public void back(ActionEvent actionEvent) {
        goToAddMeetingAttendees();
    }

    private void displayErrors() {
        resetErrorLabels();
        if (locationBox.isSelected() && !roomBox.isSelected()){
            locationLabel.setText("Location cannot be automated if room is not");
        }
        else if (!locationBox.isSelected() && !locationUtility.isLocation(locationField.getText())){
            locationLabel.setText("Location not found");
        }
        else if (!locationBox.isSelected() && !timeBox.isSelected() &&
                !locationUtility.locationOpen(locationField.getText(), timeField.getValue(), provisionalMeeting)){
            timeLabel.setText("Location closed at that time");
        }
        if (!roomBox.isSelected() && !locationUtility.isValidRoom(roomField.getText(), locationField.getText())){
            roomLabel.setText("Room not found in location");
        }
        else if (!roomBox.isSelected() && !locationUtility.fitInRoom(roomField.getText(), locationField.getText(), provisionalMeeting.getAttendees().size())){
            roomLabel.setText("Room is too small");
        }
        if (!dateBox.isSelected() && datePicker.getValue().isBefore(LocalDate.now())){
            dateLabel.setText("Date cannot be in the past");
        }
        else if (!dateBox.isSelected() && datePicker.getValue().equals(LocalDate.now()) &&
                !timeBox.isSelected() && timeField.getValue().isBefore(LocalTime.now())){
            dateLabel.setText("Date cannot be in the past");
        }
        if (!timeBox.isSelected() &&
                timeField.getValue().plusHours(provisionalMeeting.getHours()).plusMinutes(provisionalMeeting.getMinutes())
                        .isBefore(timeField.getValue())){
            timeLabel.setText("Invalid time");
        }
    }

    private boolean error() {
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

    private void resetErrorLabels() {
        locationLabel.setText("");
        roomLabel.setText("");
        timeLabel.setText("");
        dateLabel.setText("");
    }

    /**
     * Create a dialogue to display the choices for scheduling the meeting.
     * Return the chosen option, null if cancel selected
     */
    private ProvisionalMeeting meetingChoice(ArrayList<ProvisionalMeeting> provisionalMeetings) {
        TableView<ProvisionalMeeting> tableView = new TableView<>();
        tableView.setPrefWidth(550);
        TableColumn<ProvisionalMeeting, String> locationColumn = new TableColumn<>();
        TableColumn<ProvisionalMeeting, String> roomColumn = new TableColumn<>();
        TableColumn<ProvisionalMeeting, LocalDate> dateColumn = new TableColumn<>();
        TableColumn<ProvisionalMeeting, LocalTime> timeColumn = new TableColumn<>();

        locationColumn.setCellValueFactory(param ->
                new SimpleStringProperty(param.getValue().getLocation().details()));
        locationColumn.setText("Location");
        roomColumn.setCellValueFactory(param ->
                new SimpleStringProperty(param.getValue().getRoom().getName()));
        roomColumn.setText("Room");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateColumn.setText("Date");
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        timeColumn.setText("Time");

        tableView.getColumns().add(locationColumn);
        tableView.getColumns().add(roomColumn);
        tableView.getColumns().add(dateColumn);
        tableView.getColumns().add(timeColumn);
        tableView.getItems().addAll(provisionalMeetings);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText(null);
        alert.setContentText("Select an acceptable meeting slot and choose OK");
        alert.setGraphic(tableView);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            return tableView.getSelectionModel().getSelectedItem();
        }
        return null;
    }
}