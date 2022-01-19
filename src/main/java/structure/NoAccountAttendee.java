package structure;

import java.util.Objects;

public class NoAccountAttendee extends Attendee{
    private String name;
    private String email;

    public NoAccountAttendee(int attendeeID, int meetingID, boolean accepted,
                             boolean rejected, boolean attended, String name, String email) {
        super(attendeeID, meetingID, false, accepted, rejected, attended);
        this.name = name;
        this.email = email;
    }

    public NoAccountAttendee(String name, String email) {
        super(false);
        this.name = name;
        this.email = email;
    }

    public NoAccountAttendee() {
        super(false);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        NoAccountAttendee that = (NoAccountAttendee) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, email);
    }

    @Override
    public String toString() {
        return "NoAccountAttendee{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
