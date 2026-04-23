package com.smartcampus.resources;

import com.smartcampus.data.DataStore;
import com.smartcampus.exception.LinkedResourceNotFoundException;
import com.smartcampus.models.Sensor;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.ArrayList;
import java.util.List;

@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {
    private DataStore dataStore = DataStore.getInstance();

    @GET
    public List<Sensor> getAllSensors(@Context UriInfo uriInfo) {
        List<Sensor> sensors = new ArrayList<>(dataStore.getSensors().values());
        for (Sensor sensor : sensors) {
            addLinks(sensor, uriInfo);
        }
        return sensors;
    }

    @GET
    @Path("/{sensorId}")
    public Response getSensor(@PathParam("sensorId") String sensorId, @Context UriInfo uriInfo) {
        Sensor sensor = dataStore.getSensors().get(sensorId);
        if (sensor == null) {
            throw new NotFoundException("Sensor not found");
        }
        addLinks(sensor, uriInfo);
        return Response.ok(sensor).build();
    }

    @POST
    public Response addSensor(Sensor sensor, @Context UriInfo uriInfo) {
        if (!dataStore.getRooms().containsKey(sensor.getRoomId())) {
            throw new LinkedResourceNotFoundException("Room " + sensor.getRoomId() + " does not exist");
        }
        dataStore.getSensors().put(sensor.getId(), sensor);
        dataStore.getReadings().putIfAbsent(sensor.getId(), new ArrayList<>());
        return Response.created(uriInfo.getAbsolutePathBuilder().path(sensor.getId()).build())
                .entity(sensor).build();
    }

    @Path("/{sensorId}/readings")
    public SensorReadingResource getReadingsResource() {
        return new SensorReadingResource();
    }

    private void addLinks(Sensor sensor, UriInfo uriInfo) {
        sensor.getLinks().clear();
        String base = uriInfo.getBaseUriBuilder().path(SensorResource.class).path(sensor.getId()).build().toString();
        sensor.addLink("self", base);
        sensor.addLink("room", uriInfo.getBaseUriBuilder().path(RoomResource.class).path(sensor.getRoomId()).build().toString());
        sensor.addLink("readings", base + "/readings");
    }
}
