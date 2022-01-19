package application;

import connect.AccountConnection;
import controller.PrimaryController;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    private static final String addAccount = "addAccount";
    private static final String editAccount = "editAccount";
    private static final String accounts = "accounts";
    private static final String changePassword = "changePassword";
    private static final String addMeeting = "addMeeting";
    private static final String addMeetingAttendees = "addMeetingAttendees";
    private static final String addNoAccountAttendee = "addNoAccountAttendee";
    private static final String addMeetingNow = "addMeetingNow";
    private static final String addMeetingLater = "addMeetingLater";
    private static final String meetingDetails = "meetingDetails";
    private static final String invites = "invites";
    private static final String addLocation = "addLocation";
    private static final String locations = "locations";
    private static final String editLocation = "editLocation";
    private static final String addRoom = "addRoom";
    private static final String rooms = "rooms";
    private static final String editRoom = "editRoom";
    private static final String login = "login";
    private static final String calendar = "calendar";
    private static final String batch = "batch";

    @Override
    public void start(Stage primaryStage) {
        PrimaryController primaryController = new PrimaryController(primaryStage);
        primaryController.addViews(
                new String[] { addAccount, editAccount, accounts, changePassword, addMeeting, addMeetingAttendees, addNoAccountAttendee,
                        addMeetingNow, addMeetingLater, meetingDetails, invites, addLocation, locations, editLocation,
                        addRoom, rooms, editRoom, login, calendar, batch });

        if (AccountConnection.findAllAccounts().isEmpty()){
            primaryController.setPane(Main.addAccount);
        }
        else {
            primaryController.setPane(Main.login);
        }
        Group root = new Group();
        Scene scene = new Scene(root);
        root.getChildren().addAll(primaryController);
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Meeting Scheduler");
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
