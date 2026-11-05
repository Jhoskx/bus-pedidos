package com.jhosk.bus;

import org.apache.camel.builder.RouteBuilder;

public class AuditoriaRoutes extends RouteBuilder {
    @Override
    
    public  void configure ()  throws Exception {
        from("direct:auditoria")
            .routeId("auditoria")
            .delay(3000)            // Simulamos que la auditoría es lenta (3 segundos)
            .log("Auditoría: Pedido recibido con CorrelationID=${header.CorrelationID} registrado | contenido: ${body}");
    }
}
