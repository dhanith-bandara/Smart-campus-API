package com.smartcampus.exception;

import com.smartcampus.models.ErrorMessage;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class SensorUnavailableMapper implements ExceptionMapper<SensorUnavailableException> {
    @Override
    public Response toResponse(SensorUnavailableException ex) {
        ErrorMessage errorMessage = new ErrorMessage(ex.getMessage(), 503, "http://smartcampus.com/docs/errors/503");
        return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                .entity(errorMessage)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
