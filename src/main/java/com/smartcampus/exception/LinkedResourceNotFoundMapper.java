package com.smartcampus.exception;

import com.smartcampus.models.ErrorMessage;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class LinkedResourceNotFoundMapper implements ExceptionMapper<LinkedResourceNotFoundException> {
    @Override
    public Response toResponse(LinkedResourceNotFoundException ex) {
        ErrorMessage errorMessage = new ErrorMessage(ex.getMessage(), 422, "http://smartcampus.com/docs/errors/422");
        return Response.status(422) // Unprocessable Entity
                .entity(errorMessage)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
