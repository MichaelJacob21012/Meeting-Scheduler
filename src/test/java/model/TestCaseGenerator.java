package model;

import structure.*;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Random;

class TestCaseGenerator {
    public ArrayList<Account> generateAccounts(int number){
        ArrayList<Account> accounts = new ArrayList<>();
        for (int i = 0; i < number; ++i){
            accounts.add(new Account("Person" + i, "person" + i, "Person" + i + "@person.com",
                    new byte[1], new byte[1], i%4 == 0));
        }
        return accounts;
    }
    public ArrayList<Meeting> generateMeetings(int number, ArrayList<Account> accounts, ArrayList<Room> rooms){
        ArrayList<Meeting> meetings = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < number; ++i){
            Account account = accounts.get(random.nextInt(accounts.size()));
            int numberOfPeople = random.nextInt(accounts.size() - 1) + 1;
            Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now().plusDays(random.nextInt(7)).plus(
                    Duration.between(LocalTime.MIN,LocalTime.of(random.nextInt(24), random.nextInt(60))))
                    .truncatedTo(ChronoUnit.MINUTES));
            if (timestamp.toLocalDateTime().getHour() == 23){
                timestamp = new Timestamp(timestamp.getTime() - 3600*1000);
            }
            int bound = Math.min(12, 23 - timestamp.toLocalDateTime().getHour());
            int hour = 0;
            if (bound > 0){
                hour = random.nextInt(bound);
            }
            int minute = random.nextInt(60);
            if (hour == 0 && minute == 0){
                ++minute;
            }
            Time duration = Time.valueOf(LocalTime.of(hour,minute));
            Room room = rooms.get(random.nextInt(rooms.size()));
            meetings.add(new Meeting(-1, account, "i", numberOfPeople, timestamp, duration, room.getLocation(),
                    room,true,false,"i"));

        }
        return  meetings;
    }
    public LinkedHashMap<Meeting, MeetingPreferences> generateUnscheduledMeetings(int number, ArrayList<Account> accounts, ArrayList<Room> rooms){
        LinkedHashMap<Meeting, MeetingPreferences> map = new LinkedHashMap<>();
        Random random = new Random();
        for (int i = 0; i < number; ++i){
            Account account = accounts.get(random.nextInt(accounts.size()));
            int numberOfPeople = random.nextInt(accounts.size() - 1) + 1;
            Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now().plusDays(random.nextInt(7)).plus(
                    Duration.between(LocalTime.MIN,LocalTime.of(random.nextInt(24), random.nextInt(60))))
                    .truncatedTo(ChronoUnit.MINUTES));
            if (timestamp.toLocalDateTime().getHour() == 23){
                timestamp = new Timestamp(timestamp.getTime() - 3600*1000);
            }
            int bound = Math.min(12, 23 - timestamp.toLocalDateTime().getHour());
            int hour = 0;
            if (bound != 0){
                hour = random.nextInt(bound);
            }
            int minute = random.nextInt(60);
            if (hour == 0 && minute == 0){
                ++minute;
            }
            Time duration = Time.valueOf(LocalTime.of(hour,minute));
            Room room = rooms.get(random.nextInt(rooms.size()));
            Meeting meeting = new Meeting(-1, account, "i", numberOfPeople, timestamp, duration, room.getLocation(),
                    room,false,false,"i");
            int venuePriority = random.nextInt(5) + 1;
            MeetingPreferences preferences = new MeetingPreferences(-1, random.nextInt(3) + 1, room.getLocation(),
                    venuePriority, room, venuePriority, timestamp.toLocalDateTime().toLocalDate().plusDays(random.nextInt(8) + 1),
                    random.nextBoolean(),random.nextInt(5) + 1);
            map.put(meeting, preferences);

        }
        return  map;
    }
    public ArrayList<Location> generateLocations(int number){
        ArrayList<Location> locations = new ArrayList<>();
        for (int i = 0; i < number; ++i){
            locations.add(new Location("Location" + i, "Located at" + i, "postcode" + i,
                    LocalTime.of(Math.min(i%24, 23-i%24),i%60),
                    LocalTime.of(Math.max(i%24, 23-i%24),i%60), i%4 == 0, "" + i));
        }
        return locations;
    }
    public ArrayList<Room> generateRooms(int number, ArrayList<Location> locations){
        ArrayList<Room> rooms = new ArrayList<>();
        for (Location location : locations){
            for (int i = 0; i < number; ++i){
                rooms.add(new Room("Room" + i, location, i*5, "" + i, i, LocalTime.of(0,i)));
            }
        }
        return rooms;
    }
}
