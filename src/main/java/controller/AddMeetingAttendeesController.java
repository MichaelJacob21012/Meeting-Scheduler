package controller;

import javafx.fxml.FXML;
import structure.ProvisionalMeeting;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import model.*;
import org.controlsfx.control.HyperlinkLabel;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Controller for adding attendees to a meeting
 */
public class AddMeetingAttendeesController extends SceneController implements Initializable {
    @FXML
    private TextField searchField;
    public Button nextButton;
    @FXML
    private Button searchButton;
    @FXML
    private ListView<String> listView;
    public HyperlinkLabel nonAccountLabel;
    @FXML
    private MenuBar adminMenuBar;
    @FXML
    private MenuBar normalMenuBar;
    @FXML
    private ListView<String> selectedListView;

    private final AddMeetingAttendeesModel model = new AddMeetingAttendeesModel();
    private ProvisionalMeeting provisionalMeeting;


    @Override
    public void refresh() {
        adminMenuBar.setVisible(false);
        normalMenuBar.setVisible(false);
        model.resetData();
        provisionalMeeting = (ProvisionalMeeting) data.get("Provisional Meeting");
        model.setAttendees(provisionalMeeting.getAttendees());
        selectedListView.setItems(model.attendeeDetails());
        searchButton.fire();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setUpDynamicSearchField();
    }

    private void setUpDynamicSearchField(){
        searchField.textProperty().addListener((observable, oldValue, newValue) -> searchButton.fire());
    }

    public void next(MouseEvent mouseEvent) {
        provisionalMeeting.addAttendees(new ArrayList<>(model.getAttendees()));
        if (provisionalMeeting.isScheduleNow()){
            goToAddMeetingNow();
        }
        else{
            goToAddMeetingLater();
        }
    }

    public void search(ActionEvent actionEvent) {
        ArrayList<String> accounts = model.search(searchField.getText());
        ObservableList<String> observableList = FXCollections.observableArrayList(accounts);
        listView.setItems(observableList);
    }

    public void addNoAccountAttendee(MouseEvent mouseEvent) {
        goToAddNoAccountAttendee();
    }

    public void add(ActionEvent actionEvent) {
        String accountDetails = listView.getSelectionModel().getSelectedItem();
        String email = accountDetails.substring(accountDetails.lastIndexOf(" ")+1);
        model.add(email);
        selectedListView.setItems(model.attendeeDetails());
    }

    public void remove(ActionEvent actionEvent) {
        String accountDetails = selectedListView.getSelectionModel().getSelectedItem();
        String email = accountDetails.substring(accountDetails.lastIndexOf(" ")+1);
        model.remove(email);
        selectedListView.setItems(model.attendeeDetails());
    }

    public void back(ActionEvent actionEvent) {
        goToAddMeeting();
    }
}
