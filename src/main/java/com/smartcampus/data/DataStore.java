package com.smartcampus.data;

import com.smartcampus.models.Room;
import com.smartcampus.models.Sensor;
import com.smartcampus.models.SensorReading;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataStore {
    private static DataStore instance;
    private Map<String, Room> rooms = new ConcurrentHashMap<>();
    private Map<String, Sensor> sensors = new ConcurrentHashMap<>();
    private Map<String, List<SensorReading>> readings = new ConcurrentHashMap<>();

    private DataStore() {
        // Initialize with some mock data
        Room r1 = new Room("R1", "Lecture Theater 1", 100);
        Room r2 = new Room("R2", "Lab 101", 30);
        rooms.put(r1.getId(), r1);
        rooms.put(r2.getId(), r2);

        Sensor s1 = new Sensor("S1", "Temperature", "Active", 22.5, "R1");
        Sensor s2 = new Sensor("S2", "CO2", "Maintenance", 450.0, "R2");
        sensors.put(s1.getId(), s1);
        sensors.put(s2.getId(), s2);
        
        readings.put("S1", new ArrayList<>());
        readings.put("S2", new ArrayList<>());
    }

    public static synchronized DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    public Map<String, Room> getRooms() {
        return rooms;
    }

    public Map<String, Sensor> getSensors() {
        return sensors;
    }

    public Map<String, List<SensorReading>> getReadings() {
        return readings;
    }
}
