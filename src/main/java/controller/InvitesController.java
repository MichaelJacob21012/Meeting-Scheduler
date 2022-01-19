package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import structure.*;
import connect.AccountConnection;
import connect.AttendeeConnection;
import connect.MeetingConnection;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import model.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class InvitesController extends SceneController implements Initializable {
    @FXML
    private TableView<Invite> receivedTable;
    @FXML
    private TableView<Invite> sentTable;
    @FXML
    private TableColumn<Invite,String> sender;
    @FXML
    private TableColumn<Invite,String> receivedTitle;
    @FXML
    private TableColumn<Invite,String> receivedStatus;
    @FXML
    private TableColumn<Invite,Void> receivedAction;
    @FXML
    private TableColumn<Invite,String> recipient;
    @FXML
    private TableColumn<Invite,String> sentTitle;
    @FXML
    private TableColumn<Invite,String> sentStatus;
    @FXML
    private TableColumn<Invite,String> attended;
    @FXML
    private TableColumn<Invite,Void> sentAction;
    public MenuBar normalMenuBar;
    public MenuBar adminMenuBar;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setPlaceholderTexts();
        setupRows(receivedTable);
        setupRows(sentTable);
        setupColumns();
    }

    private void setPlaceholderTexts() {
        receivedTable.setPlaceholder(new Label("No invites received"));
        sentTable.setPlaceholder(new Label("No invites sent"));
    }

    public void refresh(){
        displayMenuBar();
        receivedTable.getItems().clear();
        sentTable.getItems().clear();
        Account account = (Account)data.get("account");
        fillTables(account);
    }

    private void setupRows(TableView<Invite> tableView) {
        tableView.setRowFactory(t -> {
            TableRow<Invite> row = new TableRow<>();
            row.setOnMouseClicked(click -> {
                if (!row.isEmpty() && click.getButton() == MouseButton.PRIMARY && click.getClickCount() == 2) {
                    Invite invite = row.getItem();
                    viewInvite(invite);
                }
            });
            return row;
        });
    }

    private void setupColumns() {
        sender.setCellValueFactory(new PropertyValueFactory<>("sender"));
        recipient.setCellValueFactory(new PropertyValueFactory<>("recipient"));
        receivedStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        sentStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        receivedTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        sentTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        attended.setCellValueFactory(new PropertyValueFactory<>("attended"));
        setupReceivedActions();
        setupSentActions();
    }

    private void setupSentActions() {
        Callback<TableColumn<Invite, Void>, TableCell<Invite, Void>> cellFactory = new Callback<TableColumn<Invite, Void>, TableCell<Invite, Void>>() {
            @Override
            public TableCell<Invite, Void> call(final TableColumn<Invite, Void> param) {
                return new TableCell<Invite, Void>() {

                    private final Button markAttended = new Button("Attended");
                    {
                        markAttended.setOnAction((ActionEvent event) -> {
                            Invite invite = getTableView().getItems().get(getIndex());
                            AttendeeConnection.setAttended(invite.getAttendeeID(),true);
                            refresh();
                        });
                    }

                    private final Button didNotAttend = new Button("Did not attend");
                    {
                        didNotAttend.setOnAction((ActionEvent event) -> {
                            Invite invite = getTableView().getItems().get(getIndex());
                            AttendeeConnection.setAttended(invite.getAttendeeID(), false);
                            refresh();
                        });
                    }
                    private final HBox pane = new HBox(markAttended, didNotAttend);
                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(empty ? null : pane);
                    }
                };
            }
        };
        sentAction.setCellFactory(cellFactory);
    }

    private void setupReceivedActions() {
        Callback<TableColumn<Invite, Void>, TableCell<Invite, Void>> cellFactory = new Callback<TableColumn<Invite, Void>, TableCell<Invite, Void>>() {
            @Override
            public TableCell<Invite, Void> call(final TableColumn<Invite, Void> param) {
                return new TableCell<Invite, Void>() {

                    private final Button accept = new Button("Accept");
                    {
                        accept.setOnAction((ActionEvent event) -> {
                            Invite invite = getTableView().getItems().get(getIndex());
                            AttendeeConnection.setAccepted(invite.getAttendeeID());
                            refresh();
                        });
                    }

                    private final Button reject = new Button("Reject");
                    {
                        reject.setOnAction((ActionEvent event) -> {
                            Invite invite = getTableView().getItems().get(getIndex());
                            AttendeeConnection.setRejected(invite.getAttendeeID());
                            refresh();
                        });
                    }
                    private final HBox pane = new HBox(accept, reject);
                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(empty ? null : pane);
                    }
                };
            }
        };
        receivedAction.setCellFactory(cellFactory);

    }

    private void viewInvite(Invite invite) {
        Attendee attendee = AttendeeConnection.findAttendee(invite.getAttendeeID());
        int meetingID = attendee.getMeetingID();
        goToMeetingDetails(meetingID);
    }

    private void fillTables(Account account) {
        int accountID = account.getAccountID();
        ArrayList<Meeting> organisedMeetings = MeetingConnection.meetingsByAccountID(accountID);
        fillSentTable(organisedMeetings, account);
        ArrayList<AccountAttendee> attendees = AttendeeConnection.accountAttendeesByAccountID(accountID);
        fillReceivedTable(attendees, account);
    }

    private void fillReceivedTable(ArrayList<AccountAttendee> attendees, Account account) {
        for (Attendee attendee : attendees){
            Meeting meeting = MeetingConnection.findMeeting(attendee.getMeetingID());
            if (!meeting.isBooked()){
                continue;
            }
            Account organiser = AccountConnection.findAccount(meeting.getAccount().getAccountID());
            receivedTable.getItems().add(new Invite(organiser.getFullName(), account.getFullName(),
                meeting.getTitle(), attendee.status(), "", attendee.getAttendeeID()));
        }
    }

    private void fillSentTable(ArrayList<Meeting> organisedMeetings, Account account) {
        String accountName = account.getFullName();
        for (Meeting meeting : organisedMeetings){
            if (!meeting.isBooked()){
                continue;
            }
            ArrayList<AccountAttendee> accountAttendees = AttendeeConnection.accountAttendeesForMeeting(meeting.getMeetingID());
            for (AccountAttendee accountAttendee : accountAttendees){
                String attended = attendedStatus(meeting, accountAttendee);
                sentTable.getItems().add(new Invite(accountName, accountAttendee.getAccount().getFullName(),
                        meeting.getTitle(), accountAttendee.status(), attended , accountAttendee.getAttendeeID()));
            }
            ArrayList<NoAccountAttendee> noAccountAttendees = AttendeeConnection.noAccountAttendeesForMeeting(meeting.getMeetingID());
            for (NoAccountAttendee noAccountAttendee : noAccountAttendees){
                String attended = attendedStatus(meeting, noAccountAttendee);
                sentTable.getItems().add(new Invite(accountName, noAccountAttendee.getName(),
                        meeting.getTitle(), noAccountAttendee.status(), attended, noAccountAttendee.getAttendeeID()));
            }
        }
    }

    private String attendedStatus(Meeting meeting, Attendee accountAttendee) {
        if (!meeting.inPast()){
            return "Has not occurred yet";
        }
        if (accountAttendee.attended()){
            return  "attended";
        }
        return  "did not attend";
    }

}
