package controller;

import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.view.CalendarView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuBar;
import model.CalendarModel;
import structure.Account;

import java.net.URL;
import java.util.ResourceBundle;

public class CalendarController extends SceneController implements Initializable {

    @FXML
    private CalendarView calendarView;
    public MenuBar normalMenuBar;
    public MenuBar adminMenuBar;
    private final CalendarModel model = new CalendarModel();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        calendarView.setEntryDetailsCallback(param -> {
            int meetingID = Integer.parseInt(param.getEntry().getId());
            goToMeetingDetails(meetingID);
            return true;
        });
        calendarView.setEntryFactory(param -> {
            goToAddMeeting();
            return new Entry<String>("");
        });
    }
    public void refresh(){
        CalendarSource calendarSource;
        if ((Boolean)data.get("My Calendar")){
            Account account = (Account)data.get("account");
            calendarSource = model.importMeetings(account);
        }
        else {
            int roomID = (int) data.get("Calendar roomID");
            calendarSource = model.importRoomMeetings(roomID);
        }
        calendarView.getCalendarSources().setAll(calendarSource);
        displayMenuBar();
    }



    public void addNewMeeting(ActionEvent actionEvent) {
        goToAddMeeting();
    }

}
