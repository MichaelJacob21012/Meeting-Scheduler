package structure;

import java.util.Objects;

public abstract class Attendee {
    private int attendeeID;
    private int meetingID;
    private boolean hasAccount;
    private boolean attended;
    private boolean accepted;
    private boolean rejected;

    Attendee(int attendeeID, int meetingID, boolean hasAccount, boolean attended, boolean accepted, boolean rejected) {
        this.attendeeID = attendeeID;
        this.meetingID = meetingID;
        this.hasAccount = hasAccount;
        this.attended = attended;
        this.accepted = accepted;
        this.rejected = rejected;
    }

    Attendee(boolean hasAccount) {
        attendeeID = -1;
        meetingID = -1;
        this.hasAccount = hasAccount;
        attended = false;
        accepted = false;
        rejected = false;
    }

    public int getAttendeeID() {
        return attendeeID;
    }

    public void setAttendeeID(int attendeeID) {
        this.attendeeID = attendeeID;
    }

    public int getMeetingID() {
        return meetingID;
    }

    public void setMeetingID(int meetingID) {
        this.meetingID = meetingID;
    }

    public boolean hasAccount() {
        return hasAccount;
    }

    public void setHasAccount(boolean hasAccount) {
        this.hasAccount = hasAccount;
    }

    public boolean attended() {
        return attended;
    }

    public void setAttended(boolean attended) {
        this.attended = attended;
    }

    private boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    private boolean isRejected() {
        return rejected;
    }

    public void setRejected(boolean rejected) {
        this.rejected = rejected;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Attendee attendee = (Attendee) o;
        return attendeeID == attendee.attendeeID &&
                meetingID == attendee.meetingID &&
                hasAccount == attendee.hasAccount &&
                attended == attendee.attended &&
                accepted == attendee.accepted &&
                rejected == attendee.rejected;
    }

    @Override
    public int hashCode() {
        return Objects.hash(attendeeID, meetingID, hasAccount, attended, accepted, rejected);
    }

    public String status() {
        if (isAccepted()){
            return ("Accepted");
        }
        if (isRejected()){
            return ("Rejected");
        }
        return "Undecided";
    }
}
