package com.sattler.resources;

import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.ClassRule;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class PingResourceTest {

    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new PingResource()).build();

    @Test
    public void testPing() {
        assertThat(resources.target("/ping").request().get(String.class)).isEqualTo("pong 1");
    }

}
