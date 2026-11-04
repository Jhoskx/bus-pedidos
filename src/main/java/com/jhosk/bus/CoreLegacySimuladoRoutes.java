package com.jhosk.bus;

import org.apache.camel.builder.RouteBuilder;

public class CoreLegacySimuladoRoutes extends RouteBuilder {
    @Override 
    public  void configure() throws Exception {
        from("direct:core-legacy")
            .routeId("core-legacy")
            .log("Core Legacy procesando el pedido ${header.CorrelationID}")
            .setHeader("Content-Type", constant("application/json"))
            .setBody(simple("{\"estado\": \"OK\", \"correlationId\": \"${header.CorrelationID}\"}"));
    }
    
}
