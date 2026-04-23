package com.smartcampus.resources;

import com.smartcampus.data.DataStore;
import com.smartcampus.exception.SensorUnavailableException;
import com.smartcampus.models.Sensor;
import com.smartcampus.models.SensorReading;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class SensorReadingResource {
    private DataStore dataStore = DataStore.getInstance();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<SensorReading> getReadings(@PathParam("sensorId") String sensorId) {
        Sensor sensor = dataStore.getSensors().get(sensorId);
        if (sensor == null) {
            throw new NotFoundException("Sensor not found");
        }
        if ("Maintenance".equals(sensor.getStatus())) {
            throw new SensorUnavailableException("Sensor is under maintenance");
        }
        return dataStore.getReadings().get(sensorId);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addReading(@PathParam("sensorId") String sensorId, SensorReading reading) {
        Sensor sensor = dataStore.getSensors().get(sensorId);
        if (sensor == null) {
            throw new NotFoundException("Sensor not found");
        }
        
        reading.setId(UUID.randomUUID().toString());
        reading.setTimestamp(LocalDateTime.now().toString());
        
        dataStore.getReadings().get(sensorId).add(reading);
        sensor.setCurrentValue(reading.getValue());
        
        return Response.status(Response.Status.CREATED).entity(reading).build();
    }
}
