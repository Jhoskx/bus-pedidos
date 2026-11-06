package com.jhosk.bus;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;

public class CoreLegacySimuladoRoutes extends RouteBuilder {
    @Override 
    public  void configure() throws Exception {

        errorHandler(noErrorHandler());


        from("direct:core-legacy")
            .routeId("core-legacy")
            .log("Core Legacy procesando el pedido ${header.CorrelationID}")
            .log("core legacy recibido ${body}")
            .choice()
            .when(simple("${body} contains 'FALLA'"))
            .log(LoggingLevel.WARN, "Core Legacy: ¡error simulado!")
            .throwException(RuntimeException.class, "Core Legacy no disponible")
            .end()
            .setHeader("Content-Type", constant("application/json"))
            .setBody(simple("{\"estado\": \"OK\", \"correlationId\": \"${header.CorrelationID}\"}"));
    }
    
}
