package org.jaun.clubmanager;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.springframework.stereotype.Component;

//@Component
//@Path("/")
public class StaticResources {

    @GET
    @Path("index.html")
    public Response test() {
        return Response.ok("hello").build();
    }

}
