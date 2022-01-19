package structure;

import java.util.Objects;

public class AccountAttendee extends Attendee {
    private Account account;

    public AccountAttendee(int attendeeID, int meetingID, boolean accepted, boolean rejected,
                           boolean attended,  Account account) {
        super(attendeeID, meetingID, true, attended, accepted, rejected);
        this.account = account;
    }

    public AccountAttendee(Account account) {
        super(true);
        this.account = account;
    }

    public AccountAttendee() {
        super(true);
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AccountAttendee attendee = (AccountAttendee) o;
        return Objects.equals(account, attendee.account);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), account);
    }

    @Override
    public String toString() {
        return "AccountAttendee{" +
                "account=" + account +
                '}';
    }
}
