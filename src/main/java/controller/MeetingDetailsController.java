package controller;

import connect.MeetingConnection;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import structure.Account;
import structure.Meeting;
import model.MeetingDetailsModel;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class MeetingDetailsController extends SceneController implements Initializable {
    @FXML
    private TextArea textArea;
    @FXML
    private Button deleteButton;
    private final MeetingDetailsModel model = new  MeetingDetailsModel();
    private Meeting meeting;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.textArea.setEditable(false);
    }

    public void refresh(){
        displayMenuBar();
        textArea.clear();
        int meetingID =  (Integer)data.get("meeting");
        meeting = MeetingConnection.findMeeting(meetingID);
        displayMeeting(meeting);
        if (isAdminAccount() || isOrganiser()){
            deleteButton.setVisible(true);
        }
        else {
            deleteButton.setVisible(false);
        }
    }

    private boolean isOrganiser() {
        Account account = (Account) data.get("account");
        return account.equals(meeting.getAccount());
    }

    private void displayMeeting(Meeting meeting) {
        String message = model.createMessage(meeting);
        textArea.appendText(message);
    }

    public void delete(MouseEvent mouseEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("Are you sure?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            MeetingConnection.deleteMeeting(meeting.getMeetingID());
            goToMyCalendar();
        }
    }
}
