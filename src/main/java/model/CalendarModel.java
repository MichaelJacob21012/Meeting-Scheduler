package model;

import structure.Account;
import structure.Meeting;
import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import connect.MeetingConnection;

import java.time.LocalDateTime;
import java.util.HashSet;

public class CalendarModel {

    public CalendarSource importMeetings(Account account){
        int accountID = account.getAccountID();
        Calendar calendar = new Calendar();
        HashSet<Meeting> meetings = new HashSet<>(MeetingConnection.meetingsByAccountID(accountID));
        meetings.addAll(MeetingConnection.attendeeMeetings(accountID));
        for (Meeting meeting : meetings) {
            if (meeting.isBooked()) {
                addMeetingToCalendar(meeting, calendar);
            }
        }
        return createCalendarSource(calendar);
    }

    public CalendarSource importRoomMeetings(int roomID) {
        Calendar calendar = new Calendar();
        HashSet<Meeting> meetings = new HashSet<>(MeetingConnection.meetingsByRoomID(roomID));
        for (Meeting meeting : meetings) {
            if (meeting.isBooked()) {
            addMeetingToCalendar(meeting, calendar);
        }
        }
        return createCalendarSource(calendar);
    }

    private CalendarSource createCalendarSource(Calendar calendar) {
        CalendarSource calendarSource = new CalendarSource();
        calendarSource.getCalendars().addAll(calendar);
        return calendarSource;
    }

    private void addMeetingToCalendar(Meeting meeting, Calendar calendar){
        Entry entry = new Entry<Meeting>(meeting.getTitle());
        LocalDateTime startTime = meeting.getTimestamp().toLocalDateTime();
        int hours = meeting.getDurationTime().toLocalTime().getHour();
        int minutes = meeting.getDurationTime().toLocalTime().getMinute();
        LocalDateTime endTime = startTime.plusHours(hours).plusMinutes(minutes);
        entry.setInterval(startTime,endTime);
        entry.setId("" + meeting.getMeetingID());
        calendar.addEntry(entry);
    }
}
