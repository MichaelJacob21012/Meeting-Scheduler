package controller;

import javafx.scene.control.MenuBar;
import structure.Account;
import structure.Room;

import java.util.HashMap;
import java.util.Map;

public abstract class SceneController implements ControllerInterface {
    protected Map<String, Object> data = new HashMap<>();
    private PrimaryController primaryController;
    public MenuBar adminMenuBar;
    public MenuBar normalMenuBar;

    void displayMenuBar() {
        if (isAdminAccount()){
            adminMenuBar.setVisible(true);
            normalMenuBar.setVisible(false);
        }
        else {
            adminMenuBar.setVisible(false);
            normalMenuBar.setVisible(true);
        }
    }

    void goToMeetingDetails(int meetingID){
        data.put("meeting",meetingID);
        goTo("meetingDetails");
    }

    public void goToAddMeeting() {
        goTo("addMeeting");
    }

    void goToAddMeetingAttendees() { goTo("addMeetingAttendees");
    }

    void goToAddMeetingNow() { goTo("addMeetingNow");
    }
    void goToAddMeetingLater() { goTo("addMeetingLater");
    }

    public void goToAddAccount() {
        goTo("addAccount");
    }

    public void goToAddLocation() {
        goTo("addLocation");
    }

    public void goToAddRoom() {
        goTo("addRoom");
    }

    public void goToMyCalendar() {
        data.put("My Calendar", true);
        goTo("calendar");
    }

    public void goToInvites() {
        goTo("invites");
    }

    void goToChangePassword(int accountID){
        data.put("Edit AccountID", accountID);
        goTo("changePassword");
    }
    public void goToChangePassword(){
        goTo("changePassword");
    }
    void goToEditAccount(int accountID) {
        data.put("Edit AccountID", accountID);
        goTo("editAccount");
    }
    public void goToAccounts() {
        goTo("accounts");
    }
    void goToAddNoAccountAttendee() {
        goTo("addNoAccountAttendee");
    }
    public void goToBatch() {
        goTo("batch");
    }

    public void goToMyAccount() {
        Account account = (Account)data.get("account");
        int accountID = account.getAccountID();
        goToEditAccount(accountID);
    }
    void goToRoomCalendar() {
        data.put("My Calendar", false);
        goTo("calendar");
    }
    public void goToRooms() {
        goTo("rooms");
    }
    public void goToLocations() {
        goTo("locations");
    }
    void goToEditLocation() { goTo("editLocation");
    }
    void goToEditRoom(Room room) {
        data.put("Edit room", room);
        goTo("editRoom");
    }

    public void logout() {
        data.clear();
        goTo("login");
    }


    private void goTo(String destination){
        sendAllData(destination);
        primaryController.setPane(destination);
    }

    boolean isAdminAccount() {
        Account account = (Account)data.get("account");
        return account.isAdmin();
    }

    private void sendAllData(String destination) {
        for (Map.Entry<String,Object> entry : data.entrySet()){
            sendTo(destination, entry.getKey(),entry.getValue());
        }
    }

    private void sendTo(String toReceive, String toSetKey, Object toSetVal){
        primaryController.sendTo(toReceive, toSetKey, toSetVal);
    }

    public void setScreenParent(PrimaryController primaryController){
        this.primaryController = primaryController;
    }

    public void setData(Map<String, Object> toInject){
        this.data = toInject;
    }

    public void addData(String toAddKey, Object toAddVal){
        data.put(toAddKey, toAddVal);
    }

    public abstract void refresh();
}
