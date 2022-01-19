package controller;

import javafx.scene.input.MouseEvent;
import structure.Account;
import structure.ProvisionalMeeting;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import static org.apache.commons.lang3.math.NumberUtils.isParsable;

/**
 * Controller class for the first screen involved in meeting creation
 */
public class AddMeetingController extends SceneController {

    @FXML
    private TextField hoursField;
    public Button nextButton;
    @FXML
    private TextField titleField;
    @FXML
    private TextField minutesField;
    @FXML
    private CheckBox scheduleNow;
    public MenuBar adminMenuBar;
    public MenuBar normalMenuBar;
    @FXML
    private Label titleLabel;
    @FXML
    private Label hoursLabel;
    @FXML
    private Label minutesLabel;

    @FXML
    private TextArea descriptionArea;

    @Override
    public void refresh() {
        displayMenuBar();
    }

    public void next(MouseEvent mouseEvent) {
        displayErrors();
        if (error()) {
            return;
        }
        int hours = Integer.parseInt(hoursField.getText());
        int minutes = Integer.parseInt(minutesField.getText());
        Account account = (Account) data.get("account");
        ProvisionalMeeting meeting = new ProvisionalMeeting(account,titleField.getText(),hours,minutes,descriptionArea.getText(),
                scheduleNow.isSelected());
        data.put("Provisional Meeting", meeting);
        goToAddMeetingAttendees();
    }

    private void displayErrors() {
        resetErrorLabels();
        if (titleField.getText().isEmpty()){
            titleLabel.setText("Title required");
        }
        if (hoursField.getText().isEmpty()){
            hoursLabel.setText("Hours required");
        }
        else if(!isParsable(hoursField.getText())){
            hoursLabel.setText("Hours should be a number");
        }
        else if((0 > Integer.parseInt(hoursField.getText())) || (12 < Integer.parseInt(hoursField.getText()))){
            hoursLabel.setText("Hours should be between 0 and 12");
        }
        if (minutesField.getText().isEmpty()){
            minutesLabel.setText("Minutes required");
        }
        else if(!isParsable(minutesField.getText())){
            minutesLabel.setText("Minutes should be a number");
        }
        else if((0 > Integer.parseInt(minutesField.getText())) || (59 < Integer.parseInt(minutesField.getText()))){
            minutesLabel.setText("Minutes should be between 0 and 59");
        }
    }


    private boolean error() {
        if (!titleLabel.getText().equals("")) {
            return true;
        }
        if (!hoursLabel.getText().equals("")) {
            return true;
        }
        return !minutesLabel.getText().equals("");
    }


    private void resetErrorLabels(){
        titleLabel.setText("");
        hoursLabel.setText("");
        minutesLabel.setText("");
    }
}
