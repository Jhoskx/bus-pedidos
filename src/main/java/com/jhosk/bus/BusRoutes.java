package com.jhosk.bus;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

public class BusRoutes extends RouteBuilder {
@Override

    public void configure() throws Exception {
        

        rest("/pedidos")                         
            .post()                               
            .to("direct:recibir-pedido");


          from("direct:recibir-pedido")
            .routeId("recibir-pedido")
                        .choice()
                .when(header("CorrelationID").isNull())
                    .log("Pedido RECHAZADO: falta el header CorrelationID")
                    .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
                    .setHeader("Content-Type", constant("application/json"))
                    .setBody(constant("{\"error\": \"Falta el header CorrelationID\"}"))
                    .stop()
            .end()
            .log("Pedido recibido  | CorrelationID=${header.CorrelationID} | TraceID = ${header.TraceID}")
            
            .log("Contenido: ${body}")
            .to("direct:core-legacy");
            
    }
}
