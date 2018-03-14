package com.sattler;

import io.dropwizard.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.knowm.dropwizard.sundial.SundialConfiguration;


public class rolexJobSchedulerConfiguration extends Configuration {

    public SundialConfiguration sundialConfiguration = new SundialConfiguration();

    @JsonProperty("sundial")
    public SundialConfiguration getSundialConfiguration() {
        return sundialConfiguration;
    }
}
