package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import structure.Account;
import structure.AccountAttendee;
import connect.AccountConnection;
import structure.Attendee;
import structure.NoAccountAttendee;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Model class for adding attendees for a meeting
 */
public class AddMeetingAttendeesModel {
    private ArrayList<Account> accounts;
    private HashSet<Attendee> attendees;

    public AddMeetingAttendeesModel() {
        accounts = new ArrayList<>();
        attendees = new HashSet<>();
    }

    public ArrayList<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(ArrayList<Account> accounts) {
        this.accounts = accounts;
    }

    public HashSet<Attendee> getAttendees() {
        return attendees;
    }

    public void setAttendees(HashSet<Attendee> attendees) {
        this.attendees = attendees;
    }

    /**
     *  Populate accounts field with all the accounts in the database
     */
    public void resetData() {
        accounts = AccountConnection.findAllAccounts();
    }

    /**
     * Get a list of all accounts whose name or email contains the search string
     */
    public ArrayList<String> search(String text) {
        ArrayList<String> filtered = new ArrayList<>();
        accounts.stream()
                .filter(a -> (a.getEmail().toLowerCase().contains(text.toLowerCase()) ||
                        a.getFullName().toLowerCase().contains(text.toLowerCase())))
                .map(a -> a.getFullName() + "    " + a.getEmail())
                .forEach(filtered::add);
        return filtered;
    }

    /**
     * Create a list of the names and emails of the attendees
     */
    public ObservableList<String> attendeeDetails() {
        ArrayList<String> attendeeDetails = new ArrayList<>();
        for(Attendee attendee : attendees){
            if (attendee instanceof AccountAttendee){
                Account account = ((AccountAttendee) attendee).getAccount();
                attendeeDetails.add(account.getFullName() + "    " + account.getEmail());
            }
            if (attendee instanceof NoAccountAttendee){
                attendeeDetails.add(((NoAccountAttendee) attendee).getName() +
                        "    " + ((NoAccountAttendee) attendee).getEmail());
            }
        }
        return FXCollections.observableArrayList(attendeeDetails);
    }

    /**
     * Remove the attendees that match an email
     */
    public void remove(String email) {
        Account account = AccountConnection.findAccount(email);
            Iterator<Attendee> iterator = attendees.iterator();
            while (iterator.hasNext()){
                Attendee attendee = iterator.next();
                if (account == null){
                    if (attendee instanceof NoAccountAttendee && ((NoAccountAttendee) attendee).getEmail().equals(email)){
                        iterator.remove();
                    }
                }
                else if (attendee instanceof AccountAttendee && ((AccountAttendee) attendee).getAccount().equals(account)){
                    iterator.remove();
                }
            }
    }

    /**
     * Add an account attendee given their email
     */
    public void add(String email) {
        Account account = AccountConnection.findAccount(email);

        // check if the attendee is already there
        for (Attendee attendee : attendees){
            if (attendee instanceof AccountAttendee && ((AccountAttendee) attendee).getAccount().equals(account)){
                return;
            }
        }

        attendees.add(new AccountAttendee(account));
    }
}
