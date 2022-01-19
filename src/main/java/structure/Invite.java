package structure;

public class Invite {
    private String sender;
    private String recipient;
    private String title;
    private String status;
    private String attended;
    private int attendeeID;

    public Invite(String sender, String recipient, String description, String status, String attended, int attendeeID) {
        this.sender = sender;
        this.recipient = recipient;
        this.title = description;
        this.status = status;
        this.attended = attended;
        this.attendeeID = attendeeID;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String description) {
        this.title = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAttended() {
        return attended;
    }

    public void setAttended(String attended) {
        this.attended = attended;
    }

    public int getAttendeeID() {
        return attendeeID;
    }

    public void setAttendeeID(int attendeeID) {
        this.attendeeID = attendeeID;
    }
}
