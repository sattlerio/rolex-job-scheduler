package com.sattler.resources;

import com.codahale.metrics.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.atomic.AtomicLong;

@Path("/ping")
@Produces(MediaType.APPLICATION_JSON)
public class PingResource {
    private final AtomicLong counter;

    private final static Logger log = LoggerFactory.getLogger(PingResource.class);

    public PingResource() {
        this.counter = new AtomicLong();
    }

    @GET
    @Timed
    public String ping() {
        log.info("got new new ping request");
        counter.incrementAndGet();
        return "pong " + counter;
    }

}
