package com.smartcampus.resources;

import com.smartcampus.data.DataStore;
import com.smartcampus.exception.RoomNotEmptyException;
import com.smartcampus.models.Room;
import com.smartcampus.models.Sensor;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.ArrayList;
import java.util.List;

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {
    private DataStore dataStore = DataStore.getInstance();

    @GET
    public List<Room> getAllRooms(@Context UriInfo uriInfo) {
        List<Room> rooms = new ArrayList<>(dataStore.getRooms().values());
        for (Room room : rooms) {
            addLinks(room, uriInfo);
        }
        return rooms;
    }

    @GET
    @Path("/{roomId}")
    public Response getRoom(@PathParam("roomId") String roomId, @Context UriInfo uriInfo) {
        Room room = dataStore.getRooms().get(roomId);
        if (room == null) {
            throw new NotFoundException("Room not found");
        }
        addLinks(room, uriInfo);
        return Response.ok(room).build();
    }

    @POST
    public Response addRoom(Room room, @Context UriInfo uriInfo) {
        if (dataStore.getRooms().containsKey(room.getId())) {
            return Response.status(Response.Status.CONFLICT).entity("Room ID already exists").build();
        }
        dataStore.getRooms().put(room.getId(), room);
        return Response.created(uriInfo.getAbsolutePathBuilder().path(room.getId()).build())
                .entity(room).build();
    }

    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = dataStore.getRooms().get(roomId);
        if (room == null) {
            throw new NotFoundException("Room not found");
        }
        // Check if room has sensors
        boolean hasSensors = dataStore.getSensors().values().stream()
                .anyMatch(s -> s.getRoomId().equals(roomId));
        if (hasSensors) {
            throw new RoomNotEmptyException("Cannot delete room with active sensors");
        }
        dataStore.getRooms().remove(roomId);
        return Response.noContent().build();
    }

    private void addLinks(Room room, UriInfo uriInfo) {
        room.getLinks().clear();
        String base = uriInfo.getBaseUriBuilder().path(RoomResource.class).path(room.getId()).build().toString();
        room.addLink("self", base);
        room.addLink("sensors", base + "/sensors");
    }
}
