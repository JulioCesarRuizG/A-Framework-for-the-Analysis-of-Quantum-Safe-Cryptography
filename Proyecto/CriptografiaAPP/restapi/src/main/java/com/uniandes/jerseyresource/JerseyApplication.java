package com.uniandes.jerseyresource;

import org.glassfish.jersey.server.ResourceConfig;

import com.uniandes.filters.CorsFilter;



public class JerseyApplication extends ResourceConfig {
    public JerseyApplication() {
        packages("com.uniandes");
        //packages("demo.webauthn");
        // Register my custom provider - not needed if it's in my.package.
        register(CorsFilter.class);
    }
}