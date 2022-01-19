package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import structure.NoAccountAttendee;
import structure.ProvisionalMeeting;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

/**
 * Controller for adding no account attendees to a provisional meeting
 */
public class AddNoAccountAttendeeController extends SceneController {
    @FXML
    private TextField emailField;
    @FXML
    private TextField nameField;
    @FXML
    private MenuBar normalMenuBar;
    @FXML
    private MenuBar adminMenuBar;
    @FXML
    private Label nameLabel;
    @FXML
    private Label emailLabel;

    @Override
    public void refresh() {
        adminMenuBar.setVisible(false);
        normalMenuBar.setVisible(false);
    }

    public void submit(MouseEvent mouseEvent) {
        ProvisionalMeeting meeting = (ProvisionalMeeting) data.get("Provisional Meeting");
        displayErrors();
        if (error()){
            return;
        }
        meeting.addAttendee(new NoAccountAttendee(nameField.getText(), emailField.getText()));
        goToAddMeetingAttendees();
    }

    private void displayErrors() {
        resetErrorLabels();
        if (nameField.getText().isEmpty()){
            nameLabel.setText("Name required");
        }
        if (emailField.getText().isEmpty()){
            emailLabel.setText("Email required");
        }
        else if (!isEmail(emailField.getText())){
            emailLabel.setText("Email is not a valid format");
        }
    }

    private boolean isEmail(String email) {
        try {
            InternetAddress emailAddress = new InternetAddress(email);
            emailAddress.validate();
            return true;
        } catch (AddressException e) {
            return false;
        }
    }
    private boolean error(){
        if (!nameLabel.getText().equals("")){
            return true;
        }
        return !emailLabel.getText().equals("");
    }

    private void resetErrorLabels(){
        emailLabel.setText("");
    }
}
