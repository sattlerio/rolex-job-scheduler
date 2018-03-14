package com.sattler;

import com.sattler.health.PingHealthCheck;
import com.sattler.resources.PingResource;
import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.ResourceConfigurationSourceProvider;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.knowm.dropwizard.sundial.SundialBundle;
import org.knowm.dropwizard.sundial.SundialConfiguration;

public class rolexJobSchedulerApplication extends Application<rolexJobSchedulerConfiguration> {

    public static void main(final String[] args) throws Exception {
        new rolexJobSchedulerApplication().run(args);
    }

    @Override
    public String getName() {
        return "rolex-job-scheduler";
    }

    @Override
    public void initialize(final Bootstrap<rolexJobSchedulerConfiguration> bootstrap) {

        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(
                        new ResourceConfigurationSourceProvider(),
                        new EnvironmentVariableSubstitutor()
                )
        );

        bootstrap.addBundle(new SundialBundle<rolexJobSchedulerConfiguration>() {
            @Override
            public SundialConfiguration getSundialConfiguration(rolexJobSchedulerConfiguration configuration) {
                return configuration.getSundialConfiguration();
            }
        });
    }

    @Override
    public void run(final rolexJobSchedulerConfiguration configuration,
                    final Environment environment) {

        final PingResource pingResourceresource = new PingResource();

        environment.jersey().register(pingResourceresource);

        final PingHealthCheck healthCheck = new PingHealthCheck("https://google.com");

        environment.healthChecks().register("self Ping Check", healthCheck);


    }

}
