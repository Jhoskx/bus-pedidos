package com.jhosk.bus;

import org.apache.camel.builder.RouteBuilder;

public class AuditoriaRoutes extends RouteBuilder {
    @Override
    
    public  void configure ()  throws Exception {
        from("direct:auditoria")
            .routeId("auditoria")
            .to("sql:INSERT INTO auditoria (correlation_id, trace_id, contenido) "
                + "VALUES (:#${header.CorrelationID}, :#${header.TraceID}, :#${bodyAs(String)})")
            .log("Auditoría: pedido ${header.CorrelationID} guardado en la base de datos");    }
}
