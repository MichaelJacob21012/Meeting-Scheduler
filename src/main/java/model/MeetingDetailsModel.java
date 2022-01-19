package model;

import structure.Account;
import structure.Location;
import structure.Meeting;
import connect.AccountConnection;
import structure.Room;

public class MeetingDetailsModel {
    public String createMessage(Meeting meeting) {
        Account organiser = AccountConnection.findAccount(meeting.getAccount().getAccountID());
        String organiserName = organiser.getFullName();
        StringBuilder message = new StringBuilder();
        message.append(meeting.getTitle()).append("\n\n");
        message.append(meeting.getDescription()).append("\n\n");
        message.append("Organised by ").append(organiserName).append(".\n\n");
        String meetingDate = meeting.getTimestamp().toLocalDateTime().toLocalDate().toString();
        String meetingTime = meeting.getTimestamp().toLocalDateTime().toLocalTime().toString();
        message.append("Date: ").append(meetingDate).append(" at ").append(meetingTime)
                .append(" for ").append(meeting.getDurationTime()).append(".\n\n");
        Location location = meeting.getLocation();
        Room room = meeting.getRoom();
        message.append("Venue: ").append(location.details()).append(" in ")
                .append(room.getName()).append(".\n\n");
        message.append(bookedString(meeting)).append("\n");
        return message.toString();
    }

    private String bookedString(Meeting meeting) {
        if (meeting.isBooked()){
            return "The meeting has been successfully booked.";
        }
        if (meeting.isBookingFailed()){
            return "The meeting was not booked successfully.";
        }
        return "The meeting has yet to be booked.";
    }
}
