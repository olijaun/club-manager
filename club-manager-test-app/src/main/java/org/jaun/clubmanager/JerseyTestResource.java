package org.jaun.clubmanager;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.springframework.stereotype.Component;

// https://stackoverflow.com/questions/4784028/jax-rs-multiple-paths
@Path("/{a:internal|external}")
@Component
public class JerseyTestResource {

    @GET
    @Path("/test")
    public Response test() {
        return Response.ok().entity("hello world").build();
    }
}
