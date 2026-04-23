package com.smartcampus.resources;

import com.smartcampus.models.Link;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.List;

@Path("/")
public class DiscoveryResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Link> getDiscoveryLinks(@Context UriInfo uriInfo) {
        List<Link> links = new ArrayList<>();
        String baseUri = uriInfo.getBaseUri().toString();
        
        links.add(new Link("self", baseUri));
        links.add(new Link("rooms", baseUri + "rooms"));
        links.add(new Link("sensors", baseUri + "sensors"));
        
        return links;
    }
}
