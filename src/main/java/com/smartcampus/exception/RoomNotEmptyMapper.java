package com.smartcampus.exception;

import com.smartcampus.models.ErrorMessage;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class RoomNotEmptyMapper implements ExceptionMapper<RoomNotEmptyException> {
    @Override
    public Response toResponse(RoomNotEmptyException ex) {
        ErrorMessage errorMessage = new ErrorMessage(ex.getMessage(), 409, "http://smartcampus.com/docs/errors/409");
        return Response.status(Response.Status.CONFLICT)
                .entity(errorMessage)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
